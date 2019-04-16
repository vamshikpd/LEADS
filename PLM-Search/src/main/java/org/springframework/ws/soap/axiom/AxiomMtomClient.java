package org.springframework.ws.soap.axiom;

//  package client2;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import com.plm.ws.interceptors.AddHttpHeaderInterceptor;
import org.apache.axiom.om.*;
import org.apache.axiom.soap.*;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11BodyImpl;
import org.apache.log4j.Logger;
import org.springframework.util.ClassUtils;
import org.springframework.util.StopWatch;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.client.SoapFaultClientException;


/**
 * Simple client that demonstartes MTOM by invoking <code>StoreImage</code> and
 * <code>LoadImage</code> using a WebServiceTemplate and Axiom.
 * 
 * @author Tushar
 */
public class AxiomMtomClient extends WebServiceGatewaySupport {
	
	private static final Logger logger = Logger.getLogger(AxiomMtomClient.class);

	public static final String OCTET_TYPE = "application/octet-stream";
	public static final String DATA_NAME_SPACE = "http://www.plm/ws/mtom/data";
	public static final String PHOTO_NAME_SPACE = "http://www.plm/ws/mtom/photo";
	public static final String ELE_PAROLEE_DATA_REQUEST = "InternalParoleeDataRequest";
	public static final String ELE_PAROLEE_PHOTO_REQUEST = "InternalParoleePhotoRequest";
	public static final String ELE_CASE_NUMBER = "CaseNumber";
	public static final String ELE_REASON = "Reason";
	public static final String ELE_DATA_TYPE = "DataType";
	public static final String ELE_SEARCH_CRITERIA = "SearchCriteria";
	public static final String ELE_LAST_UPDATE_DATE = "LastUpdateDate";
	public static final String ELE_CITY = "City";
	public static final String ELE_COUNTY = "County";
	public static final String ELE_GROUP_CODE = "GroupCode";
	public static final String ELE_INCLUDE_STATEWIDE_PAL = "IncludeStateWidePAL";
	public static final String ELE_INC_ATTACH = "IncludeAttachment";
	public static final String ELE_USER_NAME = "Username";
	public static final String ATT_FORMAT = "format";
	public static final String OUTPUT_PATH = "D:\\TD\\";
	public static final String SERVICE_TYPE_DATA = "Data";
	public static final String SERVICE_TYPE_PHOTO = "Photo";
	
	public String serviceType = "";
	
	private StopWatch stopWatch = new StopWatch(ClassUtils.getShortName(getClass()));

	private static final AddHttpHeaderInterceptor ADD_HTTP_HEADER_INTERCEPTOR = new AddHttpHeaderInterceptor();

	public AxiomMtomClient(AxiomSoapMessageFactory messageFactory) {
		super(messageFactory);
	}

	public AxiomMtomClient(AxiomSoapMessageFactory messageFactory, String serviceType) {
		super(messageFactory);
		this.serviceType = serviceType;

		ClientInterceptor[] interceptors = new ClientInterceptor[]{ADD_HTTP_HEADER_INTERCEPTOR};
		this.getWebServiceTemplate().setInterceptors(interceptors);
	}

	
	public SOAPBody doIt(String sLastUpdateDate, String sGroupCode, String userName, String counties, String caseNumber, String reason, String dataType, String includeStateWidePAL) {
		SOAPBody body = null;
		try {
			body = load(sLastUpdateDate, sGroupCode, userName, counties, caseNumber, reason, dataType, includeStateWidePAL);
		} catch (SoapFaultClientException ex) {
			System.err.format("SOAP Fault Code    %1s%n", ex.getFaultCode());
			System.err.format("SOAP Fault String: %1s%n", ex.getFaultStringOrReason());
		}
		return body;
	}

	private SOAPBody load(final String sLastUpdateDate, final String sGroupCode, final String userName, final String counties, final String caseNumber, final String sReason, final String dataType, final String includeStateWidePAL) {

		stopWatch.start("load");
		WebServiceMessageCallback callBack = new WebServiceMessageCallback() {
			public void doWithMessage(WebServiceMessage message)
					throws IOException, TransformerException {

				SOAPMessage axiomMessage = ((AxiomSoapMessage) message).getAxiomMessage();
				SOAPFactory factory = (SOAPFactory) axiomMessage.getOMFactory();

				// add wss security username and password token to SOAP header
                //=====================================================
                //addSecurityToHeader(header);
                /*SOAPHeader header = axiomMessage.getSOAPEnvelope().getHeader();

                logger.debug("Set the WS-security header values");
                String WS_SEC_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
                //SOAPFactory soapFact = OMAbstractFactory.getSOAP12Factory();
                OMNamespace ns = factory.createOMNamespace(WS_SEC_NS, "wsse");


                SOAPHeaderBlock wssHeader = header.addHeaderBlock("Security", ns);

                //SOAPHeaderBlock wssHeader = factory.createSOAPHeaderBlock("Security", ns);
                OMElement usernameToken = factory.createOMElement("UsernameToken", ns);
                OMElement username = factory.createOMElement("Username", ns);
                OMElement password = factory.createOMElement("Password", ns);
                username.setText("leads");
                password.setText("Password1");
                usernameToken.addChild(username);
                usernameToken.addChild(password);
                wssHeader.addChild(usernameToken);

                logger.debug(String.valueOf(wssHeader));
                logger.debug(String.valueOf(usernameToken));*/
                //=====================================================


				SOAPBody body = axiomMessage.getSOAPEnvelope().getBody();


				OMNamespace ns = null;
				if(serviceType.equalsIgnoreCase(SERVICE_TYPE_PHOTO)){
					ns = factory.createOMNamespace(PHOTO_NAME_SPACE, "sch");
				}else{
					ns = factory.createOMNamespace(DATA_NAME_SPACE, "sch");
				}

				// Create ParoleeDataRequest
				OMElement paroleeReq = null;
				if(serviceType.equalsIgnoreCase(SERVICE_TYPE_PHOTO)){
					paroleeReq =factory.createOMElement(ELE_PAROLEE_PHOTO_REQUEST, ns);
				}else{
					paroleeReq =factory.createOMElement(ELE_PAROLEE_DATA_REQUEST, ns);
				}
				
				// Create Case Number
				OMElement caseNumber_ = factory.createOMElement(ELE_CASE_NUMBER, ns);
				caseNumber_.setText(caseNumber);
				paroleeReq.addChild(caseNumber_);

				// Create Reason
				OMElement reason = factory.createOMElement(ELE_REASON, ns);
				reason.setText(sReason);
				paroleeReq.addChild(reason);

				// Create user Name
				OMElement omUserName = factory.createOMElement(ELE_USER_NAME,ns);
				omUserName.setText(userName);
				paroleeReq.addChild(omUserName);

				// Create Include Attachment
				OMElement omIncAttach = factory.createOMElement(ELE_INC_ATTACH,ns);
				omIncAttach.setText("N");
				paroleeReq.addChild(omIncAttach);

				// Create SearchCriteria
				OMElement searchCriteria = factory.createOMElement(ELE_SEARCH_CRITERIA, ns);

				// Create LastUpdateDate
				OMElement lastUpdateDate = factory.createOMElement(ELE_LAST_UPDATE_DATE, ns);
				lastUpdateDate.setText(sLastUpdateDate);
				searchCriteria.addChild(lastUpdateDate);

				/*// Create group code
				OMElement groupCode = factory.createOMElement(ELE_GROUP_CODE,ns);
				groupCode.setText(sGroupCode);
				searchCriteria.addChild(groupCode);*/
				
				// Create Counties
				if(counties != null && counties.contains(",")){					
					for(String s:counties.split(",")){
						if(s!=null && s.trim().length()>0){
							OMElement county = factory.createOMElement(ELE_COUNTY,ns);
							county.setText(s);
							searchCriteria.addChild(county);
						}
					}
				} else if(counties != null){
					OMElement county = factory.createOMElement(ELE_COUNTY,ns);
					county.setText(counties);
					searchCriteria.addChild(county);
				}
				
				//Create IncludeStateWidePAL
				OMElement includeStateWidePAL_ = factory.createOMElement(ELE_INCLUDE_STATEWIDE_PAL, ns);
				includeStateWidePAL_.setText(includeStateWidePAL);
				paroleeReq.addChild(includeStateWidePAL_);
				
				paroleeReq.addChild(searchCriteria);

				body.addChild(paroleeReq);

			}
		};

		WebServiceMessageExtractor msgExtractor = new WebServiceMessageExtractor() {
			@SuppressWarnings("unchecked")
			public Object extractData(WebServiceMessage message)
					throws IOException, TransformerException {
				AxiomSoapMessage axiomMessage = (AxiomSoapMessage) message;

				SOAPMessage soapMsg = axiomMessage.getAxiomMessage();
				SOAPBody body = soapMsg.getSOAPEnvelope().getBody();

				return body;
			}
		};

		Object image = getWebServiceTemplate().sendAndReceive(callBack, msgExtractor);
		SOAP11BodyImpl soap11 = (SOAP11BodyImpl) image;
		stopWatch.stop();
		logger.debug("DataDownload timings: Elapsed " + stopWatch.getTotalTimeSeconds() + " seconds.");
		return soap11;

	}

	/*private void addSecurityToHeader(
			SOAPHeader header) {

		OMFactory factory = OMAbstractFactory.getOMFactory();

		OMNamespace namespaceWSSE = factory
				.createOMNamespace(
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
						"wsse");

		OMElement element = factory.createOMElement("Security", namespaceWSSE);

		OMAttribute attribute = factory.createOMAttribute("mustUnderstand",
				null, "1");

		element.addAttribute(attribute);

		header.addChild(element);

		OMElement element2 = factory.createOMElement("UsernameToken",
				namespaceWSSE);

		OMNamespace namespaceWSU = factory
				.createOMNamespace(
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd",
						"wsu");

		attribute = factory.createOMAttribute("Id", namespaceWSU,
				"UsernameToken-1");

		element2.addAttribute(attribute);

		element.addChild(element2);

		OMElement element3 = factory.createOMElement("Username", namespaceWSSE);

		element3.setText("leads");

		OMElement element4 = factory.createOMElement("Password", namespaceWSSE);

		attribute = factory
				.createOMAttribute(
						"Type",
						null,
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");

		element4.setText("Password1");

		element2.addChild(element3);
		element2.addChild(element4);
	}
	*/
}


/*
public class WSSESecurityHeaderRequestWebServiceMessageCallback implements WebServiceMessageCallback {

    public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {

        try {


            SOAPMessage axiomMessage = ((AxiomSoapMessage) message).getAxiomMessage();
            SOAPFactory factory = (SOAPFactory) axiomMessage.getOMFactory();

            // add wss security username and password token to SOAP header
            SOAPHeader header = axiomMessage.getSOAPEnvelope().getHeader();
            header.addHeaderBlock()


            Name
                    headerElementName =
                    axiomMessage.getSOAPEnvelope().createName(
                            "Security",
                            "wsse",
                            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"
                    );

            // Add "Security" soapHeaderElement to soapHeader
            SOAPHeaderElement
                    soapHeaderElement =
                    header.addHeaderBlock(headerElementName);

            // This may be important for some portals!
            soapHeaderElement.setActor(null);

            // Add usernameToken to "Security" soapHeaderElement
            SOAPElement
                    usernameTokenSOAPElement =
                    soapHeaderElement.addChildElement("UsernameToken");

            // Add username to usernameToken
            SOAPElement
                    userNameSOAPElement =
                    usernameTokenSOAPElement.addChildElement("Username");

            userNameSOAPElement.addTextNode("myUserName");

            // Add password to usernameToken
            SOAPElement
                    passwordSOAPElement =
                    usernameTokenSOAPElement.addChildElement("Password");

            passwordSOAPElement.addTextNode("myPassword");

        } catch (SOAPException soapException) {
            throw new RuntimeException("WSSESecurityHeaderRequestWebServiceMessageCallback", soapException);
        }
    }
}
*/

