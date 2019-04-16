package com.plm;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.endeca.ui.constants.UI_Props;


public class PLMConnection {
	private static PLMConnection plmConnection = new PLMConnection();
	private static final Logger logger = Logger.getLogger(PLMConnection.class);
	private DataSource datasource = null;
	private DataSource lmsDatasource = null;

	/**
	 * Default private constructor
	 */
	private PLMConnection() {
	}

	/**
	 * Return Instance of PLMConnection class
	 * 
	 * @return
	 */
	public static PLMConnection getInstance() {
		return plmConnection;
	}

	/**
	 * 
	 */
	private void setConnection() {
	}

	/**
	 * This method returns the connection object
	 * 
	 * @return Connection
	 */
	public DataSource getDataSource() {
		if (datasource == null) {
			ClassPathResource xmlResource = new ClassPathResource(UI_Props
					.getInstance().getValue("DATABASE_CONFIG_FILE_PATH"));
			BeanFactory factory = new XmlBeanFactory(xmlResource);
			datasource = (DataSource) factory.getBean("dataSource");
			logger.info("DataSource initialized...");
		}
		return datasource;
	}
	
	public DataSource getLMSDataSource() {
		if (lmsDatasource == null) {
			ClassPathResource xmlResource = new ClassPathResource(UI_Props
					.getInstance().getValue("DATABASE_CONFIG_FILE_PATH"));
			BeanFactory factory = new XmlBeanFactory(xmlResource);
			lmsDatasource = (DataSource) factory.getBean("lmsDataSource");
			logger.info("LMSDataSource initialized...");
		}
		return lmsDatasource;
	}

}
