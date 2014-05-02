package com.cmov.bomberman;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @author Gureya,Bogdan,Sri
 * @version multiplayer 1.0
 * This class is basically responsible for getting message from message queue 
 * which UI thread inserting client action
 * Thread is sleeping 10 milli second every time it is processing, if we want more 
 * speed up we could remove this  
 */
public class MessageDispatcher implements Runnable {

	
	final static Lock lock = new ReentrantLock();

	private MainActivity activity;
	public MessageDispatcher(MainActivity mainactivity)
	{
		this.activity = mainactivity;
	}
	@Override
	public void run() {
		while (true) {
			
			if (!activity.isQEmpty()) {
				String smg = activity.getMessageFromQ();
			    System.out.println("[MessageDispatcher] sending message to server - " + smg);
				try {
					BombermanClient.sendDataToServer(smg);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

