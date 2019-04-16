package com.plm.oam.apps;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;


import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.ldap.NameNotFoundException;

import com.plm.oam.dao.GroupDaoImpl;
import com.plm.oam.dao.UserDaoImpl;


public class LDAPMoveUsers {

	private static final Logger logger = Logger.getLogger(LDAPMoveUsers.class);

	private static String oldOU = "LEADSUsers";

	private static String newOU = "LEADSAccountAdmins";

	private static String userList;

	private static String groupList;
	
	private static ApplicationContext ac = new ClassPathXmlApplicationContext(
									"context.xml", LDAPMoveUsers.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		collectArgs(args);
		if (userList == null || groupList == null)
			displayUsage();
		List<String> userUpdList = moveUsers(userList, groupList, oldOU, newOU);
		logger.info("Total Users updated : " + userUpdList.size());
	}

	public static List<String> moveUsers(String userList, String groupList,
			String oldOU, String newOU) {
		return moveUsers(userList, groupList, oldOU, newOU, null);
	}

	public static List<String> moveUsers(String userList, String groupList,
			String oldOU, String newOU, List<String> failedUsers) {
		if (userList == null || groupList == null)
			throw new IllegalArgumentException(
					"Either UserList/GroupList is invalid.");
		if(failedUsers == null)
			failedUsers = new ArrayList<String>();
		
		UserDaoImpl userDao = (UserDaoImpl) ac.getBean("userDao");
		GroupDaoImpl groupDao = (GroupDaoImpl) ac.getBean("groupDao");
		String baseDN = userDao.getBaseDN();
		StringTokenizer strTok = new StringTokenizer(userList.trim(), ",");
		StringTokenizer strGrpTok = null;
		String user = null;
		String grp = null;
		String oldDN = null;
		String newDN = null;
		List<String> userDNList = new ArrayList<String>();
		while (strTok.hasMoreTokens()) {
			user = (String) strTok.nextElement();
			oldDN = "CN=" + user + ",OU=" + oldOU + "," + baseDN;
			newDN = "CN=" + user + ",OU=" + newOU + "," + baseDN;
			try {
				userDao.moveUser(oldDN, newDN);
				logger.info("Moved user '" + user + "' from '" + oldOU
						+ "' to '" + newOU + "'.");
				userDNList.add(newDN);
			} catch (NameNotFoundException ex) {
				logger.error("User entry  '" + oldDN + "' not found.");
				failedUsers.add(user);
				continue;
			}
			strGrpTok = new StringTokenizer(groupList, ",");
			while (strGrpTok.hasMoreTokens()) {
				grp = strGrpTok.nextToken();
				groupDao.addMember(grp, newDN);
			}
		}
		if(userDNList.size()>0) {
			Hashtable<String, String> attr = new Hashtable<String, String>();
			attr.put("ou", newOU);
			userDao.updateAttributesToUserDN(userDNList, attr);
		}
		return userDNList;
	}

	private static void collectArgs(String args[]) {
		if (args.length == 0) {
			displayUsage();
		}
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-u") && args.length >= i + 1)
				userList = args[i + 1];
			else if (args[i].equals("-g") && args.length >= i + 1)
				groupList = args[i + 1];
			else if (args[i].equals("-o") && args.length >= i + 1)
				oldOU = args[i + 1];
			else if (args[i].equals("-n") && args.length >= i + 1)
				newOU = args[i + 1];
			else if (args[i].equals("-help")) {
				displayUsage();
			}
		}
	}

	public static void displayUsage() {
		System.out
				.println("Usage: java LDAPMoveUser [-u userList] [-g groupList] [-o oldOU] [-n newOU]");
		System.out.println("userList: CSV values of users to be moved.");
		System.out
				.println("groupList: CSV values of groups user should be added.");
		System.out.println("oldOU: existing OU of the user.");
		System.out.println("newOU: new OU user should be moved.");
		System.exit(1);
	}

}
