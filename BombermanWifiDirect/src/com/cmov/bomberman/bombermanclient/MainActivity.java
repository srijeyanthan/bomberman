package com.cmov.bomberman.bombermanclient;

/**
 *  @author Gureya, Bogdan, Sri 
 *  This is class is the starting point of this game , all threads starting 
 *  and user action will be handled here.
 */
/**
 *  @author Gureya, Bogdan, Sri 
 *  This is class is the starting point of this game , all threads starting 
 *  and user action will be handled here.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



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

public class MainActivity extends Activity {
	private DrawView bomberManView;
	private TextView bombermanelapsedTimeTextView;
	private TextView bombermanusernameview;
	private TextView bombermanNoOfPlayersview;
	private TextView bombermanScoreView;
	private static Bitmap player = null;
	private static Bitmap robot = null;
	private static Bitmap bomb = null;
	private static Bitmap explodablewall=null;
	private static Bitmap wall=null;
	private static Bitmap explosion = null;
	private static int scoreOfThePlayer = 0;
	private static int numberofPlayers=0;
	private String gameStatString;
	Button bombButton = null;
	Button pausebutton = null;
	private static RectRender rectrender = null;
	BroadcastReceiver elapsedBroadcastReciver;
	private int bombermanGameDuration = 0;
	private static List<String> messageq = new ArrayList<String>();
	final static Lock lock = new ReentrantLock();
	private static boolean isPlayerDead =false;
    private boolean isServerdead=false;
	public void setServerDeadFlag(boolean isServerDead)
	{
		 this.isServerdead = isServerDead;
		
	}
	public void SetGameStat(String stat)
	{
		this.gameStatString = stat;
		isPlayerDead =true;
	}
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
						if(bombermanGameDuration <10)
						{
							bombermanelapsedTimeTextView.setText("00" + ":0"
									+ bombermanGameDuration);
						}else
						{
						bombermanelapsedTimeTextView.setText("00" + ":"
								+ bombermanGameDuration);
						}

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

	public boolean isQEmpty() {
		lock.lock();
		boolean result = messageq.isEmpty();
		lock.unlock();
		return result;
	}

	public String getMessageFromQ() {
		lock.lock();
		String msg = messageq.get(0);
		messageq.remove(0);
		lock.unlock();
		return msg;
	}

	public void removemsg() {
		lock.lock();
		messageq.remove(0);
		lock.unlock();

	}

	public void addToQ(String smsg) {
		lock.lock();
		messageq.add(smsg);
		lock.unlock();
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
							bombermanNoOfPlayersview.setText(String.valueOf(numberofPlayers));
								if (isPlayerDead) {
								
								new AlertDialog.Builder(MainActivity.this)
								.setTitle("Game over - Game stat")
								.setMessage(gameStatString)
								.setPositiveButton(android.R.string.yes,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int which) {
												
												//once we received the stat we will close the application
												android.os.Process
												.killProcess(android.os.Process
														.myPid());
												/*Intent i = getBaseContext()
														.getPackageManager()
														.getLaunchIntentForPackage(
																getBaseContext()
																		.getPackageName());
												i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												startActivity(i);*/
											}
											
										})
								.setIcon(R.drawable.bomberman).show();
								isPlayerDead = false;
							}
							if(isServerdead)
							{
								finish();
								android.os.Process
								.killProcess(android.os.Process.myPid());
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

	private void InitBomberManMap() {

		bomberManView.invalidate();

	}
	
	public static void setScoreofThePlayer(int totalscore)
	{
		scoreOfThePlayer += totalscore;
	}
	public static void setNumberofPlayer(int numberofPlayer)
	{
		numberofPlayers = numberofPlayer;
	}

	public void Close(String title, String messageboxcontent) {
		new AlertDialog.Builder(MainActivity.this)
				.setTitle(title)
				.setMessage(messageboxcontent)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								
								finish();
									/*android.os.Process
											.killProcess(android.os.Process
													.myPid());*/
							}
						}).setIcon(R.drawable.bomberman).show();
	}

	@Override
	public void onPause() {
		super.onPause(); // Always call the superclass method first
		if (state == GameState.RUN) {
			String pauseMsg = new String(
					new byte[] { BombermanProtocol.GAME_PAUSE_MESSAGE });
			String playerPauseMsg = "<" + BombermanProtocol.MESSAGE_TYPE + "="
					+ pauseMsg + "|" + BombermanProtocol.PLAYER_ID + "="
					+ RspHandler.playerid + ">";
			addToQ(playerPauseMsg);
			this.state = GameState.PAUSE;
			pausebutton.setText("Resume");
		} else {
			String resumeMsg = new String(
					new byte[] { BombermanProtocol.GAME_RESUME_MESSAGE });
			String playerResumeMsg = "<" + BombermanProtocol.MESSAGE_TYPE + "="
					+ resumeMsg + "|" + BombermanProtocol.PLAYER_ID + "="
					+ RspHandler.playerid + ">";
			addToQ(playerResumeMsg);
			this.state = GameState.RUN;
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
		Intent myIntent = getIntent();
		String userName = myIntent.getStringExtra("userName");
		bombermanGameDuration = ClientConfigReader.gameduration /60;
		setContentView(R.layout.activity_main);

		bomberManView =  (DrawView) findViewById(R.id.bckg);
		bombermanelapsedTimeTextView = (TextView) findViewById(R.id.tleft);

		bombermanusernameview = (TextView) findViewById(R.id.uid);
		bombermanNoOfPlayersview = (TextView) findViewById(R.id.no_players);
		bombermanScoreView = (TextView) findViewById(R.id.score);

		bombermanusernameview.setText(userName);
		
		if(bombermanGameDuration <10)
		{
			bombermanelapsedTimeTextView.setText("00" + ":0"
					+ bombermanGameDuration);
		}else
		{
		bombermanelapsedTimeTextView.setText("00" + ":"
				+ bombermanGameDuration);
		}

		rectrender = new RectRender(ClientConfigReader.gridrow,
				ClientConfigReader.gridcolumn);
		player = BitmapFactory
				.decodeResource(getResources(), R.drawable.group2);
		explodablewall = BitmapFactory.decodeResource(getResources(), R.drawable.explodable_wall);
		wall = BitmapFactory.decodeResource(getResources(), R.drawable.solid_wall);
		rectrender.setWall(wall);
		rectrender.setExploadableWall(explodablewall);
		rectrender.setPlayerBitMap(player);
		robot = BitmapFactory.decodeResource(getResources(), R.drawable.robot);
		rectrender.setRobotBitMap(robot);
		bomb = BitmapFactory.decodeResource(getResources(), R.drawable.bomb);
		rectrender.setBombBitMap(bomb);
		explosion = BitmapFactory.decodeResource(getResources(), R.drawable.explosion);
		rectrender.setExplosionBitMap(explosion);
		rectrender.setGameState(state);
		bomberManView.setRenderer(rectrender);

		InitBomberManMap();

		RspHandler.setBombermanview(bomberManView);
		RspHandler.setBombermanScoreview(bombermanScoreView);
		bombermanScoreView.setText(String.valueOf(0));
		startingUp();

		MessageDispatcher md = new MessageDispatcher(this);
		Thread t = new Thread(md);
		t.setDaemon(true);
		t.start();
        BombermanClient.SetActivity(this);
		bombButton = (Button) findViewById(R.id.btnBomb);
		bombButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (state == GameState.RUN) {

					// send bomb placed message to server
					// <1=B|11=1>
					String bombplacementmsg = new String(
							new byte[] { BombermanProtocol.BOMP_PLACEMET_MESSAGE });
					String movemsg = "<" + BombermanProtocol.MESSAGE_TYPE + "="
							+ bombplacementmsg + "|"
							+ BombermanProtocol.PLAYER_ID + "="
							+ RspHandler.playerid + ">";
					// send moved msg to server and wait 0, -1
					addToQ(movemsg);

				}

			}
		});

		final Button leftbutton = (Button) findViewById(R.id.btnLeft);
		leftbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (state == GameState.RUN) {

					String playermovementmsg = new String(
							new byte[] { BombermanProtocol.PLAYER_MOVEMENT_MESSAGE });
					String movemsg = "<" + BombermanProtocol.MESSAGE_TYPE + "="
							+ playermovementmsg + "|"
							+ BombermanProtocol.PLAYER_ID + "="
							+ RspHandler.playerid + "|"
							+ BombermanProtocol.PLAYER_MOVEMENT + "=" + "0.-1"
							+ ">";
					// send moved msg to server and wait 0, -1
					addToQ(movemsg);

				}

			}
		});

		final Button rightbutton = (Button) findViewById(R.id.btnRight);
		rightbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (state == GameState.RUN) {
					String playermovementmsg = new String(
							new byte[] { BombermanProtocol.PLAYER_MOVEMENT_MESSAGE });
					String movemsg = "<" + BombermanProtocol.MESSAGE_TYPE + "="
							+ playermovementmsg + "|"
							+ BombermanProtocol.PLAYER_ID + "="
							+ RspHandler.playerid + "|"
							+ BombermanProtocol.PLAYER_MOVEMENT + "=" + "0.1"
							+ ">";
					addToQ(movemsg);
					// send moved msg to server and wait 0, 1
					// bombermanclient.sendplayermoveright();
				}
			}
		});

		final Button upbutton = (Button) findViewById(R.id.btnUp);
		upbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (state == GameState.RUN) {
					String playermovementmsg = new String(
							new byte[] { BombermanProtocol.PLAYER_MOVEMENT_MESSAGE });
					String movemsg = "<" + BombermanProtocol.MESSAGE_TYPE + "="
							+ playermovementmsg + "|"
							+ BombermanProtocol.PLAYER_ID + "="
							+ RspHandler.playerid + "|"
							+ BombermanProtocol.PLAYER_MOVEMENT + "=" + "-1.0"
							+ ">";
					addToQ(movemsg);
					// send moved msg to server and wait -1,0
					// bombermanclient.sendplayermoveup();
				}

			}
		});

		final Button downbutton = (Button) findViewById(R.id.btnDown);
		downbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (state == GameState.RUN) {
					String playermovementmsg = new String(
							new byte[] { BombermanProtocol.PLAYER_MOVEMENT_MESSAGE });
					String movemsg = "<" + BombermanProtocol.MESSAGE_TYPE + "="
							+ playermovementmsg + "|"
							+ BombermanProtocol.PLAYER_ID + "="
							+ RspHandler.playerid + "|"
							+ BombermanProtocol.PLAYER_MOVEMENT + "=" + "1.0"
							+ ">";
					addToQ(movemsg);
					// send moved msg to server and wait 1,0
					// bombermanclient.sendplayermovedown();
				}
			}
		});

		final Button quitebutton = (Button) findViewById(R.id.btnQuit);
		quitebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (state == GameState.RUN) {
					String quitMsg = new String(
							new byte[] { BombermanProtocol.GAME_QUIT_MESSAGE });
					String playerQuitMsg = "<" + BombermanProtocol.MESSAGE_TYPE
							+ "=" + quitMsg + "|" + BombermanProtocol.PLAYER_ID
							+ "=" + RspHandler.playerid + ">";
					addToQ(playerQuitMsg);
					Close("Closing..",
							"Game is quit now ...");
					// send client leave message to server, so that server won't
					// send any data

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