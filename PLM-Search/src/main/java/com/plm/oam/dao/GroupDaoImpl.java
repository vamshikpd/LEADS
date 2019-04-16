package com.plm.oam.dao;

import java.util.Collections;
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
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;



public class GroupDaoImpl implements GroupDao {

	private static final Logger logger = Logger.getLogger(GroupDaoImpl.class);

	private String baseDN; 
	
	private LdapTemplate ldapTemplate;

	public void setBaseDN(String baseDN) {
		this.baseDN = baseDN;
	}
	
	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	public List<String> getMembersForGroup(String group) {
		return getGroup(group).getMembers();
	}

	@SuppressWarnings("unchecked")
	public List<Group> getGroups(Filter filter) {
		AndFilter andFilter = new AndFilter();
		andFilter.and(new EqualsFilter("objectclass", "group"));
		if (filter != null)
			andFilter.and(filter);
		String queryfilter = andFilter.encode();
		logger.debug("Ldap Query :: " + queryfilter);
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		List<Group> groups = ldapTemplate.search(baseDN,
				queryfilter, searchControls, new GroupAttributesMapper());
		return groups;
	}
	
	public Group getGroup(String group) {
		EqualsFilter filter = null;
		if (group != null)
			filter = new EqualsFilter("cn", group);
		List<Group> groups = getGroups(filter);
		logger.debug("Group :: " + groups.get(0));
		return groups.get(0);
	}

	public void addMember(String group, String memberDn) {
		Group groupobj = getGroup(group);
		Attribute attr = new BasicAttribute("member",memberDn);
		ModificationItem item = new ModificationItem(
				DirContext.ADD_ATTRIBUTE, attr);
		try {
			ldapTemplate.modifyAttributes(groupobj.getDistinguishedName(), new ModificationItem[] { item });
			logger.debug("User '"+ memberDn+"' added in group '"+group+"'.");
		}
		catch(NameAlreadyBoundException ex) {
			logger.error("User '"+ memberDn+"' already exists in group '"+group+"'.");
		}
	}
	
	private class GroupAttributesMapper implements AttributesMapper {
		@SuppressWarnings("unchecked")
		public Object mapFromAttributes(Attributes attributes)
				throws NamingException {
			Group group = new Group();
			group.setName((String) attributes.get("cn").get());
			group.setDistinguishedName((String) attributes.get("distinguishedName").get());
			if (attributes.get("member") != null)
				group.setMembers(Collections.list((NamingEnumeration<String>) attributes.get(
						"member").getAll()));
			return group;
		}
	}
}
