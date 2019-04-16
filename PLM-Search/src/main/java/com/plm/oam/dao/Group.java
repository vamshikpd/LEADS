package com.plm.oam.dao;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Class representing a Group.
 * 
 * @author ashish.chaphekar
 */

public class Group {

	private List<String> members;

	private String name;

	private String distinguishedName;

	public void setMembers(List<String> members) {
		this.members = members;
	}

	public List<String> getMembers() {
		return members;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public String toString() {
		StringBuffer sbout = new StringBuffer();
		sbout.append(this.getName());
		return sbout.toString();
	}
}
