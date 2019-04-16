package com.plm.ws.endpoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBElement;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpServletConnection;
import plm.ws.mtom.datacheck.Error;
import plm.ws.mtom.datacheck.InternalParoleeDataCheckRequest;
import plm.ws.mtom.datacheck.InternalParoleeDataCheckResponse;
import plm.ws.mtom.datacheck.ObjectFactory;
import com.plm.dataretrival.DataCheckProvider;
import com.plm.dataretrival.DataException;
import com.plm.dataretrival.SearchCriteriaInfo;
import com.plm.dataretrival.cfg.ConfigDataProvider;
import com.plm.dataretrival.cfg.WSErrorMessages;
import com.plm.util.PLMConstants;
import com.plm.util.PLMUtil;

/*
  C H A N G E    H I S T O R Y
 ================================================================================================================+
 DATE       | REASON        | AUTHOR        | COMMENTS                                                           |
 ================================================================================================================+
 Dec'2017   | initial       |               | CDCR LEADS legacy code.                                            |
 ----------------------------------------------------------------------------------------------------------------|
 Nov'2018   | refactoring   | Emil Akhmirov | Modified code to fix concurrency issues with Date/DateFormat Java  |
            |               |               | classes. Switched to using Java 8 java.time package instead        |
 ----------------------------------------------------------------------------------------------------------------+
 */

/**
 * @author Girish Menon
 */
@Endpoint
public class DataCheckEndpoint {
    private static final ObjectFactory objectFactory = new ObjectFactory();
    private static final Logger logger = Logger.getLogger(DataCheckEndpoint.class);
    private Map<String, String> errorMap = null;
//    private SimpleDateFormat defaultDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private static final DateTimeFormatter DEFAULT_DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final LocalDate MIN_DATE = LocalDate.of(1900, Month.JANUARY, 1);

    public DataCheckEndpoint() {
//        this.objectFactory = new ObjectFactory();
        WSErrorMessages errorMessages = ConfigDataProvider.getInstance().getErrorMessages();
        if (errorMessages != null) {
            errorMap = errorMessages.getErrorMessageMap();
        }
    }

    @PayloadRoot(localPart = "InternalParoleeDataCheckRequest", namespace = "http://www.plm/ws/mtom/datacheck")
    @ResponsePayload
    public JAXBElement<InternalParoleeDataCheckResponse> checkData(@RequestPayload JAXBElement<InternalParoleeDataCheckRequest> reqElement) {

        MDC.put("id",  System.currentTimeMillis());

        try {

            return generateResponse(reqElement);

        } finally {
            MDC.clear();
        }
    }

    private JAXBElement<InternalParoleeDataCheckResponse> generateResponse(JAXBElement<InternalParoleeDataCheckRequest> reqElement) {

        TransportContext context = TransportContextHolder.getTransportContext();
        HttpServletConnection connection = (HttpServletConnection) context.getConnection();
        HttpServletRequest request = connection.getHttpServletRequest();
        SearchCriteriaInfo scInfo = null;
        InternalParoleeDataCheckRequest pdReq = null;
        String updateAvailable = null;
        DataCheckProvider dataCheckProvider = null;
        String errorCode = null;
        if (reqElement != null) {
            printRequest(reqElement);
            pdReq = reqElement.getValue();
            try {
                validateRequest(pdReq);
            } catch (ParseException e) {
                logger.error(PLMUtil.getStackTrace(e));
                errorCode = PLMConstants.ERR_INVALID_DATE_FORMAT;
            }
            if (errorCode == null) {
                if (pdReq != null) {
                    try {
                        scInfo = convertToSearchCriteria(pdReq);
                    } catch (ParseException e) {
                        logger.error(PLMUtil.getStackTrace(e));
                        errorCode = PLMConstants.ERR_INVALID_DATE_FORMAT;
                    }
                } else {
                    errorCode = PLMConstants.ERR_INVALID_REQUEST;
                }
            }
        } else {
            errorCode = PLMConstants.ERR_INVALID_REQUEST;
        }
        InternalParoleeDataCheckResponse pdResponse = new InternalParoleeDataCheckResponse();
        if (errorCode == null) {
            scInfo.setIpAddress(request.getRemoteAddr());
            //There is no error in Request - so proceed further for processing Request.
            dataCheckProvider = new DataCheckProvider();
            try {
                updateAvailable = dataCheckProvider.checkIfUpdateAvailable(scInfo);
            } catch (DataException e) {
                logger.error(PLMUtil.getStackTrace(e));
                errorCode = e.getErrorCode();
            }
        }
        if (errorCode == null) {
            //If there is NO ERROR
            pdResponse.setTxnStatus(PLMConstants.RESPONSE_STATUS_SUCCESS);
            pdResponse.setUpdateAvailable(updateAvailable);
        } else {
            Error err = createError(errorCode);
            pdResponse.setError(err);
            pdResponse.setTxnStatus(PLMConstants.RESPONSE_STATUS_ERROR);
        }
        return objectFactory.createInternalParoleeDataCheckResponse(pdResponse);

    }

    private Error createError(String errorCode) {
        Error err = new Error();
        err.setErrorCode(errorCode);
        err.setErrorMessage(errorMap.get(errorCode));
        return err;
    }

    private SearchCriteriaInfo convertToSearchCriteria(InternalParoleeDataCheckRequest pdRequest) throws ParseException {
        SearchCriteriaInfo scInfo = new SearchCriteriaInfo();
        scInfo.setCaseNumber(pdRequest.getCaseNumber());
        scInfo.setReason(pdRequest.getReason());
        scInfo.setUsername(pdRequest.getUsername());
        if (pdRequest.getSearchCriteria() != null) {
            if (pdRequest.getSearchCriteria().getCDCNumber() != null) {
                scInfo.setCDCNumber(pdRequest.getSearchCriteria().getCDCNumber());
            }
            if (pdRequest.getSearchCriteria().getLastUpdateDate() != null) {
                scInfo.setLastUpdateDate(pdRequest.getSearchCriteria().getLastUpdateDate());
                scInfo.setLastUpdateDateObj(LocalDate.parse(pdRequest.getSearchCriteria().getLastUpdateDate(), DEFAULT_DATE_FORMAT));
            }
        }
        return scInfo;
    }

    private void printRequest(JAXBElement<InternalParoleeDataCheckRequest> req) {
        if (req != null) {
            InternalParoleeDataCheckRequest pdr = req.getValue();
            if (pdr != null) {
                logger.warn("CaseNumber			:" + pdr.getCaseNumber());
                logger.warn("UserName			:" + pdr.getUsername());
                logger.warn("Reason				:" + pdr.getReason());
                if (pdr.getSearchCriteria().getCDCNumber() != null) {
                    logger.warn("CDCNumber			:" + pdr.getSearchCriteria().getCDCNumber());
                }
                if (pdr.getSearchCriteria().getLastUpdateDate() != null) {
                    logger.warn("Last Update Date	:" + pdr.getSearchCriteria().getLastUpdateDate());
                }
            }
        }
    }

    /**
     * Validates the Search Request parameters.
     *
     * @param pdRequest - InternalParoleeDataCheckRequest
     * @throws ParseException
     * @throws Exception      with ERROR_CODE as message in case if the Date value is invalid Or Date format is invalid
     *                        OR Any other search parameter data is not specified.
     */
    private void validateRequest(InternalParoleeDataCheckRequest pdRequest) throws ParseException {
        String errorCode = null;
        if (pdRequest != null) {
            if (pdRequest.getCaseNumber() == null || pdRequest.getCaseNumber().trim().length() == 0) {
                errorCode = PLMConstants.ERR_CASE_NUMBER_NOT_SPECIFIED;
            } else if (pdRequest.getReason() == null || pdRequest.getReason().trim().length() == 0) {
                errorCode = PLMConstants.ERR_REASON_NOT_SPECIFIED;
            } else if (pdRequest.getUsername() == null || pdRequest.getUsername().trim().length() == 0) {
                errorCode = PLMConstants.ERR_USERNAME_NOT_SPECIFIED;
            } else if (pdRequest.getSearchCriteria().getCDCNumber() == null || pdRequest.getSearchCriteria().getCDCNumber().trim().length() == 0) {
                errorCode = PLMConstants.ERR_CDC_NUMBERS_NOT_SPECIFIED;
            }
            //If there is no error so far - check the date and its format validity.
            if (errorCode == null) {
                if (pdRequest.getSearchCriteria() != null) {
                    if (pdRequest.getSearchCriteria().getLastUpdateDate() != null) {
                        if (pdRequest.getSearchCriteria().getLastUpdateDate().trim().length() > 0) {
                            LocalDate validDate = null;
                            //Try to parse with default date format.
                            validDate = LocalDate.parse(pdRequest.getSearchCriteria().getLastUpdateDate().trim(), DEFAULT_DATE_FORMAT);
                            //
                            //Date - Content wise date is valid
                            //Now check the Range wise eligibility of date.
                            //
                            if (validDate != null) {
//                                Date systemDate = Calendar.getInstance().getTime();
                                LocalDateTime systemDate = LocalDateTime.now();
                                if (systemDate.isBefore(validDate.atStartOfDay())) {
                                    //Specified Date is Future Date.
                                    errorCode = PLMConstants.ERR_FUTURE_LAST_UPDATE_DATE;
                                } else {
                                    if (validDate.isBefore(MIN_DATE)) {
                                        errorCode = PLMConstants.ERR_LAST_UPDATE_DATE_CROSSED_DOWNLOAD_LIMIT;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            errorCode = PLMConstants.ERR_INVALID_REQUEST;
        }
    }
}