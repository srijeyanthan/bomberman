package com.cmov.bomberman.server;

public class BombermanServerDef {
	public static final int MESSAGE_TYPE = 1;
	public static final int USER_NAME = 2;
	public static final int BOMB_PLACEMENT = 3;
	public static final int PLAYER_MOVEMENT = 4;
	public static final int ROBOT_NEW_PLACE = 5;
	public static final int ROBOT_ORIGINAL_PLACE = 6;
	public static final int GRID_ROW=7;
	public static final int GRID_COLUMN=8;
	public static final int GRID_ELEMENTS=10;

	public static final byte JOIN_MESSAGE = 'J';
	public static final byte PLAYER_MOVEMENT_MESSAGE = 'P';
	public static final byte BOMP_PLACEMET_MESSAGE = 'B';
	public static final byte ROBOT_PLACEMET_MESSAGE = 'R';
	public static final byte GRID_MESSAGE ='M';
}
