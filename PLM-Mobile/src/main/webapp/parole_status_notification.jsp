<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.endeca.ui.constants.UI_Props"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.plm.util.PLMSearchUtil" %>
<%@ page import="org.apache.log4j.Logger" %>
<html>
	<head>
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0;" />
		<title>Parole Status Notification</title>
		<link href="media/style/main.css" rel="stylesheet" type="text/css" />
<%
final Logger logger = Logger.getLogger(this.getClass());
	String queryStr = "R="+request.getParameter("R");
	PropertyContainer rec = (PropertyContainer)request.getAttribute("record");
	PropertyMap tempPropsMap = null;
	if(rec instanceof ERec) {
		tempPropsMap = ((ERec)rec).getProperties();
	}else {
		tempPropsMap = ((AggrERec)rec).getProperties();
	}
	tempPropsMap = ((ERec)rec).getProperties();

	String searchQuery = (String)session.getAttribute("searchQuery");
	searchQuery = searchQuery.replaceAll(" ","%20"); // for replacing space by %20
	searchQuery = searchQuery.replaceAll("\"","%22"); //for replacing double qoutes by %22
	
	String statusNotific1 = "";
 	//String statusNotific2 = "";
 	//String statusNotific3 = "";
 	//String statusNotific4 = "";
 	if (tempPropsMap.get("Status").toString().equals("SUSPENDED") == false 
			&& tempPropsMap.get("Classification Description").toString().equals("DEPORTED") == false 
			&& tempPropsMap.get("Unit Name").toString().substring(0,4).equals("MNRP") == false 
			&& tempPropsMap.get("Unit Name").toString().substring(0,3).equals("NRP") == false
			&& tempPropsMap.get("Classification Description").toString().substring(0,4).equals("MISD") == false) { 
 		statusNotific1 = "NONE";
 	}
 	if(tempPropsMap.get("Status").equals("SUSPENDED")) {
 		statusNotific1 = statusNotific1 + "This subject may be a Parolee-At-Large (PAL). Should you make contact with this subject, he or she may have absconded. Between 0800 and 1630 hours, please contact the Parole Agent of Record at the Parole Unit. After 1630 hours, weekends or Holidays, contact the Agent of Record through the Dept. of Corrections ID/Warrants Unit at (916) 324-2891 to obtain authorization for a Parole Hold per PC 3056.<br/><br/>";
 	}
 	if(tempPropsMap.get("Classification Description").equals("DEPORTED")) {
 		statusNotific1 = statusNotific1 + "This subject may be a criminal alien subject to immediate arrest by local law enforcement authorities under 8 U.S.C. section 1252c. A criminal alien is a person who has been convicted of a felony in the United States and then deported. Criminal aliens may be subject to federal penalties of up to ten years imprisonment. 8 U.S.C. section 1326. Contact US ICE at 1-802-872-6020, or via NLETS at VTINS07SO, to obtain appropriate confirmation of the subject`s status.<br/>";
 	} /* else {
 		statusNotific2 = "";
 	} */
 	if(tempPropsMap.get("Unit Name").toString().substring(0,4).equals("MNRP") || tempPropsMap.get("Unit Name").toString().substring(0,3).equals("NRP")) {
 		statusNotific1 = statusNotific1 + "This subject is on Non-Revocable Parole pursuant to Penal Code 3000.03 and does not have an assigned Parole Agent.  This subject may be searched by law enforcement, however, no verification or validation has been conducted by the California Department of Corrections and Rehabilitation on any residential information that is provided.  Pursuant to Penal Code 3000.03, the California Department of Corrections and Rehabilitations Warrant Unit cannot place a parole hold on this subject and this subject cannot be returned to custody for a violation of parole.<br/>";
 	} /* else {
 		statusNotific3 = "";
 	} */
 	/* Added Parole Status Notification message for MISD on 02/02/2015 by Vamshi Kapidi */
	if(tempPropsMap.get("Classification Description").toString().substring(0,4).equals("MISD"))  {
		statusNotific1 = statusNotific1 + "Subject on parole pursuant to PC 1170.18(d) <br/>";
	} /* else {
 		statusNotific4 = "";
 	} */
%>
		<script type="text/javascript">
<%
	String sUrl=PLMSearchUtil.getFormattedPLMURL(request,"/plm_mobile_controller.jsp") + "?page=paroleedetails&" + queryStr;
%>
		function gotoParoledetails() {
			location.href="<%=sUrl%>"; // Please make a required changes for redirect Parolee Details Page
		}
		</script>
	</head>
	<body>	
		<div align="center">
			<div id="Maindiv">
				<div id="Container">
					<div id="Header">
						<div class="logo"><span class="logo1">PAROLE</span><span class="logo2">LEADS2.0</span></div>
						<div class="top">
							<div class="back"><a href="plm_mobile_controller.jsp?<%=searchQuery%>">Back</a></div>
							<%@ include file="logout_include_mobile.jsp" %>
							<div class="home"><a href="plm_mobile_controller.jsp?page=login">Home</a></div>
						</div>
					</div>
					<div id="Middle">
						<div id="MainContainer">
							<div class="searchresulttable detailspage">
								<div class="tableheader">
									<div class="cdcnum"><span>Parole Status Notification</span></div>
								</div>
							</div>
	                        <span class="termsnormaltext">
		                        <%=statusNotific1%>		                        
	                        </span>
							<center>
                            <a href="javascript:gotoParoledetails();"><img src="media/images/global/continue_button.gif" style="border:none" width="50" style="border:none"/></a></center>
						</div>
					</div>
					<div id="Footer"></div>
				</div>
			</div>
		</div>
	</body>
</html>
