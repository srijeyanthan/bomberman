package com.cmov.bomberman.server;


public class Robot extends Cell {



	public static final String IMAGE_FILENAME = "resource/robot.png";

	/*public void initRobotThread(int x, int y, int width, Activity activity) {
		RobotThread robotThread = new RobotThread(x, y, width, activity);
		robotThread.setRunning(true);
		robotThread.start();
	}*/

	public Robot(int x, int y) {
		super(x, y);
	    //initRobotThread(x, y);

	}

	public String getImageFilename() {
		return IMAGE_FILENAME;
	}

}
