package com.plm.dataextract.cfg;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class DBConfig {

	private DataSourceTransactionManager dsTxnManager = null;

	public DataSourceTransactionManager getDsTxnManager() {
		return dsTxnManager;
	}

	public void setDsTxnManager(DataSourceTransactionManager dsTxnManager) {
		this.dsTxnManager = dsTxnManager;
	}

	//--- [Start] Utility Methods ---
	public DataSource getDataSource() {
		if(dsTxnManager != null) {
			return dsTxnManager.getDataSource();
		}
		return null;
	}
	
	public Connection getConnection() {
		if(dsTxnManager != null) {
		    Connection conn = DataSourceUtils.getConnection(dsTxnManager.getDataSource());
			return conn;
		}
		return null;
	}	
	//--- [ End ] Utility Methods ---
}
