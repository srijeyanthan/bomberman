package com.cmov.bomberman.server;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Logger {
	public static final String FILENAME = "bomberman.log";

	private PrintStream out = System.out;

	public Logger() {
		try {
			out = new PrintStream(FILENAME);
		} catch (Exception ex) {
			System.err.println("Logging disabled due to exception: "
					+ ex.getLocalizedMessage());
		}
	}

	public void logMessage(String message) {
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("ECT"));
		SimpleDateFormat formater = new SimpleDateFormat();
		String date = formater.format(cal.getTime());
		out.println(date + ": " + message);
		out.flush();
	}

	public void log(String arg1) {
		String message = arg1;
		logMessage(message);
	}

	public void log(String arg1,Object arg2) {
		arg1 =String.format(arg1, arg2);
		logMessage(arg1);
	}

	public void log(String arg1,Object arg2, Object arg3) {
		arg1=String.format(arg1, arg2,arg2);
		logMessage(arg1);
	}
}