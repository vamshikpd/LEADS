package com.singletone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.endeca.ui.constants.UI_Props;
import com.endeca.ui.export.PDFExportServlet;


public class ReadExcel extends GoogleMapModelImpl {
	private static ReadExcel readExcel = null;
	
	private static final Logger logger = Logger.getLogger(ReadExcel.class);

	private ReadExcel() {
	}

	public static ReadExcel getInstance() {
		if (null == readExcel) {
			readExcel = new ReadExcel();
		}
		return readExcel;
	}

	public Map<Integer,ArrayList<String>> getDataMap(double NELatitude,
			double NELongitude, double SWLatitude, double SWLongitude,ServletContext sc) throws IOException {		
		setBounds(NELatitude, NELongitude, SWLatitude, SWLongitude);
	
		if (null==ReadExcel.latlngMap) {			
			HashMap<Integer,ArrayList<String>> dataMap = new HashMap<Integer,ArrayList<String>>();
			FileInputStream fin = null;
			//try {
				//setBounds(NELatitude, NELongitude, SWLatitude, SWLongitude);
				//ClassPathResource xmlResource = new ClassPathResource(UI_Props.getInstance().getValue("DATA_FILE_PATH"));		
				String fileName = sc.getRealPath("/WEB-INF/"+UI_Props.getInstance().getValue("DATA_FILE_PATH"));			
				if(fileName == null) {
					URL resourceURL = sc.getResource("/WEB-INF/"+UI_Props.getInstance().getValue("DATA_FILE_PATH"));
					if(resourceURL != null) {
						fileName = resourceURL.getPath();
					}else{
						logger.error("School - ResourceURL is Null..");
					}
				}
				File file = new File(fileName);
				fin = new FileInputStream(file);
				//fin =  (FileInputStream)xmlResource.getURL().openStream();
				
				

				//fin = new FileInputStream(new File("E:/PLM/pubschls.xls"));
				POIFSFileSystem pFS = new POIFSFileSystem(fin);
				HSSFWorkbook wb = new HSSFWorkbook(pFS);// get the workbook from the input stream
				HSSFSheet sheet = wb.getSheetAt(0); // get the first sheet from workbook
				int numOfRows = sheet.getLastRowNum(); // gives total no. of rows in the sheet
				
				HSSFRow row = null;
				HSSFCell cellLat=null, cellLng=null, cellSchoolName=null, cellSchoolStreet=null, cellKey=null, cellStatus = null;
				HSSFCell cellSchoolDistrict=null, cellSchoolCity=null, cellSchoolZip=null, cellSchoolType=null, cellSchoolGrade=null;
				
				String strLat = null, strLng = null,strSchoolName=null,strSchoolStreet=null, strKey=null;
				String strDistrict = null, strSchoolCity = null,strSchoolZip=null,strSchoolType=null, strSchoolGrade=null;
				
				for (int i = 1; i <= numOfRows; i++) {
					row = sheet.getRow(i);
					cellKey = row.getCell(new Short("0").shortValue());
					cellStatus = row.getCell(new Short("1").shortValue());
					cellLat = row.getCell(new Short("28").shortValue());
					cellLng = row.getCell(new Short("29").shortValue());
					cellSchoolDistrict = row.getCell(new Short("3").shortValue());
					cellSchoolName = row.getCell(new Short("4").shortValue());
					cellSchoolStreet = row.getCell(new Short("5").shortValue());
					cellSchoolCity = row.getCell(new Short("6").shortValue());
					cellSchoolZip = row.getCell(new Short("7").shortValue());
					cellSchoolType = row.getCell(new Short("14").shortValue());
					cellSchoolGrade = row.getCell(new Short("35").shortValue());
					
					if(cellStatus !=null 
							&& cellStatus.getStringCellValue().trim().equalsIgnoreCase("closed")){
						continue;
					}else{
						if(cellStatus == null){
							continue;
						}
					}
					if(cellKey != null 
							&& cellKey.getStringCellValue().trim().equals("")) {
						cellKey = null;
					}
					if(cellLat != null && cellLat.getStringCellValue().trim().equals(".")) {
						cellLat = null;
					}
					if(cellLng != null && cellLng.getStringCellValue().trim().equals(".")) {
						cellLng = null;
					}
					//try {

						strKey = cellKey != null ? cellKey.getStringCellValue(): null;
						strLat = cellLat != null ? cellLat.getStringCellValue(): null;
						strLng = cellLng != null ? cellLng.getStringCellValue(): null;
						strDistrict = cellSchoolDistrict != null ? cellSchoolDistrict.getStringCellValue(): null;
						strSchoolName = cellSchoolName != null ? cellSchoolName.getStringCellValue(): null;
						strSchoolStreet = cellSchoolStreet != null ? cellSchoolStreet.getStringCellValue(): null;
						strSchoolCity = cellSchoolCity != null ? cellSchoolCity.getStringCellValue(): null;
						strSchoolZip = cellSchoolZip != null ? cellSchoolZip.getStringCellValue(): null;
						strSchoolType = cellSchoolType != null ? cellSchoolType.getStringCellValue(): null;
						strSchoolGrade = cellSchoolGrade != null ? cellSchoolGrade.getStringCellValue(): null;
						
						if (strLat != null && strLng != null && strSchoolName != null && strSchoolStreet != null) {
							ArrayList<String> schoolInfo = new ArrayList<String>();
							schoolInfo.add(strKey);
							schoolInfo.add(strLat);
							schoolInfo.add(strLng);
							schoolInfo.add(strDistrict);
							schoolInfo.add(strSchoolName);
							schoolInfo.add(strSchoolStreet);
							schoolInfo.add(strSchoolCity + ", CA " + strSchoolZip);
							schoolInfo.add(strSchoolType);
							schoolInfo.add(strSchoolGrade);
							dataMap.put(new Integer(i),schoolInfo);
						}

					//} catch (Exception e) {
					//	logger.error(e);
					//	continue;
					//}
					strLat = null;
					strLng = null;
				}
			//} catch (Exception e) {
			//	e.printStackTrace();
			//} finally {
			//	try {
			//		fin.close();
			//	} catch (Exception e) {
			//		logger.error(e.getMessage());
			//	}
			//}
			ReadExcel.latlngMap = dataMap;
		}		
		return getValuesInBounds();

	}

	
}
