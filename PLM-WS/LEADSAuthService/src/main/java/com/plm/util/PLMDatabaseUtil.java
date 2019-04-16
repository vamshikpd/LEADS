package com.plm.util;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class PLMDatabaseUtil {
    private static final Log logger = LogFactory.getLog(PLMDatabaseUtil.class);
    private static DataSource ds = null;

    static {
        init();
    }

    public static void init() {
        try {
            Context ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/InterfacesDS");

        } catch (Exception e) {
            logger.error(PLMUtil.getStackTrace(e));
        }

    }

    static Connection getConnection() throws SQLException {
        Connection dbconn = null;
        if (ds == null) {
            throw new SQLException("DATASOURCE Could not be initialized.");
        }
        dbconn = ds.getConnection();
        return dbconn;
    }
}