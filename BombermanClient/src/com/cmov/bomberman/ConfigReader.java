package com.cmov.bomberman;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
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
		private static int x;
		private static int y;

		public void setXCor(int xCor) {
			x = xCor;
		}

		public void setYCor(int yCor) {
			y = yCor;
		}

		public int getXCor() {
			return x;
		}

		public int getYCor() {
			return y;
		}
	}

	static Context mContext;
	static InputStream stream = null;
	private static int width = 0;
	private static int height = 0;
	public static Byte[][] gridlayout = null;
	private static Gameconfig gameconfig = null;
	private static GameDim gameDim = null;
	public static int gameduration=0;
	public static Player players = null;
	private static Logger logger = new Logger();
	static AssetManager am;

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

	public static void InitConfigParser(Context context)
			throws XmlPullParserException {
		mContext = context;
		am = mContext.getAssets();
		try {
			stream = am.open("config.xml");
			InitReaders(stream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void InitReaders(InputStream in)
			throws XmlPullParserException, IOException {

		ReadGameDim(in);
		//ReadGridLayout();
		ReadGameConfig();

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

	public static void InitializeTheGridFromServer(int duration,int row , int column , byte[] servermapbytearray)
	{
		gridlayout  = new Byte[row][column];
		gameduration =duration;
		int rowoffset =0;
		int columnoffset=0;
		System.out.println("Map message received from server - "+new String(servermapbytearray));
		for ( int i =0; i < servermapbytearray.length ; ++i)
		{
			
			gridlayout[rowoffset][columnoffset] = servermapbytearray[i];
			if (gridlayout[rowoffset][columnoffset] == '1') {
				players = new Player();
				players.setXCor(rowoffset);
				players.setYCor(columnoffset);
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

	public static void ReadGridLayout() throws XmlPullParserException,
			IOException {
		XmlPullParser parser = Xml.newPullParser();
		int event = parser.getEventType();
		InputStream in = am.open("config.xml");
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

					for (String string : elements) {

						grid[localycounter][localxcounter] = string.getBytes()[0];
						// System.out.println("this is the out put "
						// + grid[localrowcounter][localcolumncounter]);
						if (grid[localycounter][localxcounter] == '1') {
							players = new Player();
							players.setXCor(localycounter);
							players.setYCor(localxcounter);
						}
						/*
						 * if (grid[localrowcounter][localcolumncounter] == '2')
						 * { players[1] = new Player();
						 * players[1].setXCor(localrowcounter);
						 * players[1].setYCor(localcolumncounter); } if
						 * (grid[localrowcounter][localcolumncounter] == '3') {
						 * players[2] = new Player();
						 * players[2].setXCor(localrowcounter);
						 * players[2].setYCor(localcolumncounter); }
						 */
						++localxcounter;
					}
					++localycounter;
					// /System.out.println("name of of the tag " + sout);
				}
			}
				break;

			}
			event = parser.next();
		}
		// /System.out.println("Total number of row " + localrowcounter);
		gridlayout = grid;
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

	public static void ReadGameConfig() throws XmlPullParserException,
			IOException {
		int gd = 0;
		int et = 0;
		int ed = 0;
		int er = 0;
		int rs = 0;
		int pr = 0;
		int po = 0;
		InputStream in = am.open("config.xml");
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
		gameconfig = new Gameconfig(gd, et, ed, er, rs, pr, po);

	}

	/*
	 * public static List<Gameconfig> getEntries() throws
	 * XmlPullParserException, IOException { // return parse(stream);
	 * ReadGridLayout(stream); return ReadGameConfig(stream); }
	 * 
	 * public static List<Gameconfig> parse(InputStream in) throws
	 * XmlPullParserException, IOException { try { XmlPullParser parser =
	 * Xml.newPullParser();
	 * parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	 * parser.setInput(in, null); parser.nextTag(); return readFeed(parser); }
	 * finally { in.close(); } }
	 * 
	 * private static List<Gameconfig> readFeed(XmlPullParser parser) throws
	 * XmlPullParserException, IOException { List<Gameconfig> entries = new
	 * ArrayList<Gameconfig>();
	 * 
	 * // parser.require(XmlPullParser.START_TAG, ns, "feed"); while
	 * (parser.next() != XmlPullParser.END_TAG) { if (parser.getEventType() !=
	 * XmlPullParser.START_TAG) { continue; } String name = parser.getName(); //
	 * Starts by looking for the entry tag if (name.equals("gameconfig")) {
	 * entries.add(readEntry(parser)); } else { skip(parser); } } return
	 * entries; }
	 * 
	 * // This class represents a single entry (post) in the XML feed.
	 * 
	 * 
	 * 
	 * // Parses the contents of an entry. If it encounters a title, summary, or
	 * // link tag, hands them // off // to their respective &quot;read&quot;
	 * methods for processing. Otherwise, // skips the tag. private static
	 * Gameconfig readEntry(XmlPullParser parser) throws XmlPullParserException,
	 * IOException { // parser.require(XmlPullParser.START_TAG, ns,
	 * "gameconfig"); int gd = 0; int et = 0; int ed = 0; int er = 0; int rs =
	 * 0; int pr = 0; int po = 0;
	 * 
	 * while (parser.next() != XmlPullParser.END_TAG) { if
	 * (parser.getEventType() != XmlPullParser.START_TAG) { continue; } String
	 * name = parser.getName(); if (name.equals("gd")) { gd =
	 * Integer.parseInt(parser.getText()); } else if (name.equals("et")) { et =
	 * Integer.parseInt(parser.getText()); } else if (name.equals("ed")) { ed =
	 * Integer.parseInt(parser.getText()); } else if (name.equals("er")) { er =
	 * Integer.parseInt(parser.getText()); } else if (name.equals("rs")) { rs =
	 * Integer.parseInt(parser.getText()); } else if (name.equals("pr")) { pr =
	 * Integer.parseInt(parser.getText()); } else if (name.equals("po")) { po =
	 * Integer.parseInt(parser.getText()); } else { skip(parser); } }
	 * System.out.println("what is the is " + gd + "|" + et + "|" + ed + "|" +
	 * er + "|" + rs + "|" + pr + "|" + po); // /entries.add( new
	 * Entry(gd,et,ed,er,rs,pr,po)); return new Gameconfig(gd, et, ed, er, rs,
	 * pr, po); }
	 * 
	 * // Skips tags the parser isn't interested in. Uses depth to handle nested
	 * // tags. i.e., // if the next tag after a START_TAG isn't a matching
	 * END_TAG, it keeps // going until it // finds the matching END_TAG (as
	 * indicated by the value of "depth" being // 0). private static void
	 * skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	 * if (parser.getEventType() != XmlPullParser.START_TAG) { throw new
	 * IllegalStateException(); } int depth = 1; while (depth != 0) { switch
	 * (parser.next()) { case XmlPullParser.END_TAG: depth--; break; case
	 * XmlPullParser.START_TAG: depth++; break; } }
	 */
	// }
}