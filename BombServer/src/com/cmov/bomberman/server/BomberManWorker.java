package com.cmov.bomberman.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SocketChannel;
import java.security.spec.MGF1ParameterSpec;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.cmov.bomberman.server.RobotThread.GameState;

public class BomberManWorker implements Runnable,IMoveableRobot,IExplodable,IUpdatableScore {
	
	 public class MessageSendStatus
	    {    	
	    	public boolean isGridMsgSent =false;
	    	public boolean isBombMsgSent = false;
	    	public boolean isPlayerMsgSent=false;
	    }
	 
	 public class Cor {
			public Cor(int x, int y) {
				this.x = x;
				this.y = y;
			}

			public int x = -1;
			public int y = -1;
		}
	final static Lock lock = new ReentrantLock();
	public Map<String, MessageSendStatus> msgSendStatus = new HashMap<String,MessageSendStatus>();
	public static Map<String, Integer> useridmap = new HashMap<String,Integer>();
	public Map<Integer,Integer> globalScore = new HashMap<Integer, Integer>();
    private Message msg = new Message();
    public void processData(Server server, SocketChannel socket,
			byte[] data, int count) {
    	String key="";
		try {
			key = socket.getRemoteAddress().toString();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if(!Server.clientList.containsKey(key))	
		{
		Server.clientList.put(key, new ServerDataEvent(server,socket));
		msgSendStatus.put(key, new MessageSendStatus());
		}
			
		synchronized (msg) {
			byte[] dataCopy = new byte[count];
			System.arraycopy(data, 0, dataCopy, 0, count);
			msg.addMessge(dataCopy);
			msg.notify();
			
		}
	}
    
    // we need this method of main , because , this is the worker class need to access game information
    // to retrive the player information so that we could easily move the player.
    

    public void RobotMovedAtLogicalLayer(String Robotmovementbuffer) {

    	for (Map.Entry<String, ServerDataEvent> entry : Server.clientList.entrySet()) {
    		//send the message only who connect with server, we don't need to check if the clients are 
    		// removing from the map once its disconnected from the server
    		//please note : this is checking (socket.isconnected() ) would be  a performance issue 
    		if(entry.getValue().socket.isConnected()) {
			entry.getValue().server.send(entry.getValue().socket, Robotmovementbuffer.getBytes());
				//System.out.println("[Server] grid message is sending to client - "+entry.getKey()+"|Grid - "+Robotmovementbuffer);
    		}else
    		{
    			System.out.println("[Server] Can't send the data to this client - "+entry.getKey()+" - because client has disconnected from the server");
    		}			
			
			
		}
		

	}
    private String BuildGridLayout(int playerid) {
		String mapmessage = "";
		int row = ConfigReader.getGameDim().row;
		int column = ConfigReader.getGameDim().column;
		mapmessage = "<"+BombermanProtocol.MESSAGE_TYPE + "="
				+ BombermanProtocol.GRID_MESSAGE + "|"
				+ BombermanProtocol.GRID_ROW + "=" + row + "|"
				+ BombermanProtocol.GRID_COLUMN + "=" + column + "|"
				+ BombermanProtocol.GRID_ELEMENTS + "=";
		Byte[][] GridLayout = ConfigReader.getGridLayout();
		for (int i = 0; i < row; ++i) {
			byte[] element = new byte[GridLayout[i].length];
			for (int j = 0; j < column; ++j) {
				element[j] = GridLayout[i][j];

			}
			String strfrombyte = "";
			try {
				strfrombyte = new String(element, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			mapmessage += strfrombyte;
		}
		mapmessage +="|"+BombermanProtocol.PLAYER_ID+"="+playerid;
		mapmessage+=">";
		System.out.println("Grid message is generated - " + mapmessage);
		return mapmessage;
	}
    
	private void processPlayerMovementMessage(Map<Integer, String> fieldmap) {
		// sample player movement message from client
		// <1=P|11=1|4=1.0>
		// lets get the which direction player has moved and player name
		int playerid = Integer.parseInt(fieldmap
				.get(BombermanProtocol.PLAYER_ID));
		String playermovepos = fieldmap.get(BombermanProtocol.PLAYER_MOVEMENT);
		String[] movpos = playermovepos.split("\\.");
		int x = Integer.parseInt(movpos[0]);
		int y = Integer.parseInt(movpos[1]);
		Player localPlayer = Server.mGame.getPlayer(playerid);
		String playermovementbuffer = Server.mGame
				.movePlayer(localPlayer, x, y);
		if (!playermovementbuffer.isEmpty()) {
			System.out
					.println("[Server] Player id request moved sucesfull id - "
							+ playerid);
			for (Map.Entry<String, ServerDataEvent> entry : Server.clientList
					.entrySet()) {
				entry.getValue().server.send(entry.getValue().socket,
						playermovementbuffer.getBytes());

			}
		}	
		else {
			System.out.println("[Server] Player id request moved failed  id - "
					+ playerid);
		}
	}
   
   private void processBombPlaceMovementMessage(Map<Integer,String> fieldmap)
   {
	   //when client place the bomb, he just send his userid server will choose the player and 
	    //place the bomb
	   //<1=B|11=1>
	   // lets get the which direction player has moved and player name
	    int playerid = Integer.parseInt(fieldmap.get(BombermanProtocol.PLAYER_ID));
	    Player localPlayer = Server.mGame.getPlayer(playerid);
		String bompmsg  = localPlayer.placeBomb(playerid);
		if(!bompmsg.isEmpty())
		{
			//send the bomb placement message to everyone
			System.out.println("[Server] bomb placement msg - "+bompmsg);
			for (Map.Entry<String, ServerDataEvent> entry : Server.clientList.entrySet()) {
					entry.getValue().server.send(entry.getValue().socket, bompmsg.getBytes());
								
			}
		}
   }
   private void processJoinMessage(Map<Integer, String> fieldmap) {
		
		// we need to create 3 player and to the game
	    String username = fieldmap.get(BombermanProtocol.USER_NAME);
		Player player = new Player(username, this);
		int playerid = Server.mGame.addPlayer(player);
	  
		String mapmsg = BuildGridLayout(playerid);
		// lets send the grid layout the client, even-though client joined between the game , we should only send 
		// grid to that particular client , not all clients
		for (Map.Entry<String, ServerDataEvent> entry : Server.clientList.entrySet()) {
			if(!msgSendStatus.get(entry.getKey()).isGridMsgSent)
			{
				entry.getValue().server.send(entry.getValue().socket, mapmsg.getBytes());
				System.out.println("[Server] grid message is sending to client - "+entry.getKey()+"|Grid - "+mapmsg);
				msgSendStatus.get(entry.getKey()).isGridMsgSent=true;
				useridmap.put(entry.getKey(), playerid);
			}else
			{
				/* we have already sent a grid message to client , so now we will only send the new player update*/
				int x = player.getWorldXCor();
				int y = player.getWorldYCor();
				String newplayermsg = "<"+BombermanProtocol.MESSAGE_TYPE + "="+BombermanProtocol.NEW_PLAYER_JOIN+"|"+BombermanProtocol.NEW_PLAYER_JOIN_COR+"="+x+","+y+">";
				entry.getValue().server.send(entry.getValue().socket, newplayermsg.getBytes());
			}
			
		}
	
		// we are assuming , once two clients connected to server, we can start the robot thread safly.
		if(Server.clientList.size() ==2)
		{
			Server.robotThread.setState(GameState.RUN);
		}
	}

	/*This method will be used to break the string and return easy format . example 
	 * input - 1,7.3,10.5,13.9,10
	 * output - 0 - Cor object which contains 1,7
	 *          1 - Cor object which contains 3,10
	 * So caller function can just iterate and use*/
	public Map<Integer, Cor> breakTheCordinateInToEasyFormat(String smsg) {
		String[] corsplitted = smsg.split("\\."); // 

		Map<Integer, Cor> breakCor = new HashMap<Integer, Cor>();
		for (int i = 0; i < corsplitted.length; ++i) {
			String[] furthercorsplitted = corsplitted[i].split(",");
			int x = Integer.parseInt(furthercorsplitted[0]);
			int y = Integer.parseInt(furthercorsplitted[1]);
			breakCor.put(i, new Cor(x, y));

		}
		return breakCor;
	}

	/*This method will be used to process individual message . example 
	 * input - <1=U|2=Group2><1=R|5=1,7.3,10.5,13.9,10|6=2,7.4,10.6,13.10,10>
	 * output - 0 , in the map key =1 value =u , key =2 , value =u ....
	 * So caller function can just iterate and use*/
	private List<Map<Integer, String>> processIndividualMessage(String message) {
		// <message1><message2>... // this is out message structure
		List<Map<Integer, String>> processedtokens = new ArrayList<Map<Integer, String>>();
		String[] splittedmessage = message.split(">");
		int numberofmessages = splittedmessage.length;
		for (int i = 0; i < numberofmessages; ++i) {
			Map<Integer, String> processedTok = new HashMap<Integer, String>();
			String processingmessage = splittedmessage[i].substring(1);
			String[] splittedprocessngmessage = processingmessage.split("\\|");
			for (int j = 0; j < splittedprocessngmessage.length; ++j) {
				String[] individualTag = splittedprocessngmessage[j].split("=");
				processedTok.put(Integer.parseInt(individualTag[0]),
						individualTag[1]);
			}
			processedtokens.add(processedTok);
		}

		return processedtokens;

	}

	public void run() {

		while (true) {
			// Wait for data to become available
			synchronized (msg) {
				while (this.msg.isEmpty()) {
					try {
						this.msg.wait();
					} catch (InterruptedException e) {
					}
				}
			}

			if (!msg.isEmpty()) {
				String processmessage = msg.getProcessMessage();
				if (!processmessage.isEmpty()) {

					List<Map<Integer, String>> processedmsg = processIndividualMessage(processmessage);
					for (int i = 0; i < processedmsg.size(); ++i) {
						Map<Integer, String> fieldmap = processedmsg.get(i);
						byte msgType =fieldmap.get(BombermanProtocol.MESSAGE_TYPE)
								.getBytes()[0];
						if ( msgType == BombermanProtocol.JOIN_MESSAGE) {
							processJoinMessage(fieldmap);
						}else if ( msgType == BombermanProtocol.PLAYER_MOVEMENT_MESSAGE) {
							processPlayerMovementMessage(fieldmap);
						}else if (msgType==BombermanProtocol.BOMP_PLACEMET_MESSAGE)
						{
							processBombPlaceMovementMessage(fieldmap);
						}
						else if (msgType ==BombermanProtocol.GAME_QUIT_MESSAGE)
						{
							processbombermanQuitMessage(fieldmap);
						}
					}

				} else {
					System.out
							.println("[WARNING]- <Rsp Handler> Process message is empty!! ");
				}

			}
		}

	}

	private void processbombermanQuitMessage(Map<Integer, String> fieldmap) {
		int playerid = Integer.parseInt(fieldmap.get(BombermanProtocol.PLAYER_ID));
		int playerxcor = Server.mGame.getPlayer(playerid).getWorldXCor();
		int playerycor = Server.mGame.getPlayer(playerid).getWorldYCor();
		ConfigReader.UpdateGridLayOutCell(playerxcor, playerycor, (byte)'-');
		String quitmsg = "<" + BombermanProtocol.MESSAGE_TYPE
				+ "=" + BombermanProtocol.GAME_QUIT_MESSAGE
				+ "|" + BombermanProtocol.QUIT_PLAYER_POS+"="+playerxcor+","+playerycor+">";
			for (Map.Entry<String, ServerDataEvent> entry : Server.clientList.entrySet()) {
				int playerID = useridmap.get(entry.getKey());
				if(playerID != playerid)
				{
					System.out.println("[SERVER] Server sending quit message to these users - "+ playerid);
					entry.getValue().server.send(entry.getValue().socket, quitmsg.getBytes());
				}
			}
	}

	@Override
	public void UpdateScore(int playerid , int numberOfRobotDied , int numberofPlayerkilled) {
		int totalScore =0;
	    totalScore = (numberOfRobotDied*ConfigReader.getGameConfig().pointperrobotkilled) + (numberofPlayerkilled*ConfigReader.getGameConfig().pointsperopponentkilled);
		System.out.println("[SERVER] player id - "+playerid+"|Total score - "+totalScore);
		globalScore.put(playerid, totalScore);
		//send only if total score is more than 0
		if(totalScore >0){
		String scoreupdatemsg = "<" + BombermanProtocol.MESSAGE_TYPE
				+ "=" + BombermanProtocol.INDIVIDUAL_SCORE_UPDATE
				+ "|" + BombermanProtocol.SCORE+"="+totalScore+">";
		for (Map.Entry<String, ServerDataEvent> entry : Server.clientList.entrySet()) {
			int playerID = useridmap.get(entry.getKey());
			if(playerID == playerid)
			{
				System.out.println("[SERVER] Server sending update message to these users - "+ playerid);
				entry.getValue().server.send(entry.getValue().socket, scoreupdatemsg.getBytes());
			}
		}
		}
	}

	@Override
	public void Exploaded(boolean isPlayerDead, String bombexlodemsg) {
		for (Map.Entry<String, ServerDataEvent> entry : Server.clientList
				.entrySet()) {
			entry.getValue().server.send(entry.getValue().socket,
					bombexlodemsg.getBytes());

		}
	}
	
}
