package com.plm.dataretrival.cfg;

import com.endeca.navigation.HttpENEConnection;


public class WSConfig {

	private String fileOutputPath = null;
	private String paroleePhotoFilename = null;
	private HttpENEConnection endecaConnection = null;
	private int recordsPerPage = 3;
	private String endecaHost = null;
	private String endecaPorts = null;
	private String dateTimeFormat = null;
	private int maxPhotoDownloadCount = 15;
	private int sqlInParameterLimit = 3;

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
	 * @return the paroleeCSVDataZipFilename
	 */
	public String getParoleePhotoFilename() {
		return paroleePhotoFilename;
	}
	/**
	 * @param paroleePhotoFilename the paroleePhotoFilename to set
	 */
	public void setParoleePhotoFilename(String paroleePhotoFilename) {
		this.paroleePhotoFilename = paroleePhotoFilename;
	}	
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
	/**
	 * @return the maxPhotoDownloadCount
	 */
	public int getMaxPhotoDownloadCount() {
		return maxPhotoDownloadCount;
	}
	/**
	 * @param maxPhotoLimit the maxPhotoDownloadCount to set
	 */
	public void setMaxPhotoDownloadCount(int maxPhotoLimit) {
		this.maxPhotoDownloadCount = maxPhotoLimit;
	}
	
	/**
	 * @return the sqlInParameterLimit
	 */
	public int getSqlInParameterLimit() {
		return sqlInParameterLimit;
	}
	/**
	 * @param sqlInParameterLimit the sqlInParameterLimit to set
	 */
	public void setSqlInParameterLimit(int sqlInParameterLimit) {
		this.sqlInParameterLimit = sqlInParameterLimit;
	}
}
