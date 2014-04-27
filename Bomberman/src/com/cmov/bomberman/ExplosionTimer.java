package com.cmov.bomberman;

import java.util.Timer;
import java.util.TimerTask;

public class ExplosionTimer extends TimerTask {
	public static final int EXPL_TIME = 10000; //ConfigReader.getGameConfig().explosiontimeout;

	private Timer timer = new Timer();
	private Explosion explosion;
	
	public ExplosionTimer(Explosion explosion) {
		this.explosion = explosion;
		timer.schedule(this, EXPL_TIME / 6, EXPL_TIME / 6);
	}

	@Override
	public void run() {
		if (explosion.tick() >= 6) {
			explosion.burn();
			timer.cancel();	
		}
	}
}
