package com.plm.oam.apps;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.plm.oam.dao.User;
import com.plm.oam.dao.UserDaoImpl;


public class LDAPUserUpdateForAgency {
	
	private static final Logger logger = Logger.getLogger(LDAPUserUpdateForAgency.class);

	private static String filename;
	
	private static String path = "LEADSUsers";
	
	private static void collectArgs(String args[]) {
		if(args.length == 0) {
			displayUsage();
		}
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-f") && args.length >= i + 1)
				filename = args[i + 1];
			else if (args[i].equals("-p") && args.length >= i + 1)
				path = args[i + 1];
			else if (args[i].equals("-help")) {
				displayUsage();
			}
		}
	}
	
	public static void displayUsage(){
		System.out.println("Usage: java LDAPUserUpdateForAgency [-f filename] [-p path]");
		System.out.println("filename = Fullpath of agency list file (format=csv).");
		System.out.println("path = LDAP path be searched within BASEDN (Eg= 'LEADSUsers').");
		System.exit(1);
	}
	
	public static String getRequestFromFile() {
		if(filename == null){
			logger.error("Invalid Filename.");
			System.exit(1);
		}
		StringBuffer data = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));

			for (String line = reader.readLine(); line != null; line = reader
					.readLine()) {
				data.append(line);
			}
		} catch (IOException e) {
			logger.error("File not found error.");
			System.exit(1);
		}
		return data.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 ApplicationContext ac = new ClassPathXmlApplicationContext("context.xml", LDAPUserUpdateForAgency.class);
		 collectArgs(args);
		 String agencylst = getRequestFromFile();
		 UserDaoImpl userDao = (UserDaoImpl)ac.getBean("userDao");
		 StringTokenizer strTok = new StringTokenizer(agencylst,",");
		 if(path != null)
				userDao.setBaseDN("OU="+path+","+userDao.getBaseDN());
		 while (strTok.hasMoreTokens()) {
			 String agency = (String)strTok.nextElement();
			 agency = (agency.indexOf("_") > 0) ? agency.substring(0,agency.indexOf("_")): agency;
			 List<User> users = userDao.getAllUsersByPageResult(agency);
			 Hashtable<String,String> attr = new Hashtable<String,String>();
			 attr.put("department", agency.toUpperCase());
			 attr.put("ou", path);
			 userDao.updateAttributesToUser(users, attr);
			 System.gc();
		 }
	}
}
