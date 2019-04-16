/**
 * Bean class for Data Extract Configuration
 */
package com.plm.dataextract.cfg;

import java.util.Map;

import com.plm.dataextract.formatter.IFormatter;

/**
 * @author tushardalal
 *
 */
public class DataExtractConfig {
	
	private Map<String, FileDataConfig> extractionFileMap = null;
	private Map<String, IFormatter> formatters = null;
	private String fieldSeparator = null;
	private String fullOutputFolderPath = null;
	private String partialOutputFolderPath = null;
	private int noOfParallelThreads = 1;
	private boolean fieldSeparatorInEOL = false;
	
	/**
	 * @return the extrationFileList
	 */
	public Map<String, FileDataConfig> getExtractionFiles() {
		return extractionFileMap;
	}
	/**
	 * @param extrationFileMap the extrationFileList to set
	 */
	public void setExtractionFiles(Map<String, FileDataConfig> extractionFileMap) {
		this.extractionFileMap = extractionFileMap;
	}
	/**
	 * @return the fieldSeparator
	 */
	public String getFieldSeparator() {
		return fieldSeparator;
	}
	/**
	 * @param fieldSeparator the fieldSeparator to set
	 */
	public void setFieldSeparator(String fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}
	/**
	 * @return the outputFolderPath
	 */
	public String getFullOutputFolderPath() {
		return fullOutputFolderPath;
	}
	/**
	 * @param outputFolderPath the outputFolderPath to set
	 */
	public void setFullOutputFolderPath(String fullOutputFolderPath) {
		this.fullOutputFolderPath = fullOutputFolderPath;
	}
	
	/**
	 * @return the outputFolderPath
	 */
	public String getPartialOutputFolderPath() {
		return partialOutputFolderPath;
	}
	/**
	 * @param outputFolderPath the outputFolderPath to set
	 */
	public void setPartialOutputFolderPath(String partialOutputFolderPath) {
		this.partialOutputFolderPath = partialOutputFolderPath;
	}
	
	/**
	 * @return the formatters
	 */
	public Map<String, IFormatter> getFormatters() {
		return formatters;
	}
	/**
	 * @param formatters the formatters to set
	 */
	public void setFormatters(Map<String, IFormatter> formatters) {
		this.formatters = formatters;
	}

	/**
	 * @return the noOfParallelThreads
	 */
	public int getNoOfParallelThreads() {
		return noOfParallelThreads;
	}
	/**
	 * @param noOfParallelThreads the noOfParallelThreads to set
	 */
	public void setNoOfParallelThreads(int noOfParallelThreads) {
		this.noOfParallelThreads = noOfParallelThreads;
	}
	/**
	 * @return the fieldSeparatorInEOL
	 */
	public boolean isFieldSeparatorInEOL() {
		return fieldSeparatorInEOL;
	}
	/**
	 * @param fieldSeparatorInEOL the fieldSeparatorInEOL to set
	 */
	public void setFieldSeparatorInEOL(boolean fieldSeparatorInEOL) {
		this.fieldSeparatorInEOL = fieldSeparatorInEOL;
	}	
}
