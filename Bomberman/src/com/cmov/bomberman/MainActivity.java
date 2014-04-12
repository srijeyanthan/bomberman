package com.cmov.bomberman;

import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements IExplodable,
		IMoveableRobot, IUpdatableScore {
	private DrawView bomberManView;
	private TextView bombermanelapsedTimeTextView;
	private TextView bombermanusernameview;
	private TextView bombermanNoOfPlayersview;
	private TextView bombermanScoreView;
	final StandaloneGame standAloneGame = new StandaloneGame();
	private static Bitmap player = null;
	private LogicalWorld logicalworld = null;
	private static int scoreOfThePlayer = 0;
	private boolean isBombPlaced = false;
	private boolean isPlayerDead =false;
	Button bombButton = null;

	BroadcastReceiver elapsedBroadcastReciver;
	private int bombermanGameDuration = 0;

	/*
	 * note , this is tick timer , every minute it will reduced one minute from
	 * original game duration, actually we dont need per second based timer ,
	 * this would be sort of inefficient
	 */
	@Override
	public void onStart() {
		super.onStart();
		elapsedBroadcastReciver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context ctx, Intent intent) {
				if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
					if (bombermanGameDuration >= 0) {
						--bombermanGameDuration;
						bombermanelapsedTimeTextView.setText("00" + ":"
								+ bombermanGameDuration);

					}
				}
			}
		};

		registerReceiver(elapsedBroadcastReciver, new IntentFilter(
				Intent.ACTION_TIME_TICK));
	}

	@Override
	public void onStop() {
		super.onStop();
		if (elapsedBroadcastReciver != null)
			unregisterReceiver(elapsedBroadcastReciver);
	}

	private void startingUp() {
		Thread timer = new Thread() { // new thread
			public void run() {
				Boolean b = true;

				do {
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							bombermanScoreView.setText(String
									.valueOf(scoreOfThePlayer));
							if(!isBombPlaced)
							{
								bombButton.setText("Bomb");
							}
							if(isPlayerDead)
							{
								new AlertDialog.Builder(MainActivity.this)
							    .setTitle("Game Over - you are dead")
							    .setMessage("Are you sure you want to restart the game ?")
							    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
							        public void onClick(DialogInterface dialog, int which) {
							        	Intent i = getBaseContext().getPackageManager()
							                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
							       i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							       startActivity(i);
							        }
							     })
							    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
							        public void onClick(DialogInterface dialog, int which) { 
							        	android.os.Process.killProcess(android.os.Process.myPid());
							        }
							     })
							    .setIcon(R.drawable.alert)
							     .show();
								isPlayerDead =false;
							}
						}
					});
				} while (b == true);

			};
		};
		timer.start();
	}

	public void Render() {
		RectRender rectrender = new RectRender(ConfigReader.getGameDim().row,
				ConfigReader.getGameDim().column);
		player = BitmapFactory.decodeResource(getResources(), R.drawable.sri);
		rectrender.setPlayerBitMap(player);
		bomberManView.setRenderer(rectrender);
		bomberManView.invalidate();

	}

	public void RobotMovedAtLogicalLayer() {
		RectRender rectrender = new RectRender(ConfigReader.getGameDim().row,
				ConfigReader.getGameDim().column);
		player = BitmapFactory.decodeResource(getResources(), R.drawable.sri);
		rectrender.setPlayerBitMap(player);
		bomberManView.setRenderer(rectrender);
		bomberManView.postInvalidate();
	}

	public void Exploaded(boolean isPlayerDeadinGame) {
		RectRender rectrender = new RectRender(ConfigReader.getGameDim().row,
				ConfigReader.getGameDim().column);
		player = BitmapFactory.decodeResource(getResources(), R.drawable.sri);
		rectrender.setPlayerBitMap(player);
		bomberManView.setRenderer(rectrender);
		bomberManView.postInvalidate();
		isBombPlaced = false;
		isPlayerDead = isPlayerDeadinGame;   /// this is only for test , 
		

	}

	public void UpdateScore(int numberOfRobotDied) {
		scoreOfThePlayer += ConfigReader.getGameConfig().pointperrobotkilled
				* numberOfRobotDied;
	}

	private void InitBomberManMap() {
		player = BitmapFactory.decodeResource(getResources(), R.drawable.sri);
		RectRender rectrender = new RectRender(ConfigReader.getGameDim().row,
				ConfigReader.getGameDim().column);
		rectrender.setPlayerBitMap(player);
		bomberManView.setRenderer(rectrender);
		bomberManView.invalidate();

	}

	private void InitStartRobotThred(Activity activity) {
		RobotThread robotThread = new RobotThread(
				ConfigReader.getGameDim().row,
				ConfigReader.getGameDim().column, activity);
		robotThread.setLogicalWord(logicalworld);
		robotThread.setRunning(true);
		robotThread.start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Context mContext = MainActivity.this;
		try {
			ConfigReader.InitConfigParser(mContext);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		bombermanGameDuration = ConfigReader.getGameConfig().gameduration;
		setContentView(R.layout.activity_main);

		standAloneGame.joinGame("Cmove", MainActivity.this);
		logicalworld = standAloneGame.getBombermanGame().getLogicalWorld();

		bomberManView = (com.cmov.bomberman.DrawView) findViewById(R.id.bckg);
		bombermanelapsedTimeTextView = (TextView) findViewById(R.id.tleft);

		bombermanusernameview = (TextView) findViewById(R.id.uid);
		bombermanNoOfPlayersview = (TextView) findViewById(R.id.no_players);
		bombermanScoreView = (TextView) findViewById(R.id.score);

		bombermanusernameview.setText("Group2");
		bombermanNoOfPlayersview.setText("3");
		bombermanelapsedTimeTextView
				.setText("00" + ":" + bombermanGameDuration);
		InitBomberManMap();

	
		InitStartRobotThred(MainActivity.this);
		startingUp();

		bombButton = (Button) findViewById(R.id.btnBomb);
		bombButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (standAloneGame.getBombermanGame().getPlayers().size() == 0) {
					ConfigReader.getLogger().log(
							"Player list is null. Warning.");
				} else {
					List<Player> localPlayerList = standAloneGame
							.getBombermanGame().getPlayers();
					localPlayerList.get(0).placeBomb();
					if (!isBombPlaced)
						bombButton.setText("Bombed");
					isBombPlaced=true;
					Render();

				}

			}
		});

		final Button leftbutton = (Button) findViewById(R.id.btnLeft);
		leftbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (standAloneGame.getBombermanGame().getPlayers().size() == 0) {
					ConfigReader.getLogger().log(
							"Player list is null. Warning.");
				} else {
					List<Player> localPlayerList = standAloneGame
							.getBombermanGame().getPlayers();
					boolean ismoved = standAloneGame.getBombermanGame()
							.movePlayer(localPlayerList.get(0), 0, -1);
					if (ismoved)
						ConfigReader.getLogger().log(
								"plccayer has been moved.....");

					Render();
				}

			}
		});

		final Button rightbutton = (Button) findViewById(R.id.btnRight);
		rightbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (standAloneGame.getBombermanGame().getPlayers().size() == 0) {
					ConfigReader.getLogger().log(
							"Player list is null. Warning.");
				} else {
					List<Player> localPlayerList = standAloneGame
							.getBombermanGame().getPlayers();
					boolean ismoved = standAloneGame.getBombermanGame()
							.movePlayer(localPlayerList.get(0), 0, 1);
					if (ismoved)
						ConfigReader.getLogger().log(
								"player has been moved.....");

					Render();
				}

			}
		});

		final Button upbutton = (Button) findViewById(R.id.btnUp);
		upbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (standAloneGame.getBombermanGame().getPlayers().size() == 0) {
					ConfigReader.getLogger().log(
							"Player list is null. Warning.");
				} else {
					List<Player> localPlayerList = standAloneGame
							.getBombermanGame().getPlayers();
					boolean ismoved = standAloneGame.getBombermanGame()
							.movePlayer(localPlayerList.get(0), -1, 0);
					if (ismoved)
						ConfigReader.getLogger().log(
								"player has been moved.....");

					Render();
				}

			}
		});

		final Button downbutton = (Button) findViewById(R.id.btnDown);
		downbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (standAloneGame.getBombermanGame().getPlayers().size() == 0) {
					ConfigReader.getLogger().log(
							"Player list is null. Warning.");
				} else {
					List<Player> localPlayerList = standAloneGame
							.getBombermanGame().getPlayers();
					boolean ismoved = standAloneGame.getBombermanGame()
							.movePlayer(localPlayerList.get(0), 1, 0);
					if (ismoved)
						ConfigReader.getLogger().log(
								"player has been moved.....");

					Render();
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