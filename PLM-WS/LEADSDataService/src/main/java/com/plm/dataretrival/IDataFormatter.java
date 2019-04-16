package com.plm.dataretrival;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface IDataFormatter {

	public void init() throws DataException;
	public void beginProcess();
	public void endProcess();
	public int formatDataSet( DataSet dataSet, Set<String> stCDCNumber, List<String> returnFields) throws IOException;
	public String getOutputFile();
	public String getUserFolderNameWithPath();
	public String getUserInProgressFileNameWithPath();
	public String getUserErrorFileNameWithPath();
	public String getUserZipFileNameWithPath();
	public String getUserXMLFileNameWithPath();
	public String getUserTempZipFileNameWithPath();
	public File getUserFolder();
	public File getUserInProgressFile();
	public File getUserErrorFile();
	public File getUserZipFile();
	public File getUserTempZipFile();
}
