package com.cmov.bomberman.server;

public class BombermanProtocol {
	public static final int MESSAGE_TYPE = 1;
	public static final int USER_NAME = 2;
	public static final int BOMB_PLACEMENT = 3;
	public static final int PLAYER_MOVEMENT = 4;
	public static final int ROBOT_NEW_PLACE = 5;
	public static final int ROBOT_ORIGINAL_PLACE = 6;
	public static final int GRID_ROW=7;
	public static final int GRID_COLUMN=8;
	public static final int GRID_ELEMENTS=10;
	public static final int PLAYER_ID=11;
	public static final int PLAYER_OLD_POS=12;  /* this will be used send player movement */
	public static final int PLAYER_NEW_POS=13;
	public static final int OLD_ELEMENT_TYPE=14;
	public static final int NEW_ELEMENT_TYPE=15;
	public static final int NEW_PLAYER_JOIN_COR=16;
	public static final int BOMB_EXPLOSION_COR=17;
	public static final int GAME_STAT=18;
	public static final int QUIT_PLAYER_POS=19;
	public static final int SCORE=20;


	public static final byte JOIN_MESSAGE = 'J';
	public static final byte PLAYER_MOVEMENT_MESSAGE = 'P';
	public static final byte BOMP_PLACEMET_MESSAGE = 'B';
	public static final byte ROBOT_PLACEMET_MESSAGE = 'R';
	public static final byte GRID_MESSAGE ='M';
	public static final byte NEW_PLAYER_JOIN='N';
	public static final byte BOMB_EXPLOSION_MESSAGE='O';
	public static final byte GAME_END_MESSAGE='P';
	public static final byte GAME_QUIT_MESSAGE='Q';
	public static final byte INDIVIDUAL_SCORE_UPDATE='S';
}
