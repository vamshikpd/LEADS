package com.plm.oam.dao;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class UserExt extends User {

	private List<String> memberOf;

	public UserExt(){
		super();
	}
	
	public List<String> getMemberOf() {
		return memberOf;
	}

	public void setMemberOf(List<String> memberOf) {
		this.memberOf = memberOf;
	}
	
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public String toString() {
		StringBuffer sbout = new StringBuffer();
		sbout.append(DATA_SEP)
			.append(this.getMemberOf())
			.append(DATA_SEP);
		return sbout.toString();
	}
}
