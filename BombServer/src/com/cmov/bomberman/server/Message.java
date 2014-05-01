package com.cmov.bomberman.server;

public class Message {

	private String IncomingBuffer = "";

	public Message() {
	}

	public String getMessage() {
		return IncomingBuffer;
	}

	public void addMessge(byte[] data) {
		this.IncomingBuffer += new String(data);
		// if we have any leading new line character just remove them
		IncomingBuffer= IncomingBuffer.trim();
	}

	public boolean isEmpty() {
		return this.IncomingBuffer.isEmpty();
	}

	public String getProcessMessage() {
		int posoflastindex = 0;
		String processmessage = "";
		processmessage = this.IncomingBuffer;
		posoflastindex = this.IncomingBuffer.lastIndexOf('>');
		if (posoflastindex != 0) {
			// // why we put plus one here is , remove whole string other wise
			// this last character will remain in the string.
			IncomingBuffer = IncomingBuffer.substring(posoflastindex + 1);
			processmessage = processmessage.substring(0, posoflastindex + 1);
		} else {
			System.out
					.println("incomming buffer didnt have the expected > end charcter");
			// client didnt send the correct message to server , so we will set the server buffer to zero
			this.IncomingBuffer="";
		}
		return processmessage;
	}
}