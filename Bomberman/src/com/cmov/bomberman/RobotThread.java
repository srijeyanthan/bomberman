package com.cmov.bomberman;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;

@SuppressLint("UseSparseArrays")
public class RobotThread extends Thread {

	public class RobotCor {
		public int x = -1;
		public int y = -1;
	}

	List<Integer> moveablepos = new ArrayList<Integer>();
	private Random randomGenerator;

	private IMoveableRobot robotActiviy;
	public Activity activity;
	private int row = 0;
	private int col = 0;

	private LogicalWorld logicalworld;
	private boolean running = false;
	private List<String> Phirobotmovelist = new ArrayList<String>();
	@SuppressLint("UseSparseArrays")
	private Map<Integer, RobotCor> updatedRobotPos = new HashMap<Integer, RobotCor>();
	private Map<Integer, RobotCor> originalRbotPos = new HashMap<Integer, RobotCor>();

	public RobotThread(int row, int col, Activity activity) {
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
		robotActiviy = (IMoveableRobot) activity;

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

			System.out.println("(x-" + x + ",nyr-" + nyr + ")" + "(x-" + x
					+ ",nyl-" + nyl + ")" + "(nxu-" + nxu + ",y-" + y + ")"
					+ "(nxd-" + nxd + ",y-" + y + ")");
			if (logicalworld.getElement(x, nyr) != null) {
				// Move right = 1
				if (logicalworld.getElement(x, nyr)[0] == null)
					moveablepos.add(1);
			}
			if (logicalworld.getElement(x, nyl) != null) {
				// Move left = 2
				if (logicalworld.getElement(x, nyl)[0] == null)
					moveablepos.add(2);
			}
			if (logicalworld.getElement(nxu, y) != null) {
				// Move up = 3
				if (logicalworld.getElement(nxu, y)[0] == null)
					moveablepos.add(3);
			}

			if (logicalworld.getElement(nxd, y) != null) {
				// Move down = 4
				if (logicalworld.getElement(nxd, y)[0] == null)
					moveablepos.add(4);
			}

			int index = 0;
			if (!(moveablepos.size() == 0)) {
				index = randomGenerator.nextInt(moveablepos.size());
				int pos = moveablepos.get(index);

				if (pos == 1) {

					String key = Integer.toString(x) + Integer.toString(nyr);
					if (!Phirobotmovelist.contains(key)) // contains key
					{
						// System.out.println("expected robot move - " + key);
						Phirobotmovelist.add(key);
						//this.logicalworld.setElement(x, y, 0, null);
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
						//this.logicalworld.setElement(x, y, 0, null);
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
						////this.logicalworld.setElement(x, y, 0, null);
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
						//this.logicalworld.setElement(x, y, 0, null);
						updatedRobotPos.get(RobotCounter).x = nxd;
						updatedRobotPos.get(RobotCounter).y = y;
						originalRbotPos.get(RobotCounter).x = x;
						originalRbotPos.get(RobotCounter).y = y;
					}
				}
			} else {
				return;
			}
			moveablepos.clear();
		}

	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void run() {

		while (running) {

			try {
				Thread.sleep(2000);
				ConfigReader.LockTheGrid();
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
						System.out.println("Robot move pos - x - " + x
								+ "|y = " + y);
						ConfigReader.UpdateGridLayOutCell(x, y, (byte) 'r');
						this.logicalworld.setElement(x, y, 0, new Robot(x, y));
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
						System.out.println("Original pos - x - "+ x+ "|y = " + y);
						originalRbotPos.get(iter).x = -1;
						originalRbotPos.get(iter).y = -1;
						ConfigReader.UpdateGridLayOutCell(x, y, (byte) '-');
						this.logicalworld.setElement(x, y, 0, null);
					}
					++iter;
				}

				Phirobotmovelist.clear();
				robotActiviy.RobotMovedAtLogicalLayer(); // so robot's new
															// position have
															// been set, lets
															// fire front end to
															// draw them .:)
				ConfigReader.UnlockTheGrid();

				System.out
						.println("_____________ Robot thread is goin to sleep now ____________");
				// Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}