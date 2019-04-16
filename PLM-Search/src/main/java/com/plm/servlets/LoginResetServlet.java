package com.plm.servlets;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.endeca.ui.constants.UI_Props;

public class LoginResetServlet extends HttpServlet {

	private ServletContext servletContext;
	
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		servletContext = getServletContext();
	}	

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String userNameAttr = null;
		HttpSession httpSession = request.getSession();
		userNameAttr = (String)httpSession.getAttribute("ssoUserName");
		if(userNameAttr==null){
			userNameAttr = (String)request.getAttribute("userName");
		}
		
		String weblogicIP	= UI_Props.getInstance().getValue("WEBLOGIC_IP");
		String weblogicPort	= UI_Props.getInstance().getValue("WEBLOGIC_PORT");
		
		//Now redirect the user back to the Login page.
		String redirectURL = "http://" + weblogicIP +":" + weblogicPort +"/plm-search/login.jsp?N=0";
		httpSession.invalidate();
		response.sendRedirect(redirectURL);
	}
}

