package com.plm.oam.apps;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.plm.oam.dao.User;
import com.plm.oam.dao.UserDao;
import com.plm.oam.utils.ReportUtils;


public class LDAPInactiveUsersReport {

	private static final Logger logger = Logger.getLogger(LDAPInactiveUsersReport.class);
	
	private static String user;
	
	private static ApplicationContext ac = new ClassPathXmlApplicationContext("context.xml", LDAPInactiveUsersReport.class);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	/*	String sADdate = "129060078176958750";
		ReportUtils.convertADDateToJava(sADdate);
		
		String sJavaDate = "December 22, 2009 06:10:17 PM";
		try {
			ReportUtils.convertJavaDateToAD(sJavaDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ReportUtils.convertADDateToJava("129060078170000000");*/
		 //UserDaoImpl userDao = (UserDaoImpl)ac.getBean("userDao");
		 //List<User> users = userDao.getAllInactiveUsers("sacpd");
		 //List<User> users = userDao.getAllInactiveUsersByPageResult("cdcr");
		 //logger.debug(users);
		 //ReportUtils.createReport(users,"cdcr","leaadmin");
		collectArgs(args);
		if(user == null)
			displayUsage();
		generateReport(user);
	}

	public static File generateReport(String user) { 
		String userId = "lea";
		if(user.indexOf("_") > 0 ){
			userId = user.substring(0, user.indexOf("_"));
		}
		logger.info("Agency for report generation is :: " + userId);
		UserDao userDao = (UserDao) ac.getBean("userDao");
		List<User> users = userDao.getAllInactiveUsersByPageResult(userId);
		File file = ReportUtils.createReport(users, userId, user);
		return file;
	}
	
	private static void collectArgs(String args[]) {
		if(args.length == 0) {
			displayUsage();
		}
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-u") && args.length >= i + 1)
				user = args[i + 1];
			else if (args[i].equals("-help")) {
				displayUsage();
			}
		}
	}
	
	public static void displayUsage(){
		System.out.println("Usage: java LDAPInactiveUsersReport [-u user]");
		System.out.println("user: User for which the agency report will be generated.");
		System.exit(1);
	}
}
