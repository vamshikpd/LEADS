package com.plm.util;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.plm.util.database.PLMDatabaseUtil;


public class PLMLogPC290Thread implements Runnable {
	
	private static final Logger logger = Logger.getLogger(PLMLogPC290Thread.class);
	String county = null;
	String city = null;
	String userName = null;
	String ipAddress = null;
	String qry_type = null;

	public PLMLogPC290Thread(String county, String city, String userName,
			String ipAddress, String qry_type) {
		this.county = county;
		this.city = city;
		this.userName = userName;
		this.ipAddress = ipAddress;
		this.qry_type = qry_type;
	}

	public void run() {
		logger.info("thread started...");
		CallableStatement cstmt2 = null;
		Connection conn = null;
		;
		try {
			conn = PLMDatabaseUtil.getConnection();
		} catch (SQLException e1) {
			logger.error("error in getting DB connection: " + e1.getMessage());
			writeAudit();
		}
		try {
			// county, City,username, ip address, qry_type(R/D)
			String sql = "{call CPOWNER.LOG_PC290(?,?,?,?,?)}";
			cstmt2 = conn.prepareCall(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			cstmt2.setString(1, county);
			cstmt2.setString(2, city);
			cstmt2.setString(3, userName);
			cstmt2.setString(4, ipAddress);
			cstmt2.setString(5, qry_type);
			cstmt2.executeQuery();
		} catch (SQLException e) {
			logger.error("error in executing audit query: " + e.toString());
			writeAudit();
		} finally {
			try {
				if (cstmt2 != null) {
					cstmt2.close();
					cstmt2 = null;
				}
			} catch (SQLException e) {
				logger.error("error in closing callable statement: "
						+ e.toString());
			} finally {
				try {
					if (conn != null) {
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
					logger.error("error in closing the db connection: "
							+ e.toString());
				}
			}
		}
		logger.info("thread complete...");

	}

	public void writeAudit() {
		try {
			PLMSearchUtil.writePC290SearchResult(county, city, userName,
					ipAddress, qry_type);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
