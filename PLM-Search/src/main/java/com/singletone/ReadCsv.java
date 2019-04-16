package com.singletone;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletContext;


import org.apache.log4j.Logger;

import com.endeca.ui.export.PDFExportServlet;



public class ReadCsv extends GoogleMapModelImpl{
	
	private static final Logger logger = Logger.getLogger(ReadCsv.class);
	
	private static ReadCsv readCsv = null;
	private ReadCsv(){
		
	}
	public static ReadCsv getInstance(){
		if(null==readCsv) 
			 readCsv = new ReadCsv();
			return readCsv;
	}
	
	@Override
	public Map<Integer, ArrayList<String>> getDataMap(double NELatitude, double NELongitude, double SWLatitude, double SWLongitude,ServletContext sc) {
		return null;
	}
	
}