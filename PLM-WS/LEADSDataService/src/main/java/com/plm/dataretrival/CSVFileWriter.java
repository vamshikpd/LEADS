package com.plm.dataretrival;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.log4j.Logger;
import com.plm.util.PLMUtil;



public class CSVFileWriter {

	private FileWriter fWriterParolee = null;
	private FileWriter fWriterAddress = null;
	private FileWriter fWriterAlias = null;
	private FileWriter fWriterAb3CDC = null;
	private FileWriter fWriterJob = null;
	private FileWriter fWriterMoniker = null;
	private FileWriter fWriterOffense = null;
	private FileWriter fWriterSMT = null;
	private FileWriter fWriterSpecialCondition = null;
	private FileWriter fWriterUnit = null;
	private FileWriter fWriterVehicle = null;
	private String FILE_PATH = "C:/tmp/";
	public static final String NEW_LINE_CHAR = System.getProperty("line.separator"); //\n
	private static final Logger logger = Logger.getLogger(CSVFileWriter.class);
	
	public CSVFileWriter(String filePath) {
		FILE_PATH = filePath;
	}
	
	public void init() {
		openFiles();
	}
	
	public void openFiles() {
		try {
			
			File makeFolder = new File(FILE_PATH);
			if(!makeFolder.exists()) {
				makeFolder.mkdir();
			}
			
			fWriterParolee = new FileWriter(new File(FILE_PATH+File.separator+"AB3_PAR.LST"));
			fWriterAddress = new FileWriter(new File(FILE_PATH+File.separator+"AB3_ADD.LST"));
			fWriterAlias = new FileWriter(new File(FILE_PATH+File.separator+"AB3_ALI.LST"));
			fWriterAb3CDC = new FileWriter(new File(FILE_PATH+File.separator+"AB3_CDC.LST"));
			fWriterJob = new FileWriter(new File(FILE_PATH+File.separator+"AB3_JOB.LST"));
			fWriterMoniker = new FileWriter(new File(FILE_PATH+File.separator+"AB3_MON.LST"));
			fWriterOffense = new FileWriter(new File(FILE_PATH+File.separator+"AB3_OFF.LST"));
			fWriterSMT = new FileWriter(new File(FILE_PATH+File.separator+"AB3_SMT.LST"));
			fWriterSpecialCondition = new FileWriter(new File(FILE_PATH+File.separator+"AB3_SPC.LST"));
			fWriterVehicle = new FileWriter(new File(FILE_PATH+File.separator+"AB3_VEH.LST"));
			
			//fWriterUnit = new FileWriter(new File(FILE_PATH+"LDSDATAU.csv"));
			//fWriterDOJUnit = new FileWriter(new File(FILE_PATH+"LDSDATAD.csv"));AB3_CDC.LST
		} catch (IOException e) {
			logger.error(PLMUtil.getStackTrace(e));
		}
	}
	
	/**
	 * Writes the Parolee Record into the file.
	 * @param str
	 */
	public void writeParolee(String str) throws IOException{
		if(str != null && str.trim().length() > 0) {
			if( fWriterParolee != null ) {
				fWriterParolee.write(str+NEW_LINE_CHAR);
				fWriterParolee.flush();
			}
		}
	}
	
	/**
	 * Writes the Parolee Record into the file.
	 * @param str
	 */
	public void writeAddress(String str) throws IOException{
		if(str != null && str.trim().length() > 0) {
			if( fWriterAddress != null ) {
				fWriterAddress.write(str+NEW_LINE_CHAR);
				fWriterAddress.flush();
			}
		}
	}
	
	/**
	 * Writes the Alias Record into the file.
	 * @param str
	 */
	public void writeAlias(String str) throws IOException{
		if(str != null && str.trim().length() > 0) {
			if( fWriterAlias != null ) {
				fWriterAlias.write(str+NEW_LINE_CHAR);
				fWriterAlias.flush();
			}
		}
	}
	
	/**
	 * Writes the Ab3 CDC Record into the file.
	 * @param str
	 */
	public void writeAb3CDC(String str) throws IOException{
		if(str != null && str.trim().length() > 0) {
			if( fWriterAb3CDC!= null ) {
				fWriterAb3CDC.write(str+NEW_LINE_CHAR);
				fWriterAb3CDC.flush();
			}
		}
	}
	
	/**
	 * Writes the Job Record into the file.
	 * @param str
	 */
	public void writeJob(String str) throws IOException {
		if(str != null && str.trim().length() > 0) {
			if( fWriterJob != null ) {
				fWriterJob.write(str+NEW_LINE_CHAR);
				fWriterJob.flush();
			}
		}
	}
	
	/**
	 * Writes the Moniker Record into the file.
	 * @param str
	 */
	public void writeMoniker(String str) throws IOException {
		if(str != null && str.trim().length() > 0) {
			if( fWriterMoniker != null ) {
				fWriterMoniker.write(str+NEW_LINE_CHAR);
				fWriterMoniker.flush();
			}
		}
	}
	
	/**
	 * Writes the Offense Record into the file.
	 * @param str
	 */
	public void writeOffense(String str) throws IOException {
		if(str != null && str.trim().length() > 0) {
			if( fWriterOffense != null ) {
				fWriterOffense.write(str+NEW_LINE_CHAR);
				fWriterOffense.flush();
			}
		}
	}
	
	/**
	 * Writes the SMT Record into the file.
	 * @param str
	 */
	public void writeSMT(String str) throws IOException {
		if(str != null && str.trim().length() > 0) {
			if( fWriterSMT != null ) {
				fWriterSMT.write(str+NEW_LINE_CHAR);
				fWriterSMT.flush();
			}
		}
	}
	
	/**
	 * Writes the Special Condition Record into the file.
	 * @param str
	 */
	public void writeSpecialCondition(String str) throws IOException {
		if(str != null && str.trim().length() > 0) {
			if( fWriterSpecialCondition!= null ) {
				fWriterSpecialCondition.write(str+NEW_LINE_CHAR);
				fWriterSpecialCondition.flush();
			}
		}
	}
	
	/**
	 * Writes the Unit Record into the file.
	 * @param str
	 */
	public void writeUnit(String str) throws IOException {
		if(str != null && str.trim().length() > 0) {
			if( fWriterUnit != null ) {
				fWriterUnit.write(str+NEW_LINE_CHAR);
				fWriterUnit.flush();
			}
		}
	}
	
	/**
	 * Writes the Vehicle Record into the file.
	 * @param str
	 */
	public void writeVehicle(String str) throws IOException {
		if(str != null && str.trim().length() > 0) {
			if( fWriterVehicle != null ) {
				fWriterVehicle.write(str+NEW_LINE_CHAR);
				fWriterVehicle.flush();
			}
		}
	}
	
	/**
	 * Closes all files.
	 */
	public void closeFiles() {
		try {
			if( fWriterParolee != null ) {
				fWriterParolee.flush();
				fWriterParolee.close();
				fWriterParolee = null;
			}
			if( fWriterAddress != null ) {
				fWriterAddress.flush();
				fWriterAddress.close();
				fWriterAddress = null;
			}
			if( fWriterAlias != null ) {
				fWriterAlias.flush();
				fWriterAlias.close();
				fWriterAlias = null;
			}
			if( fWriterAb3CDC != null ) {
				fWriterAb3CDC.flush();
				fWriterAb3CDC.close();
				fWriterAb3CDC = null;
			}
			if( fWriterJob != null ) {
				fWriterJob.flush();
				fWriterJob.close();
				fWriterJob= null;
			}
			if( fWriterMoniker != null ) {
				fWriterMoniker.flush();
				fWriterMoniker.close();
				fWriterMoniker = null;
			}
			if( fWriterOffense != null ) {
				fWriterOffense.flush();
				fWriterOffense.close();
				fWriterOffense = null;
			}
			if( fWriterSMT != null ) {
				fWriterSMT.flush();
				fWriterSMT.close();
				fWriterSMT= null;
			}
			if( fWriterSpecialCondition != null ) {
				fWriterSpecialCondition.flush();
				fWriterSpecialCondition.close();
				fWriterSpecialCondition = null;
			}
			if( fWriterUnit != null ) {
				fWriterUnit.flush();
				fWriterUnit.close();
				fWriterUnit= null;
			}
			if( fWriterVehicle != null ) {
				fWriterVehicle.flush();
				fWriterVehicle.close();
				fWriterVehicle= null;
			}
		}catch(Exception e) {
			logger.error(PLMUtil.getStackTrace(e));
		}
	}	
}
