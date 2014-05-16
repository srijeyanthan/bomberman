package com.cmov.bomberman.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BombServerStart {

	

	
	

	public static void main(String[] args) {

		System.out.println("[SERVER] Enter the game level which server want to start with :::");
		 
		    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		    int gameLevel=1;  
			try {
				String gameLevelInfo="";
				gameLevelInfo = bufferRead.readLine();
				gameLevel = Integer.parseInt(gameLevelInfo);
				if(gameLevel !=1 && gameLevel !=2 && gameLevel !=3 )
				{
					System.out.println("[SERVER] Wrong game level server is staring with level 0");
				}
				else
				{
					gameLevel = Integer.parseInt(gameLevelInfo);
					System.out.println("[SERVER] Server is staring with level - "+gameLevel);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	 
		    
		new ConfigReader();
		ConfigReader.InitReaders(gameLevel);
		System.out.println("[SERVER] ______Bomberman server is going to start now at port 9090________");
		
		try {
			BomberManWorker worker = new BomberManWorker();
			System.out.println("[SERVER] ______Starting worker thread now________");
			new Thread(worker).start();
			new Thread(new Server(null, 9090, worker)).start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		

	}
}
