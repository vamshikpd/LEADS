package com.plm.oam.apps;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.ldap.core.DistinguishedName;
import org.xml.sax.InputSource;

import com.plm.oam.dao.UserDaoImpl;
import com.plm.oam.utils.XMLUtil;


public class LDAPAppendGroupInUserProfile {

	
	private static final Logger logger = Logger.getLogger(LDAPAppendGroupInUserProfile.class);
	
	private static ApplicationContext ac = new ClassPathXmlApplicationContext(
			"context.xml", LDAPMoveUsers.class);
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String eventXML = getEventXML(args);
		logger.debug("Event XML : " + eventXML);
		if(eventXML == null)
			return;
		
	
	
		//String userDN = getTargetUserID(XMLUtil.readFile("event.xml"));
		String userDN = getTargetUserID(eventXML);
		UserDaoImpl userDao = (UserDaoImpl)ac.getBean("userDao");
		List<String> groups = userDao.getUserGroups(userDN);
		logger.debug("Groups : " + groups);
		//Document document = XMLUtil.getDocument((new ClassPathResource("group-rec.xml")).getFile());
		//logger.debug("Root : " + document.getFirstChild());
		
	 /*System.out.print(eventXML);
		 System.out.flush();*/
	}
	
	public static String getUserGroupInfo(String loginID) {
		StringBuffer sb = new StringBuffer();
		if(loginID == null || loginID.equals("")) {
			logger.info("LoginID is empty...");
			return sb.toString();
		}
		logger.info("Login ID : " + loginID);
		UserDaoImpl userDao = (UserDaoImpl)ac.getBean("userDao");
		List<String> groups = userDao.getUserGroupsByLoginID(loginID);
		if(groups == null || groups.size()==0) {
			logger.info("User GroupInfo is empty...");
			return sb.toString();
		}
		Iterator<String> iter = groups.iterator();
		DistinguishedName dn;
		while(iter.hasNext()) {
			dn = new DistinguishedName(iter.next());
			String dnName = dn.get(dn.size()- 1);
			dnName = dnName.substring(dnName.indexOf("=") + 1);
			sb.append(dnName);
			if(iter.hasNext())
				sb.append(" , ");
		}
		logger.debug("Groups : " + sb.toString());
		return sb.toString();
	}
	
	private static String getEventXML(String[] args) {
		if(args.length > 1) {
			return args[(args.length-1)];
		}
		return null;
	}
	
	@SuppressWarnings("unused")
	private static String getTargetUserIDFromFile() {
		String userId = null;
		try {
			userId = XMLUtil.getValueByXPath("//ObParam[@name='ObRequest.TARGET_UID']/ObValue/text()", 
				new InputSource(new FileReader("event.xml")));
		} catch (FileNotFoundException e) {
			logger.error(e);
		}
			//XMLUtil.readNodesByXPath("//ObParam[@name='ObRequest.TARGET_UID']/ObValue/text()", "event.xml");
		return userId;
	}
	
	private static String getTargetUserID(String xmlStream) {
		String userId = XMLUtil.getValueByXPath("//ObParam[@name='ObRequest.TARGET_UID']/ObValue/text()",xmlStream);
		return userId;
	}

}
