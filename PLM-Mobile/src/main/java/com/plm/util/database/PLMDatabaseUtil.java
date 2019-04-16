package com.plm.util.database;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.sql.Blob;
import java.sql.Connection;
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
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.sql.DataSource;

import org.apache.log4j.Logger;


import com.endeca.ui.constants.UI_Props;
import com.plm.constants.PLMConstants;


public class PLMDatabaseUtil {
	private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private static final Logger logger = Logger.getLogger(PLMDatabaseUtil.class);
	private static DataSource ds = null;
    static{
    	init();
    }
    
	public static void init() {
		try {
			logger.info("initializing plm datasource...");

			/*Hashtable<String,String> ht = new Hashtable<String,String>();
			ht.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
		    InetAddress addr = InetAddress.getLocalHost();
		    String hostname = addr.getHostName();
		    String pvdrUrl = "t3://"+hostname+":"+UI_Props.getInstance().getValue("Mobile_wlsport");
			ht.put(Context.PROVIDER_URL, pvdrUrl);
			// Get a context for the JNDI lookup
			ctx = new InitialContext(ht);
			//showJndiContext( ctx, "", "");
			// Look up the data source
			ds = (DataSource) ctx.lookup("plmmultidatasource");*/

			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/InterfacesDS");

			logger.info("plm datasource initialization complete...");
		}catch (Exception e) {
		       logger.warn("Exception was thrown: " + e.getMessage());
		       e.printStackTrace();
		}
	}

    public static void showJndiContext(Context ctx, String name, String space) {
        if (null == name)
            name = "";
        if (null == space)
            space = "";
 
        try {
            NamingEnumeration<NameClassPair> en = ctx.list(name);
            while (en.hasMoreElements()) {
                String delim = 0 < name.length() ? "/" : "";
                NameClassPair nc = (NameClassPair) en.next();
                logger.info(space + name + delim + nc);
                if (40 > space.length())
                    showJndiContext(ctx, nc.getName(), "    " + space);
            }
        }
        catch (javax.naming.NamingException ex) {
            //System.out.println( ex );
        }
    }
	
	public static Connection getConnection() throws SQLException{
		//logger.info("retrieving plm database connection...");
	    Connection dbconn = null;
	    dbconn = ds.getConnection();
		dbconn.setAutoCommit(false);
		//logger.info("plm database connection retrieval complete...");
		return dbconn;
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
		} catch (Exception e) {
			logger.error("error in getting DB connection--> PLMDatabaseUtil-->insert " + e);
			return;
		}
		
		try {
			pstmt = conn.prepareStatement(query);
			for (int i = 0; i < values.size(); i++) {
				pstmt.setString(i + 1, values.get(i));
			}
			pstmt.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			logger.warn("Exception in PLMDatabaseUtil insert method :: " + e.getMessage());
			throw e;
		} finally {
			try {
                if (pstmt != null) {
                	pstmt.close();
                	pstmt = null;
                }
            } catch (SQLException e) {
                logger.warn("Cleanup failed to close Statement." + e.getMessage());
                throw e;
            } finally {
            	try {
            		if (conn != null) {
            			conn.close();
            			conn = null;
            		}
            	} catch (SQLException e) {
            		throw e;
            	}
            }
		}
	}

	public static void deleteSaveSearch(String delete_save_search) throws SQLException {
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			conn = PLMDatabaseUtil.getConnection();
			if(conn.isClosed()){
				init();
				conn = PLMDatabaseUtil.getConnection();
			}	
		} catch (Exception e) {
			logger.error("error in getting DB connection--> PLMDatabaseUtil-->deleteSaveSearch " + e);
			return;
		}
		try {
			String query = "delete from USER_SEARCH where SEARCH_NAME=?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, delete_save_search);
			pstmt.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			logger.warn("delete save search failed: " + e.getMessage());
			throw e;
		} finally {
			try {
                if (pstmt != null) {
                	pstmt.close();
                	pstmt = null;
                }
            } catch (SQLException e) {
                logger.warn("Cleanup failed to close Statement." + e.getMessage());
                throw e;
            } finally {
            	try {
            		if (conn != null) {
            			conn.close();
            			conn = null;
            		}
            	} catch (SQLException e) {
            		throw e;
            	}
            }
		}
	}

	public static boolean getSavedSearch(String save_search_name) throws SQLException {
		Connection conn = PLMDatabaseUtil.getConnection();
		if(conn.isClosed()){
			init();
			conn = PLMDatabaseUtil.getConnection();
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean bSearchExist = false;
		try {
			String query = "select * from USER_SEARCH where SEARCH_NAME=?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, save_search_name);
			rs = pstmt.executeQuery();
			if (rs.next())
				bSearchExist = true;

		} catch (SQLException e) {
			logger.warn("get saved search failed: " + e.getMessage());
			throw e;
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
                logger.warn("Cleanup failed to close Statement." + e.getMessage());
                throw e;
            } finally {
            	try {
            		if (conn != null) {
            			conn.close();
            			conn = null;
            		}
            	} catch (SQLException e) {
            		throw e;
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
		} catch (Exception e) {
			logger.error("error in getting DB connection--> PLMDatabaseUtil-->getSavedSearches " + e);
			return null;
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
            logger.warn("Cleanup failed to close Statement." + e.getMessage());
			throw e;
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
                logger.warn("Cleanup failed to close Statement." + e.getMessage());
                throw e;
            } finally {
            	try {
            		if (conn != null) {
            			conn.close();
            			conn = null;
            		}
            	} catch (SQLException e) {
            		throw e;
            	}
            }
		}
		return resultMap;
	}

	public static String getSavedSearchByID(String id) throws SQLException {
		String rtnQuery = null;
		try{
			Integer.parseInt(id);
		}catch(NumberFormatException e){
			logger.warn("getSavedSearchByID failed with " + e.getMessage());
			return null;
		}
		Connection conn = PLMDatabaseUtil.getConnection();
		if(conn.isClosed()){
			init();
			conn = PLMDatabaseUtil.getConnection();
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
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
			logger.warn("get Saved Search by ID failed." + e.getMessage());
			throw e;
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
                logger.warn("Cleanup failed to close Statement." + e.getMessage());
                throw e;
            } finally {
            	try {
            		if (conn != null) {
            			conn.close();
            			conn = null;
            		}
            	} catch (SQLException e) {
            		throw e;
            	}
            }
		}
		return rtnQuery;
	}
	public static byte[] getPhoto(String cdcNumber, String psize,
			String defaultPhoto) throws Exception {
		byte[] imgData = null;
		String query = "";
		Connection conn = PLMDatabaseUtil.getConnection();
		if(conn.isClosed()){
			init();
			conn = PLMDatabaseUtil.getConnection();
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
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
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, cdcNumber);
			rs = pstmt.executeQuery();
			Blob img = null;
			while (rs.next()) {
				img = rs.getBlob(1);
				imgData = img.getBytes(1, (int) img.length());
				
			}
			if (imgData == null || imgData.length <= 0) {
				imgData = getDefaultImage(psize, defaultPhoto);
				
			}
		} catch(SQLException e){
			logger.warn("getPhoto failed: " + e.getMessage());
			throw e;
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
                logger.warn("Cleanup failed to close Statement and ResultSet with " + e.getMessage());
                throw e;
            } finally {
            	try {
            		if (conn != null) {
            			conn.close();
            			conn = null;
            		}
            	} catch (SQLException e) {
            		throw e;
            	}
            }
		}
		return imgData;
	}

	public static String getPrimaryMugshotID(String cdcNumber) throws SQLException {
		String query = "";
		String phid = null;
		Connection conn = null;
		try {
			conn = PLMDatabaseUtil.getConnection();
			if(conn.isClosed()){
				init();
				conn = PLMDatabaseUtil.getConnection();
			}	
		} catch (Exception e) {
			logger.error("error in getting DB connection--> PLMDatabaseUtil-->getPrimaryMugshotID " + e);
			return phid;
		}
			
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		query = "SELECT id, cdc_num FROM (SELECT id, cdc_num, Row_Number() over (ORDER BY Decode(i.SUBTYPE, 'Full Face Frontal (Primary Mugshot)', 1,2) ASC, insert_date desc) AS rownumber from cpowner.image_info i where i.cdc_num=? AND i.type=1 and i.SUBTYPE IN ('Full Face Frontal (Primary Mugshot)','Full Face Frontal with Glasses','Full Face Frontal with Hat','Full Face Frontal with Scarf','Full Face Frontal (Non-NIST)') AND i.id IN (SELECT id FROM cpowner.thumbnails WHERE id=i.id)) WHERE rownumber = 1";
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, cdcNumber);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				phid = rs.getString(1);
				break;
			}
		} catch(SQLException e){
			logger.warn("getPrimaryMugshotID : " + e.getMessage());
			throw e;
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
                logger.warn("Cleanup failed to close Statement and Resultset with " + e.getMessage());
                throw e;
            } finally {
            	try {
            		if (conn != null) {
            			conn.close();
            			conn = null;
            		}
            	} catch (SQLException e) {
            		throw e;
            	}
            }
		}
		return phid;
	}

	public static String getSecondaryMugshotID(String cdcNumber) throws SQLException {
		String query = "";
		String phid = null;
		Connection conn = PLMDatabaseUtil.getConnection();
		if(conn.isClosed()){
			init();
			conn = PLMDatabaseUtil.getConnection();
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		query = "SELECT id, cdc_num FROM (SELECT id, cdc_num, Row_Number() over (ORDER BY insert_date desc) AS rownumber from cpowner.image_info i where i.cdc_num=? AND i.type=1 and i.SUBTYPE IN ('Right Profile','Right Profile with Glasses','Right Profile with Hat','Right Profile with Scarf','Left Profile','Left Profile with Glasses','Left Profile with Hat','Left Profile with Scarf')) WHERE rownumber = 1";
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, cdcNumber);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				phid = rs.getString(1);
				break;
			}
		} catch(SQLException e){
			logger.warn("getSecondaryMugshotID: " + e.getMessage());
			throw e;
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
                logger.warn("Cleanup failed to close Statement and ResultSet with " + e.getMessage());
                throw e;
            } finally {
            	try {
            		if (conn != null) {
            			conn.close();
            			conn = null;
            		}
            	} catch (SQLException e) {
            		throw e;
            	}
            }
		}
		return phid;
	}

	public static byte[] getPhotoByID(String phid, String psize,
			String defaultPhoto) throws SQLException, Exception {
		/*try{
			Integer.parseInt(phid);
		}catch(NumberFormatException e){
			logger.warn("getPhotoByID failed with " + e.getMessage());
			return null;
		}*/

		Blob img;
		byte[] imgData = null;
		Connection conn = PLMDatabaseUtil.getConnection();
		if(conn.isClosed()){
			init();
			conn = PLMDatabaseUtil.getConnection();
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			if ("t".equals(psize)) {
				pstmt = conn.prepareStatement(PLMConstants.SELECT_THUMBNAIL_QUERY_BY_ID);
			} else if ("p".equals(psize)) {
				pstmt = conn.prepareStatement(PLMConstants.SELECT_PHOTOS_QUERY_BY_ID);
			} else if ("n".equals(psize)) {
				pstmt = conn.prepareStatement(PLMConstants.SELECT_NIST_QUERY_BY_ID);
			} else {
				pstmt = conn.prepareStatement(PLMConstants.SELECT_THUMBNAIL_QUERY_BY_ID);
			}
			
			if(phid == null || phid.equals("null"))
			{
				phid="0";
			}
			
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
				imgData = getDefaultImage(psize, defaultPhoto);
			}
		} catch(SQLException e){
			logger.warn("getPhotoByID failed: " + e.getMessage());
			throw e;
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
                logger.warn("Cleanup failed to close Statement and ResultSet with " + e.getMessage());
                throw e;
            } finally {
            	try {
            		if (conn != null) {
            			conn.close();
            			conn = null;
            		}
            	} catch (SQLException e) {
            		throw e;
            	}
            }
		}
		return imgData;
	}

	public static byte[] getDefaultImage(String psize, String defaultPhoto)
			throws Exception {
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

	public static HashMap<String,String> getPhotoDetails(String cdcNumber) throws SQLException, Exception	 {

		String id = null;
		Connection conn = PLMDatabaseUtil.getConnection();
		if(conn.isClosed()){
			init();
			conn = PLMDatabaseUtil.getConnection();
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String query = "SELECT id FROM (SELECT id, cdc_num, Row_Number() over (ORDER BY insert_date desc) AS rownumber from cpowner.image_info i where i.cdc_num=? AND i.type=1 and i.subtype='Full Face Frontal (Primary Mugshot)') WHERE rownumber = 1";
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, cdcNumber);
			rs = pstmt.executeQuery();
	
			while (rs.next()) {
				id = rs.getString(1);
				break;
			}
		} catch(SQLException e){
			logger.warn("getPhotoDetails failed: " + e.getMessage());
			throw e;
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
                logger.warn("Cleanup failed to close Statement and ResultSet with " + e.getMessage());
                throw e;
            } finally {
            	try {
            		if (conn != null) {
            			conn.close();
            			conn = null;
            		}
            	} catch (SQLException e) {
            		throw e;
            	}
            }
		}
		HashMap<String,String> info = getPhotoDetailsByID(id + "");
		
		return info;
	}

	public static HashMap<String,String> getPhotoDetailsByID(String id) throws SQLException {
		String cdcnum = null;
		String type_text = null;
		String subtype = null;
		String descr = null;
		Date insert_date = null;
		String inserted_by = null;
		Connection conn = PLMDatabaseUtil.getConnection();
		if(conn.isClosed()){
			init();
			conn = PLMDatabaseUtil.getConnection();
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		if(id == null || id.equals("null"))
		{
			id="0";
		}
		String query = "Select i.cdc_num, decode(i.type,1,'MUGSHOT',2,'SCARS, MARKS AND TATTOOS',3,'VEHICLE',4,'RESIDENCE',5,'WEAPON',6,'EVIDENCE',7,'OTHER') as type_text, i.subtype, i.descr, i.insert_date, i.inserted_by from cpowner.image_info i, cpowner.photos ph Where i.id = ? AND i.cdc_num= ph.cdc_num(+) AND i.id = ph.id(+)";

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
			logger.warn("getPhotoDetailsByID failed: " + e.getMessage());
			throw e;
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
                logger.warn("Cleanup failed to close Statement and Resultset with " + e.getMessage());
                throw e;
            } finally {
            	try {
            		if (conn != null) {
            			conn.close();
            			conn = null;
            		}
            	} catch (SQLException e) {
            		throw e;
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
			throws SQLException, Exception {
		String id = null;
		ArrayList<String> ids = new ArrayList<String>();
		Connection conn = PLMDatabaseUtil.getConnection();
		if(conn.isClosed()){
			init();
			conn = PLMDatabaseUtil.getConnection();
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = null;

		if (type == -1) {
			query = "SELECT id FROM (SELECT id, cdc_num, Row_Number() over (ORDER BY insert_date desc) AS rownumber from cpowner.image_info i where i.cdc_num=? AND i.type IN(3,4,5,6,7)) order by rownumber";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, cdcNumber);
		} else {
			query = "SELECT id FROM (SELECT id, cdc_num, Row_Number() over (ORDER BY insert_date desc) AS rownumber from cpowner.image_info i where i.cdc_num=? AND i.type=?) order by rownumber";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, cdcNumber);
			pstmt.setInt(2, type);
		}
		
		try {
			rs = pstmt.executeQuery();
			while (rs.next()) {
				id = rs.getString(1);			
				ids.add(id);
			}
		}  catch(SQLException e){
			logger.warn("getPhotoIDs failed: " + e.getMessage());
			throw e;
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
                logger.warn("Cleanup failed to close Statement and Resultset with " + e.getMessage());
                throw e;
            } finally {
            	try {
            		if (conn != null) {
            			conn.close();
            			conn = null;
            		}
            	} catch (SQLException e) {
            		throw e;
            	}
            }
		}
		return ids;
	}
	
	public static int getPhotoCount(String cdcNumber) throws SQLException{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = null;
		int cnt = 0;
		try {
			conn = PLMDatabaseUtil.getConnection();
			if(conn.isClosed()){
				init();
				conn = PLMDatabaseUtil.getConnection();
			}	
		} catch (Exception e) {
			logger.error("error in getting DB connection--> PLMDatabaseUtil-->getPhotoCount " + e);
			return cnt;
		}
		
		query = "SELECT COUNT(*) FROM IMAGE_INFO WHERE CDC_NUM = ?";
		try{
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, cdcNumber);
			rs = pstmt.executeQuery();
			while(rs.next()){
				cnt = rs.getInt(1);
				break;
			}					
		} catch(SQLException e){
			logger.warn("getPhotoCount failed: " + e.getMessage());
			throw e;
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
                logger.warn("Cleanup failed to close Statement and Resultset with " + e.getMessage());
                throw e;
            } finally {
            	try {
            		if (conn != null) {
            			conn.close();
            			conn = null;
            		}
            	} catch (SQLException e) {
            		throw e;
            	}
            }
		}
		return cnt;
	}
	
	public static boolean isDBConnected(DataSource datasource){
		return true;
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
}
