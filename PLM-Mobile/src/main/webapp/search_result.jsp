<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.endeca.ui.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.endeca.ui.logging.*" %>
<%@ page import="com.endeca.ui.charts.*" %>
<%@ page import="com.util.MapsList" %>
<%@ page import="com.report.*" %>
<%@ page import="org.dom4j.*" %>
<%@ page import="java.util.Map.Entry" %>
<%@ page import="com.plm.constants.PLMConstants" %>
<%@ page import="org.apache.log4j.Logger" %>
<%
final Logger logger = Logger.getLogger(this.getClass());
%>
<html>
	<head>
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0;" />
		<title>Search Results</title>
	    <link href="media/style/main.css" rel="stylesheet" type="text/css" />
<%
   	logger.info("mobileRequest: " + request.getQueryString());
	String caseNum = (String)request.getAttribute("username");
	session.setAttribute("case",caseNum);
	Navigation nav = (Navigation)request.getAttribute("navigation");
	ERecList recs = nav.getERecs();
	int inc = Integer.parseInt(UI_Props.getInstance().getValue(PLMConstants.DEFAULT_NUM_RESULTS_MOBILE_APP));
	int maxPages = 5;
	int numLocalRecs = recs.size();
	long startRec = nav.getERecsOffset() + 1;
	long numTotalRecs = nav.getTotalNumERecs();
	List  a1 = new ArrayList();
	Collection<String> colParams = new ArrayList<String>();
	colParams.add("doAudit");
	colParams.add("county");
	colParams.add("city");
	colParams.add("userName");
	colParams.add("ipAddress");
	colParams.add("qry_type");
%>
	</head>
	<body>
		<div align="center">
			<div id="Maindiv">
				<div id="Container">
					<div id="Header">
						<div class="logo"><span class="logo1">PAROLE</span><span class="logo2">LEADS2.0</span></div>
						<div class="top">
<%
	String prevPage = request.getParameter("prevPage");
	String backTemplate = "";
	if(prevPage.equals("dischargeNotification")){
		backTemplate = "pc290_discharge.jsp";
	}else if(prevPage.equals("registrantNotification")){
		backTemplate = "pc290_registrant.jsp";
	}else{
		backTemplate = "plm_mobile_controller.jsp?page=login";
	}
%>
							<div class="back"><a href="<%=backTemplate%>">Back</a></div>
							<%@ include file="logout_include_mobile.jsp" %>
							<div class="home"><a href="plm_mobile_controller.jsp?page=login">Home</a></div>
						</div>
					</div>
					<div id="Middle">
						<div id="MainContainer">
							<div class="searchresultheader">
								<div>Search Results</div><div class="matchrecords">Matching Records:<%=numTotalRecs%></div>
							</div>
							<div class="pagination">
<%
	if(recs != null && recs.size() > 0){
		if (numTotalRecs > inc) {
%>
								<div class="topleft">
<%
			// Compute the page set and the number of pages needed
			int activePage 	= (int)((startRec+inc-1)/inc);
			int startPage 	= (int)((((startRec/(inc*maxPages))) * maxPages) + 1);
			int endPage 	= (int)((numTotalRecs/inc) + 1);
			// Exception for even number sets
			if ((numTotalRecs%inc) == 0) {
				endPage--;
			}
			// Truncate number of pages if overridden with local constant
			// and determine if there is a next page set
			int finalPage = endPage;
			if (endPage > (startPage + maxPages - 1)) {
				endPage = startPage + maxPages - 1;
			}
			// Determine if there is a previous page set
			if (startPage > 1) {
				// Create previous page set request
				String off = Long.toString((startPage - maxPages - 1)*inc);
				UrlGen urlg = new UrlGen(request.getQueryString(), "UTF-8");
				urlg.removeParams(colParams);
				urlg.removeParam("No");
				urlg.removeParam("sid");
				urlg.removeParam("in_dym");
				urlg.removeParam("in_dim_search");
				urlg.removeParam("refineTabID");
				urlg.addParam("No",off);
				urlg.addParam("sid",(String)request.getAttribute("sid"));
				String url = "plm_mobile_controller.jsp"+"?"+urlg;	
%>
									<a href='<%=url%>'>&laquo;</a>&nbsp;
<%
			}
			// Determine if there is a previous page
			if (activePage > 1) {
				// Create previous page request
				String off = Long.toString(startRec - inc - 1);
				UrlGen urlg = new UrlGen(request.getQueryString(), "UTF-8");
				urlg.removeParams(colParams);
				urlg.removeParam("No");
				urlg.removeParam("sid");
				urlg.removeParam("in_dym");
				urlg.removeParam("in_dim_search");
				urlg.removeParam("refineTabID");
				urlg.addParam("No",off);
				urlg.addParam("sid",(String)request.getAttribute("sid"));
				String url = "plm_mobile_controller.jsp"+"?"+urlg;	
%>				
									<a href='<%=url%>'>Prev</a>&nbsp;
<%
			}
			// Create direct page index
			for (int i = startPage; i <= endPage; i++) {
				if (i == activePage) {
%>
									<span><%=i%></span>&nbsp;
<%
				}else {
					// Create direct page request
					String off = Long.toString((i-1)*inc);
					UrlGen urlg = new UrlGen(request.getQueryString(), "UTF-8");
					urlg.removeParams(colParams);
					urlg.removeParam("No");
					urlg.removeParam("sid");
					urlg.removeParam("in_dym");
					urlg.removeParam("in_dim_search");
					urlg.removeParam("refineTabID");
					urlg.addParam("No",off);
					urlg.addParam("sid",(String)request.getAttribute("sid"));
					String url = "plm_mobile_controller.jsp"+"?"+urlg;	
%>
									<a href='<%=url%>'><%=i%></a>&nbsp;
<%
				}
			}
			// Determine if there is a next page
			if (finalPage > activePage){
				// Create next page request
				String off = Long.toString(startRec + inc - 1);
				UrlGen urlg = new UrlGen(request.getQueryString(), "UTF-8");
				urlg.removeParams(colParams);
				urlg.removeParam("No");
				urlg.removeParam("sid");
				urlg.removeParam("in_dym");
				urlg.removeParam("in_dim_search");
				urlg.removeParam("refineTabID");
				urlg.addParam("No",off);
				urlg.addParam("sid",(String)request.getAttribute("sid"));
				String url = "plm_mobile_controller.jsp"+"?"+urlg;	
%>
									<a href='<%=url%>'>Next</a>&nbsp;
<%
			}
			// Determine if there is a next page set
			if (finalPage > (startPage + maxPages - 1)){
				// Create next page set
				String off = Long.toString(endPage*inc);
				UrlGen urlg = new UrlGen(request.getQueryString(), "UTF-8");
				urlg.removeParams(colParams);
				urlg.removeParam("No");
				urlg.removeParam("sid");
				urlg.removeParam("in_dym");
				urlg.removeParam("in_dim_search");
				urlg.removeParam("refineTabID");
				urlg.addParam("No",off);
				urlg.addParam("sid",(String)request.getAttribute("sid"));
				String url = "plm_mobile_controller.jsp"+"?"+urlg;	
%>
									<a href='<%=url%>'>&raquo;</a>
<%
			}
%>
								</div>
<%
		}
	}	
%>
							</div>
							<div class="searchresulttable">
								<div class="tableheader">
									<div class="caseno">CDC#</div><div class="name">Name</div>
								</div>
<%
	String firstName ="";
	String lastName ="";
    // these params are required when we do back from parolee details page so adding those
	Map requestParamMap =request.getParameterMap();
	Set set = requestParamMap.entrySet();
	Iterator iter = set.iterator();

	String params = "";
	while (iter != null && iter.hasNext()){
			Entry n = (Entry) iter.next();
			String[] arr =(String[])n.getValue();
			if(!colParams.contains(n.getKey()))
				params = params+"&"+n.getKey().toString()+"="+arr[0];
	}
	session.setAttribute("searchQuery",params);
	for(int i =0; i < recs.size();i++){
		ERec rec = (ERec)recs.get(i);
		PropertyMap propsMap = ((ERec)rec).getProperties();
		
		String statusNotific1 = "";
	 	String statusNotific2 = "";
	 	String statusNotific3 = "";
	 	String statusNotific4 = "";
	 	if (propsMap.get("Status").toString().equals("SUSPENDED") == false 
				&& propsMap.get("Classification Description").toString().equals("DEPORTED") == false 
				&& propsMap.get("Unit Name").toString().substring(0,4).equals("MNRP") == false 
				&& propsMap.get("Unit Name").toString().substring(0,3).equals("NRP") == false
				&& propsMap.get("Classification Description").toString().substring(0,4).equals("MISD") == false) { 
	 		statusNotific1 = "NONE";
	 	}
	 	if(propsMap.get("Status").equals("SUSPENDED")) {
	 		statusNotific1 = "This subject may be a Parolee-At-Large (PAL). Should you make contact with this subject, he or she may have absconded. Between 0800 and 1630 hours, please contact the Parole Agent of Record at the Parole Unit. After 1630 hours, weekends or Holidays, contact the Agent of Record through the Dept. of Corrections ID/Warrants Unit at (916) 324-2891 to obtain authorization for a Parole Hold per PC 3056.<br/><br/>";
	 	}
	 	if(propsMap.get("Classification Description").equals("DEPORTED")) {
	 		statusNotific2 = "This subject may be a criminal alien subject to immediate arrest by local law enforcement authorities under 8 U.S.C. section 1252c. A criminal alien is a person who has been convicted of a felony in the United States and then deported. Criminal aliens may be subject to federal penalties of up to ten years imprisonment. 8 U.S.C. section 1326. Contact US ICE at 1-802-872-6020, or via NLETS at VTINS07SO, to obtain appropriate confirmation of the subject`s status.<br/>";
	 	} else {
	 		statusNotific2 = "";
	 	}
	 	if(propsMap.get("Unit Name").toString().substring(0,4).equals("MNRP") || propsMap.get("Unit Name").toString().substring(0,3).equals("NRP")) {
	 		statusNotific3 = "This subject is on Non-Revocable Parole pursuant to Penal Code 3000.03 and does not have an assigned Parole Agent.  This subject may be searched by law enforcement, however, no verification or validation has been conducted by the California Department of Corrections and Rehabilitation on any residential information that is provided.  Pursuant to Penal Code 3000.03, the California Department of Corrections and Rehabilitations Warrant Unit cannot place a parole hold on this subject and this subject cannot be returned to custody for a violation of parole.<br/>";
	 	} else {
	 		statusNotific3 = "";
	 	}
	 	if(propsMap.get("Classification Description").toString().substring(0,4).equals("MISD"))  {
	 		statusNotific4 = "Subject on parole pursuant to PC 1170.18(d) <br/>";
		}  else {
	 		statusNotific4 = "";
	 	} 
	 	
	 	String notif = statusNotific1 + statusNotific2 + statusNotific3 + statusNotific4;
	 	
		UrlGen urlg = new UrlGen("", "UTF-8");
		urlg.addParam("R",rec.getSpec());
		
		if(notif.equals("NONE")) {
			urlg.addParam("page","paroleedetails");
	 	} else {
	 		urlg.addParam("page","mobileDetails");
	 	}
		String url = "plm_mobile_controller.jsp?"+urlg;
		
		if(i%2 == 0){
%>
								<div class="row1">
									<a href="<%=url%>">
										<div class="caseno"><%=propsMap.get("CDC Number")!=null?propsMap.get("CDC Number"):""%></div>
										<div class="name"><%=propsMap.get("First Name")!=null?propsMap.get("First Name")+" ":" "%><%=propsMap.get("Last Name")!=null?propsMap.get("Last Name"):""%></div>
									</a>
								</div>
<%
		}else{
%>
								<div class="row2">
									<a href="<%=url%>">
									<div class="caseno"><%=propsMap.get("CDC Number")!=null?propsMap.get("CDC Number"):""%></div>
										<div class="name"><%=propsMap.get("First Name")!=null?propsMap.get("First Name")+" ":" "%><%=propsMap.get("Last Name")!=null?propsMap.get("Last Name"):""%></div>
									</a>
								</div>
<%
		}
	}
%>
							</div>
						</div>
					</div>
					<div id="Footer"></div>
				</div>
			</div>
		</div>
	</body>
</html>