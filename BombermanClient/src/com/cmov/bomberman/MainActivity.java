package com.cmov.bomberman;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private DrawView bomberManView;
	private TextView bombermanelapsedTimeTextView;
	private TextView bombermanusernameview;
	private TextView bombermanNoOfPlayersview;
	private TextView bombermanScoreView;
	private static Bitmap player = null;
	private static Bitmap robot = null;
	private static Bitmap bomb = null;
	private static int scoreOfThePlayer = 0;
	private static int numberofRobotkilled = 0;
	private boolean isBombPlaced = false;
	private boolean isPlayerDead = false;
	Button bombButton = null;
	Button pausebutton = null;
	private static RectRender rectrender = null;
	BroadcastReceiver elapsedBroadcastReciver;
	private int bombermanGameDuration = 0;
	final static Lock lock = new ReentrantLock();
	private static List<String> messageq = new ArrayList<String>();
	private Handler mMainHandler, mChildHandler;
	public enum GameState {
		PAUSE, RUN, RESUME
	}

	private GameState state = GameState.RUN;

	@Override
	public void onStop() {
		super.onStop();
		if (elapsedBroadcastReciver != null)
			unregisterReceiver(elapsedBroadcastReciver);
	}

	public List<String> getMsgQ()
	{
		return messageq;
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

								String message = "No of Robots killed - "
										+ Integer.toString(numberofRobotkilled)
										+ "\n" + "Total Score - "
										+ Integer.toString(scoreOfThePlayer);
								new AlertDialog.Builder(MainActivity.this)
										.setTitle("Game over - Game stat")
										.setMessage(message)
										.setPositiveButton(
												android.R.string.yes,
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
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
		isPlayerDead = isPlayerDeadinGame; // / this is only for test ,

	}

	public void UpdateScore(int numberOfRobotDied) {
		scoreOfThePlayer += ConfigReader.getGameConfig().pointperrobotkilled
				* numberOfRobotDied;
		numberofRobotkilled += numberOfRobotDied;
	}

	private void InitBomberManMap() {

		bomberManView.invalidate();

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
			pausebutton.setText("Resume");
		} else {
			this.state = GameState.RUN;
			pausebutton.setText("Pause");
		}

	}
	

	@Override
	public void onResume() {
		super.onResume(); // Always call the superclass method first

		// get the resource

	}
	public boolean isQEmpty()
	{
		lock.lock();
		boolean result = messageq.isEmpty();
		lock.unlock();
		return result;
	}
	public String getMessageFromQ()
	{
		lock.lock();
		String msg =  messageq.get(0);
		messageq.remove(0);
		lock.unlock();
		return msg;
	}
	public void removemsg()
	{
		lock.lock();
		messageq.remove(0);
		lock.unlock();
		
	}
	public void addToQ(String msg) {
		lock.lock();
		 messageq.add(msg);
		lock.unlock();
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
		Intent myIntent = getIntent();
		String userName = myIntent.getStringExtra("userName");
		//bombermanGameDuration = ConfigReader.getGameConfig().gameduration;
		setContentView(R.layout.activity_main);

		

		bomberManView = (com.cmov.bomberman.DrawView) findViewById(R.id.bckg);
		bombermanelapsedTimeTextView = (TextView) findViewById(R.id.tleft);

		bombermanusernameview = (TextView) findViewById(R.id.uid);
		bombermanNoOfPlayersview = (TextView) findViewById(R.id.no_players);
		bombermanScoreView = (TextView) findViewById(R.id.score);

		bombermanusernameview.setText(userName);
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
		rectrender.setBombBitMap(bomb);
		rectrender.setGameState(state);
		bomberManView.setRenderer(rectrender);

		InitBomberManMap();

		RspHandler.setBombermanview(bomberManView);
		startingUp();
		
		/*MessageDispatcher md = new MessageDispatcher(this);
		 Thread t = new Thread(md);
		 t.setDaemon(true);
		 t.start();*/

		
		bombButton = (Button) findViewById(R.id.btnBomb);
		bombButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (state == GameState.RUN) {
					
					// send bomb placed message to server
						if (!isBombPlaced)
							bombButton.setText("Bombed");
						isBombPlaced = true;
						

					
				}

			}
		});

		final Button leftbutton = (Button) findViewById(R.id.btnLeft);
		leftbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (state == GameState.RUN) {
					
				    // send moved msg to server and wait  0, -1
					String movemsg ="<"+BombermanProtocol.MESSAGE_TYPE+"="+BombermanProtocol.PLAYER_MOVEMENT_MESSAGE+"|"+BombermanProtocol.PLAYER_ID+"="+RspHandler.playerid+"|"+BombermanProtocol.PLAYER_MOVEMENT+"="+"0.-1"+">";
					addToQ(movemsg);
					
					/*if (mChildHandler != null) {
						
						 
						 * Send a message to the child thread.
						 
						Message msg = mChildHandler.obtainMessage();
						msg.obj = mMainHandler.getLooper().getThread().getName() + " says Hello";
						mChildHandler.sendMessage(msg);
						Log.i("ThreadMessaging", "Send a message to the child thread - " + (String)msg.obj);
					}*/
					}
				
			}
		});

		final Button rightbutton = (Button) findViewById(R.id.btnRight);
		rightbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (state == GameState.RUN) {
					String movemsg ="<"+BombermanProtocol.MESSAGE_TYPE+"="+BombermanProtocol.PLAYER_MOVEMENT_MESSAGE+"|"+BombermanProtocol.PLAYER_ID+"="+RspHandler.playerid+"|"+BombermanProtocol.PLAYER_MOVEMENT+"="+"0.1"+">";
					addToQ(movemsg);
					  // send moved msg to server and wait  0, 1
					//bombermanclient.sendplayermoveright();
				}
			}
		});

		final Button upbutton = (Button) findViewById(R.id.btnUp);
		upbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (state == GameState.RUN) {
					String movemsg ="<"+BombermanProtocol.MESSAGE_TYPE+"="+BombermanProtocol.PLAYER_MOVEMENT_MESSAGE+"|"+BombermanProtocol.PLAYER_ID+"="+RspHandler.playerid+"|"+BombermanProtocol.PLAYER_MOVEMENT+"="+"-1.0"+">";
					addToQ(movemsg);
					  // send moved msg to server and wait  -1,0
					//bombermanclient.sendplayermoveup();
					}
				
			}
		});

		final Button downbutton = (Button) findViewById(R.id.btnDown);
		downbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (state == GameState.RUN) {
					String movemsg ="<"+BombermanProtocol.MESSAGE_TYPE+"="+BombermanProtocol.PLAYER_MOVEMENT_MESSAGE+"|"+BombermanProtocol.PLAYER_ID+"="+RspHandler.playerid+"|"+BombermanProtocol.PLAYER_MOVEMENT+"="+"1.0"+">";
					addToQ(movemsg);
					 // send moved msg to server and wait  1,0
					//bombermanclient.sendplayermovedown();
				}
			}
		});

		final Button quitebutton = (Button) findViewById(R.id.btnQuit);
		quitebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (state == GameState.RUN) {
					Close("Closing..",
							"Are you sure you want to quite the game ?", false);
					//send client leave message to server, so that server won't send any data
					
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