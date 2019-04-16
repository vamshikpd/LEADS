package com.plm.oam.dao;

import java.util.List;

import org.springframework.ldap.filter.Filter;


/**
 * Data Access Object interface for the Group entity.
 * 
 * @author ashish.chaphekar
 */
public interface GroupDao {
	
	public List<String> getMembersForGroup(String group);
	
	public Group getGroup(String group);
	
	public void addMember(String group, String memberDn);
	
	public List<Group> getGroups(Filter filter);

}
