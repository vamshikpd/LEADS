package com.plm.dataretrival;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import com.plm.dataretrival.cfg.ConfigDataProvider;
import com.plm.dataretrival.cfg.WSConfig;
import com.plm.util.FileUtil;
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
 ----------------------------------------------------------------------------------------------------------------+
 */


/**
 * This provides the Parolee Data depending on the Search Criteria.
 *
 * @author tushardalal
 */
public class ParoleePhotoProvider {

    private final Logger logger = Logger.getLogger(ParoleePhotoProvider.class);
    private String folderOutputPath = "";
    private String userErrorFileName = "";
    private String userFileName = "";
    private String userInProgressFileName = "";
    private String username = null;
    private String userFolderName = "";
    private String userTempZipFileName = "";
    private String userZipFileName = "";
    private EndecaQueryExecuter endecaQueryExecuter = null;
    private ClassPathResource cpr = null;

    public ParoleePhotoProvider(String userName) throws DataException {
        username = userName;
        init();
    }

    protected void init() throws DataException {
        WSConfig wsCfg = ConfigDataProvider.getInstance().getWSConfig();
        folderOutputPath = wsCfg.getFileOutputPath();
        File outputFolder = new File(folderOutputPath);
        if (!outputFolder.exists()) {
            boolean bFolderCreated = outputFolder.mkdir();
            if (!bFolderCreated) {
                throw new DataException(PLMConstants.ERR_OUTPUT_FOLDER_CREATE, folderOutputPath);
            }
        }

        userFileName = getParoleePhotoFilename(username);
        userFolderName = userFileName;
        File makeFolder = new File(getUserFolderNameWithPath());
        if (!makeFolder.exists()) {
            makeFolder.mkdir();
        } else {
            try {
                FileUtils.cleanDirectory(makeFolder);
            } catch (IOException e) {
                logger.error(PLMUtil.getStackTrace(e));
                throw new DataException(PLMConstants.ERR_USER_FOLDER_DELETE, getUserFolderNameWithPath());
            }
        }
        userErrorFileName = userFileName + "." + PLMConstants.FILENAME_EXT_ERROR;
        userInProgressFileName = userFileName + "." + PLMConstants.FILENAME_EXT_INPROGRESS;
        userTempZipFileName = userFileName + PLMConstants.FILENAME_TMP_EXT_ZIP + "." + PLMConstants.FILENAME_EXT_ZIP;
        userZipFileName = userFileName + "." + PLMConstants.FILENAME_EXT_ZIP;
        cpr = new ClassPathResource(PLMConstants.DEFAULT_PHOTO_IMAGE_PATH);

        //Check if any process is already running for the user passed
        //or
        //Check if any process is hung for the user passed
        boolean fileExists = FileUtil.fileExists(userInProgressFileName);
        if (fileExists) {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.HOUR_OF_DAY, -1);
            boolean processHung = now.before(FileUtil.getModifiedTimeStamp(userInProgressFileName));
            if (processHung) {
                throw new DataException(PLMConstants.ERR_USERNAME_INPROGRESS_FILE_EXISTS_HANG, folderOutputPath);
            } else {
                throw new DataException(PLMConstants.ERR_USERNAME_INPROGRESS_FILE_EXISTS, folderOutputPath);
            }

        }
    }

    public String getErrorFilename(String username) {
        return getUserErrorFileNameWithPath();
    }

    public String getParoleePhoto(SearchCriteriaInfo searchCriteria) throws DataException {
        boolean photoExtractionSuccessful = false;
        String errorCode = null;
        String photoFileName = getUserFileNameWithPath();
        PreparedStatement pstmt = null;
        ResultSet rsPhotos = null;
        int recordCount = 0;
        boolean isSingleImageDownload = false;
        String singleImageDownloadFile = null;
        Connection conn = null;
        Set<String> stCDCNumber = new TreeSet<String>();
        String query = null;
        String cdcNumber = null;
        int count = 0;
        long startTime = System.currentTimeMillis();
        String logPrefix = searchCriteria.getUsername() + " | ";
        logger.warn(logPrefix + " photo download start");

        try {
            FileUtil.createInprogressFile(getUserInProgressFileNameWithPath(), searchCriteria);
        } catch (IOException e) {
            logger.error(PLMUtil.getStackTrace(e));
            throw new DataException(PLMConstants.ERR_INPROGRESS_FILE_CREATE);
        }
        if (searchCriteria.getCDCNumber() != null && searchCriteria.getCDCNumber().trim().length() > 0) {
            cdcNumber = searchCriteria.getCDCNumber().toUpperCase();
            count = getCountByCDCNumber(cdcNumber);
            query = getPhotoQuery(count);
        }

        if (cdcNumber != null && cdcNumber.trim().length() > 0) {
            isSingleImageDownload = true;
            int processCount = 0;
            String imageFileName = getUserFolderNameWithPath() + File.separator + cdcNumber + ".jpeg";
            //Date date = Calendar.getInstance().getTime();
//            long dateInMillis = Calendar.getInstance().getTimeInMillis();
            try {
                conn = PLMDatabaseUtil.getConnection();
                logger.warn(logPrefix + " connection retrieved");
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, cdcNumber);
                rsPhotos = pstmt.executeQuery();
                while (rsPhotos.next()) {
                    Blob blobData = rsPhotos.getBlob(2);
                    writeBlobToFile(imageFileName, blobData);
                    blobData.free();
                    String cdcNum = rsPhotos.getString(1);
                    stCDCNumber.add(cdcNum);
                    processCount++;
                }
            } catch (SQLException e) {
                logger.error(PLMUtil.getStackTrace(e));
                throw new DataException(PLMConstants.ERR_DATABASE_ERROR);
            } catch (IOException e) {
                logger.error(PLMUtil.getStackTrace(e));
                throw new DataException(PLMConstants.ERR_WRITE_PHOTO_FILE);
            } finally {
                try {
                    if (rsPhotos != null) {
                        rsPhotos.close();
                        rsPhotos = null;
                    }
                    if (pstmt != null) {
                        pstmt.close();
                        pstmt = null;
                    }
                } catch (SQLException e) {
                    logger.error(PLMUtil.getStackTrace(e));
                    throw new DataException(PLMConstants.ERR_DATABASE_ERROR);
                } finally {
                    try {
                        if (conn != null) {
                            conn.close();
                            conn = null;
//                            dateInMillis = Calendar.getInstance().getTimeInMillis();
                            logger.warn(logPrefix + " connection released");
                        }
                    } catch (SQLException e) {
                        logger.error(PLMUtil.getStackTrace(e));
                        throw new DataException(PLMConstants.ERR_DATABASE_ERROR);
                    }
                }
            }
            if (processCount == 0) {
                try {
                    writeInputStreamToFile(imageFileName);
                } catch (IOException e) {
                    logger.error(PLMUtil.getStackTrace(e));
                    throw new DataException(PLMConstants.ERR_UNKNOWN_PHOTO_CREATE);
                }
            }
            recordCount++;
            singleImageDownloadFile = imageFileName;
            photoExtractionSuccessful = true;
        } else {

            String cdcNumbers[] = getCdcNumbersForSearchCriteria(searchCriteria);
            if (cdcNumbers != null && cdcNumbers.length > 0) {
                for (String j : cdcNumbers) {
                    String[] tmpCDCNumbers = j.split("\\Q,\\E");

                    //Date date = Calendar.getInstance().getTime();
//                    long dateInMillis = Calendar.getInstance().getTimeInMillis();

                    try {
                        conn = PLMDatabaseUtil.getConnection();
                        //logger.warn("[" + dateInMillis + "] connection retrieved");
                        for (String s : tmpCDCNumbers) {
                            int processCount = 0;
                            String imageFileName = getUserFolderNameWithPath() + File.separator + s + ".jpeg";
                            int cnt = getCountByCDCNumber(s);
                            String query1 = getPhotoQuery(cnt);
                            pstmt = conn.prepareStatement(query1);
                            pstmt.setString(1, s);
                            rsPhotos = pstmt.executeQuery();
                            while (rsPhotos.next()) {
                                Blob blobData = rsPhotos.getBlob(2);
                                writeBlobToFile(imageFileName, blobData);
                                blobData.free();
                                processCount++;
                                String cdcNum = rsPhotos.getString(1);
                                stCDCNumber.add(cdcNum);
                            }
                            pstmt.clearParameters();
                            if (processCount == 0) {
                                try {
                                    writeInputStreamToFile(imageFileName);
                                } catch (IOException e) {
                                    logger.error(PLMUtil.getStackTrace(e));
                                    throw new DataException(PLMConstants.ERR_UNKNOWN_PHOTO_CREATE);
                                }
                            }
                            recordCount++;
                        }
                    } catch (SQLException e) {
                        logger.error(PLMUtil.getStackTrace(e));
                        throw new DataException(PLMConstants.ERR_DATABASE_ERROR);
                    } catch (IOException e) {
                        logger.error(PLMUtil.getStackTrace(e));
                        throw new DataException(PLMConstants.ERR_WRITE_PHOTO_FILE);
                    } finally {
                        try {
                            if (rsPhotos != null) {
                                rsPhotos.close();
                                rsPhotos = null;
                            }
                            if (pstmt != null) {
                                pstmt.close();
                                pstmt = null;
                            }
                        } catch (SQLException e) {
                            logger.error(PLMUtil.getStackTrace(e));
                            throw new DataException(PLMConstants.ERR_DATABASE_ERROR);
                        } finally {
                            try {
                                if (conn != null) {
                                    conn.close();
                                    //	logger.warn("[" + dateInMillis + "] connection released");
                                    conn = null;
                                }
                            } catch (SQLException e) {
                                logger.error(PLMUtil.getStackTrace(e));
                                throw new DataException(PLMConstants.ERR_DATABASE_ERROR);
                            }
                        }
                    }
                }// End for loop of CdcNumbers array.
            } else {
                throw new DataException(PLMConstants.ERR_NO_DATA_FOUND);
            }
            photoExtractionSuccessful = true;
        }

        //If all data is processed without Error then Create the Zip file and send the Zip file name.
        //In case failure delete the intermediate XML file and return the filename as a null value.
        if (photoExtractionSuccessful) {
            if (isSingleImageDownload) {
                if (singleImageDownloadFile != null) {
                    photoFileName = singleImageDownloadFile;
                }
            } else {
                FileUtils.deleteQuietly(getUserErrorFile());
                boolean fileCreated;
                //try {
                try {
                    fileCreated = FileUtil.createZipFileFromFolder(getUserTempZipFileNameWithPath(), getUserFolderNameWithPath(), "");
                } catch (IOException e) {
                    logger.error(PLMUtil.getStackTrace(e));
                    throw new DataException(PLMConstants.ERR_ZIP_FILE_CREATE);
                }
                if (fileCreated) {
                    FileUtils.deleteQuietly(getUserZipFile());
                    try {
                        FileUtils.copyFile(getUserTempZipFile(), getUserZipFile());
                    } catch (IOException e) {
                        logger.error(PLMUtil.getStackTrace(e));
                        throw new DataException(PLMConstants.ERR_ZIP_FILE_RENAME);
                    }
                    FileUtils.deleteQuietly(getUserTempZipFile());
                    FileUtils.deleteQuietly(getUserFolder());
                    photoFileName = getUserZipFileNameWithPath();
                } else {
                    photoFileName = null;
                }
            }
        } else {
            if (!isSingleImageDownload) {
                photoFileName = getUserFolderNameWithPath();
                FileUtils.deleteQuietly(getUserFolder());
            }
        }
        // Delete in-progress file.
        FileUtils.deleteQuietly(getUserInProgressFile());

        // 2018-05-30 emil: disable audit in this web service. All audits are done in LEADSAuthService
        //=================================================================================================
//		logger.warn(searchCriteria.getUsername() + ": audit start");
//		boolean auditStatus = false;
//		auditStatus = auditResults(searchCriteria, stCDCNumber);
//
//		if(!auditStatus) {
//			FileUtils.deleteQuietly(new File(photoFileName));
//			throw new DataException(PLMConstants.ERR_AUDIT_UPDATE_FAIL, photoFileName);
//		}
//		logger.warn(searchCriteria.getUsername() + ": audit end");
        //=================================================================================================

        if (errorCode == null && recordCount == 0) {
            errorCode = PLMConstants.ERR_NO_DATA_FOUND;
        }
        if (errorCode != null) {
            throw new DataException(errorCode, photoFileName);
        }

        long endTime = System.currentTimeMillis();
        logger.warn(logPrefix + " photo download end | " + (endTime - startTime) + " ms.");

        return photoFileName;
    }

    public boolean checkIfProcessInProgress1(String inProgressFilename) {

        return true;
    }

    public String getUserErrorFileNameWithPath() {
        return folderOutputPath + File.separator + userErrorFileName;
    }

    public File getUserErrorFile() {
        return new File(getUserErrorFileNameWithPath());
    }

    public String getUserInProgressFileNameWithPath() {
        return folderOutputPath + File.separator + userInProgressFileName;
    }

    public File getUserInProgressFile() {
        return new File(getUserInProgressFileNameWithPath());
    }

    public String getUserFolderNameWithPath() {
        return folderOutputPath + File.separator + userFolderName;
    }

    public File getUserFolder() {
        return new File(getUserFolderNameWithPath());
    }

    public String getUserTempZipFileNameWithPath() {
        return folderOutputPath + File.separator + userTempZipFileName;
    }

    public File getUserTempZipFile() {
        return new File(getUserTempZipFileNameWithPath());
    }

    public String getUserZipFileNameWithPath() {
        return folderOutputPath + File.separator + userZipFileName;
    }

    public File getUserZipFile() {
        return new File(getUserZipFileNameWithPath());
    }

    public String getUserFileNameWithPath() {
        return folderOutputPath + File.separator + userFileName;
    }

    private void writeBlobToFile(String fileName, Blob blobData) throws SQLException, IOException {

		/*InputStream is = blobData.getBinaryStream();
        FileOutputStream fos = new FileOutputStream(fileName);
        byte[] data = new byte[1024];
        int i = 0;
        while ((i = is.read(data)) != -1) {
        	fos.write(data, 0, i);
        }
        fos.close();*/

        int blobLength = (int) blobData.length();
        byte[] blobAsBytes = blobData.getBytes(1, blobLength);

        Path path = Paths.get(fileName);
        Files.write(path, blobAsBytes); //creates, overwrites

    }

    private void writeInputStreamToFile(String fileName) throws IOException {
        InputStream is = cpr.getInputStream();
        FileOutputStream fos = new FileOutputStream(fileName);
        byte[] data = new byte[1024];
        int i = 0;
        while ((i = is.read(data)) > 0) {
            fos.write(data, 0, i);
        }
        is.close();
        fos.close();
    }

    public static String getParoleePhotoFilename(String username) {
        ConfigDataProvider cdProvider = ConfigDataProvider.getInstance();
        String filename = cdProvider.getWSConfig().getParoleePhotoFilename();
        return FileUtil.generateOutputFilename(filename, username);
    }

    private String[] getCdcNumbersForSearchCriteria(SearchCriteriaInfo searchCriteria) throws DataException {
        if (endecaQueryExecuter == null) {
            endecaQueryExecuter = new EndecaQueryExecuter();
        }
        return endecaQueryExecuter.getCdcNumbersForSearchCriteria(searchCriteria);
    }


    /*
    private boolean auditResults(SearchCriteriaInfo scInfo, Set<String> stCDCNumbers) {

        boolean auditStatus = false;
        auditStatus = DataAuditor.insertCoriQuery(scInfo.getUsername(), scInfo.getIpAddress(),
                scInfo.getCaseNumber(), scInfo.getReason(), PLMConstants.SEARCH_TYPE);
        return auditStatus;
    } */

    private int getCountByCDCNumber(String cdcNumber) throws DataException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rsPhotos = null;
        int count = 0;

        // count the records from Nist table
        String countQuery = "SELECT COUNT(*) FROM NIST WHERE ID IN (SELECT ID FROM (SELECT ID, ROW_NUMBER() OVER (ORDER BY Decode(I.SUBTYPE, 'Full Face Frontal (Primary Mugshot)', 1,2) ASC, INSERT_DATE desc) AS ROWNUMBER FROM CPOWNER.IMAGE_INFO I WHERE I.CDC_NUM IN (?) AND I.TYPE=1 AND I.SUBTYPE IN ('Full Face Frontal (Primary Mugshot)','Full Face Frontal with Glasses','Full Face Frontal with Hat','Full Face Frontal with Scarf','Full Face Frontal (Non-NIST)') AND I.ID IN (SELECT ID FROM CPOWNER.THUMBNAILS WHERE ID=I.ID)) WHERE ROWNUMBER = 1)";

        try {
            conn = PLMDatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(countQuery);
            pstmt.setString(1, cdcNumber.toUpperCase());
            rsPhotos = pstmt.executeQuery();
            if (rsPhotos.next()) {
                count = rsPhotos.getInt(1);
            }
        } catch (SQLException e) {
            logger.error(PLMUtil.getStackTrace(e));
            throw new DataException(PLMConstants.ERR_DATABASE_ERROR);
        } finally {
            try {
                if (rsPhotos != null) {
                    rsPhotos.close();
                    rsPhotos = null;
                }
                if (pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                }
            } catch (SQLException e) {
                logger.error(PLMUtil.getStackTrace(e));
                throw new DataException(PLMConstants.ERR_DATABASE_ERROR);
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                        conn = null;
                    }
                } catch (SQLException e) {
                    logger.error(PLMUtil.getStackTrace(e));
                    throw new DataException(PLMConstants.ERR_DATABASE_ERROR);
                }
            }
        }
        return count;
    }

    private String getPhotoQuery(int count) {
        String query = null;
        if (count == 0) {
            query = "SELECT /*+ push_pred(PHOTOS) */CDC_NUM, IMAGE, ID FROM PHOTOS WHERE ID IN (SELECT ID FROM (SELECT ID, ROW_NUMBER() OVER (ORDER BY Decode(I.SUBTYPE, 'Full Face Frontal (Primary Mugshot)', 1,2) ASC, INSERT_DATE desc) AS ROWNUMBER FROM CPOWNER.IMAGE_INFO I WHERE I.CDC_NUM IN (?) AND I.TYPE=1 AND I.SUBTYPE IN ('Full Face Frontal (Primary Mugshot)','Full Face Frontal with Glasses','Full Face Frontal with Hat','Full Face Frontal with Scarf','Full Face Frontal (Non-NIST)') AND I.ID IN (SELECT ID FROM CPOWNER.THUMBNAILS WHERE ID=I.ID)) WHERE ROWNUMBER = 1)";
        } else {
            query = "SELECT CDC_NUM, IMAGE, ID FROM NIST WHERE ID IN (SELECT ID FROM (SELECT ID, ROW_NUMBER() OVER (ORDER BY Decode(I.SUBTYPE, 'Full Face Frontal (Primary Mugshot)', 1,2) ASC, INSERT_DATE desc) AS ROWNUMBER FROM CPOWNER.IMAGE_INFO I WHERE I.CDC_NUM IN (?) AND I.TYPE=1 AND I.SUBTYPE IN ('Full Face Frontal (Primary Mugshot)','Full Face Frontal with Glasses','Full Face Frontal with Hat','Full Face Frontal with Scarf','Full Face Frontal (Non-NIST)') AND I.ID IN (SELECT ID FROM CPOWNER.THUMBNAILS WHERE ID=I.ID)) WHERE ROWNUMBER = 1)";
        }
        return query;
    }
}
