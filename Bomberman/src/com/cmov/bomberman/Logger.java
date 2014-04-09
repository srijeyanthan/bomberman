package com.cmov.bomberman;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;


public class Logger 
{
  public static final String FILENAME = "bomberman.log";
  
  private PrintStream out = System.out;
    
  public Logger()
  {
    try
    {
      out = new PrintStream(FILENAME);  
    }
    catch(Exception ex)
    {
      System.err.println("Logging disabled due to exception: " + ex.getLocalizedMessage());
    }
  }
  
 
  public void log(String action, String ip)
  {
    Calendar cal = new GregorianCalendar( TimeZone.getTimeZone("ECT") );
    SimpleDateFormat formater = new SimpleDateFormat();
    String date = formater.format(cal.getTime());
    
    out.println(date + ": " + ip + "->" + action);
    out.flush();
  }
}