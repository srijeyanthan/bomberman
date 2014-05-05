package com.cmov.bomberman.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


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

	private static int width = 0;
	private static int height = 0;
	public static Byte[][] gridlayout = null;
	private static Gameconfig gameconfig = null;
	private static GameDim gameDim = null;

	public static Map<Integer, Player> playermap = new HashMap<Integer, Player>();
	public static Player players = null;
	public static Player players2 = null;
	public static Player players3 = null;
	public static Player players4 = null;
	private static Logger logger = new Logger();
	public static int serverPort = 0;

	public static Logger getLogger() {
		return logger;
	}

	public static void InitReaders(int gamelevel) {

		String configFilename = "config_"+gamelevel+".xml";
		ReadGameDim();
		ReadGameConfig();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			File file = new File(configFilename);
			if (file.exists()) {
				Document doc = db.parse(file);
				Element docEle = doc.getDocumentElement();

				// Print root element of the document

				NodeList studentList = docEle.getElementsByTagName("field");

				// Print total student elements in document

				Byte[][] grid = new Byte[height][width];
				if (studentList != null && studentList.getLength() > 0) {
					for (int i = 0; i < studentList.getLength(); i++) {

						Node node = studentList.item(i);
						if (node.getNodeType() == Node.ELEMENT_NODE) {
							Element e = (Element) node;
							

							for (int j = 1; j < 14; ++j) {
								String tag = "r" + j;
								int localxcounter = 0;
								NodeList nodeList = e.getElementsByTagName(tag);
								String rowVal = nodeList.item(0)
										.getChildNodes().item(0).getNodeValue();
								String[] elements = rowVal.split(" ");

								for (String mapelement : elements) {

									if (mapelement.getBytes()[0] == '1') {
										playermap.put(0, new Player(j - 1,
												localxcounter));
										grid[j - 1][localxcounter]='-';

									} else if (mapelement.getBytes()[0] == '2') {
										playermap.put(1, new Player(j - 1,
												localxcounter));
										grid[j - 1][localxcounter]='-';
									} else if (mapelement.getBytes()[0] == '3') {
										playermap.put(2, new Player(j - 1,
												localxcounter));
										grid[j - 1][localxcounter]='-';
									} else if (mapelement.getBytes()[0] == '4') {
										playermap.put(3, new Player(j - 1,
												localxcounter));
										grid[j - 1][localxcounter]='-';
									}else {
										grid[j - 1][localxcounter] = mapelement
												.getBytes()[0];
									}

									++localxcounter;
								}

							}

						}
					}
					gridlayout = grid;
				} else {
					System.exit(1);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}

	public static void ReadGameDim() {

		int maxplayer = 3;
		int row = 0;
		int column = 0;
		width = 19;
		height = 13;
		gameDim = new GameDim(maxplayer, 13, 19);
	}

	public static void ReadGameConfig() {
		int gd = 30;
		int et = 1;
		int ed = 1;
		int er = 1;
		int rs = 1;
		int pr = 300;
		int po = 1000;

		gameconfig = new Gameconfig(gd, et, ed, er, rs, pr, po);

	}

	public static Byte[][] getGridLayout() {

		return gridlayout;

	}

	public static void AcquireLock() {
		lock.lock();
	}

	public static void ReleaseLock() {
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

}