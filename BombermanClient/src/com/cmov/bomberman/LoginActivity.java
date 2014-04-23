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
	private Button button;
	private String message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		textField = (EditText) findViewById(R.id.editText1);
		button = (Button) findViewById(R.id.button1);
		// Button press event listener
		//button.setOnClickListener(new View.OnClickListener() {
	//		public void onClick(View v) {
	//			message = textField.getText().toString();
	//			textField.setText("");
	//			String hostName = "10.0.2.2";
	//			int portNumber = 4444;
	//			String msgType = "J";
	//			new BombermanClient(hostName, portNumber, msgType).execute(message);
	//		}
	//	});
		
		View.OnClickListener handler = new View.OnClickListener(){
		    public void onClick(View v) {

		        switch (v.getId()) {

		            case R.id.button1: 
		            	message = textField.getText().toString();
						textField.setText("");
						String hostName = "10.0.2.2";
						int portNumber = 4444;
						String msgType = "J";
						new BombermanClient(hostName, portNumber, msgType).execute(message);
						Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
						mainActivity.putExtra("userName", message);
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
