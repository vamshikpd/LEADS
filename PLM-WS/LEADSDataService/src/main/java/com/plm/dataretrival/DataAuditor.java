package com.plm.dataretrival;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.plm.util.PLMDatabaseUtil;
import com.plm.util.PLMUtil;


public class DataAuditor {
	
	private static final Logger logger = Logger.getLogger(DataAuditor.class);

	public static boolean insertCoriQuery(String userName, String ipAddress, String caseNumber, String reasonNo, String searchType) {
		Connection dbConnection = null;
		boolean auditStatus = true;
		PreparedStatement psCoriQuery = null;
		PreparedStatement psNextQueryId = null;
		String queryId = null;
		ResultSet rsData = null;
		//Date date = Calendar.getInstance().getTime();
		long dateInMillis = Calendar.getInstance().getTimeInMillis();
		try{
			dbConnection = PLMDatabaseUtil.getConnection();
			logger.warn("[" + dateInMillis + "] connection retrieved");
			auditStatus = true;
		
			String nextQueryIdSQL="select cori_query_id_seq.nextval from dual";
			psNextQueryId = dbConnection.prepareStatement(nextQueryIdSQL);
			rsData = psNextQueryId.executeQuery() ;
			
			if(rsData.next()) {
				queryId = rsData.getString(1);
			}
			String insertCoriQuerySQL = "INSERT INTO CORI_QUERY (QUERY_ID,USERNAME, IP_ADDRESS, INQUIRY_DATE, CASE_NO, REASON_NO, SEARCH_TYPE ) VALUES (?, ?, ?, sysdate, ?, ?, ?)";
			psCoriQuery = dbConnection.prepareStatement(insertCoriQuerySQL);
			psCoriQuery.setString(1, queryId);
			psCoriQuery.setString(2, userName);
			psCoriQuery.setString(3, ipAddress);
			psCoriQuery.setString(4, caseNumber);
			psCoriQuery.setString(5, reasonNo);
			psCoriQuery.setString(6, searchType);
			psCoriQuery.executeUpdate();
	
		}catch(SQLException sqle){
			logger.error(PLMUtil.getStackTrace(sqle));
			auditStatus = false;
		}finally{
			try {
				if(psCoriQuery!=null){
					psCoriQuery.close();
					psCoriQuery = null;
				}
				if(rsData!=null){
					rsData.close();
					rsData = null;
				}
				if (dbConnection != null) {
					dbConnection.close();
					dbConnection = null;
					logger.warn("[" + dateInMillis + "] connection released");
				}
			} catch (SQLException sqle) {
				logger.error(PLMUtil.getStackTrace(sqle));
				auditStatus = false;
			}
		}
		return auditStatus;
	}
}