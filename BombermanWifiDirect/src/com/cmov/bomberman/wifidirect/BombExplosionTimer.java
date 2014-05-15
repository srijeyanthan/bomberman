package com.cmov.bomberman.wifidirect;

import java.util.Timer;
import java.util.TimerTask;

/*
 *  Developer note - Group -2 
 *  Whenever bomb place by a player , this timer will be activated , and keep running until it explodes. explosion time 
 *  is depends on the configuration.
 *  we could read this configuration from config file.
 * */

class BombExplosionTimer extends TimerTask {
	public static final int BOMB_TIME = (int) (ConfigReader.getGameConfig().explosiontimeout*1000);
			//ConfigReader.getGameConfig().explosiontimeout;

	private Timer timer = new Timer();
	private Bomb bomb;

	public BombExplosionTimer(Bomb bomb) {
		this.bomb = bomb;
		timer.schedule(this, BOMB_TIME / 6, BOMB_TIME / 6);
	}

	@Override
	public void run() {
		if (bomb.tick() >= 6) {
			bomb.explode();
			timer.cancel();	
		}
	}
}