/* PLMDatabaseUtil
 * This contains methods for executing various SQL queries.
 * 
 * 09-28-2011 - Added getAddressCC. This function queries the Agent table to obtain the Supervisor and 
 * Assistant Supervisor email addresses for the agent's unit. It creates a string passed back to the calling 
 * procedure.  -- L Baird
*/

package com.plm.util.database;
import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import coldfusion.monitor.alert.Alert;

import com.endeca.ui.constants.UI_Props;
import com.plm.PLMConnection;
import com.plm.constants.PLMConstants;
import com.plm.util.PLMUtil;

public class PLMDatabaseUtil {
	private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	private static final Logger logger = Logger.getLogger(PLMDatabaseUtil.class);
	private static Context ctx = null;
	private static DataSource dsPLMSource = null;
	
	static{
		init();
	}
	
	public static void init() {
		try {
			logger.info("initializing plm datasource...");


			ctx = new InitialContext();
			dsPLMSource = (DataSource) ctx.lookup("java:/comp/env/jdbc/InterfacesDS");
			
			/*Hashtable<String,String> htContext = new Hashtable<String,String>();
			
			htContext.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
			
			InetAddress iaIPAddr = InetAddress.getLocalHost();
			String sHostname = iaIPAddr.getHostName();
			
			String sPvdrUrl = "t3://"+sHostname+":"+UI_Props.getInstance().getValue("App_wlsport");
			logger.info("weblogic connection ----"+sPvdrUrl);
			htContext.put(Context.PROVIDER_URL, sPvdrUrl);
			
			// Get a context for the JNDI lookup
			ctx = new InitialContext(htContext);
			
			//showJndiContext( ctx, "", "");
		
			// Data source = plmmultidatasource. See Weblogic for the URL.
			dsPLMSource = (DataSource) ctx.lookup("plmmultidatasource");*/

			logger.info("plm datasource initialization complete...");

		}catch (Exception e) {
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
		}
		catch (javax.naming.NamingException ex) {
		}
	}*/
	

	public static Connection getConnection() throws SQLException{
		Connection conDBConn = null;
		
		conDBConn = dsPLMSource.getConnection();
		conDBConn.setAutoCommit(false);
		
		return conDBConn;
	}
	
	
	public static void insert(String query, ArrayList<String> values) throws SQLException {
		PreparedStatement pstmt = null;
				
		Connection conn = null;
		
		try {
			conn = PLMDatabaseUtil.getConnection();
			if(conn.isClosed()){
				init();
				conn = PLMDatabaseUtil.getConnection();
			}
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("connection failed");
		}
		
		try {
			pstmt = conn.prepareStatement(query);
			for (int i = 0; i < values.size(); i++) {
				pstmt.setString(i + 1, values.get(i));
			}
			pstmt.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("insert failed");
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
			} catch (SQLException e) {
            	logger.error(PLMUtil.getStackTrace(e));
				throw new SQLException("statement close failed");
			} finally {
				try {
					if (conn != null) {
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
                	logger.error(PLMUtil.getStackTrace(e));
					throw new SQLException("connection close failed");
				}
			}
		}
	}

	
	public static void deleteSaveSearch(String delete_save_search) throws SQLException {
		PreparedStatement pstmt = null;
		Connection conn = null;
		String query = "delete from USER_SEARCH where SEARCH_NAME=?";
		try {
			conn = PLMDatabaseUtil.getConnection();
			if(conn.isClosed()){
				init();
				conn = PLMDatabaseUtil.getConnection();
			}
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
        	throw new SQLException("connection failed");
		}
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, delete_save_search);
			pstmt.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("delete failed");
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
			} catch (SQLException e) {
            	logger.error(PLMUtil.getStackTrace(e));
				throw new SQLException("statement close failed");
			} finally {
				try {
					if (conn != null) {
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
                	logger.error(PLMUtil.getStackTrace(e));
					throw new SQLException("connection close failed");
				}
			}
		}
	}
	
	
	public static boolean getSavedSearch(String save_search_name) throws SQLException {
		String query = "select * from USER_SEARCH where SEARCH_NAME=?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean bSearchExist = false;
		try {
			conn = PLMDatabaseUtil.getConnection();
			if(conn.isClosed()){
				init();
				conn = PLMDatabaseUtil.getConnection();
			}
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("connection failed");
		}
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, save_search_name);
			rs = pstmt.executeQuery();
			if (rs.next())
				bSearchExist = true;
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("select failed");
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException e) {
            	logger.error(PLMUtil.getStackTrace(e));
				throw new SQLException("statement or resultset close failed");
			} finally {
				try {
					if (conn != null) {
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
                	logger.error(PLMUtil.getStackTrace(e));
					throw new SQLException("connection close failed");
				}
			}
		}
		return bSearchExist;
	}
	
	
	public static Map<String,String> getSavedSearches(String query, String user) throws SQLException {
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String,String> resultMap = new HashMap<String,String>();
		try {
			conn = PLMDatabaseUtil.getConnection();
			if(conn.isClosed()){
				init();
				conn = PLMDatabaseUtil.getConnection();
			}
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("connection failed");
		}
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, user);
			rs = pstmt.executeQuery();
			if(rs != null){
				while (rs.next()){
					resultMap.put(rs.getString("SEARCH_NAME"), ""+rs.getInt("SEARCH_ID"));
				}
			}
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("select failed");
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException e) {
            	logger.error(PLMUtil.getStackTrace(e));
				throw new SQLException("statement or resultset close failed");
			} finally {
				try {
					if (conn != null) {
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
                	logger.error(PLMUtil.getStackTrace(e));
					throw new SQLException("connection close failed");
				}
			}
		}
		return resultMap;
	}
	
	
	public static String getSavedSearchByID(String id) throws SQLException {
		String rtnQuery = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			Integer.parseInt(id);
		}catch(NumberFormatException e){
        	logger.error(PLMUtil.getStackTrace(e));
			throw new NumberFormatException("invalid id");
		}
		try {
			conn = PLMDatabaseUtil.getConnection();
			if(conn.isClosed()){
				init();
				conn = PLMDatabaseUtil.getConnection();
			}
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("connection failed");
		}
		try {
			pstmt = conn.prepareStatement(PLMConstants.SELECT_SAVE_SEARCH_BY_ID);
			pstmt.setInt(1, Integer.parseInt(id));
			rs = pstmt.executeQuery();
			if(rs != null){
				while (rs.next()){
					rtnQuery = rs.getString("QUERY");
					break;
				}
			}
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("select failed");
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException e) {
            	logger.error(PLMUtil.getStackTrace(e));
				throw new SQLException("statement or resultset close failed");
			} finally {
				try {
					if (conn != null) {
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
                	logger.error(PLMUtil.getStackTrace(e));
					throw new SQLException("connection close failed");
				}
			}
		}
		return rtnQuery;
	}
	
	
	public static byte[] getPhoto(String cdcNumber, String psize,
			String defaultPhoto) throws SQLException, IOException {
		byte[] imgData = null;		
		String query;
		Connection conn;
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
	//	logger.info("getPhotoByID()-------------size-------"+psize);
		if ("t".equals(psize)) {
			query = "SELECT image FROM cpowner.thumbnails WHERE (id,cdc_num) IN (SELECT id, cdc_num FROM (SELECT id, cdc_num, Row_Number() over (ORDER BY insert_date desc) AS rownumber from cpowner.image_info i where i.cdc_num=? AND i.type=1 and i.SUBTYPE IN ('Full Face Frontal (Primary Mugshot)','Full Face Frontal with Glasses','Full Face Frontal with Hat','Full Face Frontal with Scarf')) WHERE rownumber = 1)";
		} else if ("p".equals(psize)) {
			query = "SELECT image FROM cpowner.PHOTOS WHERE (id,cdc_num) IN (SELECT id, cdc_num FROM (SELECT id, cdc_num, Row_Number() over (ORDER BY insert_date desc) AS rownumber from cpowner.image_info i where i.cdc_num=? AND i.type=1 and i.SUBTYPE IN ('Full Face Frontal (Primary Mugshot)','Full Face Frontal with Glasses','Full Face Frontal with Hat','Full Face Frontal with Scarf')) WHERE rownumber = 1)";
		} else if ("n".equals(psize)) {
			query = "SELECT image FROM cpowner.NIST WHERE (id,cdc_num) IN (SELECT id, cdc_num FROM (SELECT id, cdc_num, Row_Number() over (ORDER BY insert_date desc) AS rownumber from cpowner.image_info i where i.cdc_num=? AND i.type=1 and i.SUBTYPE IN ('Full Face Frontal (Primary Mugshot)','Full Face Frontal with Glasses','Full Face Frontal with Hat','Full Face Frontal with Scarf')) WHERE rownumber = 1)";
		} else {
			query = "SELECT image FROM thumbnails WHERE (id,cdc_num) IN (SELECT id, cdc_num FROM (SELECT id, cdc_num, Row_Number() over (ORDER BY insert_date desc) AS rownumber from cpowner.image_info i where i.cdc_num=? AND i.type=1 and i.SUBTYPE IN ('Full Face Frontal (Primary Mugshot)','Full Face Frontal with Glasses','Full Face Frontal with Hat','Full Face Frontal with Scarf')) WHERE rownumber = 1)";
		}
		try {
			conn = PLMDatabaseUtil.getConnection();
			if(conn.isClosed()){
				init();
				conn = PLMDatabaseUtil.getConnection();
			}
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("connection failed");
		}
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, cdcNumber);
			rs = pstmt.executeQuery();
			Blob img = null;
			while (rs.next()) {
				img = rs.getBlob(1);
				imgData = img.getBytes(1, (int) img.length());
				
			}
			if (imgData == null || imgData.length <= 0) {
				imgData = getDefaultImage(defaultPhoto);
			}
		} catch(SQLException e){
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("select failed");
		} catch(IOException e){
        	logger.error(PLMUtil.getStackTrace(e));
			throw new IOException("default image failed");
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException e) {
            	logger.error(PLMUtil.getStackTrace(e));
				throw new SQLException("statement or resultset close failed");
			} finally {
				try {
					if (conn != null) {
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
                	logger.error(PLMUtil.getStackTrace(e));
					throw new SQLException("connection close failed");
				}
			}
		}
		return imgData;
	}
	
	
	public static String getPrimaryMugshotID(String cdcNumber) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
	//	logger.info("getPrimaryMugshotID()-------------------"+cdcNumber);
		String query = "SELECT id, cdc_num FROM (SELECT id, cdc_num, Row_Number() over (ORDER BY Decode(i.SUBTYPE, 'Full Face Frontal (Primary Mugshot)', 1,2) ASC, insert_date desc) AS rownumber from cpowner.image_info i where i.cdc_num=? AND i.type=1 and i.SUBTYPE IN ('Full Face Frontal (Primary Mugshot)','Full Face Frontal with Glasses','Full Face Frontal with Hat','Full Face Frontal with Scarf','Full Face Frontal (Non-NIST)') AND i.id IN (SELECT id FROM cpowner.thumbnails WHERE id=i.id)) WHERE rownumber = 1";
		String phid = null;
		Connection conn = null;
		try {
			conn = PLMDatabaseUtil.getConnection();
			if(conn.isClosed()){
				init();
				conn = PLMDatabaseUtil.getConnection();
			}
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("connection failed");
		}
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, cdcNumber);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				phid =rs.getString(1);
				break;
			}
		} catch(SQLException e){
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("select failed");
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException e) {
            	logger.error(PLMUtil.getStackTrace(e));
				throw new SQLException("statement or resultset close failed");
			} finally {
				try {
					if (conn != null) {
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
                	logger.error(PLMUtil.getStackTrace(e));
					throw new SQLException("connection close failed");
				}
			}
		}
		return phid;
	}
	
	
	public static String getSecondaryMugshotID(String cdcNumber) throws SQLException {
		String phid = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;	
		String query = "SELECT id, cdc_num FROM (SELECT id, cdc_num, Row_Number() over (ORDER BY insert_date desc) AS rownumber from cpowner.image_info i where i.cdc_num=? AND i.type=1 and i.SUBTYPE IN ('Right Profile','Right Profile with Glasses','Right Profile with Hat','Right Profile with Scarf','Left Profile','Left Profile with Glasses','Left Profile with Hat','Left Profile with Scarf')) WHERE rownumber = 1";
		try {
			conn = PLMDatabaseUtil.getConnection();
			if(conn.isClosed()){
				init();
				conn = PLMDatabaseUtil.getConnection();
			}
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("connection failed");
		}
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, cdcNumber);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				phid = rs.getString(1);
				break;
			}
		} catch(SQLException e){
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("select failed");
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException e) {
            	logger.error(PLMUtil.getStackTrace(e));
				throw new SQLException("statement or resultset close failed");
			} finally {
				try {
					if (conn != null) {
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
                	logger.error(PLMUtil.getStackTrace(e));
					throw new SQLException("connection close failed");
				}
			}
		}
		return phid;
	}
	
	
	public static byte[] getPhotoByID(String phid, String psize,
			String defaultPhoto) throws SQLException, IOException {
		Blob img = null;
		byte[] imgData = null;		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = null;		
		//logger.info("getPhotoByID()-------------"+phid+"size-------"+psize);
		if ("t".equals(psize)) {			
			query = PLMConstants.SELECT_THUMBNAIL_QUERY_BY_ID;
		} else if ("p".equals(psize)) {			
			query = PLMConstants.SELECT_PHOTOS_QUERY_BY_ID;
		} else if ("n".equals(psize)) {			
			query = PLMConstants.SELECT_NIST_QUERY_BY_ID;
		} else {			
			query = PLMConstants.SELECT_THUMBNAIL_QUERY_BY_ID;
		}
		if(phid == null || phid.equals("null"))
		{
			phid="0";
		}
		/*try{
			Integer.parseInt(phid);
		}catch(NumberFormatException e){
        	logger.error(PLMUtil.getStackTrace(e));
			throw new NumberFormatException("invalid id");
		}*/
		try {
			conn = PLMDatabaseUtil.getConnection();
			if(conn.isClosed()){
				init();
				conn = PLMDatabaseUtil.getConnection();
			}
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("connection failed");
		}
		try {
			pstmt = conn.prepareStatement(query);
			//pstmt.setDouble(1, Integer.parseInt(phid));
			pstmt.setString(1, phid);
			rs = pstmt.executeQuery();			
			while (rs.next()) {			
				img = rs.getBlob(1);
				imgData = img.getBytes(1, (int) img.length());	
				if(!psize.equalsIgnoreCase("t")) {
					InputStream in = new ByteArrayInputStream(imgData);
					BufferedImage bImage = ImageIO.read(in);
					int width = PLMConstants.PHOTO_RESIZE_WIDTH;
					int height = PLMConstants.PHOTO_RESIZE_HEIGHT;
					final BufferedImage newImage = resizeImage(bImage, width,
							height);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(newImage, "jpeg", baos);
					baos.flush();
					imgData = baos.toByteArray();
					baos.close();
				}
				break;
			}
			if (imgData == null || imgData.length <= 0) {			
				imgData = getDefaultImage(defaultPhoto);
			}
		} catch(SQLException e){
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("select failed");
		} catch(IOException e){
        	logger.error(PLMUtil.getStackTrace(e));
			throw new IOException("default image failed");
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException e) {
            	logger.error(PLMUtil.getStackTrace(e));
				throw new SQLException("statement or resultset close failed");
			} finally {
				try {
					if (conn != null) {
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
                	logger.error(PLMUtil.getStackTrace(e));
					throw new SQLException("connection close failed");
				}
			}
		}
		return imgData;
	}
	
	
	public static byte[] getDefaultImage(String defaultPhoto) throws IOException {		
		byte[] imgData = null;
		File file = new File(defaultPhoto);
		int size = (int) file.length();
		imgData = new byte[size];
		DataInputStream dis = new DataInputStream(new FileInputStream(file));
		int read = 0;
		int numRead = 0;
		while (read < imgData.length
				&& (numRead = dis.read(imgData, read, imgData.length - read)) >= 0) {
			read = read + numRead;
		}
		return imgData;
	}
	
	
	public static HashMap<String,String> getPhotoDetails(String cdcNumber) throws SQLException {
		String id = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = "SELECT id FROM (SELECT id, cdc_num, Row_Number() over (ORDER BY insert_date desc) AS rownumber from cpowner.image_info i where i.cdc_num=? AND i.type=1 and i.subtype='Full Face Frontal (Primary Mugshot)') WHERE rownumber = 1";
		try {
			conn = PLMDatabaseUtil.getConnection();
			if(conn.isClosed()){
				init();
				conn = PLMDatabaseUtil.getConnection();
			}
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("connection failed");
		}
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, cdcNumber);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				id = rs.getString(1);
				break;
			}
		} catch(SQLException e){
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("select failed");
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException e) {
            	logger.error(PLMUtil.getStackTrace(e));
				throw new SQLException("statement or resultset close failed");
			} finally {
				try {
					if (conn != null) {
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
                	logger.error(PLMUtil.getStackTrace(e));
					throw new SQLException("connection close failed");
				}
			}
		}
		HashMap<String,String> info = getPhotoDetailsByID(id + "");
		return info;
	}
	
	
	public static HashMap<String,String> getPhotoDetailsByID(String id) throws SQLException, NumberFormatException {
		String cdcnum = null;
		String type_text = null;
		String subtype = null;
		String descr = null;
		Date insert_date = null;
		String inserted_by = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		if(id == null || id.equals("null"))
		{
			id="0";
		}
		String query = "Select i.cdc_num, decode(i.type,1,'MUGSHOT',2,'SCARS, MARKS AND TATTOOS',3,'VEHICLE',4,'RESIDENCE',5,'WEAPON',6,'EVIDENCE',7,'OTHER') as type_text, i.subtype, i.descr, i.insert_date, i.inserted_by from cpowner.image_info i, cpowner.photos ph Where i.id = ? AND i.cdc_num= ph.cdc_num(+) AND i.id = ph.id(+)";
		/*try{
			Integer.parseInt(id);
		}catch(NumberFormatException e){
        	logger.error(PLMUtil.getStackTrace(e));
			throw new NumberFormatException("invalid id");
		}*/
		try {
			conn = PLMDatabaseUtil.getConnection();
			if(conn.isClosed()){
				init();
				conn = PLMDatabaseUtil.getConnection();
			}
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("connection failed");
		}
		try{
			pstmt = conn.prepareStatement(query);			
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
		   while (rs.next ())
		   {
			 cdcnum = rs.getString(1);
			 type_text = rs.getString(2);
			 subtype = rs.getString(3);
			 descr = rs.getString(4);
			 insert_date = rs.getDate(5);
			 inserted_by = rs.getString(6);
		   }
		} catch(SQLException e){
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("select failed");
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException e) {
            	logger.error(PLMUtil.getStackTrace(e));
				throw new SQLException("statement or resultset close failed");
			} finally {
				try {
					if (conn != null) {
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
                	logger.error(PLMUtil.getStackTrace(e));
					throw new SQLException("connection close failed");
				}
			}
		}
		HashMap<String, String> info = new HashMap<String, String>();
		if(cdcnum!=null && insert_date!=null){
			info.put("cdcnum", cdcnum);
			info.put("showid", id);
			info.put("type_text", type_text);
			info.put("subtype", subtype);
			info.put("descr", descr);
			info.put("insert_date", dateFormat.format(insert_date).toString());
			info.put("inserted_by", inserted_by);
		}
		return info;
	}
	
	
	public static ArrayList<String> getPhotoIDs(String cdcNumber, int type)
			throws SQLException {
		String id = null;
		ArrayList<String> ids = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = null;	
		//logger.info("getPhotoIDs()-----------------------"+"cdcNumber---"+cdcNumber+"type-------"+type);
		if (type == -1) {
			query = "SELECT id FROM (SELECT id, cdc_num, Row_Number() over (ORDER BY insert_date desc) AS rownumber from cpowner.image_info i where i.cdc_num=? AND i.type IN(3,4,5,6,7)) order by rownumber";
		} else {
			query = "SELECT id FROM (SELECT id, cdc_num, Row_Number() over (ORDER BY insert_date desc) AS rownumber from cpowner.image_info i where i.cdc_num=? AND i.type=?) order by rownumber";
		}
		try {
			conn = PLMDatabaseUtil.getConnection();
			if(conn.isClosed()){
				init();
				conn = PLMDatabaseUtil.getConnection();
			}
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("connection failed");
		}
		try {
			pstmt = conn.prepareStatement(query);
			if (type == -1) {
				pstmt.setString(1, cdcNumber);
			} else {
				pstmt.setString(1, cdcNumber);
				pstmt.setInt(2, type);
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
				id = rs.getString(1);				
				ids.add(id);
			}
		}  catch(SQLException e){
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("select failed");
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException e) {
            	logger.error(PLMUtil.getStackTrace(e));
				throw new SQLException("statement or resultset close failed");
			} finally {
				try {
					if (conn != null) {
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
                	logger.error(PLMUtil.getStackTrace(e));
					throw new SQLException("connection close failed");
				}
			}
		}
		return ids;
	}
	
	
/* This function queries the database for the Unit Supervisor (US) and Assistant US (AUS) based on the Unit 
 *  Code passed as a parameter. L. Baird
 */	
	public static String getAddressCC(String inUnitCode) throws SQLException{
		Connection conDB = null;
		Statement stmtQuery = null;
		ResultSet rsResults = null;
		// ACTIVE_FLAG value changed 'Y' to 'A' on 4/4/2017 by vamshi
		String sQuery = "select EMAIL_ADDRESS from CPOWNER.AGENT where (TITLE = 'PAII' or TITLE = 'PAIII')  and ACTIVE_FLAG = 'A' and UNIT_CODE =  '" + inUnitCode + "'";
		String sAddCC = "";
		
		try {
			conDB = PLMDatabaseUtil.getConnection();
			if (conDB.isClosed()) {
				init();
				conDB = PLMDatabaseUtil.getConnection();
			}
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("connection failed");
		}
		
		try {
			stmtQuery = conDB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rsResults   = stmtQuery.executeQuery(sQuery);

			while (rsResults.next()) {
				sAddCC = rsResults.getString(1) + "," + sAddCC;
			}
		}catch(SQLException e){
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("select failed");
		}finally {
			try {
				if (stmtQuery != null) {
					stmtQuery.close();
					stmtQuery = null;
				}
				if (rsResults != null) {
					rsResults.close();
					rsResults = null;
				}
			} catch (SQLException e) {
            	logger.error(PLMUtil.getStackTrace(e));
				throw new SQLException("statement or resultset close failed");
			}finally {
				try {
					if (conDB != null) {
						conDB.close();
						conDB = null;
					}
				} catch (SQLException e) {
                	logger.error(PLMUtil.getStackTrace(e));
					throw new SQLException("connection close failed");
				}
			}
		}

		if (sAddCC != "") {
		 	sAddCC = sAddCC.substring(0, sAddCC.length() - 1);		
		}
		return sAddCC;
	}
	
	
	public static int getPhotoCount(String cdcNumber) throws SQLException{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = "SELECT COUNT(*) FROM IMAGE_INFO WHERE CDC_NUM = ?";
		int cnt = 0;
		try {
			conn = PLMDatabaseUtil.getConnection();
			if(conn.isClosed()){
				init();
				conn = PLMDatabaseUtil.getConnection();
			}
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("connection failed");
		}
		try{
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, cdcNumber);
			rs = pstmt.executeQuery();
			while(rs.next()){
				cnt = rs.getInt(1);
				break;
			}
		} catch(SQLException e){
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("select failed");
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException e) {
            	logger.error(PLMUtil.getStackTrace(e));
				throw new SQLException("statement or resultset close failed");
			} finally {
				try {
					if (conn != null) {
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
                	logger.error(PLMUtil.getStackTrace(e));
					throw new SQLException("connection close failed");
				}
			}
		}
		return cnt;
	}
	
	
	public static boolean isSearchAccessAllowed(String userId) throws SQLException  {
		DataSource lmsDatasource = PLMConnection.getInstance().getLMSDataSource();
		Connection conn = DataSourceUtils.getConnection(lmsDatasource);
		String commonQry = " SELECT OQRES1.OLATRESOURCE_FK, OQRES1.OLATRESOURCEDETAIL, OQRES1.IDENTITY_ID,  OQRES1.CREATIONDATE " +
		" FROM O_QTIRESULTSET OQRES1 JOIN O_PROPERTY PROP ON PROP.RESOURCETYPEID = OQRES1.OLATRESOURCE_FK " +
		" AND PROP.NAME = OQRES1.OLATRESOURCEDETAIL WHERE PROP.STRINGVALUE = '1' AND PROP.CATEGORY = 'isTestMandatory' "+
		" AND OQRES1.RECERTIFICATIONDATE = (SELECT MAX(RECERTIFICATIONDATE) FROM O_QTIRESULTSET OQ "+
		" WHERE OQ.OLATRESOURCE_FK = OQRES1.OLATRESOURCE_FK AND OQ.IDENTITY_ID=OQRES1.IDENTITY_ID) "+
		" AND OQRES1.IDENTITY_ID = (SELECT ID FROM O_BS_IDENTITY WHERE NAME = ?)";
		String query  = commonQry + " AND OQRES1.RECERTIFICATIONDATE < SYSDATE";
		try {
			if(conn.isClosed()){
				init();
				conn = PLMDatabaseUtil.getConnection();
			}
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("connection failed");
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean isAllowed = true;
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();
			while(rs.next()){
					isAllowed = false;  /* highest recertification/mandatory test date for any of his course is less than system date. Access should be restricted */
					break;
			}
			if(isAllowed) //no records found in previous qry
			{
				/*in case if there exist any higher date record, check if it is a mandatory test record .In this case we will have to consider (if exists)
				 2nd highest date record(which will be recertification record) which is created after mandatory test date record. */
				query = commonQry +
							" AND OQRES1.RECERTIFICATIONDATE >= SYSDATE" +
							" AND OQRES1.RESULTSET_ID not in(select oqres.RESULTSET_FK from o_qtiresult oqres )";
				pstmt = null;
				rs = null;
				pstmt = conn.prepareStatement(query);
				pstmt.setString(1, userId);
				rs = pstmt.executeQuery();
				while(rs.next()){
					BigDecimal olatResource = rs.getBigDecimal(1);
					String olatResDetail 	= rs.getString(2);
					BigDecimal identity 	= rs.getBigDecimal(3);
					Timestamp creationDate 	= rs.getTimestamp(4);
					query = "SELECT RECERTIFICATIONDATE FROM O_QTIRESULTSET OQRES1 INNER JOIN O_QTIRESULT OQRES " +
							" ON OQRES1.RESULTSET_ID = OQRES.RESULTSET_FK " +
							" WHERE OQRES1.IDENTITY_ID=? AND OQRES1.OLATRESOURCE_FK = ? " +
							" AND OQRES1.OLATRESOURCEDETAIL=? AND OQRES1.CREATIONDATE > ? "  +
							" AND ROWNUM = 1 ORDER BY  OQRES1.RECERTIFICATIONDATE DESC ";
					ResultSet rs1 = null;
					pstmt = null;
					pstmt = conn.prepareStatement(query);
					pstmt.setBigDecimal(1, identity);
					pstmt.setBigDecimal(2, olatResource);
					pstmt.setString(3, olatResDetail);
					pstmt.setTimestamp(4, creationDate);
					rs1 = pstmt.executeQuery();
					while(rs1.next()){
						Date recerDate = rs1.getDate(1);
						if(recerDate != null && recerDate.before(new Date())){
							isAllowed = false;
							break;
						}
					}
				}
			}
		} catch (SQLException e) {
        	logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("isSearchAccessAllowed failed");
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (rs != null) {
					rs.close();
				}
				// properly release our connection
				DataSourceUtils.releaseConnection(conn, lmsDatasource);
			} catch (SQLException e) {
            	logger.error(PLMUtil.getStackTrace(e));
				throw new SQLException("statement or resultset close or connection release failed");
			}
		}
		return !isAllowed;
	}
	
	public static BufferedImage resizeImage(final Image image, int width, int height) {
		final BufferedImage bufferedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		final Graphics2D graphics2D = bufferedImage.createGraphics();
		graphics2D.setComposite(AlphaComposite.Src);
		graphics2D.drawImage(image, 0, 0, width, height, null);
		graphics2D.dispose();
		return bufferedImage;
	}

	public static String getConfigParamByName(String paramName) throws SQLException {
		String paramValue  = "";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn;
		try {
			conn = PLMDatabaseUtil.getConnection();
			if(conn.isClosed()){
				init();
				conn = PLMDatabaseUtil.getConnection();
			}
		} catch (SQLException e) {
			logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("connection failed");
		}
		try {
			pstmt = conn.prepareStatement(PLMConstants.SELECT_PARAM_VALUE_QUERY);
			pstmt.setString(1, paramName);
			rs = pstmt.executeQuery();
			if(rs != null) {
				while (rs.next()){
					paramValue = rs.getString("VALUE");
					break;
				}
			}
		} catch (SQLException e) {
			logger.error(PLMUtil.getStackTrace(e));
			throw new SQLException("select failed");
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException e) {
				logger.error(PLMUtil.getStackTrace(e));
				throw new SQLException("statement or resultset close failed");
			} finally {
				try {
					if (conn != null) {
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
					logger.error(PLMUtil.getStackTrace(e));
					throw new SQLException("connection close failed");
				}
			}
		}

		return paramValue;
	}
}