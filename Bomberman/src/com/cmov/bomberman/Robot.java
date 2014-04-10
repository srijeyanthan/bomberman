package com.cmov.bomberman;

import android.app.Activity;

public class Robot extends Cell {

	public class RobotThread extends Thread {

		private int x;
		private int y;
		private final int width = 0;
		private boolean movingRight = true;
		private IMoveableRobot robotActiviy;
		public Activity activity;

		public RobotThread(int x, int y, int width, Activity activity) {
			super();
			this.x = x;
			this.y = y;
			robotActiviy = (IMoveableRobot) activity;
		}

		public void moveCircle() {

			if (this.movingRight) {
				this.x++;
				ConfigReader.gridlayout[x][y] = 'r';
				robotActiviy.RobotMovedAtLogicalLayer(x, y);
			} else {
				this.x--;
				ConfigReader.gridlayout[x][y] = 'r';
				robotActiviy.RobotMovedAtLogicalLayer(x, y);
			}

			if (this.x == width) {
				this.movingRight = false;
				
			} else if (this.x == 0) {
				this.movingRight = true;
				
			}

		}

		private boolean running;

		public void setRunning(boolean running) {
			this.running = running;
		}

		public void run() {

			while (running) {

				try {
					moveCircle();
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static final String IMAGE_FILENAME = "resource/robot.png";

	public void initRobotThread(int x, int y, int width, Activity activity) {
		RobotThread robotThread = new RobotThread(x, y, width, activity);
		robotThread.setRunning(true);
		robotThread.start();
	}

	public Robot(int x, int y, int width, Activity activity) {
		super(x, y);
		initRobotThread(x, y, width, activity);

	}

	public String getImageFilename() {
		return IMAGE_FILENAME;
	}

}
