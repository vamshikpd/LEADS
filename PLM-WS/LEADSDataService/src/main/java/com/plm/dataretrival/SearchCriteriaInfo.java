package com.plm.dataretrival;

import java.util.List;

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
	private boolean returnSingleRecord = false;
	private List<String> responseFieldsReturned;
	
	private String cdc_number = null;	
	private String lastUpdateDateInEndecaFormat = null;
	private String lastUpdateDate = null;
	private String city = null;
	private List<String> counties = null;
	private String includeStateWidePAL = null;	
	
	private String lastName;
	private String firstName;
	private String middleName;
	private String aliasLastName;
	private String aliasFirstName;
	private String moniker;
	private String zip;
	private String birthState;
	private String heightInInches;
	private String weight;
	private String ssn;
	private String ciiNumber;
	private String fbiNumber;
	private String licensePlate;
	private String dateOfBirth;
	private String paroleeReleaseFromDate;
	private String paroleeReleaseToDate;
	private String vehicleFromYear;
	private String vehicleToYear;
	private String ethnicity;
	private String hairColor;
	private String pc290SexOff;
	private String pc4571Arson;
	private String pc11590Drugs;
	private String pc30586FelonyViolation;
	private String smtType;
	private String smtPicture;
	private String smtText;
	private String commitmentOffense;
	private String countyOfLLR;
	private String unitCode;
	private String isParoleePAL;	
	
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
		return cdc_number;
	}
	/**
	 * @param CDCNumber/s the reason to set
	 */
	public void setCDCNumber(String CDCNumber) {
		this.cdc_number = CDCNumber;
	}

	public void setLastUpdateDateInEndecaFormat(String lastUpdateDateInEndecaFormat) {
		this.lastUpdateDateInEndecaFormat = lastUpdateDateInEndecaFormat;
	}
	
	public String getLastUpdateDateInEndecaFormat() {
		return lastUpdateDateInEndecaFormat;
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
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the county
	 */
	public List<String> getCounty() {
		return counties;
	}
	/**
	 * @param county the county to set
	 */
	public void setCounty(List<String> county) {
		this.counties = county;
	}
	
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the middleName
	 */
	public String getMiddleName() {
		return middleName;
	}
	/**
	 * @param middleName the middleName to set
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	/**
	 * @return the aliasLastName
	 */
	public String getAliasLastName() {
		return aliasLastName;
	}
	/**
	 * @param aliasLastName the aliasLastName to set
	 */
	public void setAliasLastName(String aliasLastName) {
		this.aliasLastName = aliasLastName;
	}
	/**
	 * @return the aliasFirstName
	 */
	public String getAliasFirstName() {
		return aliasFirstName;
	}
	/**
	 * @param aliasFirstName the aliasFirstName to set
	 */
	public void setAliasFirstName(String aliasFirstName) {
		this.aliasFirstName = aliasFirstName;
	}
	/**
	 * @return the moniker
	 */
	public String getMoniker() {
		return moniker;
	}
	/**
	 * @param moniker the moniker to set
	 */
	public void setMoniker(String moniker) {
		this.moniker = moniker;
	}
	/**
	 * @return the zip
	 */
	public String getZip() {
		return zip;
	}
	/**
	 * @param zip the zip to set
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}
	/**
	 * @return the birthState
	 */
	public String getBirthState() {
		return birthState;
	}
	/**
	 * @param birthState the birthState to set
	 */
	public void setBirthState(String birthState) {
		this.birthState = birthState;
	}
	/**
	 * @return the heightInInches
	 */
	public String getHeightInInches() {
		return heightInInches;
	}
	/**
	 * @param heightInInches the heightInInches to set
	 */
	public void setHeightInInches(String heightInInches) {
		this.heightInInches = heightInInches;
	}
	/**
	 * @return the weight
	 */
	public String getWeight() {
		return weight;
	}
	/**
	 * @param weight the weight to set
	 */
	public void setWeight(String weight) {
		this.weight = weight;
	}
	/**
	 * @return the ssn
	 */
	public String getSsn() {
		return ssn;
	}
	/**
	 * @param ssn the ssn to set
	 */
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
	/**
	 * @return the ciiNumber
	 */
	public String getCiiNumber() {
		return ciiNumber;
	}
	/**
	 * @param ciiNumber the ciiNumber to set
	 */
	public void setCiiNumber(String ciiNumber) {
		this.ciiNumber = ciiNumber;
	}
	/**
	 * @return the fbiNumber
	 */
	public String getFbiNumber() {
		return fbiNumber;
	}
	/**
	 * @param fbiNumber the fbiNumber to set
	 */
	public void setFbiNumber(String fbiNumber) {
		this.fbiNumber = fbiNumber;
	}
	/**
	 * @return the licensePlate
	 */
	public String getLicensePlate() {
		return licensePlate;
	}
	/**
	 * @param licensePlate the licensePlate to set
	 */
	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}
	/**
	 * @return the dateOfBirth
	 */
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	/**
	 * @param dateOfBirth the dateOfBirth to set
	 */
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	/**
	 * @return the paroleeReleaseFromDate
	 */
	public String getParoleeReleaseFromDate() {
		return paroleeReleaseFromDate;
	}
	/**
	 * @param paroleeReleaseFromDate the paroleeReleaseFromDate to set
	 */
	public void setParoleeReleaseFromDate(String paroleeReleaseFromDate) {
		this.paroleeReleaseFromDate = paroleeReleaseFromDate;
	}
	/**
	 * @return the paroleeReleaseToDate
	 */
	public String getParoleeReleaseToDate() {
		return paroleeReleaseToDate;
	}
	/**
	 * @param paroleeReleaseToDate the paroleeReleaseToDate to set
	 */
	public void setParoleeReleaseToDate(String paroleeReleaseToDate) {
		this.paroleeReleaseToDate = paroleeReleaseToDate;
	}
	/**
	 * @return the vehicleFromYear
	 */
	public String getVehicleFromYear() {
		return vehicleFromYear;
	}
	/**
	 * @param vehicleFromYear the vehicleFromYear to set
	 */
	public void setVehicleFromYear(String vehicleFromYear) {
		this.vehicleFromYear = vehicleFromYear;
	}
	/**
	 * @return the vehicleToYear
	 */
	public String getVehicleToYear() {
		return vehicleToYear;
	}
	/**
	 * @param vehicleToYear the vehicleToYear to set
	 */
	public void setVehicleToYear(String vehicleToYear) {
		this.vehicleToYear = vehicleToYear;
	}
	/**
	 * @return the ethnicity
	 */
	public String getEthnicity() {
		return ethnicity;
	}
	/**
	 * @param ethnicity the ethnicity to set
	 */
	public void setEthnicity(String ethnicity) {
		this.ethnicity = ethnicity;
	}
	/**
	 * @return the hairColor
	 */
	public String getHairColor() {
		return hairColor;
	}
	/**
	 * @param hairColor the hairColor to set
	 */
	public void setHairColor(String hairColor) {
		this.hairColor = hairColor;
	}
	/**
	 * @return the pc290SexOff
	 */
	public String getPc290SexOff() {
		return pc290SexOff;
	}
	/**
	 * @param pc290SexOff the pc290SexOff to set
	 */
	public void setPc290SexOff(String pc290SexOff) {
		this.pc290SexOff = pc290SexOff;
	}
	/**
	 * @return the pc4571Arson
	 */
	public String getPc4571Arson() {
		return pc4571Arson;
	}
	/**
	 * @param pc4571Arson the pc4571Arson to set
	 */
	public void setPc4571Arson(String pc4571Arson) {
		this.pc4571Arson = pc4571Arson;
	}
	/**
	 * @return the pc11590Drugs
	 */
	public String getPc11590Drugs() {
		return pc11590Drugs;
	}
	/**
	 * @param pc11590Drugs the pc11590Drugs to set
	 */
	public void setPc11590Drugs(String pc11590Drugs) {
		this.pc11590Drugs = pc11590Drugs;
	}
	/**
	 * @return the pc30586FelonyViolation
	 */
	public String getPc30586FelonyViolation() {
		return pc30586FelonyViolation;
	}
	/**
	 * @param pc30586FelonyViolation the pc30586FelonyViolation to set
	 */
	public void setPc30586FelonyViolation(String pc30586FelonyViolation) {
		this.pc30586FelonyViolation = pc30586FelonyViolation;
	}
	/**
	 * @return the smtType
	 */
	public String getSmtType() {
		return smtType;
	}
	/**
	 * @param smtType the smtType to set
	 */
	public void setSmtType(String smtType) {
		this.smtType = smtType;
	}
	/**
	 * @return the smtPicture
	 */
	public String getSmtPicture() {
		return smtPicture;
	}
	/**
	 * @param smtPicture the smtPicture to set
	 */
	public void setSmtPicture(String smtPicture) {
		this.smtPicture = smtPicture;
	}
	/**
	 * @return the smtText
	 */
	public String getSmtText() {
		return smtText;
	}
	/**
	 * @param smtText the smtText to set
	 */
	public void setSmtText(String smtText) {
		this.smtText = smtText;
	}
	/**
	 * @return the commitmentOffense
	 */
	public String getCommitmentOffense() {
		return commitmentOffense;
	}
	/**
	 * @param commitmentOffense the commitmentOffense to set
	 */
	public void setCommitmentOffense(String commitmentOffense) {
		this.commitmentOffense = commitmentOffense;
	}
	
	/**
	 * @return the countyOfLLR
	 */
	public String getCountyOfLLR() {
		return countyOfLLR;
	}
	/**
	 * @param countyOfLLR the countyOfLLR to set
	 */
	public void setCountyOfLLR(String countyOfLLR) {
		this.countyOfLLR = countyOfLLR;
	}
	/**
	 * @return the unitCode
	 */
	public String getUnitCode() {
		return unitCode;
	}
	/**
	 * @param unitCode the unitCode to set
	 */
	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}
	
	public void setReturnSingleRecord(boolean returnSingleRecord) {
		this.returnSingleRecord = returnSingleRecord;
	}
	
	public boolean shouldReturnSingleRecord() {
		return returnSingleRecord;
	}
	
	/**
	 * @return the responseFieldsReturned
	 */
	public List<String> getResponseFieldsReturned() {
		return responseFieldsReturned;
	}
	/**
	 * @param responseFieldsReturned the responseFieldsReturned to set
	 */
	public void setResponseFieldsReturned(List<String> responseFieldsReturned) {
		this.responseFieldsReturned = responseFieldsReturned;
	}

	/**
	 * @return the isParoleePAL
	 */
	public String getIsParoleePAL() {
		return isParoleePAL;
	}
	/**
	 * @param isParoleePAL the isParoleePAL to set
	 */
	public void setIsParoleePAL(String isParoleePAL) {
		this.isParoleePAL = isParoleePAL;
	}
	
	/**
	 * @return the includeStateWidePAL
	 */
	public String getIncludeStateWidePAL() {
		return includeStateWidePAL;
	}
	/**
	 * @param includeStateWidePAL the includeStateWidePAL to set
	 */
	public void setIncludeStateWidePAL(String includeStateWidePAL) {
		this.includeStateWidePAL = includeStateWidePAL;
	}
	
	public String toString() {
		return 
		"SearchCriteriaInfo: \n" +
		"  CaseNumber		:	"+getCaseNumber()+"\n"+		
		", Reason			:	"+getReason()+"\n"+
		", Username			:	"+getUsername()+"\n"+
		", CDC Numbers		:	"+getCDCNumber()+"\n"+
		", LastUpdDate		:	"+getLastUpdateDate() +"\n"+
		", City				:	"+getCity()+"\n"+
		", County			:	"+printCounties()+"\n"+
		", IncludeStateWidePAL: "+getIncludeStateWidePAL()+"\n"+
		", IsParoleePAL		:	"+getIsParoleePAL()+"\n"+
		", Last Name		:	"+getLastName()+"\n"+
		", First Name		:	"+getFirstName()+"\n"+
		", Middle Name		:	"+getMiddleName()+"\n"+
		", Alias Last Name	:	"+getAliasLastName()+"\n"+
		", Alias First Name	:	"+getAliasFirstName()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"+
		", Moniker			:	"+getMoniker()+"\n"
		
		
		;
	}
	public String printCounties(){
    	StringBuilder counties = new StringBuilder();
    	if(getCounty()!=null && getCounty().size()>0){
	    	for(String s:getCounty()){
	    		counties.append(", ").append(s);
	    	}
	    	counties = new StringBuilder(counties.substring(2));
    	}
    	return counties.toString();
    }
}
