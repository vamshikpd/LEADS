/**
 * BookServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.plm.ws;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Service;
import org.apache.axis.client.Stub;

public class BookServiceLocator extends Service implements BookService {

	private static final long serialVersionUID = -6242299123437404418L;

	public BookServiceLocator() {
    }


    public BookServiceLocator(EngineConfiguration config) {
        super(config);
    }

    public BookServiceLocator(String wsdlLoc, QName sName) throws ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for BookCfc
    private String bookCfcEndpointAddress = null;
    // The WSDD service name defaults to the port name.
    private String bookCfcWSDDServiceName = null;

    public String getBookCfcEndpointAddress() {
        return bookCfcEndpointAddress;
    }

    public void setBookCfcEndpointAddress(String address) {
    	bookCfcEndpointAddress = address;
    }


    public String getBookCfcWSDDServiceName() {
        return bookCfcWSDDServiceName;
    }

    public void setBookCfcWSDDServiceName(String name) {
    	bookCfcWSDDServiceName = name;
    }

    public LeadsAuth getBookCfc() throws ServiceException {
        URL endpoint;
         try {
             endpoint = new URL(bookCfcEndpointAddress);
         }
         catch (MalformedURLException e) {
             throw new ServiceException(e);
         }
         return getBookCfc(endpoint);
     }

     public LeadsAuth getBookCfc(URL portAddress) throws ServiceException {
         try {
             BookCfcSoapBindingStub _stub = new BookCfcSoapBindingStub(portAddress, this);
             _stub.setPortName(getBookCfcWSDDServiceName());
             return _stub;
         }
         catch (AxisFault e) {
             return null;
         }
     }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public Remote getPort(Class serviceEndpointInterface) throws ServiceException {
        try {
            if (LeadsAuth.class.isAssignableFrom(serviceEndpointInterface)) {
                BookCfcSoapBindingStub _stub = new BookCfcSoapBindingStub(new URL(bookCfcEndpointAddress), this);
                _stub.setPortName(getBookCfcWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new ServiceException(t);
        }
        throw new ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(QName portName, Class serviceEndpointInterface) throws ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("book.cfc".equals(inputPortName)) {
            return getBookCfc();
        }
        else  {
            Remote _stub = getPort(serviceEndpointInterface);
            ((Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public QName getServiceName() {
        return new QName("http://ws", "BookService");
    }

    private HashSet<QName> ports = null;

    public Iterator<QName> getPorts() {
        if (ports == null) {
            ports = new HashSet<QName>();
            ports.add(new QName("http://ws", "book.cfc"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address){
        
    	setBookCfcWSDDServiceName(portName);
    	setBookCfcEndpointAddress(address);
    }

}
