/**
 * 
 */
package com.plm.dataextract.cfg;

/**
 * @author tushardalal
 *
 */
public class BaseFieldConfig {

	public static final String DATATYPE_CLASS_STRING="java.lang.String";
	public static final String DATATYPE_CLASS_TIMESTAMP="java.sql.Timestamp";
	public static final String DATATYPE_CLASS_BIGDECIMAL="java.math.BigDecimal";
	
	public static final String STRING_FORMATTER_NAME="StringFormatter";
	private String fieldDataType = DATATYPE_CLASS_STRING;

	/**
	 * @return the fieldDataType
	 */
	public String getFieldDataType() {
		return fieldDataType;
	}

	/**
	 * @param fieldDataType the fieldDataType to set
	 */
	public void setFieldDataType(String fieldDataType) {
		this.fieldDataType = fieldDataType;
	}
	
}
