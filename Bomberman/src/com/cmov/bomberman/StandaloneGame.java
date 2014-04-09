package com.cmov.bomberman;



public class StandaloneGame {

	public void joinGame(String gameName)
	  { 
		Game game = new Game(gameName);
	    Player player = new Player(null,"Sri");   
	    if(!game.addPlayer(player))
	      return;
	    
	  }
}
