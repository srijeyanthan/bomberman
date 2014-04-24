package com.cmov.bomberman;

import android.app.Activity;
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
	public Activity activity;
	private int range = 3; //ConfigReader.getGameConfig().explosionrange;

	public Bomb(int x, int y) {
		super(x, y);
		
	}

	public void InitBomb( Player player, Activity activity)
	{
		this.player = player;
		timer = new BombExplosionTimer(this);
		isExploded = false;

		ExplodableActivity = (IExplodable) activity;
		UpdatableScore = (IUpdatableScore) activity;
	}
	public void explode() {
		try {

			System.out.println(this + " bomb has exploaded!");
			player.setBombCounter(0);
			boolean isPlayerDead = false;
			Byte CellElement = '-';
			player.game.getLogicalWorld().setElement(worldXCor, worldYCor, 0,
					null);
			ConfigReader.LockTheGrid();
			CellElement = ConfigReader.gridlayout[worldXCor][worldYCor];
			if (CellElement == 'x')
				isPlayerDead = true;
			ConfigReader.UpdateGridLayOutCell(worldXCor, worldYCor, (byte) '-');
			int nx = worldXCor;
			int ny = worldYCor;
			int numberofElementsDied = 0;
			
			for(int i = 1; i <= range; i++){
				if ((nx + i) < ConfigReader.getGameDim().row) {
					nx +=i;
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
						if (CellElement == 'r')
							++numberofElementsDied;
					}
					if (CellElement == '1')
						isPlayerDead = true;
				}
				nx = worldXCor;
				if ((nx - i) > 0) {
					nx -= i;
					CellElement = ConfigReader.gridlayout[nx][worldYCor];

					if (CellElement != 'w') {
						if (CellElement == 'o' || CellElement == 'r') {
							player.game.getLogicalWorld().setElement(nx, worldYCor,
									0, null);
						}
						ConfigReader
								.UpdateGridLayOutCell(nx, worldYCor, (byte) '-');
						if (CellElement == 'r')
							++numberofElementsDied;
					}
					if (CellElement == '1')
						isPlayerDead = true;
				}
				if ((ny + i) < ConfigReader.getGameDim().column) {
					ny += i;
					CellElement = ConfigReader.gridlayout[worldXCor][ny];

					if (CellElement != 'w') {
						if (CellElement == 'o' || CellElement == 'r') {
							player.game.getLogicalWorld().setElement(worldXCor, ny,
									0, null);
						}
						ConfigReader
								.UpdateGridLayOutCell(worldXCor, ny, (byte) '-');
						if (CellElement == 'r')
							++numberofElementsDied;
					}
					if (CellElement == '1')
						isPlayerDead = true;
				}
				ny = worldYCor;
				if ((ny-i) > 0) {
					ny -= i;
					CellElement = ConfigReader.gridlayout[worldXCor][ny];
					if (CellElement != 'w') {
						if (CellElement == 'o' || CellElement == 'r') {
							player.game.getLogicalWorld().setElement(worldXCor, ny,
									0, null);
						}
						ConfigReader
								.UpdateGridLayOutCell(worldXCor, ny, (byte) '-');
						// ConfigReader.gridlayout[worldXCor][ny] = '-';
						if (CellElement == 'r')
							++numberofElementsDied;
					}
					if (CellElement == '1')
						isPlayerDead = true;
				}
			}


			timer.cancel();
			// (player.game, gridX, gridY, player.bombDistance);
			// TO-DO: notify this explosion to front end , so that we can draw

			ExplodableActivity.Exploaded(isPlayerDead);
			UpdatableScore.UpdateScore(numberofElementsDied);
			ConfigReader.UnlockTheGrid();
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