package com.plm.dataextract.cfg;

import java.net.URL;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class ConfigDataProvider {

	private static ConfigDataProvider configDataProvider = null;
	private DataExtractConfig dataExtCfg = null;
	private DBConfig dbConfig = null;
	//private BeanFactory bfDataExtract = null;
	//private BeanFactory bfDatabase = null;
	private ApplicationContext dataExtractContext, dbContext = null;
	
	private ConfigDataProvider() {
		//bfDataExtract = loadConfiguration("dataExtractConfig.xml");
		dataExtractContext = loadConfigurationContext("dataExtractConfig.xml");
		//bfDatabase = loadConfiguration("dbConfig.xml");
		dbContext = loadConfigurationContext("dbConfig.xml");
	}
	
	/**
	 * This returns the instance of ConfigData.
	 * @return ConfigData instance.
	 */
	public static ConfigDataProvider getInstance() {
		if(configDataProvider == null) {
			configDataProvider = new ConfigDataProvider();
		}
		return configDataProvider;
	}

	public DataExtractConfig getDataExtractConfig() {
		if(dataExtCfg == null) {
			//dataExtCfg = (DataExtractConfig)bfDataExtract.getBean("dataExtractor");
			dataExtCfg = (DataExtractConfig)dataExtractContext.getBean("dataExtractor");
		}
		return dataExtCfg;
	}
	
	public DBConfig getDbConfig() {
		if(dbConfig == null) {
			//dataExtCfg = (DataExtractConfig)bfDataExtract.getBean("dataExtractor");
			dbConfig = (DBConfig)dbContext.getBean("calparoleDB");
		}
		return dbConfig;
	}
	
	/**
	 * 
	 * @param xmlResourceFileName
	 * @return
	 */
	private BeanFactory loadConfiguration(String xmlResourceFileName) {
		URL url = getClass().getClassLoader().getResource(xmlResourceFileName);
		Resource xmlResource = new FileSystemResource(url.getPath());
		BeanFactory beanFactory = new XmlBeanFactory(xmlResource);
		return beanFactory;
	}	
	/**
	 * 
	 * @param xmlResourceFileName
	 * @return
	 */
	private ApplicationContext loadConfigurationContext(String xmlResourceFileName) {
		ApplicationContext context = new ClassPathXmlApplicationContext(xmlResourceFileName);
		return context;
	}	
}
