package com.plm.util;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class PLMDatabaseUtil {

    private static final Logger logger = Logger.getLogger(PLMDatabaseUtil.class);
    private static DataSource ds = null;

    static {
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
			//showJndiContext( ctx, "", "");
			// Look up the data source
			ds = (DataSource) ctx.lookup("plmwebsvcmultidatasource"); */

            Context ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/InterfacesDS");

        } catch (Exception e) {
            logger.error(PLMUtil.getStackTrace(e));
        }
    }

    /*public static void showJndiContext(Context ctx, String name, String space) {
        if (null == name)
            name = "";
        if (null == space)
            space = "";
        try {
            NamingEnumeration<NameClassPair> en = ctx.list(name);
            while (en.hasMoreElements()) {
                String delim = (null != name && 0 < name.length()) ? "/" : "";
                NameClassPair nc = (NameClassPair) en.next();
                if (40 > space.length())
                    showJndiContext(ctx, nc.getName(), "    " + space);
            }
        } catch (NamingException e) {
            logger.error(PLMUtil.getStackTrace(e));
        }
    }*/

    public static Connection getConnection() throws SQLException {
        Connection dbconn = null;
        if (ds == null)
            throw new SQLException("DATASOURCE Could not be initialized.");
        dbconn = ds.getConnection();
        return dbconn;
    }
}
