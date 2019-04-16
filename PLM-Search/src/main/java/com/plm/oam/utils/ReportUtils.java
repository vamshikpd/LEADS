package com.plm.oam.utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.plm.oam.apps.ReportConfig;
import com.plm.oam.dao.User;


public class ReportUtils {

	private static final Logger logger = Logger.getLogger(ReportUtils.class);
	
	public static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("MMM d, yyyy hh:mm:ss aaa");
	
	public static SimpleDateFormat FILE_DATE_FORMATTER = new SimpleDateFormat("MM-dd-yyyy");
	
	public static String convertADDateToJava(String sADdate) {
		long ADdate = Long.parseLong(String.valueOf(sADdate));
		logger.debug("AD Date format : " + sADdate);
		if(ADdate == 0)
			return "Never";
		// AD Epoch is 01 January, 1601
		// java date Epoch is 01 January, 1970
		// so take the number and subtract java Epoch:
		long javaTime = ADdate - 0x19db1ded53e8000L;

		// convert UNITS from (100 nano-seconds) to (milliseconds)
		javaTime /= 10000;

		// Date(long date)
		// Allocates a Date object and initializes it to represent
		// the specified number of milliseconds since the standard base
		// time known as "the epoch", namely January 1, 1970, 00:00:00 GMT.
		Date date1 = new Date(javaTime);
		String newDateString = DATE_FORMATTER.format(date1);
		logger.debug("Java Date format : " + newDateString);
		return newDateString;
	}
	
	public static String convertJavaDateToAD(Calendar date) throws ParseException {
		return convertJavaDateToAD(DATE_FORMATTER.format(date.getTime()));
	}
	
	public static String convertJavaDateToAD(String sJavaDate) throws ParseException {
		logger.debug("Java Date format : " + sJavaDate);
		Date javaDate = DATE_FORMATTER.parse(sJavaDate);
		long javaTime = javaDate.getTime() * 10000;
		javaTime += 0x19db1ded53e8000L;
		logger.debug("AD Date value : "+javaTime);
		return new Long(javaTime).toString();
	}
	
	public static File createReport(List<User> users,String agency, String userId){
		File report = null;
		try {
			String fileName = ReportConfig.getConfig().getReportFilename();
			StringBuffer sbOut = new StringBuffer(fileName);
			sbOut.replace(0, fileName.indexOf("{AGENCY}") + "{AGENCY}".length(), agency);
			if(sbOut.indexOf("{DATE}")>0)
				sbOut.replace(sbOut.indexOf("{DATE}"), sbOut.indexOf("{DATE}") + "{DATE}".length(), FILE_DATE_FORMATTER.format(Calendar.getInstance().getTime()));
			logger.debug("Formatted FileName :: " + sbOut.toString());
			
			//String filePath = ReportConfig.getConfig().getFileOutputPath() + File.separator + sbOut.toString();
			
			String reportFilePath = System.getProperty("user.dir");
			reportFilePath = reportFilePath.replace("\\", File.separator);
			reportFilePath = reportFilePath.concat(File.separator);
			reportFilePath = reportFilePath.concat(ReportConfig.getConfig().getFileOutputPath());
			
			String filePath = reportFilePath + File.separator + sbOut.toString();
			System.out.println("ReportUtil file path is :: " + filePath);
			
			logger.debug("Formatted FilePath :: " + filePath);
			logger.debug("Headers :: " + ReportConfig.getConfig().getDisplayableReportHeaders().toString());
			sbOut = new StringBuffer(ReportConfig.getConfig().getReportTitle());
			if(sbOut.indexOf("{USERID}")>0)
				sbOut.replace(sbOut.indexOf("{USERID}"), sbOut.indexOf("{USERID}") + "{USERID}".length(), userId);
			if(sbOut.indexOf("{DATE}")>0)
				sbOut.replace(sbOut.indexOf("{DATE}"), sbOut.indexOf("{DATE}") + "{DATE}".length(), FILE_DATE_FORMATTER.format(Calendar.getInstance().getTime()));
			logger.debug("Title :: " + sbOut.toString());
			
			report = new File(filePath);
			ArrayList<Object> arr = new ArrayList<Object>();
			arr.add(sbOut);
			arr.add(ReportConfig.getConfig().getDisplayableReportHeaders());
			arr.addAll(2, users);
			FileUtils.writeLines(report, arr);
		} catch (Exception e) {
			logger.error(e);
		}
		return report;
	}
}
