package com.plm.dataretrival;
public class DataException extends Exception {
	private static final long serialVersionUID = 1L;
	private String errorCode = null;
	private String filename = null;
	public DataException(String errorCode) {
		super(errorCode);
		setErrorCode(errorCode);
	}
	public DataException(String errorCode, String filename) {
		super(errorCode);
		setErrorCode(errorCode);
		setFilename(filename);
	}
	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}
	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
}