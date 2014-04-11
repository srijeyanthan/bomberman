package com.cmov.bomberman;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

public class RectRender implements DrawView.ShapeRenderer {
	public Byte[][] grid;
	public int row;
	public int column;
	public int xOffset;
	public int yOffset;
	public Bitmap playermap;
	
	public void setPlayerBitMap(Bitmap playermap) {
		this.playermap = playermap;
	}

	public RectRender(int gridrow, int gridcolumn) {
		this.row = gridrow;
		this.column = gridcolumn;
	}

	private void CalculateOffset(int Height, int Width) {
		this.xOffset = Height / row;
		this.yOffset = Width / column;
		System.out.println("Offset has bee set xOfset -" + xOffset + "|yOffset"
				+ yOffset);
	}

	public void drawPlayer(int x, int y, Paint paint, Canvas canvas) {

		paint.setStrokeWidth(1);
		canvas.drawBitmap(getResizedBitmap(playermap, xOffset, yOffset),
				(yOffset * y), (xOffset * x), paint);

		// TODO: use drawLine, drawCircle for a non-bitmap player
	}

	private Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);
		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);
		return resizedBitmap;

	}

	@Override
	public void drawShape(Canvas canvas, Paint paint) {
		grid = ConfigReader.getGridLayout();
		CalculateOffset(canvas.getHeight(), canvas.getHeight());

		for (int x = 0; x < row; x++) {
			for (int y = 0; y < column; y++) {
				if (grid[x][y] == 'w') {

					paint.setColor(Color.LTGRAY);
					paint.setStrokeWidth(0);
					canvas.drawRect((yOffset * y), (xOffset * x), yOffset
							* (y + 1), xOffset * (x + 1), paint);
				}
				else if (grid[x][y] == 'o') {
					paint.setColor(Color.RED);
					paint.setStrokeWidth(0);
					canvas.drawRect((yOffset * y), (xOffset * x), yOffset
							* (y + 1), xOffset * (x + 1), paint);
				}
				else if (grid[x][y] == '1') {
					drawPlayer(x, y, paint, canvas);
					
					
				}
				else if (grid[x][y] == 'b') {
					paint.setColor(Color.BLACK);
					canvas.drawCircle((yOffset * y) + yOffset / 2,
							(xOffset * x) + xOffset / 2, xOffset / 3, paint);

				}
				else if(grid[x][y]=='r')
				{
						paint.setColor(Color.WHITE);
						canvas.drawCircle((yOffset * y) + yOffset / 2,
								(xOffset * x) + xOffset / 2, xOffset / 3, paint);
				}
				else if(grid[x][y]=='x')
				{
					drawPlayer(x, y, paint, canvas);
					paint.setColor(Color.BLACK);
					canvas.drawCircle((yOffset * y) + yOffset / 2,
							(xOffset * x) + xOffset / 2, xOffset / 3, paint);
				}
				else if(grid[x][y]=='E')
				{
					paint.setColor(Color.GREEN);
					paint.setStrokeWidth(0);
					canvas.drawRect((yOffset * y), (xOffset * x), yOffset
							* (y + 1), xOffset * (x + 1), paint);
				}

			}

		}

	}
}