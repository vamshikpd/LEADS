package org.springframework.ws.soap.axiom;

//  package client2;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.transform.TransformerException;

import com.plm.ws.interceptors.AddHttpHeaderInterceptor;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.mime.Attachment;
import org.springframework.ws.soap.client.SoapFaultClientException;

import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.CommonsHttpConnection;
import plm.ws.mtom.auth.DataAttachment;
import plm.ws.mtom.auth.SearchCriteria;

import com.plm.util.PLMUtil;
import com.plm.ws.endpoint.RedirectEndPoint;

/**
 * Simple client that demonstartes MTOM by invoking <code>StoreImage</code> and
 * <code>LoadImage</code> using a WebServiceTemplate and Axiom.
 * 
 * @author Tushar
 */
public class AxiomMtomClient extends WebServiceGatewaySupport {

	public static final String DATA_NAME_SPACE = "http://www.plm/ws/mtom/data";
	public static final String PHOTO_NAME_SPACE = "http://www.plm/ws/mtom/photo";
	public static final String DATA_CHECK_NAME_SPACE = "http://www.plm/ws/mtom/datacheck";
	public static final String ELE_PAROLEE_DATA_REQUEST = "InternalParoleeDataRequest";
	public static final String ELE_PAROLEE_PHOTO_REQUEST = "InternalParoleePhotoRequest";
	public static final String ELE_PAROLEE_DATA_CHECK_REQUEST = "InternalParoleeDataCheckRequest";
	public static final String ELE_CASE_NUMBER = "CaseNumber";
	public static final String ELE_REASON = "Reason";
	public static final String ELE_DATA_TYPE = "DataType";
	public static final String ELE_RESPONSE_FIELDS_RETURNED = "ResponseFieldsReturned";
	public static final String ELE_SEARCH_CRITERIA = "SearchCriteria";
	public static final String ELE_LAST_UPDATE_DATE = "LastUpdateDate";
	public static final String ELE_COUNTY = "County";
	public static final String ELE_CITY = "City";
	public static final String ELE_CDC_NUMBER = "CDCNumber";
	public static final String ELE_INC_ATTACH = "IncludeAttachment";
	public static final String ELE_USER_NAME = "Username";
	public static final String SERVICE_TYPE_DATA = "data";
	public static final String SERVICE_TYPE_PHOTO = "photo";
	public static final String SERVICE_TYPE_DATA_CHECK = "datacheck";
	public static final String ELE_INCLUDE_STATEWIDE_PAL = "IncludeStateWidePAL";
	
	public static final String ELE_LASTNAME = "LastName";
	public static final String ELE_FIRSTNAME = "FirstName";
	public static final String ELE_MIDDLENAME = "MiddleName";
	public static final String ELE_ALIAS_LASTNAME = "AliasLastName";
	public static final String ELE_ALIAS_FIRSTNAME = "AliasFirstName";
	public static final String ELE_MONIKER = "Moniker";
	public static final String ELE_ZIP = "Zip";
	public static final String ELE_BIRTHSTATE = "BirthState";
	public static final String ELE_HEIGHT_IN_INCHES = "HeightInInches";
	public static final String ELE_WEIGHT = "Weight";
	public static final String ELE_SSN = "SSN";
	public static final String ELE_CII_NUMBER = "CIINumber";
	public static final String ELE_FBI_NUMBER = "FBINumber";
	public static final String ELE_LICENSE_PLATE = "LicensePlate";
	public static final String ELE_DATE_OF_BIRTH = "DateOfBirth";
	public static final String ELE_PAROLEE_RELEASE_DATE = "ParoleeReleaseDate";
	public static final String ELE_PAROLEE_RELEASE_FROM_DATE = "FromDate";
	public static final String ELE_PAROLEE_RELEASE_TO_DATE = "ToDate";
	public static final String ELE_VEHICLE_YEAR = "VehicleYear";
	public static final String ELE_VEHICLE_FROM_YEAR = "FromYear";
	public static final String ELE_VEHICLE_TO_YEAR = "ToYear";
	public static final String ELE_ETHNICITY = "Ethnicity";
	public static final String ELE_HAIR_COLOR = "HairColor";
	public static final String ELE_PC290_SEXOFF = "PC290SexOff";
	public static final String ELE_PC4571_ARSON = "PC4571Arson";
	public static final String ELE_PC11590_DRUGS = "PC11590Drugs";
	public static final String ELE_PC30586_FELONYVIOLATION = "PC30586FelonyViolation";
	public static final String ELE_SMT = "SMT";
	public static final String ELE_SMT_TYPE_OR_LOCATION = "TypeOrLocation";
	public static final String ELE_SMT_PICTURE_OF_TATTOO = "PictureOfTattoo";
	public static final String ELE_SMT_TEXT_OF_TATTOO = "TextOfTattoo";
	public static final String ELE_COMMITMENTOFFENSE = "CommitmentOffense";
	public static final String ELE_COUNTYOFLLR = "CountyOfLLR";
	public static final String ELE_UNITCODE = "UnitCode";
	public static final String ELE_IS_PAROLEE_PAL = "IsParoleePAL";
	
	
	private static final Log logger = LogFactory.getLog(AxiomMtomClient.class);

    private static final AddHttpHeaderInterceptor ADD_HTTP_HEADER_INTERCEPTOR = new AddHttpHeaderInterceptor();

	public AxiomMtomClient(AxiomSoapMessageFactory messageFactory) {
	    super(messageFactory);

        ClientInterceptor[] interceptors = new ClientInterceptor[]{ADD_HTTP_HEADER_INTERCEPTOR};
        this.getWebServiceTemplate().setInterceptors(interceptors);

		/*WebServiceTemplate wsTemplate = this.getWebServiceTemplate();

        for (WebServiceMessageSender sender : wsTemplate.getMessageSenders()) {
			try {
				HttpComponentsMessageSender httpSender = (HttpComponentsMessageSender) sender;
				httpSender.setReadTimeout(60000);
				httpSender.setConnectionTimeout(60000);

			} catch (ClassCastException | NumberFormatException cex) {
				logger.warn("Cannot set WS timeout: " + cex.getMessage());
			}
		}*/
	}

	//public AxiomSoapMessage doIt(String sLastUpdateDate,String sGroupCode, String userName, String caseNumber,String reason, List<String> sCounties, String sCity, String sCDCNumber,String serviceType) {
	public AxiomSoapMessage doIt(SearchCriteria searchCriteria, String userName, String caseNumber,String reason, String serviceType, String responseFields, String includeStateWidePAL) {
		AxiomSoapMessage body = null;
		try {
			//logger.debug("servicetype: " + serviceType);
			body = load(searchCriteria, userName, caseNumber, reason, serviceType, responseFields, includeStateWidePAL);
		} catch (SoapFaultClientException e) {
			logger.error(PLMUtil.getStackTrace(e));
		}
		return body;
	}

	private AxiomSoapMessage load(
			final SearchCriteria searchCriteria,
			final String userName, 
			final String caseNumber,  
			final String sReason,
			final String serviceType,
			final String responseFields,
			final String includeStateWidePAL
		) {

		WebServiceMessageCallback callBack = new WebServiceMessageCallback() {
			public void doWithMessage(WebServiceMessage message)
					throws IOException, TransformerException {

				SOAPMessage axiomMessage = ((AxiomSoapMessage) message).getAxiomMessage();
				SOAPFactory factory = (SOAPFactory) axiomMessage.getOMFactory();
				SOAPBody body = axiomMessage.getSOAPEnvelope().getBody();

				// Create Data or Photo or DataCheck Namespace
				OMNamespace ns = null;
				
				if(serviceType.equalsIgnoreCase(SERVICE_TYPE_DATA_CHECK)){
					ns = factory.createOMNamespace(DATA_CHECK_NAME_SPACE, "sch");
				} else if(serviceType.equalsIgnoreCase(SERVICE_TYPE_PHOTO)){
					ns = factory.createOMNamespace(PHOTO_NAME_SPACE, "sch");
				}else{
					ns = factory.createOMNamespace(DATA_NAME_SPACE, "sch");
				}

				// Create ParoleeDataRequest
				OMElement paroleeReq = null;
				if(serviceType.equalsIgnoreCase(SERVICE_TYPE_DATA_CHECK)){
					paroleeReq =factory.createOMElement(ELE_PAROLEE_DATA_CHECK_REQUEST, ns);
				} else if(serviceType.equalsIgnoreCase(SERVICE_TYPE_PHOTO)){
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
				if(!serviceType.equalsIgnoreCase(SERVICE_TYPE_DATA_CHECK)){
					OMElement omIncAttach = factory.createOMElement(ELE_INC_ATTACH,ns);
					omIncAttach.setText("Y");
					paroleeReq.addChild(omIncAttach);
				}
				
				// Create Response Fields Returned
				if(responseFields != null && !responseFields.isEmpty()){
					OMElement omResponseField = factory.createOMElement(ELE_RESPONSE_FIELDS_RETURNED, ns);
					omResponseField.setText(responseFields);
					paroleeReq.addChild(omResponseField);
				}
				
				// Create includeStateWidePAL
				OMElement omIncludeStateWidePAL = factory.createOMElement(ELE_INCLUDE_STATEWIDE_PAL,ns);
				omIncludeStateWidePAL.setText(includeStateWidePAL);
				paroleeReq.addChild(omIncludeStateWidePAL);
				
				// Create SearchCriteria
				OMElement searchCriteriaEle = factory.createOMElement(ELE_SEARCH_CRITERIA, ns);

				// Create LastUpdateDate
				if(searchCriteria.getLastUpdateDate()!=null 
						&& searchCriteria.getLastUpdateDate().trim().length()>0
						&& !searchCriteria.getLastUpdateDate().trim().equalsIgnoreCase("null")){
					OMElement lastUpdateDate = factory.createOMElement(ELE_LAST_UPDATE_DATE, ns);
					lastUpdateDate.setText(searchCriteria.getLastUpdateDate());
					searchCriteriaEle.addChild(lastUpdateDate);
				}			
				
				// Create CDCNumber
				if(searchCriteria.getCDCNumber()!=null 
						&& searchCriteria.getCDCNumber().trim().length()>0
						&& !searchCriteria.getCDCNumber().trim().equalsIgnoreCase("null")){
					OMElement CDCNumber = factory.createOMElement(ELE_CDC_NUMBER,ns);
					CDCNumber.setText(searchCriteria.getCDCNumber().trim());
					searchCriteriaEle.addChild(CDCNumber);
				}else{
					// Create county
					if(searchCriteria.getCounty() != null && !searchCriteria.getCounty().trim().isEmpty()){
						String strCounty = searchCriteria.getCounty();
						if(strCounty.contains(",")){
							String[] arrCounty = strCounty.split(",");
							for(String county : arrCounty){
								OMElement eleCounty = factory.createOMElement(ELE_COUNTY,ns);
								eleCounty.setText(county);
								searchCriteriaEle.addChild(eleCounty);
							}
						} else {
							OMElement eleCounty = factory.createOMElement(ELE_COUNTY,ns);
							eleCounty.setText(strCounty);
							searchCriteriaEle.addChild(eleCounty);
						}
					}
					// Create city
					if(searchCriteria.getCity()!=null
							&& searchCriteria.getCity().trim().length()>0
							&& !searchCriteria.getCity().trim().equalsIgnoreCase("null")){
						OMElement city = factory.createOMElement(ELE_CITY,ns);
						city.setText(searchCriteria.getCity());
						searchCriteriaEle.addChild(city);
					}
					//Create Last Name
					if(searchCriteria.getLastName()!= null && searchCriteria.getLastName().trim().length() > 0
							&& !searchCriteria.getLastName().trim().equalsIgnoreCase("null")){
						OMElement lastname = factory.createOMElement(ELE_LASTNAME, ns);
						lastname.setText(searchCriteria.getLastName());
						searchCriteriaEle.addChild(lastname);
					}
					//Create FirstName
					if(searchCriteria.getFirstName()!= null && searchCriteria.getFirstName().trim().length() > 0
							&& !searchCriteria.getFirstName().trim().equalsIgnoreCase("null")){
						OMElement firstname = factory.createOMElement(ELE_FIRSTNAME, ns);
						firstname.setText(searchCriteria.getFirstName());
						searchCriteriaEle.addChild(firstname);
					}
					//Create Middle Name
					if(searchCriteria.getMiddleName() != null && searchCriteria.getMiddleName().trim().length() > 0
							&& !searchCriteria.getMiddleName().trim().equalsIgnoreCase("null")){
						OMElement middlename = factory.createOMElement(ELE_MIDDLENAME, ns);
						middlename.setText(searchCriteria.getMiddleName());
						searchCriteriaEle.addChild(middlename);
					}
					//Create Alias Last Name
					if(searchCriteria.getAliasLastName() != null && searchCriteria.getAliasLastName().trim().length() > 0
							&& !searchCriteria.getAliasLastName().trim().equalsIgnoreCase("null")){
						OMElement aliaslastname = factory.createOMElement(ELE_ALIAS_LASTNAME, ns);
						aliaslastname.setText(searchCriteria.getAliasLastName());
						searchCriteriaEle.addChild(aliaslastname);
					}
					//Create Alias First Name
					if(searchCriteria.getAliasFirstName() != null && searchCriteria.getAliasFirstName().trim().length() > 0
							&& !searchCriteria.getAliasFirstName().trim().equalsIgnoreCase("null")){
						OMElement aliasfirstname = factory.createOMElement(ELE_ALIAS_FIRSTNAME, ns);
						aliasfirstname.setText(searchCriteria.getAliasFirstName());
						searchCriteriaEle.addChild(aliasfirstname);
					}
					//Create Moniker
					if(searchCriteria.getMoniker() != null && searchCriteria.getMoniker().trim().length() > 0
							&& !searchCriteria.getMoniker().trim().equalsIgnoreCase("null")){
						OMElement moniker = factory.createOMElement(ELE_MONIKER, ns);
						moniker.setText(searchCriteria.getMoniker());
						searchCriteriaEle.addChild(moniker);
					}
					//Create Zip
					if(searchCriteria.getZip() != null && searchCriteria.getZip().trim().length() > 0
							&& !searchCriteria.getZip().trim().equalsIgnoreCase("null")){
						OMElement zip = factory.createOMElement(ELE_ZIP, ns);
						zip.setText(searchCriteria.getZip());
						searchCriteriaEle.addChild(zip);
					}
					//Create Birth State
					if(searchCriteria.getBirthState() != null && searchCriteria.getBirthState().trim().length() > 0
							&& !searchCriteria.getBirthState().trim().equalsIgnoreCase("null")){
						OMElement birthState = factory.createOMElement(ELE_BIRTHSTATE, ns);
						birthState.setText(searchCriteria.getBirthState());
						searchCriteriaEle.addChild(birthState);
					}
					//Create Height In CM
					if(searchCriteria.getHeightInInches() != null && searchCriteria.getHeightInInches().trim().length() > 0
							&& !searchCriteria.getHeightInInches().trim().equalsIgnoreCase("null")){
						OMElement height = factory.createOMElement(ELE_HEIGHT_IN_INCHES, ns);
						height.setText(searchCriteria.getHeightInInches());
						searchCriteriaEle.addChild(height);
					}
					//Create Weight
					if(searchCriteria.getWeight() != null && searchCriteria.getWeight().trim().length() > 0
							&& !searchCriteria.getWeight().trim().equalsIgnoreCase("null")){
						OMElement weight = factory.createOMElement(ELE_WEIGHT, ns);
						weight.setText(searchCriteria.getWeight());
						searchCriteriaEle.addChild(weight);
					}
					//Create SSN
					if(searchCriteria.getSSN() != null && searchCriteria.getSSN().trim().length() > 0
							&& !searchCriteria.getSSN().trim().equalsIgnoreCase("null")){
						OMElement ssn = factory.createOMElement(ELE_SSN, ns);
						ssn.setText(searchCriteria.getSSN());
						searchCriteriaEle.addChild(ssn);
					}
					//Create CII Number
					if(searchCriteria.getCIINumber() != null && searchCriteria.getCIINumber().trim().length() > 0
							&& !searchCriteria.getCIINumber().trim().equalsIgnoreCase("null")){
						OMElement ciiNumber = factory.createOMElement(ELE_CII_NUMBER, ns);
						ciiNumber.setText(searchCriteria.getCIINumber());
						searchCriteriaEle.addChild(ciiNumber);
					}
					//Create FBI Number
					if(searchCriteria.getFBINumber() != null && searchCriteria.getFBINumber().trim().length() > 0
							&& !searchCriteria.getFBINumber().trim().equalsIgnoreCase("null")){
						OMElement fbiNumber = factory.createOMElement(ELE_FBI_NUMBER, ns);
						fbiNumber.setText(searchCriteria.getFBINumber());
						searchCriteriaEle.addChild(fbiNumber);
					}
					//Create License Plate
					if(searchCriteria.getLicensePlate() != null && searchCriteria.getLicensePlate().trim().length() > 0
							&& !searchCriteria.getLicensePlate().trim().equalsIgnoreCase("null")){
						OMElement licensePlate = factory.createOMElement(ELE_LICENSE_PLATE, ns);
						licensePlate.setText(searchCriteria.getLicensePlate());
						searchCriteriaEle.addChild(licensePlate);
					}
					//Create Date Of Birth
					if(searchCriteria.getDateOfBirth() != null && searchCriteria.getDateOfBirth().trim().length() > 0
							&& !searchCriteria.getDateOfBirth().trim().equalsIgnoreCase("null")){
						OMElement dob = factory.createOMElement(ELE_DATE_OF_BIRTH, ns);
						dob.setText(searchCriteria.getDateOfBirth());
						searchCriteriaEle.addChild(dob);
					}
					//Create Parolee Release Date
					if(searchCriteria.getParoleeReleaseDate() != null){
						OMElement paroleereleasedate = factory.createOMElement(ELE_PAROLEE_RELEASE_DATE, ns);
						if(searchCriteria.getParoleeReleaseDate().getFromDate() != null && searchCriteria.getParoleeReleaseDate().getFromDate().trim().length() > 0
								&& !searchCriteria.getParoleeReleaseDate().getFromDate().trim().equalsIgnoreCase("null")){
							OMElement fromDate = factory.createOMElement(ELE_PAROLEE_RELEASE_FROM_DATE, ns);
							fromDate.setText(searchCriteria.getParoleeReleaseDate().getFromDate());
							paroleereleasedate.addChild(fromDate);
						}
						if(searchCriteria.getParoleeReleaseDate().getToDate() != null && searchCriteria.getParoleeReleaseDate().getToDate().trim().length() > 0
								&& !searchCriteria.getParoleeReleaseDate().getToDate().trim().equalsIgnoreCase("null")){
							OMElement toDate = factory.createOMElement(ELE_PAROLEE_RELEASE_TO_DATE, ns);
							toDate.setText(searchCriteria.getParoleeReleaseDate().getToDate());
							paroleereleasedate.addChild(toDate);
						}
						searchCriteriaEle.addChild(paroleereleasedate);
					}
					//Create Vehicle Year
					if(searchCriteria.getVehicleYear() != null){
						OMElement vehicleYear = factory.createOMElement(ELE_VEHICLE_YEAR, ns);
						if(searchCriteria.getVehicleYear().getFromYear() != null && searchCriteria.getVehicleYear().getFromYear().trim().length() > 0
								&& !searchCriteria.getVehicleYear().getFromYear().trim().equalsIgnoreCase("null")){
							OMElement fromYear = factory.createOMElement(ELE_VEHICLE_FROM_YEAR, ns);
							fromYear.setText(searchCriteria.getVehicleYear().getFromYear());
							vehicleYear.addChild(fromYear);
						}
						if(searchCriteria.getVehicleYear().getToYear() != null && searchCriteria.getVehicleYear().getToYear().trim().length() > 0
								&& !searchCriteria.getVehicleYear().getToYear().trim().equalsIgnoreCase("null")){
							OMElement toYear = factory.createOMElement(ELE_VEHICLE_TO_YEAR, ns);
							toYear.setText(searchCriteria.getVehicleYear().getToYear());
							vehicleYear.addChild(toYear);
						}
						searchCriteriaEle.addChild(vehicleYear);
					}
					//Create Ethnicity
					if(searchCriteria.getEthnicity() != null && searchCriteria.getEthnicity().trim().length() > 0
							&& !searchCriteria.getEthnicity().trim().equalsIgnoreCase("null")){
						OMElement ethinicity = factory.createOMElement(ELE_ETHNICITY, ns);
						ethinicity.setText(searchCriteria.getEthnicity());
						searchCriteriaEle.addChild(ethinicity);
					}
					//Create HairColor
					if(searchCriteria.getHairColor() != null && searchCriteria.getHairColor().trim().length() > 0
							&& !searchCriteria.getHairColor().trim().equalsIgnoreCase("null")){
						OMElement hairColor = factory.createOMElement(ELE_HAIR_COLOR, ns);
						hairColor.setText(searchCriteria.getHairColor());
						searchCriteriaEle.addChild(hairColor);
					}
					//Create PC290 SexOff
					if(searchCriteria.getPC290SexOff() != null && searchCriteria.getPC290SexOff().trim().length() > 0
							&& !searchCriteria.getPC290SexOff().trim().equalsIgnoreCase("null")){
						OMElement pc290 = factory.createOMElement(ELE_PC290_SEXOFF, ns);
						pc290.setText(searchCriteria.getPC290SexOff());
						searchCriteriaEle.addChild(pc290);
					}
					//Create PC4571 Arson
					if(searchCriteria.getPC4571Arson() != null && searchCriteria.getPC4571Arson().trim().length() > 0
							&& !searchCriteria.getPC4571Arson().trim().equalsIgnoreCase("null")){
						OMElement pc457 = factory.createOMElement(ELE_PC4571_ARSON, ns);
						pc457.setText(searchCriteria.getPC4571Arson());
						searchCriteriaEle.addChild(pc457);
					}
					//Create PC11590 Drugs
					if(searchCriteria.getPC11590Drugs() != null && searchCriteria.getPC11590Drugs().trim().length() > 0
							&& !searchCriteria.getPC11590Drugs().trim().equalsIgnoreCase("null")){
						OMElement pc11590 = factory.createOMElement(ELE_PC11590_DRUGS, ns);
						pc11590.setText(searchCriteria.getPC11590Drugs());
						searchCriteriaEle.addChild(pc11590);
					}
					//Create PC30586 Felony Violation
					if(searchCriteria.getPC30586FelonyViolation() != null && searchCriteria.getPC30586FelonyViolation().trim().length() > 0
							&& !searchCriteria.getPC30586FelonyViolation().trim().equalsIgnoreCase("null")){
						OMElement pc3058 = factory.createOMElement(ELE_PC30586_FELONYVIOLATION, ns);
						pc3058.setText(searchCriteria.getPC30586FelonyViolation());
						searchCriteriaEle.addChild(pc3058);
					}
					//Create SMT
					if(searchCriteria.getSMT() != null){
						OMElement smt = factory.createOMElement(ELE_SMT, ns);
						if(searchCriteria.getSMT().getTypeOrLocation() != null && searchCriteria.getSMT().getTypeOrLocation().trim().length() > 0
								&& !searchCriteria.getSMT().getTypeOrLocation().trim().equalsIgnoreCase("null")){
							OMElement type = factory.createOMElement(ELE_SMT_TYPE_OR_LOCATION, ns);
							type.setText(searchCriteria.getSMT().getTypeOrLocation());
							smt.addChild(type);
						}
						if(searchCriteria.getSMT().getPictureOfTattoo() != null && searchCriteria.getSMT().getPictureOfTattoo().trim().length() > 0
								&& !searchCriteria.getSMT().getPictureOfTattoo().trim().equalsIgnoreCase("null")){
							OMElement picture = factory.createOMElement(ELE_SMT_PICTURE_OF_TATTOO, ns);
							picture.setText(searchCriteria.getSMT().getPictureOfTattoo());
							smt.addChild(picture);
						}
						if(searchCriteria.getSMT().getTextOfTattoo() != null && searchCriteria.getSMT().getTextOfTattoo().trim().length() > 0
								&& !searchCriteria.getSMT().getTextOfTattoo().trim().equalsIgnoreCase("null")){
							OMElement text = factory.createOMElement(ELE_SMT_TEXT_OF_TATTOO, ns);
							text.setText(searchCriteria.getSMT().getTextOfTattoo());
							smt.addChild(text);
						}
						searchCriteriaEle.addChild(smt);
					}
					//Create Commitment Offense
					if(searchCriteria.getCommitmentOffense() != null && searchCriteria.getCommitmentOffense().trim().length() > 0
							&& !searchCriteria.getCommitmentOffense().trim().equalsIgnoreCase("null")){
						OMElement commitmentOffense = factory.createOMElement(ELE_COMMITMENTOFFENSE, ns);
						commitmentOffense.setText(searchCriteria.getCommitmentOffense());
						searchCriteriaEle.addChild(commitmentOffense);
					}
					//Create County Of Last Legal Residence
					if(searchCriteria.getCountyOfLLR() != null && searchCriteria.getCountyOfLLR().trim().length() > 0
							&& !searchCriteria.getCountyOfLLR().trim().equalsIgnoreCase("null")){
						OMElement countyOfLLR = factory.createOMElement(ELE_COUNTYOFLLR, ns);
						countyOfLLR.setText(searchCriteria.getCountyOfLLR());
						searchCriteriaEle.addChild(countyOfLLR);
					}
					//Create Unit Code
					if(searchCriteria.getUnitCode() != null && searchCriteria.getUnitCode().trim().length() > 0
							&& !searchCriteria.getUnitCode().trim().equalsIgnoreCase("null")){
						OMElement unitCode = factory.createOMElement(ELE_UNITCODE, ns);
						unitCode.setText(searchCriteria.getUnitCode());
						searchCriteriaEle.addChild(unitCode);
					}
					//Create isParoleePAL
					if(searchCriteria.getIsParoleePAL() != null && searchCriteria.getIsParoleePAL().trim().length() > 0
							&& !searchCriteria.getIsParoleePAL().trim().equalsIgnoreCase("null")){
						OMElement isParoleePAL = factory.createOMElement(ELE_IS_PAROLEE_PAL, ns);
						isParoleePAL.setText(searchCriteria.getIsParoleePAL());
						searchCriteriaEle.addChild(isParoleePAL);
					}
				}

				paroleeReq.addChild(searchCriteriaEle);

				body.addChild(paroleeReq);
				logger.debug("Body :: " + body.toString());
			}
		};

		WebServiceMessageExtractor msgExtractor = new WebServiceMessageExtractor() {
			@SuppressWarnings("unchecked")
			public Object extractData(WebServiceMessage message)
					throws IOException, TransformerException {
				AxiomSoapMessage axiomMessage = (AxiomSoapMessage) message;
				Iterator<Attachment> iter = axiomMessage.getAttachments();
				while(iter.hasNext()){
					Attachment att = iter.next();
					logger.debug("ContentId:" + att.getContentId());
					logger.debug("ContentType:" + att.getContentType());
					logger.debug("ContentSize:" + att.getSize());
				}
				return axiomMessage;
			}
		};

		Object image = getWebServiceTemplate().sendAndReceive(callBack, msgExtractor);
		AxiomSoapMessage soap11 = (AxiomSoapMessage) image;
		
		return soap11;

	}
}
