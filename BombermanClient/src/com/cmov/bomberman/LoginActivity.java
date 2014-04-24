package com.cmov.bomberman;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
	private EditText textField;
	@SuppressWarnings("unused")
	private Button button;
	private String playername;

	Runnable runnable = new Runnable() {
		public void run() {

			BombermanClient.startBombermanClient();
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
						
						Thread bombermanclientThread = new Thread(runnable);
						bombermanclientThread.start();
						
						while(!ConfigReader.isMapDataReady())
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
