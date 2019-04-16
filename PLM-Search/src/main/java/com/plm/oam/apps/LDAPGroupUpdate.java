package com.plm.oam.apps;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.InputSource;

import com.plm.oam.dao.GroupDaoImpl;
import com.plm.oam.utils.XMLUtil;


public class LDAPGroupUpdate {

	private static final Logger logger = Logger.getLogger(LDAPGroupUpdate.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String eventXML = getEventXML(args);
		logger.debug("Event XML : " + eventXML);
		if(eventXML == null)
			return;
		
		 ApplicationContext ac = new ClassPathXmlApplicationContext("context.xml", LDAPGroupUpdate.class);
		 //String userDN = getTargetUserID(XMLUtil.readFile("event.xml"));
		 String userDN = getTargetUserID(eventXML);
		 logger.info("User ID : " + userDN);
		 GroupDaoImpl groupDao = (GroupDaoImpl)ac.getBean("groupDao");
		 //UserDaoImpl userDao = (UserDaoImpl)ac.getBean("userDao");
		 //User user = userDao.getUsers(userDao.getFilter("CDCRAccountAdmin4")).get(0);
		 //logger.debug("User DN : " + user.getDistinguishedName());
		 //groupDao.addMember("CDCRAccountAdmins", user.getDistinguishedName());
		 for(int i=0; i < args.length - 1; i++ ){
			 groupDao.addMember(args[i], userDN);
		 }
		 //List<String> members = groupDao.getMembersForGroup("CDCRAccountAdmins");
		 //logger.debug("Members: " + members);
		 //logger.debug("User ID : " + getTargetUserIDFromFile());
		 //logger.debug("User ID : " + getTargetUserID(XMLUtil.readFile("event.xml")));
		 System.out.print(eventXML);
		 System.out.flush();
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
		String userId = XMLUtil.getValueByXPath("//ObParam[@name='WfInstance.obtargetdn']/ObValue/text()",xmlStream);
		return userId;
	}

}
