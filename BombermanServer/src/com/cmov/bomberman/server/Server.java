package com.cmov.bomberman.server;

import android.annotation.SuppressLint;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.cmov.bomberman.server.BombermanServerDef;

public class Server implements IMoveableRobot {
	private InetAddress addr;
	private int port;
	private Selector selector;
	private Map<SocketChannel, List<byte[]>> dataMap;
	final static Lock lock = new ReentrantLock();
	private Map<String, String> Session;
	private String IncomingBuffer = "";
	private RobotThread robotThread = null;
	private LogicalWorld logicalworld;
	private Map<String, Boolean> orderMessageMap;

	public Server(InetAddress addr, int port, LogicalWorld logicalworld)
			throws IOException {
		this.addr = addr;
		this.port = port;
		dataMap = new HashMap<SocketChannel, List<byte[]>>();
		orderMessageMap = new HashMap<String, Boolean>();
		Session = new HashMap<String, String>();
		this.logicalworld = logicalworld;
		startServer();
	}

	private void startServer() throws IOException {
		// create selector and channel
		this.selector = Selector.open();
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);

		// bind to port
		// InetSocketAddress listenAddr = new InetSocketAddress(this.addr,
		// / this.port);
		InetSocketAddress listenAddr = new InetSocketAddress(this.port);
		serverChannel.socket().bind(listenAddr);
		serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);

		log("Bomberman server is ready  Ctrl-C to stop.");

		// processing
		while (true) {
			// wait for events
			int n = this.selector.select();

			if (n == 0) {
				continue; // nothing to do
			}
			// wakeup to work on selected keys
			Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();
			while (keys.hasNext()) {
				SelectionKey key = (SelectionKey) keys.next();

				// this is necessary to prevent the same key from coming up
				// again the next time around.
				keys.remove();

				if (!key.isValid()) {
					continue;
				}

				if (key.isAcceptable()) {
					System.out
							.println("accept method has been fired ---" + key);
					this.accept(key);
				} else if (key.isReadable()) {
					System.out.println("readable key ---" + key);
					this.read(key);
				} else if (key.isWritable()) {
					System.out.println("write metho  ---" + key);
					this.write(key);
				}
			}
		}
	}

	private void accept(SelectionKey key) throws IOException {

		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
		SocketChannel channel = serverChannel.accept();
		channel.configureBlocking(false);

		System.out.println("key - " + key + "|channel - " + channel);
		// we have exceeded number of allowed of connection
		if (Session.size() > 3) {
			channel.write(ByteBuffer
					.wrap("Exceeded number of allowed of connection - retury later\r\n"
							.getBytes("US-ASCII")));
			channel.close();
			key.cancel();
			Socket socket = channel.socket();
			socket.close();
		} else {
			channel.write(ByteBuffer.wrap("Welcome to Bomberman server \r\n"
					.getBytes("US-ASCII")));
			Socket socket = channel.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			log("Connected to: " + remoteAddr);
			dataMap.put(channel, new ArrayList<byte[]>());
			channel.register(this.selector, SelectionKey.OP_READ);
		}
	}

	private void read(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		System.out.println("data is coming from this client  "
				+ channel.socket().getRemoteSocketAddress());
		ByteBuffer bufferA = ByteBuffer.allocate(100);

		String Sessoinstring = channel.socket().getRemoteSocketAddress()
				.toString();
		@SuppressWarnings("unused")
		int count = 0;

		int numRead = -1;
		while ((count = channel.read(bufferA)) > 0) {
			bufferA.flip();
			IncomingBuffer += Charset.defaultCharset().decode(bufferA);
			numRead += IncomingBuffer.length();

		}
		// if we have any leading new line chracter just remove them
		IncomingBuffer.trim();
		// just remove all the leading new line charater
		IncomingBuffer = IncomingBuffer.replaceAll("[\n\r]", "");

		System.out.println("Data has been received from the client - "
				+ IncomingBuffer);
		if (numRead == -1) {
			this.dataMap.remove(channel);
			Socket socket = channel.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			log("Connection closed by client: " + remoteAddr);
			channel.close();
			key.cancel();
			return;
		}

		processMessage(IncomingBuffer, Sessoinstring, key);
	}

	private void write(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		List<byte[]> pendingData = this.dataMap.get(channel);
		Iterator<byte[]> items = pendingData.iterator();
		while (items.hasNext()) {
			byte[] item = items.next();
			items.remove();
			channel.write(ByteBuffer.wrap(item));
		}
		key.interestOps(SelectionKey.OP_READ);
	}

	private void doSendToClient(SelectionKey key, byte[] data) {
		SocketChannel channel = (SocketChannel) key.channel();
		List<byte[]> pendingData = this.dataMap.get(channel);
		pendingData.add(data);
		key.interestOps(SelectionKey.OP_WRITE);

	}

	private static void log(String s) {
		System.out.println(s);
	}

	@SuppressLint("UseSparseArrays")
	private List<Map<Integer, String>> processIndividualMessage(String message) {
		// <message1><message2>... // this is out message structure
		List<Map<Integer, String>> processedtokens = new ArrayList<Map<Integer, String>>();
		String[] splittedmessage = message.split(">");
		int numberofmessages = splittedmessage.length;
		for (int i = 0; i < numberofmessages; ++i) {
			Map<Integer, String> processedTok = new HashMap<Integer, String>();
			String processingmessage = splittedmessage[i].substring(1);
			String[] splittedprocessngmessage = processingmessage.split("\\|");
			for (int j = 0; j < splittedprocessngmessage.length; ++j) {
				String[] individualTag = splittedprocessngmessage[j].split("=");
				processedTok.put(Integer.parseInt(individualTag[0]),
						individualTag[1]);
			}
			processedtokens.add(processedTok);
		}

		return processedtokens;

	}

	private void processMessage(String IncomBuffer, String sessionstring,
			SelectionKey key) {
		String processmessage = "";
		lock.lock(); // we are going modify the global string , so have to lock
		processmessage = IncomingBuffer;
		int posoflastindex = 0;
		posoflastindex = IncomBuffer.lastIndexOf('>');
		if (posoflastindex != 0) {
			// // why we put plus one here is , remove whole string other wise
			// this last character will remain in the string.
			IncomingBuffer = IncomingBuffer.substring(posoflastindex + 1);
			processmessage = processmessage.substring(0, posoflastindex + 1);
			lock.unlock();
		} else {
			lock.unlock();
			System.out
					.println("incomming buffer didnt have the expected > end charcter");
			return;
		}

		if (!processmessage.isEmpty()) {
			List<Map<Integer, String>> processedmsg = processIndividualMessage(processmessage);
			for (int i = 0; i < processedmsg.size(); ++i) {
				Map<Integer, String> fieldmap = processedmsg.get(i);
				if (fieldmap.get(BombermanServerDef.MESSAGE_TYPE).getBytes()[0] == BombermanServerDef.JOIN_MESSAGE) {
					processJoingMessage(fieldmap, sessionstring, key);
				} else if (fieldmap.get(BombermanServerDef.MESSAGE_TYPE)
						.getBytes()[0] == BombermanServerDef.PLAYER_MOVEMENT_MESSAGE) {
					ProcessPlayerMessage(fieldmap, sessionstring);
				}
			}
		}

	}

	private void processJoingMessage(Map<Integer, String> fieldmap,
			String sessionstring, SelectionKey key) {
		System.err.println("sessioin string - " + sessionstring + "|U="
				+ fieldmap.get(BombermanServerDef.USER_NAME));
		if (Session.put(sessionstring,
				fieldmap.get(BombermanServerDef.USER_NAME)) == null) {
			System.out.println("usernmae has been sucefully inserted ...");
		} else {
			System.out
					.println("user name is already added ...");
		}

		String mapmsg = BuildGridLayout();

		doSendToClient(key, mapmsg.getBytes());
		orderMessageMap.put(((SocketChannel) key.channel()).socket()
				.getRemoteSocketAddress().toString(), true);
		// // number of clients has reaced two, so lets start the robot movement
		// and send the data to client
		if (Session.size() >= 2) {
			robotThread = new RobotThread(ConfigReader.getGameDim().row,
					ConfigReader.getGameDim().column, this);
			robotThread.setLogicalWord(this.logicalworld);
			robotThread.setRunning(true);
			robotThread.start();
		}
	}

	private String BuildGridLayout() {
		String mapmessage = "";
		int row = ConfigReader.getGameDim().row;
		int column = ConfigReader.getGameDim().column;
		mapmessage = BombermanServerDef.MESSAGE_TYPE + "="
				+ BombermanServerDef.GRID_MESSAGE + "|"
				+ BombermanServerDef.GRID_ROW + "=" + row + "|"
				+ BombermanServerDef.GRID_COLUMN + "=" + column
				+ BombermanServerDef.GRID_ELEMENTS + "=";
		Byte[][] GridLayout = ConfigReader.getGridLayout();
		for (int i = 0; i < row; ++i) {
			byte[] element = new byte[GridLayout[i].length];
			for (int j = 0; j < column; ++j) {
				element[j] = GridLayout[i][j];

			}
			String strfrombyte = "";
			try {
				strfrombyte = new String(element, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			mapmessage += strfrombyte;
		}
		System.out.println("Grid message is generated - " + mapmessage);
		return mapmessage;
	}

	public void RobotMovedAtLogicalLayer(String Robotmovementbuffer) {

		for (@SuppressWarnings("rawtypes")
		Map.Entry entry : dataMap.entrySet()) {
			SocketChannel socket = (SocketChannel) entry.getKey();
			// we need to send this robot movement once we sent the initial grid
			// map layout
			System.out.println("socket - " + socket);
			if (orderMessageMap.containsKey(socket.socket()
					.getRemoteSocketAddress().toString())) {
				if (orderMessageMap.get(socket.socket()
						.getRemoteSocketAddress().toString())) {
					doSendToClient(socket.keyFor(this.selector),
							Robotmovementbuffer.getBytes());
				}
			}

		}

	}

	private void ProcessPlayerMessage(Map<Integer, String> fieldmap,
			String sessionstring) {

	}

}