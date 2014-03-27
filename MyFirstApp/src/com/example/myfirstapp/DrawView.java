package com.example.myfirstapp;

import java.math.BigInteger;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class DrawView extends View{
	private byte[][] state;
	public int base = 0;
	Paint paint = new Paint();
	public int cubeOffset = 50;
	private int vHeight = 0;
	private int vWidth = 0;

	
	public DrawView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public DrawView(Context context, AttributeSet attrs){
		super(context,attrs);

	}
	
	public void onDraw(Canvas canvas){
		vWidth = getWidth();
		vHeight = getHeight();
		
		setDifficultyLevel((byte)3);
		
		state = new byte[getMapHeight()][getMapWidth()];
				
		state[0][0] = 1;//1 => obstacle
		state[1][1] = 1;
		state[1][0] = 2;//2 => bricks
		state[1][2] = 2;
		
		for(int i=0;i<state.length;i++){
			for(int j=0;j<state[i].length;j++){
				if(state[i][j] == 1){
					drawObstacle(i, j, canvas);
				}
				if(state[i][j] == 2){
					drawBricks(i,j,canvas);
				}
			}
		}

	}
	
	private void drawObstacle(int x, int y, Canvas canvas){
		paint.setColor(Color.GRAY);
		paint.setStrokeWidth(0);
		canvas.drawRect(
				cubeOffset * (y+1), 
				cubeOffset * x, 
				cubeOffset * (y+2), 
				cubeOffset * (x+1), paint);
	}
	
	private void drawBricks(int x, int y, Canvas canvas){
		paint.setColor(Color.BLACK);
		canvas.drawRect(
				cubeOffset * (y+1), 
				cubeOffset * x, 
				cubeOffset * (y+2), 
				cubeOffset * (x+1), paint);
		paint.setStrokeWidth(2);
/* Figure out how to draw lines to actually create a real brick block
 * 		canvas.drawLine(
				(cubeOffset * (y+1))+3,
				(cubeOffset * x) +6, 
				(cubeOffset * (y+2))-3, 
				(cubeOffset * (x+1))-4, paint);
*/
		paint.setColor(Color.RED);
		paint.setStrokeWidth(1);
		canvas.drawRect(
				(cubeOffset * (y+1))+3, 
				(cubeOffset * x)+3, 
				(cubeOffset * (y+2))-3, 
				(cubeOffset * (x+1))-3, paint);
		
		
	}
	
	private void drawPlayer(int x, int y, Canvas canvas){
		//TODO: still to draw player
		
	}
		
	
	@Override
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec){

	    int widthSize = MeasureSpec.getSize(widthMeasureSpec);      
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

	    // Restrict the aspect ratio to 1:1, fitting within original specified dimensions
	    int chosenDimension = Math.min(widthSize, heightSize);
	    widthMeasureSpec = MeasureSpec.makeMeasureSpec(chosenDimension, MeasureSpec.AT_MOST);
	    heightMeasureSpec = MeasureSpec.makeMeasureSpec(chosenDimension, MeasureSpec.AT_MOST);

	    getLayoutParams().height = chosenDimension;
	    getLayoutParams().width = chosenDimension;
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);    
	    }

	
	public void setDifficultyLevel(byte size){
		//1 - small	TODO
		//2 - medium TODO
		//3 - large ONLY this works now
		//Using the greatest common divisor between width and height as a base with variable exponents  
		//in order to set the offset (i.e. square edge) that would fit both the width and the height of the screen
		//TODO: find smaller values between exponents 3 and 4
		base = BigInteger.valueOf(vHeight).gcd(BigInteger.valueOf(vWidth)).intValue();
		cubeOffset = (int) Math.pow(base, size);	
	}
	
	public byte getMapWidth(){
		//Return the number of columns as the result of the division 
		//between the width and the offset to identify the width of the 2D array
		if(base==0)
			return 0;
		else
			return (byte) (vWidth/cubeOffset);
	}
	
	public byte getMapHeight(){
		//Return the number of rows as the result of the division 
		//between the height and the offset to find the height of the 2D array
		if(base==0)
			return 0;
		else
			return (byte) (vHeight/cubeOffset);
	}
	
}

