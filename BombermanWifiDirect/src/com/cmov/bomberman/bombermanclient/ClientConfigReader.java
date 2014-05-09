package com.cmov.bomberman.bombermanclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class parses XML feeds from stackoverflow.com. Given an InputStream
 * representation of a feed, it returns a List of entries, where each list
 * element represents a single entry (post) in the XML feed.
 */

public class ClientConfigReader {

	final static Lock lock = new ReentrantLock();





	static Context mContext;
	public static Byte[][] gridlayout = null;

	@SuppressLint("UseSparseArrays")
	
	private static Logger logger = new Logger();
	static AssetManager am;
	public static int totalnoplayer=0;
	public static int totalrobotcount=0;

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



	public static Byte[][] getGridLayout() {

		return gridlayout;

	}

	public static void LockTheGrid() {
		lock.lock();
	}

	public static void UnlockTheGrid() {
		lock.unlock();
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
		gridlayout  = new Byte[row][column];
		int rowoffset =0;
		int columnoffset=0;
		System.out.println("Map message received from server - "+new String(servermapbytearray));
		for ( int i =0; i < servermapbytearray.length ; ++i)
		{
			
			gridlayout[rowoffset][columnoffset] = servermapbytearray[i];
			++columnoffset;
			if(columnoffset==column)
			{
				++rowoffset;
				columnoffset=0;
			}
			
		}
		
		setmapdataready(true);
	}

	
}