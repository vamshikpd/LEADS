/**
 * Book.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.plm.ws;

import java.rmi.Remote;
import java.rmi.RemoteException;

import coldfusion.xml.rpc.CFCInvocationException;

public interface LeadsAuth extends Remote {
    public String echoString(String input) throws RemoteException, CFCInvocationException;
}
