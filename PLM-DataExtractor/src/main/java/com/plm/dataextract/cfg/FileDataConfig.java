package com.plm.dataextract.cfg;

import java.util.List;

public class FileDataConfig {

	private String filename = null;
	private String table = null;
	private String fullcondition = null;
	private String partialcondition = null;
	private String orderby = null;
	private List<FieldConfig> fields = null;
	
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
	/**
	 * @return the table
	 */
	public String getTable() {
		return table;
	}
	/**
	 * @param table the table to set
	 */
	public void setTable(String table) {
		this.table = table;
	}
	/**
	 * @return the full condition
	 */
	public String getFullcondition() {
		return fullcondition;
	}
	/**
	 * @param condition the condition to set
	 */
	public void setFullcondition(String condition) {
		this.fullcondition = condition;
	}
	/**
	 * @return the condition
	 */
	public String getPartialcondition() {
		return partialcondition;
	}
	/**
	 * @param condition the condition to set
	 */
	public void setPartialcondition(String condition) {
		this.partialcondition = condition;
	}
	/**
	 * @return the orderby clause
	 */
	public String getOrderby() {
		return orderby;
	}
	/**
	 * @param orderby the orderby to set
	 */
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	/**
	 * @return the fields
	 */
	public List<FieldConfig> getFields() {
		return fields;
	}
	/**
	 * @param fields the fields to set
	 */
	public void setFields(List<FieldConfig> fields) {
		this.fields = fields;
	}
	
}
