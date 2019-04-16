package com.plm.oam.dao;

import java.util.Hashtable;
import java.util.List;

import org.springframework.ldap.core.AttributesMapper;

/**
 * Data Access Object interface for the Group entity.
 * 
 * @author ashish.chaphekar
 */
public interface UserDao {
	
	public List<User> getAllInactiveUsers();
	
	public List<User> getAllInactiveUsers(String agency);
	
	public List<User> getAllInactiveUsersByPageResult(String agency);
	
	public List<User> getAllUsersByPageResult(String agency);
	
	public String getInactiveUserFilterForAgency(String agency);
	
	public String getUserFilterForAgency(String agency);
	
	public String getFilter(String user);
	
	public String getLoginIDFilter(String user);
	
	public List<User> getUsers(String filter);
	
	public void updateAttributesToUser(List<User> users, Hashtable<String, String> attributes);
	
	public void updateAttributesToUserDN(List<String> userDNList, Hashtable<String, String> attributes);
	
	public List<String> getUserGroups(String userdn);
	
	public List<User> getUsers(String filter, AttributesMapper attrMapper);
	
	public void moveUser(String oldDN, String newDN);

	public List<String> getUserGroupsByLoginID(String loginID);
	
}
