package com.cmov.bomberman.server;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.cmov.bomberman.server.RobotThread.GameState;

class GameDurationTimer extends TimerTask {
	//public static final int Gameduration = ConfigReader.getGameConfig().gameduration;
	public static final int Gameduration =60000;

	private Timer timer = new Timer();
    int tickcounter=1;
	public GameDurationTimer() {
		timer.schedule(this, Gameduration / 6, Gameduration / 6);
	}

	@Override
	public void run() {
		if (tick() >= 6) {
			System.out.println("[SERVER] Tesing .... "+System.currentTimeMillis());
			//<1=P|18=Sri,900.Gureaya,9000.Bogdan,89>
			System.out.println("[SERVER] Game finished ...Bye Bye ...from server");
			String scoreupdatemsg = "<" + BombermanProtocol.MESSAGE_TYPE + "="
					+ BombermanProtocol.GAME_END_MESSAGE + "|"+BombermanProtocol.GAME_STAT+"=";
			for (Map.Entry<Integer, String> entry : BomberManWorker.playernameidmap
					.entrySet()) {
				int totalscore=0;
				System.out.println("[SERVER] Player id checking - "+entry.getKey());
				if(BomberManWorker.globalScore.get(entry.getKey()) != null)
				{
					totalscore= BomberManWorker.globalScore.get(entry.getKey());
				}
				
				scoreupdatemsg +=entry.getValue()+","+totalscore+".";
			}
			if (scoreupdatemsg
					.charAt(scoreupdatemsg.length() - 1) == '.') {
				scoreupdatemsg = scoreupdatemsg.replaceFirst(
						".$", "");
			}
			scoreupdatemsg +=">";
			
			System.out.println("[SERVER] Game finished . Sending update to all the clients -" +scoreupdatemsg);
		
			for (Map.Entry<String, ServerDataEvent> entry : Server.clientList
					.entrySet()) {
				entry.getValue().server.send(entry.getValue().socket,
						scoreupdatemsg.getBytes());

			}
			//send msg to clients
			Server.robotThread.setState(GameState.PAUSE);
			Server.isGameStarted=false;
			timer.cancel();
		}
	}
	
	private int tick()
	{
		++tickcounter;
		return tickcounter;
	}

}
