package com.plm.idcs;

/*
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.security.auth.Subject;
import javax.servlet.ServletException;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import weblogic.security.Security;
import weblogic.security.spi.WLSGroup;
import weblogic.security.spi.WLSUser;

import com.endeca.ui.constants.UI_Props;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class RetrieveIDCSUserInfo {
	
	private static final Logger logger = Logger.getLogger(RetrieveIDCSUserInfo.class);
	String idcsHost = "";
	String idcsClientSecret = "";
	String idcsClientID = "";
	String idcsGetAccessTokenUri = "";
	String idcsCreateUserUri = "";
	String idcsGetGroupUri = "";
	String idcsChangePasswordUri = "";
	Map mapGroup = new TreeMap();
	Map mapUser = new TreeMap();
	
	public void getGroupAndUser(){	
		int grp = 1;
		int usr = 1;		
		String loggedUser = null; 
		String username = null; 	
		Subject subject = Security.getCurrentSubject();	
		for(Principal p : subject.getPrincipals()) {
		    if(p instanceof WLSGroup ) {		    	
		    	mapGroup.put("Group #" + grp, p.getName());
		        grp++;
		    } else if (p instanceof WLSUser) {
		    	username = p.getName();
				loggedUser = p.getName();				
				mapUser.put("User #" + usr, p.getName());
		        usr++;
		    }
		}
    
	}
	public String getUserId(){		
		String userId = null;
		getGroupAndUser();
		Iterator imapUser = mapUser.entrySet().iterator();
	    while (imapUser.hasNext()) {
	       Map.Entry entry = (Map.Entry) imapUser.next();
	       String key = (String) entry.getKey();
	       String value = (String) entry.getValue();	      
	       userId = value;
	    }
	    return userId;
	}
	public String getUserGroups(){
		getGroupAndUser();
		StringBuffer groups = new StringBuffer();
		Iterator imapGroup = mapGroup.entrySet().iterator();
	    while (imapGroup.hasNext()) {
	       Map.Entry entry = (Map.Entry) imapGroup.next();
	       String key = (String) entry.getKey();
	       String value = (String) entry.getValue();
	   
	      	 if(!imapGroup.hasNext()){ 
	      		 groups.append(value );
	      		 }else{
	      			 groups.append(value + ":");
	      		 }
	      }	
	    return groups.toString();
	}
	
	public String getUserInfo(String userId){
		String userInfo = null;
		try {
			fetchProperties();
			String accessToken = getIDCSaccessToken();				
			userInfo = getIDCSUser(accessToken, userId);			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userInfo;
	}
	
	public void fetchProperties() throws ServletException, IOException {

		idcsHost = UI_Props.getInstance().getValue("idcshost");		
		idcsClientSecret = UI_Props.getInstance().getValue("idcsclientsecret");
		idcsClientID = UI_Props.getInstance().getValue("idcsclientid");
		idcsGetAccessTokenUri = UI_Props.getInstance().getValue("getaccesstokenuri");
		idcsCreateUserUri = UI_Props.getInstance().getValue("createuseruri");
		idcsGetGroupUri = UI_Props.getInstance().getValue("searchgroupuri");
		idcsChangePasswordUri = UI_Props.getInstance().getValue("changepassworduri");		
		
	}
	
	public String getIDCSaccessToken() {
		String basicAuthzToken = "Basic " + createBasicAuthZToken();		
		String contentType = "application/x-www-form-urlencoded";
		MultivaluedMap formData = new MultivaluedMapImpl();
		formData.add("grant_type", "client_credentials");
		formData.add("scope", "urn:opc:idm:__myscopes__");
		String token = callRestAPI(idcsGetAccessTokenUri, idcsHost,
				basicAuthzToken, "POST", formData, contentType, null);
		JSONObject obj = new JSONObject(token);

		return obj.getString("access_token");
	}
	public String createBasicAuthZToken() {		
		String token = "";		 
		String tempString = idcsClientID + ":" + idcsClientSecret;
		byte[] byteArraytoken = tempString.getBytes();
		byte[] tokenBytes = Base64.encodeBase64(byteArraytoken);
		token = new String(tokenBytes);		
		return token;
	}
	public String callRestAPI(String uri, String host, String authztoken,
			String method, MultivaluedMap myformdata, String contentType,
			String bodydata) {

		Client c = Client.create();
		WebResource resource = c.resource(host + uri);
		if (method == "GET") {
			String response = resource.header("Authorization", authztoken)
					.accept("application/json").get(String.class);			
			return response;
		}
		if (method == "PATCH") {
			ClientResponse response = resource.type(contentType)
					.header("Authorization", authztoken)
					.method("PATCH", ClientResponse.class, bodydata);
			return response.getEntity(String.class);
		}

		if (method == "PUT") {
			ClientResponse response = resource.type(contentType)
					.header("Authorization", authztoken)
					.method("PUT", ClientResponse.class, bodydata);
			return response.getEntity(String.class);
		}

		if (method == "POST") {
			if (bodydata == null) {
				ClientResponse response = resource.type(contentType)
						.header("Authorization", authztoken)
						.post(ClientResponse.class, myformdata);
				return response.getEntity(String.class);
			} else {
				ClientResponse response = resource.type(contentType)
						.header("Authorization", authztoken)
						.post(ClientResponse.class, bodydata);
				return response.getEntity(String.class);
			}
		}
		return "";
	}
	public String getIDCSUser(String accessToken, String userid) {		
		String myAccessToken = "Bearer " + accessToken;
		String uri = idcsCreateUserUri + "?filter=userName+eq+%22"+userid+"%22";		
		String contentType = "application/scim+json";
		MultivaluedMap formData = new MultivaluedMapImpl();
		String token = callRestAPI(uri, idcsHost, myAccessToken, "GET",
				formData, contentType, null);
		JSONObject obj = new JSONObject(token);			
		return obj.toString();		
	}

}*/
