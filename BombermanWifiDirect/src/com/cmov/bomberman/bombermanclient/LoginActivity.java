package com.cmov.bomberman.bombermanclient;


import java.io.IOException;

import com.cmov.bomberman.bombermanclient.MainActivity.GameState;

import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
	private EditText textField;
	@SuppressWarnings("unused")
	private Button button;
	private String playername;
	public Intent mainActivity;
	public static  WifiP2pManager manager;
	public static  Channel channel;
	Runnable runnable = new Runnable() {
		public void run() {

			try {
				BombermanClient.startBombermanClient();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			long endTime = System.currentTimeMillis() + 20 * 1000;

			while (System.currentTimeMillis() < endTime) {
				synchronized (this) {
					try {
						wait(endTime - System.currentTimeMillis());
					} catch (Exception e) {
					}
				}
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		textField = (EditText) findViewById(R.id.editText1);
		button = (Button) findViewById(R.id.button1);
		
		View.OnClickListener handler = new View.OnClickListener(){
		    public void onClick(View v) {

		        switch (v.getId()) {

		            case R.id.button1: 
		            	playername = textField.getText().toString();
						textField.setText("");
						BombermanClient.setPlayerName(playername);
						BombermanClient.SetLoginActivity(LoginActivity.this);
						
						Thread bombermanclientThread = new Thread(runnable);
						bombermanclientThread.start();
						
						while(!ClientConfigReader.isMapDataReady())
						{
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
						mainActivity.putExtra("userName", playername);
		                startActivity(mainActivity);
		                
		                break;
		        }
		    }
		};
		findViewById(R.id.button1).setOnClickListener(handler);
		
		
		final Button disconnect = (Button) findViewById(R.id.Button01);
		disconnect.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				//finish();
			
				// send owner dead message 
				if (manager != null) {
					manager.removeGroup(channel, new ActionListener() {

						@Override
						public void onFailure(int reasonCode) {
							System.out.println("Disconnect failed. Reason :"
									+ reasonCode);

						}

						@Override
						public void onSuccess() {
				
							finish();
							android.os.Process.killProcess(android.os.Process
									.myPid());
							// fragment.getView().setVisibility(View.GONE);
						}

					});
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
