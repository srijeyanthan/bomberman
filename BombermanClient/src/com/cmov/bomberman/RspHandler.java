package com.cmov.bomberman;

import android.app.Activity;

public class RspHandler {
	private byte[] rsp = null;
	private static IMoveableRobot robotActiviy;
	private static DrawView bomberManView1;
	public static void setMainActivity(Activity activity)
	{
		robotActiviy = (IMoveableRobot) activity;
	}
	public synchronized boolean handleResponse(byte[] rsp) {
		this.rsp = rsp;
		this.notify();
		return true;
	}
	
	public static void setBombermanview(DrawView bomberManView)
	{
		bomberManView1 = bomberManView;
	}

	public void updateRobotmovement(String smsg, boolean isnew)
	{
		/// Extensive breaking ...
		//5=(1,7).(3,10).(5,13).(9,10)
		String[] newrobtpossplitted = smsg.split("="); //(1,7).(3,10).(5,13).(9,10)
		String[] corsplitted = newrobtpossplitted[1].split("\\.");  // (1,7) (3,10) 5,13) (9,10)
	
		for(int i =0; i < corsplitted.length ; ++i)
		{
		     String[] furthercorsplitted = corsplitted[i].split(",");
		     int x  = Integer.parseInt(furthercorsplitted[0]);
		     int y  = Integer.parseInt(furthercorsplitted[1]);
		     if(isnew)
		     ConfigReader.UpdateGridLayOutCell(x, y,(byte) 'r');
		     else
		      ConfigReader.UpdateGridLayOutCell(x, y,(byte) '-');
		    	 
		}
	}
	public synchronized void waitForResponse() {
		while (this.rsp == null) {
			try {
				this.wait();
			} catch (InterruptedException e) {
			}
		}
		if (this.rsp != null) {
			System.out
					.println("Whaing for resposne  - " + new String(this.rsp));
			String response = new String(this.rsp);
			String[] splitresponse = response.split("\\|");

			// get the message type
			String messagetype = splitresponse[0];
			String[] messagetypesplitted = messagetype.split("=");
			// this is initial map message
			
		
			if ((byte)Integer.parseInt(messagetypesplitted[1]) == BombermanServerDef.GRID_MESSAGE) {
				String map = splitresponse[3];
				String[] mapsplitted = map.split("=");
				ConfigReader.InitializeTheGridFromServer(13, 19, mapsplitted[1].getBytes());
			}
			if ((byte)Integer.parseInt(messagetypesplitted[1]) == BombermanServerDef.ROBOT_PLACEMET_MESSAGE) {
				String newrobotpos = splitresponse[1];
				String originalpos = splitresponse[2];
				
				updateRobotmovement(newrobotpos,true);
				updateRobotmovement(originalpos,false);		
				
				bomberManView1.postInvalidate();
				//robotActiviy.RobotMovedAtLogicalLayer();
				
			}

			this.rsp = null;
		}
	}
}