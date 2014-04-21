
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