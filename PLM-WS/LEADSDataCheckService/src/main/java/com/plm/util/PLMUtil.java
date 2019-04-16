package com.plm.util;
import java.io.PrintWriter;
import java.io.StringWriter;
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
}