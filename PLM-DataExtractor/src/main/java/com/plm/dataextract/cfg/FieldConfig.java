package com.plm.dataextract.cfg;

public class FieldConfig extends BaseFieldConfig {

	private String fieldName = null;
	private String headerName = null;
	private String formatter = null;
	
	/**
	 * @return the filedName
	 */
	public String getFieldName() {
		return fieldName;
	}
	/**
	 * @param filedName the filedName to set
	 */
	public void setFieldName(String filedName) {
		this.fieldName = filedName;
	}
	/**
	 * @return the headerName
	 */
	public String getHeaderName() {
		return headerName;
	}
	/**
	 * @param headerName the headerName to set
	 */
	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}
	/**
	 * @return the formatter
	 */
	public String getFormatter() {
		return formatter;
	}
	/**
	 * @param formatter the formatter to set
	 */
	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}
}
