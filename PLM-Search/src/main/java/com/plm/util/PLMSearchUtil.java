/*PLMSearchUtil.java
 * 10-18-2011 Modified the postAgentMail to include CC addresses. L. Baird
 * 03-22-2012 Added readCountiesXML() to read the CountyList.xml file
 * 03-22-2012 Modified readCountiesXML() to populate List instead of HashMap to get sorted list in the select drop down
 */
package com.plm.util;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import org.apache.log4j.Logger;
import com.endeca.navigation.DimLocation;
import com.endeca.navigation.DimLocationList;
import com.endeca.navigation.DimVal;
import com.endeca.navigation.DimValList;
import com.endeca.navigation.Dimension;
import com.endeca.navigation.DimensionList;
import com.endeca.navigation.DimensionSearchResult;
import com.endeca.navigation.DimensionSearchResultGroup;
import com.endeca.navigation.DimensionSearchResultGroupList;
import com.endeca.navigation.ENEConnection;
import com.endeca.navigation.ENEConnectionException;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.HttpENEConnection;
import com.endeca.navigation.Navigation;
import com.endeca.navigation.UrlGen;
import com.endeca.ui.AdvancedENEQuery;
import com.endeca.ui.constants.UI_Props;
import com.plm.constants.PLMConstants;
import com.plm.util.database.PLMDatabaseUtil;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/*class SMTPAuthenticator extends Authenticator {

    public PasswordAuthentication getPasswordAuthentication() {
        String username = UI_Props.getInstance().getValue("EMAIL_AGENT_USERNAME");
        String password = UI_Props.getInstance().getValue("EMAIL_AGENT_PASSWORD");
        if (username != null && password != null)
            return new PasswordAuthentication(username, password);
        else
            return null;
    }
}*/

public class PLMSearchUtil {

    private static final Logger logger = Logger.getLogger(PLMSearchUtil.class);
//    static Authenticator auSMTPAuth = new SMTPAuthenticator();
    public static String downloadFileOutputpath = UI_Props.getInstance().getValue(PLMConstants.DEFAULT_DATA_PHOTO_DOWNLOAD_PATH_LABEL);
    public static String paroleeDataFilename = UI_Props.getInstance().getValue(PLMConstants.DEFAULT_DATA_DOWNLOAD_FILE_LABEL);
    public static String paroleePhotoFilename = UI_Props.getInstance().getValue(PLMConstants.DEFAULT_PHOTO_DOWNLOAD_FILE_LABEL);
    private static int portIndex_ = 0;
    private static int hostIndex_ = 0;
    //private static final String[] enePort_ = UI_Props.getInstance().getValue(UI_Props.ENE_PORT).split("\\s");
    //private static final String[] eneHost_ = UI_Props.getInstance().getValue(UI_Props.ENE_HOST).split("\\s");
    private static final String[] eneHost_ = UI_Props.getInstance().getValue(UI_Props.ENE_HOST).split("\\s");
    private static final int[] enePort_ = getEndecaPorts(UI_Props.getInstance().getValue(UI_Props.ENE_PORT));
    private final static String sGoogleMapEnv = UI_Props.getInstance().getValue("GOOGLE_MAP_ENV");
    private static Navigation CACHED_NAV;
    private static long CACHE_NAV_LAST_TS;
    //private static String nextEnePort_ = null;


    private static int[] getEndecaPorts(String endecaPorts) {
        String[] saPorts = endecaPorts.split("\\s");
        int[] iaPorts = null;
        if (saPorts != null && saPorts.length > 0) {
            iaPorts = new int[saPorts.length];
            int i = 0;
            for (String s : saPorts) {
                iaPorts[i] = Integer.parseInt(s);
                i++;
            }
        }
        return iaPorts;
    }


    private static Navigation getCurrentNavigation(String url) throws ENEQueryException {
        Navigation nav = null;
        UrlGen sUrlg = null;
        if (url != null) {
            sUrlg = new UrlGen(url, "UTF-8");
        }
        if (sUrlg != null) {
            String eneHost = PLMSearchUtil.getEndecaHost();
            int enePort = PLMSearchUtil.getEndecaPort();
            String queryString = sUrlg.toString();
            ENEConnection nec = new HttpENEConnection(eneHost, enePort);
            AdvancedENEQuery query = new AdvancedENEQuery(queryString, nec);
            long beforeTime = System.currentTimeMillis();
            Map resultMap = null;
            int cnt = 1;
            try {
                resultMap = query.process().get(0);
            } catch (ENEConnectionException eneConn) {
                do {
                    eneHost = PLMSearchUtil.getEndecaHost();
                    enePort = PLMSearchUtil.getEndecaPort();
                    nec = new HttpENEConnection(eneHost, enePort);
                    query.setConnection(nec);
                    cnt++;
                } while (!query.dgraphIsAlive() && cnt <= PLMSearchUtil.getEndecaPortSize());

                resultMap = query.process().get(0);
            }
            long afterTime = System.currentTimeMillis() - beforeTime;
            ENEQueryResults qr = (ENEQueryResults) resultMap.get("RESULT");
            if (qr.containsNavigation()) {
                nav = qr.getNavigation();
            }
        }
        return nav;
    }


    private static DimensionList getCompleteDimensions() throws ENEQueryException {
        DimensionList dimList = null;
        String queryString = "N=" + UI_Props.getInstance().getValue(UI_Props.ENE_ROOT);

        // 2018-03-22 emil introduced new static variable to store cached navigation results for 5 minutes
        if (CACHED_NAV == null || System.currentTimeMillis() - CACHE_NAV_LAST_TS > 5 * 60 * 60) {
            Navigation nav = getCurrentNavigation(queryString);
            CACHED_NAV = nav;
            CACHE_NAV_LAST_TS = System.currentTimeMillis();
        }

        if (CACHED_NAV != null) {
            dimList = CACHED_NAV.getCompleteDimensions();
        }
        return dimList;
    }


    public static ArrayList<String> getAllCities() throws ENEQueryException {
        ArrayList<String> allCities = new ArrayList<String>();
        DimensionList dimList = getCompleteDimensions();
        if (dimList != null) {
            Dimension dim = dimList.getDimension("City Name");
            if (dim != null) {
                DimValList refs = dim.getRefinements();
                for (int i = 0; i < refs.size(); i++) {
                    DimVal ref = refs.getDimValue(i);
                    allCities.add(ref.getName());
                }
            }
        }
        return allCities;
    }

    public static ArrayList<String> getAllHairColor() throws ENEQueryException {
        ArrayList<String> allHairColor = new ArrayList<String>();
        DimensionList dimList = getCompleteDimensions();
        if (dimList != null) {
            Dimension dim = dimList.getDimension("Hair Color");
            if (dim != null) {
                DimValList refs = dim.getRefinements();
                for (int i = 0; i < refs.size(); i++) {
                    DimVal ref = refs.getDimValue(i);
                    allHairColor.add(ref.getName());
                }
            }
        }
        return allHairColor;
    }

    public static ArrayList<String> getAllEthnicity() throws ENEQueryException {
        ArrayList<String> allEthnicity = new ArrayList<String>();
        DimensionList dimList = getCompleteDimensions();
        if (dimList != null) {
            Dimension dim = dimList.getDimension("Ethnicity");
            if (dim != null) {
                DimValList refs = dim.getRefinements();
                for (int i = 0; i < refs.size(); i++) {
                    DimVal ref = refs.getDimValue(i);
                    allEthnicity.add(ref.getName());
                }
            }
        }
        return allEthnicity;
    }

    public static ArrayList<String> getAllOffenseCodes() throws ENEQueryException {
        ArrayList<String> allOffenseCodes = new ArrayList<String>();
        DimensionList dimList = getCompleteDimensions();
        if (dimList != null) {
            Dimension dim = dimList.getDimension("Commitment Offense");
            if (dim != null) {
                DimValList refs = dim.getRefinements();
                for (int i = 0; i < refs.size(); i++) {
                    DimVal ref = refs.getDimValue(i);
                    allOffenseCodes.add(ref.getName());
                }
            }
        }
        return allOffenseCodes;
    }

    public static ArrayList<String> getAllSmtDesc() throws ENEQueryException {
        ArrayList<String> allAllSmtDesc = new ArrayList<String>();
        DimensionList dimList = getCompleteDimensions();
        if (dimList != null) {
            Dimension dim = dimList.getDimension("SMT Code");
            if (dim != null) {
                DimValList refs = dim.getRefinements();
                for (int i = 0; i < refs.size(); i++) {
                    DimVal ref = refs.getDimValue(i);
                    allAllSmtDesc.add(ref.getName());
                }
            }
        }
        return allAllSmtDesc;
    }

    public static String convertNValueToName(String url) throws ENEQueryException {

        Dimension dim = null;
        DimensionList descDimesions = null;
        DimVal root = null;
        DimVal desc = null;
        //long lNValue=0;
        String sRootName = null;
        String sDescName = null;
        String dbString = "";
        //String sNValue=null;
        String nameString = "";
        Navigation nav = getCurrentNavigation(url);
        if (nav != null) {
            descDimesions = nav.getDescriptorDimensions();
        }
        if (descDimesions != null && descDimesions.size() > 0) {

            for (int i = 0; i < descDimesions.size(); i++) {
                // Get individual dimension
                dim = (Dimension) descDimesions.get(i);
                // Get root for dimensionD
                root = dim.getRoot();
                desc = dim.getDescriptor();
                sRootName = root.getName();//city
                sDescName = desc.getName();//mumbai
                dbString = dbString + sRootName + "|" + sDescName + "+";
            }
            dbString = dbString.substring(0, dbString.length() - 1);
        }
        String[] stokens = url.split("&");
        for (int i = 0; i < stokens.length; i++) {
            if (stokens[i].indexOf("N=") >= 0) {
                if (dbString.length() == 0)
                    dbString = "0";
                nameString = nameString + "N=" + dbString + "&";
            } else if (stokens[i].indexOf("Ne=") < 0) {
                nameString = nameString + stokens[i] + "&";
            }
        }
        return nameString.substring(0, nameString.length() - 1);
    }


    public static Map<String, String> getSavedSearches(String query, String user) throws SQLException, ENEQueryException {
        return PLMDatabaseUtil.getSavedSearches(query, user);
    }

    public static String getSavedSearchByID(String id) throws SQLException {
        return PLMDatabaseUtil.getSavedSearchByID(id);
    }

    private static ENEQueryResults getENEQueryResults(String url) throws ENEQueryException {
        ENEQueryResults qr = null;
        //Navigation nav =null;
        UrlGen sUrlg = null;
        if (url != null) {
            sUrlg = new UrlGen(url, "UTF-8");
        }
        if (sUrlg != null) {
            String eneHost = PLMSearchUtil.getEndecaHost();
            int enePort = PLMSearchUtil.getEndecaPort();
            String queryString = sUrlg.toString();
            ENEConnection nec = new HttpENEConnection(eneHost, enePort);
            AdvancedENEQuery query = new AdvancedENEQuery(queryString, nec);
            //Map resultMap = query.process().get(0);
            Map resultMap = null;
            int cnt = 1;
            try {
                resultMap = query.process().get(0);
            } catch (ENEConnectionException eneConn) {
                do {
                    eneHost = PLMSearchUtil.getEndecaHost();
                    enePort = PLMSearchUtil.getEndecaPort();
                    nec = new HttpENEConnection(eneHost, enePort);
                    query.setConnection(nec);
                    cnt++;
                } while (!query.dgraphIsAlive() && cnt <= PLMSearchUtil.getEndecaPortSize());
                resultMap = query.process().get(0);
            }
            qr = (ENEQueryResults) resultMap.get("RESULT");
        }
        return qr;
    }

    public static String convertNameToNvalue(String url) throws ENEQueryException {
        //String url = "Dg=Personal+Information&N=Birth Year|1900-1920+SMT Code|TATTOO LEFT ARM&refineTabID=1";

        // emil 2018-03-07 decode ASCII into characters in URL
        url = decodeSpecialCharsInSearchQuery(url);

        String nValues = "";
        String[] stokens = url.split("&");
        String nameString = "";
        HashMap<String, String> dimNameValuesMap = new HashMap<String, String>();
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("N=0");
        queryBuilder.append("&");
        queryBuilder.append("D=");
        // get the Query field from DB
        // extract the 'N=' part
        // from that extract the refinement part ( build a map value vs name)
        // fire the endeca query with N=0&D=<all refinemnt part seperated with +>&Dx=mode+matchAny
        //get the navigation object
        //
        DimensionList completeDimensions = getCompleteDimensions();
        boolean flag = false;
        String name = "";
        String value = "";

        for (int i = 0; i < stokens.length; i++) {
            if (stokens[i].indexOf("N=") >= 0) {
                stokens[i] = stokens[i].substring(2, stokens[i].length());
                stokens[i] = stokens[i].replace("+/-", "####");
                String nameValuePairs[] = stokens[i].split("\\+");
                long prevNValue = 0;
                for (int j = 0; j < nameValuePairs.length; j++) {
                    if (nameValuePairs[j].indexOf("####") >= 0) {
                        nameValuePairs[j] = nameValuePairs[j].replace("####", "+/-");
                    }
                    if (nameValuePairs[j].indexOf("|") > 0) {
                        name = nameValuePairs[j].substring(0, nameValuePairs[j].indexOf("|"));
                        try {
                            value = URLDecoder.decode(nameValuePairs[j].substring(nameValuePairs[j].indexOf("|") + 1, nameValuePairs[j].length()), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            logger.error(PLMUtil.getStackTrace(e));
                        }
                        dimNameValuesMap.put(value, name);
                        queryBuilder.append(value);
                        queryBuilder.append("+");
                    }// end of for(int j =0 ...
                    if (completeDimensions != null) {
                        Dimension dim = completeDimensions.getDimension(name);
                        if (dim != null) {
                            DimValList refs = dim.getRefinements();
                            for (int k = 0; k < refs.size(); k++) {
                                if (refs.getDimValue(k).getName().equals(value.trim())) {
                                    nValues = nValues + "+" + refs.getDimValue(k).getId();
                                    prevNValue = refs.getDimValue(k).getId();
                                    break;
                                } else {
                                    String queryStr = queryBuilder.substring(0, queryBuilder.length() - 1);
                                    queryStr = queryStr + "&Dx=mode+matchAny";
                                    ENEQueryResults qr = getENEQueryResults(queryStr);
                                    if (qr.containsDimensionSearch()) {
                                        DimensionSearchResult dsr = qr.getDimensionSearch();
                                        // Get results grouped by dimension groups
                                        DimensionSearchResultGroupList dsrgl = dsr.getResults();
                                        for (int a = 0; a < dsrgl.size(); a++) {
                                            // Get individual result dimension group
                                            DimensionSearchResultGroup dsrg = (DimensionSearchResultGroup) dsrgl.get(a);
                                            // Get roots for dimension group
                                            DimValList roots = dsrg.getRoots();
                                            for (int l = 0; l < roots.size(); l++) {
                                                // Get dimension root
                                                DimVal root = (DimVal) roots.get(l);
                                                String rootName = root.getName();
                                                for (int kl = 0; kl < dsrg.size(); kl++) {
                                                    DimLocationList dll = (DimLocationList) dsrg.get(kl);
                                                    for (int ak = 0; ak < dll.size(); ak++) {
                                                        // Get individual dimlocation from result
                                                        DimLocation dl = (DimLocation) dll.get(ak);
                                                        String dimValName = dl.getDimValue().getName();
                                                        if (dimNameValuesMap.containsKey(dimValName)) {
                                                            String rootNameInMap = (String) dimNameValuesMap.get(dimValName);
                                                            dimNameValuesMap.remove(dimValName);
                                                            if (rootName.equals(rootNameInMap)) {
                                                                long dimValue = dl.getDimValue().getId();
                                                                if (dimValue == prevNValue) {
                                                                    continue;
                                                                }
                                                                nValues = nValues + "+" + dl.getDimValue().getId();
                                                                flag = true;
                                                            }
                                                        }
                                                    }
                                                }
                                            }// end of for(j=0....
                                        }// end of for(int a=0...
                                    }// end of if usq.getDimSearchTerms()
                                }

                                if (flag) {
                                    flag = false;
                                    break;
                                }
                            }// end for for loop

                        }
                    }
                }
                //}
                if (nValues.length() > 0) {
                    nValues = nValues.substring(1, nValues.length());
                    nameString = nameString + "N=" + nValues.substring(0, nValues.length()) + "&";
                } else {
                    nameString = nameString + "N=" + "0" + "&";
                }
            } else {
                nameString = nameString + stokens[i] + "&";
            }
        }

        nameString = nameString.substring(0, nameString.length() - 1);

        // emil 2018-03-09 encode special characters to ASCII after parsing is done to create a valid URL
        nameString = encodeSpecialCharsInSearchQuery(nameString);

        return nameString;
    }

    public static String getAdvanceSearchDisplayName(String field) {
        //String displayName = "";
        if (field.indexOf("\"") >= 0) {
            field = field.substring(1, field.length() - 1);
        }
        if (field.indexOf("%22") >= 0) {
            field = field.substring(field.indexOf("%22") + 3, field.lastIndexOf("%22") - 1 - field.indexOf("%22") + 1);
        }
        if (field.equals("Moniker Info"))
            field = "Moniker";
        else if (field.equals("City"))
            field = "Res. City";
        else if (field.equals("County Name"))
            field = "Res. County";
        else if (field.equals("Zip"))
            field = "Zip Code";
        else if (field.equals("Birth State Name"))
            field = "Birth State";
        else if (field.equals("Birth Date Display"))
            field = "Date of Birth";
        else if (field.equals("SSA Number"))
            field = "SSN#";
        else if (field.equals("CDC Number"))
            field = "CDC#";
        else if (field.equals("FBI Number"))
            field = "FBI#";
        else if (field.equals("CII Number"))
            field = "CII#";
        else if (field.equals("Vehicle License Plate"))
            field = "Vehicle License No.";
        else if (field.equals("PC 290 REQ"))
            field = "PC290 Sex Off.";
        else if (field.equals("PC 457 REQ"))
            field = "PC457.1Arson";
        else if (field.equals("HS REQ"))
            field = "HS 11590 Drugs";
        else if (field.equals("PC 3058 REQ"))
            field = "PC3058.6 Felony Violation";
        else if (field.equals("Race"))
            field = "Ethnicity";
        else if (field.equals("Haircolor"))
            field = "Hair Color";
        else if (field.equals("SMT_Detail"))
            field = "SMT Criteria";
        else if (field.equals("Height (+/- 2 inches)") || field.equals("Height (%2B/- 2 inches)"))
            field = "Height (+/- 2 inches)";
        else if (field.equals("Weight (+/- 10 lb)") || field.equals("Weight (%2B/- 10 lb)"))
            field = "Weight (+/- 10 lb)";
        else if (field.equals("Revocation_Release_Date_Search"))
            field = "Revocation Release Date";
        else if (field.equals("Parole_Date_Search"))
            field = "Parole Date";
        else if (field.equals("Veh_Year"))
            field = "Vehicle Year";
        else if (field.equals("Full_Phone"))
            field = "Phone";
        else if (field.equals("geocode"))
            field = "Address";
        else if (field.equals("CountyOfLastLegalResidence"))
            field = "County Of LLR";//added LBB

        return field;
    }

    public static void postAgentMail(String inRecipient, String inAddCC, String inSubject, String inMessage, String inFrom) throws MessagingException {

        //Set the host smtp address
        Properties prMailProps = new Properties();
        String sSMTPHost = UI_Props.getInstance().getValue("SMTP_MAIL_HOST");
        String sSMTPPort = UI_Props.getInstance().getValue("SMTP_MAIL_PORT");

        prMailProps.put("mail.smtp.host", sSMTPHost);
        prMailProps.put("mail.smtp.port", sSMTPPort);
        prMailProps.put("mail.smtp.starttls.enable", "true");
        prMailProps.put("mail.smtp.auth", "false");     // Emil, 2018-01-31: MDC smtp servers don't need auth
        // Put below to false, if no https is needed
        // prMailProps.put("mail.smtp.starttls.enable", "true");

        // create some properties and get the default Session
//        Session ssMailSession = Session.getInstance(prMailProps, auSMTPAuth);
        Session ssMailSession = Session.getInstance(prMailProps);

        ssMailSession.setDebug(true);

        // create a message
        Message msgMailMessage = new MimeMessage(ssMailSession);

        // set the from and to address
        InternetAddress iaAddressFrom = null;

        if (inFrom != null) {
            iaAddressFrom = new InternetAddress(inFrom);
            msgMailMessage.setFrom(iaAddressFrom);
        }

        // Setting the To Address
        InternetAddress iaAddressTo = null;

        if (inRecipient != null) {
            iaAddressTo = new InternetAddress(inRecipient);
            msgMailMessage.setRecipient(Message.RecipientType.TO, iaAddressTo);
        }

        // Setting the CC Address -- added 10-18-2011 -- L. Baird
        if (inAddCC != null && inAddCC.trim().length() > 0) {
            //iaAddressCC = new InternetAddress(inAddCC);
            msgMailMessage.setRecipients(Message.RecipientType.CC, InternetAddress.parse(inAddCC, true));
        }

        // Setting the Subject and Content Type
        msgMailMessage.setSubject(inSubject);

        inMessage = inMessage + "<br /><br /><br /><br />";
        inMessage = inMessage + UI_Props.getInstance().getValue("EMAIL_RECIPIENT_DISCLAIMER_TEXT");

        msgMailMessage.setContent(inMessage, "text/html");

        // Optional : You can also set your custom headers in the Email if you Want
        //msg.addHeader("MyHeaderName", "myHeaderValue");

        logger.debug("Before sending an email message... To: " + inRecipient);
        try {
            Transport.send(msgMailMessage);
        } catch(Exception e) {
            logger.error("Error sending email..." + e.getMessage());
            e.printStackTrace();
        }
        logger.debug("After a call to send an email...");

    }

    public static String getFormattedPLMURL(HttpServletRequest request, String landingPage) {
//        String connType = (request.isSecure()) ? "https://" : "http://";
        String connType;
        if ("prod".equalsIgnoreCase(sGoogleMapEnv) || "uat".equalsIgnoreCase(sGoogleMapEnv)) {
            connType = "https://";
        } else {
            connType = "http://";
        }

        String ssoContext = request.getHeader("WL-PATH-TRIM") != null ?
                request.getHeader("WL-PATH-TRIM") : "";
        StringBuffer sbURL = new StringBuffer();
        sbURL.append(connType)
                .append(request.getHeader("Host"))
                .append(ssoContext)
                .append(request.getContextPath())
                .append(landingPage);
        return sbURL.toString();
    }

    @SuppressWarnings("unchecked")
    public static List<File> getListOfFiles(String userName, String ext) {
        List<File> listOfFiles = new ArrayList<File>();
        File file = new File(downloadFileOutputpath);
        listOfFiles.addAll(FileUtils.listFiles(file, new WildcardFileFilter(userName + "-" + "*" + "." + ext), null));
        Collections.sort(listOfFiles, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        return listOfFiles;
    }

    public static File getLatestFile(String userName, String ext) {
        List<File> listOfFiles = getListOfFiles(userName, ext);
        if (listOfFiles != null
                && !listOfFiles.isEmpty()) {
            return listOfFiles.get(0);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<File> getListOfDataFiles(String userName, String ext) {
        List<File> listOfDataFiles = new ArrayList<File>();
        String sWildCardFileFilter = paroleeDataFilename.replace("{USERNAME}", userName).replace("{DATE_TIME}", "*");
        listOfDataFiles.addAll(FileUtils.listFiles(new File(downloadFileOutputpath), new WildcardFileFilter(sWildCardFileFilter + "." + ext), null));
        Collections.sort(listOfDataFiles, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        return listOfDataFiles;
    }

    public static File getLatestDataFile(String userName, String ext) {
        List<File> listOfDataFiles = getListOfDataFiles(userName, ext);
        if (listOfDataFiles != null
                && !listOfDataFiles.isEmpty()) {
            return listOfDataFiles.get(0);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<File> getListOfPhotoFiles(String userName, String ext) {
        List<File> listOfPhotoFiles = new ArrayList<File>();
        String sWildCardFileFilter = paroleePhotoFilename.replace("{USERNAME}", userName).replace("{DATE_TIME}", "*");
        listOfPhotoFiles.addAll(FileUtils.listFiles(new File(downloadFileOutputpath), new WildcardFileFilter(sWildCardFileFilter + "." + ext), null));
        Collections.sort(listOfPhotoFiles, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        return listOfPhotoFiles;
    }

    public static File getLatestPhotoFile(String userName, String ext) {
        List<File> listOfPhotoFiles = getListOfPhotoFiles(userName, ext);
        if (listOfPhotoFiles != null
                && !listOfPhotoFiles.isEmpty()) {
            return listOfPhotoFiles.get(0);
        }
        return null;
    }

    public static ENEQueryResults getQueryResults(String url) throws ENEQueryException {
        //Navigation nav =null;
        UrlGen sUrlg = null;
        ENEQueryResults qr = null;
        if (url != null) {
            sUrlg = new UrlGen(url, "UTF-8");
        }
        if (sUrlg != null) {
            String eneHost = PLMSearchUtil.getEndecaHost();
            int enePort = PLMSearchUtil.getEndecaPort();
            String queryString = sUrlg.toString();
            ENEConnection nec = new HttpENEConnection(eneHost, enePort);
            AdvancedENEQuery query = new AdvancedENEQuery(queryString, nec);
            long beforeTime = System.currentTimeMillis();
            Map resultMap = null;
            int cnt = 1;
            try {
                resultMap = query.process().get(0);
            } catch (ENEConnectionException eneConn) {
                do {
                    eneHost = PLMSearchUtil.getEndecaHost();
                    enePort = PLMSearchUtil.getEndecaPort();
                    nec = new HttpENEConnection(eneHost, enePort);
                    query.setConnection(nec);
                    cnt++;
                } while (!query.dgraphIsAlive() && cnt <= PLMSearchUtil.getEndecaPortSize());

                resultMap = query.process().get(0);
            }
            long afterTime = System.currentTimeMillis() - beforeTime;
            qr = (ENEQueryResults) resultMap.get("RESULT");
        }
        return qr;
    }

    public synchronized static int getEndecaPort() {
        if (enePort_.length == 1)
            return enePort_[0];

        if (portIndex_ == enePort_.length)
            portIndex_ = 0;

        return enePort_[portIndex_++];
    }

    public static int getEndecaPortSize() {
        return enePort_.length;
    }

    public synchronized static String getEndecaHost() {
        if (eneHost_.length == 1)
            return eneHost_[0];

        if (hostIndex_ == eneHost_.length)
            hostIndex_ = 0;

        return eneHost_[hostIndex_++];
    }

    /**
     * This method is used to write off line audit data to text file when database is down.
     *
     * @param county
     * @param city
     * @param userName
     * @param ipAddress
     * @param qry_type
     * @throws IOException
     */
    public static void writePC290SearchResult(String county, String city,
                                              String userName, String ipAddress, String qry_type)
            throws IOException {
        BufferedWriter output = null;
        String filePath = UI_Props.getInstance().getValue(
                "OFFLINE_AUDIT_FILE_PATH");
        File file = new File(filePath);
        try {
            StringBuffer stringBuffer = new StringBuffer();
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            String str;
            while ((str = in.readLine()) != null) {
                stringBuffer.append(str + "\n");
            }
            in.close();
            output = new BufferedWriter(new FileWriter(file));
            output.write(stringBuffer.toString());
            output.write(county);
            output.write("|");
            output.write(city);
            output.write("|");
            output.write(userName);
            output.write("|");
            output.write(ipAddress);
            output.write("|");
            output.write(qry_type);
            output.write("|");
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to test whether PLM search database is down or not.
     *
     * @return
     */
    public static boolean checkConnection() {
        boolean bTestConn = true;
        Connection conn = null;
        try {
            conn = PLMDatabaseUtil.getConnection();
            return conn.isValid(10);
        } catch (Exception e) {
            bTestConn = false;
        } finally {
            conn = null;
        }
        return bTestConn;
    }

    public static ArrayList<String> getAllHeight() throws ENEQueryException {
        ArrayList<String> allHeight = new ArrayList<String>();
        DimensionList dimList = getCompleteDimensions();
        if (dimList != null) {
            Dimension dim = dimList.getDimension("Height (+/- 2 inches)");
            if (dim != null) {
                DimValList refs = dim.getRefinements();
                for (int i = 0; i < refs.size(); i++) {
                    DimVal ref = refs.getDimValue(i);
                    allHeight.add(ref.getName() + "|" + ref.getId());
                }
            }
        }
        return allHeight;
    }

    public static ArrayList<String> getAllWeight() throws ENEQueryException {
        ArrayList<String> allWeight = new ArrayList<String>();
        DimensionList dimList = getCompleteDimensions();
        if (dimList != null) {
            Dimension dim = dimList.getDimension("Weight (+/- 10 lb)");
            if (dim != null) {
                DimValList refs = dim.getRefinements();
                for (int i = 0; i < refs.size(); i++) {
                    DimVal ref = refs.getDimValue(i);
                    allWeight.add(ref.getName() + "|" + ref.getId());
                }
            }
        }
        return allWeight;
    }

    public static ArrayList<String> getAllCountys() throws ENEQueryException {
        ArrayList<String> allCountys = new ArrayList<String>();
        DimensionList dimList = getCompleteDimensions();

        if (dimList != null) {
            Dimension dim = dimList.getDimension("Res County");
            if (dim != null) {
                DimValList refs = dim.getRefinements();
                for (int i = 0; i < refs.size(); i++) {
                    DimVal ref = refs.getDimValue(i);
                    allCountys.add(ref.getName() + "|" + ref.getId());
                }
            }
        }
        return allCountys;
    }

    //	added to build list of COLlRs  -- LBB
    public static ArrayList<String> getAllCOLLRs() throws ENEQueryException {
        ArrayList<String> allCOLLRs = new ArrayList<String>();
        DimensionList dimList = getCompleteDimensions();

        if (dimList != null) {
            Dimension dim = dimList.getDimension("CountyOfLastLegalResidence");
            if (dim != null) {
                DimValList refs = dim.getRefinements();
                for (int i = 0; i < refs.size(); i++) {
                    DimVal ref = refs.getDimValue(i);
                    allCOLLRs.add(ref.getName() + "|" + ref.getId());
                }
            }
        }
        return allCOLLRs;
    }

    public static ArrayList<String> getAllBirthStates() throws ENEQueryException {
        ArrayList<String> allBirthStates = new ArrayList<String>();
        DimensionList dimList = getCompleteDimensions();
        if (dimList != null) {
            Dimension dim = dimList.getDimension("Birth State");
            if (dim != null) {
                DimValList refs = dim.getRefinements();
                for (int i = 0; i < refs.size(); i++) {
                    DimVal ref = refs.getDimValue(i);
                    allBirthStates.add(ref.getName() + "|" + ref.getId());
                }
            }
        }
        return allBirthStates;
    }

    public static List<String> getQueryParameters(String sRecord) {

        // emil 2018-03-07 replace ASCII codes for | and " with characters
        sRecord = decodeSpecialCharsInSearchQuery(sRecord);

        String[] sAndCriteria = sRecord.split("\\Q and \\E");
        List<String> sCriteriaList = new ArrayList<String>();
        for (String s : sAndCriteria) {
            if (s.contains(" or ")) {
                String[] sOrCriteria = s.split("\\Q or \\E");
                sCriteriaList.add(removeBrackets(sOrCriteria[0]));
                sCriteriaList.add("or");
                sCriteriaList.add(removeBrackets(sOrCriteria[1]));
            } else {
                if (sCriteriaList != null
                        && sCriteriaList.size() > 1
                        && (
                        !sCriteriaList.get(sCriteriaList.size() - 1).equals("and")
                )
                        ) {
                    sCriteriaList.add("and");
                }
                sCriteriaList.add(removeBrackets(s));
                sCriteriaList.add("and");
            }
        }
        if (sCriteriaList.get(sCriteriaList.size() - 1) != null
                && sCriteriaList.get(sCriteriaList.size() - 1).equals("and")) {
            sCriteriaList.remove(sCriteriaList.size() - 1);
        }
        return calculateCriteria(sCriteriaList);
    }

    public static String removeBrackets(String criteria) {
        String sReturn = "";
        if (criteria.startsWith("((")) {
            sReturn = criteria.substring(2);
        } else if (criteria.startsWith("(")) {
            sReturn = criteria.substring(1);
        } else {
            sReturn = criteria;
        }
        if (sReturn.startsWith("endeca:matches")) {
            if (sReturn.endsWith("))")) {
                sReturn = sReturn.substring(0, sReturn.length() - 1);
            }
        } else {
            if (sReturn.endsWith("))")) {
                sReturn = sReturn.substring(0, sReturn.length() - 2);
            } else if (sReturn.endsWith(")")) {
                sReturn = sReturn.substring(0, sReturn.length() - 1);
            }
        }
        return sReturn;
    }

    public static List<String> calculateCriteria(List<String> criteria) {
        List<String> lstCriteria = new ArrayList<String>();
        int i = 0;
        while (criteria.size() > 0) {
            String sCriterion1 = "";
            String sCriterion2 = "";
            String sCondition = "";
            if (criteria.size() > 2) {
                sCriterion1 = criteria.get(i);
                sCondition = criteria.get(i + 1);
                sCriterion2 = criteria.get(i + 2);
            } else {
                sCriterion1 = criteria.get(0);
            }
            if (sCriterion1.startsWith("endeca:matches")
                    && sCriterion2.startsWith("endeca:matches")) {
                String[] criterion1 = sCriterion1.split("\"");
                String[] criterion2 = sCriterion2.split("\"");
                if (criterion1[1].equals(criterion2[1])) {
                    lstCriteria.add(getAdvanceSearchDisplayName(criterion1[1]) + ": " + criterion1[3] + " " + sCondition + " " + criterion2[3]);
                    criteria.remove(2);
                    criteria.remove(1);
                    criteria.remove(0);
                } else {
                    lstCriteria.add(getAdvanceSearchDisplayName(criterion1[1]) + ": " + criterion1[3]);
                    if (sCondition != null
                            && sCondition.equals("or")) {
                        lstCriteria.add("condition:or");
                    }
                    criteria.remove(1);
                    criteria.remove(0);
                }
            } else if (sCriterion1.startsWith("endeca:matches")
                    && !sCriterion2.startsWith("endeca:matches")) {
                String[] criterion1 = sCriterion1.split("\"");
                lstCriteria.add(getAdvanceSearchDisplayName(criterion1[1]) + ": " + criterion1[3]);
                if (criteria.size() > 1) {
                    criteria.remove(1);
                }
                criteria.remove(0);
            } else if (!sCriterion1.startsWith("endeca:matches")
                    && !sCriterion2.startsWith("endeca:matches")) {
                if (sCriterion1.contains("<=")
                        && sCriterion2.contains(">=")) {
                    String[] criterion1 = sCriterion1.split("<=");
                    String[] criterion2 = sCriterion2.split(">=");
                    if (criterion1[0].equals(criterion2[0])) {
                        if (criterion1[0].contains("date")
                                || criterion1[0].contains("Date")
                                || criterion1[0].contains("DATE")) {
                            criterion1[1] = criterion1[1].substring(4, 6) + "/" + criterion1[1].substring(6) + "/" + criterion1[1].substring(0, 4);
                            criterion2[1] = criterion2[1].substring(4, 6) + "/" + criterion2[1].substring(6) + "/" + criterion2[1].substring(0, 4);
                        }
                        lstCriteria.add(getAdvanceSearchDisplayName(criterion1[0]) + ": " + "between " + criterion1[1] + " " + sCondition + " " + criterion2[1]);
                        criteria.remove(2);
                        criteria.remove(1);
                        criteria.remove(0);
                    } else {
                        if (criterion1[0].contains("date")
                                || criterion1[0].contains("Date")
                                || criterion1[0].contains("DATE")) {
                            criterion1[1] = criterion1[1].substring(4, 5) + "/" + criterion1[1].substring(6) + "/" + criterion1[1].substring(0, 4);
                        }
                        lstCriteria.add(getAdvanceSearchDisplayName(criterion1[0]) + ": " + "less than " + criterion1[1]);
                        criteria.remove(1);
                        criteria.remove(0);
                    }
                } else if (sCriterion1.contains(">=")
                        && sCriterion2.contains("<=")) {
                    String[] criterion1 = sCriterion1.split(">=");
                    String[] criterion2 = sCriterion2.split("<=");
                    if (criterion1[0].equals(criterion2[0])) {
                        if (criterion1[0].contains("date")
                                || criterion1[0].contains("Date")
                                || criterion1[0].contains("DATE")) {
                            criterion1[1] = criterion1[1].substring(4, 6) + "/" + criterion1[1].substring(6) + "/" + criterion1[1].substring(0, 4);
                            criterion2[1] = criterion2[1].substring(4, 6) + "/" + criterion2[1].substring(6) + "/" + criterion2[1].substring(0, 4);
                        }
                        lstCriteria.add(getAdvanceSearchDisplayName(criterion1[0]) + ": " + "between " + criterion1[1] + " " + sCondition + " " + criterion2[1]);
                        criteria.remove(2);
                        criteria.remove(1);
                        criteria.remove(0);
                    } else {
                        if (criterion1[0].contains("date")
                                || criterion1[0].contains("Date")
                                || criterion1[0].contains("DATE")) {
                            criterion1[1] = criterion1[1].substring(4, 6) + "/" + criterion1[1].substring(6) + "/" + criterion1[1].substring(0, 4);
                        }
                        lstCriteria.add(getAdvanceSearchDisplayName(criterion1[0]) + ": " + "less than " + criterion1[1]);
                        criteria.remove(1);
                        criteria.remove(0);
                    }
                }
                if (criteria.size() > 0 && criteria.get(0) != null && criteria.get(0).equals("or")) {
                    lstCriteria.add("condition_immediate:or");
                    criteria.remove(0);
                }
            }
            if (criteria.size() > 0 && criteria.get(0) != null && criteria.get(0).equals("and")) {
                criteria.remove(0);
            }
        }

        List<String> lstCriteriaReturn = new ArrayList<String>();
        for (int j = 0; j < lstCriteria.size(); j++) {
            String sCriterion = lstCriteria.get(j);
            if (sCriterion != null
                    && sCriterion.contains(":")) {
                String[] _fieldValue = sCriterion.split(":");
                if ((j + 1) < lstCriteria.size()) {
                    String sNextCriterion = lstCriteria.get(j + 1);
                    if (sNextCriterion != null
                            && sNextCriterion.equals("condition_immediate:or")) {
                        if ((j + 2) < lstCriteria.size()) {
                            String s3rdCriterion = lstCriteria.get(j + 2);
                            if (s3rdCriterion != null
                                    && !s3rdCriterion.equals("condition_immediate:or")
                                    && !s3rdCriterion.equals("condition:or")
                                    ) {
                                String[] _3rdfieldValue = s3rdCriterion.split(":");
                                if (_fieldValue[1].equals(_3rdfieldValue[1])) {
                                    lstCriteriaReturn.add(_fieldValue[0] + " or " + _3rdfieldValue[0] + ": " + _fieldValue[1]);
                                    j = j + 2;
                                }
                            }
                        }
                    } else {
                        lstCriteriaReturn.add(sCriterion);
                    }
                } else {
                    lstCriteriaReturn.add(sCriterion);
                }
            }
        }

        return lstCriteriaReturn;
    }

    /*public static void main(String argc[]) {
        List<String> criteria = new ArrayList<String>();
        criteria.add("endeca:matches(.,\"Last Name\",\"smith\")");
        criteria.add("and");
        criteria.add("endeca:matches(.,\"First Name\",\"john\")");
        criteria.add("and");
        criteria.add("endeca:matches(.,\"Race\",\"CHINESE\")");
        criteria.add("or");
        criteria.add("endeca:matches(.,\"Race\",\"HISPANIC\")");
        criteria.add("and");
        criteria.add("Revocation_Release_Date_Search>=20000102");
        criteria.add("and");
        criteria.add("Revocation_Release_Date_Search<=20020304");
        criteria.add("or");
        criteria.add("Parole_Date_Search>=20000102");
        criteria.add("and");
        criteria.add("Parole_Date_Search<=20020304");
        criteria.add("and");
        criteria.add("Veh_Year>=1978");
        criteria.add("and");
        criteria.add("Veh_Year<=1979");
        criteria.add("and");
        criteria.add("endeca:matches(.,\"SMT Description\",\"BRAC L LEG\")");
        criteria.add("or");
        criteria.add("endeca:matches(.,\"Last Name\",\"john\")");
        calculateCriteria(criteria);
    }*/

    public static String encodeGeoCodeCriteria(String queryString) {
        if (queryString != null && queryString.indexOf("Nf=geocode") > 0) {
            int iNfGeoStart = queryString.indexOf("Nf=geocode");
            int iNfGeoEnd = queryString.indexOf("&", iNfGeoStart);
            String preGeo = queryString.substring(0, iNfGeoStart);
            String geoStr = "";
            if (iNfGeoEnd > 0) {
                geoStr = queryString.substring(iNfGeoStart, iNfGeoEnd);
            } else {
                geoStr = queryString.substring(iNfGeoStart);
            }
            geoStr = geoStr.replaceAll("\\Q+\\E", "%2B");

            // 2018-03-08 emil replace | with ASCII %7C
            geoStr = geoStr.replaceAll("\\Q|\\E", "%7C");

            String postGeo = "";
            if (iNfGeoEnd > 0) {
                postGeo = queryString.substring(iNfGeoEnd);
            }
            queryString = preGeo + geoStr + postGeo;
        }
        return queryString;
    }

    public static String decodeGeoCodeCriteria(String queryString) {
        if (queryString != null && queryString.indexOf("Nf=geocode") > 0) {
            int iNfGeoStart = queryString.indexOf("Nf=geocode");
            int iNfGeoEnd = queryString.indexOf("&", iNfGeoStart);
            String preGeo = queryString.substring(0, iNfGeoStart);
            String geoStr = "";
            if (iNfGeoEnd > 0) {
                geoStr = queryString.substring(iNfGeoStart, iNfGeoEnd);
            } else {
                geoStr = queryString.substring(iNfGeoStart);
            }
            geoStr = geoStr.replaceAll("\\Q%2B\\E", "+");
            geoStr = geoStr.replaceAll("\\Q \\E", "+");

            // 2018-03-08 emil Replace ASCII %7C with |
//            geoStr = geoStr.replaceAll("\\Q%7C\\E", "|");

            String postGeo = "";
            if (iNfGeoEnd > 0) {
                postGeo = queryString.substring(iNfGeoEnd);
            }
            queryString = preGeo + geoStr + postGeo;
        }
        return queryString;
    }

    @SuppressWarnings("unchecked")
    public static String getSearchQuery(HttpServletRequest request) {
        String searchQuery = "";
        Enumeration<String> en = request.getParameterNames();
        while (en.hasMoreElements()) {
            String paramName = (String) en.nextElement();
            String paramValue = request.getParameter(paramName);
            if (paramName.equalsIgnoreCase("zoomlevel")
                    || paramName.equalsIgnoreCase("centerpoint")
                    || paramName.equalsIgnoreCase("keepthis")) {
                continue;
            }
            if (searchQuery.equals("")) {
                searchQuery = paramName + "=" + paramValue;
            } else {
                searchQuery = searchQuery + "&" + paramName + "=" + paramValue;
            }
        }

        return searchQuery;
    }

    public static String encodeSpecialCharsInSearchQuery(String searchQuery) {

        // emil 2018-03-07 Fix |, >, <, and " characters encoding issue in Tomcat
        searchQuery = searchQuery.replaceAll("\\Q|\\E", "%7C");
        searchQuery = searchQuery.replaceAll("\\Q\"\\E", "%22");
        searchQuery = searchQuery.replaceAll("\\Q<=\\E", "%3C=");
        searchQuery = searchQuery.replaceAll("\\Q>=\\E", "%3E=");

        return searchQuery;
    }

    public static String decodeSpecialCharsInSearchQuery(String searchQuery) {

        // emil 2018-03-07 Replace ASCII codes for |, <, >, and " with characters
        // to address encoding issue in Tomcat
        searchQuery = searchQuery.replaceAll("%7C", "|");
        searchQuery = searchQuery.replaceAll("%22", "\"");
        searchQuery = searchQuery.replaceAll("%3C=", "<=");
        searchQuery = searchQuery.replaceAll("%3E=", ">=");

        return searchQuery;
    }

    public static int changeEndecaPort(int deadPort) {
        int changePortIndex = 0;
        for (int i = 0; i < getEndecaPortSize(); i++) {
            if (getEndecaPort(i) == deadPort) {
                if (i == getEndecaPortSize() - 1) {
                    changePortIndex = 0;
                } else {
                    changePortIndex = i + 1;
                }
                break;
            }
        }
        return getEndecaPort(changePortIndex);
    }

    public static int getEndecaPort(int index) {
        return enePort_[index];
    }

    public static List readCountiesXML(ServletContext sc) {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document dom = null;
        List listMapCounties = new ArrayList();
        String fileName = sc.getRealPath("/WEB-INF/" + UI_Props.getInstance().getValue("COUNTY_FILE_PATH"));
        try {
            if (fileName == null) {
                URL resourceURL = sc.getResource("/WEB-INF/" + UI_Props.getInstance().getValue("COUNTY_FILE_PATH"));
                if (resourceURL != null) {
                    fileName = resourceURL.getPath();
                } else {
                    logger.error("County file is empty..");
                }
            }
            File file = new File(fileName);
            db = dbf.newDocumentBuilder();
            dom = db.parse(file);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Element docEle = dom.getDocumentElement();
        NodeList nl = docEle.getElementsByTagName("COUNTY");
        String code = null;
        String name = null;

        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                Element el = (Element) nl.item(i);

                NodeList countyCodeList = el.getElementsByTagName("CODE");
                Element countyCodeElement = (Element) countyCodeList.item(0);
                NodeList textCCList = countyCodeElement.getChildNodes();
                code = ((Node) textCCList.item(0)).getNodeValue().trim();

                NodeList countyNameList = el.getElementsByTagName("NAME");
                Element countyNameElement = (Element) countyNameList.item(0);
                NodeList textCNList = countyNameElement.getChildNodes();
                name = ((Node) textCNList.item(0)).getNodeValue().trim();

                listMapCounties.add(code + ":" + name);
            }
            Collections.sort(listMapCounties);
        }
        return listMapCounties;
    }

    public static String getConfigParam(String paramName) {
        try {
            return PLMDatabaseUtil.getConfigParamByName(paramName);
        } catch (SQLException e) {
            e.printStackTrace();
            // on DB exception, return default values from property files
            return UI_Props.getInstance().getValue(paramName);
        }
    }
}