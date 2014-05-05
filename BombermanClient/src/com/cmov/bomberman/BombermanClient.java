package com.cmov.bomberman;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import android.os.Handler;
import android.os.Message;


public abstract class BombermanClient implements Runnable {
	private static final long INITIAL_RECONNECT_INTERVAL = 500; // 500 ms.
	private static final long MAXIMUM_RECONNECT_INTERVAL = 30000; // 30 sec.
	private static final int READ_BUFFER_SIZE = 0x100000;
	private static final int WRITE_BUFFER_SIZE = 0x100000;

	private long reconnectInterval = INITIAL_RECONNECT_INTERVAL;

	private ByteBuffer readBuf = ByteBuffer.allocate(READ_BUFFER_SIZE); // 1Mb
	private ByteBuffer writeBuf = ByteBuffer.allocate(WRITE_BUFFER_SIZE); // 1Mb

	private final Thread thread = new Thread(this);
	private SocketAddress address;

	private Selector selector;
	private SocketChannel channel;
	private static RspHandler handler = null;
	private final AtomicBoolean connected = new AtomicBoolean(false);

	private AtomicLong bytesOut = new AtomicLong(0L);
	private AtomicLong bytesIn = new AtomicLong(0L);

	public static BombermanClient client = null;
	private static String username;

	public BombermanClient() {

	}

	public void init() {
		assert address != null : "server address missing";
	}

	public void start() throws IOException {
		System.out.println("starting event loop");
		thread.start();
	}

	public void join() throws InterruptedException {
		if (Thread.currentThread().getId() != thread.getId())
			thread.join();
	}

	public void stop() throws IOException, InterruptedException {
		System.out.println("stopping event loop");
		thread.interrupt();
		selector.wakeup();
	}

	public boolean isConnected() {
		return connected.get();
	}

	public void send(ByteBuffer buffer) throws InterruptedException,
			IOException {
		if (!connected.get())
			throw new IOException("not connected");
		synchronized (writeBuf) {
			// try direct write of what's in the buffer to free up space
			if (writeBuf.remaining() < buffer.remaining()) {
				writeBuf.flip();
				int bytesOp = 0, bytesTotal = 0;
				while (writeBuf.hasRemaining()
						&& (bytesOp = channel.write(writeBuf)) > 0)
					bytesTotal += bytesOp;
				writeBuf.compact();
			}

			// if didn't help, wait till some space appears
			if (Thread.currentThread().getId() != thread.getId()) {
				while (writeBuf.remaining() < buffer.remaining())
					writeBuf.wait();
			} else {
				if (writeBuf.remaining() < buffer.remaining())
					throw new IOException("send buffer full"); // TODO: add
																// reallocation
																// or buffers
																// chain
			}
			writeBuf.put(buffer);

			// try direct write to decrease the latency
			writeBuf.flip();
			int bytesOp = 0, bytesTotal = 0;
			while (writeBuf.hasRemaining()
					&& (bytesOp = channel.write(writeBuf)) > 0)
				bytesTotal += bytesOp;
			writeBuf.compact();

			if (writeBuf.hasRemaining()) {
				SelectionKey key = channel.keyFor(selector);
				key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
				selector.wakeup();
			}
		}
	}

	static class ExampleHandler extends Handler {
		 
		 
        public ExampleHandler() {
        }
 
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 100) {
                String sendmsg = msg.getData().getString("text");
                System.out.println("This is the message we have to send to the server - "+sendmsg);
 
                try {
					sendDataToServer(sendmsg);
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
    }
	protected abstract void onRead(ByteBuffer buf) throws Exception;

	protected abstract void onConnected() throws Exception;

	protected abstract void onDisconnected();

	private void configureChannel(SocketChannel channel) throws IOException {
		channel.configureBlocking(false);
		channel.socket().setSendBufferSize(0x100000); // 1Mb
		channel.socket().setReceiveBufferSize(0x100000); // 1Mb
		channel.socket().setKeepAlive(true);
		channel.socket().setReuseAddress(true);
		channel.socket().setSoLinger(false, 0);
		channel.socket().setSoTimeout(0);
		channel.socket().setTcpNoDelay(true);
	}

	@Override
	public void run() {
		System.out.println("event loop running");
		try {
			while (!Thread.interrupted()) { // reconnection loop
				try {
					selector = Selector.open();
					channel = SocketChannel.open();
					configureChannel(channel);

					channel.connect(address);
					channel.register(selector, SelectionKey.OP_CONNECT);

					while (!thread.isInterrupted() && channel.isOpen()) { // events
																			// multiplexing
																			// loop
						if (selector.select() > 0)
							processSelectedKeys(selector.selectedKeys());
					}
				} catch (Exception e) {
					System.out.println("exception");
					e.printStackTrace();
				} finally {
					connected.set(false);
					onDisconnected();
					writeBuf.clear();
					readBuf.clear();
					if (channel != null)
						channel.close();
					if (selector != null)
						selector.close();
					System.out.println("connection closed");
				}

				try {
					Thread.sleep(reconnectInterval);
					if (reconnectInterval < MAXIMUM_RECONNECT_INTERVAL)
						reconnectInterval *= 2;
					System.out.println("reconnecting to " + address);
				} catch (InterruptedException e) {
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("unrecoverable error");
			e.printStackTrace();
		}

		System.out.println("event loop terminated");
	}

	private void processSelectedKeys(Set keys) throws Exception {
		Iterator itr = keys.iterator();
		while (itr.hasNext()) {
			SelectionKey key = (SelectionKey) itr.next();
			if (key.isReadable())
				processRead(key);
			if (key.isWritable())
				processWrite(key);
			if (key.isConnectable())
				processConnect(key);
			if (key.isAcceptable())
				;
			itr.remove();
		}
	}

	private void processConnect(SelectionKey key) throws Exception {
		SocketChannel ch = (SocketChannel) key.channel();
		if (ch.finishConnect()) {
			System.out.println("connected to " + address);
			key.interestOps(key.interestOps() ^ SelectionKey.OP_CONNECT);
			key.interestOps(key.interestOps() | SelectionKey.OP_READ);
			reconnectInterval = INITIAL_RECONNECT_INTERVAL;
			connected.set(true);
			onConnected();
		}
	}

	private void processRead(SelectionKey key) throws Exception {
		ReadableByteChannel ch = (ReadableByteChannel) key.channel();

		int bytesOp = 0, bytesTotal = 0;
		while (readBuf.hasRemaining() && (bytesOp = ch.read(readBuf)) > 0)
			bytesTotal += bytesOp;

		if (bytesTotal > 0) {
			readBuf.flip();
			onRead(readBuf);
			readBuf.compact();
		} else if (bytesOp == -1) {
			System.out.println("peer closed read channel");
			ch.close();
		}

		byte[] rspData = new byte[bytesTotal];
		System.arraycopy(readBuf.array(), 0, rspData, 0, bytesTotal);
		bytesIn.addAndGet(bytesTotal);
		handler.handleResponse(rspData);
		System.out.println("this is the out put - " + new String(rspData));
		readBuf.clear();

	}

	private void processWrite(SelectionKey key) throws IOException {
		WritableByteChannel ch = (WritableByteChannel) key.channel();
		synchronized (writeBuf) {
			writeBuf.flip();

			int bytesOp = 0, bytesTotal = 0;
			while (writeBuf.hasRemaining()
					&& (bytesOp = ch.write(writeBuf)) > 0)
				bytesTotal += bytesOp;

			bytesOut.addAndGet(bytesTotal);

			if (writeBuf.remaining() == 0) {
				key.interestOps(key.interestOps() ^ SelectionKey.OP_WRITE);
			}

			if (bytesTotal > 0)
				writeBuf.notify();
			else if (bytesOp == -1) {
				System.out.println("peer closed write channel");
				ch.close();
			}

			writeBuf.compact();
		}
	}

	public SocketAddress getAddress() {
		return address;
	}

	public void setAddress(SocketAddress address) {
		this.address = address;
	}

	public long getBytesOut() {
		return bytesOut.get();
	}

	public long getBytesIn() {
		return bytesIn.get();
	}

	protected static String constructLoginMsg(String userName) {
		String loginMsg = "<1=J|2=" + userName + '>';
		return loginMsg;
	}

	public static void setPlayerName(String name) {
		username = name;
	}

	public static void sendDataToServer2(ByteBuffer bf) throws IOException {
		try {
			client.send(bf);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static void sendDataToServer(String msgtoserver) throws IOException {
		
		ByteBuffer buf = ByteBuffer.allocate(65535);
		buf = ByteBuffer.wrap(msgtoserver.getBytes());	
		try {
			client.send(buf);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		buf.clear();
	}

	public static void startBombermanClient() throws IOException {
		client = new BombermanClient() {
			@Override
			protected void onRead(ByteBuffer buf) throws Exception {
				buf.position(buf.limit());
			}

			@Override
			protected void onDisconnected() {
			}

			@Override
			protected void onConnected() throws Exception {
			}
		};

		handler = new RspHandler();
		client.setAddress(new InetSocketAddress("192.168.1.91", 9090));
		try {
			client.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (!client.isConnected())
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		System.out.println("[Client] Client is starting ....");

		String loginmsg = constructLoginMsg(username);
		sendDataToServer(loginmsg);
		while (true) {
			handler.waitForResponse();
			// Thread.sleep(3000);
		}
	}
}
