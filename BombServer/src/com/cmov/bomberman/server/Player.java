package com.cmov.bomberman.server;



/*
 * Developer note - Group -2
 */

public class Player extends Cell {

	static enum MoveDirection {
		UP, DOWN, LEFT, RIGHT, NONE, EXPLODING
	}

	protected Game game;
	protected String nickname;
	protected int id=0;

	protected int bombDistance = 1;
	protected int bombCount = 0;
	private final int ALLOWED_BOMB =1;

	private MoveDirection lastMoveDirection = MoveDirection.DOWN;
    private Server server;
	
	public Player(Game game, String nickname,Server server) {
		super(0, 0);
		this.game = game;
		this.nickname = nickname;
		this.server = server;
	}

	public void raiseBombDistance() {
		this.bombDistance++;
	}

	public void raiseBombCount() {
		this.bombCount++;
	}

	public String getImageFilename() {
		String imgPath = "resource/player" + getID() + "/";
		String addition = "";

		switch (lastMoveDirection) {
		case UP: {
			addition = "1";
			break;
		}
		case DOWN: {
			addition = "2";
			break;
		}
		case LEFT: {
			addition = "3";
			break;
		}
		case RIGHT: {
			addition = "4";
			break;
		}
		case EXPLODING:
			break;
		case NONE:
			break;
		default:
			break;
		}

		imgPath = imgPath + addition + ".png";
		return imgPath;
	}

	public String getNickname() {
		return this.nickname;
	}

	@Override
	public String toString() {
		return this.nickname;
	}

	public int getID() {
		return id;
	}

	void move(int dx, int dy) {
		this.worldXCor += dx;
		this.worldYCor += dy;

		if (dx < 0)
			lastMoveDirection = MoveDirection.LEFT;
		else if (dx > 0)
			lastMoveDirection = MoveDirection.RIGHT;
		else if (dy < 0)
			lastMoveDirection = MoveDirection.UP;
		else if (dy > 0)
			lastMoveDirection = MoveDirection.DOWN;
	}
    void setBombCounter(int counter)
    {
    	this.bombCount = counter;
    }
    int getBombCounter() { return this.bombCount;}
	void placeBomb() {
	         
		++bombCount;
		if(bombCount> ALLOWED_BOMB)
		{
			return;
		}
		
		
		System.out.println("User " + nickname + " place the bomb  " + worldXCor
				+ "/" + worldYCor);

		ConfigReader.UpdateGridLayOutCell(worldXCor, worldYCor, (byte) 'x');
		this.game.getLogicalWorld().setElement(worldXCor, worldYCor, 0, new Bomb(worldXCor, worldYCor));
		((Bomb)this.game.getLogicalWorld().getElement(worldXCor, worldYCor)[0]).InitBomb(this,server);
		
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void setID(int id) {
		this.id = id;
	}
}