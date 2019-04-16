package com.plm.util;

public class PermissionInfo implements Cloneable {

	private boolean canQuery = false;
	private boolean canDownload = false;
	private boolean canViewMaps = false;
	private String userType = null;
	/**
	 * @return the canQuery
	 */
	public boolean canQuery() {
		return canQuery;
	}
	/**
	 * @param canQuery the canQuery to set
	 */
	public void setCanQuery(boolean canQuery) {
		this.canQuery = canQuery;
	}
	/**
	 * @return the canDownload
	 */
	public boolean canDownload() {
		return canDownload;
	}
	/**
	 * @param canDownload the canDownload to set
	 */
	public void setCanDownload(boolean canDownload) {
		this.canDownload = canDownload;
	}

	/**
	 * @return the canViewMap
	 */
	public boolean canViewMaps() {
		return canViewMaps;
	}
	/**
	 * @param canViewMap the canViewMap to set
	 */
	public void setCanViewMaps(boolean canViewMaps) {
		this.canViewMaps = canViewMaps;
	}
	
	/**
	 * @return the userType
	 */
	public String getUserType() {
		return userType;
	}
	/**
	 * @param userType the userType to set
	 */
	public void setUserType(String userType) {
		this.userType = userType;
	}

	/**
	 * This returns whether the User is Type of Admin User.
	 * @return boolean - returns true if the user is type of Admin otherwise false.
	 */
	public boolean isAdmin() {
		if("admin".equalsIgnoreCase(userType)) {
			return true;
		}
		return false;
	}

	/**
	 * This returns whether the User is Type of normal User or not.
	 * @return boolean - returns true if the user is type of normal User otherwise false.
	 */
	public boolean isUser() {
		if("user".equalsIgnoreCase(userType)) {
			return true;
		}
		return false;
	}

	/**
	 * This returns whether the User is Type of Author or not.
	 * @return boolean - returns true if the user is type of Author otherwise false.
	 */
	public boolean isAuthor() {
		if("author".equalsIgnoreCase(userType)) {
			return true;
		}
		return false;
	}
	
	/**
	 * This returns whether the User is Type of Guest or not.
	 * @return boolean - returns true if the user is type of Guest otherwise false.
	 */
	public boolean isGuest() {
		if("guest".equalsIgnoreCase(userType)) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		return 	"Can Download:"+ canDownload() + 
				" CanQuery:" + canQuery() + 
				" CanViewMaps:" + canViewMaps() + 
				" UserType:"+ getUserType();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		PermissionInfo retPInfo = new PermissionInfo();
		retPInfo.setCanDownload(canDownload());
		retPInfo.setCanQuery(canQuery());
		retPInfo.setCanViewMaps(canViewMaps());
		retPInfo.setUserType(getUserType());
		return retPInfo;

	}
	
}
