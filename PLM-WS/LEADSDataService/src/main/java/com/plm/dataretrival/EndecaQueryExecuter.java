package com.plm.dataretrival;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.endeca.navigation.ENEConnection;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.HttpENEConnection;
import com.endeca.navigation.Navigation;
import com.endeca.navigation.PropertyContainer;
import com.plm.dataretrival.cfg.ConfigDataProvider;
import com.plm.util.PLMConstants;
import com.plm.util.PLMSearchUtil;
import com.plm.util.PLMUtil;


public class EndecaQueryExecuter implements IQueryExecuter {

	private ENEConnection connection = null;
	public int PAGE_MAX_RECORDS = 10;
	private boolean paginationEnabled = true;
	private long lTotalRecords = 0;	
	private static final Logger logger = Logger.getLogger(EndecaQueryExecuter.class);

	public EndecaQueryExecuter() {
		init();
	}

	public void init() {
		lTotalRecords = 0;
		PAGE_MAX_RECORDS = ConfigDataProvider.getInstance().getWSConfig().getRecordsPerPage();
	}

	/**
	 * This set the first random Endeca Connection (ENEConnection) object.
	 * 
	 * @return ENEConnection object
	 */
	public void setEndecaConnection(SearchCriteriaInfo searchCriteria) {
		if (this.connection == null) {
			logger.warn(searchCriteria.getUsername() + ": setting endeca connection");
			String eneHost = PLMSearchUtil.getEndecaHost();
			int enePort = PLMSearchUtil.getEndecaPort();
			this.connection = new HttpENEConnection(eneHost, enePort);
			logger.warn(searchCriteria.getUsername() + ": endeca connection setting complete [" + eneHost + ":" + enePort + "]");
		}
	}

	/**
	 * This set the Live Endeca Connection (ENEConnection) object. If the
	 * previous connection fails
	 * 
	 * @return ENEConnection object
	 */
	public void setLiveEndecaConnection(SearchCriteriaInfo searchCriteria) throws DataException{
		logger.warn(searchCriteria.getUsername() + ": setting live endeca connection");
		String eneHost = "";
		int newEnePort = 0;
		int noOfAttempts = 0;
		boolean attemptSuccessful = false;
		int currEnePort = ((HttpENEConnection)connection).getPort();
		do{
			try {
				eneHost = PLMSearchUtil.getEndecaHost();
				newEnePort = PLMSearchUtil.changeEndecaPort(currEnePort);
				URL localURL = new URL("http", eneHost, newEnePort, "/admin?op=stats");
				localURL.getContent();
				attemptSuccessful = true;
			} catch (IOException e) {
				currEnePort = newEnePort;
				noOfAttempts++;
				logger.error(searchCriteria.getUsername() + ": seems like dgraph on host " + eneHost + " and port " + newEnePort + " is down.");
				logger.error(PLMUtil.getStackTrace(e));
			}
		}while(noOfAttempts<3 && !attemptSuccessful);
		
		if(!attemptSuccessful){
			logger.error(searchCriteria.getUsername() + ": endeca Connection failed more than 3 times");
			throw new DataException(PLMConstants.ERR_SEARCH_ENGINE);
		}
		
		this.connection = new HttpENEConnection(eneHost, newEnePort);
		logger.warn(searchCriteria.getUsername() + ": live endeca connection setting complete [" + eneHost + ":" + newEnePort + "]");
	}

	/**
	 * Sets the Pagination Enabled/Disabled mode.
	 * 
	 * @param paginationEnabled
	 */
	protected void setPaginationEnabled(boolean paginationEnabled) {
		this.paginationEnabled = paginationEnabled;
	}

	/**
	 * Returns whether the Pagination is enabled in getting the Query Results.
	 * 
	 * @return
	 */
	public boolean isPaginationEnabled() {
		return paginationEnabled;
	}

	/**
	 * Sets the Total Records found for in result-set of last executed Query.
	 * 
	 * @param totalRecords
	 */
	protected void setTotalRecords(long totalRecords) {
		lTotalRecords = totalRecords;
	}

	/**
	 * Returns the Total Records found for in result-set of last executed Query.
	 * 
	 * @return long value of total no of records.
	 */
	public long getTotalRecords() {
		return lTotalRecords;
	}

	/**
	 * This creates the Endeca Query String depending on the SearchCriteriaInfo
	 * details and Start Index.
	 * 
	 * @param searchCriteria
	 * @param iStartIndex
	 * @return String - Endeca Query String.
	 */
	protected String getQueryFromSearchCriteria(SearchCriteriaInfo searchCriteria, long iStartIndex) {
		StringBuffer sbFinalQueryString = new StringBuffer();
		StringBuffer sbNrsQuery = new StringBuffer();
		boolean bFirstVal = false;
		boolean bIncludeStateWidePALOR = false;
		boolean bNRSQuery = false;
		
		if (searchCriteria.getCDCNumber() != null
				&& !searchCriteria.getCDCNumber().trim().equals("")) {
			sbFinalQueryString.append("R=");
			sbFinalQueryString.append(searchCriteria.getCDCNumber().toUpperCase());
		} else {
			sbFinalQueryString.append("Ns=CDC Number|0");
			sbFinalQueryString.append("&No=" + iStartIndex);
			sbFinalQueryString.append("&N=0");
			if(
				(searchCriteria.getCounty() != null && searchCriteria.getCounty().size() > 0) ||
				(searchCriteria.getFirstName() != null && searchCriteria.getFirstName().trim().length() > 0) ||
				(searchCriteria.getLastName() != null && searchCriteria.getLastName().trim().length() > 0) ||
				(searchCriteria.getMiddleName() != null && searchCriteria.getMiddleName().trim().length() > 0) ||
				(searchCriteria.getAliasFirstName() != null && searchCriteria.getAliasFirstName().trim().length() > 0) ||
				(searchCriteria.getAliasLastName() != null && searchCriteria.getAliasLastName().trim().length() > 0) ||
				(searchCriteria.getMoniker() != null && searchCriteria.getMoniker().trim().length() > 0) ||
				(searchCriteria.getZip() != null && searchCriteria.getZip().trim().length() > 0) ||
				(searchCriteria.getBirthState() != null && searchCriteria.getBirthState().trim().length() > 0) ||
				(searchCriteria.getHeightInInches() != null && searchCriteria.getHeightInInches().trim().length() > 0) ||
				(searchCriteria.getWeight() != null && searchCriteria.getWeight().trim().length() > 0) ||
				(searchCriteria.getSsn() != null && searchCriteria.getSsn().trim().length() > 0) ||
				(searchCriteria.getFbiNumber() != null && searchCriteria.getFbiNumber().trim().length() > 0) ||
				(searchCriteria.getCiiNumber() != null && searchCriteria.getCiiNumber().trim().length() > 0) ||
				(searchCriteria.getLicensePlate() != null && searchCriteria.getLicensePlate().trim().length() > 0) ||
				(searchCriteria.getDateOfBirth() != null && searchCriteria.getDateOfBirth().trim().length() > 0) ||					
				(searchCriteria.getEthnicity() != null && searchCriteria.getEthnicity().trim().length() > 0) ||
				(searchCriteria.getHairColor() != null && searchCriteria.getHairColor().trim().length() > 0) ||
				(searchCriteria.getPc290SexOff() != null && searchCriteria.getPc290SexOff().trim().length() > 0) ||
				(searchCriteria.getPc4571Arson() != null && searchCriteria.getPc4571Arson().trim().length() > 0) ||
				(searchCriteria.getPc11590Drugs() != null && searchCriteria.getPc11590Drugs().trim().length() > 0) ||
				(searchCriteria.getPc30586FelonyViolation() != null && searchCriteria.getPc30586FelonyViolation().trim().length() > 0) ||
				(searchCriteria.getSmtPicture() != null && searchCriteria.getSmtPicture().trim().length() > 0) ||
				(searchCriteria.getSmtText() != null && searchCriteria.getSmtText().trim().length() > 0) ||
				(searchCriteria.getSmtType() != null && searchCriteria.getSmtType().trim().length() > 0) ||
				(searchCriteria.getCommitmentOffense() != null && searchCriteria.getCommitmentOffense().trim().length() > 0) ||
				(searchCriteria.getCountyOfLLR() != null && searchCriteria.getCountyOfLLR().trim().length() > 0) ||
				(searchCriteria.getUnitCode() != null && searchCriteria.getUnitCode().trim().length() > 0) ||
				(searchCriteria.getCity() != null && searchCriteria.getCity().trim().length() > 0) ||
				(searchCriteria.getParoleeReleaseFromDate() != null && searchCriteria.getParoleeReleaseFromDate().trim().length() > 0) ||
				(searchCriteria.getParoleeReleaseToDate() != null && searchCriteria.getParoleeReleaseToDate().trim().length() > 0) ||
				(searchCriteria.getVehicleFromYear() != null && searchCriteria.getVehicleFromYear().trim().length() > 0) ||
				(searchCriteria.getVehicleToYear() != null && searchCriteria.getVehicleToYear().trim().length() > 0) ||
				(searchCriteria.getIsParoleePAL() != null && searchCriteria.getIsParoleePAL().trim().length() > 0)) {
				
				// forming NRS query begin
				sbNrsQuery.append("&Nrs=collection()/record[");
				bIncludeStateWidePALOR = true;
				bNRSQuery = true;				
			}else if(!bNRSQuery && (searchCriteria.getIncludeStateWidePAL() != null && searchCriteria.getIncludeStateWidePAL().trim().length() > 0)){
				sbNrsQuery.append("&Nrs=collection()/record[");
				bNRSQuery = true;
			}
			
			//County code
			if (searchCriteria.getCounty() != null && searchCriteria.getCounty().size() > 0) {
				int iCnt = 1;
				if(searchCriteria.getCounty().size() > 0) {
					sbNrsQuery.append("(");
				}
				for (String s : searchCriteria.getCounty()) {
					//sbNrsQuery.append("(endeca:matches(.,\"Jurisdiction_Counties\",\"*:" + s + ":*\") and endeca:matches(.,\"Res County\",\"-No County-\")) or ");
					//sbNrsQuery.append("(endeca:matches(.,\"Jurisdiction_Counties\",\"*:" + s + ":*\") and endeca:matches(.,\"County Code\",\"" + s + "\"))");
					//MGM-5-22-2012-C005 defect
					sbNrsQuery.append("(endeca:matches(.,\"Jurisdiction_Counties\",\"*:" + s.trim() + ":*\") or endeca:matches(.,\"County Code\",\"" + s.trim() + "\"))");
					if (iCnt < searchCriteria.getCounty().size()) {
						sbNrsQuery.append(" or ");
					}
					iCnt++;
				}
				if(searchCriteria.getCounty().size() > 0) {
					sbNrsQuery.append(")");
					bFirstVal = true;
				}
				//logger.debug(sbNrsQuery);
			}
			
			//First Name
			if(searchCriteria.getFirstName() != null && searchCriteria.getFirstName().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				} 
				sbNrsQuery.append("(endeca:matches(.,\"First Name\",\"*" + searchCriteria.getFirstName().trim() + "*\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//Last Name
			if(searchCriteria.getLastName() != null && searchCriteria.getLastName().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"Last Name\",\"*" + searchCriteria.getLastName().trim() + "*\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//Middle Name
			if(searchCriteria.getMiddleName() != null && searchCriteria.getMiddleName().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"Middle Name\",\"*" + searchCriteria.getMiddleName().trim() + "*\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			// Alias First Name
			if(searchCriteria.getAliasFirstName() != null && searchCriteria.getAliasFirstName().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"Alias First Name\",\"*" + searchCriteria.getAliasFirstName().trim() + "*\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//Alias Last Name
			if(searchCriteria.getAliasLastName() != null && searchCriteria.getAliasLastName().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"Alias Last Name\",\"*" + searchCriteria.getAliasLastName().trim() + "*\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//Moniker
			if(searchCriteria.getMoniker() != null && searchCriteria.getMoniker().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"Moniker Info\",\"*" + searchCriteria.getMoniker().trim() + "*\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//Zip
			if(searchCriteria.getZip() != null && searchCriteria.getZip().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"Zip\",\"*" + searchCriteria.getZip().trim() + "*\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//Birth State
			if(searchCriteria.getBirthState() != null && searchCriteria.getBirthState().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"Birth State Code\",\"" + searchCriteria.getBirthState().trim() + "\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//Height in CMs			
			if(searchCriteria.getHeightInInches() != null && searchCriteria.getHeightInInches().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"P_Height\",\"" + searchCriteria.getHeightInInches().trim() + "\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}

			//Weight
			if(searchCriteria.getWeight() != null && searchCriteria.getWeight().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"P_Weight\",\"" + searchCriteria.getWeight().trim() + "\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//SSN
			if(searchCriteria.getSsn() != null && searchCriteria.getSsn().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"SSA Number\",\"" + searchCriteria.getSsn().trim() + "\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//FBI
			if(searchCriteria.getFbiNumber() != null && searchCriteria.getFbiNumber().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"FBI Number\",\"" + searchCriteria.getFbiNumber().trim() + "\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//CII
			if(searchCriteria.getCiiNumber() != null && searchCriteria.getCiiNumber().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"CII Number\",\"" + searchCriteria.getCiiNumber().trim() + "\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//License Plate
			if(searchCriteria.getLicensePlate() != null && searchCriteria.getLicensePlate().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"Vehicle License Plate\",\"" + searchCriteria.getLicensePlate().trim() + "\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//Date of Birth
			if(searchCriteria.getDateOfBirth() != null && searchCriteria.getDateOfBirth().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"Birth Date Search\",\"" + searchCriteria.getDateOfBirth().trim() + "\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
						
			//Ethnicity
			if(searchCriteria.getEthnicity() != null && searchCriteria.getEthnicity().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"Race Cd\",\"" + searchCriteria.getEthnicity().trim() + "\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//HairColor - need Hair Color cd as input
			if(searchCriteria.getHairColor() != null && searchCriteria.getHairColor().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"Haircolor Cd\",\"" + searchCriteria.getHairColor().trim() + "\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//PC290 - need a Y or N
			if(searchCriteria.getPc290SexOff() != null && searchCriteria.getPc290SexOff().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"PC 290 REQ\",\"" + searchCriteria.getPc290SexOff().trim() + "\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//PC457.1 - need a Y or N
			if(searchCriteria.getPc4571Arson() != null && searchCriteria.getPc4571Arson().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"PC 457 REQ\",\"" + searchCriteria.getPc4571Arson().trim() + "\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//PC11590 - need a Y or N
			if(searchCriteria.getPc11590Drugs() != null && searchCriteria.getPc11590Drugs().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"HS REQ\",\"" + searchCriteria.getPc11590Drugs().trim() + "\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//PC30586 - need a Y or N
			if(searchCriteria.getPc30586FelonyViolation() != null && searchCriteria.getPc30586FelonyViolation().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"PC 3058 REQ\",\"" + searchCriteria.getPc30586FelonyViolation().trim() + "\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//SMT Location, picture, text
			if(searchCriteria.getSmtType() != null && searchCriteria.getSmtType().trim().length() > 0) {
				String smtQuery = "";
				String smtType = searchCriteria.getSmtType();
				smtType = smtType.replace(" ","_");
				smtQuery = smtType;
				if(searchCriteria.getSmtPicture() != null && searchCriteria.getSmtPicture().trim().length() > 0) {
					smtQuery = smtQuery + " " + searchCriteria.getSmtPicture();
				}
				if(searchCriteria.getSmtText() != null && searchCriteria.getSmtText().trim().length() > 0) {
					smtQuery = smtQuery + " " + searchCriteria.getSmtText();
				}
				if(bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"SMT_Detail\",\"*" + smtQuery + "*\"))");
				bFirstVal = true;
			}	
			if(searchCriteria.getSmtPicture() != null && searchCriteria.getSmtPicture().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"SMT Picture\",\"*" + searchCriteria.getSmtPicture().trim() + "*\"))");
				bFirstVal = true;
			}
			if(searchCriteria.getSmtText() != null && searchCriteria.getSmtText().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"SMT Text\",\"*" + searchCriteria.getSmtText().trim() + "*\"))");
				bFirstVal = true;
			}
			
			//Commitment Offense - need offense code
			if(searchCriteria.getCommitmentOffense() != null && searchCriteria.getCommitmentOffense().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"Offense Code\",\"" + searchCriteria.getCommitmentOffense().trim() + "\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//COLLR - need County Code
			if(searchCriteria.getCountyOfLLR() != null && searchCriteria.getCountyOfLLR().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"CountyOfLastLegalResidence\",\"" + searchCriteria.getCountyOfLLR().trim() + "\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//Unit Code
			if(searchCriteria.getUnitCode() != null && searchCriteria.getUnitCode().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"Unit Code\",\"" + searchCriteria.getUnitCode().trim() + "\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			// Added City Search Criteria
			if (searchCriteria.getCity() != null && searchCriteria.getCity().trim().length() > 0 && !searchCriteria.getCity().equals("city")) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"City\",\"*" + searchCriteria.getCity().trim() + "*\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}

			//Parolee Release Date - from * To
			if(searchCriteria.getParoleeReleaseFromDate() != null && searchCriteria.getParoleeReleaseFromDate().trim().length() > 0 && searchCriteria.getParoleeReleaseToDate() != null && searchCriteria.getParoleeReleaseToDate().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}		
				sbNrsQuery.append("((Revocation_Release_Date_Search>=" + searchCriteria.getParoleeReleaseFromDate() + " and " + "Revocation_Release_Date_Search<=" + searchCriteria.getParoleeReleaseToDate() + ")");
				sbNrsQuery.append(" or ");
				sbNrsQuery.append("(Parole_Date_Search>=" + searchCriteria.getParoleeReleaseFromDate() + " and " + "Parole_Date_Search<=" + searchCriteria.getParoleeReleaseToDate() + "))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//Vehicle Year - From & To
			if(searchCriteria.getVehicleFromYear() != null && searchCriteria.getVehicleFromYear().trim().length() > 0 && searchCriteria.getVehicleToYear() != null && searchCriteria.getVehicleToYear().trim().length() > 0) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(Veh_Year>=" + searchCriteria.getVehicleFromYear() + " and " + "Veh_Year<=" + searchCriteria.getVehicleToYear() + ")");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//is Parolee a PAL
			if(searchCriteria.getIsParoleePAL() != null && searchCriteria.getIsParoleePAL().trim().length() > 0 && searchCriteria.getIsParoleePAL().equalsIgnoreCase("Y")) {
				if (bFirstVal) {
					sbNrsQuery.append(" and ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"Parole Status\",\"SUSPENDED\"))");
				bFirstVal = true;
				//logger.debug(sbNrsQuery);
			}
			
			//IncludeStateWidePAL
			if(searchCriteria.getIncludeStateWidePAL() != null && searchCriteria.getIncludeStateWidePAL().trim().length() > 0 && 
					searchCriteria.getIncludeStateWidePAL().equalsIgnoreCase("Y") ) {
				if(bIncludeStateWidePALOR){
					sbNrsQuery.append(" or ");
				}
				sbNrsQuery.append("(endeca:matches(.,\"Parole Status\",\"SUSPENDED\"))");
				//logger.debug(sbNrsQuery);
			}

			if(bNRSQuery) {
				sbNrsQuery.append("]");
			}
			// forming NRS query end

			if (sbNrsQuery.length() > 0) {
				sbFinalQueryString.append(sbNrsQuery.toString());
			}

			if (searchCriteria.getLastUpdateDateInEndecaFormat() != null) {
				sbFinalQueryString.append("&Nf=Parole Data Change Date|GTEQ+" + searchCriteria.getLastUpdateDateInEndecaFormat());
			}
			
			
		}
		if(iStartIndex==0){
			if(searchCriteria.getUsername()!=null){
				logger.warn(searchCriteria.getUsername() + ": " + sbFinalQueryString.toString());
			}else{
				logger.warn(sbFinalQueryString.toString());
			}
		}
		//logger.debug(sbFinalQueryString.toString());
		return sbFinalQueryString.toString();
	}

	@SuppressWarnings("unchecked")
	public DataSet fetchData(SearchCriteriaInfo searchCriteria, long iStartIndex) throws DataException {
		Collection<PropertyContainer> collData = null;
		String sQueryString = getQueryFromSearchCriteria(searchCriteria,iStartIndex);
		ENEQueryResults eqRs = null;
		try {
			setEndecaConnection(searchCriteria);
			eqRs = PLMSearchUtil.getQueryResults(sQueryString, this.connection);
		} catch(DataException de){
			if(de.getErrorCode().equals(PLMConstants.ERR_PARTIAL_UPDATE_INPROGRESS)){
				setLiveEndecaConnection(searchCriteria);
				eqRs = PLMSearchUtil.getQueryResults(sQueryString, this.connection);
			}
		}
		if (eqRs != null) {
			if (eqRs.containsNavigation()) {
				Navigation navi = eqRs.getNavigation();
				if (navi != null) {
					if (iStartIndex == 0) {
						setTotalRecords(navi.getTotalNumERecs());
					}
					collData = navi.getERecs();
				} else {
					throw new DataException(PLMConstants.ERR_SEARCH_ENGINE);
				}
			} else {
				collData = eqRs.getERecs();
				if (iStartIndex == 0) {
					setTotalRecords(collData.size());
				}
			}
		} else {
			throw new DataException(PLMConstants.ERR_SEARCH_ENGINE);
		}
		DataSet dSet = new DataSet();
		dSet.setEndecaData(collData);
		return dSet;
	}

	public int getRecordsPerPage() {
		return PAGE_MAX_RECORDS;
	}
}
