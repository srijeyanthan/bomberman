package com.cmov.bomberman.standAlone;

/*
 *  This is basic cell class for other objects like obstacle , bomb , robot  and wall. All these classes have to implement
 *  this Cell class to store there x and y pos. 
 * 
 */
public abstract class Cell {
	protected int worldXCor;
	protected int worldYCor;

	public abstract String getImageFilename();

	public Cell(int x, int y) {
		this.worldXCor = x;
		this.worldYCor = y;
	}

	public int getWorldXCor() {
		return this.worldXCor;
	}

	public int getWorldYCor() {
		return this.worldYCor;
	}

	public void setPosition(int x, int y) {
		this.worldXCor = x;
		this.worldYCor = y;
	}
}