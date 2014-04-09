package com.cmov.bomberman;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity {
	DrawView drawView;
	

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
		
		final int x = ConfigReader.players.getXCor();
		final int y = ConfigReader.players.getYCor();
		
		drawView = new DrawView(this);
		setContentView(R.layout.activity_main);
		final Button button = (Button) findViewById(R.id.btnBomb);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	drawView.drawBomb(1,1);
            	com.cmov.bomberman.DrawView mycanvas = (com.cmov.bomberman.DrawView)findViewById(R.id.bckg);
            	 mycanvas.postInvalidate();
               
              
              // v.invalidate();
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