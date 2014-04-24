package com.cmov.bomberman.server;

import java.io.IOException;

public class BombServerStart {

	private static LogicalWorld logicalworld = null;
	private static Server server = null;
	public static Game mGame = null;
	static Runnable runnable = new Runnable() {
		public void run() {

			try {
				
				server = new Server(null, 9090, logicalworld);
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			long endTime = System.currentTimeMillis() + 20 * 1000;

			while (System.currentTimeMillis() < endTime) {
				synchronized (this) {
					try {
						wait(endTime - System.currentTimeMillis());
					} catch (Exception e) {
					}
				}
			}
		}
	};

	public static void main(String[] args) {

		new ConfigReader();
		ConfigReader.InitReaders();
		mGame = new Game("BombServer");
		logicalworld = mGame.getLogicalWorld();
		Thread bombermanserverThread = new Thread(runnable);
		bombermanserverThread.start();

		// we need to create 3 plater and to the game
		Player player = new Player(null, "Sri", server);
		Player player2 = new Player(null, "Gureaya", server);
		Player player3 = new Player(null, "Bogdan", server);
		if (!mGame.addPlayer(player))
			return;

		if (!mGame.addPlayer(player2))
			return;

		if (!mGame.addPlayer(player3))
			return;

	}
}
