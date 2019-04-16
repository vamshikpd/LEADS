package com.plm.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.endeca.navigation.UrlENEQuery;
import com.endeca.navigation.UrlENEQueryParseException;

public class PLMUtil {

	public static String replaceAllPlaceHolder(String sourceString, String placeHolder, String stringToReplace){
		String regexString = "\\Q" + placeHolder + "\\E";
		return sourceString.replaceAll(regexString, stringToReplace);
	}
	
	public static String getStackTrace(Exception e){
		StringWriter sw = new StringWriter();
	 	e.printStackTrace(new PrintWriter(sw));
	 	return "Exception	:"+ sw.toString();
	}
	
	public static void main(String[] args) throws UrlENEQueryParseException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		long millisLong = 1303876800000L;
		TimeZone tz1 = TimeZone.getTimeZone("UTC");
		TimeZone tz2 = TimeZone.getTimeZone("PST");
		sdf.setTimeZone(tz1);
		
		Calendar c1 = Calendar.getInstance(tz1);
		c1.setTimeInMillis(millisLong);
		System.out.println("UTC:" + sdf.format(c1.getTime()));
		sdf.setTimeZone(tz2);
		Calendar c2 = Calendar.getInstance(tz2);
		c2.setTimeInMillis(millisLong);
		System.out.println("PST:" + sdf.format(c2.getTime()));
		
		UrlENEQuery query = new UrlENEQuery("Nty=1&Ntx=mode matchallpartial&Ntk=All&tab=0&N=0&Ntt=%","UTF-8");
		
	}
}
