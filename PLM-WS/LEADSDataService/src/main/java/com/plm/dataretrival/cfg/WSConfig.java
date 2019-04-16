package com.plm.dataretrival.cfg;

import com.endeca.navigation.HttpENEConnection;

public class WSConfig {

	private HttpENEConnection endecaConnection = null;
	private int recordsPerPage = 3;
	//private int maxNoOfDaysOfDownloadAllowed = 72360;
	private String endecaHost = null;
	private String endecaPorts = null;
	private String paroleeDataFilename = null;
	private String dateTimeFormat = null;
	private String fileOutputPath = null;
	/**
	 * @return the endecaConnection
	 */
	public HttpENEConnection getEndecaConnection() {
		return endecaConnection;
	}
	/**
	 * @param endecaConnection the endecaConnection to set
	 */
	public void setEndecaConnection(HttpENEConnection endecaConnection) {
		this.endecaConnection = endecaConnection;
	}
	
	/**
	 * @return the paroleeCSVDataZipFilename
	 */
	public String getParoleeDataFilename() {
		return paroleeDataFilename;
	}
	/**
	 * @param paroleeDataFilename the paroleeDataFilename to set
	 */
	public void setParoleeDataFilename(String paroleeDataFilename) {
		this.paroleeDataFilename = paroleeDataFilename;
	}
	/**
	 * @return the dateTimeFormat
	 */
	public String getDateTimeFormat() {
		return dateTimeFormat;
	}
	/**
	 * @param dateTimeFormat the dateTimeFormat to set
	 */
	public void setDateTimeFormat(String dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
	}
	/**
	 * @return the fileOutputPath
	 */
	public String getFileOutputPath() {
		return fileOutputPath;
	}
	/**
	 * @param fileOutputPath the fileOutputPath to set
	 */
	public void setFileOutputPath(String fileOutputPath) {
		this.fileOutputPath = fileOutputPath;
	}
	/**
	 * @return the recordsPerPage
	 */
	public int getRecordsPerPage() {
		return recordsPerPage;
	}
	/**
	 * @param recordsPerPage the recordsPerPage to set
	 */
	public void setRecordsPerPage(int recordsPerPage) {
		this.recordsPerPage = recordsPerPage;
	}
	
	/**
	 * @return the maxNoOfDaysOfDownloadAllowed
	 */
	//public int getMaxNoOfDaysOfDownloadAllowed() {
	//	return maxNoOfDaysOfDownloadAllowed;
	//}
	/**
	 * @param maxNoOfDaysOfDownloadAllowed the maxNoOfDaysOfDownloadAllowed to set
	 */
	//public void setMaxNoOfDaysOfDownloadAllowed(int maxNoOfDaysOfDownloadAllowed) {
	//	this.maxNoOfDaysOfDownloadAllowed = maxNoOfDaysOfDownloadAllowed;
	//}
	/**
	 * @return the Endeca HostName
	 */
	public String getEndecaHost() {
		return endecaHost;
	}
	/**
	 * @param endecaHost the endecaHost to set
	 */
	public void setEndecaHost(String endecaHost) {
		this.endecaHost = endecaHost;
	}	
	/**
	 * @return the Endeca Ports
	 */
	public String getEndecaPorts() {
		return endecaPorts;
	}
	/**
	 * @param endecaPorts the endecaPorts to set
	 */
	public void setEndecaPorts(String endecaPorts) {
		this.endecaPorts = endecaPorts;
	}
}
