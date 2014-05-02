package com.cmov.bomberman.server;

import java.io.IOException;

public class BombServerStart {

	

	/*
	 * static Runnable runnable = new Runnable() { public void run() {
	 * 
	 * try {
	 * 
	 * server = new Server(null, 9090, logicalworld);
	 * 
	 * } catch (IOException e1) { // TODO Auto-generated catch block
	 * e1.printStackTrace(); } long endTime = System.currentTimeMillis() + 20 *
	 * 1000;
	 * 
	 * while (System.currentTimeMillis() < endTime) { synchronized (this) { try
	 * { wait(endTime - System.currentTimeMillis()); } catch (Exception e) { } }
	 * } } };
	 */

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
