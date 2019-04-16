package com.plm.ws.interceptors;

import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.CommonsHttpConnection;
import org.springframework.ws.transport.http.HttpUrlConnection;

public class AddHttpHeaderInterceptor implements ClientInterceptor {

    public boolean handleFault(MessageContext messageContext)
            throws WebServiceClientException {
        return true;
    }

    public boolean handleRequest(MessageContext messageContext)
            throws WebServiceClientException {
        TransportContext context = TransportContextHolder.getTransportContext();
//        CommonsHttpConnection connection = (CommonsHttpConnection) context.getConnection();
//        PostMethod postMethod = connection.getPostMethod();
//        postMethod.addRequestHeader( "LEADS-WS-REQUESTKEY", "123456" );
        HttpUrlConnection connection = (HttpUrlConnection)context.getConnection();
        connection.getConnection().addRequestProperty( "LEADS-WS-REQUESTKEY", "123456" );
        return true;
    }

    public boolean handleResponse(MessageContext messageContext)
            throws WebServiceClientException {
        return true;
    }

}
