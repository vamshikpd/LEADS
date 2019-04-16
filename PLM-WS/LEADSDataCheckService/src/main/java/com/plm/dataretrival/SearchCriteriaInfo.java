package com.plm.dataretrival;
import java.time.LocalDate;

/*
  C H A N G E    H I S T O R Y
 ================================================================================================================+
 DATE       | REASON        | AUTHOR        | COMMENTS                                                           |
 ================================================================================================================+
 Dec'2017   | initial       |               | CDCR LEADS legacy code.                                            |
 ----------------------------------------------------------------------------------------------------------------|
 Nov'2018   | refactoring   | Emil Akhmirov | Modified code to fix concurrency issues with Date/DateFormat Java  |
            |               |               | classes. Switched to using Java 8 java.time package instead        |
 ----------------------------------------------------------------------------------------------------------------+
 */

/**
 * Info class for Search Criteria.
 * @author tushardalal
 *
 */
public class SearchCriteriaInfo {
	private String username = null;
	private String caseNumber = null;
	private String reason = null;
	private String ipAddress = null;
	private String cdcNumber = null;
	private LocalDate dLastUpdateDate = null;
	private String lastUpdateDate = null;
	//--- Base Methods ---
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}
	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	/**
	 * @return the caseNumber
	 */
	public String getCaseNumber() {
		return caseNumber;
	}
	/**
	 * @param caseNumber the caseNumber to set
	 */
	public void setCaseNumber(String caseNumber) {
		this.caseNumber = caseNumber;
	}
	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}
	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}
	/**
	 * @return the CDCNumber/s
	 */
	public String getCDCNumber() {
		return cdcNumber;
	}
	/**
	 * @param CDCNumber/s the reason to set
	 */
	public void setCDCNumber(String CDCNumber) {
		this.cdcNumber = CDCNumber;
	}
	/**
	 * @return the lastUpdateDate Object
	 */
	public LocalDate getLastUpdateDateObj() {
		return dLastUpdateDate;
	}
	/**
	 * @param lastUpdateDate the lastUpdateDate to set
	 */
	public void setLastUpdateDateObj(LocalDate dLastUpdateDate) {
		this.dLastUpdateDate = dLastUpdateDate;
	}
	/**
	 * @return the lastUpdateDate
	 */
	public String getLastUpdateDate() {
		return lastUpdateDate;
	}
	/**
	 * @param lastUpdateDate the lastUpdateDate to set
	 */
	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public String toString() {
		return "SearchCriteriaInfo: \nCaseNumber		:	"+getCaseNumber()+"\n"+
		", Reason		:	"+getReason()+"\n"+
		", Username		:	"+getUsername()+"\n"+
		", CDC Numbers	:	"+getCDCNumber()+"\n"+
		", LastUpdDate	:	"+getLastUpdateDate();
	}
}