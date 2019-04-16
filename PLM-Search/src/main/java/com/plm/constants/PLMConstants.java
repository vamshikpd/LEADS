package com.plm.constants;

public class PLMConstants {
	private PLMConstants(){}
	public static final String INSERT_SAVE_SEARCH_STRING = "Insert into USER_SEARCH (SEARCH_ID, USERNAME,SEARCH_NAME,QUERY) values (SEARCH_SEQ.nextval, ?, ?, ?)";
	public static final String SELECT_SAVE_SEARCH_STRING = "Select * from USER_SEARCH where userName=?";
	public static final String SELECT_SAVE_SEARCH_BY_ID = "Select * from USER_SEARCH where SEARCH_ID=?";
	public static final String INSERT_INTO_CORI_INFO = "Insert into CORI_INFO(QUERY_ID, USERNAME,IP_ADDRESS,CDC_NUM,INQUIRY_DATE,CASE_NO,REASON_NO) values (CORI_INFO_ID_SEQ.nextval, ?, ?, ?,SYSDATE, ?, ?)";
	public static final String INSERT_INTO_CORI_QUERY = "Insert into CORI_QUERY(QUERY_ID, USERNAME,IP_ADDRESS,INQUIRY_DATE,CASE_NO,REASON_NO, SEARCH_TYPE) values (CORI_QUERY_ID_SEQ.nextval, ?, ?, SYSDATE, ?, ?, ?)";
	public static final String INSERT_INTO_CORI_QUERY_RESULTS = "Insert into CORI_QUERY_RESULTS(QUERY_ID, CDC_NUM) values (?, ?)";	
	public static final String SELECT_THUMBNAIL_QUERY_BY_ID = "Select IMAGE From THUMBNAILS Where id = ?";
	public static final String SELECT_PHOTOS_QUERY_BY_ID = "Select IMAGE From PHOTOS Where id = ?";
	public static final String SELECT_NIST_QUERY_BY_ID = "Select IMAGE From NIST Where id = ?";
	public static final String SELECT_PARAM_VALUE_QUERY = "Select VALUE From KEYVALSTORE Where KEY = ?";

	public static final String DEFAULT_IMAGE_PATH = "media/images/parolee_details/nophoto_thumbnail.jpg";
	public static final String DEFAULT_THUMBNAIL_IMAGE_PATH = "media/images/parolee_details/nophoto_thumbnail.jpg";
	public static final String DEFAULT_PHOTO_IMAGE_PATH = "media/images/parolee_details/nophoto.jpg";
	public static final String SEPARATOR = "" + (char) 176;
	public static final String PAROLE_STATUS_SUSPENDED = "SUSPENDED";
	
	public static final String TEMP_PDF_CREATE_FOLDER = "temp/pdf/parolee_details/";
	public static final String PDF_DISPLAY_TYPE_PHOTO_NIST = "photonist";
	public static final String PDF_DISPLAY_TYPE_GALLERY = "gallery";
	public static final String PDF_DISPLAY_TYPE_PAL_POSTER = "palposter";
	public static final String PDF_DISPLAY_TYPE_THUMB_NAIL = "thumbnail";
	public static final String PDF_DISPLAY_TYPE_PHOTO_LINEUP = "photolineup";
	public static final String DEFAULT_NUM_RESULTS_ENDECA_LINEUP = "NUM_ITEMS_LINEUP";
	public static final String DEFAULT_NUM_RESULTS_MOBILE_APP = "NUM_ITEMS_MOBILE_APP";
	public static final String DEFAULT_NUM_RESULTS_VIEW_ALL_PAROLEE = "NUM_ITEMS_VIEW_ALL_PAR";
	
	public static final String DEFAULT_DATA_PHOTO_DOWNLOAD_PATH_LABEL = "DATA_PHOTO_FILE_DOWNLOAD_PATH";
	public static final String DEFAULT_DATA_DOWNLOAD_FILE_LABEL = "PAROLEE_DATA_FILENAME";
	public static final String DEFAULT_PHOTO_DOWNLOAD_FILE_LABEL = "PAROLEE_PHOTO_FILENAME";

	public static final String BOOKCFC_WSD_SERVICENAME_LABEL = "BOOKCFC_WSD_SERVICENAME";
	public static final String BOOKCFC_ENDPOINT_ADDRESS_LABEL = "BOOKCFC_ENDPOINT_ADDRESS";
	public static final String CALPAROLE_TO_LEADS_LANDING_PAGE_LABEL = "CALPAROLE_TO_LEADS_LANDING_PAGE";
	public static final String SOMS_TO_LEADS_LANDING_PAGE_LABEL = "SOMS_TO_LEADS_LANDING_PAGE";
	
	public static final String PRINT_PAROLEE_PAGE = "Parolee";
	public static final String PRINT_RESULTS_PAGE = "Results";
	
	public static final String PHOTO_DOWNLOAD_AVAILABLE_FLAG = "PHOTO_DOWNLOAD_AVAILABLE_FLAG";
	
	public static final String OFFLINE_AUDIT_FILE_PATH = "OFFLINE_AUDIT_FILE_PATH";
	
	//LDAP Attributes
/*	public static final String OB_USER_ACCOUNT_CONTROL="obuseraccountcontrol";
	public static final String OB_USER_ACCOUNT_CONTROL_VALUE="DEACTIVATED";	
	public static final String USER_NEEDS_TO_COMPLETE_CERTIFICATION="userNeedsToCompleteCertification";
	public static final String USER_IS_A_PROSPECTIVE_LEADS_ACCOUNT_ADMIN="isProspectiveLeadsAdmin";	
	public static final String USER_MODIFY_VALUE="false";
	
	public static final String USER_GROUP="LEADSUsers";
	public static final String NEW_GROUP="LEADSAccountAdmins";
*///	public static final String NEW_GROUP="LeadsAccountAdmins";
	public static final int PHOTO_RESIZE_WIDTH=240;
	public static final int PHOTO_RESIZE_HEIGHT=300;
}
