package com.plm.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;

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


/*class SMTPAuthenticator extends javax.mail.Authenticator {

    public PasswordAuthentication getPasswordAuthentication() {    	
        String username = UI_Props.getInstance().getValue("EMAIL_AGENT_USERNAME");
        String password = UI_Props.getInstance().getValue("EMAIL_AGENT_PASSWORD");
        if(username != null && password!=null)
        	return new PasswordAuthentication(username, password);
        else
        	return null;
    }
}*/

public class PLMSearchUtil {
	private static final Logger logger = Logger.getLogger(PLMSearchUtil.class);
//    static Authenticator auSMTPAuth = new SMTPAuthenticator();
	public static String downloadFileOutputpath =UI_Props.getInstance().getValue(PLMConstants.DEFAULT_DATA_PHOTO_DOWNLOAD_PATH_LABEL);
	public static String paroleeDataFilename =UI_Props.getInstance().getValue(PLMConstants.DEFAULT_DATA_DOWNLOAD_FILE_LABEL);
	public static String paroleePhotoFilename =UI_Props.getInstance().getValue(PLMConstants.DEFAULT_PHOTO_DOWNLOAD_FILE_LABEL);
	private static int portIndex_ = 0;
	private static int hostIndex_ = 0;
	private static final String[] enePort_ = UI_Props.getInstance().getValue(UI_Props.ENE_PORT).split("\\s");
	private static final String[] eneHost_ = UI_Props.getInstance().getValue(UI_Props.ENE_HOST).split("\\s");
	private final static String sGoogleMapEnv = UI_Props.getInstance().getValue("GOOGLE_MAP_ENV");
	//private static String nextEnePort_ = null; 

	private static Navigation getCurrentNavigation(String url) throws ENEQueryException{
		Navigation nav =null;  	  
		UrlGen sUrlg = null;
		if(url != null){
			sUrlg = new UrlGen(url, "UTF-8");; 
		}
		if(sUrlg != null){
			String eneHost = PLMSearchUtil.getEndecaHost();
			String enePort = PLMSearchUtil.getEndecaPort();
			String queryString = sUrlg.toString();
			ENEConnection nec = new HttpENEConnection(eneHost,enePort);
			AdvancedENEQuery query = new AdvancedENEQuery(queryString, nec);
        	long beforeTime=System.currentTimeMillis();
        	Map resultMap = null;
        	int cnt = 1;
        	try{
        		resultMap = query.process().get(0);
        	}catch(ENEConnectionException eneConn){
        		do{
        			eneHost	= PLMSearchUtil.getEndecaHost();
        			enePort	= PLMSearchUtil.getEndecaPort();
        			nec = new HttpENEConnection(eneHost,enePort);
        			query.setConnection(nec);
        			cnt++;
        			//logger.debug("counter: " + cnt);
        		}while(!query.dgraphIsAlive() && cnt<=PLMSearchUtil.getEndecaPortSize());
        		
        		resultMap = query.process().get(0);
        	}			
        	long afterTime=System.currentTimeMillis() - beforeTime;
        	logger.info("Time endeca took to process following query is " + afterTime + " milliseconds");
        	logger.info("Query: " + queryString);
			ENEQueryResults qr = (ENEQueryResults)resultMap.get("RESULT");
			if(qr.containsNavigation()){
				nav = qr.getNavigation();
			}
		}
		return nav;
	}	
		
	private  static DimensionList getCompleteDimensions() throws ENEQueryException{
		DimensionList dimList = null;
		String queryString = "N="+UI_Props.getInstance().getValue(UI_Props.ENE_ROOT);
		Navigation nav = getCurrentNavigation(queryString); 
		if(nav!= null){
			dimList= nav.getCompleteDimensions();		
		}	
		return dimList;
	}	
	
	public static ArrayList<String> getAllCities() throws ENEQueryException{
		ArrayList<String> allCities = new ArrayList<String>();
		 DimensionList dimList=  getCompleteDimensions();
		 if(dimList!=null){
				 Dimension  dim = dimList.getDimension("Res City");
				 if(dim != null){
					 DimValList  refs = dim.getRefinements();
					 for(int i =0;i<refs.size();i++){
						 DimVal ref = refs.getDimValue(i);
						 allCities.add(ref.getName() + "|" + ref.getId());
					 }
				 }		
		 }
		return allCities;
	}

	public static ArrayList<String> getCitiesForCounty(String sCounty) throws ENEQueryException{
		ArrayList<String> allCitiesForCounty = new ArrayList<String>();
		ENEQueryResults qr=null;
		Dimension dim =  null;
		
		if(sCounty != null){
			String queryString ="N="+sCounty+"&Ns=City|0";
			String eneHost = PLMSearchUtil.getEndecaHost();
			String enePort = PLMSearchUtil.getEndecaPort();
			ENEConnection nec = new HttpENEConnection(eneHost,enePort);
			AdvancedENEQuery query = new AdvancedENEQuery(queryString, nec);
        	Map resultMap = null;
        	int cnt = 1;
        	try{
        		resultMap = query.process().get(0);
        	}catch(ENEConnectionException eneConn){
        		do{
        			eneHost	= PLMSearchUtil.getEndecaHost();
        			enePort	= PLMSearchUtil.getEndecaPort();
        			nec = new HttpENEConnection(eneHost,enePort);
        			query.setConnection(nec);
        			cnt++;
        			logger.debug("counter: " + cnt);
        		}while(!query.dgraphIsAlive() && cnt<=PLMSearchUtil.getEndecaPortSize());        		
        		resultMap = query.process().get(0);
        	}
			qr = (ENEQueryResults)resultMap.get("RESULT");
		}

		dim = qr.getNavigation().getRefinementDimensions().getDimension("Res City");
		if(dim != null){
        	DimValList refs = dim.getRefinements();
       	 if(refs != null){
       		 for(int i = 0; i < refs.size(); i++){
       			 DimVal ref = refs.getDimValue(i);
            		 allCitiesForCounty.add(ref.getName() + "|" + ref.getId());
       		 }
       	 }
			
		}
		return allCitiesForCounty;
	}
	
	public static ArrayList<String> getAllHairColor() throws ENEQueryException{
		ArrayList<String> allHairColor= new ArrayList<String>();
		 DimensionList dimList=  getCompleteDimensions();
		 if(dimList!=null){
			 Dimension  dim = dimList.getDimension("Hair Color");
			 if(dim != null){
				 DimValList  refs = dim.getRefinements();
				 for(int i =0;i<refs.size();i++){
					 DimVal ref = refs.getDimValue(i);
					 allHairColor.add(ref.getName());
				 }
			 }		
		 }
		return allHairColor;
	}
	
	public static ArrayList<String> getAllEthnicity() throws ENEQueryException{
		ArrayList<String> allEthnicity= new ArrayList<String>();
		 DimensionList dimList=  getCompleteDimensions();
		 if(dimList!=null){
			 Dimension  dim = dimList.getDimension("Ethnicity");
			 if(dim != null){
				 DimValList  refs = dim.getRefinements();
				 for(int i =0;i<refs.size();i++){
					 DimVal ref = refs.getDimValue(i);
					 allEthnicity.add(ref.getName());
				 }
			 }		
		 }
		return allEthnicity;
	}
	
	public static ArrayList<String> getAllOffenseCodes() throws ENEQueryException{
		ArrayList<String> allOffenseCodes = new ArrayList<String>();
		 DimensionList dimList=  getCompleteDimensions();
		 if(dimList!=null){
			 Dimension  dim = dimList.getDimension("Commitment Offense");
			 if(dim != null){
				 DimValList  refs = dim.getRefinements();
				 for(int i =0;i<refs.size();i++){
					 DimVal ref = refs.getDimValue(i);
					 allOffenseCodes.add(ref.getName());
				 }
			 }		
		 }
		return allOffenseCodes;
	}
	
	public static ArrayList<String> getAllSmtDesc() throws ENEQueryException{
		ArrayList<String> allAllSmtDesc = new ArrayList<String>();
		 DimensionList dimList=  getCompleteDimensions();
		 if(dimList!=null){
			 Dimension  dim = dimList.getDimension("SMT Code");
			 if(dim != null){
				 DimValList  refs = dim.getRefinements();
				 for(int i =0;i<refs.size();i++){
					 DimVal ref = refs.getDimValue(i);
					 allAllSmtDesc.add(ref.getName());
				 }
			 }		
		}
		return allAllSmtDesc;
	}
	
	public static String convertNValueToName(String url) throws ENEQueryException{
		
		Dimension dim =null;
		DimensionList descDimesions= null;
		DimVal root =null;
		DimVal desc =null;
		//long lNValue=0;
		String sRootName = null;
		String sDescName = null;
		String dbString = "";
		//String sNValue=null;
		String nameString = "";
		Navigation nav = getCurrentNavigation(url);	
		if(nav != null){
			descDimesions = nav.getDescriptorDimensions();
		}
		if(descDimesions != null && descDimesions.size()>0){
			
			for (int i=0; i < descDimesions.size(); i++) {
				// Get individual dimension
				dim = (Dimension)descDimesions.get(i);
				// Get root for dimensionD
				root = dim.getRoot();
				desc = dim.getDescriptor();
				sRootName=root.getName();//city
				sDescName = desc.getName();//mumbai
				dbString =  dbString+sRootName+"|"+sDescName+"+";
			}
			dbString = dbString.substring(0,dbString.length()-1);			
		}
		String[] stokens = url.split("&");
		for (int i =0;i<stokens.length;i++){			
			if(stokens[i].indexOf("N=")>=0){	
				if(dbString.length()==0)
					dbString="0";
				nameString = nameString+"N="+dbString+"&";
			} else if(stokens[i].indexOf("Ne=")<0){
				nameString = nameString + stokens[i]+"&";
			}
		}
		return nameString.substring(0, nameString.length()-1);
	}
	
	
	public static Map<String,String> getSavedSearches(String query,String user) throws SQLException, ENEQueryException{
		return PLMDatabaseUtil.getSavedSearches(query, user);
	}
	
	public static String getSavedSearchByID(String id) throws SQLException{
		return PLMDatabaseUtil.getSavedSearchByID(id);
	}
	private static ENEQueryResults getENEQueryResults(String url) throws ENEQueryException{
		ENEQueryResults qr=null;
		//Navigation nav =null;  	  
		UrlGen sUrlg = null;
		if(url != null){
			sUrlg = new UrlGen(url, "UTF-8");; 
		}
		if(sUrlg != null){
			String eneHost = PLMSearchUtil.getEndecaHost();
			String enePort = PLMSearchUtil.getEndecaPort();
			String queryString = sUrlg.toString();
			ENEConnection nec = new HttpENEConnection(eneHost,enePort);
			AdvancedENEQuery query = new AdvancedENEQuery(queryString, nec);
			//Map resultMap = query.process().get(0);
        	Map resultMap = null;
        	int cnt = 1;
        	try{
        		resultMap = query.process().get(0);
        	}catch(ENEConnectionException eneConn){
        		do{
        			eneHost	= PLMSearchUtil.getEndecaHost();
        			enePort	= PLMSearchUtil.getEndecaPort();
        			nec = new HttpENEConnection(eneHost,enePort);
        			query.setConnection(nec);
        			cnt++;
        			logger.debug("counter: " + cnt);
        		}while(!query.dgraphIsAlive() && cnt<=PLMSearchUtil.getEndecaPortSize());        		
        		resultMap = query.process().get(0);
        	}
			qr = (ENEQueryResults)resultMap.get("RESULT");
		}
		return qr;
	}

	public static String convertNameToNvalue(String url) throws ENEQueryException{
		//String url = "Dg=Personal+Information&N=Birth Year|1900-1920+SMT Code|TATTOO LEFT ARM&refineTabID=1";

        // emil 2018-03-07 decode ASCII into characters in URL
        url = decodeSpecialCharsInSearchQuery(url);

		String nValues = "";
		String[] stokens = url.split("&");
		String nameString ="";
		HashMap<String,String> dimNameValuesMap = new HashMap<String, String>();
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
		DimensionList completeDimensions =getCompleteDimensions();
		boolean flag = false;
		String name = "";
		String value = "";
		
		for (int i =0;i<stokens.length;i++){
			if(stokens[i].indexOf("N=")>=0){
				stokens[i] = stokens[i].substring(2,stokens[i].length());
				stokens[i]=stokens[i].replace("+/-", "####");	
				String nameValuePairs[] = stokens[i].split("\\+");
				long prevNValue =0;
				for(int j =0 ;j<nameValuePairs.length;j++){
					if(nameValuePairs[j].indexOf("####")>=0){
						nameValuePairs[j] = nameValuePairs[j].replace("####","+/-");
					}					
					if(nameValuePairs[j].indexOf("|")>0){
						name = nameValuePairs[j].substring(0,nameValuePairs[j].indexOf("|"));
						try{
							value = URLDecoder.decode(nameValuePairs[j].substring(nameValuePairs[j].indexOf("|")+1,nameValuePairs[j].length()), "UTF-8");
						}catch(UnsupportedEncodingException uee){
							logger.error("Error decoding the dimension value");
						}
						dimNameValuesMap.put(value,name);
						queryBuilder.append(value);
						queryBuilder.append("+");
					}// end of for(int j =0 ...
						if(completeDimensions!=null){
							Dimension dim = completeDimensions.getDimension(name);
							if(dim!=null){
								DimValList refs = dim.getRefinements();
								for(int k =0;k<refs.size();k++){
									if(refs.getDimValue(k).getName().equals(value.trim())){
										nValues = nValues+"+"+refs.getDimValue(k).getId();
										prevNValue = refs.getDimValue(k).getId();
										break;
									}else{
										String queryStr = queryBuilder.substring(0, queryBuilder.length()-1);
										queryStr = queryStr+ "&Dx=mode+matchAny";
										ENEQueryResults qr = getENEQueryResults(queryStr);										
										if (qr.containsDimensionSearch()) {
											DimensionSearchResult dsr = qr.getDimensionSearch();
											// Get results grouped by dimension groups
											DimensionSearchResultGroupList dsrgl = dsr.getResults();
											for (int a=0; a < dsrgl.size(); a++) {
												// Get individual result dimension group
												DimensionSearchResultGroup dsrg = (DimensionSearchResultGroup)dsrgl.get(a);
												// Get roots for dimension group
												DimValList roots = dsrg.getRoots();
												for (int l=0; l < roots.size(); l++) {
													// Get dimension root
													DimVal root = (DimVal)roots.get(l);
													String rootName = root.getName();
													for(int kl=0;kl<dsrg.size();kl++){
															DimLocationList dll = (DimLocationList)dsrg.get(kl);
															for (int ak=0; ak<dll.size(); ak++) {
																// Get individual dimlocation from result
																DimLocation dl = (DimLocation)dll.get(ak);
																String dimValName = dl.getDimValue().getName();
																if(dimNameValuesMap.containsKey(dimValName)){
																	String rootNameInMap =(String)dimNameValuesMap.get(dimValName);
																	dimNameValuesMap.remove(dimValName);
																	if(rootName.equals(rootNameInMap)){
																		long dimValue = dl.getDimValue().getId();
																		if(dimValue == prevNValue){
																			continue;
																		}
																		nValues = nValues+"+"+dl.getDimValue().getId();
																		flag = true;																		
																	}
																}
															}
														}
												}// end of for(j=0....
											}// end of for(int a=0...
										}// end of if usq.getDimSearchTerms()
									}
									
									if(flag){
										flag = false;
										break;	
									}
								}// end for for loop
								
							}
						}
					}
				//}		
				if(nValues.length()>0){
					nValues = nValues.substring(1,nValues.length());
					nameString = nameString+"N="+nValues.substring(0,nValues.length())+"&";	
				}else{
					nameString = nameString+"N="+"0"+"&";	
				}							
			}else{
				nameString = nameString + stokens[i]+"&";
			}
		}

        nameString = nameString.substring(0, nameString.length() - 1);

        // emil 2018-03-09 encode special characters to ASCII after parsing is done to create a valid URL
        nameString = encodeSpecialCharsInSearchQuery(nameString);

        return nameString;
	}
	public static String getAdvanceSearchDisplayName(String field){
		String displayName = "";
		if(field.indexOf("\"")>=0){
			field = field.substring(1,field.length()-1);
		}
		if(field.indexOf("%22")>=0){
			field = field.substring(field.indexOf("%22")+3, field.lastIndexOf("%22")-1-field.indexOf("%22")+1);
		}
		logger.debug("field: " + field);
		if(field.equals("Last Name"))
			displayName ="Last Name";
		if(field.equals("First Name"))
			displayName ="First Name";
		if(field.equals("Middle Name"))
			displayName ="Middle Name";
		if(field.equals("Alias Last Name"))
			displayName ="Alias Last Name";
		if(field.equals("Alias First Name"))
			displayName ="Alias First Name";
		if(field.equals("Moniker Info"))
			displayName ="Moniker";
		if(field.equals("City"))
			displayName ="Res. City";
		if(field.equals("County Name"))
			displayName ="Res. County";
		if(field.equals("Zip"))
			displayName ="Zip Code";
		if(field.equals("Birth State Name"))
			displayName ="Birth State";
		if(field.equals("Birth Date Display"))
			displayName ="Date of Birth";
		if(field.equals("SSA Number"))
			displayName ="SSN#";
		if(field.equals("CDC Number"))
			displayName ="CDC#";
		if(field.equals("FBI Number"))
			displayName ="FBI#";
		if(field.equals("CII Number"))
			displayName ="CII#";
		if(field.equals("Vehicle License Plate"))
			displayName ="Vehicle License No.";		
		if(field.equals("PC 290 REQ"))
			displayName ="PC290 Sex Off.";
		if(field.equals("PC 457 REQ"))
			displayName ="PC457.1Arson";
		if(field.equals("HS REQ"))
			displayName ="HS 11590 Drugs";
		if(field.equals("PC 3058 REQ"))
			displayName ="PC3058.6 Felony Violation";
		if(field.equals("Tattoo_Pict_Text"))
			displayName ="Tattoo Criteria";
		if(field.equals("Race"))
			displayName ="Ethnicity";
		if(field.equals("Haircolor"))
			displayName ="Hair Color";
		if(field.equals("Offense Code"))
			displayName ="Offense Code";
		if(field.equals("SMT Description"))
			displayName ="SMT Code";
		if(field.equals("Height (+/- 2 inches)") || field.equals("Height (%2B/- 2 inches)"))
			displayName ="Height (+/- 2 inches)";
		if(field.equals("Weight (+/- 10 lb)") || field.equals("Weight (%2B/- 10 lb)"))
			displayName ="Weight (+/- 10 lb)";
		return displayName;
	}
	
	public static void postAgentMail( String recipient, String subject, String message , String from) throws MessagingException {
	    boolean debug = false;
	     //Set the host smtp address
	    Properties props = new Properties();
	    String smtpHost = UI_Props.getInstance().getValue("SMTP_MAIL_HOST");
	    String smtpPort = UI_Props.getInstance().getValue("SMTP_MAIL_PORT");
	    props.put("mail.smtp.host", smtpHost);
	    props.put("mail.smtp.port", smtpPort);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "false");     // Emil, 2018-01-31: MDC smtp servers don't need auth
	     
	    // create some properties and get the default Session
//      Session ssMailSession = Session.getInstance(prMailProps, auSMTPAuth);
        Session ssMailSession = Session.getInstance(props);
		ssMailSession.setDebug(debug);

	    // create a message
	    Message msg = new MimeMessage(ssMailSession);

	    if(msg != null){
	    	// set the from and to address
	    	InternetAddress addressFrom = null;
	    	if(from != null){
	    		addressFrom = new InternetAddress(from);
	    		if(addressFrom != null)
	    			msg.setFrom(addressFrom);
	    	}
	    	
	    	InternetAddress addressTo = null;
	    	if(recipient != null){
	    		addressTo = new InternetAddress(recipient);
	    		if(addressTo != null){	    			
	    			msg.setRecipient(Message.RecipientType.TO, addressTo);   
	    		}
	    	}
	    	 // Setting the Subject and Content Type
	    	 msg.setSubject(subject);
	    	 message = message+"<br /><br /><br /><br />";
	    	 message = message+ UI_Props.getInstance().getValue("EMAIL_RECIPIENT_DISCLAIMER_TEXT");
	    	 msg.setContent(message, "text/html");
	    	 Transport.send(msg);
	    }
	  	  
	    // Optional : You can also set your custom headers in the Email if you Want
	    //msg.addHeader("MyHeaderName", "myHeaderValue");	  
	}	
	
	public static String getFormattedPLMURL(HttpServletRequest request,String landingPage) {
//		String connType = (request.isSecure())? "https://" : "http://";

        String connType;
        /*if ("prod".equalsIgnoreCase(sGoogleMapEnv) || "uat".equalsIgnoreCase(sGoogleMapEnv)) {
            connType = "https://";
        } else {
            connType = "http://";
        }*/
        connType = "https://";
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
	/*
	public static Collection getListOfFiles(String userName, String extension) {
		String fileOutputpath =UI_Props.getInstance().getValue("FILE_OUTPUT_PATH");
		return FileUtils.listFiles(new File(fileOutputpath), new WildcardFileFilter("*"+userName+"."+extension), null);
	}
*/	
		
	@SuppressWarnings("unchecked")
	public static Collection<File> getListOfFiles(String userName,String ext) {
		logger.info("downloadFileOutputpath1 : " + downloadFileOutputpath);
		File file = new File(downloadFileOutputpath);
		logger.info("directory1 + " + file.isDirectory());
		logger.info("file1 + " + file.isFile());
		return FileUtils.listFiles(new File(downloadFileOutputpath), new WildcardFileFilter(userName+"_"+"*"+"."+ext), null);
	}

	@SuppressWarnings("unchecked")
	public static Collection<File> getListOfDataFiles(String userName,String ext) {
		logger.info("downloadFileOutputpath2 : " + downloadFileOutputpath);
		String sWildCardFileFilter = paroleeDataFilename.replace("{USERNAME}", userName).replace("{DATE_TIME}","*").replace("{DATA_TYPE}", "*");
		return FileUtils.listFiles(new File(downloadFileOutputpath), new WildcardFileFilter(sWildCardFileFilter+"."+ext), null);
	}
		
	@SuppressWarnings("unchecked")
	public static Collection<File> getListOfPhotoFiles(String userName,String ext) {
		logger.info("downloadFileOutputpath3 : " + downloadFileOutputpath);
		String sWildCardFileFilter = paroleePhotoFilename.replace("{USERNAME}", userName).replace("{DATE_TIME}","*");
		return FileUtils.listFiles(new File(downloadFileOutputpath), new WildcardFileFilter(sWildCardFileFilter+"."+ext), null);
	}

	public static ENEQueryResults getQueryResults(String url) throws ENEQueryException{
		//Navigation nav =null;
		UrlGen sUrlg = null;
		ENEQueryResults qr = null;
		if(url != null){
			sUrlg = new UrlGen(url, "UTF-8");; 
		}
		if(sUrlg != null){
			String eneHost = PLMSearchUtil.getEndecaHost();
			String enePort = PLMSearchUtil.getEndecaPort();
			String queryString = sUrlg.toString();
			ENEConnection nec = new HttpENEConnection(eneHost,enePort);
			AdvancedENEQuery query = new AdvancedENEQuery(queryString, nec);
        	long beforeTime=System.currentTimeMillis();
        	Map resultMap = null;
        	int cnt = 1;
        	try{
        		resultMap = query.process().get(0);
        	}catch(ENEConnectionException eneConn){
        		do{
        			eneHost	= PLMSearchUtil.getEndecaHost();
        			enePort	= PLMSearchUtil.getEndecaPort();
        			nec = new HttpENEConnection(eneHost,enePort);
        			query.setConnection(nec);
        			cnt++;
        			//logger.debug("counter: " + cnt);
        		}while(!query.dgraphIsAlive() && cnt<=PLMSearchUtil.getEndecaPortSize());
        		
        		resultMap = query.process().get(0);
        	}			
        	long afterTime=System.currentTimeMillis() - beforeTime;
        	logger.info("Time endeca took to process following query is " + afterTime + " milliseconds");
        	logger.info("Query: " + queryString);
			qr = (ENEQueryResults)resultMap.get("RESULT");
		}
		return qr;
	}	
	public static void logExeception(String e){		
		logger.info(e);
	}
	
	public synchronized static String getEndecaPort(){
		if(enePort_.length==1)
			return enePort_[0];
		
		if(portIndex_==enePort_.length)
			portIndex_=0;
		
		return enePort_[portIndex_++];
	}
	
	public static int getEndecaPortSize(){
		return enePort_.length;
	}
	
	public synchronized static String getEndecaHost(){
		if(eneHost_.length==1)
			return eneHost_[0];
		
		if(hostIndex_==eneHost_.length)
			hostIndex_=0;
		
		return eneHost_[hostIndex_++];
	}
	/**
	 * This method is used to write off line audit data to text file when database is down.
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
	 *This method is used to test whether PLM search database is down or not. 
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
		}finally{
			conn = null;
		}
		return bTestConn;
	}

	public static ArrayList<String> getAllHeight() throws ENEQueryException{
		ArrayList<String> allHeight= new ArrayList<String>();
		 DimensionList dimList = getCompleteDimensions();
		 if(dimList!=null){
			 Dimension dim = dimList.getDimension("Height (+/- 2 inches)");
			 if(dim != null){
				 DimValList refs = dim.getRefinements();
				 for(int i=0;i<refs.size();i++){
					 DimVal ref = refs.getDimValue(i);
					 allHeight.add(ref.getName());
				 }
			 }		
		 }
		return allHeight;
	}
	
	public static ArrayList<String> getAllWeight() throws ENEQueryException{
		ArrayList<String> allWeight= new ArrayList<String>();
		 DimensionList dimList = getCompleteDimensions();
		 if(dimList!=null){
			 Dimension dim = dimList.getDimension("Weight (+/- 10 lb)");
			 if(dim != null){
				 DimValList refs = dim.getRefinements();
				 for(int i=0;i<refs.size();i++){
					 DimVal ref = refs.getDimValue(i);
					 allWeight.add(ref.getName());
				 }
			 }		
		 }
		return allWeight;
	}

	public static ArrayList<String> getAllCounties() throws ENEQueryException{
		ArrayList<String> allCountys = new ArrayList<String>();
		 DimensionList dimList=  getCompleteDimensions();
		 if(dimList!=null){
			 Dimension  dim = dimList.getDimension("Res County");
			 if(dim != null){
				 DimValList  refs = dim.getRefinements();
				 for(int i =0;i<refs.size();i++){
					 DimVal ref = refs.getDimValue(i);
					 //allCountys.add(ref.getName());
					 //System.out.println("id:"+ref.getId() + " dimid:"+ ref.getDimensionId() + " dimname:" + ref.getDimensionName() + " name:"+ ref.getName());
					 allCountys.add(ref.getName() + "|" + ref.getId());
				 }
			 }		
		 }
		return allCountys;
	}
	
	public static ArrayList<String> getAllBirthStates() throws ENEQueryException{
		ArrayList<String> allBirthStates = new ArrayList<String>();
		DimensionList dimList=  getCompleteDimensions();
		if(dimList!=null){
			Dimension  dim = dimList.getDimension("Birth State");
			if(dim != null){
				DimValList  refs = dim.getRefinements();
				for(int i =0;i<refs.size();i++){
					DimVal ref = refs.getDimValue(i);
					allBirthStates.add(ref.getName());
				}
			}		
		}
		return allBirthStates;
	}

	public static String getFormattedIISURL(HttpServletRequest request,String landingPage) {
		String connType = (request.isSecure())? "https://" : "http://";
		String sbURL = connType +
				request.getHeader("Host") +
                request.getContextPath() +
				landingPage;
		return sbURL;
	}

	public static String encodeSpecialCharsInSearchQuery(String searchQuery) {

		// emil 2018-03-07 Fix | and " characters encoding issue in Tomcat
		searchQuery = searchQuery.replaceAll("\\Q|\\E", "%7C");
		searchQuery = searchQuery.replaceAll("\\Q\"\\E", "%22");

		return searchQuery;
	}

	public static String decodeSpecialCharsInSearchQuery(String searchQuery) {

		// emil 2018-03-07 Replace ASCII codes for | and " with characters
		// to address encoding issue in Tomcat
		searchQuery = searchQuery.replaceAll("%7C", "|");
		searchQuery = searchQuery.replaceAll("%22", "\"");

		return searchQuery;
	}
}