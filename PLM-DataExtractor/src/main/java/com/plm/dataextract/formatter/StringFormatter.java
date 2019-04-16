package com.plm.dataextract.formatter;

import com.plm.dataextract.formatter.IFormatter;

/**
 * @author tushardalal
 *
 */
public class StringFormatter implements IFormatter {

	private int minLength = 0;
	private int maxLength = 1;
	private String alignment = IFormatter.LEFT_ALIGNED;
	private char fillChar = ' ';
	private String formattingType = IFormatter.FORMATTING_TYPE_VARIABLE_LENGTH;
	

	/**
	 * @return the minLength
	 */
	public int getMinLength() {
		return minLength;
	}

	/**
	 * @param minLength the minLength to set
	 */
	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	/**
	 * @return the maxLength
	 */
	public int getMaxLength() {
		return maxLength;
	}

	/**
	 * @param maxLength the maxLength to set
	 */
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * @return the alignment
	 */
	public String getAlignment() {
		return alignment;
	}

	/**
	 * @param alignment the alignment to set
	 */
	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}

	/**
	 * @return the fillChar
	 */
	public char getFillChar() {
		return fillChar;
	}

	/**
	 * @param fillChar the fillChar to set
	 */
	public void setFillChar(char fillChar) {
		this.fillChar = fillChar;
	}

	/**
	 * @return the formattingType
	 */
	public String getFormattingType() {
		return formattingType;
	}

	/**
	 * @param formattingType the formattingType to set
	 */
	public void setFormattingType(String formattingType) {
		this.formattingType = formattingType;
	}

	/* (non-Javadoc)
	 * @see com.plm.dataextract.IFormatter#format(java.lang.Object)
	 */
	@Override
	public String format(Object dataValue) {
		StringBuffer sbValue = new StringBuffer();
		if( dataValue != null ) {
			String data = dataValue.toString();
			if(IFormatter.FORMATTING_TYPE_FIXED_LENGTH.equalsIgnoreCase(getFormattingType())) {
				int length = data.length();
				if( length < getMinLength() ) {
					if(IFormatter.LEFT_ALIGNED.equalsIgnoreCase(getAlignment())) {
						sbValue.append(data+getPadding(getMaxLength()-length));
					}else if(IFormatter.RIGHT_ALIGNED.equalsIgnoreCase(getAlignment())) {
						sbValue.append(getPadding(getMaxLength()-length) + data );
					}
				}else if (length > getMaxLength()) {
					sbValue.append(data.substring(0,length));
				}
			}else{
				//Return same value.
				sbValue.append(data);
			}
		}
		return sbValue.toString();
	}
	
	private String getPadding(int length) {
		StringBuffer sbValue = new StringBuffer();
		for(int i=0; i < length; i++) {
			sbValue.append(fillChar);
		}
		return sbValue.toString();
	}
}
