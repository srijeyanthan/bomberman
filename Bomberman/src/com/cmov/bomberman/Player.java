package com.cmov.bomberman;

import java.util.ArrayList;
import java.util.List;

/*
 * Developer note - Group -2
 */

public class Player extends Cell {

	static enum MoveDirection {
		UP, DOWN, LEFT, RIGHT, NONE, EXPLODING
	}

	protected List<Bomb> bombs = new ArrayList<Bomb>();
	protected Game game;
	protected String nickname;
	protected int id;

	protected int bombDistance = 1;
	protected int bombCount = 1;

	private MoveDirection lastMoveDirection = MoveDirection.DOWN;

	public Player(Game game, String nickname) {
		super(0, 0);
		this.game = game;
		this.nickname = nickname;
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
        System.out.println("Player path is checking .... "+ imgPath);
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

	void placeBomb() {
		if (bombs.size() >= this.bombCount)
			return;
		System.out.println("User " + nickname + " place the bomb  " + worldXCor
				+ "/" + worldYCor);

		Bomb bomb = new Bomb(worldXCor, worldYCor, this);
		this.bombs.add(bomb);

		this.game.getLogicalWorld().setElement(worldXCor, worldYCor, 0, bomb);
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void setID(int id) {
		this.id = id;
	}
}