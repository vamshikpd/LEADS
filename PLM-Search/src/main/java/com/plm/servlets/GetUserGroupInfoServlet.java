package com.plm.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.plm.oam.apps.LDAPAppendGroupInUserProfile;


public class GetUserGroupInfoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(GetUserGroupInfoServlet.class);
	
	 /**
     * @see HttpServlet#HttpServlet()
     */
    public GetUserGroupInfoServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uid = request.getParameter("sAMAccountName");
		//System.out.println("Get UserGroup : " + uid);
		logger.debug("User ID : " + uid);
		ServletOutputStream out = response.getOutputStream();
		response.setContentType("text/plain");
		String groupInfo = LDAPAppendGroupInUserProfile.getUserGroupInfo(uid);
		//System.out.println("Group : " + groupInfo);
		logger.debug("Group Info : " + groupInfo);
		out.print(groupInfo);
		out.flush();
	}
}
