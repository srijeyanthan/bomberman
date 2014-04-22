package com.cmov.bomberman.server;

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


public class Server {
	private InetAddress addr;
	private int port;
	private Selector selector;
	private Map<SocketChannel, List<byte[]>> dataMap;
	final static Lock lock = new ReentrantLock();
	private Map<String, String> Session;
	private String IncomingBuffer = "";

	private final int MESSAGE_TYPE = 1;
	private final int USER_NAME = 2;
	private final int BOMB_PLACEMENT = 3;
	private final int PLAYER_MOVEMENT = 4;

	private final byte JOIN_MESSAGE = 'J';
	private final byte PLAYER_MOVEMENT_MESSAGE = 'P';
	private final byte BOMP_PLACEMET_MESSAGE = 'b';

	public Server(InetAddress addr, int port) throws IOException {
		this.addr = addr;
		this.port = port;
		dataMap = new HashMap<SocketChannel, List<byte[]>>();
		Session = new HashMap<String, String>();
		startServer();
	}

	private void startServer() throws IOException {
		// create selector and channel
		this.selector = Selector.open();
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);

		// bind to port
		InetSocketAddress listenAddr = new InetSocketAddress(this.addr,
				this.port);
		serverChannel.socket().bind(listenAddr);
		serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);

		log("Echo server ready. Ctrl-C to stop.");

		// processing
		while (true) {
			// wait for events
			this.selector.select();

			// wakeup to work on selected keys
			Iterator keys = this.selector.selectedKeys().iterator();
			while (keys.hasNext()) {
				SelectionKey key = (SelectionKey) keys.next();

				// this is necessary to prevent the same key from coming up
				// again the next time around.
				keys.remove();

				if (!key.isValid()) {
					continue;
				}

				if (key.isAcceptable()) {
					this.accept(key);
				} else if (key.isReadable()) {
					this.read(key);
				} else if (key.isWritable()) {
					this.write(key);
				}
			}
		}
	}

	private void accept(SelectionKey key) throws IOException {
		
		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
		SocketChannel channel = serverChannel.accept();
		channel.configureBlocking(false);

		// we have exceeded number of allowed of connection
		if(Session.size() > 3) 
		{
			channel.write(ByteBuffer.wrap("Exceeded number of allowed of connection - retury later\r\n"
					.getBytes("US-ASCII")));
			channel.close();
			key.cancel();
			Socket socket = channel.socket();
			socket.close();
		}
		else
		{
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
		ByteBuffer buffer = ByteBuffer.allocate(8192);
		ByteBuffer bufferA = ByteBuffer.allocate(100);

		String Sessoinstring = channel.socket().getRemoteSocketAddress()
				.toString();
		int count = 0;

		int numRead = -1;
		while ((count = channel.read(bufferA)) > 0) {
			bufferA.flip();
			IncomingBuffer += Charset.defaultCharset().decode(bufferA);
			numRead += IncomingBuffer.length();

		}

		if (numRead == -1) {
			this.dataMap.remove(channel);
			Socket socket = channel.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			log("Connection closed by client: " + remoteAddr);
			channel.close();
			key.cancel();
			return;
		}

		/*
		 * byte[] data = new byte[numRead]; System.arraycopy(buffer.array(), 0,
		 * data, 0, numRead); log("Got: " + new String(data, "US-ASCII"));
		 */

		processMessage(IncomingBuffer, Sessoinstring,key);
		// write back to client
		// doEcho(key, data);
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

	private void processMessage(String IncomBuffer, String sessionstring,SelectionKey key) {
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

		List<Map<Integer, String>> processedmsg = processIndividualMessage(processmessage);
		for (int i = 0; i < processedmsg.size(); ++i) {
			Map<Integer, String> fieldmap = processedmsg.get(i);
			if (fieldmap.get(MESSAGE_TYPE).getBytes()[0] == JOIN_MESSAGE) {
				processJoingMessage(fieldmap, sessionstring,key);
			} else if (fieldmap.get(MESSAGE_TYPE).getBytes()[0] == PLAYER_MOVEMENT_MESSAGE) {
				ProcessPlayerMessage(fieldmap, sessionstring);
			}
		}

	}

	private void processJoingMessage(Map<Integer, String> fieldmap,
			String sessionstring,SelectionKey key) {
		System.err.println("sessioin string - " + sessionstring + "|U="
				+ fieldmap.get(USER_NAME));
		Session.put(sessionstring, fieldmap.get(USER_NAME));
		String mapmsg  =BuildGridLayout();
		
		doSendToClient(key,mapmsg.getBytes());

	}
	
	private String BuildGridLayout()
	{
		String mapmessage="";
		int row = ConfigReader.getGameDim().row;
		int column = ConfigReader.getGameDim().column;
		mapmessage = "1=M|R="+row+"|C="+column+"|G=";
		Byte[][] GridLayout = ConfigReader.getGridLayout();
		for(int i=0 ; i < row ; ++i)
		{
			byte[] element = new byte[GridLayout[i].length];
			for (int j =0 ; j < column ;++j)
			{
		        element[j] = GridLayout[i][j];
				
			}
			String strfrombyte="";
			try {
				strfrombyte = new String(element,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			mapmessage +=strfrombyte;
		}
		System.out.println("Grid message is generated - "+ mapmessage);
		return mapmessage;
	}

	private void ProcessPlayerMessage(Map<Integer, String> fieldmap,
			String sessionstring) {

	}

	public static void main(String[] args) throws Exception {
		new Server(null, 8989);
	}
}