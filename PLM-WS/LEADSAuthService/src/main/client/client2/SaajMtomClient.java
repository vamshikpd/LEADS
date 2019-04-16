/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package client2;

import org.springframework.util.ClassUtils;
import org.springframework.util.StopWatch;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import plm.ws.mtom.auth.ObjectFactory;

/**
 * Simple client that demonstartes MTOM by invoking <code>StoreImage</code> and <code>LoadImage</code> using a
 * WebServiceTemplate and SAAJ.
 *
 * @author Tareq Abed Rabbo
 * @author Arjen Poutsma
 */
public class SaajMtomClient extends WebServiceGatewaySupport {

    private StopWatch stopWatch = new StopWatch(ClassUtils.getShortName(getClass()));

    public SaajMtomClient(SaajSoapMessageFactory messageFactory) {
        super(messageFactory);
    }

    public void doIt(String path) {
		try {
	        load(path);
	        logger.info(stopWatch.prettyPrint());
	    }
	    catch (SoapFaultClientException ex) {
	        System.err.format("SOAP Fault Code    %1s%n", ex.getFaultCode());
	        System.err.format("SOAP Fault String: %1s%n", ex.getFaultStringOrReason());
	    }
    }

    private void load(String path) {
        logger.info("------Process Complete----------" );
    }
    
    
}
