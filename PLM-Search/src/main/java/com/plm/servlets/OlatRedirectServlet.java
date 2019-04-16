package com.plm.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
//import com.plm.idcs.RetrieveIDCSUserInfo;
import com.plm.util.AccessControlManager;
import com.plm.util.PermissionInfo;
import org.apache.log4j.Logger;
//import org.json.JSONObject;
//import org.json.JSONArray;


public class OlatRedirectServlet extends HttpServlet {


	private static final long serialVersionUID = 7483019740473168673L;
	
	private static final Logger logger = Logger
			.getLogger(OlatRedirectServlet.class);
	private ServletContext servletContext;
	
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		servletContext = servletConfig.getServletContext();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {		
		AccessControlManager acm = new AccessControlManager(servletContext);
		//RetrieveIDCSUserInfo userInfo = new RetrieveIDCSUserInfo();		
		HttpSession httpSession = request.getSession(true);
		String firstName;
		String lastName;
		String email;
		String groupName;
		String userId;
		String ipAddress;
		String displayName;

		//Added by Ashish to get SSO Params from headers
		userId = request.getHeader("USERID");
		if(userId == null){
			userId = request.getParameter("user_id");
		}
		groupName = request.getHeader("GROUPS");
		if(groupName == null) {
			groupName = request.getParameter("group");
		}
		firstName = request.getHeader("NAME.FIRST") != null ? request.getHeader("NAME.FIRST") : userId;
		lastName = request.getHeader("NAME.LAST") != null ? request.getHeader("NAME.LAST") : "";
		email = request.getHeader("EMAIL") != null ? request.getHeader("EMAIL") : "";
		ipAddress = request.getHeader("X-FORWARDED-FOR") != null ? request.getHeader("X-FORWARDED-FOR") : "";
		if (ipAddress == null) {
			ipAddress = request.getHeader("IPADDRESS") != null ? request.getHeader("IPADDRESS") : "";
		}

		// fix per defect #91 to display AD displayName attribute in application header
		displayName = request.getHeader("DISPLAY.NAME") != null ? request.getHeader("DISPLAY.NAME") : firstName + " " + lastName ;

		/*try {
			if(userId == null){
				groupName = userInfo.getUserGroups();			
				userId = userInfo.getUserId();
				String result = userInfo.getUserInfo(userId);
				JSONObject obj = new JSONObject(result);				
				JSONArray jsonArray =  obj.getJSONArray("Resources");
				JSONArray jsonArray1 = new JSONArray();			
				
				// Get the firstname & lastname from JSON Object
				List<String> nameList = new ArrayList<String>();
				for(int i = 0 ; i < jsonArray.length() ; i++){
					nameList.add(jsonArray.getJSONObject(i).optString("name"));
				}
				for(int i=0;i<nameList.size();i++){				
					JSONObject obj1 = new JSONObject(nameList.get(i));
					lastName =  obj1.getString("familyName");
					firstName =  obj1.getString("givenName");				
				} 
				
				// Get the Email from JSON Object
				List<String> emailList = new ArrayList<String>();			
				for(int i = 0 ; i < jsonArray.length() ; i++){
					 jsonArray1 = jsonArray.getJSONObject(i).getJSONArray("emails");
				}
				Iterator<Object> iterator = jsonArray1.iterator();
		        while (iterator.hasNext()) {	                
		        	emailList.add(iterator.next().toString());	                
		        }	            
		        for(int k=0;k<emailList.size();k++){
		          JSONObject obj2 = new JSONObject(emailList.get(k));
		          email = obj2.getString("value");
		        }
			}   	
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		// From SOMS to LEADS 2.0
		String fromSoms = (String) request.getAttribute("fromSOMS");

		if ("Y".equals(fromSoms)) {

			userId = (String) request.getAttribute("USERID");
			groupName = "App-CDCR-LEADS-MapUsers|App-CDCR-LEADS-Query";
			firstName = (String) request.getAttribute("FIRSTNM");
			lastName = (String) request.getAttribute("LASTNM");
			email = (String) request.getAttribute("MAIL");
			ipAddress = (String) request.getAttribute("IPADDRESS");
			displayName = (String) request.getAttribute("DISPLAYNM");
//			logger.debug("SOMS User Authenticated... " + " user id: " + userId);

            if (displayName == null) {
                displayName = firstName + " " + lastName;
            }

			if (userId == null) {
				logger.warn("User ID is null!");
			}
		}

		PermissionInfo permissionInfo = null;
		try {
			if (groupName == null) {
				groupName = "App-CDCR-LEADS-Query"; // Default Group
				logger.debug("Setting default group to user id: " + userId);
			}
			permissionInfo = acm.getPermission(groupName);
			httpSession.setAttribute("permissionInfo", permissionInfo);
//			logger.debug(String.format("Permissions for user %s --> %s", userId, permissionInfo.toString()));

		} catch (Exception e) {
			logger.debug("Error while setting permissions for user id: " + userId);
			e.printStackTrace();
		}
		request.setAttribute("userName", userId);
		request.setAttribute("firstName", firstName);
		request.setAttribute("lastName", lastName);
		request.setAttribute("email", email);
		request.setAttribute("groupName", groupName);
		request.setAttribute("displayName", displayName);

		String userType = permissionInfo.getUserType();
		request.setAttribute("userType", userType);
		request.setAttribute("fromLoginPage", "Y");
		request.setAttribute("ipAddress", ipAddress);

		// Set the properties in session instead of request
		httpSession.setAttribute("userId", userId);
		httpSession.setAttribute("firstName", firstName);
		httpSession.setAttribute("lastName", lastName);
		httpSession.setAttribute("email", email);
		httpSession.setAttribute("groupName", groupName);
		httpSession.setAttribute("userType", userType);
		httpSession.setAttribute("ipAddress", ipAddress);
		httpSession.setAttribute("displayName", displayName);

		if (httpSession.getAttribute("isFirstLogin") == null) {
			httpSession.setAttribute("fromLoginPage", "Y");
		}
		httpSession.setAttribute("isFirstLogin", "Y");

		RequestDispatcher dispatcher = null;
		if (userId == null) {
			dispatcher = request.getRequestDispatcher("/invalid.htm");
		} else if ("Y".equals(fromSoms)) {
			request.setAttribute("fromSOMS", "Y");
			httpSession.setAttribute("fromSOMS", "Y");
			dispatcher = request.getRequestDispatcher("goToSomsMainPage");

		} else {
			dispatcher = request.getRequestDispatcher("goToPLMMainPage");

		}

		dispatcher.forward(request, response);
	}
	
	
}
