package com.cmov.bomberman.server;



import java.nio.channels.SocketChannel;
import java.nio.channels.SocketChannel;

class ServerDataEvent {
	public Server server;
	public SocketChannel socket;
	
	public ServerDataEvent(Server server, SocketChannel socket) {
		this.server = server;
		this.socket = socket;
	}
}
