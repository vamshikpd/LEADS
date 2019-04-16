package com.plm.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.endeca.navigation.ENEQueryException;
import com.endeca.ui.export.PDFExportServlet;
import com.plm.util.PLMSearchUtil;
import com.plm.util.PLMUtil;


/**
 * Servlet implementation class RetrieveSavedSearchKeyValuePairs
 */
public class RetrieveSavedSearchKeyValuePairs extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RetrieveSavedSearchKeyValuePairs.class);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public RetrieveSavedSearchKeyValuePairs() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String key = null;
		String name = null;
		String endecaQueryString = null;
		String urlQueryString = null;
		String trimmedEndecaQueryString = null;
		
		if(request.getAttribute("searchid")!=null)
			key = (String)request.getAttribute("searchid");
		else if(request.getParameter("searchid")!=null)
			key = request.getParameter("searchid");

		if(request.getAttribute("searchname")!=null)
			name = (String)request.getAttribute("searchname");
		else if(request.getParameter("searchname")!=null)
			name = request.getParameter("searchname");

		if(key==null)
			out.println("invalid");
		else{
			try {
				endecaQueryString = PLMSearchUtil.getSavedSearchByID(key);
			} catch (SQLException e) {
            	logger.error(PLMUtil.getStackTrace(e));
				throw new ServletException();
			}
			if(endecaQueryString!=null){
				int ibegin = endecaQueryString.indexOf("searchName");
				if(ibegin>=0){
					int iend = endecaQueryString.indexOf("&", ibegin);
					if(iend>=0){
						String removeSearchName = endecaQueryString.substring(ibegin, iend);
						if(removeSearchName!=null){
							trimmedEndecaQueryString = endecaQueryString.substring(0, ibegin) + endecaQueryString.substring(iend+1);
						}
					}else{
						String removeSearchName = endecaQueryString.substring(ibegin);
						if(removeSearchName!=null){
							trimmedEndecaQueryString = endecaQueryString.substring(0, ibegin);
						}
					}
				}else{
					trimmedEndecaQueryString = endecaQueryString;
				}
				if(trimmedEndecaQueryString!=null){
					try {
						urlQueryString = PLMSearchUtil.convertNameToNvalue(trimmedEndecaQueryString);
					} catch (ENEQueryException e) {
	                	logger.error(PLMUtil.getStackTrace(e));
					}
				}
			}
			if(urlQueryString!=null)
				out.print(urlQueryString+"&searchName="+name);
		}
	    
	    out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
