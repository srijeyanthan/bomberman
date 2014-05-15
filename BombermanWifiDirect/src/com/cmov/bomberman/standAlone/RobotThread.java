package com.cmov.bomberman.standAlone;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.cmov.bomberman.standAlone.MainActivity.GameState;

import android.app.Activity;

public class RobotThread extends Thread {

	public class RobotCor {
		public int x = -1;
		public int y = -1;
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
	public RobotThread(int row, int col, Activity activity) {
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
		robotActiviy = (IMoveableRobot) activity;
		ExplodableActivity = (IExplodable) activity;
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
				//System.out.println("expected robot movement:" + pos);
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
								ExplodableActivity.Exploaded(true);							}
							ConfigReader.UpdateGridLayOutCell(x, y, (byte) 'r');
							this.logicalworld.setElement(x, y, 0, new Robot(x,
									y));
							updatedRobotPos.get(iter).x = -1;
							updatedRobotPos.get(iter).y = -1;

						}
						++iter;
					}

					iter = 1;
					for (Map.Entry<Integer, RobotCor> entry : originalRbotPos
							.entrySet()) {
						if (originalRbotPos.get(iter).x != -1
								&& originalRbotPos.get(iter).y != -1) {

							int x = originalRbotPos.get(iter).x;
							int y = originalRbotPos.get(iter).y;
							originalRbotPos.get(iter).x = -1;
							originalRbotPos.get(iter).y = -1;
							ConfigReader.UpdateGridLayOutCell(x, y, (byte) '-');
							this.logicalworld.setElement(x, y, 0, null);
						}
						++iter;
					}

					Phirobotmovelist.clear();
					/*System.out.println("Robot movement buffer is ready - "
							+ Robotmovementbuffer);*/
					robotActiviy.RobotMovedAtLogicalLayer();

					// ConfigReader.UnlockTheGrid();
					lock.unlock();
					/*System.out
							.println("_____________ Robot thread is going to sleep now ____________");*/
					// Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}
}