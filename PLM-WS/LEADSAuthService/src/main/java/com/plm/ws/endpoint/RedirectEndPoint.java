package com.plm.ws.endpoint;

/*
  C H A N G E    H I S T O R Y
 ================================================================================================================+
 DATE       | REASON        | AUTHOR        | COMMENTS                                                           |
 ================================================================================================================+
  5/21/2018 | ALM #13252    | Emil          | Modified class to add extra DB logging for WS requests             |
 ----------------------------------------------------------------------------------------------------------------|
  6/05/2018 | Refactoring   | Emil          | Made username inline method variable b/c of multithreading issue   |
 ----------------------------------------------------------------------------------------------------------------+
 */

import java.sql.Timestamp;
import java.util.Iterator;
import javax.activation.DataHandler;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.plm.util.WSAuditor;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ws.client.WebServiceTransportException;
import org.springframework.ws.mime.Attachment;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.axiom.AxiomMtomClient;
import org.springframework.ws.soap.axiom.AxiomSoapMessage;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpServletConnection;
import plm.ws.mtom.auth.DataAttachment;
import plm.ws.mtom.auth.Error;
import plm.ws.mtom.auth.ObjectFactory;
import plm.ws.mtom.auth.ParoleeInfoRequest;
import plm.ws.mtom.auth.ParoleeInfoResponse;
import plm.ws.mtom.auth.SearchCriteria;

import com.plm.util.PLMConstants;
import com.plm.util.PLMUtil;
//import com.plm.ws.idcs.service.authentication.IDCSAuthenticationManager;

@Endpoint
public class RedirectEndPoint {
    private static final Logger logger = Logger.getLogger(RedirectEndPoint.class);
    private static final ObjectFactory objectFactory = new ObjectFactory();
    private static final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
    private static final AxiomMtomClient axiomDataClient = (AxiomMtomClient) applicationContext.getBean("axiomDataClient");
    private static final AxiomMtomClient axiomPhotoClient = (AxiomMtomClient) applicationContext.getBean("axiomPhotoClient");
    private static final AxiomMtomClient axiomDataCheckClient = (AxiomMtomClient) applicationContext.getBean("axiomDataCheckClient");

    private static final String namespaceEnv = "http://schemas.xmlsoap.org/soap/envelope/";
    private static final String namespaceDataPLM = "http://www.plm/ws/mtom/data";
    private static final String namespacePhotoPLM = "http://www.plm/ws/mtom/photo";
    private static final String namespaceDataCheckPLM = "http://www.plm/ws/mtom/datacheck";
    private static final String prefixEnv = "env";
    private static final String prefixNS2 = "ns2";

    public RedirectEndPoint() {
//        this.objectFactory = new ObjectFactory();
    }

    @PayloadRoot(localPart = "ParoleeInfoRequest", namespace = "http://www.plm/ws/mtom/auth")
    @ResponsePayload
    public JAXBElement<ParoleeInfoResponse> returnParoleeInfo(@RequestPayload JAXBElement<ParoleeInfoRequest> reqElement) {

        if (MDC.get("id") == null) {
            MDC.put("id", System.currentTimeMillis());
        }

        try {
            return generateResponse(reqElement);
        } finally {
            MDC.clear();
        }

    }

    private JAXBElement<ParoleeInfoResponse> generateResponse(JAXBElement<ParoleeInfoRequest> reqElement) {

        // get HTTP request
        TransportContext context = TransportContextHolder.getTransportContext();
        HttpServletConnection httpServletConnection = (HttpServletConnection) context.getConnection();
        HttpServletRequest httpServletRequest = httpServletConnection.getHttpServletRequest();

        // retrieve username from security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); //get logged in username

        // IP
        String ipAddress = httpServletRequest.getHeader("X-FORWARDED-FOR") != null
                ? httpServletRequest.getHeader("X-FORWARDED-FOR")
                : httpServletRequest.getRemoteAddr();

        // request timestamp
        Timestamp ts = new Timestamp(System.currentTimeMillis());


        ParoleeInfoResponse pdResponse = new ParoleeInfoResponse();
        if (reqElement == null) {
            Error err = new Error();
            err.setErrorCode(PLMConstants.ERR_REQUEST_NULL_FOUND);
            err.setErrorMessage(PLMConstants.MSG_REQUEST_NULL_FOUND);
            pdResponse.setTxnStatus(PLMConstants.RESPONSE_STATUS_ERROR);
            pdResponse.setError(err);

            auditError(ts, username, ipAddress, null, "", pdResponse);

            return objectFactory.createParoleeInfoResponse(pdResponse);
        }

//		username = LDAPAuthenticationManager.getUserToken().getPrincipal().toString();
//		username = IDCSAuthenticationManager.getUserToken().getPrincipal().toString();

        ParoleeInfoRequest pdReq = reqElement.getValue();

        if (!validateRequest(pdReq)) {
            Error err = new Error();
            err.setErrorCode(PLMConstants.ERR_NO_CRITERIA_FOUND);
            err.setErrorMessage(PLMConstants.MSG_NO_CRITERIA_FOUND);
            pdResponse.setTxnStatus(PLMConstants.RESPONSE_STATUS_ERROR);
            pdResponse.setError(err);

            auditError(ts, username, ipAddress, pdReq.getServiceRequestType(), "", pdResponse);

            return objectFactory.createParoleeInfoResponse(pdResponse);
        }

        // get WS request parameters from SOAP message
        String wsRequestParams = getRequestDetails(reqElement, username);

        if (pdReq.getServiceRequestType().equalsIgnoreCase("datacheck")) {
            pdResponse = getResponse(axiomDataCheckClient, namespaceDataCheckPLM, pdReq, pdResponse, "datacheck", username);
            if (pdResponse.getTxnStatus() != null
                    && pdResponse.getTxnStatus().equals(PLMConstants.RESPONSE_STATUS_ERROR)) {

                auditError(ts, username, ipAddress, "datacheck", wsRequestParams, pdResponse);

                return objectFactory.createParoleeInfoResponse(pdResponse);
            }
        } else if (pdReq.getServiceRequestType().equalsIgnoreCase("data")) {
            pdResponse = getResponse(axiomDataClient, namespaceDataPLM, pdReq, pdResponse, "data", username);
            if (pdResponse.getTxnStatus() != null
                    && pdResponse.getTxnStatus().equals(PLMConstants.RESPONSE_STATUS_ERROR)) {

                auditError(ts, username, ipAddress, "data", wsRequestParams, pdResponse);

                return objectFactory.createParoleeInfoResponse(pdResponse);
            }
        } else if (pdReq.getServiceRequestType().equalsIgnoreCase("photo")) {
            pdResponse = getResponse(axiomPhotoClient, namespacePhotoPLM, pdReq, pdResponse, "photo", username);
            if (pdResponse.getTxnStatus() != null
                    && pdResponse.getTxnStatus().equals(PLMConstants.RESPONSE_STATUS_ERROR)) {

                auditError(ts, username, ipAddress, "photo", wsRequestParams, pdResponse);

                return objectFactory.createParoleeInfoResponse(pdResponse);
            }
        } else if (pdReq.getServiceRequestType().equalsIgnoreCase("both")) {
            //logger.debug("********************* inside both ***************************");
            pdResponse = getResponse(axiomDataClient, namespaceDataPLM, pdReq, pdResponse, "data", username);
            //logger.debug("**************** Data : " + pdResponse.getTxnStatus() + " ********************");
            if (pdResponse.getTxnStatus() != null
                    && pdResponse.getTxnStatus().equals(PLMConstants.RESPONSE_STATUS_ERROR)) {

                auditError(ts, username, ipAddress, "both", wsRequestParams, pdResponse);

                return objectFactory.createParoleeInfoResponse(pdResponse);
            }

            pdResponse = getResponse(axiomPhotoClient, namespacePhotoPLM, pdReq, pdResponse, "photo", username);
            //logger.debug("**************** Photo : " + pdResponse.getTxnStatus() + " ********************");
            if (pdResponse.getTxnStatus() != null
                    && pdResponse.getTxnStatus().equals(PLMConstants.RESPONSE_STATUS_ERROR)) {

                auditError(ts, username, ipAddress, "both", wsRequestParams, pdResponse);

                return objectFactory.createParoleeInfoResponse(pdResponse);
            }
        }

        // audit WS request
        WSAuditor.insertWSAuditEntry(ts, username, ipAddress, pdReq.getServiceRequestType(), PLMConstants.RESPONSE_STATUS_SUCCESS, wsRequestParams);

        pdResponse.setTxnStatus(PLMConstants.RESPONSE_STATUS_SUCCESS);
        return objectFactory.createParoleeInfoResponse(pdResponse);
    }

    private boolean validateRequest(ParoleeInfoRequest pdReq) {
        if (pdReq == null || pdReq.getSearchCriteria() == null) {
            return false;
        }
        SearchCriteria sch = pdReq.getSearchCriteria();

        if (sch.getCDCNumber() == null
                && sch.getCity() == null
                && sch.getCounty() == null
                && sch.getIsParoleePAL() == null
                && sch.getLastUpdateDate() == null
                && sch.getLastName() == null
                && sch.getFirstName() == null
                && sch.getMiddleName() == null
                && sch.getAliasLastName() == null
                && sch.getAliasFirstName() == null
                && sch.getMoniker() == null
                && sch.getZip() == null
                && sch.getBirthState() == null
                && sch.getHeightInInches() == null
                && sch.getWeight() == null
                && sch.getSSN() == null
                && sch.getCIINumber() == null
                && sch.getFBINumber() == null
                && sch.getLicensePlate() == null
                && sch.getDateOfBirth() == null
                && sch.getParoleeReleaseDate().getFromDate() == null
                && sch.getParoleeReleaseDate().getToDate() == null
                && sch.getVehicleYear().getFromYear() == null
                && sch.getVehicleYear().getToYear() == null
                && sch.getEthnicity() == null
                && sch.getHairColor() == null
                && sch.getPC290SexOff() == null
                && sch.getPC4571Arson() == null
                && sch.getPC11590Drugs() == null
                && sch.getPC30586FelonyViolation() == null
                && sch.getSMT().getTypeOrLocation() == null
                && sch.getSMT().getPictureOfTattoo() == null
                && sch.getSMT().getTextOfTattoo() == null
                && sch.getCommitmentOffense() == null
                && sch.getCountyOfLLR() == null
                && sch.getUnitCode() == null) {
            return false;
        }
        return true;
    }

    private String getRequestDetails(JAXBElement<ParoleeInfoRequest> req, String username) {

        StringBuilder requestDetails = new StringBuilder();

        try {
            if (req != null) {
                ParoleeInfoRequest pdr = req.getValue();

                requestDetails.append("UserName:").append(username).append("|");
                requestDetails.append("CaseNumber:").append(pdr.getCaseNumber()).append("|");
                requestDetails.append("Reason:").append(pdr.getReason()).append("|");
                requestDetails.append("ServiceType:").append(pdr.getServiceRequestType()).append("|");

                logger.debug("UserName				:" + username);
                logger.debug("CaseNumber			:" + pdr.getCaseNumber());
                logger.debug("Reason				:" + pdr.getReason());
                logger.debug("ServiceType			:" + pdr.getServiceRequestType());

                String strCriteriaValue;

                if (pdr.getResponseFieldsReturned() != null) {
                    strCriteriaValue = pdr.getResponseFieldsReturned().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("ResponseFieldsReturned:").append(strCriteriaValue).append("|");
                        logger.debug("ResponseFieldsReturned:" + strCriteriaValue);
                    }
                }
                if (pdr.getIncludeStateWidePAL() != null) {
                    strCriteriaValue = pdr.getIncludeStateWidePAL().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("IncludeStateWidePAL:").append(strCriteriaValue).append("|");
                        logger.debug("IncludeStateWidePAL:" + strCriteriaValue);
                    }
                }

                SearchCriteria criteria = pdr.getSearchCriteria();

                if (criteria.getCDCNumber() != null) {
                    strCriteriaValue = criteria.getCDCNumber().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("CDCNumber:").append(strCriteriaValue).append("|");
                        logger.debug("CDCNumber			:" + strCriteriaValue);
                    }
                }
                if (criteria.getCity() != null) {
                    strCriteriaValue = criteria.getCity().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("City:").append(strCriteriaValue).append("|");
                        logger.debug("City				:" + strCriteriaValue);
                    }

                }
                if (criteria.getCounty() != null) {
                    printCounties(criteria.getCounty(), requestDetails);
                }
                if (criteria.getLastUpdateDate() != null) {
                    strCriteriaValue = criteria.getLastUpdateDate().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("LastUpdDate:").append(strCriteriaValue).append("|");
                        logger.debug("LastUpdDate		:" + strCriteriaValue);
                    }
                }
                if (criteria.getIsParoleePAL() != null) {
                    strCriteriaValue = criteria.getIsParoleePAL().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("IsParoleePAL:").append(strCriteriaValue).append("|");
                        logger.debug("IsParoleePAL		:" + strCriteriaValue);
                    }
                }
                if (criteria.getLastName() != null) {
                    strCriteriaValue = criteria.getLastName().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("LastName:").append(strCriteriaValue).append("|");
                        logger.debug("LastName			:" + strCriteriaValue);
                    }
                }
                if (criteria.getFirstName() != null) {
                    strCriteriaValue = criteria.getFirstName().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("FirstName:").append(strCriteriaValue).append("|");
                        logger.debug("FirstName			:" + strCriteriaValue);
                    }
                }
                if (criteria.getMiddleName() != null) {
                    strCriteriaValue = criteria.getMiddleName().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("MiddleName:").append(strCriteriaValue).append("|");
                        logger.debug("MiddleName		:" + strCriteriaValue);
                    }
                }
                if (criteria.getAliasLastName() != null) {
                    strCriteriaValue = criteria.getAliasLastName().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("AliasLastName:").append(strCriteriaValue).append("|");
                        logger.debug("AliasLastName		:" + strCriteriaValue);
                    }
                }
                if (criteria.getAliasFirstName() != null) {
                    strCriteriaValue = criteria.getAliasFirstName().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("AliasFirstName:").append(strCriteriaValue).append("|");
                        logger.debug("AliasFirstName	:" + strCriteriaValue);
                    }
                }
                if (criteria.getMoniker() != null) {
                    strCriteriaValue = criteria.getMoniker().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("Moniker:").append(strCriteriaValue).append("|");
                        logger.debug("Moniker			:" + strCriteriaValue);
                    }
                }
                if (criteria.getZip() != null) {
                    strCriteriaValue = criteria.getZip().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("Zip:").append(strCriteriaValue).append("|");
                        logger.debug("Zip				:" + strCriteriaValue);
                    }
                }
                if (criteria.getBirthState() != null) {
                    strCriteriaValue = criteria.getBirthState().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("BirthState:").append(strCriteriaValue).append("|");
                        logger.debug("BirthState		:" + strCriteriaValue);
                    }
                }
                if (criteria.getHeightInInches() != null) {
                    strCriteriaValue = criteria.getHeightInInches().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("HeightInInches:").append(strCriteriaValue).append("|");
                        logger.debug("HeightInInches	:" + strCriteriaValue);
                    }
                }
                if (criteria.getWeight() != null) {
                    strCriteriaValue = criteria.getWeight().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("Weight:").append(strCriteriaValue).append("|");
                        logger.debug("Weight			:" + strCriteriaValue);
                    }
                }
                if (criteria.getSSN() != null) {
                    strCriteriaValue = criteria.getSSN().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("SSN:").append(strCriteriaValue).append("|");
                        logger.debug("SSN				:" + strCriteriaValue);
                    }
                }
                if (criteria.getCIINumber() != null) {
                    strCriteriaValue = criteria.getCIINumber().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("CIINumber:").append(strCriteriaValue).append("|");
                        logger.debug("CIINumber			:" + strCriteriaValue);
                    }
                }
                if (criteria.getFBINumber() != null) {
                    strCriteriaValue = criteria.getFBINumber().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("FBINumber:").append(strCriteriaValue).append("|");
                        logger.debug("FBINumber			:" + strCriteriaValue);
                    }
                }
                if (criteria.getLicensePlate() != null) {
                    strCriteriaValue = criteria.getLicensePlate().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("LicensePlate:").append(strCriteriaValue).append("|");
                        logger.debug("LicensePlate		:" + strCriteriaValue);
                    }
                }
                if (criteria.getDateOfBirth() != null) {
                    strCriteriaValue = criteria.getDateOfBirth().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("DateOfBirth:").append(strCriteriaValue).append("|");
                        logger.debug("DateOfBirth		:" + strCriteriaValue);
                    }
                }
                if (criteria.getParoleeReleaseDate() != null) {
                    if (criteria.getParoleeReleaseDate().getFromDate() != null) {
                        strCriteriaValue = criteria.getParoleeReleaseDate().getFromDate().trim();
                        if (!strCriteriaValue.isEmpty()) {
                            requestDetails.append("ParoleeReleaseFromDate:").append(strCriteriaValue).append("|");
                            logger.debug("ParoleeReleaseFromDate:" + strCriteriaValue);
                        }
                    }
                    if (criteria.getParoleeReleaseDate().getToDate() != null) {
                        strCriteriaValue = criteria.getParoleeReleaseDate().getToDate().trim();
                        if (!strCriteriaValue.isEmpty()) {
                            requestDetails.append("ParoleeReleaseToDate:").append(strCriteriaValue).append("|");
                            logger.debug("ParoleeReleaseToDate	:" + strCriteriaValue);
                        }
                    }
                }
                if (criteria.getVehicleYear() != null) {
                    if (criteria.getVehicleYear().getFromYear() != null) {
                        strCriteriaValue = criteria.getVehicleYear().getFromYear().trim();
                        if (!strCriteriaValue.isEmpty()) {
                            requestDetails.append("VehicleFromYear:").append(strCriteriaValue).append("|");
                            logger.debug("VehicleFromYear		:" + strCriteriaValue);
                        }
                    }
                    if (criteria.getVehicleYear().getToYear() != null) {
                        strCriteriaValue = criteria.getVehicleYear().getToYear().trim();
                        if (!strCriteriaValue.isEmpty()) {
                            requestDetails.append("VehicleToYear:").append(strCriteriaValue).append("|");
                            logger.debug("VehicleToYear			:" + strCriteriaValue);
                        }
                    }
                }
                if (criteria.getEthnicity() != null) {
                    strCriteriaValue = criteria.getEthnicity().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("Ethnicity:").append(strCriteriaValue).append("|");
                        logger.debug("Ethnicity			:" + strCriteriaValue);
                    }
                }
                if (criteria.getHairColor() != null) {
                    strCriteriaValue = criteria.getHairColor().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("HairColor:").append(strCriteriaValue).append("|");
                        logger.debug("HairColor			:" + strCriteriaValue);
                    }
                }
                if (criteria.getPC290SexOff() != null) {
                    strCriteriaValue = criteria.getPC290SexOff().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("PC290SexOff:").append(strCriteriaValue).append("|");
                        logger.debug("PC290SexOff		:" + strCriteriaValue);
                    }
                }
                if (criteria.getPC4571Arson() != null) {
                    strCriteriaValue = criteria.getPC4571Arson().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("PC4571Arson:").append(strCriteriaValue).append("|");
                        logger.debug("PC4571Arson		:" + strCriteriaValue);
                    }
                }
                if (criteria.getPC11590Drugs() != null) {
                    strCriteriaValue = criteria.getPC11590Drugs().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("PC11590Drugs:").append(strCriteriaValue).append("|");
                        logger.debug("PC11590Drugs		:" + strCriteriaValue);
                    }
                }
                if (criteria.getPC30586FelonyViolation() != null) {
                    strCriteriaValue = criteria.getPC30586FelonyViolation().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("PC30586FelonyViolation:").append(strCriteriaValue).append("|");
                        logger.debug("PC30586FelonyViolation:" + strCriteriaValue);
                    }
                }
                if (criteria.getSMT() != null) {
                    if (criteria.getSMT().getTypeOrLocation() != null) {
                        strCriteriaValue = criteria.getSMT().getTypeOrLocation().trim();
                        if (!strCriteriaValue.isEmpty()) {
                            requestDetails.append("SMTTypeOrLocation:").append(strCriteriaValue).append("|");
                            logger.debug("SMTTypeOrLocation	:" + strCriteriaValue);
                        }
                    }
                    if (criteria.getSMT().getPictureOfTattoo() != null) {
                        strCriteriaValue = criteria.getSMT().getPictureOfTattoo().trim();
                        if (!strCriteriaValue.isEmpty()) {
                            requestDetails.append("SMTPictureOfTattoo:").append(strCriteriaValue).append("|");
                            logger.debug("SMTPictureOfTattoo:" + strCriteriaValue);
                        }
                    }
                    if (criteria.getSMT().getTextOfTattoo() != null) {
                        strCriteriaValue = criteria.getSMT().getTextOfTattoo().trim();
                        if (!strCriteriaValue.isEmpty()) {
                            requestDetails.append("SMTTextOfTattoo:").append(strCriteriaValue).append("|");
                            logger.debug("SMTTextOfTattoo	:" + strCriteriaValue);
                        }
                    }
                }
                if (criteria.getCommitmentOffense() != null) {
                    strCriteriaValue = criteria.getCommitmentOffense().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("CommitmentOffense:").append(strCriteriaValue).append("|");
                        logger.debug("CommitmentOffense	:" + strCriteriaValue);
                    }
                }
                if (criteria.getCountyOfLLR() != null) {
                    strCriteriaValue = criteria.getCountyOfLLR().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("CountyOfLLR:").append(strCriteriaValue).append("|");
                        logger.debug("CountyOfLLR		:" + strCriteriaValue);
                    }
                }
                if (criteria.getUnitCode() != null) {
                    strCriteriaValue = criteria.getUnitCode().trim();
                    if (!strCriteriaValue.isEmpty()) {
                        requestDetails.append("UnitCode:").append(strCriteriaValue).append("|");
                        logger.debug("UnitCode			:" + strCriteriaValue);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(PLMUtil.getStackTrace(e));
        }

        return requestDetails.toString();
    }

    private void printCounties(String tmpCounties, StringBuilder requestDetails) {
        if (tmpCounties != null && !tmpCounties.isEmpty()) {
            if (tmpCounties.contains(",")) {
                String[] arrCounties = tmpCounties.split(",");
                for (String county : arrCounties) {
                    if (!county.trim().isEmpty()) {
                        logger.debug("County			:" + county);
                        requestDetails.append("County:").append(county).append("|");
                    }
                }
            } else {
                if (!tmpCounties.trim().isEmpty()) {
                    logger.debug("County			:" + tmpCounties);
                    requestDetails.append("County:").append(tmpCounties).append("|");
                }
            }
        }
    }

    private OMElement checkErrors(SOAPMessage msg, String namespacePLM, String serviceType) {
        OMElement eleEnv = msg.getFirstChildWithName(new QName(namespaceEnv, "Envelope", prefixEnv));
        logger.debug(eleEnv.getLocalName());
        if (eleEnv != null) {
            OMElement eleBody = eleEnv.getFirstChildWithName(new QName(namespaceEnv, "Body", prefixEnv));
            logger.debug(eleBody.getLocalName());
            if (eleBody != null) {
                String responseType = "";
                if (serviceType.equalsIgnoreCase("datacheck")) {
                    responseType = "InternalParoleeDataCheckResponse";
                } else if (serviceType.equalsIgnoreCase("photo")) {
                    responseType = "InternalParoleePhotoResponse";
                } else {
                    responseType = "InternalParoleeDataResponse";
                }

                OMElement eleDataResp = eleBody.getFirstChildWithName(new QName(namespacePLM, responseType, prefixNS2));
                if (eleDataResp != null) {
                    logger.debug(eleDataResp.getLocalName());
                    OMElement eleStatus = eleDataResp.getFirstChildWithName(new QName(namespacePLM, "TxnStatus", prefixNS2));
                    if (eleStatus != null) {
                        logger.debug(eleStatus.getLocalName());
                        String sStatus = eleStatus.getText();
                        if (sStatus != null && !"".equals(sStatus.trim())) {
                            if (sStatus.equalsIgnoreCase(PLMConstants.RESPONSE_STATUS_ERROR)) {
                                OMElement eleError = eleDataResp.getFirstChildWithName(new QName(namespacePLM, "Error", prefixNS2));
                                logger.debug(eleError.getLocalName());
                                if (eleError != null) {
                                    return eleError;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private String getUpdateAvailable(SOAPMessage msg, String namespacePLM, String serviceType) {
        String sParoleeRecord = null;
        OMElement eleEnv = msg.getFirstChildWithName(new QName(namespaceEnv, "Envelope", prefixEnv));
        if (eleEnv != null) {
            OMElement eleBody = eleEnv.getFirstChildWithName(new QName(namespaceEnv, "Body", prefixEnv));
            if (eleBody != null) {
                String responseType = "";
                if (serviceType.equalsIgnoreCase("datacheck")) {
                    responseType = "InternalParoleeDataCheckResponse";
                }

                OMElement eleDataResp = eleBody.getFirstChildWithName(new QName(namespacePLM, responseType, prefixNS2));
                if (eleDataResp != null) {
                    OMElement eleUpdateAvailable = eleDataResp.getFirstChildWithName(new QName(namespacePLM, "UpdateAvailable", prefixNS2));
                    if (eleUpdateAvailable != null) {
                        logger.debug(eleUpdateAvailable.getLocalName());
                        sParoleeRecord = eleUpdateAvailable.getText();
                    }
                }
            }
        }
        return sParoleeRecord;
    }

    private String getFileNamefromResponse(SOAPMessage msg, String namespacePLM, String serviceType) {
        String sFileName = null;
        OMElement eleEnv = msg.getFirstChildWithName(new QName(namespaceEnv, "Envelope", prefixEnv));
        if (eleEnv != null) {
            OMElement eleBody = eleEnv.getFirstChildWithName(new QName(namespaceEnv, "Body", prefixEnv));
            if (eleBody != null) {
                String responseType = "";
                if (serviceType.equalsIgnoreCase("photo")) {
                    responseType = "InternalParoleePhotoResponse";
                } else {
                    responseType = "InternalParoleeDataResponse";
                }

                OMElement eleDataResp = eleBody.getFirstChildWithName(new QName(namespacePLM, responseType, prefixNS2));
                if (eleDataResp != null) {
                    OMElement eleDataAttachment = eleDataResp.getFirstChildWithName(new QName(namespacePLM, "DataAttachment", prefixNS2));
                    if (eleDataAttachment != null) {
                        logger.debug("DataAttachment :: " + eleDataAttachment.getLocalName());
                        OMElement eleFileName = eleDataAttachment.getFirstChildWithName(new QName(namespacePLM, "filename", prefixNS2));
                        if (eleFileName != null) {
                            logger.debug(eleFileName.getLocalName());
                            sFileName = eleFileName.getText();
                        }
                    }
                }
            }
        }
        return sFileName;
    }


    @SuppressWarnings("unchecked")
    private ParoleeInfoResponse getResponse(AxiomMtomClient axiomClient, String namespacePLM, ParoleeInfoRequest pdReq, ParoleeInfoResponse pdResponse, String serviceType, String username) {

        AxiomSoapMessage body = null;
        try {
            body = axiomClient.doIt(
                    pdReq.getSearchCriteria(),
                    username,
                    pdReq.getCaseNumber(),
                    pdReq.getReason(),
                    serviceType,
                    pdReq.getResponseFieldsReturned(),
                    pdReq.getIncludeStateWidePAL()
            );
        } catch (WebServiceTransportException e) {
            logger.error(PLMUtil.getStackTrace(e));
        }

        if (body == null) {
            Error err = new Error();
            err.setErrorCode(PLMConstants.ERR_SERVICE_UNAVAILABLE);
            err.setErrorMessage(PLMConstants.ERR_SERVICE_UNAVAILABLE_MESSAGE);
            pdResponse.setTxnStatus(PLMConstants.RESPONSE_STATUS_ERROR);
            pdResponse.setError(err);
            return pdResponse;
        }
        SOAPMessage msg = body.getAxiomMessage();
        OMElement eleError = checkErrors(msg, namespacePLM, serviceType);
        if (eleError != null) {
            OMElement eleErrorCode = eleError.getFirstChildWithName(new QName(namespacePLM, "ErrorCode", prefixNS2));
            OMElement eleErrorMsg = eleError.getFirstChildWithName(new QName(namespacePLM, "ErrorMessage", prefixNS2));
            Error err = new Error();
            if (eleErrorCode != null) {
                err.setErrorCode(eleErrorCode.getText());
            }
            if (eleErrorMsg != null) {
                err.setErrorMessage(eleErrorMsg.getText());
            }
            pdResponse.setTxnStatus(PLMConstants.RESPONSE_STATUS_ERROR);
            pdResponse.setError(err);
            return pdResponse;
        }

        String updateAvailable = getUpdateAvailable(msg, namespacePLM, serviceType);
        if (updateAvailable != null) {
            pdResponse.setUpdateAvailable(updateAvailable);
        }

        Iterator<Attachment> ite = body.getAttachments();
        while (ite.hasNext()) {
            Attachment att = ite.next();
            logger.debug("Attachment Type :: " + att.getContentType());
//			if(!att.getContentType().startsWith("text/xml")){
            // 2018-03-15 emil change content type exclusion to prevent duplicate attachments
            if (!att.getContentType().startsWith("application/xop+xml")) {
                logger.debug("Attachement: " + att.getContentId() + " Content Type: " + att.getContentType());
                DataHandler dataHandler = att.getDataHandler();
                DataAttachment dtAttach = new DataAttachment();
                dtAttach.setFilename(getFileNamefromResponse(msg, namespacePLM, serviceType));
                dtAttach.setData(dataHandler);
                pdResponse.getDataAttachment().add(dtAttach);
            }
        }
        return pdResponse;
    }

    private void auditError(Timestamp ts, String username, String ipAddress, String wsType, String wsRequestParams, ParoleeInfoResponse pdResponse) {
        String errorCode = PLMConstants.RESPONSE_STATUS_ERROR;

        if (pdResponse.getError() != null && pdResponse.getError().getErrorCode() != null) {
            errorCode = errorCode + "|" + pdResponse.getError().getErrorCode();
        }

        WSAuditor.insertWSAuditEntry(ts, username, ipAddress, wsType, errorCode, wsRequestParams);
    }
}