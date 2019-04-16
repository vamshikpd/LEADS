package com.plm.dataretrival.cfg;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


public class ConfigDataProvider {

	private static ConfigDataProvider configDataProvider = null;
	private static final Logger logger = Logger.getLogger(ConfigDataProvider.class);

	private WSConfig wsCfg = null;
	private WSErrorMessages errorMessages = null;
	private BeanFactory bfEndeca;
	//private BeanFactory bfDatabase = null;
	
	private ConfigDataProvider() {
		bfEndeca = loadConfiguration("xsd/WSConfig.xml");
		logger.debug("Loading WSConfig .xml file");
		//bfDatabase = loadConfiguration("xsd/dbConfig.xml");
		//bfDataExtract = loadConfiguration("NiemElementMappingConfig.xml");
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

	public WSConfig getWSConfig() {
		if(wsCfg == null) {
			wsCfg = (WSConfig)bfEndeca.getBean("wsCfg");
		}
		return wsCfg;
	}
	
	/**
	 * @return the errorMessages
	 */
	public WSErrorMessages getErrorMessages() {
		if(errorMessages == null) {
			errorMessages = (WSErrorMessages)bfEndeca.getBean("wsErrMessages");
		}
		return errorMessages;
	}
	/**
	 * @param errorMessages the errorMessages to set
	 */
	public void setErrorMessages(WSErrorMessages errorMessages) {
		this.errorMessages = errorMessages;
	}	
	
	/**
	 * Returns the BeanFactory for the given resource file.
	 * @param xmlResourceFileName
	 * @return BeanFactory
	 */
	private BeanFactory loadConfiguration(String xmlResourceFileName) {
		Resource xmlResource = new ClassPathResource(xmlResourceFileName);
		return new XmlBeanFactory(xmlResource);
	}	
}
