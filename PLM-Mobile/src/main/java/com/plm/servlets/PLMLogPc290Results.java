package com.plm.servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.plm.util.PLMLogPC290Thread;
import com.plm.util.PLMSearchUtil;


public class PLMLogPc290Results extends HttpServlet {
	private static final Logger logger = Logger.getLogger(PLMLogPc290Results.class);
	private static final long serialVersionUID = 1L;

	public PLMLogPc290Results() {
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String county = request.getParameter("county");
		String city = request.getParameter("city");
		String userName = request.getParameter("userName");
		String ipAddress = request.getParameter("ipAddress");
		String qry_type = request.getParameter("qry_type");
		logger.info("executing PC290AuditLog...");
		doAudit(county, city, userName, ipAddress, qry_type);
		logger.info("PC290AuditLog process complete...");
		return;
	}
	
	public static void doAudit(String county, String city, String user, String ip, String qry_type){
		boolean bTestConnection = PLMSearchUtil.checkConnection();
		if (bTestConnection) {
			PLMLogPC290Thread p = new PLMLogPC290Thread(county, city, user,
					ip, qry_type);
			new Thread(p).start();
		} else {
			try {
				PLMSearchUtil.writePC290SearchResult(county, city, user,
						ip, qry_type);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
