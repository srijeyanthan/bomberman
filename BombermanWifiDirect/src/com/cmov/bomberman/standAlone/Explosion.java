package com.cmov.bomberman.standAlone;

public class Explosion extends Cell {
	public static final String IMAGE_FILENAME = "resource/explosion.png";
	public static final int range = ConfigReader.getGameConfig().explosionrange;
	private int stage = 1;
	public static final int duration = ConfigReader.getGameConfig().explosionduration;
	
	public Explosion(int x, int y){
		super(x,y);
	}
	
	public String getImageFilename() {
		return IMAGE_FILENAME;
	}
	
	public void burn(){
		int nx = worldXCor;
		int ny = worldYCor;
		
	}

	int tick() {
		return ++stage;
	}

}
