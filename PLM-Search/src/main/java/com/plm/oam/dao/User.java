package com.plm.oam.dao;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Class representing a Person.
 * @author ashish.chaphekar
 */

public class User {

	private String samAccountName;
	
	private String fullName;

	private String lastName;
	
	private String firstName;

	private String lastLogon;
	
	private String pwdLastSet;
	
	private String distinguishedName;
	
	protected static final String SEP = ",";
	
	protected static final String DATA_SEP = "\"";

	public String getFullName() {
		return fullName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public String getLastLogon() {
		return lastLogon;
	}
	
	public String getPwdLastSet() {
		return pwdLastSet;
	}
	
	public void setPwdLastSet(String pwdLastSet) {
		this.pwdLastSet = pwdLastSet;
	}

	public void setSamAccountName(String samAccountName) {
		this.samAccountName = samAccountName;
	}

	public String getSamAccountName() {
		return samAccountName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public void setLastLogon(String lastLogon) {
		this.lastLogon = lastLogon;
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
			.append(this.getSamAccountName())
			.append(DATA_SEP)
			.append(SEP)
			.append(DATA_SEP)
			.append(this.getFullName())
			.append(DATA_SEP)
			.append(SEP)
			.append(DATA_SEP)
			.append(this.getFirstName())
			.append(DATA_SEP)
			.append(SEP)
			.append(DATA_SEP)
			.append(this.getLastName())
			.append(DATA_SEP)
			.append(SEP)
			.append(DATA_SEP)
			.append(this.getLastLogon())
			.append(DATA_SEP)
			.append(SEP)
			.append(DATA_SEP)
			.append(this.getPwdLastSet())
			.append(DATA_SEP);
		return sbout.toString();
	}
}
