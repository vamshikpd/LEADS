package com.plm.ws;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;

import org.apache.log4j.Logger;

import coldfusion.xml.rpc.CFCInvocationException;

import com.endeca.ui.constants.UI_Props;
import com.plm.constants.PLMConstants;


public class BookProxy implements LeadsAuth {
  private String _endpoint = null;
  private LeadsAuth book = null;
  private static final Logger logger = Logger.getLogger(BookProxy.class);

  public BookProxy() {
    _initBookProxy();
  }
  
  public BookProxy(String serverName) {
	    _initBookProxy(serverName);
  }

  private void _initBookProxy() {
	    try {
	    	BookServiceLocator bookServiceLocator = new BookServiceLocator();
	    	String portName = UI_Props.getInstance().getValue(PLMConstants.BOOKCFC_WSD_SERVICENAME_LABEL);
	    	String address = UI_Props.getInstance().getValue(PLMConstants.BOOKCFC_ENDPOINT_ADDRESS_LABEL);
	    	
	    	bookServiceLocator.setEndpointAddress(portName, address);
	    	book = bookServiceLocator.getBookCfc();
	    	if (book != null) {
				_endpoint = (String)((Stub)book)._getProperty("javax.xml.rpc.service.endpoint.address");
	    	}
	      
	    }
	    catch (ServiceException serviceException) {}
  }

  private void _initBookProxy(String serverName) {
    try {
    	
    	BookServiceLocator bookServiceLocator = new BookServiceLocator();
    	String portName = UI_Props.getInstance().getValue(PLMConstants.BOOKCFC_WSD_SERVICENAME_LABEL);
    	String address = UI_Props.getInstance().getValue(PLMConstants.BOOKCFC_ENDPOINT_ADDRESS_LABEL);
    	String preAddress="";
    	String postAddress="";
    	String fullAddress = "";
    	preAddress = address.substring(0, address.indexOf("$"));
    	postAddress = address.substring(address.indexOf("}")+1);
    	fullAddress = preAddress + serverName + postAddress;
    	
    	bookServiceLocator.setEndpointAddress(portName, fullAddress);
    	book = bookServiceLocator.getBookCfc();
    	if (book != null) {
			_endpoint = (String)((Stub)book)._getProperty("javax.xml.rpc.service.endpoint.address");
    	}
      
    }
    catch (ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (book != null)
      ((Stub)book)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public LeadsAuth getBook() {
    if (book == null)
      _initBookProxy();
    return book;
  }
  
  public String echoString(String input) throws RemoteException, CFCInvocationException{
    if (book == null)
      _initBookProxy();
    return book.echoString(input);
  }
  
  
}