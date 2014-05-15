package com.cmov.bomberman.wifidirect;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.cmov.bomberman.bombermanclient.BombermanProtocol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class parses XML feeds from stackoverflow.com. Given an InputStream
 * representation of a feed, it returns a List of entries, where each list
 * element represents a single entry (post) in the XML feed.
 */

public class ConfigReader {

	final static Lock lock = new ReentrantLock();

	public static class Gameconfig {
		public final int gameduration;
		public final int explosiontimeout;
		public final int explosionduration;
		public final int explosionrange;
		public final int robotspeed;
		public final int pointperrobotkilled;
		public final int pointsperopponentkilled;

		private Gameconfig(int gd, int et, int ed, int er, int rs, int pr,
				int po) {
			this.gameduration = gd;
			this.explosiontimeout = et;
			this.explosionduration = ed;
			this.explosionrange = er;
			this.robotspeed = rs;
			this.pointperrobotkilled = pr;
			this.pointsperopponentkilled = po;

		}
	}

	public static class GameDim {
		public final int maxplayer;
		public final int row;
		public final int column;

		private GameDim(int maxplayer, int row, int column) {
			this.maxplayer = maxplayer;
			this.row = row;
			this.column = column;

		}

	}

	public static class Player {
		private  int x;
		private  int y;

		public Player(int xcor , int ycor )
		{
			x = xcor ;
			y= ycor;
		}

		public int getXCor() {
			return x;
		}

		public int getYCor() {
			return y;
		}
	}


	public static List<Integer> playeridlist = new ArrayList<Integer>();

	public static List<Integer> deadplayerlist = new ArrayList<Integer>();
    public static boolean isServiceInterubted=false;
    private static final String FILENAME = "cmove_bomberman.txt";
    private static String readFromFile() {
        
        String ret = "";
         
        try {
        	if(mContext != null)
        	{
	 		InputStream inputStream = mContext.openFileInput(FILENAME);

				if (inputStream != null) {
					InputStreamReader inputStreamReader = new InputStreamReader(
							inputStream);
					BufferedReader bufferedReader = new BufferedReader(
							inputStreamReader);
					String receiveString = "";
					StringBuilder stringBuilder = new StringBuilder();

					while ((receiveString = bufferedReader.readLine()) != null) {
						stringBuilder.append(receiveString);
					}

					inputStream.close();
					ret = stringBuilder.toString();
				}
			}
        }
        catch (FileNotFoundException e) {
            Log.e("Config", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Config", "Can not read file: " + e.toString());
        }
 
        return ret;
    }
    private static void writeToFile(String data) {
        try {
        	if(mContext!=null)
 {
				File dir = mContext.getFilesDir();
				File file = new File(dir, FILENAME);
				boolean deleted = file.getCanonicalFile().delete();
				System.out
						.println("[Client] File is deletation is sucessful - deleted before write - "
								+ deleted);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
						mContext.openFileOutput(FILENAME, Context.MODE_PRIVATE));
				outputStreamWriter.write(data);
				outputStreamWriter.close();
			}
        	else
        	{
        		System.out.println("mcontect is null...........................");
        	}
        }
        catch (IOException e) {
            Log.e("Config", "File write failed: " + e.toString());
        } 
         
    }
    public static void copyNewGridLayout(Byte[][] serverlayout, int gamedurarion)
    {
    	/*gridlayout = new Byte[13][19];
    	for(int i =0 ; i <13; ++i)
    	{
    		for(int j=0; j <19 ;++j)
    		{
    			gridlayout[i][j]='-';
    		}
    	}*/
    	int playercounter=0;
    	String gridstring ="";
    	for (int i = 0; i < 13; ++i) {
			byte[] element = new byte[serverlayout[i].length];
			for (int j = 0; j < 19; ++j) {
				element[j] = serverlayout[i][j];

			}
			String strfrombyte = "";
			try {
				strfrombyte = new String(element, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			gridstring += strfrombyte;
		}
    	gridstring += "|"+gamedurarion;
    	writeToFile(gridstring);
    	    	
    }
	
    public static void  startServerFromFile()
     {
		gridlayout = new Byte[13][19];
		String filegriddata = readFromFile();
		String [] getGamedurationString= filegriddata.split("\\|");
		System.out.println("This is the gameduration from the file - "+getGamedurationString[1]);
		gameduationafterserverfailed= Integer.parseInt(getGamedurationString[1]);
		String newfilegriddataafterremovalofgameduration = getGamedurationString[0];
		int rowoffset = 0;
		int columnoffset = 0;
		int playercounter = 0;
		for (int i = 0; i < newfilegriddataafterremovalofgameduration.length(); ++i) {

			gridlayout[rowoffset][columnoffset] = newfilegriddataafterremovalofgameduration.getBytes()[i];
		
			if (gridlayout[rowoffset][columnoffset] == '1') {
				playermap.put(playercounter,
						new Player(rowoffset, columnoffset));
				gridlayout[rowoffset][columnoffset] = '-';
				++totalnoplayer;
				++playercounter;
			}
			if (gridlayout[rowoffset][columnoffset] == 'r')
				++totalrobotcount;

			
			++columnoffset;
			if (columnoffset == 19) {
				++rowoffset;
				columnoffset = 0;
			}

		}
		isServiceInterubted =true;
	}
	public static Context mContext;
	static InputStream stream = null;
	private static int width = 0;
	private static int height = 0;
	public static Byte[][] gridlayout = null;
	public static Byte[][] clientgridlayout = null;
	private static Gameconfig gameconfig = null;
	private static GameDim gameDim = null;

	@SuppressLint("UseSparseArrays")
	public static Map<Integer, Player> playermap = new HashMap<Integer, Player>();
	public static Player players = null;
	public static Player players2 = null;
	public static Player players3 = null;
	
	private static Logger logger = new Logger();
	static AssetManager am;
	public static int totalnoplayer=0;
	public static int totalrobotcount=0;
	public static int gameduationafterserverfailed=0;

private static boolean mapdataready = false;
	
	
	private static void setmapdataready(boolean readyornot)
	{
		mapdataready = readyornot;
	}
	
	public static boolean isMapDataReady()
	{
		return mapdataready;
	}
	public static Logger getLogger() {
		return logger;
	}


	public static void InitConfigParser(int gamelevel)
			throws XmlPullParserException {
		
		File dir = mContext.getFilesDir();
		File file = new File(dir, FILENAME);
		try {
			boolean fileexist = file.getCanonicalFile().exists();
			System.out.println("Checking the file exist or not  " +fileexist);
			if(fileexist)
			{
				isServiceInterubted=true;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		am = mContext.getAssets();
		String configFilename = "config_"+gamelevel+".xml";
		try {
			stream = am.open(configFilename);
			InitReaders(stream, gamelevel);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void InitReaders(InputStream in, int gamelevel)
			throws XmlPullParserException, IOException {

		ReadGameDim(in);
		ReadGridLayout(gamelevel);
		ReadGameConfig(gamelevel);

	}

	public static Byte[][] getGridLayout() {

		return gridlayout;

	}

	public static void LockTheGrid() {
		lock.lock();
	}

	public static void UnlockTheGrid() {
		lock.unlock();
	}

	public static Gameconfig getGameConfig() {
		return gameconfig;
	}

	public static GameDim getGameDim() {
		return gameDim;
	}

	public static void UpdateGridLayOutCell(int x, int y, Byte type) {
		lock.lock();
		try {
			gridlayout[x][y] = type;
		} finally {
			lock.unlock();
		}

	}

	public static void AcquireLock() {
		lock.lock();
	}

	public static void ReleaseLock() {
		lock.unlock();
	}
	
	public static void InitializeTheGridFromServer(int row , int column , byte[] servermapbytearray)
	{
		clientgridlayout  = new Byte[row][column];
		int rowoffset =0;
		int columnoffset=0;
		System.out.println("Map message received from server - "+new String(servermapbytearray));
		for ( int i =0; i < servermapbytearray.length ; ++i)
		{
			
			clientgridlayout[rowoffset][columnoffset] = servermapbytearray[i];
			if (clientgridlayout[rowoffset][columnoffset] == '1') {
				players = new Player(rowoffset,columnoffset);
			}
			++columnoffset;
			if(columnoffset==column)
			{
				++rowoffset;
				columnoffset=0;
			}
			
		}
		
		setmapdataready(true);
	}
	public static void ReadGridLayout(int gamelevel) throws XmlPullParserException,
			IOException {
		if(!isServiceInterubted)
 {
			XmlPullParser parser = Xml.newPullParser();
			int event = parser.getEventType();
			String configFilename = "config_" + gamelevel + ".xml";
			InputStream in = am.open(configFilename);
			parser.setInput(in, null);
			String sout = null;
			String text = null;
			int localycounter = 0;

			Byte[][] grid = new Byte[height][width];
			while (event != XmlPullParser.END_DOCUMENT) {
				String name = parser.getName();
				switch (event) {
				case XmlPullParser.START_TAG:
					break;
				case XmlPullParser.TEXT:
					text = parser.getText();
					break;
				case XmlPullParser.END_TAG: {
					int localxcounter = 0;
					if (name.equals("row")) {
						sout = text;
						String[] elements = sout.split(" ");

						for (String mapelement : elements) {

							if (mapelement.getBytes()[0] == '1') {
								playermap.put(0, new Player(localycounter,
										localxcounter));
								grid[localycounter][localxcounter] = '-';

							} else if (mapelement.getBytes()[0] == '2') {
								playermap.put(1, new Player(localycounter,
										localxcounter));
								grid[localycounter][localxcounter] = '-';
							} else if (mapelement.getBytes()[0] == '3') {
								playermap.put(2, new Player(localycounter,
										localxcounter));
								grid[localycounter][localxcounter] = '-';
							} else {
								grid[localycounter][localxcounter] = mapelement
										.getBytes()[0];
								if (mapelement.getBytes()[0] == 'r') {
									++totalrobotcount;
								}
							}
							++localxcounter;
						}
						++localycounter;
					}
				}
					break;

				}
				event = parser.next();
			}
			gridlayout = grid;
			totalnoplayer = playermap.size();
		}
		else
		{
			System.out.println("Starting server from local file ..");
			startServerFromFile();
			
		}
	}

	public static void ReadGameDim(InputStream in)
			throws XmlPullParserException, IOException {
		XmlPullParser parser = Xml.newPullParser();
		int event = parser.getEventType();

		parser.setInput(in, null);

		int maxplayer = 0;
		int row = 0;
		int column = 0;

		while (event != XmlPullParser.END_DOCUMENT) {
			String name = parser.getName();
			switch (event) {
			case XmlPullParser.START_TAG:
				break;
			case XmlPullParser.END_TAG: {
				if (name.equals("maxplayers")) {
					maxplayer = Integer.parseInt(parser.getAttributeValue(null,
							"value"));
				} else if (name.equals("height")) {
					row = Integer.parseInt(parser.getAttributeValue(null,
							"value"));
				} else if (name.equals("width")) {
					column = Integer.parseInt(parser.getAttributeValue(null,
							"value"));
				}
			}
				break;

			}
			event = parser.next();
		}
		width = column;
		height = row;
		gameDim = new GameDim(maxplayer, row, column);
	}

	public static void ReadGameConfig(int gamelevel) throws XmlPullParserException,
			IOException {
		int gd = 0;
		int et = 0;
		int ed = 0;
		int er = 0;
		int rs = 0;
		int pr = 0;
		int po = 0;
		String configFilename = "config_"+gamelevel+".xml";
		InputStream in = am.open(configFilename);
		XmlPullParser parser = Xml.newPullParser();
		int event = parser.getEventType();
		parser.setInput(in, null);
		while (event != XmlPullParser.END_DOCUMENT) {
			String name = parser.getName();
			switch (event) {
			case XmlPullParser.START_TAG:
				break;
			case XmlPullParser.END_TAG: {
				if (name.equals("gd")) {
					gd = Integer.parseInt(parser.getAttributeValue(null,
							"value"));
				} else if (name.equals("et")) {
					et = Integer.parseInt(parser.getAttributeValue(null,
							"value"));
				} else if (name.equals("ed")) {
					ed = Integer.parseInt(parser.getAttributeValue(null,
							"value"));
				} else if (name.equals("er")) {
					er = Integer.parseInt(parser.getAttributeValue(null,
							"value"));
				} else if (name.equals("rs")) {
					rs = Integer.parseInt(parser.getAttributeValue(null,
							"value"));
				} else if (name.equals("pr")) {
					pr = Integer.parseInt(parser.getAttributeValue(null,
							"value"));
				} else if (name.equals("po")) {
					po = Integer.parseInt(parser.getAttributeValue(null,
							"value"));
				}
			}
				break;

			}
			event = parser.next();
		}
		if(isServiceInterubted)
		{
			gd = gameduationafterserverfailed*60;
		}
		gameconfig = new Gameconfig(gd, et, ed, er, rs, pr, po);

	}

	
}