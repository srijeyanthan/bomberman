package com.cmov.bomberman;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity {
	private DrawView drawView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Context mContext =MainActivity.this;
		try {
			ConfigReader.InitConfigParser(mContext);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StandaloneGame game = new StandaloneGame();
		game.joinGame("Cmove");
		
		final int m = ConfigReader.players.getXCor();
		final int n = ConfigReader.players.getYCor();
		final int cols = ConfigReader.getGameDim().column;
		final int rows = ConfigReader.getGameDim().row;
		
		System.out.println("row and col - "+cols +"|" +rows);
		setContentView(R.layout.activity_main);
		
		Bitmap player = BitmapFactory.decodeResource(getResources(),R.drawable.sri);
        drawView = (com.cmov.bomberman.DrawView)findViewById(R.id.bckg);
        RectRender rectrender = new RectRender(rows, cols);
        rectrender.setPlayerBitMap(player);
        drawView.setRenderer(rectrender);
       
        drawView.invalidate();
       
        
		final Button button = (Button) findViewById(R.id.btnBomb);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	         
            	CircleRender circlrender = new CircleRender(1,1);
            	circlrender.setGridCor(rows,cols);
        		drawView.setRenderer(circlrender);
        		drawView.invalidate();
            	
            }
        });
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}