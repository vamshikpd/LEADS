package com.plm.dataretrival;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.plm.util.FileUtil;
import com.plm.util.PLMConstants;
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
 ----------------------------------------------------------------------------------------------------------------+
 */


/**
 * This provides the Parolee Data depending on the Search Criteria.
 *
 * @author tushardalal
 */
public class ParoleeTextDataProvider {

    private IQueryExecuter eqExecuter = null;
    private IDataFormatter dataFormatter = null;
    private int PAGE_MAX_RECORDS = 10;
    private static long webServiceCounter = 0;
    private static final Logger logger = Logger.getLogger(ParoleeTextDataProvider.class);

    public ParoleeTextDataProvider() {
        init();
    }

    protected void init() {
        PAGE_MAX_RECORDS = getQueryExecuter().getRecordsPerPage();
    }

    public String generateParoleeDataInBackground(SearchCriteriaInfo searchCriteria) throws DataException {
        final SearchCriteriaInfo sCriteria = searchCriteria;
        final String sUserName = searchCriteria.getUsername();
        final IDataFormatter tDataFormatter = getDataFormatter(sUserName, searchCriteria.shouldReturnSingleRecord());
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    FileUtil.deleteErrFiles(sUserName);
                    getParoleeData(sCriteria, tDataFormatter, true);
                } catch (DataException e) {
                    FileUtils.deleteQuietly(tDataFormatter.getUserInProgressFile());
                    FileUtil.createErrorFile(tDataFormatter.getUserErrorFileNameWithPath(), e.getErrorCode());
                }
            }
        };
        t.start();
        return tDataFormatter.getUserZipFileNameWithPath();
    }

    public String getParoleeData(SearchCriteriaInfo searchCriteria) throws DataException {
        IDataFormatter tDataFormatter = getDataFormatter(searchCriteria.getUsername(), searchCriteria.shouldReturnSingleRecord());
        return getParoleeData(searchCriteria, tDataFormatter, false);
    }

    private String getParoleeData(SearchCriteriaInfo searchCriteria, IDataFormatter tDataFormatter, boolean shouldCreateInProgressFile) throws DataException {
        webServiceCounter++;
        boolean dataExtractionSuccessful = true;
        String dataFileName = null;
        IQueryExecuter tEqExecuter = getQueryExecuter();
        int processCount = 0;
        DataSet dataSet;
        Set<String> stCDCNumber = new TreeSet<String>();
        long startTime = System.currentTimeMillis();
        String logPrefix = searchCriteria.getUsername() + " | ";
        logger.warn(logPrefix + " data download start");

        try {
            if (shouldCreateInProgressFile) {
                FileUtil.createInprogressFile(tDataFormatter.getUserInProgressFileNameWithPath(), searchCriteria);
            }
        } catch (IOException e) {
            logger.error(PLMUtil.getStackTrace(e));
            throw new DataException(PLMConstants.ERR_INPROGRESS_FILE_CREATE);
        }

        dataSet = tEqExecuter.fetchData(searchCriteria, 0);

        long totalRecords = tEqExecuter.getTotalRecords();
        logger.warn(logPrefix + " totalRecords: " + totalRecords);
        if (dataSet != null) {
            if (dataSet.getRecordSize() > 0) {
                tDataFormatter.beginProcess();
                if (tEqExecuter.isPaginationEnabled()) {
                    if (totalRecords <= PAGE_MAX_RECORDS) {
                        try {
                            processCount = tDataFormatter.formatDataSet(dataSet, stCDCNumber, searchCriteria.getResponseFieldsReturned());
                        } catch (IOException iex) {
                            logger.error(PLMUtil.getStackTrace(iex));
                            throw new DataException(PLMConstants.ERR_WRITE_DATA_FILE);
                        }
                        if (processCount == 0) {
                            throw new DataException(PLMConstants.ERR_SEARCH_ENGINE);
                        }
                        //Set the Success Status
                        if (processCount != dataSet.getRecordSize()) {
                            dataExtractionSuccessful = false;
                        }

                        logger.debug(logPrefix + " Records written " + processCount);

                    } else {
                        try {
                            processCount = tDataFormatter.formatDataSet(dataSet, stCDCNumber, searchCriteria.getResponseFieldsReturned());
                        } catch (IOException iex) {
                            logger.error(PLMUtil.getStackTrace(iex));
                            throw new DataException(PLMConstants.ERR_WRITE_DATA_FILE);
                        }

                        logger.debug(logPrefix + " Records written " + processCount);

                        long lReadCounter = dataSet.getRecordSize();
                        if (lReadCounter == processCount) {
                            while (lReadCounter < totalRecords) {
                                dataSet = tEqExecuter.fetchData(searchCriteria, lReadCounter);
                                try {
                                    processCount = tDataFormatter.formatDataSet(dataSet, stCDCNumber, searchCriteria.getResponseFieldsReturned());
                                } catch (IOException iex) {
                                    logger.error(PLMUtil.getStackTrace(iex));
                                    throw new DataException(PLMConstants.ERR_WRITE_DATA_FILE);
                                }
                                if (processCount != dataSet.getRecordSize()) {
                                    dataExtractionSuccessful = false;
                                    break;
                                }
                                lReadCounter += processCount;
                                logger.debug(logPrefix + " Records written " + lReadCounter);
                            }
                        } else {
                            //Error in formatting data.
                            //Could not process all data.
                            dataExtractionSuccessful = false;
                            throw new DataException(PLMConstants.ERR_FORMAT_DATA);
                        }
                    }
                } else {
                    //There is no pagination.
                    //All Data is coming in Single SHOT.
                    try {
                        processCount = tDataFormatter.formatDataSet(dataSet, stCDCNumber, searchCriteria.getResponseFieldsReturned());
                    } catch (IOException iex) {
                        logger.error(PLMUtil.getStackTrace(iex));
                        throw new DataException(PLMConstants.ERR_WRITE_DATA_FILE);
                    }

                    //Set the Success Status
                    if (processCount != dataSet.getRecordSize()) {
                        dataExtractionSuccessful = false;
                    }
                }
                tDataFormatter.endProcess();
            } else {
                throw new DataException(PLMConstants.ERR_NO_DATA_FOUND);
            }
        } else {
            throw new DataException(PLMConstants.ERR_SEARCH_ENGINE);
        }

        if (dataExtractionSuccessful) {
            dataFileName = tDataFormatter.getUserFolderNameWithPath();
            boolean fileCreated = false;
            try {
                if (!searchCriteria.shouldReturnSingleRecord()) {
                    fileCreated = FileUtil.createZipFileFromFolder(tDataFormatter.getUserTempZipFileNameWithPath(), tDataFormatter.getUserFolderNameWithPath(), "");
                }
            } catch (IOException e) {
                logger.error(PLMUtil.getStackTrace(e));
                throw new DataException(PLMConstants.ERR_ZIP_FILE_CREATE);
            }
            if (fileCreated) {
                FileUtils.deleteQuietly(tDataFormatter.getUserZipFile());
                try {
                    FileUtils.copyFile(tDataFormatter.getUserTempZipFile(), tDataFormatter.getUserZipFile());
                } catch (IOException e) {
                    logger.error(PLMUtil.getStackTrace(e));
                    throw new DataException(PLMConstants.ERR_ZIP_FILE_RENAME);
                }
                FileUtils.deleteQuietly(tDataFormatter.getUserTempZipFile());
                FileUtils.deleteQuietly(tDataFormatter.getUserFolder());
                dataFileName = tDataFormatter.getUserZipFileNameWithPath();
            } else if (searchCriteria.shouldReturnSingleRecord()) {
                dataFileName = tDataFormatter.getUserXMLFileNameWithPath();
            } else {
                dataFileName = null;
            }
        } else {
            dataFileName = tDataFormatter.getUserFolderNameWithPath();
            FileUtils.deleteQuietly(tDataFormatter.getUserFolder());
        }

        // 2018-05-30 emil: disable audit in this web service. All audits are done in LEADSAuthService
        //=================================================================================================
        // NOTE
        // Currently irrespective of the data is returned the WebService is Auditing request in database.
        // If Audit is required only in case of actual data is returned then change the following condition accordingly.
        ////logger.warn(searchCriteria.getUsername() + ": audit start");
        // boolean auditStatus = false;
        // auditStatus = auditResults(searchCriteria, stCDCNumber);

        // if(!auditStatus) {
        // 	FileUtils.deleteQuietly(new File(tDataFormatter.getUserZipFileNameWithPath()));
        // 	throw new DataException(PLMConstants.ERR_AUDIT_UPDATE_FAIL, dataFileName);
        // }
        // logger.warn(searchCriteria.getUsername() + ": audit end");
        //=================================================================================================

        // Delete in-progress file.
        if (shouldCreateInProgressFile) {
            FileUtils.deleteQuietly(tDataFormatter.getUserInProgressFile());
        }

        if (processCount == 0) {
            throw new DataException(PLMConstants.ERR_NO_DATA_FOUND, dataFileName);
        }
        long endTime = System.currentTimeMillis();
        logger.warn(logPrefix + "  data download end | " + (endTime - startTime) + " ms.");
        return dataFileName;
    }

    protected IDataFormatter getDataFormatter(String username, boolean singleParolee) throws DataException {
        if (singleParolee) {
            if (dataFormatter == null) {
                dataFormatter = new XMLDataFormatter(username);
            }
        } else {
            if (dataFormatter == null) {
                dataFormatter = new CSVDataFormatter(username);
            }
        }
        return dataFormatter;
    }

    protected IQueryExecuter getQueryExecuter() {
        if (eqExecuter == null) {
            eqExecuter = getNewQueryExecuter();
        }
        return eqExecuter;
    }

    protected IQueryExecuter getNewQueryExecuter() {
        return new EndecaQueryExecuter();
    }

    public String toString() {
        return "ParoleeTextDataProvider:\n" +
                ", eqExecuter = " + eqExecuter + "\n" +
                ", dataFormatter = " + dataFormatter + "\n" +
                ", PAGE_MAX_RECORDS = " + PAGE_MAX_RECORDS + "\n" +
                ", webServiceCounter = " + webServiceCounter + "\n";
    }


	/*
	public boolean auditResults(SearchCriteriaInfo searchCriteria, Set<String> stCDCNumbers) {
		boolean auditStatus = false;
		auditStatus = DataAuditor.insertCoriQuery(searchCriteria.getUsername(),
					searchCriteria.getIpAddress(), searchCriteria.getCaseNumber(), 
					searchCriteria.getReason(), PLMConstants.SEARCH_TYPE);

		return auditStatus;
		
	} */

}
