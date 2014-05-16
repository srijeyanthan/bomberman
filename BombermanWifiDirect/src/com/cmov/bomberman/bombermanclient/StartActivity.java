package com.cmov.bomberman.bombermanclient;

import com.cmov.bomberman.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class StartActivity extends Activity implements OnItemSelectedListener {
	private EditText textField;
	@SuppressWarnings("unused")
	private Button button;
	private String playername;
	private String level;
	private Spinner spinner;
	private static final String[] levels = { "Game Level 1", "Game Level 2", "Game Level 3" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		textField = (EditText) findViewById(R.id.username);
		button = (Button) findViewById(R.id.wifiDirect);

		spinner = (Spinner) findViewById(R.id.level);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				StartActivity.this, android.R.layout.simple_spinner_item,
				levels);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);

		View.OnClickListener handler = new View.OnClickListener() {
			public void onClick(View v) {

				switch (v.getId()) {

				case R.id.wifiDirect:
					playername = textField.getText().toString();
					textField.setText("");
					Intent wdActivity = new Intent(StartActivity.this,
							WiFiDirectActivity.class);
					wdActivity.putExtra("userName", playername);
					wdActivity.putExtra("level", level);
					startActivity(wdActivity);

					break;
				case R.id.standAlone:
					playername = textField.getText().toString();
					Intent standAloneActivity = new Intent(StartActivity.this,
							com.cmov.bomberman.standAlone.MainActivity.class);
					standAloneActivity.putExtra("userName", playername);
					standAloneActivity.putExtra("level", level);
					startActivity(standAloneActivity);
					break;
				}
			}
		};
		findViewById(R.id.wifiDirect).setOnClickListener(handler);
		findViewById(R.id.standAlone).setOnClickListener(handler);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View v, int position,
			long id) {
		// TODO Auto-generated method stub
		switch (position) {
		case 0:
			// What ever you want to happen when item 1 selected
			level = "1";
			break;
		case 1:
			// What ever you want to happen when item 2 selected
			level = "2";
			break;
		case 2:
			// What ever you want to happen when item 3 selected
			level = "3";
			break;

		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
