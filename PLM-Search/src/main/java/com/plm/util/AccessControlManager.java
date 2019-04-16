package com.plm.util;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Access Control Manager which provides the Permission / User Roles.
 */
public class AccessControlManager {

	public static final String NODE_ROLE_MANAGER="ROLE-MANAGER";
	public static final String NODE_GROUP="GROUP";
	
	public static final String NODE_DOWNLOAD="DOWNLOAD";
	public static final String NODE_QUERY="QUERY";
	public static final String NODE_USER="USER";
	public static final String NODE_VIEWMAPS="VIEWMAPS";
	public static final String NODE_AMS="AMS";

	public static final String ATTR_DEFAULT="default";
	public static final String ATTR_VIEW="view";
	public static final String ATTR_TYPE="type";
	
	public static final String ADMIN="admin";
	public static final String AUTHOR="author";
	public static final String USER="user";
	public static final String GUEST="guest";
	
	private static final String SEPARATOR = "\\|";
	private static Document doc = null;

	private static Hashtable<String, PermissionInfo> htPermission = null;
	private static PermissionInfo defaultPermission = null;
	private static final Logger logger = Logger.getLogger(AccessControlManager.class);

	public AccessControlManager(ServletContext sc) {
		if(htPermission == null || htPermission.size() == 0 ) {
			htPermission = new Hashtable<String, PermissionInfo>();			
			try {
				doc = getConfigDocument(sc);
			}catch(Exception e) {
				e.printStackTrace();
			}
			init();
		}
	}
	
	/**
	 * Initialize the UserType and Permissions from roleConfig.xml file.
	 */
	private void init() {
		if( doc != null ) {
			processGroups(doc.getChildNodes());
		}
	}
	
	/**
	 * This extract the Group details from the XML DOM and creates the cache of PermissionInfo objects.
	 * @param nodeList of Root node.
	 */
	private void processGroups(NodeList nodeList) {
		if(nodeList != null) {
			int nodeListSize = nodeList.getLength();
			for(int i=0;i < nodeListSize; i++) {
				Node node = nodeList.item(i);
				String nodeName = node.getNodeName();
				if(nodeName.equalsIgnoreCase(NODE_GROUP) ) {
					String userGroupName = node.getAttributes().getNamedItem("name").getNodeValue();
					String groupType = null;
					if( node.getAttributes().getNamedItem(ATTR_TYPE) != null ) {
						groupType = node.getAttributes().getNamedItem(ATTR_TYPE).getNodeValue();
					}
					PermissionInfo pInfo = new PermissionInfo();
					NodeList groupPermissionList = node.getChildNodes();
					int nlSize = groupPermissionList.getLength();
					for(int j=0;j < nlSize; j++) {
						Node permissionNode = groupPermissionList.item(j);
						String permissionNodeName = permissionNode.getNodeName();
						if(permissionNodeName.equalsIgnoreCase(NODE_DOWNLOAD) ) {
							NamedNodeMap nnmAttributes = permissionNode.getAttributes();
							Node viewAttrNode = nnmAttributes.getNamedItem(ATTR_VIEW);
							if(viewAttrNode.getNodeValue().trim().equalsIgnoreCase("true")) {
								pInfo.setCanDownload(true);
							}
						}else if(permissionNodeName.equalsIgnoreCase(NODE_QUERY) ) {
							NamedNodeMap nnmAttributes = permissionNode.getAttributes();
							Node viewAttrNode = nnmAttributes.getNamedItem(ATTR_VIEW);
							if(viewAttrNode.getNodeValue().trim().equalsIgnoreCase("true")) {
								pInfo.setCanQuery(true);
							}
						}else if(permissionNodeName.equalsIgnoreCase(NODE_VIEWMAPS) ) {
							NamedNodeMap nnmAttributes = permissionNode.getAttributes();
							Node viewAttrNode = nnmAttributes.getNamedItem(ATTR_VIEW);
							if(viewAttrNode.getNodeValue().trim().equalsIgnoreCase("true")) {
								pInfo.setCanViewMaps(true);
							}
						}else if(permissionNodeName.equalsIgnoreCase(NODE_USER) ) {
							NamedNodeMap nnmAttributes = permissionNode.getAttributes();
							Node viewAttrNode = nnmAttributes.getNamedItem(ATTR_TYPE);
							pInfo.setUserType(viewAttrNode.getNodeValue());
						}
						else if(permissionNodeName.equalsIgnoreCase(NODE_AMS) ) {
							NamedNodeMap nnmAttributes = permissionNode.getAttributes();
							Node viewAttrNode = nnmAttributes.getNamedItem(ATTR_VIEW);
							if(viewAttrNode.getNodeValue().trim().equalsIgnoreCase("true")) {
								pInfo.setCanViewAMS(true);
							}
						}
					}
					if(groupType != null && groupType.equalsIgnoreCase(ATTR_DEFAULT)) {
						defaultPermission = pInfo;
					}else{
						htPermission.put(userGroupName, pInfo);
					}
				}else{
					NodeList childNodes = node.getChildNodes();
					if(childNodes != null) {
						processGroups(childNodes);
					}
				}
			}
		}
	}
	
	/**
	 * This returns the PermissionInfo Object for the given UserGroup. It supports the multiple UserGroups separated by : sign.
	 * In case of multiple group it makes the UNION of access permission and Highest accessibility ROLE among the given UserGroups.
	 * @param userGroup: AD memberOf attribute
	 * @return
	 */
	public PermissionInfo getPermission(String userGroup) {
		PermissionInfo retPInfo = null;
		String[] strGroups = userGroup.split(SEPARATOR);
		if(strGroups.length == 1) {
			retPInfo = htPermission.get(strGroups[0].trim());
		}else{
			for (String strGroup : strGroups) {

				strGroup = extractGroupCN(strGroup); // get CN from group's DN

				PermissionInfo pInfo = htPermission.get(strGroup.trim());
				if (retPInfo == null && pInfo != null) {
					try {
						retPInfo = (PermissionInfo) pInfo.clone();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (retPInfo != null && pInfo != null) {
					if (pInfo.canDownload()) {
						retPInfo.setCanDownload(pInfo.canDownload());
					}
					if (pInfo.canQuery()) {
						retPInfo.setCanQuery(pInfo.canQuery());
					}
					if (pInfo.canViewMaps()) {
						retPInfo.setCanViewMaps(pInfo.canViewMaps());
					}
					if (pInfo.canViewAMS()) {
						retPInfo.setCanViewAMS(pInfo.canViewAMS());
					}
					//If returning Object has the Lower User type then the comparing Object - then upgrade the User Type to higher user type.
					if (retPInfo.isGuest() && !pInfo.isGuest()) {
						retPInfo.setUserType(pInfo.getUserType());
					} else if (retPInfo.isUser() && !pInfo.isGuest() && !pInfo.isUser()) {
						retPInfo.setUserType(pInfo.getUserType());
					} else if (retPInfo.isAuthor() && !pInfo.isGuest() && !pInfo.isUser() && !pInfo.isAuthor()) {
						retPInfo.setUserType(pInfo.getUserType());
					}
				}
			}
		}
		if(retPInfo == null) {			
			retPInfo = defaultPermission;
		}
		return retPInfo;
	}

    /**
     * @param strGroup: a memberOf string for a single group
     * @return CN of a group or empty string
     */
    private String extractGroupCN(String strGroup) {
	    String groupCN = "";

	    // strGroup consists of a text in the format CN=...,OU=..,OU=...,DC=...,DC=...
        if (strGroup.indexOf("CN=",0) < 0 ) {
			// if a group is not formatted in AD-like way, CN=...,OU=...
			// then return the original group name from parameter
        	return strGroup;
        }

        // extract Group's CN from membership string
        int cnIndex = strGroup.indexOf("CN=");
        if (cnIndex > -1) {
            int cnTokenLen = strGroup.indexOf(",", cnIndex) - cnIndex + 1;
            groupCN = strGroup.substring(cnIndex + 3, cnTokenLen);
            // at this point, the strGroup should contain something like "App-CDCR-LEADS-Default"
        }

        return groupCN;
    }

    /**
	 * Implemented the methods for OLAT - to retain the existing functionality.
	 * @param sc - ServletContext.
	 * @return Map of UserType
	 */
	public Map getRolesMap(ServletContext sc) {
		return getRolesFor("App-CDCR-LEADS-Downloaders|App-CDCR-LEADS-Query");
	}	
	
	/**
	 * Implemented the methods for OLAT - to retain the existing functionality.
	 * @param roleName - user role name.
	 * @return Map of UserTypes
	 */
	public Map getRolesFor(String roleName) {
		HashMap<String, Object> roleMap = new HashMap<String, Object>();
		String[] strGroups = roleName.split(SEPARATOR);
		for (String strGroup : strGroups) {
			PermissionInfo pInfo = htPermission.get(strGroup.trim());
			if (pInfo != null) {
				if (pInfo.isAdmin()) {
					roleMap.put("isAdmin", pInfo.isAdmin());
				} else if (pInfo.isAuthor()) {
					roleMap.put("isAuthor", pInfo.isAuthor());
				} else if (pInfo.isUser()) {
					roleMap.put("isUser", pInfo.isUser());
				} else {
					roleMap.put("isGuest", true);
				}
			}
		}
		return roleMap;
	}	

	/**
	 * This reads the roleConfig.xml file and creates the Document object and returns it.
	 * @param sc - ServletContext
	 * @return - Document object.
	 */
	private Document getConfigDocument(ServletContext sc) {
		try {
			String fileName = sc.getRealPath("/WEB-INF/roleConfig.xml");
			if(fileName == null) {
				 URL resourceURL = sc.getResource("/WEB-INF/roleConfig.xml");
				 if(resourceURL != null) {
					 fileName = resourceURL.getPath();
				 }else{
					 logger.error("RoleConfig - ResourceURL is Null.." );
				 }
			}			
			File file = new File(fileName);
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = docBuilder.parse(file);		
		}catch(Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	public static void main(String args[]) {
		AccessControlManager acm = new AccessControlManager(null);
		acm.init();
		
	}
}
