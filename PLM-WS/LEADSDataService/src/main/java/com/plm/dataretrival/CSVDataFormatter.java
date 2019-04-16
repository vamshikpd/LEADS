package com.plm.dataretrival;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import com.endeca.navigation.PropertyContainer;
import com.endeca.navigation.PropertyMap;
import com.plm.dataretrival.cfg.ConfigDataProvider;
import com.plm.dataretrival.cfg.WSConfig;
import com.plm.util.FileUtil;
import com.plm.util.PLMConstants;
import com.plm.util.PLMUtil;


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


public class CSVDataFormatter implements IDataFormatter {

    private String userFileName = "";
    private String userInProgressFileName = "";
    private String userErrorFileName = "";
    private String userZipFileName = "";
    private String userTempZipFileName = "";
    private String folderOutputPath = "";

    private String tmpFileFolderName = "TMP";
    private String username = null;
    private static final String FIELD_SEPARATOR = ",";
    public static final String INNER_FIELD_SEPARATOR_REGEX = "\\" + (char) 176;
    public static final String QUOTE = "\"";
    private static final Logger logger = Logger.getLogger(CSVDataFormatter.class);

    //	private SimpleDateFormat fromFormat = new SimpleDateFormat("MM/dd/yyyy"); //04/23/1975
//	private SimpleDateFormat toFormat = new SimpleDateFormat("yyyyMMdd"); //YYYYMMDD
    private static final DateTimeFormatter FROM_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TO_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private StringBuffer sbTmpRecord = new StringBuffer();
    private StringBuffer sbParoleeRecord = new StringBuffer();
    private int cntParoleeRecords = 0;
    private CSVFileWriter csvFileWriter = null;

    public CSVDataFormatter(String userName) throws DataException {
        this.username = userName;
        init();
    }

    public void init() throws DataException {
        WSConfig wsCfg = ConfigDataProvider.getInstance().getWSConfig();
        folderOutputPath = wsCfg.getFileOutputPath();
        File outputFolder = new File(folderOutputPath);
        if (!outputFolder.exists()) {
            boolean folderCreated = outputFolder.mkdir();
            if (!folderCreated) {
                throw new DataException(PLMConstants.ERR_OUTPUT_FOLDER_CREATE);
            }
        }
        userFileName = FileUtil.getParoleeDataFilename(username);
        userInProgressFileName = userFileName + "." + PLMConstants.FILENAME_EXT_INPROGRESS;
        userErrorFileName = userFileName + "." + PLMConstants.FILENAME_EXT_ERROR;
        userZipFileName = userFileName + "." + PLMConstants.FILENAME_EXT_ZIP;
        userTempZipFileName = userFileName + PLMConstants.FILENAME_TMP_EXT + "." + PLMConstants.FILENAME_EXT_ZIP;
        csvFileWriter = new CSVFileWriter(folderOutputPath + File.separator + userFileName);
        sbParoleeRecord = new StringBuffer();
    }

    public void beginProcess() {
        logger.debug("CSVDataFormatted :: beginProcess()");
        csvFileWriter.openFiles();
    }

    public void endProcess() {
        logger.debug("CSVDataFormatted :: endProcess()");
        csvFileWriter.closeFiles();
    }

    /* (non-Javadoc)
     * @see com.plm.dataretrival.IDataFormatter#formatDataSet(com.plm.dataretrival.DataSet)
     */
    public int formatDataSet(DataSet dataSet, Set<String> stCDCNumber, List<String> returnFields) throws IOException {
//		logger.debug("CSVDataFormatter :: formatDataSet()");
        int iRecordProcessCounter = 0;
        Collection<PropertyContainer> collData = dataSet.getEndecaData();
        cntParoleeRecords = collData.size();
        Iterator<PropertyContainer> iteERecs = collData.iterator();
        while (iteERecs.hasNext()) {
            PropertyContainer pc = iteERecs.next();
            PropertyMap pm = pc.getProperties();

            String cdcNumber = (String) pm.get("CDC Number");
            stCDCNumber.add(cdcNumber);
            if (returnFields == null || returnFields.isEmpty()) {
//				logger.debug("Returning All Data Fields  ");
                //Format and Write Parolee Details
//				logger.debug("Writing Parolee Data ...");
                csvFileWriter.writeParolee(getParolee(pm));

                //Format and Write Address Details
//				logger.debug("Writing Parolee Address Data ...");
                csvFileWriter.writeAddress(getAddress(pm));

                //Format and Write Alias Details
//				logger.debug("Writing Parolee Alias Data...");
                csvFileWriter.writeAlias(getAlias(pm));

                //Format and Write Job Details
//				logger.debug("Writing Parolee Job Data...");
                csvFileWriter.writeJob(getJob(pm));

                //Format and Write Moniker Details
//				logger.debug("Writing Parolee Moniker Data...");
                csvFileWriter.writeMoniker(getMoniker(pm));

                //Format and Write Offense Details
//				logger.debug("Writing Parolee Offense Data...");
                csvFileWriter.writeOffense(getOffense(pm));

                //Format and Write SMT Details
//				logger.debug("Writing Parolee SMT Data...");
                csvFileWriter.writeSMT(getSMT(pm));

                //Format and Write Special Condition Details
//				logger.debug("Writing Parolee Special Condition Data...");
                csvFileWriter.writeSpecialCondition(getSpecialCondition(pm));

                //Format and Write Vehicle Details
//				logger.debug("Writing Parolee Vehicle Data...");
                csvFileWriter.writeVehicle(getVehicle(pm));
            } else {
                boolean returnIndividualParFields = false;
                //logger.debug("returnFields Size :: " + returnFields.size());
                for (String parField : returnFields) {
                    //logger.debug("Returning Specific Data Fields :: " + parField);
                    if (parField.startsWith("PAR.")) {
                        returnIndividualParFields = true;
                        break;
                    }
                }
                //logger.debug("returnIndividualParFields :: " + returnIndividualParFields);
                if (returnIndividualParFields && !returnFields.contains(PLMConstants.PAR)) {
                    csvFileWriter.writeParolee(getParolee(pm, returnFields));
                } else {
                    csvFileWriter.writeParolee(getParolee(pm));
                }
                if (returnFields.contains(PLMConstants.ADD)) {
                    csvFileWriter.writeAddress(getAddress(pm));
                }
                if (returnFields.contains(PLMConstants.ALI)) {
                    csvFileWriter.writeAlias(getAlias(pm));
                }
                if (returnFields.contains(PLMConstants.JOB)) {
                    csvFileWriter.writeJob(getJob(pm));
                }
                if (returnFields.contains(PLMConstants.MON)) {
                    csvFileWriter.writeMoniker(getMoniker(pm));
                }
                if (returnFields.contains(PLMConstants.OFF)) {
                    csvFileWriter.writeOffense(getOffense(pm));
                }
                if (returnFields.contains(PLMConstants.SMT)) {
                    csvFileWriter.writeSMT(getSMT(pm));
                }
                if (returnFields.contains(PLMConstants.SPC)) {
                    csvFileWriter.writeSpecialCondition(getSpecialCondition(pm));
                }
                if (returnFields.contains(PLMConstants.VEH)) {
                    csvFileWriter.writeVehicle(getVehicle(pm));
                }
            }
            //Format and Write AB3 CDC Details
            csvFileWriter.writeAb3CDC(getAB3CDC(pm));

            iRecordProcessCounter++;
        }
        return iRecordProcessCounter;
    }

    /**
     * Creates the AB3 CDC Number separated by comma and returns as a String.
     *
     * @param pm
     * @return String - AB3 CDC numbers.
     */
    private String getAB3CDC(PropertyMap pm) {
        sbTmpRecord.replace(0, sbTmpRecord.length(), "");
        sbTmpRecord.append(QUOTE + (String) pm.get("CDC Number") + QUOTE);                                                        //1. CDC Number
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("Parole Last Chg Date"), "") + QUOTE);    //2. Last Change Date
        if (cntParoleeRecords == 1) {
            writeParoleeRecord(sbTmpRecord);
        }
        return sbTmpRecord.toString();
    }

    /**
     * Creates the Job details separated by comma and returns as a String.
     *
     * @param pm
     * @return String - Job Details.
     */
    private String getJob(PropertyMap pm) {
        sbTmpRecord.replace(0, sbTmpRecord.length(), "");
        String sortedJobInfos = (String) pm.get("Sorted Employer Information");
        String[] jobInfos = null;
        if (sortedJobInfos != null) {
            jobInfos = sortedJobInfos.split("@@");
            int cnt = 0;
            while (jobInfos != null && cnt < jobInfos.length) {
                String jobInfo = jobInfos[cnt];
                String[] jobInfo_result = null;
                if (jobInfo != null) {
                    jobInfo_result = jobInfo.split(INNER_FIELD_SEPARATOR_REGEX);
                }
                sbTmpRecord.append(QUOTE + (String) pm.get("CDC Number") + QUOTE);                                //1. CDC Number
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(jobInfo_result[0]) + QUOTE);                //2. Employer
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(jobInfo_result[1]) + QUOTE);                //3. Job Street
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(jobInfo_result[2]) + QUOTE);                //4. Job City Name
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(jobInfo_result[3]) + QUOTE);                //5. Job State Code
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(jobInfo_result[4]) + QUOTE);                //6. Job County Code
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(jobInfo_result[5]) + QUOTE);                //7. Job Zip
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(jobInfo_result[6]) + QUOTE);                //8. Job Zip4
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatFullPhone(jobInfo_result[7]) + QUOTE);        //9. Job full phone
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(jobInfo_result[9]) + QUOTE);                //10. Employer Aware
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD(jobInfo_result[10], "") + QUOTE);    //11. Job Start Date
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Unit Code")) + QUOTE);    //12. Unit Code
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD(jobInfo_result[11], "") + QUOTE);    //13. Job Last Change Date
                cnt++;
                if (cnt < jobInfos.length) {
                    sbTmpRecord.append(CSVFileWriter.NEW_LINE_CHAR);
                }
            }
        }
        if (cntParoleeRecords == 1) {
            writeParoleeRecord(sbTmpRecord);
        }
        return sbTmpRecord.toString();
    }

    /**
     * Creates the Offense details separated by comma and returns as a String.
     *
     * @param pm
     * @return String - Offense Details.
     */
    private String getOffense(PropertyMap pm) {
        sbTmpRecord.replace(0, sbTmpRecord.length(), "");
        Collection<String> collOffenseInfo = pm.getValues("Offense Information");
        if (collOffenseInfo != null) {
            int recCounter = 0;
            int size = collOffenseInfo.size();
            Iterator<String> ite = collOffenseInfo.iterator();
            while (ite.hasNext()) {
                String[] offenseInfo = (ite.next()).split(INNER_FIELD_SEPARATOR_REGEX);
                sbTmpRecord.append(QUOTE + (String) pm.get("CDC Number") + QUOTE);                                //1. CDC Number
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(offenseInfo[0]) + QUOTE);                //2. Offense Code
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(offenseInfo[4]) + QUOTE);                //3. Controlling Offense
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Unit Code")) + QUOTE);    //4. Unit code
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD(offenseInfo[5], "") + QUOTE);    //5. Last change date
                recCounter++;
                if (recCounter < size) {
                    sbTmpRecord.append(CSVFileWriter.NEW_LINE_CHAR);
                }
            }
        }
        if (cntParoleeRecords == 1) {
            writeParoleeRecord(sbTmpRecord);
        }
        return sbTmpRecord.toString();
    }

    /**
     * Creates the SMT details separated by comma and returns as a String.
     *
     * @param pm
     * @return String - SMT details.
     */
    private String getSMT(PropertyMap pm) {
        sbTmpRecord.replace(0, sbTmpRecord.length(), "");
        Collection<String> collSMTInfo = pm.getValues("SMT Information");
        if (collSMTInfo != null) {
            int recCounter = 0;
            int size = collSMTInfo.size();
            Iterator<String> ite = collSMTInfo.iterator();
            while (ite.hasNext()) {
                String str = ite.next() + INNER_FIELD_SEPARATOR_REGEX;
                String[] SMTInfo = str.split(INNER_FIELD_SEPARATOR_REGEX);
                sbTmpRecord.append(QUOTE + (String) pm.get("CDC Number") + QUOTE);                                //1. CDC Number
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(SMTInfo[0]) + QUOTE);                    //2. SMT Code
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(SMTInfo[2]) + QUOTE);                    //3. SMT Picture
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(SMTInfo[3]) + QUOTE);                    //4. SMT Text
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Unit Code")) + QUOTE);    //5. Unit Code
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD(SMTInfo[4], "") + QUOTE);            //6. SMT Last Change Date

                recCounter++;
                if (recCounter < size) {
                    sbTmpRecord.append(CSVFileWriter.NEW_LINE_CHAR);
                }
            }
        }
        if (cntParoleeRecords == 1) {
            writeParoleeRecord(sbTmpRecord);
        }
        return sbTmpRecord.toString();
    }


    /**
     * Creates the Vehicle details separated by comma and returns as a String.
     *
     * @param pm
     * @return String - Vehicle Details.
     */
    private String getVehicle(PropertyMap pm) {
        sbTmpRecord.replace(0, sbTmpRecord.length(), "");
        Collection<String> collVehicleInfo = pm.getValues("Vehicle Information");
        if (collVehicleInfo != null) {
            int recCounter = 0;
            int size = collVehicleInfo.size();
            Iterator<String> ite = collVehicleInfo.iterator();
            while (ite.hasNext()) {
                String[] vehicleInfo = ite.next().split(INNER_FIELD_SEPARATOR_REGEX);
                sbTmpRecord.append(QUOTE + (String) pm.get("CDC Number") + QUOTE);                                //1. CDC Number
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(vehicleInfo[0]) + QUOTE);                //2. Make Code
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(vehicleInfo[2]) + QUOTE);                //3. Model Code
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(vehicleInfo[4]) + QUOTE);                //4. Style Code
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(vehicleInfo[6]) + QUOTE);                //5. Vehicle Class
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(vehicleInfo[7]) + QUOTE);                //6. Vehicle Year
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(vehicleInfo[8]) + QUOTE);                //7. Vehicle Color Code 1
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(vehicleInfo[10]) + QUOTE);                //8. Vehicle Color Code 2
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(vehicleInfo[12]) + QUOTE);                //9. Vehicle LicencePlate
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(vehicleInfo[13]) + QUOTE);                //10. Vehicle State Code
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(vehicleInfo[17]) + QUOTE);                //11. Vehicle Owned.
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Unit Code")) + QUOTE);    //12. Unit Code
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD(vehicleInfo[15], "") + QUOTE);    //13. Vehicle Start Date
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD(vehicleInfo[16], "") + QUOTE); //14. Vehicle LastChangeDate
                recCounter++;
                if (recCounter < size) {
                    sbTmpRecord.append(CSVFileWriter.NEW_LINE_CHAR);
                }
            }
        }
        if (cntParoleeRecords == 1) {
            writeParoleeRecord(sbTmpRecord);
        }
        return sbTmpRecord.toString();
    }

    /**
     * Creates the Moniker details separated by comma and returns as a String.
     *
     * @param pm
     * @return String - Moniker Details.
     */
    private String getMoniker(PropertyMap pm) {
        sbTmpRecord.replace(0, sbTmpRecord.length(), "");
        Collection<String> collMonikerInfo = pm.getValues("Moniker Info");
        if (collMonikerInfo != null) {
            int recCounter = 0;
            int size = collMonikerInfo.size();
            Iterator<String> ite = collMonikerInfo.iterator();
            while (ite.hasNext()) {
                String[] monikerInfo = ite.next().split(INNER_FIELD_SEPARATOR_REGEX);
                sbTmpRecord.append(QUOTE + (String) pm.get("CDC Number") + QUOTE);                                            //1. CDC Number
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(monikerInfo[0]) + QUOTE);                            //2. Moniker Name/Code
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(formatNull((String) pm.get("Unit Code"))) + QUOTE);    //3. Unit Code
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD(monikerInfo[1], "") + QUOTE);                //4. Last Change Date
                recCounter++;
                if (recCounter < size) {
                    sbTmpRecord.append(CSVFileWriter.NEW_LINE_CHAR);
                }
            }
        }
        if (cntParoleeRecords == 1) {
            writeParoleeRecord(sbTmpRecord);
        }
        return sbTmpRecord.toString();
    }

    /**
     * Creates the Special Condition details separated by comma and returns as a String.
     *
     * @param pm
     * @return String - Special Condition Details.
     */
    private String getSpecialCondition(PropertyMap pm) {
        sbTmpRecord.replace(0, sbTmpRecord.length(), "");
        Collection<String> collSPConditionInfo = pm.getValues("Special Condition Information");
        if (collSPConditionInfo != null) {
            int recCounter = 0;
            int size = collSPConditionInfo.size();
            Iterator<String> ite = collSPConditionInfo.iterator();
            while (ite.hasNext()) {
                String[] specialConditionInfo = ite.next().split(INNER_FIELD_SEPARATOR_REGEX);
                String spcCond = "";
                String lstchangeDate = "";
                if (specialConditionInfo.length > 0) {
                    spcCond = specialConditionInfo[0];
                }
                if (specialConditionInfo.length > 1) {
                    lstchangeDate = formatDateYYYYMMDD(specialConditionInfo[1], "");
                }
                sbTmpRecord.append(QUOTE + (String) pm.get("CDC Number") + QUOTE);                                            //1. CDC Number
                //Checking for Unit Code = "PRCS" OR "DAI-" records not to display Special Conditions
                //Variable to hold the current Unit Code
                //Modified 10/7/2011--RD
                String sUnitNm = (String) pm.get("Unit Name") != null ? (String) pm.get("Unit Name") : "";

                // emil 2018-03-07 fix exception in substring call
                String sUnitSubstr = "";
                try {
                    sUnitSubstr = sUnitNm.substring(0, 4);
                } catch (StringIndexOutOfBoundsException e) {
                    // logger.error("StringIndexOutOfBoundsException --> Unit Name= " + sUnitNm);
                }

                if ((sUnitSubstr.equals("PRCS")) || (sUnitSubstr.equals("DAI-"))) {
                    //logger.info("Agent Code true part of the if statement");
                    sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
                } else {
                    //logger.info("Agent Code false part of the if statement");
                    sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(spcCond) + QUOTE);                                    //2. Special Condition
                }
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Unit Code")) + QUOTE);                //3. Unit Code
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + lstchangeDate + QUOTE);                                        //4. Last Change Date.

                recCounter++;
                if (recCounter < size) {
                    sbTmpRecord.append(CSVFileWriter.NEW_LINE_CHAR);
                }
            }
        }
        if (cntParoleeRecords == 1) {
            writeParoleeRecord(sbTmpRecord);
        }
        return sbTmpRecord.toString();
    }

    /**
     * Creates the Parolee Alias details separated by comma and returns as a String.
     *
     * @param pm
     * @return String - Alias Current Address.
     */
    private String getAlias(PropertyMap pm) {
        sbTmpRecord.replace(0, sbTmpRecord.length(), "");
        Collection<String> collAliasInfo = pm.getValues("Alias Info");
        if (collAliasInfo != null) {
            int recCounter = 0;
            int size = collAliasInfo.size();
            Iterator<String> ite = collAliasInfo.iterator();
            while (ite.hasNext()) {
                String[] aliasInfo = ite.next().split(INNER_FIELD_SEPARATOR_REGEX);
                sbTmpRecord.append(QUOTE + (String) pm.get("CDC Number") + QUOTE);                                //1. CDC Number
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(aliasInfo[0]) + QUOTE);                    //2. Last Name
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(aliasInfo[1]) + QUOTE);                    //3. First Name
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(aliasInfo[2]) + QUOTE);                    //4. Middle Name
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Unit Code")) + QUOTE);    //5. Unit Code
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD(aliasInfo[3], "") + QUOTE);    //6. Last Change Date
                recCounter++;
                if (recCounter < size) {
                    sbTmpRecord.append(CSVFileWriter.NEW_LINE_CHAR);
                }
            }
        }
        if (cntParoleeRecords == 1) {
            writeParoleeRecord(sbTmpRecord);
        }
        return sbTmpRecord.toString();
    }

    /**
     * Creates the Parolee Current Address details separated by comma and returns as a String.
     *
     * @param pm
     * @return String - of Parolee Current Address.
     */
    private String getAddress(PropertyMap pm) {
        sbTmpRecord.replace(0, sbTmpRecord.length(), "");
        sbTmpRecord.append(QUOTE + (String) pm.get("CDC Number") + QUOTE);                                            //1. CDC Number
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("Address Effective Date"), "19930101") + QUOTE);    //2. Address Effective Date
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("Care of (live with)")) + QUOTE);        //3. Care of
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("Street")) + QUOTE);                    //4. Street
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("City")) + QUOTE);                    //5. City
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("State Code")) + QUOTE);                //6. State Code
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("County Code")) + QUOTE);                //7. County Code
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Zip")) + QUOTE);                        //8. Zip
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Zip4")) + QUOTE);                        //9. Zip4
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatFullPhone((String) pm.get("Full_Phone")) + QUOTE);            //10. Full Phone
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Map")) + QUOTE);                        //11. Map
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatFullPhone((String) pm.get("Message Phone")) + QUOTE);        //12. Message Phone
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Unit Code")) + QUOTE);                //13. Unit Code
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("Address Last Change Date"), "") + QUOTE);                //14. Last Change Date
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("geocode")) + QUOTE);                //15. Lat/Long
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Resident Status")) + QUOTE);    //16. Resident Status


        String sortedPrevAddress = (String) pm.get("Sorted Prev Address");
        String[] prevAddress = null;
        if (sortedPrevAddress != null) {
            sbTmpRecord.append(CSVFileWriter.NEW_LINE_CHAR);
            prevAddress = sortedPrevAddress.split("@@");
            int cnt = 0;
            while (prevAddress != null && cnt < prevAddress.length) {
                String prevAdd = prevAddress[cnt];
                String[] pAddress_result = null;
                if (prevAdd != null) {
                    pAddress_result = prevAdd.split(INNER_FIELD_SEPARATOR_REGEX);
                }
                sbTmpRecord.append(QUOTE + (String) pm.get("CDC Number") + QUOTE);                                //1. CDC Number
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD(pAddress_result[0], "19930101") + QUOTE);                    //2. Address Effective Date
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(pAddress_result[1]) + QUOTE);            //3. Care of
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(pAddress_result[2]) + QUOTE);            //4. Street
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote(pAddress_result[3]) + QUOTE);            //5. City
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull(pAddress_result[4]) + QUOTE);                //6. State Code
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull(pAddress_result[5]) + QUOTE);                //7. County Code
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull(pAddress_result[7]) + QUOTE);                //8. Zip
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull(pAddress_result[8]) + QUOTE);                //9. Zip4
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatFullPhone(pAddress_result[9]) + QUOTE);        //10. Full Phone
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull(pAddress_result[11]) + QUOTE);            //11. Map
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatFullPhone(pAddress_result[10]) + QUOTE);        //12. Message Phone
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Unit Code")) + QUOTE);    //13. Unit Code
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD(pAddress_result[12], "") + QUOTE);    //14. 						//14. Last Change Date
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + "" + QUOTE);
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + "" + QUOTE);
                cnt++;
                if (cnt < prevAddress.length) {
                    sbTmpRecord.append(CSVFileWriter.NEW_LINE_CHAR);
                }
            }
        }
        if (cntParoleeRecords == 1) {
            writeParoleeRecord(sbTmpRecord);
        }
        return sbTmpRecord.toString();
    }

    private String getParolee(PropertyMap pm) {
        sbTmpRecord.replace(0, sbTmpRecord.length(), "");
        sbTmpRecord.append(QUOTE + (String) pm.get("CDC Number") + QUOTE);                                                    //1. CDC Number
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("Last Name")) + QUOTE);                        //2. Last Name
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("First Name")) + QUOTE);                        //3. First Name
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("Middle Name")) + QUOTE);                        //4. Middle Name
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Unit Code")) + QUOTE);                        //5. Unit Code

        //Checking for Unit Code = "PRCS" OR "DAI-" records not to display Agent Code
        //Variable to hold the current Unit Code
        //Modified 09/27/2011--RD
        String sUnitNm = (String) pm.get("Unit Name") != null ? (String) pm.get("Unit Name") : "";

        // emil 2018-03-07 fix exception in substring call
        String sUnitSubstr = "";
        try {
            sUnitSubstr = sUnitNm.substring(0, 4);
        } catch (StringIndexOutOfBoundsException e) {
            // logger.error("StringIndexOutOfBoundsException --> Unit Name= " + sUnitNm);
        }

        if ((sUnitSubstr.equals("PRCS")) || (sUnitSubstr.equals("DAI-"))) {
            //logger.info("Agent Code true part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
        } else {
            //logger.info("Agent Code false part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Agent Code")) + QUOTE);                    //6. Agent Code
        }

        //Agent Name is coming as FNAME SPACE LNAME - As per existing down-load it is LNAME SPACE FNAME.
        //"Agent Name=Adalbert Ada" NEED "Agent Name=Ada Adalbert" OR SAPERATE PROPERTIES.
        //--- EVEN THOUGH - the LNAME FNAME is done in data - actual data for WS needs LNAME,FNAME so needs two saperate fields.
        //Checking for Unit Code = "PRCS" records not to display Agent Name
        //Modified 09/27/2011--RD
        if ((sUnitSubstr.equals("PRCS")) || (sUnitSubstr.equals("DAI-"))) {
            //logger.info("Agent Name true part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
        } else {
            //logger.info("Agent Name false part of the if statement");
            if (((String) pm.get("Agent Name")).equals(",")) {
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
            } else {
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Agent Name")) + QUOTE);                        //7. Agent Name
            }

        }

        //AGENT PHONE IS REQUIRED but it is not available either in DB or in Endeca.
        //sbParolee.append(FIELD_SEPARATOR+QUOTE+formatPhone( (String)pm.get("Phone"))+QUOTE);	//-- REMOVED FROM PIPELINE-NEED TO ADD IN PIPELINE.
        //NOTE: Putting the Blank field to keep the sequence in CSV -
        //      Here it will search for 'Agent Phone' and as it is not available in Endeca Properties it will get the Null VALUE.
        //Modified the formatting as Agent Phone is coming as full phone e.g. (408)454-3434
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatFullPhone((String) pm.get("Agent Phone")) + QUOTE);                //8. Agent Phone
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Birth Date Search")) + QUOTE);                //9. Birth Date
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatSex((String) pm.get("Sex")) + QUOTE);                                //10. Sex
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + (String) pm.get("Race Cd") + QUOTE);                                        //11. Race Code
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + (String) pm.get("Eyecolor Cd") + QUOTE);                                    //12. Eye Color Code
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatHeight((String) pm.get("Height Feet"), (String) pm.get("Height Inches")) + QUOTE); //13. Height
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatWeight((String) pm.get("P_Weight")) + QUOTE);                        //14. Weight
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Haircolor Cd"), " ", 3) + QUOTE);                //15. Hair Color Code
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("Parole Date Display"), "") + QUOTE);    //16. Parole Date
        //Checking for Unit Code = PRCS OR DAI unit not to display "Classification Code"
        //Modified 09/27/2011--RD
        if ((sUnitSubstr.equals("PRCS")) || (sUnitSubstr.equals("DAI-"))) {
            //logger.info("Classification Code true part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
        } else {
            //logger.info("Classification Code false part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("Classification Code"), " ", 2) + QUOTE);                //17. Classification Code
        }
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("HS REQ"), " ", 1) + QUOTE);                            //18. HS Req
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("PC 290 REQ"), " ", 1) + QUOTE);                        //19. PC 290 Req
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("PC 457 REQ"), " ", 1) + QUOTE);                        //20. PC 457 Req
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("CII Number"), " ", 10) + QUOTE);                        //21. CII Number
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + lpad((String) pm.get("FBI Number"), " ", 10) + QUOTE);                        //22. FBI Number
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + lpad((String) pm.get("SSA Number"), " ", 9) + QUOTE);                        //23. SSA Number
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("Driver License Number"), " ", 10) + QUOTE);            //24. Driver's Licence
        //Checking for Unit Code = "PRCS" OR "DAI-" records not to display Status
        //Modified 09/27/2011--RD
        if ((sUnitSubstr.equals("PRCS")) || (sUnitSubstr.equals("DAI-"))) {
            //logger.info("Status true part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
        } else {
            //logger.info("Status false part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Status")) + QUOTE);                            //25. Status
        }
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("PC 3058 REQ"), " ", 1) + QUOTE);                        //26. PC 3058 Req
        //Checking for Unit Code = "PRCS" OR "DAI-"records not to display "ANT REQ"
        //Modified 09/27/2011--RD
        if ((sUnitSubstr.equals("PRCS")) || (sUnitSubstr.equals("DAI-"))) {
            //logger.info("ANT REQ true part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
        } else {
            //logger.info("ANT REQ false part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("ANT REQ"), " ", 1) + QUOTE);                            //27. Ant Req
        }
        //Checking for Unit Code = "PRCS" OR "DAI-" records not to display "No Alcohol"
        //Modified 09/27/2011--RD
        if ((sUnitSubstr.equals("PRCS")) || (sUnitSubstr.equals("DAI-"))) {
            //logger.info("No Alcohol true part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
        } else {
            //logger.info("No Alcohol false part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNoAlcohol((String) pm.get("No Alcohol")) + QUOTE);                    //28. No Alcohol
        }
        //Checking for Unit Code = "PRCS" OR "DAI-" records not to display "POC REQ"
        //Modified 09/27/2011--RD
        if (sUnitSubstr.equals("PRCS") || (sUnitSubstr.equals("DAI-"))) {
            //logger.info("POC REQ true part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
        } else {
            //logger.info("POC REQ false part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("POC REQ"), " ", 1) + QUOTE);                            //29. POC Req
        }
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("Birth State Code"), " ", 2) + QUOTE);                //30. Birth State Code
        //Checking for Unit Code = "DAI-" records not to display "Comments"
        //Modified 09/27/2011--RD
        if ((sUnitSubstr.equals("DAI-"))) {
            //logger.info("POC REQ true part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
        } else {
            //logger.info("POC REQ false part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("Comments")) + QUOTE);                        //31. Comments
        }
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("Problem Area - Narcotic")) + QUOTE);        //32. Problem Area - Narcotic
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("Problem Area - Alcohol")) + QUOTE);        //33. Problem Area - Alcohol
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("Problem Area - Assault")) + QUOTE);        //34. Problem Area - Assault
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("Problem Area - Sex")) + QUOTE);            //35. Problem Area - Sex
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("Problem Area - Other")) + QUOTE);            //36. Problem Area - Other
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("HS DATE"), "") + QUOTE);            //37. HS Date
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("PC 290 DATE"), "") + QUOTE);        //38. PC 290 Date
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("PC 457 DATE"), "") + QUOTE);        //39. PC 457 Date
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("PC 3058 DATE"), "") + QUOTE);        //40. PC 3058 Date
        //Checking for Unit Code = "PRCS" OR "DAI-" records not to display "Control Discharg Date"
        //Modified 09/27/2011--RD
        if ((sUnitSubstr.equals("PRCS")) || (sUnitSubstr.equals("DAI-"))) {
            //logger.info("Control Discharg Date true part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
        } else {
            //logger.info("Control Discharg Date false part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("Control Discharg Date"), "") + QUOTE);        //41. Control Discharge Date
        }
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("County Commit"), " ", 3) + QUOTE);                    //42. County Commit
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("Discharged Date"), "") + QUOTE);    //43. Discharged Date
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("Parole Last Chg Date"), "") + QUOTE);        //44. Last Change Date
        //Show revocation relese date to PRCS* and NOT to DAI*
        //Modified 09/27/2011--RD
        if ((sUnitSubstr.equals("DAI-"))) {
            //logger.info("Revocation Release Date true part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
        } else {
            //logger.info("Revocation Release Date false part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("Revocation Release Date"), "") + QUOTE);    //45. Revocation Release Date
        }
        //Added COLLR field to download
        //Modified 01/06/2012 RD
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("CountyOfLLR")) + QUOTE);        //46. County of Last Legal Residence
        if (cntParoleeRecords == 1) {
            writeParoleeRecord(sbTmpRecord);
        }
        //Checking for Unit Code = "PRCS" OR "DAI-" records not to display agent e-mail
        //Modified 12/17/2013--RD
        if ((sUnitSubstr.equals("PRCS")) || (sUnitSubstr.equals("DAI-"))) {
            //logger.info("Status true part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
        } else {
            //logger.info("Status false part of the if statement");
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Agent Email")) + QUOTE);                        //47. Agent EMail
        }
        return sbTmpRecord.toString();
    }

    /**
     * Creates the Parolee Details separated by comma and returns as a String.
     *
     * @param pm
     * @return String - of Formatted Parolee Detail
     */

    private String getParolee(PropertyMap pm, List<String> returnParFields) {
        sbTmpRecord.replace(0, sbTmpRecord.length(), "");
        sbTmpRecord.append(QUOTE + (String) pm.get("CDC Number") + QUOTE);                                                    //1. CDC Number
        if (returnParFields.contains(PLMConstants.LAST_NAME)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("Last Name")) + QUOTE);                        //2. Last Name
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.FIRST_NAME)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("First Name")) + QUOTE);                        //3. First Name
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.MIDDLE_NAME)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("Middle Name")) + QUOTE);                        //4. Middle Name
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Unit Code")) + QUOTE);                        //5. Unit Code

        //Checking for Unit Code = "PRCS" OR "DAI-" records not to display Agent Code
        //Variable to hold the current Unit Code
        //Modified 09/27/2011--RD
        String sUnitNm = (String) pm.get("Unit Name") != null ? (String) pm.get("Unit Name") : "";

        // emil 2018-03-08 fix substring exception
        String sUnitSubstr = "";
        try {
            sUnitSubstr = sUnitNm.substring(0, 4);
        } catch (StringIndexOutOfBoundsException e) {
            // logger.error("StringIndexOutOfBoundsException --> sUnitNm = " + sUnitNm);
        }
        if (returnParFields.contains(PLMConstants.AGENT_CODE)) {
            if ((sUnitSubstr.equals("PRCS")) || (sUnitSubstr.equals("DAI-"))) {
                //logger.info("Agent Code true part of the if statement");
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
            } else {
                //logger.info("Agent Code false part of the if statement");
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Agent Code")) + QUOTE);                    //6. Agent Code
            }
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        //Agent Name is coming as FNAME SPACE LNAME - As per existing down-load it is LNAME SPACE FNAME.
        //"Agent Name=Adalbert Ada" NEED "Agent Name=Ada Adalbert" OR SAPERATE PROPERTIES.
        //--- EVEN THOUGH - the LNAME FNAME is done in data - actual data for WS needs LNAME,FNAME so needs two saperate fields.
        //Checking for Unit Code = "PRCS" records not to display Agent Name
        //Modified 09/27/2011--RD
        if (returnParFields.contains(PLMConstants.AGENT_NAME)) {
            if ((sUnitSubstr.equals("PRCS")) || (sUnitSubstr.equals("DAI-"))) {
                //logger.info("Agent Name true part of the if statement");
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
            } else {
                //logger.info("Agent Name false part of the if statement");
                //Added below code for agent name is empty  on 03/06/2017 - vamshi kapidi

                if (((String) pm.get("Agent Name")).equals(",")) {
                    sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
                } else {
                    sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Agent Name")) + QUOTE);                        //7. Agent Name
                }

            }
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        //AGENT PHONE IS REQUIRED but it is not available either in DB or in Endeca.
        //sbParolee.append(FIELD_SEPARATOR+QUOTE+formatPhone( (String)pm.get("Phone"))+QUOTE);	//-- REMOVED FROM PIPELINE-NEED TO ADD IN PIPELINE.
        //NOTE: Putting the Blank field to keep the sequence in CSV -
        //      Here it will search for 'Agent Phone' and as it is not available in Endeca Properties it will get the Null VALUE.
        //Modified the formatting as Agent Phone is coming as full phone e.g. (408)454-3434
        if (returnParFields.contains(PLMConstants.AGENT_PHONE)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatFullPhone((String) pm.get("Agent Phone")) + QUOTE);                //8. Agent Phone
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.BIRTH_DATE)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Birth Date Search")) + QUOTE);                //9. Birth Date
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.SEX)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatSex((String) pm.get("Sex")) + QUOTE);                                //10. Sex
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.RACE_CODE)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + (String) pm.get("Race Cd") + QUOTE);                                        //11. Race Code
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.EYE_COLOR_CODE)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + (String) pm.get("Eyecolor Cd") + QUOTE);                                    //12. Eye Color Code
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.HEIGHT)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatHeight((String) pm.get("Height Feet"), (String) pm.get("Height Inches")) + QUOTE); //13. Height
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.WEIGHT)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatWeight((String) pm.get("P_Weight")) + QUOTE);                        //14. Weight
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.HAIR_COLOR_CODE)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Haircolor Cd"), " ", 3) + QUOTE);                //15. Hair Color Code
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.PAROLE_DATE)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("Parole Date Display"), "") + QUOTE);    //16. Parole Date
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        //Checking for Unit Code = PRCS OR DAI unit not to display "Classification Code"
        //Modified 09/27/2011--RD
        if (returnParFields.contains(PLMConstants.CLASSIFICATION_CODE)) {
            if ((sUnitSubstr.equals("PRCS")) || (sUnitSubstr.equals("DAI-"))) {
                //logger.info("Classification Code true part of the if statement");
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
            } else {
                //logger.info("Classification Code false part of the if statement");
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("Classification Code"), " ", 2) + QUOTE);                //17. Classification Code
            }
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.HS_REQ)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("HS REQ"), " ", 1) + QUOTE);                            //18. HS Req
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.PC_290_REQ)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("PC 290 REQ"), " ", 1) + QUOTE);                        //19. PC 290 Req
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.PC_457_REQ)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("PC 457 REQ"), " ", 1) + QUOTE);                        //20. PC 457 Req
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.CII_NUMBER)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("CII Number"), " ", 10) + QUOTE);                        //21. CII Number
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.FBI_NUMBER)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + lpad((String) pm.get("FBI Number"), " ", 10) + QUOTE);                        //22. FBI Number
        }
        if (returnParFields.contains(PLMConstants.SSA_NUMBER)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + lpad((String) pm.get("SSA Number"), " ", 9) + QUOTE);                        //23. SSA Number
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.DRIVER_LICENCE)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("Driver License Number"), " ", 10) + QUOTE);            //24. Driver's Licence
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        //Checking for Unit Code = "PRCS" OR "DAI-" records not to display Status
        //Modified 09/27/2011--RD
        if (returnParFields.contains(PLMConstants.STATUS)) {
            if ((sUnitSubstr.equals("PRCS")) || (sUnitSubstr.equals("DAI-"))) {
                //logger.info("Status true part of the if statement");
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
            } else {
                //logger.info("Status false part of the if statement");
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Status")) + QUOTE);                            //25. Status
            }
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.PC_3058_REQ)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("PC 3058 REQ"), " ", 1) + QUOTE);                        //26. PC 3058 Req
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        //Checking for Unit Code = "PRCS" OR "DAI-"records not to display "ANT REQ"
        //Modified 09/27/2011--RD
        if (returnParFields.contains(PLMConstants.ANT_REQ)) {
            if ((sUnitSubstr.equals("PRCS")) || (sUnitSubstr.equals("DAI-"))) {
                //logger.info("ANT REQ true part of the if statement");
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
            } else {
                //logger.info("ANT REQ false part of the if statement");
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("ANT REQ"), " ", 1) + QUOTE);                            //27. Ant Req
            }
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        //Checking for Unit Code = "PRCS" OR "DAI-" records not to display "No Alcohol"
        //Modified 09/27/2011--RD
        if (returnParFields.contains(PLMConstants.NO_ALCOHOL)) {
            if ((sUnitSubstr.equals("PRCS")) || (sUnitSubstr.equals("DAI-"))) {
                //logger.info("No Alcohol true part of the if statement");
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
            } else {
                //logger.info("No Alcohol false part of the if statement");
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNoAlcohol((String) pm.get("No Alcohol")) + QUOTE);                    //28. No Alcohol
            }
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        //Checking for Unit Code = "PRCS" OR "DAI-" records not to display "POC REQ"
        //Modified 09/27/2011--RD
        if (returnParFields.contains(PLMConstants.POC_REQ)) {
            if ((sUnitSubstr.equals("PRCS")) || (sUnitSubstr.equals("DAI-"))) {
                //logger.info("POC REQ true part of the if statement");
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
            } else {
                //logger.info("POC REQ false part of the if statement");
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("POC REQ"), " ", 1) + QUOTE);                            //29. POC Req
            }
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.BIRTH_STATE_CODE)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("Birth State Code"), " ", 2) + QUOTE);                //30. Birth State Code
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        //Checking for Unit Code = "DAI-" records not to display "Comments"
        //Modified 09/27/2011--RD
        if (returnParFields.contains(PLMConstants.COMMENTS)) {
            if ((sUnitSubstr.equals("DAI-"))) {
                //logger.info("POC REQ true part of the if statement");
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
            } else {
                //logger.info("POC REQ false part of the if statement");
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("Comments")) + QUOTE);                        //31. Comments
            }
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.PROBLEM_AREA_NARCOTIC)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("Problem Area - Narcotic")) + QUOTE);        //32. Problem Area - Narcotic
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.PROBLEM_AREA_ALCOHOL)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("Problem Area - Alcohol")) + QUOTE);        //33. Problem Area - Alcohol
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.PROBLEM_AREA_ASSAULT)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("Problem Area - Assault")) + QUOTE);        //34. Problem Area - Assault
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.PROBLEM_AREA_SEX)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("Problem Area - Sex")) + QUOTE);            //35. Problem Area - Sex
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.PROBLEM_AREA_OTHER)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("Problem Area - Other")) + QUOTE);            //36. Problem Area - Other
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.HS_DATE)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("HS DATE"), "") + QUOTE);            //37. HS Date
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.PC_290_DATE)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("PC 290 DATE"), "") + QUOTE);        //38. PC 290 Date
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.PC_457_DATE)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("PC 457 DATE"), "") + QUOTE);        //39. PC 457 Date
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.PC_3058_DATE)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("PC 3058 DATE"), "") + QUOTE);        //40. PC 3058 Date
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        //Checking for Unit Code = "PRCS" OR "DAI-" records not to display "Control Discharg Date"
        //Modified 09/27/2011--RD
        if (returnParFields.contains(PLMConstants.CONTROL_DISCHARGE_DATE)) {
            if ((sUnitSubstr.equals("PRCS")) || (sUnitSubstr.equals("DAI-"))) {
                //logger.info("Control Discharg Date true part of the if statement");
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
            } else {
                //logger.info("Control Discharg Date false part of the if statement");
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("Control Discharg Date"), "") + QUOTE);        //41. Control Discharge Date
            }
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.COUNTY_COMMIT)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + rpad((String) pm.get("County Commit"), " ", 3) + QUOTE);                    //42. County Commit
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        if (returnParFields.contains(PLMConstants.DISCHARGED_DATE)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("Discharged Date"), "") + QUOTE);    //43. Discharged Date
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("Parole Last Chg Date"), "") + QUOTE);        //44. Last Change Date

        //Show revocation relese date to PRCS* and NOT to DAI*
        //Modified 09/27/2011--RD
        if (returnParFields.contains(PLMConstants.REVOCATION_RELEASE_DATE)) {
            if ((sUnitSubstr.equals("DAI-"))) {
                //logger.info("Revocation Release Date true part of the if statement");
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) ("")) + QUOTE);
            } else {
                //logger.info("Revocation Release Date false part of the if statement");
                sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatDateYYYYMMDD((String) pm.get("Revocation Release Date"), "") + QUOTE);    //45. Revocation Release Date
            }
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        //Added COLLR field to download
        //Modified 01/06/2012 RD
        if (returnParFields.contains(PLMConstants.COLLR)) {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatQuote((String) pm.get("CountyOfLLR")) + QUOTE);        //46. County of Last Legal Residence
        } else {
            sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + QUOTE);
        }
        sbTmpRecord.append(FIELD_SEPARATOR + QUOTE + formatNull((String) pm.get("Agent Email")) + QUOTE);                        //47. Agent Email

        if (cntParoleeRecords == 1) {
            writeParoleeRecord(sbTmpRecord);

        }
        return sbTmpRecord.toString();
    }

    //--------Util Methods----------------

    private String formatNull(String src) {
        if (src == null) {
            return "";
        }
        return src;
    }

    private String formatQuote(String srcStr) {
        if (srcStr != null && srcStr.length() > 0) {
            return srcStr.replace('"', '\'');
        }
        return "";
    }

	/*private String formatSMTText(String srcStr) {
		if(srcStr != null) {
			return srcStr.replace('"', '~');
		}
		return "";
	}*/

    private String formatFullPhone(String srcPhone) {
        if (srcPhone == null || srcPhone.trim().length() == 0) {
            return "(   )   -    ";
        }
        return srcPhone;
    }

    private String formatDateYYYYMMDD(String date, String defaultDate) {
        try {
            if (date != null && date.trim().length() > 0) {
//				Date dt = fromFormat.parse(date);
//				return toFormat.format(dt);
                LocalDate dt = LocalDate.parse(date, FROM_FORMAT);
                return dt.format(TO_FORMAT);
            }
//		} catch (ParseException e) {
        } catch (DateTimeParseException e) {
            logger.error(PLMUtil.getStackTrace(e));
        }
        return defaultDate;
    }

    private String formatHeight(String heightFeet, String heightInches) {
        StringBuffer sbHeight = new StringBuffer();
        if (heightFeet == null || heightFeet.trim().length() == 0) {
            sbHeight.append(" ");
        } else {
            sbHeight.append(heightFeet.trim());
        }
        if (heightInches == null || heightInches.trim().length() == 0) {
            sbHeight.append("00");
        } else {
            String inch = "00" + heightInches.trim();
            sbHeight.append(inch.substring(inch.length() - 2));
        }
        return sbHeight.toString();
    }

    private String formatWeight(String weight) {
        String retWeight = null;
        if (weight == null) {
            retWeight = "   ";
        } else {
            String tmp = "   " + weight.trim();
            retWeight = tmp.substring(tmp.length() - 3);
        }
        return retWeight;
    }

    private String formatNull(String src, String repeatChar, int size) {
        if (src == null) {
            return getString(repeatChar, size);
        }
        return src;
    }

    private String formatNoAlcohol(String src) {
        if (src == null || src.trim().equals("N")) {
            return "N";
        }
        return "Y";
    }

    private String rpad(String src, String repeatChar, int size) {
        if (src == null) {
            return getString(repeatChar, size);
        }
        return (src.trim() + getString(repeatChar, size)).substring(0, size);
    }

    private String lpad(String src, String repeatChar, int size) {
        if (src == null) {
            return getString(repeatChar, size);
        }
        return (getString(repeatChar, size - src.trim().length()) + src.trim());
    }

    //MGM-Created method to return single character values for Sex
    private String formatSex(String srcSex) {
        String sex = "";
        if (srcSex == null) {
            sex = "";
        } else if (srcSex.equals("MALE")) {
            sex = "M";
        } else if (srcSex.equals("FEMALE")) {
            sex = "F";
        }
        return sex;
    }

    private String getString(String charToRepeat, int length) {
        StringBuffer sbRet = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sbRet.append(charToRepeat);
        }
        return sbRet.toString();
    }

    /* (non-Javadoc)
     * @see com.plm.dataretrival.IDataFormatter#getOutputFile()
     */
    public String getOutputFile() {
        return tmpFileFolderName;//zipFileNameOfCSVFiles;
    }

    public String getUserFileName() {
        return userFileName;
    }

    public String getUserInProgressFileName() {
        return userInProgressFileName;
    }

    public String getUserErrorFileName() {
        return userErrorFileName;
    }

    public String getUserZipFileName() {
        return userZipFileName;
    }

    public String getUserFolderNameWithPath() {
        return folderOutputPath + File.separator + userFileName;
    }

    public String getUserInProgressFileNameWithPath() {
        return folderOutputPath + File.separator + userInProgressFileName;
    }

    public String getUserErrorFileNameWithPath() {
        return folderOutputPath + File.separator + userErrorFileName;
    }

    public String getUserZipFileNameWithPath() {
        return folderOutputPath + File.separator + userZipFileName;
    }

    public String getUserXMLFileNameWithPath() {
        return "";
    }

    public String getUserTempZipFileNameWithPath() {
        return folderOutputPath + File.separator + userTempZipFileName;
    }

    private void writeParoleeRecord(StringBuffer sbTmpParoleeRecord) {
        sbParoleeRecord.append(sbTmpParoleeRecord.toString());
        sbParoleeRecord.append("::");
    }

    @Override
    public File getUserErrorFile() {
        return new File(getUserErrorFileNameWithPath());
    }

    @Override
    public File getUserFolder() {
        return new File(getUserFolderNameWithPath());
    }

    @Override
    public File getUserInProgressFile() {
        return new File(getUserInProgressFileNameWithPath());
    }

    @Override
    public File getUserTempZipFile() {
        return new File(getUserTempZipFileNameWithPath());
    }

    @Override
    public File getUserZipFile() {
        return new File(getUserZipFileNameWithPath());
    }
}
