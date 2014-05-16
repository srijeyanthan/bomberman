package com.cmov.bomberman.standAlone;

class Obstacle extends Cell {
	public static final String IMAGE_FILENAME = "resource/obstacle.png";

	public Obstacle(int x, int y) {
		super(x, y);
	}

	public String getImageFilename() {
		return IMAGE_FILENAME;
	}
}
