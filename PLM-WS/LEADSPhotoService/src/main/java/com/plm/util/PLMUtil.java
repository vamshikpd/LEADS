package com.plm.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/*
  C H A N G E    H I S T O R Y
 ================================================================================================================+
 DATE       | REASON        | AUTHOR        | COMMENTS                                                           |
 ================================================================================================================+
 06/21/2018 | Refactoring   | Emil          | Add milliseconds to directory name patter to prevent duplicate dir-|
            |               |               | ectories creating at shared location by different instances of the |
            |               |               | service.                                                           |
 ----------------------------------------------------------------------------------------------------------------|
 Nov'2018   | refactoring   | Emil Akhmirov | Modified code to fix concurrency issues with Date/DateFormat Java  |
            |               |               | classes. Switched to using Java 8 java.time package instead        |
 ----------------------------------------------------------------------------------------------------------------+
 */

public class PLMUtil {
//	private final static SimpleDateFormat requestDateFormat = new SimpleDateFormat("MMddyyyyHHmmssSSS");
	private static final DateTimeFormatter REQUEST_DATE_FORMAT = DateTimeFormatter.ofPattern("MMddyyyyHHmmssSSS");
	private static int nCounter = 1;

	public static String replaceAllPlaceHolder(String sourceString, String stringToReplace){
//		String currentTimestamp = requestDateFormat.format(Calendar.getInstance().getTime());
		String currentTimestamp = LocalDateTime.now().format(REQUEST_DATE_FORMAT);

		sourceString = PLMUtil.replaceAllPlaceHolder(sourceString, "{USERNAME}", stringToReplace);
		sourceString = PLMUtil.replaceAllPlaceHolder(sourceString, "{TIMESTAMP}", currentTimestamp);
		sourceString = PLMUtil.replaceAllPlaceHolder(sourceString, "{COUNTER}", "" + nCounter++);
		
		return sourceString;
	}
	
	public static String replaceAllPlaceHolder(String sourceString, String sPlaceHolder, String stringToReplace){
		String regexString = "\\Q" + sPlaceHolder + "\\E";
		return sourceString.replaceAll(regexString, stringToReplace);
	}

	public static String getStackTrace(Exception e){
		StringWriter sw = new StringWriter();
	 	e.printStackTrace(new PrintWriter(sw));
	 	return "Exception	:"+ sw.toString();
	}
}
