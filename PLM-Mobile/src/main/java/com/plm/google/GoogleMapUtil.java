package com.plm.google;

import com.endeca.ui.constants.UI_Props;
import com.plm.util.PLMSearchUtil;

import org.apache.log4j.Logger;

public class GoogleMapUtil {
    final static String sGoogleMapEnv = UI_Props.getInstance().getValue("GOOGLE_MAP_ENV");
    final static String sGoogleMapUrl = UI_Props.getInstance().getValue("GOOGLE_MAP_URL");
    final static String sGoogleMapKey = UI_Props.getInstance().getValue("GOOGLE_MAP_KEY");
    final static String sGoogleMapSecureHost = UI_Props.getInstance().getValue("GOOGLE_MAP_URL_HOST_SECURE");
    final static String sGoogleMapNonSecureHost = UI_Props.getInstance().getValue("GOOGLE_MAP_URL_HOST_NONSECURE");
    private static final Logger logger = Logger.getLogger(PLMSearchUtil.class);
    private static String sGoogleMapUrlSecure = "";
    private static String sGoogleMapUrlNonSecure = "";

    static {
        init();
    }

    public static void init() {
        sGoogleMapUrlSecure = sGoogleMapSecureHost + sGoogleMapUrl + sGoogleMapKey;
        sGoogleMapUrlNonSecure = sGoogleMapNonSecureHost + sGoogleMapUrl + sGoogleMapKey;
        logger.debug("sGoogleMapUrlNonSecure: " + sGoogleMapUrlNonSecure);
        logger.debug("sGoogleMapUrlSecure: " + sGoogleMapUrlSecure);
    }

    public static String getGoogleMapUrl(boolean isSecure) {
        if ("prod".equalsIgnoreCase(sGoogleMapEnv)) {
//			if(isSecure)
            return sGoogleMapUrlSecure;
//			else
//				return sGoogleMapUrlNonSecure;
        } else if ("uat".equalsIgnoreCase(sGoogleMapEnv)) {
            return sGoogleMapUrlSecure;
        } else {
            return sGoogleMapUrlNonSecure;
        }
    }
}
