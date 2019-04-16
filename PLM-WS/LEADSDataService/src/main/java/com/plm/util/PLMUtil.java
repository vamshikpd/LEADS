package com.plm.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

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

	private static String replaceAllPlaceHolder(String sourceString, String sPlaceHolder, String stringToReplace){
		String regexString = "\\Q" + sPlaceHolder + "\\E";
		return sourceString.replaceAll(regexString, stringToReplace);
	}

	public static String getStackTrace(Exception e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return "Exception	:"+ sw.toString();
	}

	public static Set<String> getReturnFieldsSet(){
		Set<String> returnFieldsSet = new TreeSet<String>();
		returnFieldsSet.add(PLMConstants.PAR);
		returnFieldsSet.add(PLMConstants.ADD);
		returnFieldsSet.add(PLMConstants.ALI);
		returnFieldsSet.add(PLMConstants.JOB);
		returnFieldsSet.add(PLMConstants.MON);
		returnFieldsSet.add(PLMConstants.OFF);
		returnFieldsSet.add(PLMConstants.SMT);
		returnFieldsSet.add(PLMConstants.SPC);
		returnFieldsSet.add(PLMConstants.VEH);
		returnFieldsSet.add(PLMConstants.LAST_NAME);
		returnFieldsSet.add(PLMConstants.FIRST_NAME);
		returnFieldsSet.add(PLMConstants.MIDDLE_NAME);
		returnFieldsSet.add(PLMConstants.AGENT_CODE);
		returnFieldsSet.add(PLMConstants.AGENT_NAME);
		returnFieldsSet.add(PLMConstants.AGENT_PHONE);
		returnFieldsSet.add(PLMConstants.BIRTH_DATE);
		returnFieldsSet.add(PLMConstants.SEX);
		returnFieldsSet.add(PLMConstants.RACE_CODE);
		returnFieldsSet.add(PLMConstants.EYE_COLOR_CODE);
		returnFieldsSet.add(PLMConstants.HEIGHT);
		returnFieldsSet.add(PLMConstants.WEIGHT);
		returnFieldsSet.add(PLMConstants.HAIR_COLOR_CODE);
		returnFieldsSet.add(PLMConstants.PAROLE_DATE);
		returnFieldsSet.add(PLMConstants.CLASSIFICATION_CODE);
		returnFieldsSet.add(PLMConstants.HS_REQ);
		returnFieldsSet.add(PLMConstants.PC_290_REQ);
		returnFieldsSet.add(PLMConstants.PC_457_REQ);
		returnFieldsSet.add(PLMConstants.CII_NUMBER);
		returnFieldsSet.add(PLMConstants.FBI_NUMBER);
		returnFieldsSet.add(PLMConstants.SSA_NUMBER);
		returnFieldsSet.add(PLMConstants.DRIVER_LICENCE);
		returnFieldsSet.add(PLMConstants.STATUS);
		returnFieldsSet.add(PLMConstants.PC_3058_REQ);
		returnFieldsSet.add(PLMConstants.ANT_REQ);
		returnFieldsSet.add(PLMConstants.NO_ALCOHOL);
		returnFieldsSet.add(PLMConstants.POC_REQ);
		returnFieldsSet.add(PLMConstants.BIRTH_STATE_CODE);
		returnFieldsSet.add(PLMConstants.COMMENTS);
		returnFieldsSet.add(PLMConstants.PROBLEM_AREA_NARCOTIC);
		returnFieldsSet.add(PLMConstants.PROBLEM_AREA_ALCOHOL);
		returnFieldsSet.add(PLMConstants.PROBLEM_AREA_ASSAULT);
		returnFieldsSet.add(PLMConstants.PROBLEM_AREA_SEX);
		returnFieldsSet.add(PLMConstants.PROBLEM_AREA_OTHER);
		returnFieldsSet.add(PLMConstants.HS_DATE);
		returnFieldsSet.add(PLMConstants.PC_290_DATE);
		returnFieldsSet.add(PLMConstants.PC_457_DATE);
		returnFieldsSet.add(PLMConstants.PC_3058_DATE);
		returnFieldsSet.add(PLMConstants.CONTROL_DISCHARGE_DATE);
		returnFieldsSet.add(PLMConstants.COUNTY_COMMIT);
		returnFieldsSet.add(PLMConstants.DISCHARGED_DATE);
		returnFieldsSet.add(PLMConstants.REVOCATION_RELEASE_DATE);
		returnFieldsSet.add(PLMConstants.COLLR);

		return returnFieldsSet;
	}
}
