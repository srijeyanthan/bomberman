package com.cmov.bomberman.server;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

import com.cmov.bomberman.server.RobotThread.GameState;

public class Server implements Runnable {
	// The host:port combination to listen on
	private InetAddress hostAddress;
	private int port;

	// The channel on which we'll accept connections
	private ServerSocketChannel serverChannel;

	// The selector we'll be monitoring
	private Selector selector;

	// The buffer into which we'll read data when it's available
	private ByteBuffer readBuffer = ByteBuffer.allocate(8192);

	private BomberManWorker worker;
	public static RobotThread robotThread = null;

	// A list of PendingChange instances
	private List pendingChanges = new LinkedList();

	// Maps a SocketChannel to a list of ByteBuffer instances
	private Map pendingData = new HashMap();
	public static LogicalWorld logicalworld;
	public static Game mGame = null;
	public static Map<String, ServerDataEvent> clientList = new HashMap<String,ServerDataEvent>();
	public static boolean isGameStarted = false;
	public Server(InetAddress hostAddress, int port, BomberManWorker worker) throws IOException {
		this.hostAddress = hostAddress;
		this.port = port;
		this.selector = this.initSelector();
		this.worker = worker;
		InitBombermanGame();
		startRobotThread();
		
	}
	
	private void InitBombermanGame() {
		mGame = new Game("BombServer");
		logicalworld = mGame.getLogicalWorld();
	}
	 private void startRobotThread()
	   {
		   robotThread = new RobotThread(ConfigReader.getGameDim().row,
					ConfigReader.getGameDim().column, this.worker);
			robotThread.setLogicalWord(Server.logicalworld);
			robotThread.setRunning(true);
			robotThread.start();
			System.out.println("[SERVER] ************* Starting robot thread ****************");
	   }
	public void send(SocketChannel socket, byte[] data) {
		synchronized (this.pendingChanges) {
			// Indicate we want the interest ops set changed
			this.pendingChanges.add(new ChangeRequest(socket, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

			// And queue the data we want written
			synchronized (this.pendingData) {
				List queue = (List) this.pendingData.get(socket);
				if (queue == null) {
					queue = new ArrayList();
					this.pendingData.put(socket, queue);
				}
				queue.add(ByteBuffer.wrap(data));
			}
		}

		// Finally, wake up our selecting thread so it can make the required changes
		this.selector.wakeup();
	}

	public void run() {
		while (true) {
			try {
				// Process any pending changes
				synchronized (this.pendingChanges) {
					Iterator changes = this.pendingChanges.iterator();
					while (changes.hasNext()) {
						ChangeRequest change = (ChangeRequest) changes.next();
						switch (change.type) {
						case ChangeRequest.CHANGEOPS:
							SelectionKey key = change.socket.keyFor(this.selector);
							key.interestOps(change.ops);
						}
					}
					this.pendingChanges.clear();
				}

				// Wait for an event one of the registered channels
				this.selector.select();

				// Iterate over the set of keys for which events are available
				Iterator selectedKeys = this.selector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();

					if (!key.isValid()) {
						continue;
					}

					// Check what event is available and deal with it
					if (key.isAcceptable()) {
						this.accept(key);
					} else if (key.isReadable()) {
						this.read(key);
					} else if (key.isWritable()) {
						this.write(key);
					}
				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void accept(SelectionKey key) throws IOException {
		// For an accept to be pending the channel must be a server socket channel.
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

		// Accept the connection and make it non-blocking
		SocketChannel socketChannel = serverSocketChannel.accept();
		Socket socket = socketChannel.socket();
		socketChannel.configureBlocking(false);

		// Register the new SocketChannel with our Selector, indicating
		// we'd like to be notified when there's data waiting to be read
		socketChannel.register(this.selector, SelectionKey.OP_READ);
	}

	private void read(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		// Clear out our read buffer so it's ready for new data
		this.readBuffer.clear();

		// Attempt to read off the channel
		int numRead;
		try {
			numRead = socketChannel.read(this.readBuffer);
		} catch (IOException e) {
			System.out.println("[Server] Remote has forcibly closed the conention, do we need still remove the client, I dont know ??<TODO>");
			// The remote forcibly closed the connection, cancel
			// the selection key and close the channel.
			key.cancel();
			socketChannel.close();
			return;
		}

		if (numRead == -1) {
			// Remote entity shut the socket down cleanly. Do the
			// same from our end and cancel the channel.
			
			System.out.println("[Server] Client has diconnection from server -"+socketChannel.socket().getRemoteSocketAddress().toString());
			clientList.remove(socketChannel.socket().getRemoteSocketAddress().toString());
			System.out.println("[Server] Client has sucessfully remvoed from the client list");
			//remvove the player from the map
			
			int playerid  = BomberManWorker.useridmap.get(socketChannel.socket().getRemoteSocketAddress().toString());
			System.out.println("[Server] Player id is removing from the map -" +playerid);
			mGame.removePlayer(playerid);
			if(clientList.size() < 2)
			{
				//Server.isGameStarted =false;
				Server.robotThread.setState(GameState.PAUSE);
			}
			key.channel().close();
			key.cancel();
			
			// some of the client has diconnected , send the message to all the client except disconnected socket
			String noOfPlayerMsg = "<" + BombermanProtocol.MESSAGE_TYPE
					+ "=" + BombermanProtocol.NUMBER_OF_PLAYERS_MESSAGE
					+ "|" + BombermanProtocol.NUMBER_OF_PLAYERS+"="+Server.clientList.size()+">";
			for (Map.Entry<String, ServerDataEvent> entry : Server.clientList
					.entrySet()) {
				entry.getValue().server.send(entry.getValue().socket,
						noOfPlayerMsg.getBytes());
				
			}
			
			return;
		}

		// Hand the data off to our worker thread
		this.worker.processData(this, socketChannel, this.readBuffer.array(), numRead);
		this.readBuffer.clear();
	}

	private void write(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		synchronized (this.pendingData) {
			List queue = (List) this.pendingData.get(socketChannel);

			// Write until there's not more data ...
			while (!queue.isEmpty()) {
				ByteBuffer buf = (ByteBuffer) queue.get(0);
				socketChannel.write(buf);
				if (buf.remaining() > 0) {
					// ... or the socket's buffer fills up
					break;
				}
				queue.remove(0);
			}

			if (queue.isEmpty()) {
				// We wrote away all data, so we're no longer interested
				// in writing on this socket. Switch back to waiting for
				// data.
				key.interestOps(SelectionKey.OP_READ);
			}
		}
	}

	private Selector initSelector() throws IOException {
		// Create a new selector
		Selector socketSelector = SelectorProvider.provider().openSelector();

		// Create a new non-blocking server socket channel
		this.serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);

		// Bind the server socket to the specified address and port
		InetSocketAddress isa = new InetSocketAddress(this.hostAddress, this.port);
		serverChannel.socket().bind(isa);

		// Register the server socket channel, indicating an interest in 
		// accepting new connections
		serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

		return socketSelector;
	}

}
