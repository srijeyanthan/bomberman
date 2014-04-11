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

	public Bomb(int x, int y, Player player, Activity activity) {
		super(x, y);
		this.player = player;
		timer = new BombExplosionTimer(this);
		isExploded = false;

		ExplodableActivity = (IExplodable) activity;
		UpdatableScore = (IUpdatableScore) activity;
	}

	public void explode() {
		try {
			ExplodableActivity.Exploaded();
			System.out.println(this + " bomb has exploaded!");
			player.bombs.remove(this);
			player.game.getLogicalWorld().setElement(worldXCor, worldYCor, 0,
					null);
			ConfigReader.UpdateGridLayOutCell(worldXCor, worldYCor, (byte) '-');
			int nx = worldXCor;
			int ny = worldYCor;
			int numberofElementsDied=0;
			if (++nx < ConfigReader.getGameDim().row) {
				Byte CellElement = ConfigReader.gridlayout[nx][worldYCor];
				if (CellElement != 'w') // dont update , because we can not
										// break the wall :)
				{

					if (CellElement == 'o' || CellElement == 'r') {
						player.game.getLogicalWorld().setElement(nx, worldYCor,
								0, null);

					}
					ConfigReader.UpdateGridLayOutCell(nx, worldYCor, (byte) '-');
					if (CellElement == 'r')
						++numberofElementsDied;

				}

			}
			nx = worldXCor;
			if (--nx > 0) {
				Byte CellElement = ConfigReader.gridlayout[nx][worldYCor];

				if (CellElement != 'w') {
					if (CellElement == 'o' || CellElement == 'r') {
						player.game.getLogicalWorld().setElement(nx, worldYCor,
								0, null);
					}
					ConfigReader.UpdateGridLayOutCell(nx, worldYCor, (byte) '-');
					if (CellElement == 'r')
						++numberofElementsDied;

				}

			}
			if (++ny < ConfigReader.getGameDim().column) {
				Byte CellElement = ConfigReader.gridlayout[worldXCor][ny];

				if (CellElement != 'w') {
					if (CellElement == 'o' || CellElement == 'r') {
						player.game.getLogicalWorld().setElement(worldXCor, ny,
								0, null);

					}
					ConfigReader.UpdateGridLayOutCell(worldXCor, ny, (byte) '-');
					if (CellElement == 'r')
						++numberofElementsDied;
				}

			}
			ny = worldYCor;
			if (--ny > 0) {
				Byte CellElement = ConfigReader.gridlayout[worldXCor][ny];
				if (CellElement != 'w') {
					if (CellElement == 'o' || CellElement == 'r') {
						player.game.getLogicalWorld().setElement(worldXCor, ny,
								0, null);

					}
					ConfigReader.UpdateGridLayOutCell(worldXCor, ny, (byte) '-');
					//ConfigReader.gridlayout[worldXCor][ny] = '-';
					if (CellElement == 'r')
						++numberofElementsDied;
						

				}

			}
			timer.cancel();
			// (player.game, gridX, gridY, player.bombDistance);
			// TO-DO: notify this explosion to front end , so that we can draw
			UpdatableScore.UpdateScore(numberofElementsDied);
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