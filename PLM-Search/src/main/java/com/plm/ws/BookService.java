/**
 * BookService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.plm.ws;

import java.net.URL;

import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;

public interface BookService extends Service {
    public String getBookCfcEndpointAddress();

    public LeadsAuth getBookCfc() throws ServiceException;

    public LeadsAuth getBookCfc(URL portAddress) throws ServiceException;
}
