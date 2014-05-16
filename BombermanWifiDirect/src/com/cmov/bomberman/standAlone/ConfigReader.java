package com.cmov.bomberman.standAlone;

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
		public final float explosiontimeout;
		public final int explosionduration;
		public final int explosionrange;
		public final float robotspeed;
		public final int pointperrobotkilled;
		public final int pointsperopponentkilled;

		private Gameconfig(int gd, float et, int ed, int er, float rs, int pr,
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
	public static int totalrobotcount=0;

	public static Player players = null;
	private static Logger logger = new Logger();
	static AssetManager am;

	public static Logger getLogger() {
		return logger;
	}

	public static void InitConfigParser(Context context, int gamelevel)
			throws XmlPullParserException {
		mContext = context;
		am = mContext.getAssets();
		String configFilename = "config_"+gamelevel+".xml";
		try {
			stream = am.open(configFilename);
			InitReaders(stream, gamelevel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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

	public static void ReadGridLayout(int gamelevel) throws XmlPullParserException,
			IOException {
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

					for (String string : elements) {

						grid[localycounter][localxcounter] = string.getBytes()[0];
						if (grid[localycounter][localxcounter] == '1') {
							players = new Player();
							players.setXCor(localycounter);
							players.setYCor(localxcounter);
						}
						if (grid[localycounter][localxcounter] == 'r') {
							++totalrobotcount;
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
		float et = 0;
		int ed = 0;
		int er = 0;
		float rs = 0;
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
					et = Float.parseFloat(parser.getAttributeValue(null,
							"value"));
				} else if (name.equals("ed")) {
					ed = Integer.parseInt(parser.getAttributeValue(null,
							"value"));
				} else if (name.equals("er")) {
					er = Integer.parseInt(parser.getAttributeValue(null,
							"value"));
				} else if (name.equals("rs")) {
					rs = Float.parseFloat(parser.getAttributeValue(null,
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
}