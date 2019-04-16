package com.plm.dataretrival.cfg;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class ConfigDataProvider {

	private static ConfigDataProvider configDataProvider = null;
	private WSConfig wsCfg = null;
	private WSErrorMessages errorMessages = null;
	private BeanFactory bfEndeca = null;
	
	private ConfigDataProvider() {
		bfEndeca = loadConfiguration("xsd/WSConfig.xml");
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
		BeanFactory beanFactory = new XmlBeanFactory(xmlResource);
		return beanFactory;
	}	
}
