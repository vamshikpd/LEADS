package com.plm.util;

import java.util.HashMap;

public class PLMJasperEndecaUtil {
	private static HashMap<String, String> hmEndecaProperties = new HashMap<String, String>();
	static{
		init();
	}
	private static void init() {
		hmEndecaProperties.put("AliasInfo","Alias Info");
		hmEndecaProperties.put("MonikerInfo","Moniker Info");
		hmEndecaProperties.put("OffenseInformation","Offense Information");
		hmEndecaProperties.put("SMTInformation","SMT Information");
		hmEndecaProperties.put("SpecialConditionInformation","Special Condition Information");
		hmEndecaProperties.put("VehicleInformation","Vehicle Information");
		hmEndecaProperties.put("ActionDateDisplay","Action Date Display");
		hmEndecaProperties.put("ActionDateSearch","Action Date Search");
		hmEndecaProperties.put("ActionType","Action Type");
		hmEndecaProperties.put("AddressChangedDateDisplay","Address Changed Date Display");
		hmEndecaProperties.put("AddressChangedDateSearch","Address Changed Date Search");
		hmEndecaProperties.put("AddressEffectiveDate","Address Effective Date");
		hmEndecaProperties.put("AddressLastChangeDate","Address Last Change Date");
		hmEndecaProperties.put("ParoleeAge","Parolee Age ( 3 yrs)");
		hmEndecaProperties.put("AgentCode","Agent Code");
		hmEndecaProperties.put("AgentEmail","Agent Email");
		hmEndecaProperties.put("AgentName","Agent Name");
		hmEndecaProperties.put("AgentPhone","Agent Phone");
		hmEndecaProperties.put("AliasFirstName","Alias First Name");
		hmEndecaProperties.put("AliasLastName","Alias Last Name");
		hmEndecaProperties.put("AliasMiddleName","Alias Middle Name");
		hmEndecaProperties.put("ANTREQ","ANT REQ");
		hmEndecaProperties.put("BirthDateDisplay","Birth Date Display");
		hmEndecaProperties.put("BirthDateSearch","Birth Date Search");
		hmEndecaProperties.put("BirthState","Birth State");
		hmEndecaProperties.put("BirthStateCode","Birth State Code");
		hmEndecaProperties.put("BirthStateName","Birth State Name");
		hmEndecaProperties.put("BirthYr","Birth_Yr");
		hmEndecaProperties.put("Careof","Care of (live with)");
		hmEndecaProperties.put("CDCNumber","CDC Number");
		hmEndecaProperties.put("CIINumber","CII Number");
		hmEndecaProperties.put("City","City");
		hmEndecaProperties.put("ClassificationCode","Classification Code");
		hmEndecaProperties.put("ClassificationDescription","Classification Description");
		hmEndecaProperties.put("Comments","Comments");
		hmEndecaProperties.put("CommitmentOffense","Commitment Offense");
		hmEndecaProperties.put("CountyCode","County Code");
		hmEndecaProperties.put("CountyCommit","County Commit");
		hmEndecaProperties.put("CountyName","County Name");
		hmEndecaProperties.put("ControlDischargDate","Control Discharg Date");
		hmEndecaProperties.put("DriverLicenseNumber","Driver License Number");
		hmEndecaProperties.put("DischargedDate","Discharged Date");
		hmEndecaProperties.put("Ethnicity","Ethnicity");
		hmEndecaProperties.put("EyeColor","Eye Color");
		hmEndecaProperties.put("Eyecolor","Eyecolor");
		hmEndecaProperties.put("EyecolorCd","Eyecolor Cd");
		hmEndecaProperties.put("FBINumber","FBI Number");
		hmEndecaProperties.put("FirstName","First Name");
		hmEndecaProperties.put("FullPhone","Full_Phone");
		hmEndecaProperties.put("geocode","geocode");
		hmEndecaProperties.put("Gender","Gender");
		hmEndecaProperties.put("GPS","GPS");
		hmEndecaProperties.put("GroupCode","Group Code");
		hmEndecaProperties.put("HairColor","Hair Color");
		hmEndecaProperties.put("Haircolor","Haircolor");
		hmEndecaProperties.put("HaircolorCd","Haircolor Cd");
		hmEndecaProperties.put("HasAddressChanged","Has Address Changed");
		hmEndecaProperties.put("PHeight","P_Height");
		hmEndecaProperties.put("Height","Height ( 2 inches)");
		hmEndecaProperties.put("HeightFeet","Height Feet");
		hmEndecaProperties.put("HeightInches","Height Inches");
		hmEndecaProperties.put("HRSO","HRSO");
		hmEndecaProperties.put("HSDATE","HS DATE");
		hmEndecaProperties.put("HSREQ","HS REQ");
		hmEndecaProperties.put("LastName","Last Name");
		hmEndecaProperties.put("Map","Map");
		hmEndecaProperties.put("MiddleName","Middle Name");
		hmEndecaProperties.put("MessagePhone","Message Phone");
		hmEndecaProperties.put("NoAlcohol","No Alcohol");
		hmEndecaProperties.put("OffenseCode","Offense Code");
		hmEndecaProperties.put("ParoleDataChangeDate","Parole Data Change Date");
		hmEndecaProperties.put("ParoleDateDisplay","Parole Date Display");
		hmEndecaProperties.put("ParoleDateSearch","Parole_Date_Search");
		hmEndecaProperties.put("ParoleLastChgDate","Parole Last Chg Date");
		hmEndecaProperties.put("ParoleState","Parole State");
		hmEndecaProperties.put("ParoleStatus","Parole Status");
		hmEndecaProperties.put("ParoleUnit","Parole Unit");
		hmEndecaProperties.put("ParoleYear","Parole Year");
		hmEndecaProperties.put("ParoleYr","Parole Yr");
		hmEndecaProperties.put("PC290DATE","PC 290 DATE");
		hmEndecaProperties.put("PC290REQ","PC 290 REQ");
		hmEndecaProperties.put("PC3058DATE","PC 3058 DATE");
		hmEndecaProperties.put("PC3058REQ","PC 3058 REQ");
		hmEndecaProperties.put("PC457DATE","PC 457 DATE");
		hmEndecaProperties.put("PC457REQ","PC 457 REQ");
		hmEndecaProperties.put("POCREQ","POC REQ");
		hmEndecaProperties.put("PrimaryDimension","Primary_Dimension");
		hmEndecaProperties.put("ProblemArea-Alcohol","Problem Area - Alcohol");
		hmEndecaProperties.put("ProblemArea-Assault","Problem Area - Assault");
		hmEndecaProperties.put("ProblemArea-Narcotic","Problem Area - Narcotic");
		hmEndecaProperties.put("ProblemArea-Other","Problem Area - Other");
		hmEndecaProperties.put("ProblemArea-Sex","Problem Area - Sex");
		hmEndecaProperties.put("Race","Race");
		hmEndecaProperties.put("RaceCd","Race Cd");
		hmEndecaProperties.put("ResCity","Res City");
		hmEndecaProperties.put("ResCounty","Res County");
		hmEndecaProperties.put("RevocationReleaseDate","Revocation Release Date");
		hmEndecaProperties.put("RevocationReleaseDateSearch","Revocation_Release_Date_Search");
		hmEndecaProperties.put("SMTCd","SMT Cd");
		hmEndecaProperties.put("SMTCode","SMT Code");
		hmEndecaProperties.put("SMTDescription","SMT Description");
		hmEndecaProperties.put("SMTPicture","SMT Picture");
		hmEndecaProperties.put("SMTText","SMT Text");
		hmEndecaProperties.put("SupervisionLevel","Supervision Level");
		hmEndecaProperties.put("SortedEmployerInformation","Sorted Employer Information");
		hmEndecaProperties.put("SortedPrevAddress","Sorted Prev Address");
		hmEndecaProperties.put("SSANumber","SSA Number");
		hmEndecaProperties.put("StateCode","State Code");
		hmEndecaProperties.put("Status","Status");
		hmEndecaProperties.put("StatusCd","Status Cd");
		hmEndecaProperties.put("Street","Street");
		hmEndecaProperties.put("Sex","Sex");
		hmEndecaProperties.put("TattooPictText","Tattoo_Pict_Text");
		hmEndecaProperties.put("UnitCode","Unit Code");
		hmEndecaProperties.put("UnitName","Unit Name");
		hmEndecaProperties.put("USINSNumber","USINS Number");
		hmEndecaProperties.put("VehicleColor1","Vehicle Color1");
		hmEndecaProperties.put("VehicleColor2","Vehicle Color2");
		hmEndecaProperties.put("VehicleClass","Vehicle Class");
		hmEndecaProperties.put("VehicleLicensePlate","Vehicle License Plate");
		hmEndecaProperties.put("VehicleModel","Vehicle Model");
		hmEndecaProperties.put("VehicleMake","Vehicle Make");
		hmEndecaProperties.put("VehicleState","Vehicle State");
		hmEndecaProperties.put("VehicleStyle","Vehicle Style");
		hmEndecaProperties.put("VehYear","Veh_Year");
		hmEndecaProperties.put("VehicleYear","Vehicle Year");
		hmEndecaProperties.put("PWeight","P_Weight");
		hmEndecaProperties.put("Weight","Weight ( 10 lb)");
		hmEndecaProperties.put("Zip","Zip");
		hmEndecaProperties.put("Zip4","Zip4");
		hmEndecaProperties.put("PrimaryMugShot", "PrimaryMugShot");
	}
	public static String getEndecaProperty(String jasperField){
		return (String) hmEndecaProperties.get(jasperField);
	}
}
