package com.cmov.bomberman.wifidirect;


/*
 * Developer note - Group -2
 *  Please refer the Cell class for detail 
 *  So, Bomb is extending Cell class to store bomb coordinates , and this bomb class have some unique function as well.
 *  functions are not finalized , for this moment , I hope this would be okay. :)
 *  
 *  When we create the bomb we will have pass the player object , so  that we could able to identify that who has placed the bomb
 * */
class Bomb extends Cell {
	private IExplodable ExplodableActivity;
	private IUpdatableScore UpdatableScore;
	private Player player;
	private BombExplosionTimer timer;
	public boolean isExploded;
	private int stage = 1;
	private int playerid = 0;

	public Bomb(int x, int y) {
		super(x, y);

	}

	public void InitBomb(Player player, BomberManWorker server, int playerid) {
		this.player = player;
		timer = new BombExplosionTimer(this);
		isExploded = false;
		this.playerid = playerid;
		ExplodableActivity = (IExplodable) server;
		UpdatableScore = (IUpdatableScore) server;
	}

	public void explode() {
		try {

			String bombexplodemsg = "<" + BombermanProtocol.MESSAGE_TYPE + "="
					+ BombermanProtocol.BOMB_EXPLOSION_MESSAGE + "|"
					+ BombermanProtocol.BOMB_EXPLOSION_COR + "=";
			System.out.println(this + " bomb has exploaded!");
			player.setBombCounter(0);
			boolean isPlayerDead = false;
			Byte CellElement = '-';
			player.game.getLogicalWorld().setElement(worldXCor, worldYCor, 0,
					null);
			ConfigReader.AcquireLock();
			CellElement = ConfigReader.gridlayout[worldXCor][worldYCor];
			if (CellElement == 'x')
				isPlayerDead = true;
			ConfigReader.UpdateGridLayOutCell(worldXCor, worldYCor, (byte) 'e');
			bombexplodemsg += worldXCor + "," + worldYCor + ".";
			int nx = worldXCor;
			int ny = worldYCor;
			int numberofRobotKilled = 0;
			int numberofPlayerkilled = 0;
			boolean rowR = false;
			boolean rowL = false;
			boolean colU = false;
			boolean colD = false;
			for (int i = 1; i <= ConfigReader.getGameConfig().explosionrange; i++) {
				if ((nx + i) < ConfigReader.getGameDim().row) {
					nx += i;
					CellElement = ConfigReader.gridlayout[nx][worldYCor];
					if (CellElement == 'w')
						rowR = true;
					if (CellElement != 'w') // dont update , because we can not
											// break the wall :)
					{
						if (!rowR) {
							if (CellElement == 'o' || CellElement == 'r') {
								player.game.getLogicalWorld().setElement(nx,
										worldYCor, 0, null);
							}
							ConfigReader.UpdateGridLayOutCell(nx, worldYCor,
									(byte) 'e');
							bombexplodemsg += nx + "," + worldYCor + ".";
							if (CellElement == 'r')
								++numberofRobotKilled;
							if (CellElement == '1') {
								// if playerid and bompid is same then we can
								// not give that point to
								// that player
								int existingplayerid = ((Player) player.game
										.getLogicalWorld().getElement(nx,
												worldYCor)[0]).getID();
								if (existingplayerid != this.playerid) {
									++numberofPlayerkilled;
								}
								Server.deadPlayerlist.add(existingplayerid);
							}
						}
					}
				}
				nx = worldXCor;
				if ((nx - i) > 0) {
					nx -= i;
					CellElement = ConfigReader.gridlayout[nx][worldYCor];
					if (CellElement == 'w')
						rowL = true;
					if (CellElement != 'w') {
						if (!rowL) {
							if (CellElement == 'o' || CellElement == 'r') {
								player.game.getLogicalWorld().setElement(nx,
										worldYCor, 0, null);
							}
							ConfigReader.UpdateGridLayOutCell(nx, worldYCor,
									(byte) 'e');
							bombexplodemsg += nx + "," + worldYCor + ".";
							if (CellElement == 'r')
								++numberofRobotKilled;

							if (CellElement == '1') {
								int existingplayerid = ((Player) player.game
										.getLogicalWorld().getElement(nx,
												worldYCor)[0]).getID();
								if (existingplayerid != this.playerid) {
									++numberofPlayerkilled;
								}
								Server.deadPlayerlist.add(existingplayerid);
							}
						}
					}
				}
				if ((ny + i) < ConfigReader.getGameDim().column) {
					ny += i;
					CellElement = ConfigReader.gridlayout[worldXCor][ny];
					if (CellElement == 'w')
						colD = true;
					if (CellElement != 'w') {
						if (!colD) {
							if (CellElement == 'o' || CellElement == 'r') {
								player.game.getLogicalWorld().setElement(
										worldXCor, ny, 0, null);
							}
							ConfigReader.UpdateGridLayOutCell(worldXCor, ny,
									(byte) 'e');
							bombexplodemsg += worldXCor + "," + ny + ".";
							if (CellElement == 'r')
								++numberofRobotKilled;
							if (CellElement == '1') {
								int existingplayerid = ((Player) player.game
										.getLogicalWorld().getElement(
												worldXCor, ny)[0]).getID();
								if (existingplayerid != this.playerid) {
									++numberofPlayerkilled;
								}
								Server.deadPlayerlist.add(existingplayerid);
							}
						}
					}
				}
				ny = worldYCor;
				if ((ny - i) > 0) {
					ny -= i;
					CellElement = ConfigReader.gridlayout[worldXCor][ny];
					if (CellElement == 'w')
						colU = true;
					if (CellElement != 'w') {
						if (!colU) {
							if (CellElement == 'o' || CellElement == 'r') {
								player.game.getLogicalWorld().setElement(
										worldXCor, ny, 0, null);
							}
							ConfigReader.UpdateGridLayOutCell(worldXCor, ny,
									(byte) 'e');
							bombexplodemsg += worldXCor + "," + ny + ".";
							if (CellElement == 'r')
								++numberofRobotKilled;
							if (CellElement == '1') {
								int existingplayerid = ((Player) player.game
										.getLogicalWorld().getElement(
												worldXCor, ny)[0]).getID();
								if (existingplayerid != this.playerid) {
									++numberofPlayerkilled;
								}
								Server.deadPlayerlist.add(existingplayerid);
							}
						}
					}
				}
				nx = worldXCor;
				ny = worldYCor;
				
			}
			timer.cancel();
			if (bombexplodemsg.charAt(bombexplodemsg.length() - 1) == '.') {
				bombexplodemsg = bombexplodemsg.replaceFirst(".$", "");
			}
			bombexplodemsg += ">";
			System.out.println("[SERVER] Bomb exploaded message is - "
					+ bombexplodemsg);
			ExplodableActivity.Exploaded(isPlayerDead, bombexplodemsg);
			UpdatableScore.UpdateScore(this.playerid, numberofRobotKilled,
					numberofPlayerkilled);

			// (player.game, gridX, gridY, player.bombDistance);
			// TO-DO: notify this explosion to front end , so that we can draw

			
			ConfigReader.ReleaseLock();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public String getImageFilename() {
		return "resource/bomb" + stage + ".png";
	}

	int tick() {
		return ++stage;
	}
}