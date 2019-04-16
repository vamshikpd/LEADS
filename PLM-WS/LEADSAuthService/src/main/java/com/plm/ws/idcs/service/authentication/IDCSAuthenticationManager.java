package com.plm.ws.idcs.service.authentication;
/*
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import javax.xml.namespace.QName;

public class IDCSAuthenticationManager implements AuthenticationManager {
	
	private static final Logger logger = Logger.getLogger(IDCSAuthenticationManager.class);

	static final List<GrantedAuthority> AUTHORITIES = new ArrayList<GrantedAuthority>();
	static {
		AUTHORITIES.add(new GrantedAuthorityImpl("ROLE_WEBSERVICE"));
	}
	
	private static UsernamePasswordAuthenticationToken userToken;
	private String idcsFilterPath;		
	private String user;
	private String password;
	String idcsHost                 = "";
    String idcsClientSecret         = "";
    String idcsClientID             = "";
    String idcsGetAccessTokenUri    = "";
    String idcsAuthenticateUseruri   = "";
	
	public void setIdcsFilterPath(String idcsFilterPath) {		
		this.idcsFilterPath = idcsFilterPath;
	}
	public void setUser(String user) {		
		this.user = user;
	}
	public void setPassword(String password) {		
		this.password = password;
	}
	
	public static UsernamePasswordAuthenticationToken getUserToken(){
		return userToken;
	}

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		// TODO Auto-generated method stub			
		String accessToken = "";
		String username = "";
		String password = "";
		String groupname = "";
		userToken = new UsernamePasswordAuthenticationToken(authentication.getName(), authentication.getCredentials(), AUTHORITIES);
		try {
			fetchProperties();
			accessToken = getIDCSaccessToken();
			username = authentication.getName();
			password = (String) authentication.getCredentials();
			groupname = idcsFilterPath;
			boolean auth = false;
			boolean searchScope = false;
			
			String authenticateResult = userAuthenticate(accessToken,username, password);
			
			if(authenticateResult.equalsIgnoreCase("400")){
				auth = false;
			}else{	
				auth = true;	
				searchScope = roleSearchScope(authenticateResult,groupname); 				
			}
			if(!auth){
				
				QName authenticationResponse_QNAME = new QName("http://www.plm/ws/mtom/data", "ParoleeDataResponse");
				throw new com.sun.xml.wss.impl.WssSoapFaultException(authenticationResponse_QNAME, "Username or password is incorrect", "", null);
			}else{
				if(!searchScope){					
					throw new InsufficientAuthenticationException("Insufficient access permissions");
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userToken;
	}
	
	protected void fetchProperties() throws IOException{        
		Properties prop = new Properties();
		String propFileName = "app.properties";
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		if(inputStream != null){
			prop.load(inputStream);
		}else{
			throw new FileNotFoundException("property file'"+ propFileName + "' not found in classpath");
			}
        idcsHost                = prop.getProperty("idcshost");
        idcsClientSecret        = prop.getProperty("idcsclientsecret");
        idcsClientID            = prop.getProperty("idcsclientid");
        idcsGetAccessTokenUri   = prop.getProperty("getaccesstokenuri");
        idcsAuthenticateUseruri  = prop.getProperty("authenticateuseruri"); 
    }
	public String getIDCSaccessToken() {
		String basicAuthzToken = "Basic " + createBasicAuthZToken();		
		String contentType = "application/x-www-form-urlencoded";
		MultivaluedMap formData = new MultivaluedMapImpl();
		formData.add("grant_type", "client_credentials");
		formData.add("scope", "urn:opc:idm:t.user.authenticate");  
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
	public String userAuthenticate(String accessToken,String username, String password) {
        String myAccessToken = "Bearer "+accessToken;
        String uri = idcsAuthenticateUseruri;
        String contentType = "application/scim+json";
        MultivaluedMap formData = new MultivaluedMapImpl();
        
        
        String UserJSONData = "{ \n" +
        " \"mappingAttribute\": \"userName\",\n" +
        " \"mappingAttributeValue\": \""+username+"\",\n" +
        " \"password\": \""+password+"\",\n" +
        " \"includeMemberships\": true,\n" +
        " \"schemas\": [\n" +
        "   \"urn:ietf:params:scim:schemas:oracle:idcs:PasswordAuthenticator\"\n" +
        " ]\n" +
        "}";
        
     
        String result = callRestAPI(uri,idcsHost,myAccessToken,"POST",formData,contentType, UserJSONData); 
        JSONObject obj = new JSONObject(result);
        
        if(obj.isNull("status")) { 
            return result;
        }
        else {
         return obj.getString("status");
        
        }
        
	}
	public boolean roleSearchScope(String result, String groupname){
		JSONObject obj = new JSONObject(result);      
		   
        int numberOfGroups = obj.getJSONArray("groups").length();
        
        for (int i=0;i<numberOfGroups; i++) {
        if(groupname.equals(obj.getJSONArray("groups").getJSONObject(i).get("display").toString())) {              
        	return true;
            }            
        }
        return false;
	}

	
}*/
	
