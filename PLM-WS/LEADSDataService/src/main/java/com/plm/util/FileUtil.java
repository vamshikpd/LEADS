package com.plm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import org.apache.log4j.Logger;

import com.plm.dataretrival.DataException;
import com.plm.dataretrival.SearchCriteriaInfo;
import com.plm.dataretrival.cfg.ConfigDataProvider;
import com.plm.dataretrival.cfg.WSConfig;
import com.plm.dataretrival.cfg.WSErrorMessages;
import com.plm.ws.endpoint.ParoleeDataEndpoint;


public class FileUtil {

	public static final String PLACEHOLDER_USERNAME="USERNAME";
	public static final String PLACEHOLDER_DATE_TIME = "DATE_TIME";
	public static final String TMP_FILE_EXTENTION = ".tmp";
	public static final String INPROGRESS_FILE_EXTENTION = ".inprogress";
	private static final Logger logger = Logger.getLogger(FileUtil.class);
	private static String folderOutPutPath = null;

	public static boolean createZipFile(String zipFileName, String sourceFileName) {
		return createZipFile(zipFileName, sourceFileName, null);
	}
	public static boolean createZipFile(String zipFileName, String sourceFileName, String zipEntryPath) {
		boolean bSuccess = false;
		
		try {
			ZipOutputStream zOut = new ZipOutputStream(new FileOutputStream(zipFileName));
			byte[] buffer = new byte[1024];
			FileInputStream fisSource = new FileInputStream(sourceFileName);
			if( zipEntryPath != null ) {
			String onlySourceFileName = getOnlyFilename(sourceFileName);
			zOut.putNextEntry(new ZipEntry(zipEntryPath+File.separator+onlySourceFileName));
			} else {
				zOut.putNextEntry(new ZipEntry(sourceFileName));
			}
			//Write Source File.
			int length = 0;
			while((length = fisSource.read(buffer)) > 0) {
				zOut.write(buffer, 0, length);
			}
			fisSource.close();
			zOut.closeEntry();
			zOut.close();
			bSuccess=true;
		} catch (FileNotFoundException e) {
			logger.error(PLMUtil.getStackTrace(e));
			bSuccess=false;
		} catch (IOException e) {
			logger.error(PLMUtil.getStackTrace(e));
			bSuccess=false;
		}
		return bSuccess;
	}
	
	public static boolean createZipFileFromFolder(String zipFileName, String sourceFolder, String zipEntryPath) throws IOException {
		boolean bSuccess = false;
		ZipOutputStream zOut = null;
		zOut = new ZipOutputStream(new FileOutputStream(zipFileName));
		byte[] buffer = new byte[1024];
		
		File subFiles[] = null;
		File directory = new File(sourceFolder);
		if(directory.isDirectory()) {
			subFiles = directory.listFiles(); 
		}
		
		if( subFiles != null && subFiles.length > 0 ) {
			for( int i=0; i < subFiles.length; i++) {

				String sourceFileName = sourceFolder+File.separator+subFiles[i].getName();
				//Add the Zip Entry and close it in the End after adding the Entry.
				FileInputStream fisSource = new FileInputStream(sourceFileName);
				if( zipEntryPath != null ) {
					String onlySourceFileName = getOnlyFilename(sourceFileName);
					if(zipEntryPath.isEmpty()){
						zOut.putNextEntry(new ZipEntry(onlySourceFileName));
					}else{
						zOut.putNextEntry(new ZipEntry(zipEntryPath+File.separator+onlySourceFileName));
					}
				} else {
					zOut.putNextEntry(new ZipEntry(sourceFileName));
				}
				//Write Source File.
				int length = 0;
				while((length = fisSource.read(buffer)) > 0) {
					zOut.write(buffer, 0, length);
				}
				fisSource.close();
				zOut.closeEntry();
			}
		}
		zOut.close();
		return true;
	}
	
	public static boolean deleteFile(String filename) {
		boolean deleteStatus = false;
		try {
			File fileToDelete = new File(filename);
			if( fileToDelete.isDirectory() ) {
				deleteFiles(fileToDelete.listFiles());
			}
			deleteStatus = fileToDelete.delete();
		} catch (Exception e) {
			logger.error(PLMUtil.getStackTrace(e));
		}
		return deleteStatus;
	}
	
	public static boolean deleteFiles(File filesToDelete[]) {
		boolean bStatus = false;
		if(filesToDelete != null) {
			for( int i=0; i < filesToDelete.length; i++) {
				if(filesToDelete[i].isDirectory()) {
					deleteFiles( filesToDelete[i].listFiles() );
					bStatus = filesToDelete[i].delete();
				}else{
					bStatus = filesToDelete[i].delete();
				}
			}
		}
		return bStatus;
	}
	
	public static boolean renameFile(String sourceFileName, String targetFileName){
		boolean bStatus = false;
		//Obtain the reference of the existing file
		File oldFile = new File(sourceFileName); 
		//Now invoke the renameTo() method on the reference, oldFile in this case
		bStatus = oldFile.renameTo(new File(targetFileName));
		return bStatus;
	}
	
	public static String getParoleeDataFilename(String username) {
		ConfigDataProvider cdProvider = ConfigDataProvider.getInstance();
		String filename = cdProvider.getWSConfig().getParoleeDataFilename();
		return generateOutputFilename(filename,username);
	}
	
	public static String checkAndGenerateUniqueFilename(String folderPath, String fileName) {
		int iCounter = 0;
		File file = new File(folderPath+File.separator+fileName+TMP_FILE_EXTENTION);
		while(file.exists()) {
			file = new File(folderPath+File.separator+fileName+TMP_FILE_EXTENTION+"_"+(++iCounter));
			if( iCounter > 10 ) {
				return null;
			}
		}
		if(iCounter > 0) {
			return fileName+TMP_FILE_EXTENTION+"_"+iCounter;
		}else{
			return fileName+TMP_FILE_EXTENTION;
		}
	}
	
	private static String generateOutputFilename(String filename, String username) {
		return PLMUtil.replaceAllPlaceHolder(filename, username);
	}
	
	public static String convertFileExtZipToInprogress(String filename) {
		return convertFileExt(filename, ".zip", ".inprogress");
	}
	
	public static String convertFileExtXmlToZip(String filename) {
		return convertFileExt(filename, ".xml", ".zip");
	}
	
	public static String convertFileExtZipToXml(String filename) {
		return convertFileExt(filename, ".zip", ".xml");
	}

	public static String getOnlyFilename(String fullpathFileName) {
		if(fullpathFileName != null) {
			int index = fullpathFileName.lastIndexOf(File.separatorChar);
			if(index > 0) {
				return fullpathFileName.substring(index+1);
			}
		}
		return fullpathFileName;
	}
	
	public static String convertFileExt(String filename, String fromExt, String toExt) {
		String retFilename = null;
		if(filename != null) {
			int index = filename.toLowerCase().lastIndexOf(fromExt);
			if( index >= 0 ) {
				retFilename = filename.substring(0,index)+toExt;
			}
		}
		return retFilename;
	}
	
	public static String extractActualFileName(String fileName) {
		String retFilename = null;
		if(fileName != null) {
			int index = fileName.lastIndexOf(TMP_FILE_EXTENTION);
			if( index >= 0) {
				retFilename = fileName.substring(0, index);
				int index2 = retFilename.lastIndexOf(".");
				if( index2 >=0 ) {
					retFilename = retFilename.substring(0,index2)+fileName.substring(index+4)+retFilename.substring(index2);
				}
			}
		}
		return retFilename;
	}
	
	public static void createErrorFile(String filename, String errorCode ) {

		try {
	        WSErrorMessages errorMessages = ConfigDataProvider.getInstance().getErrorMessages();
			String TAB = "\t";
			String NEWLINE = "\n";
			String errMsg = "";
			StringBuffer fileContent = new StringBuffer();
			fileContent.append("<InternalParoleeDataResponse>"+NEWLINE);
			fileContent.append(TAB+"<TxnStatus>"+ PLMConstants.RESPONSE_STATUS_ERROR +"</TxnStatus>"+NEWLINE);
			fileContent.append(TAB+"<Error>"+NEWLINE);
			fileContent.append(TAB+TAB+"<ErrorCode>"+errorCode+"</ErrorCode>"+NEWLINE);
	    	if(errorMessages != null) {
	    		Map<String, String> errorMap = errorMessages.getErrorMessageMap();
	    		errMsg = errorMap.get(errorCode);
	    		if(errMsg == null) {
    				errMsg="Unknown Error";
	    		}
	    	}
	    	//WSConfig wsCfg = ConfigDataProvider.getInstance().getWSConfig();
	    	fileContent.append(TAB+TAB+"<ErrorMessage>"+ errMsg  +"</ErrorMessage>"+NEWLINE);
			fileContent.append(TAB+"</Error>"+NEWLINE);
			fileContent.append("</InternalParoleeDataResponse>"+NEWLINE);
			FileWriter fWriter = new FileWriter(new File(filename));
			fWriter.write(fileContent.toString());
			fWriter.flush();
			fWriter.close();
		} catch (IOException e) {
			logger.error(PLMUtil.getStackTrace(e));
		}
	}
	
	public static void createInprogressFile(String filename, SearchCriteriaInfo scInfo ) throws IOException  {
		//try {
			String TAB = "\t";
			String NEWLINE = "\n";
			StringBuffer fileContent = new StringBuffer();
			fileContent.append("<InternalParoleeDataRequest>"+NEWLINE);
			fileContent.append(TAB+"<CaseNumber>"+ scInfo.getCaseNumber() +"</CaseNumber>"+NEWLINE);
			fileContent.append(TAB+"<SearchCriteria>"+NEWLINE);
			fileContent.append(TAB+TAB+"<LastUpdateDate>"+scInfo.getLastUpdateDate()+"</LastUpdateDate>"+NEWLINE);
	    	fileContent.append(TAB+TAB+"<City>"+scInfo.getCity()+"</City>"+NEWLINE);
	    	fileContent.append(TAB+TAB+"<County>"+scInfo.getCounty()+"</County>"+NEWLINE);
			fileContent.append(TAB+"</SearchCriteria>"+NEWLINE);
			fileContent.append("</InternalParoleeDataRequest>"+NEWLINE);
			FileWriter fWriter = new FileWriter(new File(filename));
			fWriter.write(fileContent.toString());
			fWriter.flush();
			fWriter.close();
		//} catch (IOException e) {
//			logger.error(PLMUtil.getStackTrace(e));
//			throw new DataException(PLMConstants.ERR_INPROGRESS_FILE_CREATION);
//		}
	}
	
	public static boolean fileExists(String fileNameWithPath){
		File file = new File(fileNameWithPath);
		return file.isFile();
	}
	
	public static boolean folderExists(String folderNameWithPath){
		File file = new File(folderNameWithPath);
		return file.isDirectory();
	}
	
	public static Calendar getModifiedTimestamp(String fileNameWithPath){
		File file = new File(fileNameWithPath);
		Calendar dateModified = Calendar.getInstance();
		dateModified.setTimeInMillis(file.lastModified());
		return dateModified;
	}

	public static String getFileOutputPath(){
		if(folderOutPutPath == null){
			WSConfig wsCfg = ConfigDataProvider.getInstance().getWSConfig();
			folderOutPutPath = wsCfg.getFileOutputPath();
		}
		return folderOutPutPath;
	}
	
	public static void deleteErrFiles(String userName){
		List<File> listOfFiles = PLMSearchUtil.getListOfFiles(userName, "err");
		if(listOfFiles!=null){
			for(File file : listOfFiles){
				FileUtils.deleteQuietly(file);
			}
		}
	}
}
