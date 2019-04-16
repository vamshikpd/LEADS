package com.plm.ws.endpoint;

import java.io.File;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBElement;

import com.plm.dataretrival.cfg.WSConfig;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpServletConnection;

import plm.ws.mtom.data.DataAttachment;
import plm.ws.mtom.data.Error;
import plm.ws.mtom.data.InternalParoleeDataRequest;
import plm.ws.mtom.data.InternalParoleeDataResponse;
import plm.ws.mtom.data.ObjectFactory;
import plm.ws.mtom.data.ParoleeReleaseDate;
import plm.ws.mtom.data.VehicleYear;

import com.plm.dataretrival.DataException;
import com.plm.dataretrival.ParoleeTextDataProvider;
import com.plm.dataretrival.SearchCriteriaInfo;
import com.plm.dataretrival.cfg.ConfigDataProvider;
import com.plm.dataretrival.cfg.WSErrorMessages;
import com.plm.util.FileUtil;
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
public class ParoleeDataEndpoint {
    private static final ObjectFactory objectFactory = new ObjectFactory();
    private static final Logger logger = Logger.getLogger(ParoleeDataEndpoint.class);
    private Map<String, String> errorMap = null;
//	private SimpleDateFormat defaultDateFormat = new SimpleDateFormat("MM/dd/yyyy");
//	private SimpleDateFormat endecaDefaultFormat = new SimpleDateFormat("yyyyMMdd");

    private static final DateTimeFormatter DEFAULT_DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter ENDECA_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final LocalDate MIN_DATE = LocalDate.of(1900, Month.JANUARY, 1);

    public ParoleeDataEndpoint() {
//		this.objectFactory =
        WSErrorMessages errorMessages = ConfigDataProvider.getInstance().getErrorMessages();
        if (errorMessages != null) {
            errorMap = errorMessages.getErrorMessageMap();
        }
    }

    @PayloadRoot(localPart = "InternalParoleeDataRequest", namespace = "http://www.plm/ws/mtom/data")
    @ResponsePayload
    public JAXBElement<InternalParoleeDataResponse> getParoleeData(@RequestPayload JAXBElement<InternalParoleeDataRequest> reqElement) {

        MDC.put("id", System.currentTimeMillis());

        try {

            return generateResponse(reqElement);

        } finally {
            MDC.clear();
        }
    }

    private JAXBElement<InternalParoleeDataResponse> generateResponse(JAXBElement<InternalParoleeDataRequest> reqElement) {

        TransportContext context = TransportContextHolder.getTransportContext();
        HttpServletConnection connection = (HttpServletConnection) context.getConnection();

        HttpServletRequest request = connection.getHttpServletRequest();
        SearchCriteriaInfo scInfo;
        String includeAttachment;
        String fileName;

        ParoleeTextDataProvider paroleeDataProvider;
        InternalParoleeDataResponse pdResponse = new InternalParoleeDataResponse();
        String errorCode;

        // logger.debug("LEADSDataService getParoleeData() endpoint ----->");

        // logger.debug(reqElement.toString());

        if (reqElement == null) {
            logger.error("reqElement is null");
            return generateErrorResponse(PLMConstants.ERR_INVALID_REQUEST);
        }

        InternalParoleeDataRequest pdReq = reqElement.getValue();
        errorCode = validateRequest(pdReq);

		/*if (errorCode != null) {
			logger.debug("After validate request ---> errorCode=" + errorCode);
		} else {
			logger.debug("After validate request, no errors");
		}*/

        if (errorCode != null) {
            return generateErrorResponse(errorCode);
        }
        printRequest(reqElement);
        try {
            scInfo = convertToSearchCriteria(pdReq);
        } catch (ParseException pe) {
            logger.error(PLMUtil.getStackTrace(pe));
            return generateErrorResponse(PLMConstants.ERR_INVALID_LAST_UPDATE_DATE);
        }

        includeAttachment = pdReq.getIncludeAttachment().trim();
        scInfo.setIpAddress(request.getRemoteAddr());
        paroleeDataProvider = new ParoleeTextDataProvider();
        try {
            if (includeAttachment != null && (includeAttachment.equalsIgnoreCase("N") || includeAttachment.equalsIgnoreCase("No"))) {
                fileName = paroleeDataProvider.generateParoleeDataInBackground(scInfo);
            } else {
                fileName = paroleeDataProvider.getParoleeData(scInfo);
                DataAttachment data = createDataAttachment(fileName);
                pdResponse.setDataAttachment(data);
            }
        } catch (DataException e) {
            logger.error(PLMUtil.getStackTrace(e));
            errorCode = e.getErrorCode();
            return generateErrorResponse(errorCode);
        }

        pdResponse.setTxnStatus(PLMConstants.RESPONSE_STATUS_SUCCESS);
        return objectFactory.createInternalParoleeDataResponse(pdResponse);

    }

    /**
     * Validates the Search Request parameters.
     *
     * @param pdRequest - InternalParoleeDataRequest
     * @throws ParseException
     * @throws Exception      with ERROR_CODE as message in case if the Date value is invalid Or Date format is invalid
     *                        OR Any other search parameter data is not specified.
     */
    private String validateRequest(InternalParoleeDataRequest pdRequest) {
        if (pdRequest == null || pdRequest.getSearchCriteria() == null) {
            return PLMConstants.ERR_INVALID_REQUEST;
        }

        if (pdRequest.getCaseNumber() == null || pdRequest.getCaseNumber().trim().isEmpty()) {
            return PLMConstants.ERR_CASE_NUMBER_NOT_SPECIFIED;
        } else if (pdRequest.getReason() == null || pdRequest.getReason().trim().isEmpty()) {
            return PLMConstants.ERR_REASON_NOT_SPECIFIED;
        } else if (pdRequest.getUsername() == null || pdRequest.getUsername().trim().isEmpty()) {
            return PLMConstants.ERR_USERNAME_NOT_SPECIFIED;
        }

        if (pdRequest.getResponseFieldsReturned() != null && !pdRequest.getResponseFieldsReturned().isEmpty()) {
            String strResponseField = pdRequest.getResponseFieldsReturned();
            if (strResponseField.contains(",")) {
                String[] arrRespField = strResponseField.split(",");
                for (String responseField : arrRespField) {
                    if (!PLMUtil.getReturnFieldsSet().contains(responseField.trim())) {
                        return PLMConstants.ERR_INVALID_RESPONSE_FIELD;
                    }
                }
            } else {
                if (!PLMUtil.getReturnFieldsSet().contains(strResponseField.trim())) {
                    return PLMConstants.ERR_INVALID_RESPONSE_FIELD;
                }
            }
        }


        //Check if any process is already running for the user
        //or
        //Check if any process is hung for the user
        String userFileName = FileUtil.getParoleeDataFilename(pdRequest.getUsername());
        String inProgressFileNameWithPath = FileUtil.getFileOutputPath() + File.separator + userFileName + ".inprogress";
        boolean fileExists = FileUtil.fileExists(inProgressFileNameWithPath);
        if (fileExists) {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.HOUR_OF_DAY, -1);
            boolean processHung = FileUtils.isFileOlder(new File(inProgressFileNameWithPath), now.getTimeInMillis());
            if (processHung) {
                return PLMConstants.ERR_USERNAME_INPROGRESS_FILE_EXISTS_HANG;
            } else {
                return PLMConstants.ERR_USERNAME_INPROGRESS_FILE_EXISTS;
            }
        }
        String errorCode = null;
        //If there is no error so far - check the date and its format validity.
        if (pdRequest.getSearchCriteria().getLastUpdateDate() != null
                && !pdRequest.getSearchCriteria().getLastUpdateDate().trim().isEmpty()) {
            errorCode = validateLastUpdateDate(pdRequest.getSearchCriteria().getLastUpdateDate());
            if (errorCode != null) {
                return errorCode;
            }
        }

        if (pdRequest.getSearchCriteria().getDateOfBirth() != null
                && !pdRequest.getSearchCriteria().getDateOfBirth().trim().isEmpty()) {
            errorCode = validateDateOfBirth(pdRequest.getSearchCriteria().getDateOfBirth());
            if (errorCode != null) {
                return errorCode;
            }
        }

        //validate From Date and To Date from Parole Release Date.
        if (pdRequest.getSearchCriteria().getParoleeReleaseDate() != null) {
            errorCode = validateParoleReleaseDate(pdRequest.getSearchCriteria().getParoleeReleaseDate());
            if (errorCode != null) {
                return errorCode;
            }
        }

        if (pdRequest.getSearchCriteria().getVehicleYear() != null) {
            errorCode = validateVehicleYear(pdRequest.getSearchCriteria().getVehicleYear());
            if (errorCode != null) {
                return errorCode;
            }
        }

        return null;
    }

    private String validateLastUpdateDate(String strLastUpdateDate) {
        //Try to parse with default date format.
        LocalDate validDate = null;
        try {
            validDate = LocalDate.parse(strLastUpdateDate, DEFAULT_DATE_FORMAT);
        } catch (DateTimeParseException exc) {
            try {
                validDate = LocalDate.parse(strLastUpdateDate, ENDECA_DATE_FORMAT);
            } catch (DateTimeParseException exc2) {
                logger.error(PLMUtil.getStackTrace(exc2));
                return PLMConstants.ERR_INVALID_LAST_UPDATE_DATE;
            }
        }

        //Date - Content wise date is valid
        //Now check the Range wise eligibility of date.
        if (validDate != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(validDate.atStartOfDay())) {

                //Specified Date is Future Date.
                return PLMConstants.ERR_FUTURE_LAST_UPDATE_DATE;

            } else {

                if (validDate.isBefore(MIN_DATE)) {
                    return PLMConstants.ERR_LAST_UPDATE_DATE_CROSSED_DOWNLOAD_LIMIT;
                }
            }
        }
        return null;
    }

    private String validateDateOfBirth(String strDateOfBirth) {
        //Try to parse with default date format.
        LocalDate validDate = null;
        try {
            validDate = LocalDate.parse(strDateOfBirth, ENDECA_DATE_FORMAT);
        } catch (DateTimeParseException pe) {
            logger.error(PLMUtil.getStackTrace(pe));
            return PLMConstants.ERR_INVALID_DATE_OF_BIRTH;
        }

        //Date - Content wise date is valid
        //Now check the Range wise eligibility of date.
        if (validDate != null) {

            // Date systemDate = Calendar.getInstance().getTime();
            LocalDateTime now = LocalDateTime.now();

            if (now.isBefore(validDate.atStartOfDay())) {

                //Specified Date is Future Date.
                return PLMConstants.ERR_FUTURE_DATE_OF_BIRTH;
            }
        }
        return null;
    }

    private String validateParoleReleaseDate(ParoleeReleaseDate pdRelDate) {
        LocalDate fromDate = null;
        LocalDate toDate = null;
        if (pdRelDate.getFromDate() != null && !pdRelDate.getFromDate().trim().isEmpty()) {
            try {
//                fromDate = endecaDefaultFormat.parse(pdRelDate.getFromDate().trim());
                fromDate = LocalDate.parse(pdRelDate.getFromDate().trim(), ENDECA_DATE_FORMAT);

            } catch (DateTimeParseException pe) {
                logger.error(PLMUtil.getStackTrace(pe));
                return PLMConstants.ERR_INVALID_FROM_PAROLE_RELEASE_DATE;
            }
        }

        if (pdRelDate.getToDate() != null && !pdRelDate.getToDate().trim().isEmpty()) {
            try {

                toDate = LocalDate.parse(pdRelDate.getToDate().trim(), ENDECA_DATE_FORMAT);

            } catch (DateTimeParseException pe) {
                logger.error(PLMUtil.getStackTrace(pe));
                return PLMConstants.ERR_INVALID_TO_PAROLE_RELEASE_DATE;
            }
        }
        if (fromDate != null && toDate == null) {
            return PLMConstants.ERR_FROM_DATE_WITHOUT_TO_DATE;
        }
        if (fromDate == null && toDate != null) {
            return PLMConstants.ERR_TO_DATE_WITHOUT_FROM_DATE;
        }

        if (fromDate != null) {

            if (fromDate.isBefore(MIN_DATE)) {
                return PLMConstants.ERR_OLDEST_PAROLE_RELEASE_DATE;
            }
        }

        if (fromDate != null && toDate.isBefore(fromDate)) {
            return PLMConstants.ERR_TO_DATE_BEFORE_FROM_DATE;
        }

        return null;
    }

    private String validateVehicleYear(VehicleYear vehYear) {
        int fromYear = 0;
        int toYear = 0;
        int oldestYear = 1900;
        if (vehYear.getFromYear() != null && !vehYear.getFromYear().trim().isEmpty()) {
            try {
                if (vehYear.getFromYear().trim().length() < 4) {
                    return PLMConstants.ERR_INVALID_VEHICLE_YEAR;
                }
                fromYear = Integer.parseInt(vehYear.getFromYear().trim());
            } catch (NumberFormatException ne) {
                logger.error(PLMUtil.getStackTrace(ne));
                return PLMConstants.ERR_INVALID_VEHICLE_YEAR;
            }
        }
        if (vehYear.getToYear() != null && !vehYear.getToYear().trim().isEmpty()) {
            try {
                if (vehYear.getToYear().trim().length() < 4) {
                    return PLMConstants.ERR_INVALID_VEHICLE_YEAR;
                }
                toYear = Integer.parseInt(vehYear.getToYear().trim());
            } catch (NumberFormatException ne) {
                logger.error(PLMUtil.getStackTrace(ne));
                return PLMConstants.ERR_INVALID_VEHICLE_YEAR;
            }
        }

        if (fromYear != 0 && toYear == 0) {
            return PLMConstants.ERR_FROM_YEAR_WITHOUT_TO_YEAR;
        }
        if (fromYear == 0 && toYear != 0) {
            return PLMConstants.ERR_TO_YEAR_WITHOUT_FROM_YEAR;
        }
        Calendar cal = Calendar.getInstance();
        int curYear = cal.get(Calendar.YEAR);
        if (fromYear != 0) {
            if (fromYear > curYear) {
                return PLMConstants.ERR_FUTURE_FROM_VEHICLE_YEAR;
            } else if (fromYear < oldestYear) {
                return PLMConstants.ERR_OLDEST_VEHICLE_YEAR;
            }
        }
        if (toYear != 0 && toYear > curYear) {
            return PLMConstants.ERR_FUTURE_TO_VEHICLE_YEAR;
        }
        if (fromYear != 0 && toYear != 0 && toYear < fromYear) {
            return PLMConstants.ERR_TO_YEAR_BEFORE_FROM_YEAR;
        }
        return null;
    }

    private DataAttachment createDataAttachment(String filename) {
        DataAttachment dtAttach = new DataAttachment();
        FileDataSource fileDataSource = new FileDataSource(new File(filename));
        DataHandler dataHandler = new DataHandler(fileDataSource);
        int index = filename.lastIndexOf(File.separator);
        String onlyFileName = null;
        if (index > 0) {
            onlyFileName = filename.substring(index + 1);
        } else {
            onlyFileName = filename;
        }
        dtAttach.setFilename(onlyFileName);
        dtAttach.setData(dataHandler);
        return dtAttach;
    }

    /**
     * This method constructs and returns web service error response
     *
     * @param errorCode
     * @return
     */
    private JAXBElement<InternalParoleeDataResponse> generateErrorResponse(String errorCode) {
        InternalParoleeDataResponse pdResponse = new InternalParoleeDataResponse();
        Error err = new Error();
        err.setErrorCode(errorCode);
        err.setErrorMessage(errorMap.get(errorCode));
        pdResponse.setError(err);
        pdResponse.setTxnStatus(PLMConstants.RESPONSE_STATUS_ERROR);
        return objectFactory.createInternalParoleeDataResponse(pdResponse);
    }


    private SearchCriteriaInfo convertToSearchCriteria(InternalParoleeDataRequest pdRequest) throws ParseException {
        SearchCriteriaInfo scInfo = new SearchCriteriaInfo();
        scInfo.setCaseNumber(pdRequest.getCaseNumber());
        scInfo.setReason(pdRequest.getReason());
        scInfo.setUsername(pdRequest.getUsername());

        if (pdRequest.getIncludeStateWidePAL() != null) {
            scInfo.setIncludeStateWidePAL(pdRequest.getIncludeStateWidePAL().trim());
        }

        if (pdRequest.getResponseFieldsReturned() != null && !pdRequest.getResponseFieldsReturned().isEmpty()) {
            List<String> responseFieldsReturnList = new ArrayList<String>();
            String strResponseField = pdRequest.getResponseFieldsReturned();
            if (strResponseField.contains(",")) {
                String[] arrRespFiled = strResponseField.split(",");
                for (String respField : arrRespFiled) {
                    //logger.debug("respField :: " + respField);
                    responseFieldsReturnList.add(respField.trim());
                }
            } else {
                responseFieldsReturnList.add(strResponseField.trim());
            }
            scInfo.setResponseFieldsReturned(responseFieldsReturnList);
        }

        if (pdRequest.getSearchCriteria().getCDCNumber() != null && !pdRequest.getSearchCriteria().getCDCNumber().trim().isEmpty()) {
            scInfo.setCDCNumber(pdRequest.getSearchCriteria().getCDCNumber().trim());
            scInfo.setReturnSingleRecord(true);
        } else {
            if (pdRequest.getSearchCriteria().getCity() != null) {
                scInfo.setCity(pdRequest.getSearchCriteria().getCity().trim());
            }
            if (pdRequest.getSearchCriteria().getCounty() != null && !pdRequest.getSearchCriteria().getCounty().isEmpty()) {
                List<String> countyList = new ArrayList<String>();
                for (String county : pdRequest.getSearchCriteria().getCounty()) {
                    if (county != null && !county.isEmpty()) {
                        countyList.add(county);
                    }
                }
                scInfo.setCounty(countyList);
            }

            if (pdRequest.getSearchCriteria().getLastUpdateDate() != null) {
                String strDate = pdRequest.getSearchCriteria().getLastUpdateDate().trim();
                scInfo.setLastUpdateDate(strDate);

                LocalDate date = null;

                try {
                    date = LocalDate.parse(strDate, DEFAULT_DATE_FORMAT);
                } catch (DateTimeParseException e) {
                    date = LocalDate.parse(strDate, ENDECA_DATE_FORMAT);
                }
                scInfo.setLastUpdateDateInEndecaFormat(date.format(ENDECA_DATE_FORMAT));
            }

            if (pdRequest.getSearchCriteria().getIsParoleePAL() != null) {
                scInfo.setIsParoleePAL(pdRequest.getSearchCriteria().getIsParoleePAL().trim());
            }

            if (pdRequest.getSearchCriteria().getLastName() != null) {
                scInfo.setLastName(pdRequest.getSearchCriteria().getLastName().trim());
            }
            if (pdRequest.getSearchCriteria().getFirstName() != null) {
                scInfo.setFirstName(pdRequest.getSearchCriteria().getFirstName().trim());
            }
            if (pdRequest.getSearchCriteria().getMiddleName() != null) {
                scInfo.setMiddleName(pdRequest.getSearchCriteria().getMiddleName().trim());
            }
            if (pdRequest.getSearchCriteria().getAliasLastName() != null) {
                scInfo.setAliasLastName(pdRequest.getSearchCriteria().getAliasLastName().trim());
            }
            if (pdRequest.getSearchCriteria().getAliasFirstName() != null) {
                scInfo.setAliasFirstName(pdRequest.getSearchCriteria().getAliasFirstName().trim());
            }
            if (pdRequest.getSearchCriteria().getMoniker() != null) {
                scInfo.setMoniker(pdRequest.getSearchCriteria().getMoniker().trim());
            }
            if (pdRequest.getSearchCriteria().getZip() != null) {
                scInfo.setZip(pdRequest.getSearchCriteria().getZip().trim());
            }
            if (pdRequest.getSearchCriteria().getBirthState() != null) {
                scInfo.setBirthState(pdRequest.getSearchCriteria().getBirthState().trim());
            }
            if (pdRequest.getSearchCriteria().getHeightInInches() != null) {
                scInfo.setHeightInInches(pdRequest.getSearchCriteria().getHeightInInches().trim());
            }
            if (pdRequest.getSearchCriteria().getWeight() != null) {
                scInfo.setWeight(pdRequest.getSearchCriteria().getWeight().trim());
            }
            if (pdRequest.getSearchCriteria().getSSN() != null) {
                scInfo.setSsn(pdRequest.getSearchCriteria().getSSN().trim());
            }
            if (pdRequest.getSearchCriteria().getCIINumber() != null) {
                scInfo.setCiiNumber(pdRequest.getSearchCriteria().getCIINumber().trim());
            }
            if (pdRequest.getSearchCriteria().getFBINumber() != null) {
                scInfo.setFbiNumber(pdRequest.getSearchCriteria().getFBINumber().trim());
            }
            if (pdRequest.getSearchCriteria().getLicensePlate() != null) {
                scInfo.setLicensePlate(pdRequest.getSearchCriteria().getLicensePlate().trim());
            }
            if (pdRequest.getSearchCriteria().getDateOfBirth() != null) {
                scInfo.setDateOfBirth(pdRequest.getSearchCriteria().getDateOfBirth().trim());
            }
            if (pdRequest.getSearchCriteria().getParoleeReleaseDate() != null) {
                if (pdRequest.getSearchCriteria().getParoleeReleaseDate().getFromDate() != null) {
                    scInfo.setParoleeReleaseFromDate(pdRequest.getSearchCriteria().getParoleeReleaseDate().getFromDate().trim());
                }
                if (pdRequest.getSearchCriteria().getParoleeReleaseDate().getToDate() != null) {
                    scInfo.setParoleeReleaseToDate(pdRequest.getSearchCriteria().getParoleeReleaseDate().getToDate().trim());
                }
            }
            if (pdRequest.getSearchCriteria().getVehicleYear() != null) {
                if (pdRequest.getSearchCriteria().getVehicleYear().getFromYear() != null) {
                    scInfo.setVehicleFromYear(pdRequest.getSearchCriteria().getVehicleYear().getFromYear().trim());
                }
                if (pdRequest.getSearchCriteria().getVehicleYear().getToYear() != null) {
                    scInfo.setVehicleToYear(pdRequest.getSearchCriteria().getVehicleYear().getToYear().trim());
                }
            }
            if (pdRequest.getSearchCriteria().getEthnicity() != null) {
                scInfo.setEthnicity(pdRequest.getSearchCriteria().getEthnicity().trim());
            }
            if (pdRequest.getSearchCriteria().getHairColor() != null) {
                scInfo.setHairColor(pdRequest.getSearchCriteria().getHairColor().trim());
            }
            if (pdRequest.getSearchCriteria().getPC290SexOff() != null) {
                scInfo.setPc290SexOff(pdRequest.getSearchCriteria().getPC290SexOff().trim());
            }
            if (pdRequest.getSearchCriteria().getPC4571Arson() != null) {
                scInfo.setPc4571Arson(pdRequest.getSearchCriteria().getPC4571Arson().trim());
            }
            if (pdRequest.getSearchCriteria().getPC11590Drugs() != null) {
                scInfo.setPc11590Drugs(pdRequest.getSearchCriteria().getPC11590Drugs().trim());
            }
            if (pdRequest.getSearchCriteria().getPC30586FelonyViolation() != null) {
                scInfo.setPc30586FelonyViolation(pdRequest.getSearchCriteria().getPC30586FelonyViolation().trim());
            }
            if (pdRequest.getSearchCriteria().getSMT() != null) {
                if (pdRequest.getSearchCriteria().getSMT().getTypeOrLocation() != null) {
                    scInfo.setSmtType(pdRequest.getSearchCriteria().getSMT().getTypeOrLocation().trim());
                }
                if (pdRequest.getSearchCriteria().getSMT().getPictureOfTattoo() != null) {
                    scInfo.setSmtPicture(pdRequest.getSearchCriteria().getSMT().getPictureOfTattoo().trim());
                }
                if (pdRequest.getSearchCriteria().getSMT().getTextOfTattoo() != null) {
                    scInfo.setSmtText(pdRequest.getSearchCriteria().getSMT().getTextOfTattoo().trim());
                }
            }
            if (pdRequest.getSearchCriteria().getCommitmentOffense() != null) {
                scInfo.setCommitmentOffense(pdRequest.getSearchCriteria().getCommitmentOffense().trim());
            }
            if (pdRequest.getSearchCriteria().getCountyOfLLR() != null) {
                scInfo.setCountyOfLLR(pdRequest.getSearchCriteria().getCountyOfLLR().trim());
            }
            if (pdRequest.getSearchCriteria().getUnitCode() != null) {
                scInfo.setUnitCode(pdRequest.getSearchCriteria().getUnitCode().trim());
            }
        }
        return scInfo;
    }

    private void printRequest(JAXBElement<InternalParoleeDataRequest> req) {
        if (req != null) {
            InternalParoleeDataRequest pdr = req.getValue();
            if (pdr != null) {
                logger.debug("CaseNumber			:" + pdr.getCaseNumber());
                logger.debug("IncludeAttachment	:" + pdr.getIncludeAttachment());
                logger.debug("UserName			:" + pdr.getUsername());
                logger.debug("Reason				:" + pdr.getReason());
                if (pdr.getResponseFieldsReturned() != null) {
                    logger.debug("ResponseFieldsReturned	:" + pdr.getResponseFieldsReturned());
                }
                if (pdr.getIncludeStateWidePAL() != null) {
                    logger.debug("IncludeStateWidePAL:" + pdr.getIncludeStateWidePAL());
                }
                if (pdr.getSearchCriteria().getCDCNumber() != null) {
                    logger.debug("CDCNumber			:" + pdr.getSearchCriteria().getCDCNumber());
                }
                if (pdr.getSearchCriteria().getCity() != null) {
                    logger.debug("City				:" + pdr.getSearchCriteria().getCity());
                }
                if (pdr.getSearchCriteria().getCounty() != null) {
                    logger.debug("County				:" + printCounties(pdr.getSearchCriteria().getCounty()));
                }
                if (pdr.getSearchCriteria().getLastUpdateDate() != null) {
                    logger.debug("LastUpdDate		:" + pdr.getSearchCriteria().getLastUpdateDate());
                }
                if (pdr.getSearchCriteria().getIsParoleePAL() != null) {
                    logger.debug("IsParoleePAL		:" + pdr.getSearchCriteria().getIsParoleePAL());
                }
                if (pdr.getSearchCriteria().getLastName() != null) {
                    logger.debug("LastName			:" + pdr.getSearchCriteria().getLastName());
                }
                if (pdr.getSearchCriteria().getFirstName() != null) {
                    logger.debug("FirstName			:" + pdr.getSearchCriteria().getFirstName());
                }
                if (pdr.getSearchCriteria().getMiddleName() != null) {
                    logger.debug("MiddleName			:" + pdr.getSearchCriteria().getMiddleName());
                }
                if (pdr.getSearchCriteria().getAliasLastName() != null) {
                    logger.debug("AliasLastName		:" + pdr.getSearchCriteria().getAliasLastName());
                }
                if (pdr.getSearchCriteria().getAliasFirstName() != null) {
                    logger.debug("AliasFirstName		:" + pdr.getSearchCriteria().getAliasFirstName());
                }
                if (pdr.getSearchCriteria().getMoniker() != null) {
                    logger.debug("Moniker			:" + pdr.getSearchCriteria().getMoniker());
                }
                if (pdr.getSearchCriteria().getZip() != null) {
                    logger.debug("Zip				:" + pdr.getSearchCriteria().getZip());
                }
                if (pdr.getSearchCriteria().getBirthState() != null) {
                    logger.debug("BirthState			:" + pdr.getSearchCriteria().getBirthState());
                }
                if (pdr.getSearchCriteria().getHeightInInches() != null) {
                    logger.debug("HeightInInches		:" + pdr.getSearchCriteria().getHeightInInches());
                }
                if (pdr.getSearchCriteria().getWeight() != null) {
                    logger.debug("Weight				:" + pdr.getSearchCriteria().getWeight());
                }
                if (pdr.getSearchCriteria().getSSN() != null) {
                    logger.debug("SSN				:" + pdr.getSearchCriteria().getSSN());
                }
                if (pdr.getSearchCriteria().getCIINumber() != null) {
                    logger.debug("CIINumber			:" + pdr.getSearchCriteria().getCIINumber());
                }
                if (pdr.getSearchCriteria().getFBINumber() != null) {
                    logger.debug("FBINumber			:" + pdr.getSearchCriteria().getFBINumber());
                }
                if (pdr.getSearchCriteria().getLicensePlate() != null) {
                    logger.debug("LicensePlate		:" + pdr.getSearchCriteria().getLicensePlate());
                }
                if (pdr.getSearchCriteria().getDateOfBirth() != null) {
                    logger.debug("DateOfBirth		:" + pdr.getSearchCriteria().getDateOfBirth());
                }
                if (pdr.getSearchCriteria().getParoleeReleaseDate() != null) {
                    if (pdr.getSearchCriteria().getParoleeReleaseDate().getFromDate() != null) {
                        logger.debug("ParoleeReleaseFromDate:" + pdr.getSearchCriteria().getParoleeReleaseDate().getFromDate());
                    }
                    if (pdr.getSearchCriteria().getParoleeReleaseDate().getToDate() != null) {
                        logger.debug("ParoleeReleaseToDate	:" + pdr.getSearchCriteria().getParoleeReleaseDate().getToDate());
                    }
                }
                if (pdr.getSearchCriteria().getVehicleYear() != null) {
                    if (pdr.getSearchCriteria().getVehicleYear().getFromYear() != null) {
                        logger.debug("VehicleFromYear		:" + pdr.getSearchCriteria().getVehicleYear().getFromYear());
                    }
                    if (pdr.getSearchCriteria().getVehicleYear().getToYear() != null) {
                        logger.debug("VehicleToYear			:" + pdr.getSearchCriteria().getVehicleYear().getToYear());
                    }
                }
                if (pdr.getSearchCriteria().getEthnicity() != null) {
                    logger.debug("Ethnicity			:" + pdr.getSearchCriteria().getEthnicity());
                }
                if (pdr.getSearchCriteria().getHairColor() != null) {
                    logger.debug("HairColor			:" + pdr.getSearchCriteria().getHairColor());
                }
                if (pdr.getSearchCriteria().getPC290SexOff() != null) {
                    logger.debug("PC290SexOff		:" + pdr.getSearchCriteria().getPC290SexOff());
                }
                if (pdr.getSearchCriteria().getPC4571Arson() != null) {
                    logger.debug("PC4571Arson		:" + pdr.getSearchCriteria().getPC4571Arson());
                }
                if (pdr.getSearchCriteria().getPC11590Drugs() != null) {
                    logger.debug("PC11590Drugs		:" + pdr.getSearchCriteria().getPC11590Drugs());
                }
                if (pdr.getSearchCriteria().getPC30586FelonyViolation() != null) {
                    logger.debug("PC30586FelonyViolation	:" + pdr.getSearchCriteria().getPC30586FelonyViolation());
                }
                if (pdr.getSearchCriteria().getSMT() != null) {
                    if (pdr.getSearchCriteria().getSMT().getTypeOrLocation() != null) {
                        logger.debug("SMTTypeOrLocation:" + pdr.getSearchCriteria().getSMT().getTypeOrLocation());
                    }
                    if (pdr.getSearchCriteria().getSMT().getPictureOfTattoo() != null) {
                        logger.debug("SMTPictureOfTattoo:" + pdr.getSearchCriteria().getSMT().getPictureOfTattoo());
                    }
                    if (pdr.getSearchCriteria().getSMT().getTextOfTattoo() != null) {
                        logger.debug("SMTTextOfTattoo:" + pdr.getSearchCriteria().getSMT().getTextOfTattoo());
                    }
                }
                if (pdr.getSearchCriteria().getCommitmentOffense() != null) {
                    logger.debug("CommitmentOffense	:" + pdr.getSearchCriteria().getCommitmentOffense());
                }
                if (pdr.getSearchCriteria().getCountyOfLLR() != null) {
                    logger.debug("CountyOfLLR		:" + pdr.getSearchCriteria().getCountyOfLLR());
                }
                if (pdr.getSearchCriteria().getUnitCode() != null) {
                    logger.debug("UnitCode			:" + pdr.getSearchCriteria().getUnitCode());
                }
            }
        }
    }

    private String printCounties(List<String> tmpCounties) {
        StringBuilder counties = new StringBuilder();
        if (tmpCounties != null && tmpCounties.size() > 0) {
            for (String s : tmpCounties) {
                counties.append(", ").append(s);
            }
            counties = new StringBuilder(counties.substring(2));
        }
        return counties.toString();
    }
}