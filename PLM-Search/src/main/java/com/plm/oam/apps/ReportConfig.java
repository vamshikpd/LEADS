package com.plm.oam.apps;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ReportConfig implements InitializingBean {

	private String reportFilename = "{AGENCY}_{DATE}.csv" ;
	
	private String fileOutputPath = "REPORT_TMP";
	
	private String reportTitle = "Inactive User Report - Date: {DATE}";
	
	private int inActiveDaysVal = 45;
	
	private HashMap<String,String> reportHeaders = new HashMap<String,String>();
		
	private static final String SEP = ",";
	
	public static final int PAGE_SIZE = 500;
	
	public static ReportConfig getConfig() throws Exception {
		ApplicationContext ac = new ClassPathXmlApplicationContext("context.xml",ReportConfig.class);
		Object obj = ac.getBean(ReportConfig.class.getName());
		if(obj == null)
			throw new Exception("Bean Initialization Error...");
		return (ReportConfig)obj;
	}
	
	public String getFileOutputPath() {
		return fileOutputPath;
	}
	
	public String getReportFilename() {
		return reportFilename;
	}


	public void setInActiveDaysVal(int inActiveDaysVal) {
		this.inActiveDaysVal = inActiveDaysVal;
	}

	public int getInActiveDaysVal() {
		return inActiveDaysVal;
	}

	public void setReportFilename(String reportFilename) {
		this.reportFilename = reportFilename;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setFileOutputPath(String fileOutputPath) {
		this.fileOutputPath = fileOutputPath;
	}
	
	public void setReportHeaders(HashMap<String,String> reportHeaders) {
		this.reportHeaders = reportHeaders;
	}

	public HashMap<String,String> getReportHeaders() {
		return reportHeaders;
	}
	
	public String getDisplayableReportHeaders() {
		StringBuffer sbout = new StringBuffer();
		Collection<String> headers = getReportHeaders().values();
		Iterator<String> iter = headers.iterator();
		while(iter.hasNext()) {
			sbout.append(iter.next());
				if(iter.hasNext())
					sbout.append(SEP);
		}
		return sbout.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
