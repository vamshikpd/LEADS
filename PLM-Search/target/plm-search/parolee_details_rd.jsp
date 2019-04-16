<!-- parolee-details rd.jsp 
      08--23-2011 Modified to not display fields for PRCS -- L. Baird
      10-04-2011 Modified by adding DAI- changes L. Baird
       04-09-2012 Modified Job Info to clear the field before adding new data. LBaird -->
<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.net.*" %>
<%@ page import="com.util.format.*" %>
<%@ page import="com.endeca.ui.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.plm.constants.PLMConstants" %>
<%@ page import="com.plm.util.database.PLMDatabaseUtil" %>
<%@ page import="com.plm.util.PLMSearchUtil" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.net.URLDecoder" %>
<%
final Logger logger = Logger.getLogger(this.getClass());
	
	String strDiscDate = "";
	Navigation nav = (Navigation)request.getAttribute("navigation");
	ENEQuery usq = (ENEQuery)request.getAttribute("eneQuery");
	ENEQueryResults qr = (ENEQueryResults)request.getAttribute("eneQueryResults");
	PropertyContainer rec = qr.getERec();
	String tempQueryString = URLDecoder.decode(request.getQueryString(),"UTF-8");
	String queryString = PLMSearchUtil.encodeGeoCodeCriteria(tempQueryString);

	if(rec == null)
		rec = qr.getAggrERec();
	
	UnifiedPropertyMap uPropsMap = new UnifiedPropertyMap(rec,
			Boolean.valueOf(UI_Props.getInstance().getValue(UI_Props.ROLLUP_RECS)).booleanValue());
	String imageLocation ="";
	PropertyMap tempPropsMap = null;
	
	if(rec instanceof ERec) {
		tempPropsMap = ((ERec)rec).getProperties();	//sk
	}else {
		tempPropsMap = ((AggrERec)rec).getProperties();	//sk
	}
%>

<%@ include file="all_properties.jsp" %>

<%
	DateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
	java.util.Date date = new java.util.Date();
	String repTitle = UI_Props.getInstance().getValue("PAROLEE_DETAIL_REP_TITLE") + " " + sCDCNum;
	
	String filename = "";
	if(request.getHeader("USERID")!=null){
		filename = request.getHeader("USERID") + "_" + dateFormat.format(date) + "_" + sCDCNum + ".pdf";
	}else if(session.getAttribute("userId")!=null){
		filename = (String) session.getAttribute("userId") + "_" + dateFormat.format(date) + "_" + sCDCNum + ".pdf";
	}else{
		filename = "dummy" + "_" + dateFormat.format(date) + "_" + sCDCNum + ".pdf";
	}
	
	String sMsgDisp = "";
	/* Added PRCS and DAI -- LBB  and MISD*/
	// emil 2018-03-10 fix exception in substring call
	String sUnitSubstr = "";
	try {
		sUnitSubstr = sUntNm.substring(0,4);
	} catch (StringIndexOutOfBoundsException e) {
		logger.error("StringIndexOutOfBoundsException --> Unit Name= " + sUntNm);
	}

	if (sStatus.equals("SUSPENDED") == false
		&& sClassDesc.equals("DEPORTED") == false
		&& sUnitSubstr.equals("MNRP") == false
		&& sUntNm.substring(0,3).equals("NRP") == false
		&& sUnitSubstr.equals("PRCS") == false
		&& sUnitSubstr.equals("DAI-") == false
		&& sPendPAL.equals("Y") == false
		&& sClassDesc.substring(0,4).equals("MISD") == false) { // Added by Vamshi Kapidi on 02/02/2015 for MISD Enhancement
		sMsgDisp="NONE";
	}
	
	if(sStatus.equals("SUSPENDED")) {
		sMsgDisp = sMsgDisp + "This subject may be a Parolee-At-Large (PAL). Should you make contact with this subject, he or she may have absconded. Between 0800 and 1630 hours, please contact the Parole Agent of Record at the Parole Unit. After 1630 hours, weekends or Holidays, contact the Agent of Record through the Dept. of Corrections ID/Warrants Unit at (916) 324-2891 to obtain authorization for a Parole Hold per PC 3056.<br/><br/>";
	}else {
		if(sPendPAL.equals("Y")) {
			sMsgDisp = sMsgDisp + "Warrant is pending for this parolee, please use caution.  Call ID Warrants at (916) 445-6713. <br/>";
		}
	}
	
	if(sClassDesc.equals("DEPORTED")) {
		sMsgDisp = sMsgDisp + "This subject may be a criminal alien subject to immediate arrest by local law enforcement authorities under 8 U.S.C. section 1252c. A criminal alien is a person who has been convicted of a felony in the United States and then deported. Criminal aliens may be subject to federal penalties of up to ten years imprisonment. 8 U.S.C. section 1326. Contact US ICE at 1-802-872-6020, or via NLETS at VTINS07SO, to obtain appropriate confirmation of the subject`s status.<br/>";
	}
	
	if(sUntNm.substring(0,4).equals("MNRP")
			|| sUntNm.substring(0,3).equals("NRP")) {
		sMsgDisp = sMsgDisp + "This subject is on Non-Revocable Parole pursuant to Penal Code 3000.03 and does not have an assigned Parole Agent.  This subject may be searched by law enforcement, however, no verification or validation has been conducted by the California Department of Corrections and Rehabilitation on any residential information that is provided.  Pursuant to Penal Code 3000.03, the California Department of Corrections and Rehabilitations Warrant Unit cannot place a parole hold on this subject and this subject cannot be returned to custody for a violation of parole.<br/>";
	}
	
	/* Added popup message for PRCS -- LBB */
	if(sUntNm.substring(0,4).equals("PRCS"))  {
		sMsgDisp = sMsgDisp + "This subject was released under the provisions of Title 2.05, Post Release Community Supervision Act of 2011, of the California Penal Code.  Pursuant to Penal Code Section 3457, the California Department of Corrections and Rehabilitation (CDCR) shall have no jurisdiction over any person who is under postrelease community supervision pursuant to this title.  CDCR has not verified or validated this subject`s status in the community, including any residential information.  Pursuant to Penal Code Section 3458, No person subject to this title shall be returned to prison for a violation of any condition of the person`s postrelease supervision agreement.  The CDCR Warrant Unit cannot place a hold on this subject.  Please contact the county authorities to verify current status. <br/>";
	}

	/* Added popup message for DAI -- LBB */
	if(sUntNm.substring(0,4).equals("DAI-"))  {
		sMsgDisp = sMsgDisp + "This subject will be released under the provisions of Title 2.05, Post Release Community Supervision Act of 2011, of the California Penal Code. Pursuant to Penal Code Section 3457, the California Department of Corrections and Rehabilitation (CDCR) shall have no jurisdiction over any person who is under postrelease community supervision pursuant to this title. CDCR has not verified or validated this subject`s status in the community, including any residential information. Pursuant to Penal Code Section 3458, No person subject to this title shall be returned to prison for a violation of any condition of the person`s postrelease supervision agreement. The CDCR Warrant Unit cannot place a hold on this subject. <br/>";
	}
	
	/* Added Parole Status Notification message for MISD on 02/02/2015 by Vamshi Kapidi */
	if(sClassDesc.substring(0,4).equals("MISD"))  {
		sMsgDisp = sMsgDisp + "Subject on parole pursuant to PC 1170.18(d) <br/>";
	}
	
	String showid1 = request.getParameter("showid");
	if(showid1 == null) {
		showid1 = "" + PLMDatabaseUtil.getPrimaryMugshotID(sCDCNum);
	}
	//logger.info("photo_details_rd.jsp-------"+showid1);
	String hei1 = null;
	if (sHghtFt!=null && sHghtFt.length()>0){
		hei1 = sHghtFt + "'" + sHghtInch + "\"";
	}else{
		hei1 = "";
	}

	// added for Unit Supervisor and Asst. Unit Supervisor email addressess -- L. Baird 
	String sAddCC = PLMDatabaseUtil.getAddressCC(sUntCd);
 %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<title>Parolee Details</title>
		<script type="text/javascript" src="media/js/jquery-1.3.2.js" ></script>
		<script type="text/javascript" src="media/js/thickbox.js" ></script>
		<script type="text/javascript" src="media/js/show_hide_div.js" ></script>
		<link 	type="text/css" href="media/style/thickbox.css" rel="stylesheet" ></link>
		<link 	type="text/css" rel="stylesheet" href="media/style/main.css" ></link>
	</head>
	<body onLoad="javascript:displayPopup();">
<%
//Added to blank fields for PRCS - LBB
if (sUntNm.substring(0,4).equals("PRCS") == true
		|| sUntNm.substring(0,4).equals("DAI-") == true) {
	sStatus          = "";	
	sAgntNm      = "";
	sAgntEml     = "";
	sCtrlDschDt = "";
	sClassDesc  = "";
	sAntReq       = "";
	sNoAlchl      = "";
	sPOCReq      = "";
	}
%>
		<div align="center" id="paroleedetailspage">
			<div id="Maindiv">
				<div id="Container" >
					<div id="Header">
						<div id="logoimg"><a href="#"><img src="media/images/global/logo.gif" alt="CDCR" border="0"/></a></div>
					</div>
					<div id="Middle" class="content_box" >
						<div id="middletop">
							<div id="middletopright">
								<div id="paroleedetailstopbutton"><%@ include file="details_tabs.jsp" %></div>
								<div id="parolledetailsdiv">
									<div id="parolledetailsdivtop">
										<div id="parolledetailsdivtopmiddle">
											<div id="parolleheadertextdiv">Quick Information</div>
											<div style="float: right; margin-top: 5px;">
												<div style="float:right"><img src="media/images/global/right_half_curve_button.gif" alt="" /></div>
												<div style="float:right;"><a href="#" onclick="printToPDF();" style="padding:5px; width:auto; background-color:#E17236; text-decoration:none; color:#fff; font-weight:bold; float:left">Print Parolee Details</a></div>
												<div style="float:right"><img src="media/images/global/left_half_curve_button.gif" alt="" /></div>
											</div>
										</div>
									</div>
									<%@ include file="essence_info.jsp" %>
								</div>
								<div id="parolledetailsdivcontent">
									<div id="parolledetailsdivtop">
										<div id="parolledetailsdivtopmiddle">
											<div id="parolleheadertextdiv">Parole Status Notification</div>
										</div>
									</div>
									<div class="row2">
										<div class="paroleedetailsvalue notification"><%=sMsgDisp%></div>
									</div>
								</div>
								<div id="parolledetailsdiv">
									<div id="parolledetailsdivtop">
										<div id="parolledetailsdivtopmiddle" onclick="showContent(this);">
											<div id="parolleheadertextdiv"><div class="arrowbuttondiv"><img src="media/images/global/arrow_up_blue.gif" alt="Expand" border="0"/></div><div style="float:left; color:#FFFFFF">Parolee Details</div></div>
										</div>
									</div>
									<div id="parolledetailsdivcontent">
										<div class="firsttworowphoto">
											<div class="firsttworow">
												<div class="row2">
													<div class="paroleedetailsname col1">Last Name</div>
													<div class="paroleedetailsvalue col2"><%=sLstNm%></div>
													<div class="paroleedetailsname col3">First Name</div>
													<div class="paroleedetailsvalue col4"><%=sFstNm%></div>
													<div class="paroleedetailsname col5">Middle Name</div>
													<div class="paroleedetailsvalue col6"><%=sMidNm%></div>
												</div>
												<div class="row1">
													<div class="paroleedetailsname col1">CDC#</div>
													<div class="paroleedetailsvalue col2"><%=sCDCNum%></div>
													<div class="paroleedetailsname col3">Parole Status</div>
													<div class="paroleedetailsvalue col4"><%=sStatus%></div>
													<div class="paroleedetailsname col5">USINS#</div>
													<div class="paroleedetailsvalue col6"><%=sUSINSNum%></div>
												</div>
												<div class="row2">
													<div class="paroleedetailsname col1">Parole Date</div>
													<div class="paroleedetailsvalue col2"><%=sParDtDisp%></div>
													<div class="paroleedetailsname col3">Max Discharge Date</div>
													<div class="paroleedetailsvalue col4"><%=sCtrlDschDt%></div>
													<div class="paroleedetailsname col5">Revocation Release Date</div>
													<div class="paroleedetailsvalue col6"><%=sRevRelDt%></div>
												</div>
												<div class="row1">
													<div class="paroleedetailsname col1">Supervision Level</div>
													<div class="paroleedetailsvalue col2"><%=sClassDesc%></div>
													<div class="paroleedetailsname col3">Commitment County</div>
													<div class="paroleedetailsvalue col4"><%=sCntyCmt%></div>
													<!--Added for COLLR -- LBB -->
													<div class="paroleedetailsname col5">County of LLR</div>
													<div class="paroleedetailsvalue col6"><%=sCOLLRNm%></div>
												</div>
											</div>
											<div class="paroleedetailsphoto">
												<div class="photo">
<%
	UrlGen urlPGallery = new UrlGen(queryString, "UTF-8");
	urlPGallery.removeParam("N");
	urlPGallery.addParam("N", nValue.toString());
	urlPGallery.removeParam("showid");
	urlPGallery.addParam("showid",""+showid);
	urlPGallery.removeParam("ptab");
	urlPGallery.addParam("ptab", "1");
	String urlPGalleryMain = "plm_controller.jsp"+"?"+urlPGallery;
%>													<a href="<%=urlPGalleryMain%>"><img src="image.jsp?showid=<%=showid1%>&psize=p" alt="<%=sCDCNum%>" height="150" width="120"/></a>
												</div>
											</div>
										</div>
										<!--Added to create a new row for Parole Unit  to make room for COLLR -- LBB-->
										<div class="row2">
											<div class="paroleedetailsname col1">Parole Unit</div>
											<div class="paroleedetailsvalue col2"><%=sUntNm%></div>
										</div>
										<div class="row1">
											<div class="paroleedetailsname col1">Agent Name</div>
											<div class="paroleedetailsvalue col2"><%=sAgntNm%></div>
											<div class="paroleedetailsname col3">Agent Telephone</div>
											<div class="paroleedetailsvalue col4"><%=sAgntPh%></div>
											<div class="paroleedetailsname col5">Agent e-mail</div>
											<!-- Added  sAddCC to the parameter string and increeased the size of the dialog box. -- L. Baird -->
											<div class="paroleedetailsvalue col6 lastdiv">
												<a class="thickbox" title="Email" name="Agent"
												   href="email_agent_rd.jsp?toAddress=<%=sAgntEml%>&cdcNum=<%=sCDCNum%>&firstName=<%=sFstNm%>&lastName=<%=sLstNm%>&addCC=<%=sAddCC%>&keepThis=true&amp;TB_iframe=true&amp;width=550;height=550"><%=sAgntEml%></a>
											</div>
										</div>
										<div class="row2">
											<div class="paroleedetailsname col1">Birth Date</div>
											<div class="paroleedetailsvalue col2"><%=sBrthDtDisp%></div>
											<div class="paroleedetailsname col3">Birth State</div>
											<div class="paroleedetailsvalue col4"><%=sBrthStateNm%></div>
											<div class="paroleedetailsname col5">Gender</div>
											<div class="paroleedetailsvalue col6"><%=sSx%></div>
											<div class="paroleedetailsname col7">Ethnicity</div>
											<div class="paroleedetailsvalue col8"><%=sRace%></div>
										</div>
										<div class="row1">
											<div class="paroleedetailsname col1">Height</div>
											<div class="paroleedetailsvalue col2"><%=hei1%></div>
											<div class="paroleedetailsname col3">Weight</div>
											<div class="paroleedetailsvalue col4"><%=sWght%></div>
											<div class="paroleedetailsname col5">Hair Color</div>
											<div class="paroleedetailsvalue col6"><%=sHairclr%></div>
											<div class="paroleedetailsname col7">Eye Color</div>
											<div class="paroleedetailsvalue col8"><%=sEyeclr%></div>
										</div>
										<div class="row2">
											<div class="paroleedetailsname col1">FBI#</div>
											<div class="paroleedetailsvalue col2"><%=sFBINum%></div>
											<div class="paroleedetailsname col3">CII#</div>
											<div class="paroleedetailsvalue col4"><%=sCIINum%></div>
											<div class="paroleedetailsname col5">SSA#</div>
											<div class="paroleedetailsvalue col6"><%=sSSANum%></div>
											<div class="paroleedetailsname col7">Drivers License#</div>
											<div class="paroleedetailsvalue col8"><%=sDrvrLicNum%></div>
										</div>
									</div>
								</div>
								<div id="parolledetailsdivcontent">
									<div id="parolledetailsdivtop">
										<div id="parolledetailsdivtopmiddle" onclick="showContent(this);">
											<div id="parolleheadertextdiv"><div class="arrowbuttondiv"><img src="media/images/global/arrow_down_blue.gif" alt="Expand" border="0"/></div>Registration & Notice Information</div>
										</div>
										<div class="arrowbuttondivall" onclick="showallContent();">
											<div id="showall">Expand All</div>
											<img src="media/images/global/arrow_down_blue_all.gif" alt="Expand All" border="0" id="showallimg"/>
										</div>
									</div>
									<div id="parolledetailsdivcontent" class="hide">
										<div class="row2">
											<div class="paroleedetailsname col1">HS 11590</div>
											<div class="paroleedetailsvalue col2"><%=sHSReq%></div>
											<div class="paroleedetailsname col3">PC 290</div>
											<div class="paroleedetailsvalue col4"><%=sPC290Req%></div>
											<div class="paroleedetailsname col5">PC457.1</div>
											<div class="paroleedetailsvalue col6"><%=sPC457Req%></div>
											<div class="paroleedetailsname col7">PC3058.6</div>
											<div class="paroleedetailsvalue col8"><%=sPC3058Req%></div>
										</div>
										<div class="row1">
											<div class="paroleedetailsname col1">HS 11590 Reg. Date</div>
											<div class="paroleedetailsvalue col2"><%=sHSDt%></div>
											<div class="paroleedetailsname col3">PC 290 Reg. Date</div>
											<div class="paroleedetailsvalue col4"><%=sPC290Dt%></div>
											<div class="paroleedetailsname col5">PC457.1 Reg. Date</div>
											<div class="paroleedetailsvalue col6"><%=sPC457Dt%></div>
											<div class="paroleedetailsname col7">PC3058.6 Reg. Date</div>
											<div class="paroleedetailsvalue col8"><%=sPC3058Dt%></div>
										</div>
									</div>
								</div>
								<div id="parolledetailsdivcontent">
									<div id="parolledetailsdivtop">
										<div id="parolledetailsdivtopmiddle" onclick="showContent(this);">
											<div id="parolleheadertextdiv"><div class="arrowbuttondiv"><img src="media/images/global/arrow_down_blue.gif" alt="Expand" border="0"/></div>Special Parole Conditions</div>
										</div>
									</div>
									<div id="parolledetailsdivcontent" class="hide">
										<div class="row2">
											<div class="paroleedetailsname col1">Drug testing</div>
											<div class="paroleedetailsvalue col2"><%=sAntReq%></div>
											<div class="paroleedetailsname col3">No Alcohol</div>
											<div class="paroleedetailsvalue col4"><%=sNoAlchl%></div>
											<div class="paroleedetailsname col5">Psychiatric Outpatient Clinic</div>
											<div class="paroleedetailsvalue col6"><%=sPOCReq%></div>
										</div>
									</div>
								</div>
								<div id="paroleeaddressesdiv">
									<div id="paroleeaddressesdivtop">
										<div id="parolledetailsdivtopmiddle" onclick="showContent(this);">
											<div id="parolleheadertextdiv">
												<div class="arrowbuttondiv">
													<img src="media/images/global/arrow_down_blue.gif" alt="Expand" border="0"/>
												</div>Parolee Addresses
											</div>
										</div>
									</div>
<%
	strDiscDate = sAddrEffDt;
	if(strDiscDate == null || strDiscDate.trim().length() == 0) {
		strDiscDate = "NONE";
	}
	String currAdd = "";
	if (sCareOf.length()>0)
		currAdd = currAdd + sCareOf + "<br/>";
	if (sStrt.length()>0)
		currAdd = currAdd + sStrt + "<br/>";
	if (sCity.length()>0)
		currAdd = currAdd + sCity;
	if (sCntyCd.length()>0)
		currAdd = currAdd + ", " + sCntyCd + "<br/>";
	if (sStateCd.length()>0)
		currAdd = currAdd + sStateCd;
	if (sZip.length()>0)
		currAdd = currAdd + ", " + sZip;
	if (sZip4.length()>0)
		currAdd = currAdd + "-" + sZip4 + "<br/>";
	else
		currAdd = currAdd + "<br/>";
	if (sMap.length()>0)
		currAdd = currAdd + "Map Reference: " + sMap + "<br/>";
	if (sFullPh.length()>0)
		currAdd = currAdd + "Phone: " + sFullPh +"<br/>";
	if (sMsgPh.length()>0)
		currAdd = currAdd + "Msg Phone: " + sMsgPh + "<br/>";
	if (sResStatus.length()>0)
		currAdd = currAdd + "Resident Status: " + sResStatus + "<br/>";
	if("".equals(currAdd.trim())){
		currAdd = "NONE";
	}
	int iAddrCnt=0;
	String urlTab4 = null;
	String sUrlTab4 = "";
	if (strDiscDate != null && strDiscDate.trim().length() > 0) {
		if(currAdd.equals("NONE")) {
			currAdd = "NO ADDRESS FOUND";
		}
		if(!(request.getParameter("fromToolsListPage")!=null && "y".equals(request.getParameter("fromToolsListPage")))){
			if(iAddrCnt==0){
				UrlGen urlg = new UrlGen("", "UTF-8");
				urlg.removeParam("R");
				urlg.removeParam("Nrs");
				urlg.removeParam("fromTools");
				urlg.removeParam("keepThis");
				urlg.removeParam("ptab");
				urlg.removeParam("No");
				urlg.addParam("singleParoleeMap","y");
				urlg.addParam("R",sCDCNum);
				urlg.addParam("fromTools","y");
				urlTab4 = "plm_controller.jsp"+"?"+urlg+"&keepThis=true&amp;TB_iframe=true&amp;width=800;height=650";
				urlg.removeParam("singleParoleeMap");
				urlg.removeParam("R");

				iAddrCnt++;
			}
			if(urlTab4!=null && tempPropsMap.get("geocode")!=null){
				sUrlTab4 = "<br /><a href=\"" + urlTab4 + "\" class=\"thickbox\">Find in map</a>";
			}
		}
	} else {
		strDiscDate="N/A";
		sUrlTab4="";
	} 
%>
									<div id="parolledetailsdivcontent" class="hide">
										<div id="paroleeaddressesdivcontent">
											<!--  latest address first -->
											<div class="row2">
												<div class="paroleedetailsvalue col1"><%=strDiscDate%><%=sUrlTab4%></div>
												<div class="paroleedetailsvalue col2"><%=currAdd%></div>
											</div>
<%
	String sortedPrevAddresses = sSrtPrevAddr;
	String[] prevAddresses = null;
	if(sortedPrevAddresses != null) {
		prevAddresses = sortedPrevAddresses.split("@@");
	}
	int cnt=0;
	int rowColor = 0;
	while (prevAddresses != null && cnt<prevAddresses.length){
		String sRow = "";
		if (rowColor%2==0){
			sRow="row1";
		}else{
			sRow="row2";
		}
%>
											<div class="<%=sRow%>">
<%
		for(int k=0; k<2 && cnt<prevAddresses.length; k++){
			String prevAdd1 = prevAddresses[cnt];
			String prevAddress = "";
			String[] result = null;
			//start date - 0
			//careof - 1
			//street - 2
			//city, county - 3,5
			//state,zip-zip4 - 4,7,8
			//map - 11
			//phone - 9
			//msg ph - 10
			if(prevAdd1 != null){
				result = prevAdd1.split(PLMConstants.SEPARATOR);
			}
			if(result != null){
				int iResult =0;
				//date
				if(result[0] != null && result[0].length()>0 && !"".equals(result[0])){
					strDiscDate = result[0];
				}else{
					strDiscDate ="";
				}
				//careof
				if(result.length > 1 && result[1] != null && result[1].length()>0 && !"".equals(result[1]))
					prevAddress = prevAddress+(String)result[1]+"<br/>";
				//street
				if(result.length > 2 && result[2] != null && result[2].length()>0 && !"".equals(result[2]))
					prevAddress = prevAddress+(String)result[2]+"<br/>";
				//city
				if(result.length > 3 && result[3] != null && result[3].length()>0 && !"".equals(result[3])){
					prevAddress = prevAddress+(String)result[3]+",&nbsp;";
					iResult =1;
				}
				//county
				if(result.length > 5 && result[5] != null && result[5].length()>0 && !"".equals(result[5])){
					prevAddress = prevAddress+(String)result[5];
					iResult =1;
				}
				if(iResult >0)
					prevAddress = prevAddress+"<br />";
				iResult = 0;
				//state
				if(result.length > 4 && result[4] != null && result[4].length()>0 && !"".equals(result[4])){
					prevAddress = prevAddress+(String)result[4]+",&nbsp;";
					iResult =1;
				}
				//zip
				if(result.length > 7 && result[7] != null && result[7].length()>0 && !"".equals(result[7])){
					prevAddress = prevAddress+(String)result[7];
					iResult =1;
				}
				//zip4
				if(result.length > 8 && result[8] != null && result[8].length()>0 && !"".equals(result[8])){
					prevAddress = prevAddress+"-"+(String)result[8];
					iResult =1;
				}
				if(iResult >0)
					prevAddress = prevAddress+"<br />";
					iResult = 0;
				//map
				if(result.length > 11 && result[11] != null && result[11].length()>0 && !"".equals(result[11])) {
					prevAddress = prevAddress+"Map Reference:"+(String)result[11]+"<br />";
				}
				//phone
				if(result.length > 9 && result[9] != null && result[9].trim().length()>0 && !"".equals(result[9].trim())){
					prevAddress = prevAddress+ "Phone:" + (String)result[9]+"<br />";
				}
				//msg phone
				if(result.length > 10 && result[10] != null && result[10].trim().length()>0 && !"".equals(result[10].trim())){
					prevAddress = prevAddress+ "Msg Phone:" + (String)result[10]+"<br />";
				}
			}
%>
												<div class="paroleedetailsvalue col1"><%=strDiscDate%></div>
												<div class="paroleedetailsvalue col2"><%=prevAddress%></div>
<%
			cnt++;
		}
%>
											</div>
<%
		rowColor = rowColor+1;
	}// end while
%>
										</div>
									</div>
								</div>
								<div id="parolledetailsdivcontent">
									<div id="parolledetailsdivtop">
										<div id="parolledetailsdivtopmiddle" onclick="showContent(this);">
											<div id="parolleheadertextdiv">
												<div class="arrowbuttondiv">
													<img src="media/images/global/arrow_down_blue.gif" alt="Expand" border="0"/>
												</div>Scars, Marks & Tattoo Information
											</div>
										</div>
									</div>
									<div id="parolledetailsdivcontent" class="hide">
<%
	Collection smtInfoColl = colSMTInf;
	if (smtInfoColl != null && smtInfoColl.size() > 0){
		Iterator smtInfoCollIter = (smtInfoColl != null)?smtInfoColl.iterator():null;
		cnt = 0;
		while (smtInfoCollIter != null && smtInfoCollIter.hasNext()){
			cnt++;
			String sRow="";
			if (cnt%2==0){
				sRow="row1";
			}else{
				sRow="row2";
			}
			String smtInfo = (String)smtInfoCollIter.next();
			HashMap hmapSmt = new HashMap();
			//String[] smtInfo_result = smtInfo.split("\\<\\>");
			String[] smtInfo_result = smtInfo.split(PLMConstants.SEPARATOR);
			if (smtInfo_result.length > 0 && (String)smtInfo_result[0] != null && !((String)smtInfo_result[0]).equals("")){
				hmapSmt.put("Code/Location", (String)smtInfo_result[0]);
			}else{
				hmapSmt.put("Code/Location", "");
			}
			if (smtInfo_result.length > 2 && (String)smtInfo_result[2] != null && !((String)smtInfo_result[2]).equals("")){
				hmapSmt.put("Picture", (String)smtInfo_result[2]);
			}else{
				hmapSmt.put("Picture", "");
			}
			if (smtInfo_result.length > 3 && (String)smtInfo_result[3] != null && !((String)smtInfo_result[3]).equals("")){
				hmapSmt.put("Text", (String)smtInfo_result[3]);
			}else{
				hmapSmt.put("Text", "");
			}
%>
										<div class="<%=sRow%>">
											<div class="paroleedetailsname col1">Code/Location</div>
											<div class="paroleedetailsvalue col2"><%=hmapSmt.get("Code/Location")%></div>
											<div class="paroleedetailsname col3">Picture</div>
											<div class="paroleedetailsvalue col4"><%=hmapSmt.get("Picture")%></div>
											<div class="paroleedetailsname col5">Text</div>
											<div class="paroleedetailsvalue col6 lastdiv"><%=hmapSmt.get("Text")%></div>
										</div>
<%
		}// end while
	}else{ // if size 0
%>
										<div class="row2">
											<div class="paroleedetailsvalue col1">NONE</div>
										</div>
<%
	}// end 
%>
									</div>
								</div>
								<div id="parolledetailsdivcontent">
									<div id="parolledetailsdivtop">
										<div id="parolledetailsdivtopmiddle" onclick="showContent(this);">
											<div id="parolleheadertextdiv">
												<div class="arrowbuttondiv">
													<img src="media/images/global/arrow_down_blue.gif" alt="Expand" border="0"/>
												</div>Alias Information
											</div>
										</div>
									</div>
									<div id="parolledetailsdivcontent" class="hide">
<%
	Collection aliasInfoColl = colAlsInf;
	if (aliasInfoColl != null && aliasInfoColl.size()>0){
		Iterator aliasInfoCollIter = (aliasInfoColl != null)?aliasInfoColl.iterator():null;
		cnt = 0;
		while (aliasInfoCollIter != null && aliasInfoCollIter.hasNext()){
			cnt++;
			String sRow="";
			if (cnt%2==0){
				sRow="row1";
			}else{
				sRow="row2";
			}
			String aliasInfo = (String)aliasInfoCollIter.next();
			HashMap hmapAlias = new HashMap();
			//String[] aliasInfo_result = aliasInfo.split("\\<\\>");
			String[] aliasInfo_result = aliasInfo.split(PLMConstants.SEPARATOR);
			if (aliasInfo_result.length > 0 && (String)aliasInfo_result[0] != null && !((String)aliasInfo_result[0]).equals("")){
				hmapAlias.put("Alias Last Name", (String)aliasInfo_result[0]);
			}else{
				hmapAlias.put("Alias Last Name", "");
			}
			if (aliasInfo_result.length > 1 && (String)aliasInfo_result[1] != null && !((String)aliasInfo_result[1]).equals("")){
				hmapAlias.put("Alias First Name", (String)aliasInfo_result[1]);
			}else{
				hmapAlias.put("Alias First Name", "");
			}
			if (aliasInfo_result.length > 2 && (String)aliasInfo_result[2] != null && !((String)aliasInfo_result[2]).equals("")){
				hmapAlias.put("Alias Middle Name", (String)aliasInfo_result[2]);
			}else{
				hmapAlias.put("Alias Middle Name", "");
			}
%>
										<div class="<%=sRow%>">
											<div class="paroleedetailsname col1">Alias Last Name</div>
											<div class="paroleedetailsvalue col2"><%=hmapAlias.get("Alias Last Name")%></div>
											<div class="paroleedetailsname col3">Alias First Name</div>
											<div class="paroleedetailsvalue col4"><%=hmapAlias.get("Alias First Name")%></div>
											<div class="paroleedetailsname col5">Alias Middle Name</div>
											<div class="paroleedetailsvalue col6 lastdiv"><%=hmapAlias.get("Alias Middle Name")%></div>
										</div>
<%
		}//end while
	}else{// else if
%>
										<div class="row2">
											<div class="paroleedetailsvalue col1">NONE</div>
										</div>
<%
	} 
%>
									</div>
								</div>
								<div id="parolledetailsdivcontent">
									<div id="parolledetailsdivtop">
										<div id="parolledetailsdivtopmiddle" onclick="showContent(this);">
											<div id="parolleheadertextdiv">
												<div class="arrowbuttondiv">
													<img src="media/images/global/arrow_down_blue.gif" alt="Expand" border="0"/>
												</div>Moniker Information
											</div>
										</div>
									</div>
									<div id="parolledetailsdivcontent" class="hide">
<%
	Collection monikerInfoColl = colMonInf;
	StringBuffer sbf = new StringBuffer();
	if (monikerInfoColl != null && monikerInfoColl.size() > 0){
		Iterator monikerInfoCollIter = (monikerInfoColl != null)?monikerInfoColl.iterator():null;
		while (monikerInfoCollIter != null && monikerInfoCollIter.hasNext()){
			String moniInfo = (String)monikerInfoCollIter.next();
			HashMap hmapMoni = new HashMap();
			String[] moniInfo_result = moniInfo.split(PLMConstants.SEPARATOR);
			if (moniInfo_result.length > 0 && (String)moniInfo_result[0] != null && !((String)moniInfo_result[0]).equals("")){
				sbf.append("/");
				sbf.append((String)moniInfo_result[0]);
			}else{
				hmapMoni.put("Moniker", "");
			}
			// second filed is last change date, that we do not need to display...it's for data download
		}
	}
%>
										<div class="row2">
											<div class="paroleedetailsvalue col1"><%=sbf.length()>0?sbf.toString().substring(1):"NONE"%></div>
										</div>
									</div>
									</div>								
									<div id="parolledetailsdivcontent">
										<div id="parolledetailsdivtop">
											<div id="parolledetailsdivtopmiddle" onclick="showContent(this);">
												<div id="parolleheadertextdiv">
													<div class="arrowbuttondiv">	
														<img src="media/images/global/arrow_down_blue.gif" alt="Expand" border="0"/>
													</div>Commitment Offense Information
												</div>
											</div>
										</div>
										<div id="parolledetailsdivcontent" class="hide">
<%
	Collection offenseInfoColl = colOffInf;
	if (offenseInfoColl != null && offenseInfoColl.size() > 0){
		Iterator offenseInfoCollIter = (offenseInfoColl != null)?offenseInfoColl.iterator():null;
		cnt = 0;
		while (offenseInfoCollIter != null && offenseInfoCollIter.hasNext()){
			cnt++;
			String sRow="";
			if (cnt%2==0){
				sRow="row1";
			}else{
				sRow="row2";
			}
			String offInfo = (String)offenseInfoCollIter.next();
			HashMap hmapOff = new HashMap();
			String[] offInfo_result = offInfo.split(PLMConstants.SEPARATOR);
			if (offInfo_result.length > 0 && (String)offInfo_result[0] != null && !((String)offInfo_result[0]).equals("")){
				hmapOff.put("Offense Code", (String)offInfo_result[0]);
			}else{
				hmapOff.put("Offense Code", "");
			}
			if (offInfo_result.length > 1 && (String)offInfo_result[1] != null && !((String)offInfo_result[1]).equals("")){
				hmapOff.put("Description", (String)offInfo_result[1]);
			}else{
				hmapOff.put("Description", "");
			}
			if (offInfo_result.length > 4 && (String)offInfo_result[4] != null && !((String)offInfo_result[4]).equals("")){
				hmapOff.put("Controlling Offense", (String)offInfo_result[4]);
			}else{
				hmapOff.put("Controlling Offense", "");
			}
%>
											<div class="<%=sRow%>">
												<div class="paroleedetailsname col1">Offense Code</div>
												<div class="paroleedetailsvalue col2"><%=hmapOff.get("Offense Code")%></div>
												<div class="paroleedetailsname col3">Description</div>
												<div class="paroleedetailsvalue col4"><%=hmapOff.get("Description")%></div>
												<div class="paroleedetailsname col5">Controlling Offense</div>
												<div class="paroleedetailsvalue col6"><%=hmapOff.get("Controlling Offense")%></div>
											</div>
<%
		}//end while
	}else{// else if
%>
											<div class="row2">
												<div class="paroleedetailsvalue col1">NONE</div>
											</div>
<%
	} 
%>
										</div>
									</div>
									<div id="paroleeaddressesdiv">
										<div id="paroleeaddressesdivtop">
											<div id="parolledetailsdivtopmiddle" onclick="showContent(this);">
												<div id="parolleheadertextdiv">
													<div class="arrowbuttondiv">
														<img src="media/images/global/arrow_down_blue.gif" alt="Expand" border="0"/>
													</div>Job Information
												</div>
											</div>
										</div>
										<div id="parolledetailsdivcontent" class="hide">
											<div id="paroleeaddressesdivcontent">
<%
	String sortedJobInfos = sSrtEmprInf;
	String[] jobInfos = null;
	if(sortedJobInfos != null) {
		jobInfos = sortedJobInfos.split("@@");
		cnt=0;
		rowColor = 0;
		while (jobInfos != null && cnt<jobInfos.length){
			String sRow="";
			if (rowColor%2==0){
				sRow="row2";
			}else{
				sRow="row1";
			}
%>
												<div class="<%=sRow%>">
<%
			for(int k=0; k<2 && cnt<jobInfos.length; k++){
				String currJobInfo = "";
				String jobInfo = jobInfos[cnt];
				String[] jobInfo_result = null;
				if(jobInfo != null){
					jobInfo_result = jobInfo.split(PLMConstants.SEPARATOR);
				}
				//start date - 10
				//employer - 0
				//street - 1
				//city, county, state - 2,4
				//zip-zip4 - 3,5,6
				//phone - 7
				//job title - 8
				//emp aware - 9
				if(jobInfo_result != null){
					int iResult=0;
					//employer
					if (jobInfo_result.length > 0 && (String)jobInfo_result[0] != null && !((String)jobInfo_result[0]).trim().equals("")){
						currJobInfo = currJobInfo+jobInfo_result[0]+"<br />";
					}
					//street
					if (jobInfo_result.length > 1 && (String)jobInfo_result[1] != null && !((String)jobInfo_result[1]).trim().equals("")){
						currJobInfo = currJobInfo+jobInfo_result[1]+"<br />";
					}
					//city
					if (jobInfo_result.length > 2 && (String)jobInfo_result[2] != null && !((String)jobInfo_result[2]).trim().equals("")){
						currJobInfo = currJobInfo+jobInfo_result[2]+",&nbsp;";
						iResult = 1;
					}
					//county
					if (jobInfo_result.length > 4 && (String)jobInfo_result[4] != null && !((String)jobInfo_result[4]).trim().equals("")){
						iResult = 1;
						currJobInfo = currJobInfo+jobInfo_result[4];
					}
					if(iResult > 0)
						currJobInfo = currJobInfo+"<br />";
						iResult = 0;
					//state
					if (jobInfo_result.length > 3 && (String)jobInfo_result[3] != null && !((String)jobInfo_result[3]).trim().equals("")){
						currJobInfo = currJobInfo+jobInfo_result[3]+",&nbsp;";
						iResult = 1;
					}
					//zip
					if (jobInfo_result.length > 5 && (String)jobInfo_result[5] != null && !((String)jobInfo_result[5]).trim().equals("")){
						currJobInfo = currJobInfo+jobInfo_result[5];
						iResult = 1;
					}
					//zip4
					if (jobInfo_result.length > 6 && (String)jobInfo_result[6] != null && !((String)jobInfo_result[6]).trim().equals("")){
						currJobInfo = currJobInfo+"-"+jobInfo_result[6];
						iResult = 1;
					}
					if(iResult >0)
						currJobInfo = currJobInfo+"<br />";
					//phone
					if (jobInfo_result.length > 7 && (String)jobInfo_result[7] != null && !((String)jobInfo_result[7]).trim().equals("")){
						currJobInfo = currJobInfo + "Phone:" + jobInfo_result[7]+"<br />";
					}
					//job title
					if (jobInfo_result.length > 8 && (String)jobInfo_result[8] != null && !((String)jobInfo_result[8]).trim().equals("")){
						currJobInfo = currJobInfo+jobInfo_result[8]+"<br />";
					}
					//emp aware
					if (jobInfo_result.length > 9 && (String)jobInfo_result[9] != null && !((String)jobInfo_result[9]).trim().equals("")){
						currJobInfo = currJobInfo+"Employer Aware:&nbsp;&nbsp;"+jobInfo_result[9]+"<br />";
					}
					//start date
					if (jobInfo_result.length > 10 && (String)jobInfo_result[10] != null && !((String)jobInfo_result[10]).trim().equals("")){
						strDiscDate = jobInfo_result[10];
					}else{
						strDiscDate ="";
					}
				}
%>
													<div class="paroleedetailsvalue col1"><%=strDiscDate%></div>
													<div class="paroleedetailsvalue col2 "><%=currJobInfo%></div>
<%
				cnt=cnt+1;
			}
%>
												</div>
<%
			rowColor = rowColor+1;
		}//end while
	}else{
%>
												<div class="row2">
													<div class="paroleedetailsvalue col1">NONE</div>
												</div>
<%
	}
%>
											</div>
										</div>
										</div>
										<div id="parolledetailsdivcontent">
											<div id="parolledetailsdivtop">
												<div id="parolledetailsdivtopmiddle" onclick="showContent(this);">
													<div id="parolleheadertextdiv">
														<div class="arrowbuttondiv">
															<img src="media/images/global/arrow_down_blue.gif" alt="Expand" border="0"/>
														</div>Vehicle Information
													</div>
												</div>
											</div>
											<div id="parolledetailsdivcontent" class="hide">
<%
	Collection vehInfoColl = colVehInf;
	if (vehInfoColl != null && vehInfoColl.size() > 0){
		Iterator vehInfoCollIter = (vehInfoColl != null)?vehInfoColl.iterator():null;
		cnt = 0;
		while (vehInfoCollIter != null && vehInfoCollIter.hasNext()){
			cnt++;
			String sRow="";
			if (cnt%2==0){
				sRow="row1";
			}else{
				sRow="row2";
			}
			HashMap hmapVeh = new HashMap();
			String vehInfo = (String)vehInfoCollIter.next();
			//String[] vehInfo_result = vehInfo.split("\\<\\>");
			String[] vehInfo_result = vehInfo.split(PLMConstants.SEPARATOR);
			if ((String)vehInfo_result[1] != null && !((String)vehInfo_result[1]).equals("")){
				hmapVeh.put("Make", (String)vehInfo_result[1]);
			}else{
				hmapVeh.put("Make", "");
			}
			if ((String)vehInfo_result[3] != null && !((String)vehInfo_result[3]).equals("")){
				hmapVeh.put("Model", (String)vehInfo_result[3]);
			}else{
				hmapVeh.put("Model", "");
			}
			if ((String)vehInfo_result[5] != null && !((String)vehInfo_result[5]).equals("")){
				hmapVeh.put("Style", (String)vehInfo_result[5]);
			}else{
				hmapVeh.put("Style", "");
			}
			if ((String)vehInfo_result[6] != null && !((String)vehInfo_result[6]).equals("")){
				hmapVeh.put("Vehicle Class", (String)vehInfo_result[6]);
			}else{
				hmapVeh.put("Vehicle Class", "");
			}
			if ((String)vehInfo_result[7] != null && !((String)vehInfo_result[7]).equals("")){
				hmapVeh.put("Year", (String)vehInfo_result[7]);
			}else{
				hmapVeh.put("Year", "");
			}
			if ((String)vehInfo_result[9] != null && !((String)vehInfo_result[9]).equals("")){
				hmapVeh.put("Color 1", (String)vehInfo_result[9]);
			}else{
				hmapVeh.put("Color 1", "");
			}
			if ((String)vehInfo_result[11] != null && !((String)vehInfo_result[11]).equals("")){
				hmapVeh.put("Color 2", (String)vehInfo_result[11]);
			}else{
				hmapVeh.put("Color 2", "");
			}
			if ((String)vehInfo_result[12] != null && !((String)vehInfo_result[12]).equals("")){
				hmapVeh.put("License Plate", (String)vehInfo_result[12]);
			}else{
				hmapVeh.put("License Plate", "");
			}
			if ((String)vehInfo_result[14] != null && !((String)vehInfo_result[14]).equals("")){
				hmapVeh.put("State", (String)vehInfo_result[14]);
			}else{
				hmapVeh.put("State", "");
			}
			if ((String)vehInfo_result[17] != null && !((String)vehInfo_result[17]).equals("")){
				hmapVeh.put("Owned", (String)vehInfo_result[17]);
			}else{
				hmapVeh.put("Owned", "");
			}
%>
												<div class="<%=sRow%>">
													<div class="paroleedetailsname col1">Make</div>
													<div class="paroleedetailsvalue col2"><%=hmapVeh.get("Make")%></div>
													<div class="paroleedetailsname col3">Model</div>
													<div class="paroleedetailsvalue col4"><%=hmapVeh.get("Model")%></div>
													<div class="paroleedetailsname col5">Style</div>
													<div class="paroleedetailsvalue col6"><%=hmapVeh.get("Style")%></div>
													<div class="paroleedetailsname col7">Vehicle Class</div>
													<div class="paroleedetailsvalue col8"><%=hmapVeh.get("Vehicle Class")%></div>
												</div>
												<div class="<%=sRow%>">
													<div class="paroleedetailsname col1">Year</div>
													<div class="paroleedetailsvalue col2"><%=hmapVeh.get("Year")%></div>
													<div class="paroleedetailsname col3">Color 1</div>
													<div class="paroleedetailsvalue col4"><%=hmapVeh.get("Color 1")%></div>
													<div class="paroleedetailsname col5">Color 2</div>
													<div class="paroleedetailsvalue col6"><%=hmapVeh.get("Color 2")%></div>
													<div class="paroleedetailsname col7">License Plate</div>
													<div class="paroleedetailsvalue col8"><%=hmapVeh.get("License Plate")%></div>
												</div>
												<div class="<%=sRow%>">
													<div class="paroleedetailsname col1">State</div>
													<div class="paroleedetailsvalue col2"><%=hmapVeh.get("State")%></div>
													<div class="paroleedetailsname col3">Owned</div>
													<div class="paroleedetailsvalue col4"><%=hmapVeh.get("Owned")%></div>
												</div>
<%
		}//end while
	}else{
%>
												<div class="row2">
													<div class="paroleedetailsvalue col1">NONE</div>
												</div>
<%
	} 
%>
											</div>
										</div>
										<div id="parolledetailsdivcontent">
											<div id="parolledetailsdivtop">
												<div id="parolledetailsdivtopmiddle" onclick="showContent(this);">
													<div id="parolleheadertextdiv">
														<div class="arrowbuttondiv">
															<img src="media/images/global/arrow_down_blue.gif" alt="Expand" border="0"/>
														</div>Comments
													</div>
												</div>
											</div>
											<div id="parolledetailsdivcontent" class="hide">
												<div class="row2">
													<div class="paroleedetailsvalue specialcomments"><%=sCmnts%></div>
												</div>
											</div>
										</div>
										<div id="parolledetailsdivcontent">
											<div id="parolledetailsdivtop">
												<div id="parolledetailsdivtopmiddle" onclick="showContent(this);">
													<div id="parolleheadertextdiv">
														<div class="arrowbuttondiv">
															<img src="media/images/global/arrow_down_blue.gif" alt="Expand" border="0"/>
														</div>Other Special Parole Conditions
													</div>
												</div>
											</div>
											<div id="parolledetailsdivcontent" class="hide">
<%
	Collection spInfoColl = colSpclCdtnInf;

	// emil 2018-03-10 fix exception in substring call
	sUnitSubstr = "";
	try {
		sUnitSubstr = sUntNm.substring(0,4);
	} catch (StringIndexOutOfBoundsException e) {
		logger.error("StringIndexOutOfBoundsException --> Unit Name= " + sUntNm);
	}

	//Change for PRCS - LBB
	if (sUnitSubstr.equals("PRCS")  || sUnitSubstr.equals("DAI-")) {
	}else{
	if (spInfoColl != null && spInfoColl.size()>0){
		Iterator spInfoCollIter = (spInfoColl != null)?spInfoColl.iterator():null;
		cnt = 0;
		while (spInfoCollIter != null && spInfoCollIter.hasNext()){
			cnt++;
			String sRow="";
			if (cnt%2==0){
				sRow="row1";
			}else{
				sRow="row2";
			}
			HashMap hmapSp = new HashMap();
			String spInfo = (String)spInfoCollIter.next();
			//String[] spInfo_result = spInfo.split("\\<\\>");
			String[] spInfo_result = spInfo.split(PLMConstants.SEPARATOR);
			if ((String)spInfo_result[0] != null && !((String)spInfo_result[0]).equals("")){
				hmapSp.put("Special Comment", (String)spInfo_result[0]);
			}else{
				hmapSp.put("Special Comment", "");
			}
%>
												<div class="<%=sRow%>">
													<div class="paroleedetailsname col1">Special Comment</div>
													<div class="paroleedetailsvalue specialcomments"><%=hmapSp.get("Special Comment")%><br /></div>
												</div>
<%
		}//end while
	}else{
%>
												<div class="row2">
													<div class="paroleedetailsvalue col1">NONE</div>
												</div>
<%
	} 
	}
%>
											</div>
										</div>
									</div>
								</div>
							</div>
<!-- Middle Part End -->
<!-- Footer Part Start -->
<!-- Footer Part End -->
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
<script language="javascript">
	function displayPopup() {
		checkPAL();
		checkNRP();
		checkPRCS();
		//checkPENDPAL();
	}

	function checkPAL() {
		var status='<%=sStatus%>';
		var classification='<%=sClassDesc%>';
		if(status == "SUSPENDED") {
			alert('This subject may be a Parolee-At-Large (PAL). Should you make contact with this subject, he or she may have absconded. Between 0800 and 1630 hours, please contact the Parole Agent of Record at the Parole Unit. After 1630 hours, weekends or Holidays, contact the Agent of Record through the Dept. of Corrections ID/Warrants Unit at (916) 324-2891 to obtain authorization for a Parole Hold per PC 3056.');
		}else {
			var PendPAL='<%=sPendPAL%>';
			if(PendPAL == "Y") {
				alert("Warrant is pending for this parolee, please use caution.  Call ID Warrants at (916) 445-6713.");
			}
		}
		if(classification == "DEPORTED") {
			alert('This subject may be a criminal alien subject to immediate arrest by local law enforcement authorities under 8 U.S.C. section 1252c. A criminal alien is a person who has been convicted of a felony in the United States and then deported. Criminal aliens may be subject to federal penalties of up to ten years imprisonment. 8 U.S.C. section 1326. Contact US ICE at 1-802-872-6020, or via NLETS at VTINS07SO, to obtain appropriate confirmation of the subject`s status.');
		}
	}
	
<%-- 	function checkPENDPAL() {
		var PendPAL='<%=sPendPAL%>';
		if(PendPAL == "Y") {
			alert("Warrant is pending for this parolee, please use caution.  Call ID Warrants at (916) 445-6713.");
		}
	}
 --%>
	function checkNRP() {
		var unit='<%=sUntNm%>';
		try {
            if (unit.substring(0, 4) == "MNRP" || unit.substring(0, 3) == "NRP") {
                alert('This subject is on Non-Revocable Parole pursuant to Penal Code 3000.03 and does not have an assigned Parole Agent.  This subject may be searched by law enforcement, however, no verification or validation has been conducted by the California Department of Corrections and Rehabilitation on any residential information that is provided.  Pursuant to Penal Code 3000.03, the California Department of Corrections and Rehabilitations Warrant Unit cannot place a parole hold on this subject and this subject cannot be returned to custody for a violation of parole');
            }
        } catch (exception) {}
	}
	/*  Added PRCS -- LBB */
	function checkPRCS() {
		var unit='<%=sUntNm%>';
		try {

			if(unit.substring(0, 4) == "PRCS") {
				alert('This subject was released under the provisions of Title 2.05, Post Release Community Supervision Act of 2011, of the California Penal Code.  Pursuant to Penal Code Section 3457, the California Department of Corrections and Rehabilitation (CDCR) shall have no jurisdiction over any person who is under postrelease community supervision pursuant to this title.  CDCR has not verified or validated this subject`s status in the community, including any residential information.  Pursuant to Penal Code Section 3458, No person subject to this title shall be returned to prison for a violation of any condition of the person`s postrelease supervision agreement.  The CDCR Warrant Unit cannot place a hold on this subject.  Please contact the county authorities to verify current status.');
			}
			if(unit.substring(0, 4) == "DAI-") {
				alert('This subject will be released under the provisions of Title 2.05, Post Release Community Supervision Act of 2011, of the California Penal Code. Pursuant to Penal Code Section 3457, the California Department of Corrections and Rehabilitation (CDCR) shall have no jurisdiction over any person who is under postrelease community supervision pursuant to this title. CDCR has not verified or validated this subject`s status in the community, including any residential information. Pursuant to Penal Code Section 3458, No person subject to this title shall be returned to prison for a violation of any condition of the person`s postrelease supervision agreement. The CDCR Warrant Unit cannot place a hold on this subject.');
			}
        } catch (exception) {}
	}
	
	function printToPDF(){
<%
UrlGen urlPdfGen = new UrlGen("", "UTF-8");
urlPdfGen.addParam("R",sCDCNum);
urlPdfGen.addParam("Sn","-1");
urlPdfGen.addParam("Ef",filename);
urlPdfGen.addParam("Et",repTitle);

if(request.getHeader("USERID")!=null){
	urlPdfGen.addParam("userid",request.getHeader("USERID"));
}else if(session.getAttribute("userId")!=null){
	urlPdfGen.addParam("userid",(String) session.getAttribute("userId"));
}else{
	urlPdfGen.addParam("userid","dummy");
}
	
urlPdfGen.addParam("Page","Parolee");
String sURLPdfGen = "exportPDF"+"?"+urlPdfGen;
%>
		window.open('<%=sURLPdfGen%>');
	}
</script>
<%
	String userName= request.getHeader("USERID");
	if(userName==null || "".equals(userName)){
		userName = (String)session.getAttribute("userId");
		if(userName==null || "".equals(userName)){
			userName = "NULL_USERNAME";
		}
	}
	String ip_address= request.getHeader("X-FORWARDED-FOR");
	if(ip_address==null || "".equals(ip_address)){
		ip_address= (String)session.getAttribute("ipAddress");
		if(ip_address==null || "".equals(ip_address)){
			ip_address = "NULL_IPADDRESS";
		}
	}
	if(sCDCNum == null  || "".equals(sCDCNum)){
		sCDCNum  = "??????";
	}
	String case_no = (String)session.getAttribute("case_no");
	String reason_no =(String)session.getAttribute("reason_no");
	String insertString = PLMConstants.INSERT_INTO_CORI_INFO;
	ArrayList values = new ArrayList();
	values.add(userName);
	values.add(ip_address);
	values.add(sCDCNum);
	values.add(case_no);
	values.add(reason_no);
	PLMDatabaseUtil.insert(insertString,values);
%>