package com.plm.util;

import com.endeca.ui.constants.UI_Props;

public class PLMConstants {
	public static final String RESPONSE_STATUS_SUCCESS="Success";
	public static final String RESPONSE_STATUS_ERROR="Error";
	public static final String ERR_SERVER="ERR001";
	public static final String ERR_DATABASE_ERROR="ERR002";
	public static final String ERR_SEARCH_ENGINE="ERR003";
	public static final String ERR_INVALID_REQUEST="ERR004";
	public static final String ERR_CASE_NUMBER_NOT_SPECIFIED="ERR101";
	public static final String ERR_REASON_NOT_SPECIFIED="ERR102";
	public static final String ERR_USERNAME_NOT_SPECIFIED="ERR106";
	public static final String ERR_CDC_NUMBERS_NOT_SPECIFIED="ERR108";
	public static final String ERR_INVALID_LAST_UPDATE_DATE="ERR201";
	public static final String ERR_FUTURE_LAST_UPDATE_DATE="ERR202";
	public static final String ERR_INVALID_DATE_FORMAT="ERR203";
	public static final String ERR_LAST_UPDATE_DATE_CROSSED_DOWNLOAD_LIMIT="ERR204";
	public static final String ERR_NO_DATA_FOUND="ERR301";
	public static final String ERR_NO_PHOTO_FOUND="ERR302";
	public static final String ERR_AUDIT_UPDATE_FAIL = "ERR601";
	public static final String ERR_WRITE_PHOTO_FILE="ERR602";
	public static final String ERR_GET_PHOTO_FROM_DB="ERR603";
	public static final String ERR_FORMAT_DATA="ERR604";
	public static final String SEARCH_TYPE="WS";
	public static final String INITIAL_CONTEXT_FACTORY_VALUE = "weblogic.jndi.WLInitialContextFactory";
	public static final String PROVIDER_URL_VALUE = "t3://{HOSTNAME}:"+UI_Props.getInstance().getValue("websvc_wlsport");
}