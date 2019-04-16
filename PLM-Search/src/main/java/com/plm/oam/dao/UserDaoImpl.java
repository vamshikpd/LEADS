/*
 * Copyright 2005-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.plm.oam.dao;

import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

import org.apache.log4j.Logger;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.control.PagedResult;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.LessThanOrEqualsFilter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.filter.NotFilter;
import org.springframework.ldap.filter.OrFilter;

import com.plm.oam.apps.ReportConfig;
import com.plm.oam.utils.ReportUtils;


/**
 * 
 * @author ashish.chaphekar
 */
public class UserDaoImpl implements UserDao {

	private static final Logger logger = Logger.getLogger(UserDaoImpl.class);

	private String baseDN; 
	
	private LdapTemplate ldapTemplate;
	

	public void setBaseDN(String baseDN) {
		this.baseDN = baseDN;
	}
	
	public String getBaseDN() {
		return this.baseDN;
	}
	
	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}
	
	@Override
	public void moveUser(String oldDN, String newDN) throws NameNotFoundException {
		if(oldDN == null || newDN == null)
			throw new IllegalArgumentException("Either OldOU/NewOU is invalid...");
		ldapTemplate.rename(oldDN, newDN);
		logger.debug("Moved from '" + oldDN + "' to '" + newDN +"'.");
	}
	
	@Override
	public List<User> getAllInactiveUsers() {
		return getAllInactiveUsers(null);
	}

	@Override
	public String getInactiveUserFilterForAgency(String agency) {
		AndFilter andFilter = new AndFilter();
		andFilter.and(new EqualsFilter("objectclass", "user"));
		if (agency != null)
			andFilter.and(new LikeFilter("samAccountName", agency+"*"));
		OrFilter orFilter = new OrFilter();
		orFilter.or(new NotFilter(new LikeFilter("obuseraccountcontrol", "*")));
		orFilter.or(new EqualsFilter("obuseraccountcontrol", "ACTIVATED"));
		andFilter.and(orFilter);
		
		String sADDateValue = "0";
		Calendar curr = Calendar.getInstance();
		try {
			curr.add(Calendar.DATE, -(ReportConfig.getConfig().getInActiveDaysVal()));
			sADDateValue = ReportUtils.convertJavaDateToAD(curr);
		} catch (Exception e) {
			logger.error(e);
		}
		logger.debug("Date Filter :: " + sADDateValue);
		orFilter = new OrFilter();
		orFilter.or(new LessThanOrEqualsFilter("lastLogonTimestamp", sADDateValue));
		orFilter.or(new AndFilter().and(new LessThanOrEqualsFilter("lastLogon", sADDateValue))
									.and(new NotFilter(new LikeFilter("lastLogonTimestamp","*"))));
		andFilter.and(orFilter);
		return andFilter.encode();
	}
	
	@Override
	public String getUserFilterForAgency(String agency) {
		AndFilter andFilter = new AndFilter();
		andFilter.and(new EqualsFilter("objectclass", "user"));
		if (agency != null)
			andFilter.and(new LikeFilter("samAccountName", agency+"*"));
		OrFilter orFilter = new OrFilter();
		orFilter.or(new NotFilter(new LikeFilter("obuseraccountcontrol", "*")));
		orFilter.or(new EqualsFilter("obuseraccountcontrol", "ACTIVATED"));
		andFilter.and(orFilter);
		return andFilter.encode();
	}
	
	@Override
	public String getFilter(String user) {
		AndFilter andFilter = new AndFilter();
		andFilter.and(new EqualsFilter("objectclass", "user"));
		if (user != null)
			andFilter.and(new EqualsFilter("cn", user));
		String queryfilter = andFilter.encode();
		logger.debug("Ldap Query :: " + queryfilter);
		return queryfilter;
	}
	
	@Override
	public String getLoginIDFilter(String user) {
		AndFilter andFilter = new AndFilter();
		andFilter.and(new EqualsFilter("objectclass", "user"));
		if (user != null)
			andFilter.and(new EqualsFilter("sAMAccountName", user));
		String queryfilter = andFilter.encode();
		logger.debug("Ldap Query :: " + queryfilter);
		return queryfilter;
	}
	
	@Override
	public List<String> getUserGroups(String userdn) {
		AndFilter andFilter = new AndFilter();
		andFilter.and(new EqualsFilter("objectclass", "user"));
		if (userdn != null)
			andFilter.and(new EqualsFilter("distinguishedName", userdn));
		String queryfilter = andFilter.encode();
		List<User> users = getUsers(queryfilter, new UserExtAttributesMapper());
		if(users == null || users.size()== 0) {
			logger.info("User not found for query = " + userdn);
			return null;
		}
		//Above query should return a single user object
		return ((UserExt)users.get(0)).getMemberOf();
	}
	
	@Override
	public List<String> getUserGroupsByLoginID(String loginID) {
		List<User> users = getUsers(getLoginIDFilter(loginID), new UserExtAttributesMapper());
		if(users == null || users.size()== 0) {
			logger.info("User not found for query = " + loginID);
			return null;
		}
		//Above query should return a single user object
		return ((UserExt)users.get(0)).getMemberOf();
	}
	
	/*
	 * (&(objectclass=user)(samAccountName=lapd*)
	 * 	 (|(!(obuseraccountcontrol=*))(obuseraccountcontrol=ACTIVATED))
	 *   (|(lastLogonTimestamp<=129110941170000000)(&(lastLogon<=129110941170000000)(!(lastLogonTimestamp=*)))))
	 * 
	 * @see PersonDao#getAllInactiveUsers(String agency)
	 */
	@Override
	public List<User> getAllInactiveUsers(String agency) {
		return getUsers(getInactiveUserFilterForAgency(agency));
	}
	
	@Override
	public List<User> getUsers(String filter) {
		return getUsers(filter,new UserAttributesMapper());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<User> getUsers(String filter, AttributesMapper attrMapper) {
		if(attrMapper == null)
			attrMapper = new UserAttributesMapper();
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		List<User> users = ldapTemplate.search(baseDN, filter ,searchControls, attrMapper);
		logger.debug("Total User Count :: " + users.size());
		return users;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<User> getAllInactiveUsersByPageResult(String agency) {
		String filter = getInactiveUserFilterForAgency(agency);
		logger.debug("Ldap Query :: " + filter);
		PagedResultsCookie cookie = null;
		List<User> users = null;
		do{
			PagedResult result = getPageResult(filter,cookie,new UserAttributesMapper());
			cookie = result.getCookie();
			if(users == null)
				users = result.getResultList();
			else 
				users.addAll(result.getResultList());
		} while(cookie != null && cookie.getCookie() != null);
	   return users;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<User> getAllUsersByPageResult(String agency) {
		String filter = getUserFilterForAgency(agency);
		logger.info("Updating Users for agency :: " + agency);
		logger.debug("Ldap Query :: " + filter);
		logger.debug("Base DN :: " + baseDN);
		PagedResultsCookie cookie = null;
		List<User> users = null;
		do{
			PagedResult result = getPageResult(filter,cookie,new UserDNMapper());
			if(result != null) {
				cookie = result.getCookie();
				if(users == null)
					users = result.getResultList();
				else 
					users.addAll(result.getResultList());
			}
		} while(cookie != null && cookie.getCookie() != null);
	   return users;
	}
	
	@SuppressWarnings("unchecked")
	private PagedResult getPageResult(String filter, PagedResultsCookie cookie, AttributesMapper mapper){
		if(mapper == null)
			mapper = new UserAttributesMapper();
		List<User> users = null;
		PagedResultsDirContextProcessor control = new PagedResultsDirContextProcessor(ReportConfig.PAGE_SIZE, cookie);
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		try {
			users = ldapTemplate.search(baseDN, filter, searchControls, mapper ,control);
			logger.info("Total User records found : " + users.size());
		}
		catch(NameNotFoundException ex){
			logger.error("No Users found.");
			return null;
		}
		return new PagedResult(users, control.getCookie());
	}
	
	@Override
	public void updateAttributesToUser(List<User> users, Hashtable<String,String> attributes) {
		if(users != null && attributes != null) {
			Iterator<User> iUser = users.iterator();
			Enumeration<String> iAtt = attributes.keys();
			ModificationItem items[] = new ModificationItem[attributes.size()];
			int i=0;
			while (iAtt.hasMoreElements()) {
				String attKey = iAtt.nextElement();
				Attribute attr = new BasicAttribute(attKey,attributes.get(attKey));
				items[i] = new ModificationItem(
						DirContext.REPLACE_ATTRIBUTE, attr);
				i++;
			}
			while(iUser.hasNext()) {
				String userDN = ((User)iUser.next()).getDistinguishedName();
				ldapTemplate.modifyAttributes(userDN, items);
				logger.debug("Updating User: '"+ userDN+"' record. ");
			}
			logger.info("User records updated : " + users.size());
		}
	}
	
	@Override
	public void updateAttributesToUserDN(List<String> userDNList, Hashtable<String,String> attributes) {
		if(userDNList != null && attributes != null) {
			Iterator<String> iUser = userDNList.iterator();
			Enumeration<String> iAtt = attributes.keys();
			ModificationItem items[] = new ModificationItem[attributes.size()];
			int i=0;
			while (iAtt.hasMoreElements()) {
				String attKey = iAtt.nextElement();
				Attribute attr = new BasicAttribute(attKey,attributes.get(attKey));
				items[i] = new ModificationItem(
						DirContext.REPLACE_ATTRIBUTE, attr);
				i++;
			}
			while(iUser.hasNext()) {
				String userDN = (String)iUser.next();
				ldapTemplate.modifyAttributes(userDN, items);
				logger.debug("Updating User: '"+ userDN+"' record. ");
			}
			logger.info("User records updated : " + userDNList.size());
		}
	}
	
	private class UserAttributesMapper implements AttributesMapper {
		public Object mapFromAttributes(Attributes attributes)
				throws NamingException {
			User user = new User();
			user.setSamAccountName((String) attributes.get("sAMAccountName").get());
			user.setFullName((String) attributes.get("cn").get());
			user.setDistinguishedName((String) attributes.get("distinguishedName").get());
			if (attributes.get("givenName") != null)
				user.setFirstName((String) attributes.get("givenName").get());
			if (attributes.get("sn") != null)
				user.setLastName((String) attributes.get("sn").get());
			//Get the lastLogontimeStamp as this attribute is replicated across domain.
			if (attributes.get("lastLogontimeStamp") != null) {
				String lastLogon = ReportUtils
						.convertADDateToJava((String) attributes.get("lastLogontimeStamp").get());
				user.setLastLogon(lastLogon);
			}
			// Get the lastLogon because the user has not logged in the PLM system
			else if(attributes.get("lastLogon") != null){
				String lastLogon = ReportUtils
				.convertADDateToJava((String) attributes.get("lastLogon").get());
				user.setLastLogon(lastLogon);
			}
			if (attributes.get("pwdLastSet") != null) {
				String pwdLastSet = ReportUtils
						.convertADDateToJava((String) attributes.get("pwdLastSet").get());
				user.setPwdLastSet(pwdLastSet);
			}
			return user;
		}
	}
	
	private class UserDNMapper implements AttributesMapper {
		public Object mapFromAttributes(Attributes attributes)
				throws NamingException {
			User user = new User();
			user.setDistinguishedName((String) attributes.get("distinguishedName").get());
			return user;
		}
	}
	
	@SuppressWarnings("unchecked")
	private class UserExtAttributesMapper implements AttributesMapper {
		public Object mapFromAttributes(Attributes attributes)
				throws NamingException {
			UserExt user = new UserExt();
			user.setDistinguishedName((String) attributes.get("distinguishedName").get());
			if (attributes.get("memberOf") != null) {
				user.setMemberOf(Collections.list((NamingEnumeration<String>)attributes.get("memberOf").getAll()));
			}
			return user;
		}
	}
}
