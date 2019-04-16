package com.Implfactory;

import com.singletone.GoogleMapModelImpl;
import com.singletone.ReadCsv;
import com.singletone.ReadExcel;

public class ImplementationFactory {
	String objectType;
	public ImplementationFactory(String objectType) {
		this.objectType = objectType;
	}
	public GoogleMapModelImpl getGoogleMapImplInstance(){
		GoogleMapModelImpl googleMapModelImpl=null;
		if("xls".equals(objectType)){
			googleMapModelImpl = ReadExcel.getInstance();
		}else if("csv".equalsIgnoreCase(objectType)){
			googleMapModelImpl = ReadCsv.getInstance();
		}
		 return googleMapModelImpl;
	}
}
