/*package com.cmov.bomberman;

import java.math.BigInteger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.cmov.bomberman.R;

public class DrawView extends View {
	public byte[][] state;
	public int base = 0;
	Paint paint = new Paint();
	public int reqOffset = 50;
	private int vHeight = 0;
	private int vWidth = 0;
	private Bitmap player = BitmapFactory.decodeResource(getResources(),
			R.drawable.sri);

	private Canvas canvas;

	// TODO: move these into a configuration class
	private static byte OBSTACLE = 1;
	private static byte BRICKS = 2;
	private static byte PLAYER = 3;
	private static byte BOMB = 4;

	public DrawView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void onDraw(Canvas canvas) {
		this.canvas = canvas;
		vWidth = getWidth();
		vHeight = getHeight();

		setDifficultyLevel((byte) 3); // default level

		state = new byte[getMapHeight()][getMapWidth()];

		// Populate grid with obstacles
		for (int i = 0; i < state.length; i++) {
			for (int j = 0; j < state[i].length; j++) {
				state[i][0] = OBSTACLE;
				state[0][j] = OBSTACLE;
				// state[i][state.length-1] = OBSTACLE;
				// state[state.length-1][i] = OBSTACLE;
				if (i % 2 == 1 && j % 2 == 1) {
					state[i][j] = OBSTACLE;
				}
			}
		}
		draw(state);

		// TESTING
		drawBomb(2, 7);
		drawExplosion(4, 4, 3);

	}

	public void drawObstacle(int x, int y) {

		paint.setColor(Color.GRAY);
		paint.setStrokeWidth(0);
		canvas.drawRect((reqOffset * x) + 1, (reqOffset * y) + 1, reqOffset
				* (x + 1) - 1, reqOffset * (y + 1) - 1, paint);
	}

	public void drawBricks(int x, int y) {
		if (state[x][y] == OBSTACLE) {
			return;
		}

		paint.setColor(Color.RED);
		paint.setStrokeWidth(1);
		canvas.drawRect((reqOffset * x) + 1, (reqOffset * y) + 1, reqOffset
				* (x + 1) - 1, reqOffset * (y + 1) - 1, paint);
	}

	public void drawPlayer(int x, int y) {
		if (state[x][y] == BRICKS || state[x][y] == OBSTACLE) {
			return;
		}
		paint.setStrokeWidth(1);
		canvas.drawBitmap(getResizedBitmap(player, reqOffset, reqOffset),
				(reqOffset * x) + 2, (reqOffset * x) + 2, paint);
		// TODO: use drawLine, drawCircle for a non-bitmap player
	}

	public void drawBomb(int x, int y) {
		paint.setColor(Color.BLACK);
		canvas.drawCircle((reqOffset * x) + reqOffset / 2, (reqOffset * y)
				+ reqOffset / 2, reqOffset / 3, paint);
	}

	public void drawEmpty(int x, int y) {
		if (state[x][y] == OBSTACLE)
			return;
		state[x][y] = 0;
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(0);
		canvas.drawRect((reqOffset * x) + 1, (reqOffset * y) + 1, reqOffset
				* (x + 1) - 1, reqOffset * (y + 1) - 1, paint);
	}

	public void drawExplosion(int x, int y, int range) {

		if (state[x][y] == OBSTACLE)
			return;

		int uvr = 1;
		int dvr = 1;
		int lhr = 1;
		int rhr = 1;

		// iterate and update individual ranges
		for (int i = 0; i < range; i++) {
			if (y - i >= 0) {
				if (state[x][y - i] == OBSTACLE || ((y - i) % 2 == 0)
						&& (i > 0)) {
					continue;
				}
				if (state[x][y - i] != OBSTACLE) {
					uvr += i;
				}
			}

			if (y + i < getMapHeight()) {
				if (state[x][y + i] == OBSTACLE
						|| (((y + i) % 2 == 0) && (i > 0))) {
					continue;
				}

				if (state[x][y + i] != OBSTACLE) {
					dvr += i;
				}
			}

			if (x - i >= 0) {
				if (state[x - i][y] == OBSTACLE
						|| (((x - i) % 2 == 0) && (i > 0))) {
					continue;
				}

				if (state[x - i][y] != OBSTACLE) {
					lhr += i;
				}
			}

			if (x + i <= getMapWidth()) {
				if (state[x + i][y] == OBSTACLE
						|| (((x + i) % 2 == 0) && (i > 0))) {
					continue;
				}

				if (state[x + i][y] != OBSTACLE) {
					rhr += i;
				}
			}
		}

		paint.setStrokeWidth(10);
		paint.setColor(Color.RED);

		// Drawing each direction's ranges
		// UVR
		canvas.drawLine((reqOffset * x) + reqOffset / 2, reqOffset * (y - uvr),
				(reqOffset * x) + reqOffset / 2, (reqOffset * y) + reqOffset,
				paint);

		// DVR
		canvas.drawLine((reqOffset * x) + reqOffset / 2, reqOffset * y,
				(reqOffset * x) + reqOffset / 2, (reqOffset * (y + dvr))
						+ reqOffset, paint);

		// LHR
		canvas.drawLine(reqOffset * (x - lhr), (reqOffset * y) + reqOffset / 2,
				(reqOffset * x) + reqOffset / 2, (reqOffset * y) + reqOffset
						/ 2, paint);

		// RHR
		canvas.drawLine((reqOffset * x) + reqOffset / 2, (reqOffset * y)
				+ reqOffset / 2, reqOffset * (x + rhr) + reqOffset,
				(reqOffset * y) + reqOffset / 2, paint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		// Restrict the aspect ratio to 1:1, fitting within original specified
		// dimensions
		int chosenDimension = Math.min(widthSize, heightSize);
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(chosenDimension,
				MeasureSpec.AT_MOST);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(chosenDimension,
				MeasureSpec.AT_MOST);

		getLayoutParams().height = chosenDimension;
		getLayoutParams().width = chosenDimension;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setDifficultyLevel(byte size) {
		// 1 - small TODO
		// 2 - medium TODO
		// 3 - large ONLY this works now
		// Using the greatest common divisor between width and height as a base
		// with variable exponents
		// in order to set the offset (i.e. square edge) that would fit both the
		// width and the height of the screen
		// TODO: find smaller values between exponents 3 and 4
		if (vHeight % 1 == 0 && vWidth % 1 == 0) {
			base = BigInteger.valueOf(vHeight).gcd(BigInteger.valueOf(vWidth))
					.intValue();
		} else if (vHeight % 1 != 0) {
			vHeight--;
			base = BigInteger.valueOf(vHeight).gcd(BigInteger.valueOf(vWidth))
					.intValue();
		} else if (vWidth % 1 != 0) {
			vWidth--;
			base = BigInteger.valueOf(vHeight).gcd(BigInteger.valueOf(vWidth))
					.intValue();
		}
		reqOffset = (int) Math.pow(base, size);
	}

	public byte getMapWidth() {
		// Return the number of columns
		// as the result of width/offset
		if (base == 0)
			return 0;
		else
			return (byte) (vWidth / reqOffset);
	}

	public byte getMapHeight() {
		// Return the number of rows
		// as the result of height/offset
		if (base == 0)
			return 0;
		else
			return (byte) (vHeight / reqOffset);
	}

	public byte[][] getState() {
		return state;
	}

	public byte getCellType(int x, int y) {
		return state[x][y];
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

	
	 * private void draw(byte[][] state) { for (int i = 0; i < state.length;
	 * i++) { for (int j = 0; j < state[i].length; j++) { if (state[i][j] ==
	 * OBSTACLE) { drawObstacle(i, j); } if (state[i][j] == BRICKS) {
	 * drawBricks(i, j); } if (state[i][j] == PLAYER) { drawPlayer(i, j); } if
	 * (state[i][j] == BOMB) { drawBomb(i, j); } } }
	 * 
	 * }
	 
	private void draw(byte[][] state) {
		Byte[][] gridLayout = ConfigReader.getGridLayout();
		int cols = ConfigReader.getGameDim().column;
		int rows = ConfigReader.getGameDim().row;
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < cols; y++) {
				if (gridLayout[x][y] == 'W')
					drawBricks(0,0);
				else if (gridLayout[x][y] == 'O')
					drawObstacle(x, y);
				else if (gridLayout[x][y] == '1')   // we will first draw only one palyer.
					drawPlayer(x, y);

			}

		}
	}

}
*/



/*package com.cmov.bomberman;

import java.math.BigInteger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.cmov.bomberman.R;

public class DrawView extends View {
	public Byte[][] state;
	public int base = 0;
	Paint paint = new Paint();
	public int reqOffset = 50;
	public int yoffset=41;
	public int xoffset=60;
	private int vHeight = 0;
	private int vWidth = 0;
	private Bitmap player = BitmapFactory.decodeResource(getResources(),
			R.drawable.sri);

	public  Canvas canvas;
	public int state2 = 0;


	public DrawView(Context context) {
		super(context);
		
		// TODO Auto-generated constructor stub
	}

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		this.canvas = canvas;
		vWidth = getWidth();
		vHeight = getHeight();

		setDifficultyLevel((byte) 3); // default level

		state =ConfigReader.getGridLayout();

		//drawBricks(1,0);
		draw(state);
		//drawBomb(1, 1);

		

	}

	public void drawObstacle(int x, int y) {

		paint.setColor(Color.GRAY);
		paint.setStrokeWidth(0);
		canvas.drawRect((yoffset * x) , (xoffset * y) , yoffset
				* (x +1), xoffset * (y+1), paint);
	}

	public void drawBricks(int x, int y) {

		paint.setColor(Color.RED);
		paint.setStrokeWidth(1);
		canvas.drawRect((yoffset * x) , (xoffset * y) , yoffset
				* (x +1), xoffset * (y+1), paint);
	}

	public void drawPlayer(int x, int y) {
		
		paint.setStrokeWidth(1);
		canvas.drawBitmap(getResizedBitmap(player, reqOffset, reqOffset),
				(reqOffset * x) + 2, (reqOffset * x) + 2, paint);
		// TODO: use drawLine, drawCircle for a non-bitmap player
	}

	public void drawBomb(int x, int y) {
		this.invalidate();
		paint.setColor(Color.BLACK);
		if(canvas !=null)
		{
			System.out.println("canvas is not null");
			
			canvas.drawCircle((yoffset * x) + yoffset / 2, (xoffset * y)+ xoffset / 2, reqOffset / 3, paint);
			invalidate();
		}
		else
			System.out.println("canvas in null");
	}

	public void drawEmpty(int x, int y) {
		
		state[x][y] = 0;
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(0);
		canvas.drawRect((reqOffset * x) + 1, (reqOffset * y) + 1, reqOffset
				* (x + 1) - 1, reqOffset * (y + 1) - 1, paint);
	}

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		// Restrict the aspect ratio to 1:1, fitting within original specified
		// dimensions
		int chosenDimension = Math.min(widthSize, heightSize);
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(chosenDimension,
				MeasureSpec.AT_MOST);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(chosenDimension,
				MeasureSpec.AT_MOST);

		getLayoutParams().height = chosenDimension;
		getLayoutParams().width = chosenDimension;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setDifficultyLevel(byte size) {
		// 1 - small TODO
		// 2 - medium TODO
		// 3 - large ONLY this works now
		// Using the greatest common divisor between width and height as a base
		// with variable exponents
		// in order to set the offset (i.e. square edge) that would fit both the
		// width and the height of the screen
		// TODO: find smaller values between exponents 3 and 4
		if (vHeight % 1 == 0 && vWidth % 1 == 0) {
			base = BigInteger.valueOf(vHeight).gcd(BigInteger.valueOf(vWidth))
					.intValue();
		} else if (vHeight % 1 != 0) {
			vHeight--;
			base = BigInteger.valueOf(vHeight).gcd(BigInteger.valueOf(vWidth))
					.intValue();
		} else if (vWidth % 1 != 0) {
			vWidth--;
			base = BigInteger.valueOf(vHeight).gcd(BigInteger.valueOf(vWidth))
					.intValue();
		}
		reqOffset = (int) Math.pow(base, size);
	}

	public byte getMapWidth() {
		// Return the number of columns
		// as the result of width/offset
		if (base == 0)
			return 0;
		else
			return (byte) (vWidth / reqOffset);
	}

	public byte getMapHeight() {
		// Return the number of rows
		// as the result of height/offset
		if (base == 0)
			return 0;
		else
			return (byte) (vHeight / reqOffset);
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

	
	 
	 
	private void draw(Byte[][] state) {
		int cols = ConfigReader.getGameDim().column;
		int rows = ConfigReader.getGameDim().row;
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < cols; y++) {
				System.out.println(state[x][y].toString());
				if (state[x][y] == 'w')
					drawBricks(y,x);
				else if (state[x][y] == 'o')
					drawObstacle(y,x);
				else if (state[x][y] == '1')   // we will first draw only one palyer.
					drawPlayer(x, y);

			}

		}
	}

}
*/
package com.cmov.bomberman;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


public class DrawView extends View {

    private Paint mPaint;
    private ShapeRenderer mRenderer;
    

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        mPaint = new Paint();
    }

    public void setPaintColor(int color){
        mPaint.setColor(color);
    }

    public void setPaintStrokeWidth(float width){
        mPaint.setStrokeWidth(width);
    }

    public void setRenderer(ShapeRenderer renderer) {
        mRenderer = renderer;
    }
   
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
       
        if(mRenderer != null){
        	
            mRenderer.drawShape(canvas,mPaint);
        }
    }

    public static interface ShapeRenderer{
        public void drawShape(Canvas canvas, Paint paint);
    }
}