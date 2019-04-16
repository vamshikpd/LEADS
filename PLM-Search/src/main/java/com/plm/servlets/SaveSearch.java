package com.plm.servlets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.plm.util.PLMUtil;
import com.plm.util.database.PLMDatabaseUtil;


public class SaveSearch extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	private static final Logger logger = Logger.getLogger(SaveSearch.class);
	
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		getServletContext();
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {
		
		String sSearchName = request.getParameter("searchName");
		boolean bSearchExist = false;
		try {
			bSearchExist = PLMDatabaseUtil.getSavedSearch(sSearchName);
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			bSearchExist = true;
		}
		if(bSearchExist){
			response.setContentType("text/html");
			response.getWriter().write("true");
		}else{
			response.getWriter().write("false");			
		}
	}	
}
