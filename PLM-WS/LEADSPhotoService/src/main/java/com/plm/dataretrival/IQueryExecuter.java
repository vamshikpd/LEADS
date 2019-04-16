package com.plm.dataretrival;

public interface IQueryExecuter {

	public boolean isPaginationEnabled();
	public long getTotalRecords();
	public int getRecordsPerPage();
	public DataSet fetchData(SearchCriteriaInfo searchCriteria, long startIndex) throws DataException;
	
}
