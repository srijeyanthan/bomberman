package com.cmov.bomberman.server;

import android.app.Activity;



public class StandaloneGame {

	public Game mGame = null;
	public void joinGame(String gameName,Activity activity)
	  { 
		mGame = new Game(gameName,activity);
	    Player player = new Player(null,"Sri",activity);   
	    if(!mGame.addPlayer(player))
	      return;
	    
	  }
	public Game getBombermanGame()
	{
		return this.mGame;
	}
}
