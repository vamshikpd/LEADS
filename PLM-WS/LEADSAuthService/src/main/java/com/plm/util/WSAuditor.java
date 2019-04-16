package com.plm.util;

import java.sql.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class WSAuditor {

    private static final Log logger = LogFactory.getLog(WSAuditor.class);

    public static boolean insertWSAuditEntry(Timestamp timestamp, String username, String ipAddress, String wsType, String status, String query) {
        Connection conn = null;

        String sql = "INSERT INTO LEADS_WS_AUDIT_LOG (ID, TIME_STAMP, USERNAME, IP_ADDRESS, WS_TYPE, STATUS, QUERY) VALUES (LEADS_WS_AUDIT_LOG_SEQ.nextval, ?, ?, ?, ?, ?, ?)";

        boolean auditStatus = true;
        PreparedStatement ps = null;

        try {
            conn = PLMDatabaseUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, timestamp);
            ps.setString(2, username);
            ps.setString(3, ipAddress);
            ps.setString(4, wsType);
            ps.setString(5, status);
            ps.setString(6, query);
            ps.executeUpdate();
            ps.close();


        } catch (SQLException sqle) {
            logger.error(PLMUtil.getStackTrace(sqle));
            auditStatus = false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }

                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sqle) {
                logger.error(PLMUtil.getStackTrace(sqle));
                auditStatus = false;
            }
        }
        return auditStatus;
    }
}