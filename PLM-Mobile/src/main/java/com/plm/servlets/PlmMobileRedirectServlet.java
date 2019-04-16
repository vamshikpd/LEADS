package com.plm.servlets;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import com.plm.util.AccessControlManager;
import com.plm.util.PermissionInfo;

public class PlmMobileRedirectServlet extends HttpServlet {

	private static final long serialVersionUID = 7483019740473168673L;
	private ServletContext servletContext;
	private static final Logger logger = Logger.getLogger(PlmMobileRedirectServlet.class);

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
//		RetrieveIDCSUserInfo userInfo = new RetrieveIDCSUserInfo();
		HttpSession httpSession = request.getSession();

		String groupName = request.getHeader("GROUPS");
		String userId = request.getHeader("USERID");
		String firstName = request.getHeader("NAME.FIRST") != null ? request.getHeader("NAME.FIRST") : userId;
		String lastName = request.getHeader("NAME.LAST") != null ? request.getHeader("NAME.LAST") : "";
		String email = request.getHeader("EMAIL") != null ? request.getHeader("EMAIL") : "";


		/*try{
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
		}catch(Exception e){
			e.printStackTrace();
		}*/
		
		
		PermissionInfo permissionInfo = null;
		try {
			if(groupName == null){
//				groupName = "LEADS-Query"; //Default Group
				groupName = "App-CDCR-LEADS-Query"; //Default Group
			}
			permissionInfo = acm.getPermission(groupName);
			httpSession.setAttribute("permissionInfo", permissionInfo);
		}catch(Exception e) {
			e.printStackTrace();
		}



		request.setAttribute("userName", userId);
		request.setAttribute("groupName", groupName);
		request.setAttribute("firstName", firstName);
		request.setAttribute("lastName", lastName);
		request.setAttribute("email", email);
//		request.setAttribute("userId", userId);
		
		httpSession.setAttribute("userId",userId);
		httpSession.setAttribute("groupName",groupName);
		httpSession.setAttribute("firstName",firstName);
		httpSession.setAttribute("lastName",lastName);
		httpSession.setAttribute("email",email);

		// 2018-03-30 vamshi added mobile invalid role page
		RequestDispatcher dispatcher = null;
		if(groupName.contains("App-CDCR-LEADS-Mobile")){
			dispatcher =  request.getRequestDispatcher("/goToPLMMobileMainPage");
		}else{
			dispatcher = request.getRequestDispatcher("/invalid_role.htm");
		}


		dispatcher.forward(request, response);
	}
	
}
