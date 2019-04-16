package com.plm.dataretrival;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.plm.util.PLMUtil;

import plm.ws.mtom.paroleedata.ObjectFactory;
import plm.ws.mtom.paroleedata.ParoleeDataSet;

public class XMLFileWriter {

	private String FILE_PATH = "C:/tmp/";
	public static final String NEW_LINE_CHAR="\n";
	private static final Logger logger = Logger.getLogger(XMLFileWriter.class);
	
	public XMLFileWriter(String filePath) {
		FILE_PATH = filePath;
	}
	
	/**
	 * Writes the Parolee Record into the xml file.
	 * @param str
	 * @throws SQLException 
	 * @throws JAXBException 
	 * @throws FileNotFoundException 
	 */
	public void writeParoleeData(ParoleeDataSet paroleeDataSet) throws IOException {
	    try{        
			// Create java object and assign values to this object            
			JAXBContext jaxbContext = JAXBContext.newInstance("plm.ws.mtom.paroleedata");            
			ObjectFactory objFactory = new ObjectFactory();            

	        // Create instance of marshaller and write object to XML file            
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
			JAXBElement<ParoleeDataSet> jaxbParolee = objFactory.createParoleeDataSet(paroleeDataSet);
			FileOutputStream fo = new FileOutputStream(FILE_PATH);
			marshaller.marshal(jaxbParolee, fo);  
		    fo.close();
	    } catch(JAXBException jex){
	    	logger.error(PLMUtil.getStackTrace(jex));
	    	throw new IOException();
		} 
	}
	
}
