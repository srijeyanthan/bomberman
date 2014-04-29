package com.cmov.bomberman.server;

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
	// private GameState state = GameState.RUN;
	private IExplodable ExplodableActivity;

	public RobotThread(int row, int col, Server server) {
		super();

		for (int i = 1; i <= 5; ++i) {
			RobotCor robotCor = new RobotCor();
			updatedRobotPos.put(i, robotCor);
		}
		for (int i = 1; i <= 5; ++i) {
			RobotCor robotCor = new RobotCor();
			originalRbotPos.put(i, robotCor);
		}
		this.row = row;
		this.col = col;
		robotActiviy = (IMoveableRobot) server;
		ExplodableActivity = (IExplodable) server;

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

			/*
			 * System.out.println("(x-" + x + ",nyr-" + nyr + ")" + "(x-" + x +
			 * ",nyl-" + nyl + ")" + "(nxu-" + nxu + ",y-" + y + ")" + "(nxd-" +
			 * nxd + ",y-" + y + ")");
			 */
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
						// System.out.println("expected robot move - " + key);
						Phirobotmovelist.add(key);
						// //this.logicalworld.setElement(x, y, 0, null);
						updatedRobotPos.get(RobotCounter).x = nxu;
						updatedRobotPos.get(RobotCounter).y = y;
						originalRbotPos.get(RobotCounter).x = x;
						originalRbotPos.get(RobotCounter).y = y;
					}

				} else if (pos == 4) {
					String key = Integer.toString(nxd) + Integer.toString(y);

					if (!Phirobotmovelist.contains(key)) // contains key
					{
						// ystem.out.println("expected robot move - " + key);
						Phirobotmovelist.add(key);
						// this.logicalworld.setElement(x, y, 0, null);
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
							// System.out.println("expected robot move - " +
							// key);
							Phirobotmovelist.add(key);
							// this.logicalworld.setElement(x, y, 0, null);
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
							// System.out.println("expected robot move - " +
							// key);
							Phirobotmovelist.add(key);
							// this.logicalworld.setElement(x, y, 0, null);
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
							// System.out.println("expected robot move - " +
							// key);
							Phirobotmovelist.add(key);
							// //this.logicalworld.setElement(x, y, 0, null);
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
							// ystem.out.println("expected robot move - " +
							// key);
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

	/*
	 * public void setState(GameState state) { this.state = state; }
	 * 
	 * public GameState getGameState() { return this.state; }
	 */
	public void run() {

		while (running) {
			/* if ((getGameState() == GameState.RUN)) { */
			try {
				Thread.sleep(6000);
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
				Robotmovementbuffer = "<"+BombermanServerDef.MESSAGE_TYPE + "="
						+ BombermanServerDef.ROBOT_PLACEMET_MESSAGE + "|"
						+ BombermanServerDef.ROBOT_NEW_PLACE + "=";
				int iter = 1;
				for (Map.Entry<Integer, RobotCor> entry : updatedRobotPos
						.entrySet()) {
					if (updatedRobotPos.get(iter).x != -1
							&& updatedRobotPos.get(iter).y != -1) {
						int x = updatedRobotPos.get(iter).x;
						int y = updatedRobotPos.get(iter).y;
						/*
						 * System.out.println("Robot move pos - x - " + x +
						 * "|y = " + y);
						 */
						// if(this.logicalworld.getElement(x, y)[0] instanceof
						// Player)
						// /ExplodableActivity.Exploaded(true);
						ConfigReader.UpdateGridLayOutCell(x, y, (byte) 'r');
						this.logicalworld.setElement(x, y, 0, new Robot(x, y));
						Robotmovementbuffer +=  x + "," + y + ".";
						updatedRobotPos.get(iter).x = -1;
						updatedRobotPos.get(iter).y = -1;

					}
					++iter;
				}

				if (Robotmovementbuffer
						.charAt(Robotmovementbuffer.length() - 1) == '.') {
					Robotmovementbuffer = Robotmovementbuffer.replaceFirst(
							".$", "");
				}
				Robotmovementbuffer += "|"
						+ BombermanServerDef.ROBOT_ORIGINAL_PLACE + "=";
				iter = 1;
				for (Map.Entry<Integer, RobotCor> entry : originalRbotPos
						.entrySet()) {
					if (originalRbotPos.get(iter).x != -1
							&& originalRbotPos.get(iter).y != -1) {

						int x = originalRbotPos.get(iter).x;
						int y = originalRbotPos.get(iter).y;
						/*
						 * System.out.println("Original pos - x - " + x +
						 * "|y = " + y);
						 */
						Robotmovementbuffer +=  x + "," + y + ".";
						originalRbotPos.get(iter).x = -1;
						originalRbotPos.get(iter).y = -1;
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
				Robotmovementbuffer+=">";
				System.out.println("Robot movement buffer is ready - "
						+ Robotmovementbuffer);
				robotActiviy.RobotMovedAtLogicalLayer(Robotmovementbuffer);

				// ConfigReader.UnlockTheGrid();
				lock.unlock();
				System.out
						.println("_____________ Robot thread is goin to sleep now ____________");
				// Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	// }
}