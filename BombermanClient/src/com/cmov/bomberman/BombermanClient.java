package com.cmov.bomberman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;

public class BombermanClient extends AsyncTask<String, Void, Integer> {
	private Socket client;
	private boolean isConnected;
	private PrintWriter printwriter;
	private BufferedReader in;
	
	String hostName = "10.0.2.2";
	int portNumber = 4444;
	String userName;

	@Override
	protected Integer doInBackground(String... strings) {
		// TODO Auto-generated method stub
		// validate input parameters
		if (strings.length <= 0) {
			return 0;
		}
		// connect to the server and send the message
		try {
			client = new Socket(hostName, portNumber);
			printwriter = new PrintWriter(client.getOutputStream(), true);
			in = new BufferedReader(
	                new InputStreamReader(client.getInputStream()));
			userName=strings[0];
			isConnected = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.err.println("Don't know about host " + hostName);
            System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Couldn't get I/O for the connection to " +
	                hostName);
	            System.exit(1);
		}
		
		if(isConnected)
		{
			String msg = constructLoginMsg();
			printwriter.write(msg);
			printwriter.flush();
			printwriter.close();
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0;
	}

	protected void onPostExecute(Long result) {
		return;
	}
	
	protected String constructLoginMsg()
	{
		String loginMsg = '<' + BombermanProtocol.MESSAGE_TYPE + '=' + BombermanProtocol.JOIN_MESSAGE + BombermanProtocol.USER_NAME +
				 '=' + userName + '>';
		return loginMsg;
	}
}