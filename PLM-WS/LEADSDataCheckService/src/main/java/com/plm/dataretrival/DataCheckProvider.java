package com.plm.dataretrival;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.plm.util.PLMConstants;
import com.plm.util.PLMDatabaseUtil;
import com.plm.util.PLMUtil;


/*
  C H A N G E    H I S T O R Y
 ================================================================================================================+
 DATE       | REASON        | AUTHOR        | COMMENTS                                                           |
 ================================================================================================================+
 05/30/2018 | ALM #13252    | Emil          | Refactor this class to disable extra logging and audit. This web   |
            |               |               | service is a "back end" service while the LEADSAuthService is the  |
            |               |               | primary service that end users are connecting to. DB level audits  |
            |               |               | will be made in LEADSAuthService.                                  |
-----------------------------------------------------------------------------------------------------------------|
 Nov'2018   | refactoring   | Emil Akhmirov | Modified code to fix concurrency issues with Date/DateFormat Java  |
            |               |               | classes. Switched to using Java 8 java.time package instead        |
 ----------------------------------------------------------------------------------------------------------------+
 */

/**
 * This provides the Data Check depending on the Search Criteria.
 *
 * @author Rakesh Nemade
 */
public class DataCheckProvider {
    private String updateAvailable = "";
    private static final Logger logger = Logger.getLogger(DataCheckProvider.class);

    public DataCheckProvider() {
        init();
    }

    protected void init() {
        updateAvailable = "";
    }

    public String checkIfUpdateAvailable(SearchCriteriaInfo searchCriteria) throws DataException {
        boolean isUpdateAvailableForData = checkIfUpdateAvailableForData(searchCriteria);
        boolean isUpdateAvailableForPhoto = checkIfUpdateAvailableForPhoto(searchCriteria);
        if (isUpdateAvailableForData && isUpdateAvailableForPhoto) {
            updateAvailable = "both";
        } else if (isUpdateAvailableForData) {
            updateAvailable = "data";
        } else if (isUpdateAvailableForPhoto) {
            updateAvailable = "photo";
        } else {
            updateAvailable = "none";
        }
        return updateAvailable;
    }

    private boolean checkIfUpdateAvailableForData(SearchCriteriaInfo searchCriteria) throws DataException {
        boolean checkIfUpdateAvailable = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        LocalDate lstUpdDate = null;
        ResultSet rsLastUpdDate = null;
        String query = "SELECT CDC_NUM, LAST_CHG_DATE FROM PLM_CDC WHERE CDC_NUM = ? ";
        String cdcNumber = searchCriteria.getCDCNumber().toUpperCase();

        long dateInMillis = Calendar.getInstance().getTimeInMillis();
        try {
            conn = PLMDatabaseUtil.getConnection();
            logger.warn("[" + dateInMillis + "] connection retrieved");
        } catch (SQLException e) {
            logger.error(PLMUtil.getStackTrace(e));
            throw new DataException(PLMConstants.ERR_DATABASE_ERROR);
        }

        if (cdcNumber != null && cdcNumber.trim().length() > 0) {
            try {
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, cdcNumber);
                rsLastUpdDate = pstmt.executeQuery();
                while (rsLastUpdDate.next()) {
                    lstUpdDate = rsLastUpdDate.getDate(2).toLocalDate();
                }
                logger.debug("data - check: " + String.valueOf(searchCriteria.getLastUpdateDateObj()));
                logger.debug("data - with:  " + (lstUpdDate != null ? lstUpdDate.toString() : "null"));
            } catch (SQLException e) {
                logger.error(PLMUtil.getStackTrace(e));
                throw new DataException(PLMConstants.ERR_DATABASE_ERROR);
            } finally {
                try {
                    if (rsLastUpdDate != null) {
                        rsLastUpdDate.close();
                        rsLastUpdDate = null;
                    }
                    if (pstmt != null) {
                        pstmt.close();
                        pstmt = null;
                    }
                    if (conn != null) {
                        conn.close();
                        conn = null;
                        logger.warn("[" + dateInMillis + "] connection released");
                    }
                } catch (SQLException sqle) {
                    logger.error(PLMUtil.getStackTrace(sqle));
                    throw new DataException(PLMConstants.ERR_DATABASE_ERROR);
                }
            }
        }
        if (lstUpdDate != null) {
            if (searchCriteria.getLastUpdateDateObj().isBefore(lstUpdDate)) {
                checkIfUpdateAvailable = true;
            }
        }
        return checkIfUpdateAvailable;
    }

    private boolean checkIfUpdateAvailableForPhoto(SearchCriteriaInfo searchCriteria) throws DataException {
        boolean checkIfUpdateAvailable = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        LocalDate lstUpdDate = null;
        ResultSet rsLastUpdDate = null;
        String cdcNumber = searchCriteria.getCDCNumber().toUpperCase();
        //Date date = Calendar.getInstance().getTime();
        long dateInMillis = Calendar.getInstance().getTimeInMillis();
        try {
            conn = PLMDatabaseUtil.getConnection();
            logger.warn("[" + dateInMillis + "] connection retrieved");
        } catch (SQLException e) {
            logger.error(PLMUtil.getStackTrace(e));
            throw new DataException(PLMConstants.ERR_DATABASE_ERROR);
        }
        String query = "SELECT CDC_NUM, DT FROM (SELECT CDC_NUM, CASE WHEN i.update_date IS NULL THEN i.INSERT_date WHEN i.update_date IS NOT NULL THEN CASE WHEN i.insert_date >= i.update_date THEN i.insert_date ELSE i.update_date END END AS dt, ROW_NUMBER() OVER (PARTITION BY cdc_num ORDER BY INSERT_DATE DESC) AS ROWNUMBER FROM CPOWNER.IMAGE_INFO I WHERE I.CDC_NUM IN (?) AND I.TYPE=1 AND Upper(I.SUBTYPE) IN ('FULL FACE FRONTAL (PRIMARY MUGSHOT)','FULL FACE FRONTAL WITH GLASSES','FULL FACE FRONTAL WITH HAT', 'FULL FACE FRONTAL WITH SCARF')) WHERE ROWNUMBER = 1";
        if (cdcNumber != null && cdcNumber.trim().length() > 0) {
            try {
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, cdcNumber);
                rsLastUpdDate = pstmt.executeQuery();
                while (rsLastUpdDate.next()) {
                    lstUpdDate = rsLastUpdDate.getDate(2).toLocalDate();
                }
                logger.debug("photo - check: " + String.valueOf(searchCriteria.getLastUpdateDateObj()));
                logger.debug("photo - with:  " + (lstUpdDate != null ? lstUpdDate.toString() : "null"));
            } catch (SQLException e) {
                logger.error(PLMUtil.getStackTrace(e));
                throw new DataException(PLMConstants.ERR_DATABASE_ERROR);
            } finally {
                try {
                    if (rsLastUpdDate != null) {
                        rsLastUpdDate.close();
                        rsLastUpdDate = null;
                    }
                    if (pstmt != null) {
                        pstmt.close();
                        pstmt = null;
                    }
                    if (conn != null) {
                        conn.close();
                        conn = null;
                        logger.warn("[" + dateInMillis + "] connection released");
                    }
                } catch (SQLException sqle) {
                    logger.error(PLMUtil.getStackTrace(sqle));
                    throw new DataException(PLMConstants.ERR_DATABASE_ERROR);
                }
            }
        }
        if (lstUpdDate != null) {
            if (searchCriteria.getLastUpdateDateObj().isBefore(lstUpdDate)) {
                checkIfUpdateAvailable = true;
            }
        }
        return checkIfUpdateAvailable;
    }
}