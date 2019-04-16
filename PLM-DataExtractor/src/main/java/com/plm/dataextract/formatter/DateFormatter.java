package com.plm.dataextract.formatter;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.plm.dataextract.formatter.IFormatter;

public class DateFormatter implements IFormatter {

	private String dateFormat = "MM/dd/yyyy";
	private SimpleDateFormat sdFormat = new SimpleDateFormat(dateFormat);
	
	/**
	 * @return the dateFormat
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(String dateFormat) {
		if(dateFormat != null) {
			try {
				sdFormat = new SimpleDateFormat(dateFormat);
				this.dateFormat = dateFormat;
			}catch(Exception e) {
				sdFormat = new SimpleDateFormat(this.dateFormat);
				e.printStackTrace();
			}
		}else{
			sdFormat = new SimpleDateFormat(this.dateFormat);
		}
	}

	@Override
	public String format(Object dataValue) {
		StringBuffer sbValue = new StringBuffer();
		//System.out.println("DATE Formatter. Value. >" + dataValue+"<");
		if(dataValue != null && dataValue instanceof Date) {
			try {
				sbValue.append( sdFormat.format((Date)dataValue) ) ;
			}catch(Exception e) {
				System.out.println("Exception while formatting the data value. >" + dataValue+"<");
				e.printStackTrace();
			}
		}
		return sbValue.toString();
	}

}
