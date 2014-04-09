package com.cmov.bomberman;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class CircleRender implements DrawView.ShapeRenderer {

	private int xBombpos;
	private int yBombpos;
	public int xOffset;
	public int yOffset;

	public int row;
	public int column;
	public void setGridCor(int gridrow, int gridcolumn) {
		this.row = gridrow;
		this.column = gridcolumn;
	}
	public CircleRender(int x, int y) {
		this.xBombpos = x;
		this.yBombpos = y;
	}
	private void CalculateOffset(int Height, int Width) {
		this.xOffset = Height / row;
		this.yOffset = Width / column;
		System.out.println("Offset has bee set xOfset -" + xOffset + "|yOffset"
				+ yOffset);
	}

	@Override
	public void drawShape(Canvas canvas, Paint paint) {
				paint.setColor(Color.BLACK);
				CalculateOffset(canvas.getHeight(),canvas.getWidth());
	canvas.drawCircle((yOffset * yBombpos) + yOffset / 2, (xOffset * xBombpos)+ xOffset / 2, xOffset / 3, paint);
					
			

	}
}