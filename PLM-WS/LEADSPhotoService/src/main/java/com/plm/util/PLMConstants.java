package com.plm.util;

import com.endeca.ui.constants.UI_Props;

public class PLMConstants {
	public static final String RESPONSE_STATUS_SUCCESS = "Success";
    public static final String RESPONSE_STATUS_ERROR = "Error";
    
    public static final String ERR_SERVER = "ERR001";
    public static final String ERR_DATABASE_ERROR = "ERR002";
    public static final String ERR_SEARCH_ENGINE = "ERR003";
    public static final String ERR_INVALID_REQUEST = "ERR004";
    public static final String ERR_PARTIAL_UPDATE_INPROGRESS = "ERR005";
    public static final String ERR_INVALID_RESPONSE_FIELD = "ERR006";

    public static final String ERR_CASE_NUMBER_NOT_SPECIFIED = "ERR101";
    public static final String ERR_REASON_NOT_SPECIFIED = "ERR102";
    public static final String ERR_CITY_NOT_SPECIFIED = "ERR103";
    public static final String ERR_COUNTY_NOT_SPECIFIED = "ERR104";
    public static final String ERR_LAST_UPDATE_DATE_NOT_SPECIFIED = "ERR105";
    public static final String ERR_USERNAME_NOT_SPECIFIED = "ERR106";
    public static final String ERR_GROUP_CODE_NOT_SPECIFIED = "ERR107";
    public static final String ERR_CDC_NUMBERS_NOT_SPECIFIED = "ERR108";

    public static final String ERR_INVALID_LAST_UPDATE_DATE = "ERR201";
    public static final String ERR_FUTURE_LAST_UPDATE_DATE = "ERR202";
    public static final String ERR_LAST_UPDATE_DATE_CROSSED_DOWNLOAD_LIMIT = "ERR203";
    
    public static final String ERR_INVALID_FROM_PAROLE_RELEASE_DATE = "ERR204";
    public static final String ERR_INVALID_TO_PAROLE_RELEASE_DATE = "ERR205";
    public static final String ERR_FROM_DATE_WITHOUT_TO_DATE = "ERR206";
    public static final String ERR_TO_DATE_WITHOUT_FROM_DATE = "ERR207";
    public static final String ERR_FUTURE_FROM_PAROLE_RELEASE_DATE = "ERR208";
    public static final String ERR_FUTURE_TO_PAROLE_RELEASE_DATE = "ERR209";
    public static final String ERR_OLDEST_PAROLE_RELEASE_DATE = "ERR210";
    public static final String ERR_TO_DATE_BEFORE_FROM_DATE = "ERR211";
    
    public static final String ERR_INVALID_VEHICLE_YEAR = "ERR212";
    public static final String ERR_FROM_YEAR_WITHOUT_TO_YEAR = "ERR213";
    public static final String ERR_TO_YEAR_WITHOUT_FROM_YEAR = "ERR214";
    public static final String ERR_FUTURE_FROM_VEHICLE_YEAR = "ERR215";
    public static final String ERR_FUTURE_TO_VEHICLE_YEAR = "ERR216";
    public static final String ERR_OLDEST_VEHICLE_YEAR = "ERR217";
    public static final String ERR_TO_YEAR_BEFORE_FROM_YEAR = "ERR218";
    
    public static final String ERR_INVALID_DATE_OF_BIRTH = "ERR219";
    public static final String ERR_FUTURE_DATE_OF_BIRTH = "ERR220";

    public static final String ERR_NO_DATA_FOUND="ERR301";
    public static final String ERR_NO_PHOTO_FOUND="ERR302";
    
    public static final String ERR_USERNAME_INPROGRESS_FILE_EXISTS="ERR401";
    public static final String ERR_USERNAME_INPROGRESS_FILE_EXISTS_HANG="ERR402";

    public static final String ERR_INPROGRESS_FILE_CREATE="ERR501";
    public static final String ERR_OUTPUT_FOLDER_CREATE = "ERR502";
    public static final String ERR_ZIP_FILE_CREATE="ERR503";
    public static final String ERR_USER_ERROR_FILE_DELETE="ERR504";
    public static final String ERR_ZIP_FILE_DELETE="ERR505";
    public static final String ERR_ZIP_FILE_RENAME="ERR506";
    public static final String ERR_USER_FOLDER_DELETE="ERR507";
    public static final String ERR_INPROGRESS_FILE_DELETE="ERR508";
    public static final String ERR_UNKNOWN_PHOTO_CREATE="ERR509";

    public static final String ERR_AUDIT_UPDATE_FAIL = "ERR601";
    public static final String ERR_WRITE_PHOTO_FILE="ERR602";
    public static final String ERR_GET_PHOTO_FROM_DB="ERR603";
    public static final String ERR_FORMAT_DATA="ERR604";

    public static final String ERR_UNKNOWN="ERR999";
    
	public static final String DEFAULT_PHOTO_IMAGE_PATH = "images/nophoto.jpeg";

    public static final String SEARCH_TYPE="WS";
    public static final String WEB_INF_FOLDER="WEB-INF";

	public static final String DATA_TYPE_NIEM="NIEM";
	public static final String DATA_TYPE_CSV="CSV";
    
	public static final String FILENAME_EXT_INPROGRESS = "inprogress";
	public static final String FILENAME_EXT_ERROR = "err";
	public static final String FILENAME_EXT_ZIP = "zip";
	
	//"_tmp" will be added while creation of the zip file. 
	//Once the temporary zip file is created existing zip file will be deleted 
	//and the temporary zip file will be renamed
	public static final String FILENAME_TMP_EXT_ZIP = "_tmp"; 
	
//	public static final String INITIAL_CONTEXT_FACTORY_VALUE = "weblogic.jndi.WLInitialContextFactory";
//	public static final String PROVIDER_URL_VALUE = "t3://{HOSTNAME}:"+UI_Props.getInstance().getValue("websvc_wlsport");

	public static final String PAR = "PAR";
	public static final String ADD = "ADD";
	public static final String ALI = "ALI";
	public static final String JOB = "JOB";
	public static final String MON = "MON";
	public static final String OFF = "OFF";
	public static final String SMT = "SMT";
	public static final String SPC = "SPC";
	public static final String VEH = "VEH";
	public static final String LAST_NAME = "PAR.LAST_NAME";
	public static final String FIRST_NAME = "PAR.FIRST_NAME";
	public static final String MIDDLE_NAME = "PAR.MIDDLE_NAME";
	public static final String AGENT_CODE = "PAR.AGENT_CODE";
	public static final String AGENT_NAME = "PAR.AGENT_NAME";
	public static final String AGENT_PHONE = "PAR.AGENT_PHONE";
	public static final String BIRTH_DATE = "PAR.BIRTH_DATE";
	public static final String SEX = "PAR.SEX";
	public static final String RACE_CODE = "PAR.RACE_CODE";
	public static final String EYE_COLOR_CODE = "PAR.EYE_COLOR_CODE";
	public static final String HEIGHT = "PAR.HEIGHT";
	public static final String WEIGHT = "PAR.WEIGHT";
	public static final String HAIR_COLOR_CODE = "PAR.HAIR_COLOR_CODE";
	public static final String PAROLE_DATE = "PAR.PAROLE_DATE";
	public static final String CLASSIFICATION_CODE = "PAR.CLASSIFICATION_CODE";
	public static final String HS_REQ = "PAR.HS_REQ";
	public static final String PC_290_REQ = "PAR.PC_290_REQ";
	public static final String PC_457_REQ = "PAR.PC_457_REQ";
	public static final String CII_NUMBER = "PAR.CII_NUMBER";
	public static final String FBI_NUMBER = "PAR.FBI_NUMBER";
	public static final String SSA_NUMBER = "PAR.SSA_NUMBER";
	public static final String DRIVER_LICENCE = "PAR.DRIVER_LICENCE";
	public static final String STATUS = "PAR.STATUS";
	public static final String PC_3058_REQ = "PAR.PC_3058_REQ";
	public static final String ANT_REQ = "PAR.ANT_REQ";
	public static final String NO_ALCOHOL = "PAR.NO_ALCOHOL";
	public static final String POC_REQ = "PAR.POC_REQ";
	public static final String BIRTH_STATE_CODE = "PAR.BIRTH_STATE_CODE";
	public static final String COMMENTS = "PAR.COMMENTS";
	public static final String PROBLEM_AREA_NARCOTIC = "PAR.PROBLEM_AREA_NARCOTIC";
	public static final String PROBLEM_AREA_ALCOHOL = "PAR.PROBLEM_AREA_ALCOHOL";
	public static final String PROBLEM_AREA_ASSAULT = "PAR.PROBLEM_AREA_ASSAULT";
	public static final String PROBLEM_AREA_SEX = "PAR.PROBLEM_AREA_SEX";
	public static final String PROBLEM_AREA_OTHER = "PAR.PROBLEM_AREA_OTHER";
	public static final String HS_DATE = "PAR.HS_DATE";
	public static final String PC_290_DATE = "PAR.PC_290_DATE";
	public static final String PC_457_DATE = "PAR.PC_457_DATE";
	public static final String PC_3058_DATE = "PAR.PC_3058_DATE";
	public static final String CONTROL_DISCHARGE_DATE = "PAR.CONTROL_DISCHARGE_DATE";
	public static final String COUNTY_COMMIT = "PAR.COUNTY_COMMIT";
	public static final String DISCHARGED_DATE = "PAR.DISCHARGED_DATE";
	public static final String REVOCATION_RELEASE_DATE = "PAR.REVOCATION_RELEASE_DATE";
	public static final String COLLR = "PAR.COLLR";

}
