package com.cmov.bomberman;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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

public class MainActivity extends Activity implements IExplodable{
	private DrawView drawView;
	final StandaloneGame standGame = new StandaloneGame();
	final Bitmap player=null;
	
	private LogicalWorld logicalworld=null;
	
	public void Render(int rows , int cols,Bitmap player)
	{
		RectRender rectrender = new RectRender(rows, cols);
		rectrender.setPlayerBitMap(player);
		drawView.setRenderer(rectrender);
		drawView.invalidate();
		
	}
	
	
	public void Exploaded(int row , int col)
	{
		RectRender rectrender = new RectRender(13, 19);
		rectrender.setPlayerBitMap(player);
		drawView.setRenderer(rectrender);
		drawView.postInvalidate();
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Context mContext = MainActivity.this;
		try {
			ConfigReader.InitConfigParser(mContext);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		standGame.joinGame("Cmove",MainActivity.this);
		
		logicalworld= standGame.getBombermanGame().getLogicalWorld();

		final int m = ConfigReader.players.getXCor();
		final int n = ConfigReader.players.getYCor();
		final int cols = ConfigReader.getGameDim().column;
		final int rows = ConfigReader.getGameDim().row;

		setContentView(R.layout.activity_main);

		final Bitmap player = BitmapFactory.decodeResource(getResources(),
				R.drawable.sri);
		drawView = (com.cmov.bomberman.DrawView) findViewById(R.id.bckg);
		RectRender rectrender = new RectRender(rows, cols);
		rectrender.setPlayerBitMap(player);
		drawView.setRenderer(rectrender);

		drawView.invalidate();

		final Button button = (Button) findViewById(R.id.btnBomb);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (standGame.getBombermanGame().getPlayers().size() == 0) {
					System.out.println("Player list is null. Warning.");
				}else
				{
					List<Player> localPlayerList = standGame.getBombermanGame().getPlayers();
					localPlayerList.get(0).placeBomb();
					Render(rows,cols,player);
				}
				

			}
		});
		
		final Button leftbutton = (Button) findViewById(R.id.btnLeft);
		leftbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (standGame.getBombermanGame().getPlayers().size() == 0) {
					System.out.println("Player list is null. Warning.");
				}else
				{
					List<Player> localPlayerList = standGame.getBombermanGame().getPlayers();
					boolean ismoved = standGame.getBombermanGame().movePlayer(localPlayerList.get(0), 0,-1);
					if(ismoved)
						System.out.println("player has been moved.....");
					
					Render(rows,cols,player);
				}
				

			}
		});
		
		final Button rightbutton = (Button) findViewById(R.id.btnRight);
		rightbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (standGame.getBombermanGame().getPlayers().size() == 0) {
					System.out.println("Player list is null. Warning.");
				}else
				{
					List<Player> localPlayerList = standGame.getBombermanGame().getPlayers();
					boolean ismoved = standGame.getBombermanGame().movePlayer(localPlayerList.get(0), 0,1);
					if(ismoved)
						System.out.println("player has been moved.....");
					
					Render(rows,cols,player);
				}
				

			}
		});
		
		final Button upbutton = (Button) findViewById(R.id.btnUp);
		upbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (standGame.getBombermanGame().getPlayers().size() == 0) {
					System.out.println("Player list is null. Warning.");
				}else
				{
					List<Player> localPlayerList = standGame.getBombermanGame().getPlayers();
					boolean ismoved = standGame.getBombermanGame().movePlayer(localPlayerList.get(0), -1,0);
					if(ismoved)
						System.out.println("player has been moved.....");
					
					Render(rows,cols,player);
				}
				

			}
		});
		
		final Button downbutton = (Button) findViewById(R.id.btnDown);
		downbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (standGame.getBombermanGame().getPlayers().size() == 0) {
					System.out.println("Player list is null. Warning.");
				}else
				{
					List<Player> localPlayerList = standGame.getBombermanGame().getPlayers();
					boolean ismoved = standGame.getBombermanGame().movePlayer(localPlayerList.get(0), 1,0);
					if(ismoved)
						System.out.println("player has been moved.....");
					
					Render(rows,cols,player);
				}
				

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