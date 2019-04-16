package com.plm.dataretrival;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import plm.ws.mtom.paroleedata.Address;
import plm.ws.mtom.paroleedata.Addresses;
import plm.ws.mtom.paroleedata.Alias;
import plm.ws.mtom.paroleedata.Aliases;
import plm.ws.mtom.paroleedata.CommitmentOffense;
import plm.ws.mtom.paroleedata.CommitmentOffenses;
import plm.ws.mtom.paroleedata.Identity;
import plm.ws.mtom.paroleedata.Job;
import plm.ws.mtom.paroleedata.Jobs;
import plm.ws.mtom.paroleedata.Moniker;
import plm.ws.mtom.paroleedata.Monikers;
import plm.ws.mtom.paroleedata.OtherSpecialCondition;
import plm.ws.mtom.paroleedata.OtherSpecialConditions;
import plm.ws.mtom.paroleedata.Parolee;
import plm.ws.mtom.paroleedata.ParoleeDataSet;
import plm.ws.mtom.paroleedata.RegistrationNotice;
import plm.ws.mtom.paroleedata.Smt;
import plm.ws.mtom.paroleedata.Smts;
import plm.ws.mtom.paroleedata.SpecialConditions;
import plm.ws.mtom.paroleedata.Vehicle;
import plm.ws.mtom.paroleedata.Vehicles;

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


public class XMLDataFormatter implements IDataFormatter {

    private String userFileName = "";
    private String userInProgressFileName = "";
    private String userErrorFileName = "";
    private String userXMLFileName = "";
    private String userTempXMLFileName = "";
    private String folderOutputPath = "";

    private String tmpFileFolderName = "TMP";
    private String username = null;
    public static final String FIELD_SEPARATOR = ",";
    public static final String INNER_FIELD_SEPARATOR_REGEX = "\\" + (char) 176;
    public static final String QUOTE = "\"";
    private static final Logger logger = Logger.getLogger(XMLDataFormatter.class);

    //	private SimpleDateFormat fromFormat = new SimpleDateFormat("MM/dd/yyyy"); //04/23/1975
    //	private SimpleDateFormat toFormat = new SimpleDateFormat("yyyyMMdd"); //YYYYMMDD
    private static final DateTimeFormatter FROM_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy"); //04/23/1975
    private static final DateTimeFormatter TO_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd"); //YYYYMMDD
    private StringBuffer sbTmpRecord = new StringBuffer();
    private XMLFileWriter xmlFileWriter = null;

    public XMLDataFormatter(String userName) throws DataException {
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
        userXMLFileName = userFileName + "." + PLMConstants.FILENAME_EXT_XML;
        userTempXMLFileName = userFileName + PLMConstants.FILENAME_TMP_EXT + "." + PLMConstants.FILENAME_EXT_XML;
        xmlFileWriter = new XMLFileWriter(folderOutputPath + File.separator + userXMLFileName);
    }

    public void beginProcess() {
    }

    public void endProcess() {
    }

    /* (non-Javadoc)
     * @see com.plm.dataretrival.IDataFormatter#formatDataSet(com.plm.dataretrival.DataSet)
     */
    public int formatDataSet(DataSet dataSet, Set<String> stCDCNumber, List<String> returnFields) throws IOException {
        int iRecordProcessCounter = 0;

        Collection<PropertyContainer> collData = dataSet.getEndecaData();
        Iterator<PropertyContainer> iteERecs = collData.iterator();
        ParoleeDataSet paroleeDataSet = new ParoleeDataSet();
        List<Parolee> paroleeList = new ArrayList<Parolee>();
        String cdcNum = null;
        while (iteERecs.hasNext()) {
            PropertyContainer pc = iteERecs.next();
            PropertyMap pm = pc.getProperties();
            cdcNum = (String) pm.get("CDC Number");
            stCDCNumber.add(cdcNum);
            //Format Parolee Details
            Parolee parolee = getParolee(pm);

            //Add Identity Details to Parolee
            Identity identity = getIdentity(pm);
            parolee.setIdentity(identity);

            //Add Registration Notice Details to Parolee
            RegistrationNotice registrationNotice = getRegistrationNotice(pm);
            parolee.setRegistrationNotice(registrationNotice);
            //registration-notice

            //Add Special Condition Details to Parolee
            SpecialConditions specialConditions = getSpecialConditions(pm);
            parolee.setSpecialConditions(specialConditions);

            //Add Addresses to Parolee
            Addresses addresses = new Addresses();
            List<Address> addressList = getAddresses(pm);
            addresses.getAddress().addAll(addressList);
            parolee.setAddresses(addresses);

            //Add SMT Details to Parolee
            Smts smts = new Smts();
            List<Smt> smtList = getSMTs(pm);
            smts.getSmt().addAll(smtList);
            parolee.setSmts(smts);

            //Add Alias Details to Parolee
            Aliases aliases = new Aliases();
            List<Alias> aliasList = getAliases(pm);
            aliases.getAlias().addAll(aliasList);
            parolee.setAliases(aliases);

            //Add Moniker Details to Parolee
            Monikers monikers = new Monikers();
            List<Moniker> monikerList = getMonikers(pm);
            monikers.getMoniker().addAll(monikerList);
            parolee.setMonikers(monikers);

            //Add Offense Details to Parolee
            CommitmentOffenses commitmentOffenses = new CommitmentOffenses();
            List<CommitmentOffense> commitmentOffenseList = getOffenses(pm);
            commitmentOffenses.getCommitmentOffense().addAll(commitmentOffenseList);
            parolee.setCommitmentOffenses(commitmentOffenses);

            //Add Job Details to Parolee
            Jobs jobs = new Jobs();
            List<Job> jobList = getJobs(pm);
            jobs.getJob().addAll(jobList);
            parolee.setJobs(jobs);

            //Add Vehicle Details to Parolee
            Vehicles vehicles = new Vehicles();
            List<Vehicle> vehicleList = getVehicles(pm);
            vehicles.getVehicle().addAll(vehicleList);
            parolee.setVehicles(vehicles);

            OtherSpecialConditions otherSpecialConditions = new OtherSpecialConditions();
            List<OtherSpecialCondition> otherSpecialConditionList = getOtherSpecialConditions(pm);
            otherSpecialConditions.getOtherSpecialCondition().addAll(otherSpecialConditionList);
            parolee.setOtherSpecialConditions(otherSpecialConditions);

            paroleeList.add(parolee);


            iRecordProcessCounter++;
        }

        paroleeDataSet.getParolee().addAll(paroleeList);
        xmlFileWriter.writeParoleeData(paroleeDataSet);
        return iRecordProcessCounter;
    }

    /**
     * Creates the Parolee Details separated by comma and returns as a String.
     *
     * @param pm
     * @return String - of Formatted Parolee Detail
     */
    private Parolee getParolee(PropertyMap pm) {
        Parolee parolee = new Parolee();
        parolee.setComments(formatQuote((String) pm.get("Comments")));
        return parolee;
    }

    private Identity getIdentity(PropertyMap pm) {
        Identity identity = new Identity();
        identity.setAgentEmail(formatNull((String) pm.get("Agent Email")));
        //Added below code for agent name is empty  on 03/06/2017 - vamshi kapidi
        if (((String) pm.get("Agent Name")).equals(",")) {
            identity.setAgentName(formatNull((String) ("")));
        } else {
            identity.setAgentName(formatNull((String) pm.get("Agent Name")));
        }

        identity.setAgentPhone(formatFullPhone((String) pm.get("Agent Phone")));
        identity.setBirthDate(formatNull((String) pm.get("Birth Date Display")));
        identity.setBirthState(formatNull((String) pm.get("Birth State Name")));
        identity.setCdcNum((String) pm.get("CDC Number"));
        identity.setCiiNum((String) pm.get("CII Number"));
        identity.setCommitmentCounty((String) pm.get("County Commit"));
        identity.setDriverLicenseNum((String) pm.get("Driver License Number"));
        identity.setEthnicity((String) pm.get("Race"));
        identity.setEyeColor((String) pm.get("Eyecolor"));
        identity.setFbiNum((String) pm.get("FBI Number"));
        identity.setFirstName((String) pm.get("First Name"));
        identity.setGender(formatSex((String) pm.get("Sex")));
        identity.setHairColor((String) pm.get("Haircolor"));
        identity.setHeight(formatHeight((String) pm.get("Height Feet"), (String) pm.get("Height Inches")));
        identity.setLastName((String) pm.get("Last Name"));
        identity.setMaxDischargeDate(formatDateYYYYMMDD((String) pm.get("Control Discharg Date"), ""));
        identity.setMiddleName((String) pm.get("Middle Name"));
        identity.setParoleDate(formatDateYYYYMMDD((String) pm.get("Parole Date Display"), ""));
        identity.setParoleStatus(formatNull((String) pm.get("Status")));
        identity.setParoleUnit(formatNull((String) pm.get("Unit Name")));
        identity.setRevocationReleaseDate(formatDateYYYYMMDD((String) pm.get("Revocation Release Date"), ""));
        identity.setSsaNum((String) pm.get("SSA Number"));
        identity.setSupervisionLevel((String) pm.get("Classification Description"));
        identity.setWeight(formatWeight((String) pm.get("P_Weight")));
        return identity;
    }


    private RegistrationNotice getRegistrationNotice(PropertyMap pm) {
        RegistrationNotice registrationNotice = new RegistrationNotice();
        registrationNotice.setHs11590Flag((String) pm.get("HS REQ"));
        registrationNotice.setHs11590RegDate(formatDateYYYYMMDD((String) pm.get("HS DATE"), ""));
        registrationNotice.setPc290Flag((String) pm.get("PC 290 REQ"));
        registrationNotice.setPc290RegDate(formatDateYYYYMMDD((String) pm.get("PC 290 DATE"), ""));
        registrationNotice.setPc30586Flag((String) pm.get("PC 3058 REQ"));
        registrationNotice.setPc30586RegDate(formatDateYYYYMMDD((String) pm.get("PC 3058 DATE"), ""));
        registrationNotice.setPc4571Flag((String) pm.get("PC 457 REQ"));
        registrationNotice.setPc4571RegDate(formatDateYYYYMMDD((String) pm.get("PC 457 DATE"), ""));

        return registrationNotice;
    }

    /**
     * Creates the Parolee Current Address details.
     *
     * @param pm
     * @return String - of Parolee Current Address.
     */
    private List<Address> getAddresses(PropertyMap pm) {
        List<Address> addressList = new ArrayList<Address>();
        Address address = new Address();
        address.setActiveDate(formatDateYYYYMMDD((String) pm.get("Address Effective Date"), "19930101"));
        address.setCareOf(formatQuote((String) pm.get("Care of (live with)")));
        address.setStreet(formatQuote((String) pm.get("Street")));
        address.setCity(formatQuote((String) pm.get("City")));
        address.setState(formatNull((String) pm.get("State Code")));
        address.setCounty(formatNull((String) pm.get("County Code")));
        address.setZip(formatNull((String) pm.get("Zip")));
        address.setZip4(formatNull((String) pm.get("Zip4")));
        address.setPhone(formatFullPhone((String) pm.get("Full_Phone")));
        address.setMapRef(formatNull((String) pm.get("Map")));
        address.setMsgPhone(formatFullPhone((String) pm.get("Message Phone")));
        address.setGeoCodes(formatNull((String) pm.get("geocode")));
        address.setResidentStatus(formatNull((String) pm.get("Resident Status")));
        addressList.add(address);

        String sortedPrevAddress = (String) pm.get("Sorted Prev Address");
        String[] prevAddress = null;
        if (sortedPrevAddress != null) {
            prevAddress = sortedPrevAddress.split("@@");
            int cnt = 0;
            while (prevAddress != null && cnt < prevAddress.length) {
                String prevAdd = prevAddress[cnt];
                String[] pAddress_result = null;
                if (prevAdd != null) {
                    pAddress_result = prevAdd.split(INNER_FIELD_SEPARATOR_REGEX);
                }
                Address objPrevAddress = new Address();
                objPrevAddress.setActiveDate(formatDateYYYYMMDD(pAddress_result[0], "19930101"));
                objPrevAddress.setCareOf(formatQuote(pAddress_result[1]));
                objPrevAddress.setStreet(formatQuote(pAddress_result[2]));
                objPrevAddress.setCity(formatQuote(pAddress_result[3]));
                objPrevAddress.setState(formatNull(pAddress_result[4]));
                objPrevAddress.setCounty(formatNull(pAddress_result[5]));
                objPrevAddress.setZip(formatNull(pAddress_result[7]));
                objPrevAddress.setZip4(formatNull(pAddress_result[8]));
                objPrevAddress.setMapRef(formatNull(pAddress_result[11]));
                objPrevAddress.setMsgPhone(formatFullPhone(pAddress_result[10]));
                addressList.add(objPrevAddress);

                cnt++;
            }
        }
        return addressList;
    }

    /**
     * Creates the Job details separated by comma and returns as a String.
     *
     * @param pm
     * @return String - Job Details.
     */
    private List<Job> getJobs(PropertyMap pm) {
        List<Job> jobList = new ArrayList<Job>();
        String sortedJobInfos = (String) pm.get("Sorted Employer Information");
        String[] jobInfos = null;
        if (sortedJobInfos != null) {
            jobInfos = sortedJobInfos.split("@@");
            int cnt = 0;
            while (jobInfos != null && cnt < jobInfos.length) {
                Job job = new Job();
                String jobInfo = jobInfos[cnt];
                String[] jobInfo_result = null;
                if (jobInfo != null) {
                    jobInfo_result = jobInfo.split(INNER_FIELD_SEPARATOR_REGEX);
                }
                job.setEmployer(formatQuote(jobInfo_result[0]));
                job.setStreet(formatQuote(jobInfo_result[1]));
                job.setCity(formatQuote(jobInfo_result[2]));
                job.setState(jobInfo_result[3]);
                job.setZip(jobInfo_result[5]);
                job.setPhone(formatFullPhone(jobInfo_result[7]));
                job.setAwareFlag(jobInfo_result[9]);
                job.setStartDate(formatDateYYYYMMDD(jobInfo_result[10], ""));
                job.setUnit(formatNull((String) pm.get("Unit Code")));

                jobList.add(job);
                cnt++;
            }
        }
        return jobList;
    }

    /**
     * Creates the Offense details separated by comma and returns as a String.
     *
     * @param pm
     * @return String - Offense Details.
     */
    private List<CommitmentOffense> getOffenses(PropertyMap pm) {
        List<CommitmentOffense> commitmentOffenseList = new ArrayList<CommitmentOffense>();
        Collection collOffenseInfo = pm.getValues("Offense Information");
        if (collOffenseInfo != null) {
//			int size = collOffenseInfo.size();
            Iterator ite = collOffenseInfo.iterator();
            while (ite.hasNext()) {
                String[] offenseInfo = ((String) ite.next()).split(INNER_FIELD_SEPARATOR_REGEX);
                CommitmentOffense commitmentOffense = new CommitmentOffense();
                commitmentOffense.setCode(offenseInfo[0]); //0. Offense Code
                commitmentOffense.setDesc(formatQuote(offenseInfo[1])); //1. Description
                commitmentOffense.setControlOffenseFlag(offenseInfo[4]); //4. Controlling Offense
                commitmentOffenseList.add(commitmentOffense);
            }

        }
        return commitmentOffenseList;
    }

    /**
     * Creates the SMT details separated by comma and returns as a String.
     *
     * @param pm
     * @return String - SMT details.
     */
    private List<Smt> getSMTs(PropertyMap pm) {
        List<Smt> smtList = new ArrayList<Smt>();
        sbTmpRecord.replace(0, sbTmpRecord.length(), "");
        Collection<String> collSMTInfo = pm.getValues("SMT Information");
        if (collSMTInfo != null) {
            Iterator<String> ite = collSMTInfo.iterator();
            while (ite.hasNext()) {
                Smt smt = new Smt();
                String[] SMTInfo = ite.next().split(INNER_FIELD_SEPARATOR_REGEX);

                if (SMTInfo.length > 0)
                    smt.setLoc(formatQuote(SMTInfo[0])); // 0. SMT Code
                if (SMTInfo.length > 2)
                    smt.setPicText(formatQuote(SMTInfo[2])); // 2. SMT Picture
                if (SMTInfo.length > 3)
                    smt.setSmtText(formatQuote(SMTInfo[3])); // 3. SMT Text
                smtList.add(smt);
            }
        }
        return smtList;
    }


    /**
     * Creates the Vehicle details separated by comma and returns as a String.
     *
     * @param pm
     * @return String - Vehicle Details.
     */
    private List<Vehicle> getVehicles(PropertyMap pm) {
        List<Vehicle> vehicleList = new ArrayList<Vehicle>();

        Collection collVehicleInfo = pm.getValues("Vehicle Information");
        if (collVehicleInfo != null) {
            int size = collVehicleInfo.size();
            Iterator ite = collVehicleInfo.iterator();
            while (ite.hasNext()) {
                Vehicle vehicle = new Vehicle();
                String[] vehicleInfo = ((String) ite.next()).split(INNER_FIELD_SEPARATOR_REGEX);

                vehicle.setMake(vehicleInfo[1]);
                vehicle.setModel(vehicleInfo[3]);
                vehicle.setStyle(vehicleInfo[5]);
                vehicle.setClazz(vehicleInfo[6]);
                vehicle.setYear(vehicleInfo[7]);
                vehicle.setColor1(vehicleInfo[9]);
                vehicle.setColor2(vehicleInfo[11]);
                vehicle.setLicensePlate(vehicleInfo[12]);
                vehicle.setState(vehicleInfo[13]);
                vehicle.setOwnedFlag(vehicleInfo[17]);
                vehicle.setStartDate(vehicleInfo[15]);
                vehicle.setUnit((String) pm.get("Unit Code"));

                vehicleList.add(vehicle);

            }
        }
        return vehicleList;
    }

    private SpecialConditions getSpecialConditions(PropertyMap pm) {

        SpecialConditions specialConditions = new SpecialConditions();
        specialConditions.setDrugTestFlag((String) pm.get("ANT REQ") != null ? (String) pm.get("ANT REQ") : "");
        specialConditions.setNoAlcFlag((String) pm.get("No Alcohol") != null ? (String) pm.get("No Alcohol") : "");
        specialConditions.setPsychOutpatientClinicFlag((String) pm.get("POC REQ") != null ? (String) pm.get("POC REQ") : "");

        return specialConditions;
    }

    /**
     * Creates the Moniker details separated by comma and returns as a String.
     *
     * @param pm
     * @return String - Moniker Details.
     */
    private List<Moniker> getMonikers(PropertyMap pm) {
        List<Moniker> monikerList = new ArrayList<Moniker>();
        Collection collMonikerInfo = pm.getValues("Moniker Info");
        if (collMonikerInfo != null) {
            int size = collMonikerInfo.size();
            Iterator ite = collMonikerInfo.iterator();
            while (ite.hasNext()) {
                Moniker moniker = new Moniker();
                String[] monikerInfo = ((String) ite.next()).split(INNER_FIELD_SEPARATOR_REGEX);
                moniker.setDesc(formatQuote(monikerInfo[0]));

                monikerList.add(moniker);
            }
        }
        return monikerList;
    }

    /**
     * Creates the Other Special Condition details separated by comma and returns as a String.
     *
     * @param pm
     * @return String - Other Special Condition Details.
     */
    private List<OtherSpecialCondition> getOtherSpecialConditions(PropertyMap pm) {
        List<OtherSpecialCondition> otherSpecialConditionList = new ArrayList<OtherSpecialCondition>();
        sbTmpRecord.replace(0, sbTmpRecord.length(), "");
        Collection collSPConditionInfo = pm.getValues("Special Condition Information");
        if (collSPConditionInfo != null) {
            int size = collSPConditionInfo.size();
            Iterator ite = collSPConditionInfo.iterator();
            while (ite.hasNext()) {
                OtherSpecialCondition otherSpecialCondition = new OtherSpecialCondition();
                String[] specialConditionInfo = ((String) ite.next()).split(INNER_FIELD_SEPARATOR_REGEX);
                if (specialConditionInfo.length > 0) {
                    otherSpecialCondition.setSpecialComment(specialConditionInfo[0]); // 0. Special Condition
                }
                otherSpecialConditionList.add(otherSpecialCondition);

            }
        }
        return otherSpecialConditionList;
    }

    /**
     * Creates the Parolee Alias details separated by comma and returns as a String.
     *
     * @param pm
     * @return String - Alias Current Address.
     */
    private List<Alias> getAliases(PropertyMap pm) {
        List<Alias> aliasList = new ArrayList<Alias>();

        Collection collAliasInfo = pm.getValues("Alias Info");
        if (collAliasInfo != null) {
            int size = collAliasInfo.size();
            Iterator ite = collAliasInfo.iterator();
            while (ite.hasNext()) {
                Alias alias = new Alias();
                String[] aliasInfo = ((String) ite.next()).split(INNER_FIELD_SEPARATOR_REGEX);
                alias.setLastName(formatQuote(aliasInfo[0]));    //0. Last  Name
                alias.setFirstName(formatQuote(aliasInfo[1]));    //1. First Name
                alias.setMiddleName(formatQuote(aliasInfo[2])); //2. Middle Name

                aliasList.add(alias);
            }
        }
        return aliasList;
    }

    //--------Util Methods----------------

    private String formatNull(String src) {
        if (src == null) {
            return "";
        }
        return src;
    }


    private String formatQuote(String srcStr) {
        if (srcStr != null) {
            return srcStr.replace('"', '\'');
        }
        return "";
    }

    private String formatSMTText(String srcStr) {
        if (srcStr != null) {
            return srcStr.replace('"', '~');
        }
        return "";
    }

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

    public String getUserXMLFileName() {
        return userXMLFileName;
    }

    public String getUserFolderNameWithPath() {
        return folderOutputPath + File.separator + userXMLFileName;
    }

    public String getUserInProgressFileNameWithPath() {
        return folderOutputPath + File.separator + userInProgressFileName;
    }

    public String getUserErrorFileNameWithPath() {
        return folderOutputPath + File.separator + userErrorFileName;
    }

    public String getUserZipFileNameWithPath() {
        return ""; //folderOutputPath + File.separator + userXMLFileName;
    }

    public String getUserXMLFileNameWithPath() {
        return folderOutputPath + File.separator + userXMLFileName;
    }

    public String getUserTempZipFileNameWithPath() {
        return folderOutputPath + File.separator + userTempXMLFileName;
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
