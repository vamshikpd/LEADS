package com.plm.dataextract.formatter;

public interface IFormatter {

	public static final String LEFT_ALIGNED = "LEFT_ALIGNED";
	public static final String RIGHT_ALIGNED = "RIGHT_ALIGNED";
	
	public static final String FORMATTING_TYPE_FIXED_LENGTH = "FORMATTING_TYPE_FIXED_LENGTH";
	public static final String FORMATTING_TYPE_VARIABLE_LENGTH="FORMATTING_TYPE_VARIABLE_LENGTH";
	//format
	//, int minLength, int maxLength, int justification
	public String format(Object dataValue );
}
