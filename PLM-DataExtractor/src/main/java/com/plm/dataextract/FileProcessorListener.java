package com.plm.dataextract;

import java.util.EventListener;

/**
 * File Processor Listener
 * 
 * @author tushardalal
 */
public interface FileProcessorListener extends EventListener {

	/**
	 * This method gets called when the File Processing Starts
	 * @param fpEvent
	 */
	public void startProcessingFile(FileProcessEvent fpEvent);
	/**
	 * This method gets called when the File Processing Stops / Finished.
	 * @param fpEvent
	 */
	public void stopProcessingFile(FileProcessEvent fpEvent);
	
}
