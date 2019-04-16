package com.plm.dataretrival;

import java.util.Collection;

import com.endeca.navigation.PropertyContainer;

public class DataSet {
	
	private int totalRecords = 0;
	private Collection<PropertyContainer> endecaDataCollection = null;
	//private ResultSet rsData = null;

	/**
	 * @return the data
	 */
	public Collection<PropertyContainer> getEndecaData() {
		return endecaDataCollection;
	}

	/**
	 * @param data the data to set
	 */
	public void setEndecaData(Collection<PropertyContainer> data) {
		if(data != null) {
			totalRecords = data.size();
		}
		this.endecaDataCollection = data;
	}
	
	/**
	 * Returns the no of records in DataSet
	 * @return
	 */
	public int getRecordSize() {
		return totalRecords;
	}
}
