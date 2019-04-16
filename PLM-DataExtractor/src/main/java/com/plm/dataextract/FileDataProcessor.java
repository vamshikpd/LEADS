package com.plm.dataextract;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.plm.dataextract.cfg.BaseFieldConfig;
import com.plm.dataextract.cfg.ConfigDataProvider;
import com.plm.dataextract.cfg.DBConfig;
import com.plm.dataextract.cfg.DataExtractConfig;
import com.plm.dataextract.cfg.FieldConfig;
import com.plm.dataextract.cfg.FileDataConfig;
import com.plm.dataextract.formatter.IFormatter;

public class FileDataProcessor extends BaseFileDataProcessor {

	public static final String PROCESS_NAME= "PLM_DATA_EXTRACT_PROCESS";
	public static final String STATUS_IN_PROCESS= "IN_PROCESS";
	public static final String STATUS_SUCCESS = "SUCCESS";
	public static final String STATUS_ERROR = "ERROR";

	public static final String DATA_EXTRACT_UPTO = "${LAST_DATA_EXTRACT_UPTO_DATE}";
	public static final String SYSTEM_DATE = "${SYSTEM_DATE}";
	private static final int JDBC_FETCH_SIZE = 1000;

	private String sqlQuery = null;
	private String fieldSeparator = null;
	private String processID = null;
	private String filename = null;
	private String onlyFilename = null;
	private ResultSet rsData = null;
	private BufferedWriter bWriter = null;
	private boolean fieldSeparatorInEOL = false;
	
	private PreparedStatement psInsertDataExtractStatus = null;
	private PreparedStatement psUpdateDataExtractStatus = null;

	private String systemDate = null;
	private String dataExtractedUpTo = null;
	private String processStatSeqID;
	private String extractMode = "";
	
	public FileDataProcessor( String processID, String filename, String sqlQuery, String fieldSeparator, String dataExtractedUpTo, String systemDate, String processStatSeqID, String extractMode ) {
		super(extractMode);
		this.processID = processID;
		this.filename = filename;
		this.fieldSeparator = fieldSeparator;
		this.dataExtractedUpTo = dataExtractedUpTo;
		this.systemDate = systemDate;
		this.processStatSeqID = processStatSeqID;
		this.sqlQuery = parseQuery(sqlQuery);
		this.extractMode = extractMode;
		
		if(filename != null) {
			int index = filename.lastIndexOf("\\");
			if(index > 0) {
				onlyFilename = filename.substring(index+1);
			}else {
				index = filename.lastIndexOf("//");
				if( index > 0) {
					onlyFilename = filename.substring(index+1);
				}
			}
		}
	}
	
//	public void setProcessStatSeqID( String processStatSeqID ) {
//		this.processStatSeqID = processStatSeqID;
//	}
	public void setFieldSeparatorInEOL(boolean bValue) {
		fieldSeparatorInEOL = bValue;
	}
	
	/**
	 * Parse the Constant of Date to replace actual Dates.
	 * @param origSQL
	 * @return parsed String.
	 */
	private String parseQuery(String origSQL) {
		System.out.println("BEFORE PARSE: >>>"+ origSQL+"<<<");
		String SQL = replaceStr(origSQL,DATA_EXTRACT_UPTO, dataExtractedUpTo);
		//SQL = replaceStr(SQL,SYSTEM_DATE, systemDate );
		System.out.println("AFTER  PARSE: >>>"+ SQL+"<<<");
		return SQL;
	}
	
	private String replaceStr(String mainStr, String replaceStr, String replaceWith) {
		StringBuffer sbRet = new StringBuffer(mainStr);
		int iIndex = 0;
		if( mainStr != null && replaceStr != null) {
			while(iIndex >= 0) {
				iIndex = sbRet.indexOf(replaceStr);
				if( iIndex >= 0 ) {
					sbRet.replace(0,sbRet.length(), sbRet.substring(0,iIndex) + replaceWith + sbRet.substring(iIndex+replaceStr.length()) );
				}
			}
		}
		return sbRet.toString();
	}
	
	public boolean canStartProcess() {
		boolean bProcessRunningStat = false;
		DBConfig dbConfig = ConfigDataProvider.getInstance().getDbConfig();
		Connection dbConnection = dbConfig.getConnection();
		if( dbConnection != null) {
			try {
				PreparedStatement psCheckProcessStatus = dbConnection.prepareStatement(
						"SELECT count(*) FROM DATA_EXTRACT_STATUS WHERE STATUS = '"+STATUS_IN_PROCESS+"' "+
						"AND PROCESS_ID = (SELECT PROCESS_ID FROM DATA_EXTRACT_STATUS WHERE ID = "+
						"(SELECT MAX(ID) FROM DATA_EXTRACT_STATUS WHERE PROCESS_NAME = '"+FileDataProcessor.PROCESS_NAME+"')) GROUP BY PROCESS_ID");
				
				System.out.println("SELECT count(*) FROM DATA_EXTRACT_STATUS WHERE STATUS = '"+STATUS_IN_PROCESS+"' AND PROCESS_ID=(SELECT PROCESS_ID FROM DATA_EXTRACT_STATUS WHERE ID = (select max(ID) from DATA_EXTRACT_STATUS WHERE PROCESS_NAME = '"+FileDataProcessor.PROCESS_NAME+"')) GROUP BY PROCESS_ID");
				
				//(SELECT PROCESS_ID FROM DATA_EXTRACT_STATUS WHERE ID = (select max(ID) from DATA_EXTRACT_STATUS WHERE PROCESS_NAME = '"+FileDataProcessor.PROCESS_NAME+"')) GROUP BY PROCESS_ID"
				
				//psCheckProcessStatus.setString(1, PROCESS_NAME);
				//psCheckProcessStatus.setString(2, onlyFilename);
				ResultSet rsResult = psCheckProcessStatus.executeQuery();
				if(rsResult.next()) {
					int processCount = rsResult.getInt(1);
					//TODO - Logger - Info
					System.out.println("Total "+processCount+ " Processe(s) are running for (" +  onlyFilename + ") extract.");
					if(processCount == 0) {
						bProcessRunningStat = true;
					}
				}else{
					//No IN_PROCESS Records found for max PROCESS_ID - indicates there is safe to run the Extract Process.
					bProcessRunningStat = true;
				}
			} catch (SQLException e) {
				//TODO Logging
				System.out.println("FileDateProcessor: canStartProcess: Exception while checking RUNNING Status");
				e.printStackTrace();
			}finally{
				try{
					dbConnection.close();
				}catch(SQLException e) {
					//TODO Logging
					System.out.println("FileDateProcessor: canStartProcess: Exception while checking RUNNING Status - in collection close.");
					e.printStackTrace();
				}
			}
		}
		return bProcessRunningStat;
	}
	
	public void startDataProcessing() {
		System.out.println("Going to [start] processing: FOR: "+ processID);
		
		//Create Output Handler.
		createOutputHandler();
		
		//Create Row Header.
		processHeaders();

		DBConfig dbConfig = ConfigDataProvider.getInstance().getDbConfig();
		Connection dbConnection = dbConfig.getConnection();
		if( dbConnection != null) {
			String extractId = getNextId(dbConnection);
			try {
				//Create Statements for Data Extract Status
				psInsertDataExtractStatus = dbConnection.prepareStatement("INSERT INTO DATA_EXTRACT_STATUS (ID, PROCESS_ID, PROCESS_NAME, FILE_NAME, DATA_EXTRACT_UPTO_DATE, PROCESS_START_DATE, STATUS ) VALUES (?, ?, ?, ?, to_date( ?, 'DD-MM-YYYY HH24:MI:SS'), sysdate, '"+STATUS_IN_PROCESS+"' )");
				psUpdateDataExtractStatus = dbConnection.prepareStatement("UPDATE DATA_EXTRACT_STATUS SET PROCESS_END_DATE = sysdate, STATUS = ?, MESSAGE = ? WHERE ID = ? AND PROCESS_NAME = ? AND FILE_NAME = ? AND PROCESS_ID = ?");
				
				insertDataExtractStatus(dbConnection, extractId, processStatSeqID, PROCESS_NAME, onlyFilename, systemDate );
				Statement stmt = dbConnection.createStatement();

				// 2018-04-08 emil: increase fetch size to 1000 from default of 10 to decrease # of network requests
				stmt.setFetchSize(JDBC_FETCH_SIZE);

				rsData = stmt.executeQuery(sqlQuery);
				processResultSet(processID, rsData);
				updateDataExtractStatus(dbConnection, extractId, processStatSeqID, PROCESS_NAME, onlyFilename, STATUS_SUCCESS, "" );
			}catch(Exception e) {
				System.out.println("Exception while executing the SQL: " + sqlQuery);
				updateDataExtractStatus(dbConnection, extractId, processStatSeqID, PROCESS_NAME, onlyFilename, STATUS_ERROR, "ERROR: while processing ("+processID+")\n"+e.getMessage() );
				e.printStackTrace();
			}finally {
				try {
					dbConnection.close();
				}catch(Exception e) {
					System.out.println("Exception while Closing the Connection for : " + sqlQuery);
					updateDataExtractStatus(dbConnection, extractId, processStatSeqID, PROCESS_NAME, onlyFilename, STATUS_ERROR, "ERROR: in closing db Connection while processing ("+processID+")\n"+e.getMessage() );
					e.printStackTrace();
				}
			}
		}else{
			System.out.println("Exception while creating DB Connection");
		}
		System.out.println("Going to [stop] processing: FOR: "+ processID);
	}

	protected void createOutputHandler() {
		try {
			bWriter = new BufferedWriter(new FileWriter(filename));
		}catch(IOException e) {
			e.printStackTrace();
		}		
	}
	
	protected void processHeaders() {
		DataExtractConfig deConfig = ConfigDataProvider.getInstance().getDataExtractConfig();
		if(deConfig != null) {
			Map<String, FileDataConfig> mExtFiles = deConfig.getExtractionFiles();
			FileDataConfig fdCfg = mExtFiles.get(processID);
			List<FieldConfig> fields = fdCfg.getFields();
			
			ArrayList<String> alColHeader = new ArrayList<String>();
			for(FieldConfig fieldCfg:  fields) {
				alColHeader.add(fieldCfg.getHeaderName());
			}
			String headerRow = convertToRow(alColHeader);
			writeRow(headerRow);		
		}else{
			System.out.println("No column/field data found for the Key:" + processID);
		}
	}
	
	protected void processResultSet(String processID, ResultSet rsData) {
		List<FieldConfig> fields = null;
		DataExtractConfig deConfig = ConfigDataProvider.getInstance().getDataExtractConfig();
		if(deConfig != null) {
			Map<String, FileDataConfig> mExtFiles = deConfig.getExtractionFiles();
			FileDataConfig fdCfg = mExtFiles.get(processID);
			fields = fdCfg.getFields();
		}else{
			System.out.println("No column/field data found for the Key:" + processID);
		}
		
		try {
			fireProcessingStart();
			//Process Meta data for Column Data Types.
			//----------------------------------------
			ResultSetMetaData rsm = rsData.getMetaData();
			int i=1;
			for( FieldConfig fc : fields ) {
				String colClass = rsm.getColumnClassName(i);
				fc.setFieldDataType(colClass);
				i++;
			}
			//----------------------------------------
			while(rsData.next()) {
				ArrayList<String> arRowElements = processRow(fields, rsData);
				String singleRow = convertToRow(arRowElements);
				writeRow(singleRow);
			}
			closeFile();
//			//-----------------
//			try{
//				System.out.println("[START] - WAITING for THREAD PROCESS CHECKING.....");
//				Thread.sleep(30000);
//			}catch(Exception e) {}
//			System.out.println("[ END ] - WAITING for THREAD PROCESS CHECKING.....");
//			//-----------------
			fireProcessingStop();
		}catch(Exception e) {
			System.out.println("Error while processing the Resulting Data for Key: " + processID );
			e.printStackTrace();
		}
	}

	public ArrayList<String> processRow(List<FieldConfig> fields, ResultSet rsData){
		ArrayList<String> alRow = new ArrayList<String>();
		if(fields != null) {
			int columnIndex = 1;
			for(FieldConfig fCfg : fields ) {
				String formatterName = fCfg.getFormatter();
				//System.out.println("Formatter "+formatterName+ " for field: " + fCfg.getFiledName() );
				IFormatter formatter = getFormatter(formatterName);
				//Check to DataType of 
				Object rawValue = null;
				try {
					if( BaseFieldConfig.DATATYPE_CLASS_TIMESTAMP.equals(fCfg.getFieldDataType()) ) {
						rawValue = rsData.getTimestamp(columnIndex);
					}else if( BaseFieldConfig.DATATYPE_CLASS_BIGDECIMAL.equals(fCfg.getFieldDataType()) ) {
						rawValue = rsData.getBigDecimal(columnIndex);
					}else{
						rawValue = rsData.getString(columnIndex);
					}
				}catch(Exception e) {
					System.out.println("Not able to get the value for Column Name: '"+fCfg.getFieldName()+"' at index: " + columnIndex );
					e.printStackTrace();
				}
				if( formatter != null ) {
					String formatedValue = formatter.format( rawValue );
					alRow.add(formatedValue);
				}else{
					alRow.add(rawValue.toString());
				}
				columnIndex++;
			}
		}else{
			System.out.println("No column/field data found for the Key:" + processID);
		}
		return alRow;
	}
	
	private String convertToRow(ArrayList<String> rowData) {
		StringBuffer sbRow = new StringBuffer();
		if(rowData != null) {
			int iCounter = 0;
			for(String elementValue: rowData) {
				if(iCounter == 0) {
					sbRow.append(elementValue);
				}else{
					sbRow.append(fieldSeparator+elementValue);
				}
				iCounter++;
			}
		}
		if(fieldSeparatorInEOL) {
			sbRow.append(fieldSeparator);
		}
		return sbRow.toString();
	}
	
	private void writeRow(String sRowData) {
		try {
			bWriter.write(sRowData);
			bWriter.newLine();
			bWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void closeFile() {
		try {
			bWriter.flush();
			bWriter.close();
		} catch (IOException e) {
			System.out.println("Error while Closing the file Stream..");
			e.printStackTrace();
		}
	}

	public String getNextId(Connection dbConnection) {
		String id = "1";
		String sqlQuery="select data_extract_seq.nextval from dual";
		try {
			Statement stmt = dbConnection.createStatement();
			ResultSet rsData = stmt.executeQuery(sqlQuery);
			if(rsData.next()) {
				id = rsData.getString(1);
			}
		}catch(Exception e) {
			System.out.println("Exception while executing the SQL: " + sqlQuery);
			e.printStackTrace();
		}
		return id;
	}	
	
	private void insertDataExtractStatus(Connection dbConnection, String id, String processID, String processName, String fileName, String dataExtractedUpTo ) {
		if(psInsertDataExtractStatus != null) {
			try {
				psInsertDataExtractStatus.setString(1, id);
				psInsertDataExtractStatus.setString(2, processID);
				psInsertDataExtractStatus.setString(3, processName);
				psInsertDataExtractStatus.setString(4, fileName);
				psInsertDataExtractStatus.setString(5, dataExtractedUpTo);
				psInsertDataExtractStatus.execute();
				//System.out.println("=======> ID: " + id + " processID: " + processID + " ProcessName:" + processName + " FileName:" + fileName + " Upto-Date:" + dataExtractedUpTo  );
				dbConnection.commit();
			} catch (SQLException e) {
				//TODO Logging
				System.out.println("FileDateProcessor: insertDataExtractStatus: Unable to insert the process Status");
				e.printStackTrace();
			} catch (Exception e) {
				//TODO Logging
				System.out.println("FileDateProcessor: insertDataExtractStatus: Unable to insert the process Status");
				e.printStackTrace();
			}
		}
	}
	
	private void updateDataExtractStatus(Connection dbConnection, String id, String processID, String processName, String fileName, String status, String message ) {
		if(psUpdateDataExtractStatus != null) {
			try {
				psUpdateDataExtractStatus.setString(1, status);
				psUpdateDataExtractStatus.setString(2, message);
				psUpdateDataExtractStatus.setString(3, id);
				psUpdateDataExtractStatus.setString(4, processName);
				psUpdateDataExtractStatus.setString(5, fileName);
				psUpdateDataExtractStatus.setString(6, processID);
				psUpdateDataExtractStatus.execute();
				dbConnection.commit();
			} catch (SQLException e) {
				//TODO Logging
				System.out.println("FileDateProcessor: updateDateExtractStatus: Unable to update the process Status");
				e.printStackTrace();
			}
		}
	}
	public String getProcessID() {
		return processID;
	}
}
