package com.cmov.bomberman;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;

public class RobotThread extends Thread {

	List<Integer> moveablepos;
	private Random randomGenerator;

	private IMoveableRobot robotActiviy;
	public Activity activity;
	private int row = 0;
	private int col = 0;
	private Byte[][] GridLayout = null;
	private LogicalWorld logicalworld;
	private boolean running = false;
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Integer> updatedRobotPos = new HashMap<Integer, Integer>();

	public RobotThread(int row, int col, Activity activity) {
		super();

		this.row = row;
		this.col = col;
		robotActiviy = (IMoveableRobot) activity;
		GridLayout = ConfigReader.getGridLayout();

	}

	public void setLogicalWord(LogicalWorld logicalworld) {
		this.logicalworld = logicalworld;
	}

	private void move(int x, int y) {

		moveablepos = new ArrayList<Integer>();
		randomGenerator = new Random();

		int nyr = y + 1;
		int nyl = y - 1;

		int nxu = x - 1;
		int nxd = x + 1;

		if (nyl > 0 || nyr < this.col || nxu > 0 || nxd < this.row) {

			Cell cellr = logicalworld.getElement(x, nyr)[0]; // Move right = 1
			Cell celll = logicalworld.getElement(x, nyl)[0]; // Move left = 2

			Cell cellu = logicalworld.getElement(nxu, y)[0]; // Move up = 3
			Cell celld = logicalworld.getElement(nxd, y)[0]; // Move down = 4

			if (cellr == null)
				moveablepos.add(1);
			if (celll == null)
				moveablepos.add(2);
			if (cellu == null)
				moveablepos.add(3);
			if (celld == null)
				moveablepos.add(4);

			int index = randomGenerator.nextInt(moveablepos.size());
			int pos = moveablepos.get(index);
            
			if (pos == 1) {
				System.out
						.println("cell is empty lets move the robot,, remove this sysout..");
				ConfigReader.gridlayout[x][y] = '-';
				this.logicalworld.setElement(x, y, 0, null);
				updatedRobotPos.put(x, nyr);
			} else if (pos == 2) {
				System.out
						.println("cell is empty lets move the robot,, remove this sysout..");
				ConfigReader.gridlayout[x][y] = '-';
				this.logicalworld.setElement(x, y, 0, null);
				updatedRobotPos.put(x, nyl);
			} else if (pos == 3) {
				System.out
						.println("cell is empty lets move the robot,, remove this sysout..");
				ConfigReader.gridlayout[x][y] = '-';
				this.logicalworld.setElement(x, y, 0, null);
				updatedRobotPos.put(nxu, y);
			} else if (pos == 4) {
				System.out
						.println("cell is empty lets move the robot,, remove this sysout..");
				ConfigReader.gridlayout[x][y] = '-';
				this.logicalworld.setElement(x, y, 0, null);
				updatedRobotPos.put(nxd, y);
			}
			else
			{
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
				Thread.sleep(200);
				for (int x = 0; x < this.row; x++) {
					for (int y = 0; y < this.col; y++) {

						switch (GridLayout[x][y]) {
						case 'r': {
							move(x, y);
						}
							break;
						}
					}
				}

				for (Map.Entry<Integer, Integer> entry : updatedRobotPos
						.entrySet()) {
					ConfigReader.UpdateGridLayOutCell(entry.getKey(),
							entry.getValue(), (byte) 'r');

				}
				updatedRobotPos.clear();
				robotActiviy.RobotMovedAtLogicalLayer(); // so robot's new
															// position have
															// been set, lets
															// fire front end to
															// draw them .:)
				System.out.println("thread is sleeping now.");
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}