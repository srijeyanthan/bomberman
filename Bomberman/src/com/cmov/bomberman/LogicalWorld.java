package com.cmov.bomberman;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;





public class LogicalWorld {

	// This have to be get from config file or after we adjusted according scree
	// size
	final static Lock lock = new ReentrantLock();
	private static final int MAX_NUM_PLAYER = 3;

	public  Cell[][][] twoDWorld = null;
	private int cols, rows;
	

	public LogicalWorld() {
		this.cols = ConfigReader.getGameDim().column;
		this.rows = ConfigReader.getGameDim().row;
		this.twoDWorld = new Cell[rows][cols][MAX_NUM_PLAYER];

		Byte[][] entries = null;

		entries = ConfigReader.getGridLayout();

		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < cols; y++) {

				switch (entries[x][y]) {
				case 'w': {
					this.twoDWorld[x][y][0] = new Wall(x, y);
				}
					break;
				case 'o': {
					this.twoDWorld[x][y][0] = new Obstacle(x, y);
				}
					break;
				case 'r': {
					this.twoDWorld[x][y][0] = new Robot(x, y);
				}
					break;

				}
			}
		}
	}

	public int getWidth() {
		return cols;
	}

	public int getHeight() {
		return rows;
	}

	public Cell[] getElement(int x, int y) {
		try {
			System.out.println("x- "+x+"|y="+y+"|="+this.twoDWorld[x][y][0]);
			return this.twoDWorld[x][y];
		} catch (ArrayIndexOutOfBoundsException ex) {
			return null;
		}
	}

	public void setElement(int x, int y, int layer, Cell e) {
		lock.lock();
		try {
			this.twoDWorld[x][y][0] = e;
		} finally {
			lock.unlock();
		}
		
	}
}
