package com.cmov.bomberman;

import java.util.Map;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.app.Activity;

public class RobotThread extends Thread {

	private IMoveableRobot robotActiviy;
	public Activity activity;
	private int row = 0;
	private int col = 0;
	private Byte[][] GridLayout = null;
	private LogicalWorld logicalworld;
	private boolean movingRight;
	private boolean movingUp;
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
		
		int nyr = y+1;
		int nyl = y-1;
		
		int nxu = x-1;
		int nxd = x+1;
		
		Cell cellr = logicalworld.getElement(x, nyr)[0];
		Cell celll = logicalworld.getElement(x, nyl)[0];
		
		Cell cellu = logicalworld.getElement(nxu, y)[0];
		Cell celld = logicalworld.getElement(nxd, y)[0];
		
		if (nyl > 0 || nyr < this.col || nxu > 0 || nxd < this.row ) {
			
			if (cellr == null) {
				movingRight = true;
			} else if(celll == null) {
				movingRight = false;
			}
			else if(cellu == null)
				movingUp = true;
			else if(celld == null)
				movingUp = false;
		}
		
		if (movingRight) {
			System.out
					.println("cell is empty lets move the robot,, remove this sysout..");
			ConfigReader.gridlayout[x][y] = '-';
			this.logicalworld.setElement(x, y, 0, null);
			updatedRobotPos.put(x, nyr);
			movingRight = false;
		} else if (movingUp) {
			System.out
					.println("cell is empty lets move the robot,, remove this sysout..");
			ConfigReader.gridlayout[x][y] = '-';
			this.logicalworld.setElement(x, y, 0, null);
			updatedRobotPos.put(nxu, y);
			movingUp = false;
		} else if (!movingRight) {
			System.out
					.println("cell is empty lets move the robot,, remove this sysout..");
			ConfigReader.gridlayout[x][y] = '-';
			this.logicalworld.setElement(x, y, 0, null);
			updatedRobotPos.put(x, nyl);
			movingRight = false;
		}
		else if (!movingUp) {
			System.out
					.println("cell is empty lets move the robot,, remove this sysout..");
			ConfigReader.gridlayout[x][y] = '-';
			this.logicalworld.setElement(x, y, 0, null);
			updatedRobotPos.put(nxd, y);
			movingUp = false;
		}
		
		/* else if (++ny < this.col) {
			Cell cell = logicalworld.getElement(x, ny)[0];
			if (cell == null) {
				System.out
						.println("cell is empty lets move the robot,, remove this sysout..");
				ConfigReader.gridlayout[x][y] = '-';
				this.logicalworld.setElement(x, y, 0, null);
				updatedRobotPos.put(x, ny);
			}
		}*/
		 
	}

	public void moveCircle() {

		/*
		 * for (int x = 0; x < this.row; x++) { for (int y = 0; y < this.col;
		 * y++) {
		 * 
		 * switch (GridLayout[x][y]) { case 'r': { move(x,y); } break; } } }
		 * this.x++; if ( < 0 || ny < 0 || this.logicalWorld.getWidth() <= ny ||
		 * this.logicalWorld.getHeight() <= nx) return false; // player can not
		 * move if there is wall or obstacle , have to add here. Cell el =
		 * this.logicalWorld.getElement(nx, ny)[0]; if (el == null) // oder
		 * Extra {
		 * 
		 * } if (this.movingRight) { this.x++; ConfigReader.gridlayout[x][y] =
		 * 'r'; robotActiviy.RobotMovedAtLogicalLayer(x, y); } else { this.x--;
		 * ConfigReader.gridlayout[x][y] = 'r';
		 * robotActiviy.RobotMovedAtLogicalLayer(x, y); }
		 * 
		 * if (this.x == width) { this.movingRight = false;
		 * 
		 * } else if (this.x == 0) { this.movingRight = true;
		 * 
		 * }
		 */
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