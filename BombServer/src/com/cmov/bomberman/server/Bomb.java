package com.cmov.bomberman.server;


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

	public Bomb(int x, int y) {
		super(x, y);
		
	}

	public void InitBomb( Player player,BomberManWorker server)
	{
		this.player = player;
		timer = new BombExplosionTimer(this);
		isExploded = false;

		ExplodableActivity = (IExplodable) server;
		UpdatableScore = (IUpdatableScore) server;
	}
	public void explode() {
		try {

			String bombexplodemsg = "<" + BombermanProtocol.MESSAGE_TYPE
					+ "=" + BombermanProtocol.BOMB_EXPLOSION_MESSAGE
					+ "|" + BombermanProtocol.BOMB_EXPLOSION_COR+"=";
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
			ConfigReader.UpdateGridLayOutCell(worldXCor, worldYCor, (byte) '-');
			bombexplodemsg += worldXCor+","+worldYCor+".";
			int nx = worldXCor;
			int ny = worldYCor;
			int numberofElementsDied = 0;
			if (++nx < ConfigReader.getGameDim().row) {
				CellElement = ConfigReader.gridlayout[nx][worldYCor];
				if (CellElement != 'w') // dont update , because we can not
										// break the wall :)
				{

					if (CellElement == 'o' || CellElement == 'r') {
						player.game.getLogicalWorld().setElement(nx, worldYCor,
								0, null);

					}
					ConfigReader
							.UpdateGridLayOutCell(nx, worldYCor, (byte) '-');
					bombexplodemsg += nx+","+worldYCor+".";
					if (CellElement == 'r')
						++numberofElementsDied;

				}
				if (CellElement == '1')
					isPlayerDead = true;
			}
			nx = worldXCor;
			if (--nx > 0) {
				CellElement = ConfigReader.gridlayout[nx][worldYCor];

				if (CellElement != 'w') {
					if (CellElement == 'o' || CellElement == 'r') {
						player.game.getLogicalWorld().setElement(nx, worldYCor,
								0, null);
					}
					ConfigReader
							.UpdateGridLayOutCell(nx, worldYCor, (byte) '-');
					bombexplodemsg += nx+","+worldYCor+".";
					if (CellElement == 'r')
						++numberofElementsDied;

				}
				if (CellElement == '1')
					isPlayerDead = true;

			}
			if (++ny < ConfigReader.getGameDim().column) {
				CellElement = ConfigReader.gridlayout[worldXCor][ny];

				if (CellElement != 'w') {
					if (CellElement == 'o' || CellElement == 'r') {
						player.game.getLogicalWorld().setElement(worldXCor, ny,
								0, null);

					}
					ConfigReader
							.UpdateGridLayOutCell(worldXCor, ny, (byte) '-');
					bombexplodemsg += worldXCor+","+ny+".";
					if (CellElement == 'r')
						++numberofElementsDied;
				}
				if (CellElement == '1')
					isPlayerDead = true;
			}
			ny = worldYCor;
			if (--ny > 0) {
				CellElement = ConfigReader.gridlayout[worldXCor][ny];
				if (CellElement != 'w') {
					if (CellElement == 'o' || CellElement == 'r') {
						player.game.getLogicalWorld().setElement(worldXCor, ny,
								0, null);

					}
					ConfigReader
							.UpdateGridLayOutCell(worldXCor, ny, (byte) '-');
					bombexplodemsg += worldXCor+","+ny+".";
					// ConfigReader.gridlayout[worldXCor][ny] = '-';
					if (CellElement == 'r')
						++numberofElementsDied;

				}
				if (CellElement == '1')
					isPlayerDead = true;

			}
			timer.cancel();
			// (player.game, gridX, gridY, player.bombDistance);
			// TO-DO: notify this explosion to front end , so that we can draw

			if (bombexplodemsg
					.charAt(bombexplodemsg.length() - 1) == '.') {
				bombexplodemsg = bombexplodemsg.replaceFirst(
						".$", "");
			}
			bombexplodemsg += ">";
			System.out.println("[SERVER] Bomb exploaded message is - "+bombexplodemsg);
			ExplodableActivity.Exploaded(isPlayerDead,bombexplodemsg);
			UpdatableScore.UpdateScore(numberofElementsDied);
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