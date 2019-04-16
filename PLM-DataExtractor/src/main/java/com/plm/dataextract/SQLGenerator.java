package com.plm.dataextract;

import java.util.List;

import com.plm.dataextract.cfg.FieldConfig;
import com.plm.dataextract.cfg.FileDataConfig;

public class SQLGenerator {
	
	private static SQLGenerator sqlGenerator = null;

	/**
	 * Private constructor for SQLGenerator.
	 */
	private SQLGenerator() {
	}

	/**
	 * This returns the instance of SQLGenerator.
	 * @return instance of SQLGenerator
	 */
	public static SQLGenerator getInstance() {
		if(sqlGenerator == null) {
			sqlGenerator = new SQLGenerator();
		}
		return sqlGenerator;
	}
	
	/**
	 * Returns generates the SQL Query String from the given FileDataConfig object and returns it.
	 * @param dataConfig - FileDataConfig
	 * @return String - SQL.
	 */
	public String generateSQL(FileDataConfig dataConfig,String sExtractMode) {
		System.out.println("Extract Mode : " + sExtractMode);
		StringBuffer sbSQL = new StringBuffer();
		sbSQL.append("SELECT " + getColumnList(dataConfig) +" FROM " + getTableList(dataConfig) );
		String condition = getCondition(dataConfig, sExtractMode);
		String orderby = getOrderBy(dataConfig);
		if( condition != null && condition.trim().length() > 0 ) {
			sbSQL.append(" WHERE "+ condition);
		}
		if(orderby!=null && orderby.trim().length()>0){
			sbSQL.append(" ORDER BY "+ orderby);
		}
		return sbSQL.toString();
	}
	
	private String getColumnList(FileDataConfig dataConfig) {
		StringBuffer sbCol = new StringBuffer();
		List<FieldConfig> fieldsConfig = dataConfig.getFields();
		if(fieldsConfig != null && fieldsConfig.size() > 0) {
			for(FieldConfig fConfig : fieldsConfig) {
				sbCol.append(", "+fConfig.getFieldName());
			}
			return sbCol.toString().substring(2);
		}
		return "";
	}

	private String getTableList(FileDataConfig dataConfig) {
		String tableName = dataConfig.getTable();
		return tableName;
	}

	private String getCondition(FileDataConfig dataConfig,String sExtractMode) {
		String condition = null;
		if(sExtractMode != null && sExtractMode.equals("FULL"))
			condition = dataConfig.getFullcondition();
		else 
			condition = dataConfig.getPartialcondition();

		return condition;
	}	
	
	private String getOrderBy(FileDataConfig dataConfig){
		String orderby = dataConfig.getOrderby();
		return orderby;
	}
	
}
