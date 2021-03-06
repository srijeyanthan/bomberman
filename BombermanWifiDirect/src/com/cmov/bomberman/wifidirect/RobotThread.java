package com.cmov.bomberman.wifidirect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RobotThread extends Thread {

	public class RobotCor {
		public int x = -1;
		public int y = -1;
	}

	public enum GameState {
		PAUSE, RUN, RESUME
	}

	private GameState state = GameState.PAUSE;
	List<Integer> moveablepos = new ArrayList<Integer>();
	List<Integer> moveableAI = new ArrayList<Integer>();
	private Random randomGenerator;

	private IMoveableRobot robotActiviy;
	private int row = 0;
	private int col = 0;

	private LogicalWorld logicalworld;
	private boolean running = false;
	private List<String> Phirobotmovelist = new ArrayList<String>();
	private Map<Integer, RobotCor> updatedRobotPos = new HashMap<Integer, RobotCor>();
	private Map<Integer, RobotCor> originalRbotPos = new HashMap<Integer, RobotCor>();
	final static Lock lock = new ReentrantLock();
	private IExplodable ExplodableActivity;
    private Object valueLock = new Object();
    private boolean value = false; 
    private float robotSpeed = ConfigReader.getGameConfig().robotspeed;
    private boolean dowereallyhavearobotmovementdataforloop1 =false;
    private boolean dowereallyhavearobotmovementdataforloop2 =false;
	public RobotThread(int row, int col, BomberManWorker server) {
		super();

		for (int i = 1; i <= ConfigReader.totalrobotcount; ++i) {
			RobotCor robotCor = new RobotCor();
			updatedRobotPos.put(i, robotCor);
		}
		for (int i = 1; i <= ConfigReader.totalrobotcount; ++i) {
			RobotCor robotCor = new RobotCor();
			originalRbotPos.put(i, robotCor);
		}
		this.row = row;
		this.col = col;
		// I think we could remove this interface , because , we can directly
		// call that method.
		robotActiviy = (IMoveableRobot) server;

	}

	public void setLogicalWord(LogicalWorld logicalworld) {
		this.logicalworld = logicalworld;
	}

	private void move(int x, int y, int RobotCounter) {

		randomGenerator = new Random();

		int nyr = y + 1;
		int nyl = y - 1;

		int nxu = x - 1;
		int nxd = x + 1;

		if (nyl > 0 || nyr < this.col || nxu > 0 || nxd < this.row) {

			if (logicalworld.getElement(x, nyr) != null) {
				// Move right = 1
				if (logicalworld.getElement(x, nyr)[0] == null)
					moveablepos.add(1);
				if (logicalworld.getElement(x, nyr)[0] instanceof Player)
					moveableAI.add(1);

			}
			if (logicalworld.getElement(x, nyl) != null) {
				// Move left = 2
				if (logicalworld.getElement(x, nyl)[0] == null)
					moveablepos.add(2);
				if (logicalworld.getElement(x, nyl)[0] instanceof Player)
					moveableAI.add(2);
			}
			if (logicalworld.getElement(nxu, y) != null) {
				// Move up = 3
				if (logicalworld.getElement(nxu, y)[0] == null)
					moveablepos.add(3);
				if (logicalworld.getElement(nxu, y)[0] instanceof Player)
					moveableAI.add(3);
			}

			if (logicalworld.getElement(nxd, y) != null) {
				// Move down = 4
				if (logicalworld.getElement(nxd, y)[0] == null)
					moveablepos.add(4);
				if (logicalworld.getElement(nxd, y)[0] instanceof Player)
					moveableAI.add(1);
			}

			int indexAI = 0;
			if (!(moveableAI.size() == 0)) {
				indexAI = randomGenerator.nextInt(moveableAI.size());
				int pos = moveableAI.get(indexAI);
				System.out.println("expected robot movement:" + pos);
				if (pos == 1) {

					String key = Integer.toString(x) + Integer.toString(nyr);
					if (!Phirobotmovelist.contains(key)) // contains key
					{
						// System.out.println("expected robot move - " + key);
						Phirobotmovelist.add(key);
						// this.logicalworld.setElement(x, y, 0, null);
						updatedRobotPos.get(RobotCounter).x = x;
						updatedRobotPos.get(RobotCounter).y = nyr;
						originalRbotPos.get(RobotCounter).x = x;
						originalRbotPos.get(RobotCounter).y = y;
					}

				} else if (pos == 2) {
					String key = Integer.toString(x) + Integer.toString(nyl);
					if (!Phirobotmovelist.contains(key)) // contains key
					{
						// System.out.println("expected robot move - " + key);
						Phirobotmovelist.add(key);
						// this.logicalworld.setElement(x, y, 0, null);
						updatedRobotPos.get(RobotCounter).x = x;
						updatedRobotPos.get(RobotCounter).y = nyl;
						originalRbotPos.get(RobotCounter).x = x;
						originalRbotPos.get(RobotCounter).y = y;
					}

				} else if (pos == 3) {

					String key = Integer.toString(nxu) + Integer.toString(y);
					if (!Phirobotmovelist.contains(key)) // contains key
					{
						Phirobotmovelist.add(key);
						updatedRobotPos.get(RobotCounter).x = nxu;
						updatedRobotPos.get(RobotCounter).y = y;
						originalRbotPos.get(RobotCounter).x = x;
						originalRbotPos.get(RobotCounter).y = y;
					}

				} else if (pos == 4) {
					String key = Integer.toString(nxd) + Integer.toString(y);

					if (!Phirobotmovelist.contains(key)) // contains key
					{
						Phirobotmovelist.add(key);
						updatedRobotPos.get(RobotCounter).x = nxd;
						updatedRobotPos.get(RobotCounter).y = y;
						originalRbotPos.get(RobotCounter).x = x;
						originalRbotPos.get(RobotCounter).y = y;
					}
				}
			}

			else {
				int index = 0;
				if (!(moveablepos.size() == 0)) {
					index = randomGenerator.nextInt(moveablepos.size());
					int pos = moveablepos.get(index);

					if (pos == 1) {

						String key = Integer.toString(x)
								+ Integer.toString(nyr);
						if (!Phirobotmovelist.contains(key)) // contains key
						{
							Phirobotmovelist.add(key);
							updatedRobotPos.get(RobotCounter).x = x;
							updatedRobotPos.get(RobotCounter).y = nyr;
							originalRbotPos.get(RobotCounter).x = x;
							originalRbotPos.get(RobotCounter).y = y;
						}

					} else if (pos == 2) {
						String key = Integer.toString(x)
								+ Integer.toString(nyl);
						if (!Phirobotmovelist.contains(key)) // contains key
						{
							Phirobotmovelist.add(key);
							updatedRobotPos.get(RobotCounter).x = x;
							updatedRobotPos.get(RobotCounter).y = nyl;
							originalRbotPos.get(RobotCounter).x = x;
							originalRbotPos.get(RobotCounter).y = y;
						}

					} else if (pos == 3) {

						String key = Integer.toString(nxu)
								+ Integer.toString(y);
						if (!Phirobotmovelist.contains(key)) // contains key
						{
							Phirobotmovelist.add(key);
							updatedRobotPos.get(RobotCounter).x = nxu;
							updatedRobotPos.get(RobotCounter).y = y;
							originalRbotPos.get(RobotCounter).x = x;
							originalRbotPos.get(RobotCounter).y = y;
						}

					} else if (pos == 4) {
						String key = Integer.toString(nxd)
								+ Integer.toString(y);

						if (!Phirobotmovelist.contains(key)) // contains key
						{
							Phirobotmovelist.add(key);
							// this.logicalworld.setElement(x, y, 0, null);
							updatedRobotPos.get(RobotCounter).x = nxd;
							updatedRobotPos.get(RobotCounter).y = y;
							originalRbotPos.get(RobotCounter).x = x;
							originalRbotPos.get(RobotCounter).y = y;
						}
					}
				} else {
					return;
				}

			}
			moveablepos.clear();
			moveableAI.clear();
		}

	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void setState(GameState state) {
		this.state = state;
		if (this.state.equals(GameState.RUN))
			synchronized ( valueLock ) {    
			      value = true;    
			      valueLock.notify();  // notifyAll() might be safer...    
			}  
		//in case if we set the game state to pause , then we have to put the thread in wait
		//mode, because we don't let the mobile phone to consume more cpu, we should be verty carefull
		//when we write the multi-threaded program, just run() method is more dangerous. :)
		//by : Sri.
		if (this.state.equals(GameState.PAUSE))
			synchronized ( valueLock ) {    
			      value = false;    
			      valueLock.notify();  // notifyAll() might be safer...    
			}  
	}

	public GameState getGameState() {
		return this.state;
	}

	public void run() {

		while (running) {

			synchronized ( valueLock ) {    
		          while ( value != true ) {    
		              try {
						valueLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}    
		         }    
		      }  
			if ((getGameState() == GameState.RUN)) {
				try {
					int rs = (int) (robotSpeed*1000);
					Thread.sleep(rs);
					// ConfigReader.LockTheGrid();
					lock.lock();
					int RobotCounter = 0;
					Byte[][] GridLayout = ConfigReader.getGridLayout();
					for (int x = 0; x < this.row; x++) {
						for (int y = 0; y < this.col; y++) {
							if (GridLayout[x][y] == 'r') {
								++RobotCounter;
								move(x, y, RobotCounter);

							}
						}
					}

					String Robotmovementbuffer = new String();
					Robotmovementbuffer = "<" + BombermanProtocol.MESSAGE_TYPE
							+ "=" + BombermanProtocol.ROBOT_PLACEMET_MESSAGE
							+ "|" + BombermanProtocol.ROBOT_NEW_PLACE + "=";
					int iter = 1;
					for (Map.Entry<Integer, RobotCor> entry : updatedRobotPos
							.entrySet()) {
						if (updatedRobotPos.get(iter).x != -1
								&& updatedRobotPos.get(iter).y != -1) {
							int x = updatedRobotPos.get(iter).x;
							int y = updatedRobotPos.get(iter).y;
							if(this.logicalworld.getElement(x, y)[0]instanceof Player)
							{
								// player has been killed by robot 
								//get the player id 
								int playerid =((Player)this.logicalworld.getElement(x, y)[0]).getID();
								//Server.mGame.removePlayer(playerid);
								Server.deadPlayerlist.add(playerid);
								
							}
							ConfigReader.UpdateGridLayOutCell(x, y, (byte) 'r');
							this.logicalworld.setElement(x, y, 0, new Robot(x,
									y));
							Robotmovementbuffer += x + "," + y + ".";
							updatedRobotPos.get(iter).x = -1;
							updatedRobotPos.get(iter).y = -1;
							dowereallyhavearobotmovementdataforloop1= true;

						}
						++iter;
					}

					if (Robotmovementbuffer
							.charAt(Robotmovementbuffer.length() - 1) == '.') {
						Robotmovementbuffer = Robotmovementbuffer.replaceFirst(
								".$", "");
					}
					Robotmovementbuffer += "|"
							+ BombermanProtocol.ROBOT_ORIGINAL_PLACE + "=";
					iter = 1;
					for (Map.Entry<Integer, RobotCor> entry : originalRbotPos
							.entrySet()) {
						if (originalRbotPos.get(iter).x != -1
								&& originalRbotPos.get(iter).y != -1) {

							int x = originalRbotPos.get(iter).x;
							int y = originalRbotPos.get(iter).y;
							Robotmovementbuffer += x + "," + y + ".";
							originalRbotPos.get(iter).x = -1;
							originalRbotPos.get(iter).y = -1;
							dowereallyhavearobotmovementdataforloop2=true;
							ConfigReader.UpdateGridLayOutCell(x, y, (byte) '-');
							this.logicalworld.setElement(x, y, 0, null);
						}
						++iter;
					}

					if (Robotmovementbuffer
							.charAt(Robotmovementbuffer.length() - 1) == '.') {
						Robotmovementbuffer = Robotmovementbuffer.replaceFirst(
								".$", "");
					}
					Phirobotmovelist.clear();
					Robotmovementbuffer += ">";
					if (dowereallyhavearobotmovementdataforloop2
							&& dowereallyhavearobotmovementdataforloop1) {
						robotActiviy
								.RobotMovedAtLogicalLayer(Robotmovementbuffer);
					}

					dowereallyhavearobotmovementdataforloop2 = false;
					dowereallyhavearobotmovementdataforloop1 = false;

					lock.unlock();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}
}