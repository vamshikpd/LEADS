package com.plm.util;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;


public class PLMDatabaseUtil {
	private static final Logger logger = Logger.getLogger(PLMDatabaseUtil.class);
    private static DataSource ds = null;
    
    static{
    	init();
    }
    
	public static void init() {
		try {
			/*Hashtable<String,String> ht = new Hashtable<String,String>();
			ht.put(Context.INITIAL_CONTEXT_FACTORY, PLMConstants.INITIAL_CONTEXT_FACTORY_VALUE);
		    InetAddress addr = InetAddress.getLocalHost();
		    String hostname = addr.getHostName();
		    String pvdrUrl = PLMUtil.replaceAllPlaceHolder(PLMConstants.PROVIDER_URL_VALUE, "{HOSTNAME}", hostname);
			ht.put(Context.PROVIDER_URL, pvdrUrl);
			// Get a context for the JNDI lookup
			ctx = new InitialContext(ht);
			// Look up the data source
			ds = (DataSource) ctx.lookup("plmwebsvcmultidatasource"); */

			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/InterfacesDS");

		}catch (Exception e) {
			logger.error(PLMUtil.getStackTrace(e));
		} finally {
			
		}

	}
	
	public static Connection getConnection() throws SQLException{
	    Connection dbconn;
	    if(ds==null)
	    	throw new SQLException("DATASOURCE Could not be initialized.");
	    dbconn = ds.getConnection();
		return dbconn;
	}
}
