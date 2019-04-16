package com.plm.dataextract;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.plm.dataextract.cfg.ConfigDataProvider;
import com.plm.dataextract.cfg.DBConfig;
import com.plm.dataextract.cfg.DataExtractConfig;
import com.plm.dataextract.cfg.FileDataConfig;

public class DataExtractor {
	
	private static int noOfThreads = 1;
	private Hashtable<String, FileDataProcessor> htFileDataProcessor = new Hashtable<String, FileDataProcessor>();
	private String dataExtractedUpTo = null;
	private String systemDate = null;
	
	private PreparedStatement psPartialExtractDate = null;
	private PreparedStatement psFullExtractDate = null;
	
	public static final String EXTRACT_FULL="FULL";
	public static final String EXTRACT_PARTIAL="PARTIAL";
	
	private String sExtractMode = EXTRACT_PARTIAL;
	private boolean bCheckProcessRunningStatus = true;
	private boolean bRunExtractProcess = false;
	
	public DataExtractor() {
		initParams();
		startDataFileGeneration();
	}
	
	private void initParams() {
	    sExtractMode = System.getProperty("ExtractMode",EXTRACT_PARTIAL);
	    DataExtractConfig dataExtractConfig = ConfigDataProvider.getInstance().getDataExtractConfig();
	    if(dataExtractConfig != null) {
	    	noOfThreads = dataExtractConfig.getNoOfParallelThreads();
	    }
	    System.out.println("No of Threads : " + noOfThreads);
	    initLastExtractUptoAndSysDate();
	}
	
	public void initLastExtractUptoAndSysDate() {
		DBConfig dbConfig = ConfigDataProvider.getInstance().getDbConfig();
		Connection dbConnection = dbConfig.getConnection();
		if( dbConnection != null) {
			try {
				if(psPartialExtractDate == null) {
					psPartialExtractDate = dbConnection.prepareStatement("SELECT to_char(min(DATA_EXTRACT_UPTO_DATE),'DD-MM-YYYY HH24:MI:SS') AS DATA_EXTRACT_UPTO_DATE, to_char(sysdate,'DD-MM-YYYY HH24:MI:SS') AS SYSTEM_DATE FROM DATA_EXTRACT_STATUS WHERE PROCESS_ID=(SELECT PROCESS_ID FROM DATA_EXTRACT_STATUS WHERE ID = (select max(ID) from DATA_EXTRACT_STATUS WHERE PROCESS_NAME = '"+FileDataProcessor.PROCESS_NAME+"')) GROUP BY PROCESS_ID");
				}
				if( psFullExtractDate == null ) {
					//SELECT sysdate, sysdate-((365*4)+1)+1,  sysdate - (decode( MOD(to_char(sysdate,'YYYY')-0, 4), 0, 366, 365)) - (decode( MOD(to_char(sysdate,'YYYY')-1, 4), 0, 366, 365)) - (decode( MOD(to_char(sysdate,'YYYY')-2, 4), 0, 366, 365)) - (decode( MOD(to_char(sysdate,'YYYY')-3, 4), 0, 366, 365)) from dual;
					psFullExtractDate = dbConnection.prepareStatement("SELECT to_char(Min(created_date),'DD-MM-YYYY HH24:MI:SS') AS DATA_EXTRACT_UPTO_DATE,to_char(sysdate,'DD-MM-YYYY HH24:MI:SS') AS SYSTEM_DATE FROM cpowner.parolee WHERE cdc_num IN (SELECT cdc_num FROM CPOWNER.PLM_CDC)");
				}
				
				if( sExtractMode.equalsIgnoreCase(EXTRACT_PARTIAL) ) {
					ResultSet rsResult = psPartialExtractDate.executeQuery();
					if( rsResult.next() ) {
						dataExtractedUpTo = rsResult.getString(1);
						systemDate = rsResult.getString(2);
					}else{
						rsResult = psFullExtractDate.executeQuery();
						if( rsResult.next() ) {
							dataExtractedUpTo = rsResult.getString(1);
							systemDate = rsResult.getString(2);
						}
						System.out.println("Changing Extract MODE from PARTIAL to FULL as there is no history found which indicates the earlier extract is done successfully.");
						sExtractMode = EXTRACT_FULL;
					}
				} else {
					ResultSet rsResult = psFullExtractDate.executeQuery();
					if( rsResult.next() ) {
						dataExtractedUpTo = rsResult.getString(1);
						systemDate = rsResult.getString(2);
					}
					//TODO Add to Logger
					System.out.println("Setting Extract MODE to FULL as invalid option specified");
					sExtractMode = EXTRACT_FULL;
				}
				
			} catch (SQLException e) {
				//TODO Logging
				System.out.println("FileDateProcessor: insertDateExtractStatus: Unable to update the process Status");
				e.printStackTrace();
			}finally{
				try {
					dbConnection.close();
				}catch(SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getProcessStatusSeqId() {
		DBConfig dbConfig = ConfigDataProvider.getInstance().getDbConfig();
		Connection dbConnection = dbConfig.getConnection();
		String id = "1";
		String sqlQuery="select data_extract_process_id_seq.nextval from dual";
		try {
			Statement stmt = dbConnection.createStatement();
			ResultSet rsData = stmt.executeQuery(sqlQuery);
			if(rsData.next()) {
				id = rsData.getString(1);
			}
		}catch(Exception e) {
			System.out.println("Exception while executing the SQL: " + sqlQuery);
			e.printStackTrace();
		}finally{
			try {
				dbConnection.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return id;
	}	
	
	
	public void startDataFileGeneration() {
		preProcess();
		process();
		postProcess();
	}
	
	protected void preProcess() {
		htFileDataProcessor.clear();
	    DataExtractConfig dataExtractConfig = ConfigDataProvider.getInstance().getDataExtractConfig();
	    if(dataExtractConfig != null) {
	    	//Set/Retrieve Last Update Upto Date and System Date as well as Process Sequence No.
			//DBConfig dbConfig = ConfigDataProvider.getInstance().getDbConfig();
			//Connection dbConnection = dbConfig.getConnection();
			
			String processStatSeqID = getProcessStatusSeqId();
		    //initLastExtractUptoAndSysDate(dbConnection);
		    System.out.println("---> EXTRACT MODE: " + sExtractMode + "  LastExtractDate:" + dataExtractedUpTo);
   	
		    Map<String, FileDataConfig> mapExtractionFiles = dataExtractConfig.getExtractionFiles();
		    for( String fileRefName : mapExtractionFiles.keySet() ) {
		    	FileDataConfig fdCfg = mapExtractionFiles.get(fileRefName);
		    	String sql = SQLGenerator.getInstance().generateSQL(fdCfg,sExtractMode);
		    	FileDataProcessor fdp;
		    	if(sExtractMode.equalsIgnoreCase(EXTRACT_FULL)) {
		    		fdp = new FileDataProcessor(fileRefName, dataExtractConfig.getFullOutputFolderPath()+File.separator+fdCfg.getFilename(), sql, dataExtractConfig.getFieldSeparator(), dataExtractedUpTo, systemDate, processStatSeqID,this.sExtractMode);
		    	} else { 
	    			fdp = new FileDataProcessor(fileRefName, dataExtractConfig.getPartialOutputFolderPath()+File.separator+fdCfg.getFilename(), sql, dataExtractConfig.getFieldSeparator(), dataExtractedUpTo, systemDate, processStatSeqID,this.sExtractMode);
		    	}
		    	fdp.setFieldSeparatorInEOL(dataExtractConfig.isFieldSeparatorInEOL());
		    	htFileDataProcessor.put(fileRefName, fdp );
		    }
	    }
	}

	protected void process(){
		Thread process = new Thread(new ProcessThread() );
		process.start();
	}
	
	protected void postProcess() {
		//TODO
		//Copy ALL the generated files into the Target Folder.
	}
	
	public static void main(String args[]) {
		new DataExtractor();
	}

	
	class ProcessThread implements Runnable {
		private int iThreadCounter = 0;
		private FileProcessorListener fpListener = null;
		private long startTime = 0;
		private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss S");
		
		public ProcessThread() {
			fpListener = new FileProcessorListener() {
				@Override
				public void stopProcessingFile(FileProcessEvent fpEvent) {
					iThreadCounter--;
				}
				@Override
				public void startProcessingFile(FileProcessEvent fpEvent) {
				}
			};
		}
		@Override
		public void run() {
			
			Vector<String> vKeys = new Vector<String>();
			vKeys.addAll(htFileDataProcessor.keySet());
			
			while(true) {
				if( iThreadCounter < noOfThreads ) {
					String key = null;
					if( vKeys.size() > 0 ) {
						key = vKeys.remove(0);
					}
					if( key != null ) {
						if(vKeys.size() == htFileDataProcessor.size()-1) {
							startTime = Calendar.getInstance().getTimeInMillis();
							//TODO Add into Log also.
							System.out.println("--- Actual Data Extraction process Start Time (Local Machine): "+ dateFormat.format(Calendar.getInstance().getTime()) +" ---");
						}
						System.out.println("\n\nProcessing Thread started for " + key);
						final String fileKey = key;
						
						Runnable fileProcess = new Runnable() {
							@Override
							public void run() {
								iThreadCounter++;
								FileDataProcessor fdp = htFileDataProcessor.get(fileKey);
								fdp.addFileProcessorListener(fpListener);
								fdp.startDataProcessing();
							}
						};
						
						FileDataProcessor fdp = htFileDataProcessor.get(key);
						//START the Thread only if it is eligible.
						
						if( bCheckProcessRunningStatus ) {
							if( sExtractMode.equalsIgnoreCase(EXTRACT_PARTIAL) ) {
								bRunExtractProcess = fdp.canStartProcess();
								bCheckProcessRunningStatus = false;
							}else{
								bRunExtractProcess = true;
								bCheckProcessRunningStatus = false;
							}
						}
						if(bRunExtractProcess) {
							new Thread(fileProcess).start();
						}else{
							System.out.println("---------I G N O R I N G ---- PROCESS for " + key + "  " + fileKey);
						}
					}else{
						//
						//Start New Thread which keep watch on current running process and notifies 
						//when the all processes are complete.
						//
						Runnable processWatcher = new Runnable() {
							@Override
							public void run() {
								while(true) {
									//System.out.println("Current running processes" + iThreadCounter);
									try{
										Thread.sleep(1000);
									}catch(Exception e) {
									}
									if(iThreadCounter <= 0) {
										//TODO Add into Log also.
										System.out.println("--- Data Extraction process complete within "+((Calendar.getInstance().getTimeInMillis()-startTime)/1000)+" second(s) - at Time: "+ dateFormat.format(Calendar.getInstance().getTime()) +" ---");
										break;
									}
								}
							}
						};
						new Thread(processWatcher).start();
						//Start watcher thread and end the current Thread.
						break;
					}
				} else {
					//
					//Wait for Running Threads to complete...
					//
					try{
						//System.out.print("waiting...: COU:" + iThreadCounter + " - "+ noOfThreads+" >|< ");
						Thread.sleep(1000);
					}catch(Exception e) {
					}
				}
				
			}
		}
	}
}
