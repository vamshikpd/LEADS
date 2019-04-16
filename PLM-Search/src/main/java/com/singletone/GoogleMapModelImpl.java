package com.singletone;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;

public abstract class GoogleMapModelImpl {
	abstract public Map<Integer,ArrayList<String>> getDataMap(double NELatitude, double NELongitude, double SWLatitude, double SWLongitude,ServletContext sc) throws MalformedURLException, FileNotFoundException, IOException;
	//abstract public void setBounds(double NELatitude, double NELongitude, double SWLatitude, double SWLongitude,);
	protected static Map<Integer, ArrayList<String>> latlngMap = null;
	
	protected double NELatitude;

	protected double NELongitude;

	protected double SWLatitude;

	protected double SWLongitude;
	
	protected Map<Integer, ArrayList<String>> getValuesInBounds() {		
		Map<Integer, ArrayList<String>> inBoundsMap = new HashMap<Integer, ArrayList<String>>();
		Iterator<Integer> itLat = latlngMap.keySet().iterator();
		Double value = 0.0;
		while (itLat.hasNext()) {
			Integer key = itLat.next();
			ArrayList<String> values = (ArrayList<String>)latlngMap.get(key);			
			try{
				double dLat = Double.parseDouble(values.get(1));
				double dLong = Double.parseDouble(values.get(2));
				if (dLat <= NELatitude && dLat >= SWLatitude) {
					if (dLong >= SWLongitude && dLong <= NELongitude) {
						inBoundsMap.put(key, values);
					}
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		return inBoundsMap;
	}
	protected void setBounds(double NELatitude, double NELongitude,
			double SWLatitude, double SWLongitude) {		
		this.NELatitude = NELatitude;
		this.NELongitude = NELongitude;
		this.SWLatitude = SWLatitude;
		this.SWLongitude = SWLongitude;
	}
}
