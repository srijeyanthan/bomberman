package com.cmov.bomberman.server;

import java.io.IOException;

public class BombServerStart {

	

	
	

	public static void main(String[] args) {

		new ConfigReader();
		ConfigReader.InitReaders();
		System.out.println("[SERVER] ______Bomberman server is going to start now at port 9090________");
		
		try {
			BomberManWorker worker = new BomberManWorker();
			System.out.println("[SERVER] ______Starting worker thread now________");
			new Thread(worker).start();
			new Thread(new Server(null, 9090, worker)).start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		/*
		 * Thread bombermanserverThread = new Thread(runnable);
		 * bombermanserverThread.start();
		 */

	}
}
