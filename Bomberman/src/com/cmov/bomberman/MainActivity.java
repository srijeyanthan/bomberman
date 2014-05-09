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
	private static Bitmap robot = null;
	private static Bitmap bomb = null;
	private static Bitmap explosion = null;
	private LogicalWorld logicalworld = null;
	private static int scoreOfThePlayer = 0;
	private static int numberofRobotkilled =0;
	private boolean isBombPlaced = false;
	private boolean isPlayerDead = false;
	Button bombButton = null;
	Button pausebutton = null;
	private static RectRender rectrender = null;
	private RobotThread robotThread = null;

	BroadcastReceiver elapsedBroadcastReciver;
	private int bombermanGameDuration = 0;

	public enum GameState {
		PAUSE, RUN, RESUME
	}

	private GameState state = GameState.RUN;

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
							if (!isBombPlaced) {
								bombButton.setText("Bomb");
							}
							if (isPlayerDead) {
								
								String message = "No of Robots killed - "+Integer.toString(numberofRobotkilled)+"\n"+"Total Score - "+Integer.toString(scoreOfThePlayer);
								new AlertDialog.Builder(MainActivity.this)
								.setTitle("Game over - Game stat")
								.setMessage(message)
								.setPositiveButton(android.R.string.yes,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int which) {
												
												Intent i = getBaseContext()
														.getPackageManager()
														.getLaunchIntentForPackage(
																getBaseContext()
																		.getPackageName());
												i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												startActivity(i);
											}
											
										})
								.setIcon(R.drawable.bomberman).show();
								isPlayerDead = false;
							}
						}
					});
				} while (b == true);

			};
		};
		timer.start();
	}

	public void Render() {

		bomberManView.invalidate();

	}

	public void RobotMovedAtLogicalLayer() {
		bomberManView.postInvalidate();
	}

	public void Exploaded(boolean isPlayerDeadinGame) {
		bomberManView.postInvalidate();
		isBombPlaced = false;
		if(isPlayerDeadinGame)
			standAloneGame.getBombermanGame().clearPlayerList();
		isPlayerDead = isPlayerDeadinGame; // / this is only for test ,
		bomberManView.postInvalidate();
	}

	public void UpdateScore(int numberOfRobotDied) {
		scoreOfThePlayer += ConfigReader.getGameConfig().pointperrobotkilled
				* numberOfRobotDied;
		numberofRobotkilled += numberOfRobotDied;
	}

	private void InitBomberManMap() {

		bomberManView.invalidate();

	}

	private void InitStartRobotThred(Activity activity) {
		robotThread = new RobotThread(ConfigReader.getGameDim().row,
				ConfigReader.getGameDim().column, activity);
		robotThread.setLogicalWord(logicalworld);
		robotThread.setRunning(true);
		robotThread.setState(GameState.RUN);
		robotThread.start();
	}
	
	public void quit(String title, String messageboxcontent) {
		new AlertDialog.Builder(MainActivity.this)
				.setTitle(title)
				.setMessage(messageboxcontent)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								
									android.os.Process
											.killProcess(android.os.Process
													.myPid());
							}
						}).setIcon(R.drawable.bomberman).show();
	}

	public void Close(String title, String messageboxcontent,
			final boolean isRestart) {
		new AlertDialog.Builder(MainActivity.this)
				.setTitle(title)
				.setMessage(messageboxcontent)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (isRestart) {
									Intent i = getBaseContext()
											.getPackageManager()
											.getLaunchIntentForPackage(
													getBaseContext()
															.getPackageName());
									i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(i);
								} else
									android.os.Process
											.killProcess(android.os.Process
													.myPid());
							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (isRestart) {
									android.os.Process
											.killProcess(android.os.Process
													.myPid());
								} else {
									Intent i = getBaseContext()
											.getPackageManager()
											.getLaunchIntentForPackage(
													getBaseContext()
															.getPackageName());
									i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(i);
								}

							}
						}).setIcon(R.drawable.bomberman).show();
	}

	@Override
	public void onPause() {
		super.onPause(); // Always call the superclass method first
		if (state == GameState.RUN) {
			this.state = GameState.PAUSE;
			robotThread.setState(GameState.PAUSE);
			pausebutton.setText("Resume");
		}else
		{
			this.state = GameState.RUN;
			robotThread.setState(GameState.RUN);
			pausebutton.setText("Pause");
		}

	}

	@Override
	public void onResume() {
		super.onResume(); // Always call the superclass method first

		// get the resource

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

		rectrender = new RectRender(ConfigReader.getGameDim().row,
				ConfigReader.getGameDim().column);
		player = BitmapFactory
				.decodeResource(getResources(), R.drawable.group2);
		rectrender.setPlayerBitMap(player);
		robot = BitmapFactory.decodeResource(getResources(), R.drawable.robot);
		rectrender.setRobotBitMap(robot);
		bomb = BitmapFactory.decodeResource(getResources(), R.drawable.bomb);
		explosion = BitmapFactory.decodeResource(getResources(), R.drawable.explosion);
		rectrender.setExplosionBitMap(explosion);
		rectrender.setBombBitMap(bomb);
		rectrender.setGameState(state);
		bomberManView.setRenderer(rectrender);

		InitBomberManMap();

		InitStartRobotThred(MainActivity.this);
		startingUp();

		bombButton = (Button) findViewById(R.id.btnBomb);
		bombButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (state == GameState.RUN) {
					if (standAloneGame.getBombermanGame().getPlayers().size() == 0) {
						ConfigReader.getLogger().log(
								"Player list is null. Warning.");
					} else {
						List<Player> localPlayerList = standAloneGame
								.getBombermanGame().getPlayers();
						localPlayerList.get(0).placeBomb();
						if (!isBombPlaced)
							bombButton.setText("Bombed");
						isBombPlaced = true;
						Render();

					}
				}

			}
		});

		final Button leftbutton = (Button) findViewById(R.id.btnLeft);
		leftbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (state == GameState.RUN) {
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
			}
		});

		final Button rightbutton = (Button) findViewById(R.id.btnRight);
		rightbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (state == GameState.RUN) {
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
			}
		});

		final Button upbutton = (Button) findViewById(R.id.btnUp);
		upbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (state == GameState.RUN) {
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
			}
		});

		final Button downbutton = (Button) findViewById(R.id.btnDown);
		downbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (state == GameState.RUN) {
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
			}
		});

		final Button quitebutton = (Button) findViewById(R.id.btnQuit);
		quitebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (state == GameState.RUN) {
					quit("Closing..",
							"Are you sure you want to quite the game ?");
					// finish();
					// android.os.Process.killProcess(android.os.Process.myPid());
					// onDestroy();
				}
			}
		});

		pausebutton = (Button) findViewById(R.id.btnPause);
		pausebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onPause();
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}