<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.net.*" %>
<%@ page import="com.util.format.*" %>
<%@ page import="com.endeca.ui.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.plm.constants.PLMConstants" %>
<%@ page import="com.plm.util.database.PLMDatabaseUtil" %>
<html>
	<head>
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0;" />
		<title>Parolee Details</title>
	    <link href="media/style/main.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
<%
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
	
	String userName= request.getHeader("USERID");
	if(userName==null || "".equals(userName)){
		userName = request.getParameter("USERID");
		if(userName==null || "".equals(userName)){
			userName = "NULL_USERNAME";
		}
	}
	
	String ip_address= request.getHeader("X-FORWARDED-FOR");
	if(ip_address==null || "".equals(ip_address)){
			ip_address = "NULL_IPADDRESS";
	}
	
	String cdcNum=(String)tempPropsMap.get("CDC Number")!=null ?(String)tempPropsMap.get("CDC Number"):"";
	if(cdcNum == null  || "".equals(cdcNum)){
		cdcNum  = "??????";
	}

	String case_no = (String)session.getAttribute("case_no");
	String reason =(String)session.getAttribute("reason_no");
	String insertString = PLMConstants.INSERT_INTO_CORI_INFO;	
	ArrayList values = new ArrayList();	
	values.add(userName);
	values.add(ip_address);
	values.add(cdcNum);
	values.add(case_no);
	values.add(reason);
	PLMDatabaseUtil.insert(insertString,values);
%>
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
									<div class="cdcnum"><span>Parolee Details</span></div>
								</div>
								<div class="row1">
									<div class="cdcnumheader">CDC#: <span><%=tempPropsMap.get("CDC Number")%></span></div>
									<div class="nameheader floatright">Name: <span><%=tempPropsMap.get("First Name")%>&nbsp;<%=tempPropsMap.get("Last Name")%></span></div>
								</div>
								<div class="row2">
									<a href="plm_mobile_controller.jsp?page=personalInfo&<%=queryStr%>"><div class="viewmenu">View Personal Information</div></a>
								</div>
								<div class="row1">
									<a href="plm_mobile_controller.jsp?page=paroleInfo&<%=queryStr%>"><div class="viewmenu">View Parole Information</div></a>
								</div>
								<div class="row2">
									<a href="plm_mobile_controller.jsp?page=photo&<%=queryStr%>"><div class="viewmenu">View Photo</div></a>
								</div>
								<div class="row1">
									<a href="plm_mobile_controller.jsp?page=residenceInfo&<%=queryStr%>"><div class="viewmenu">View Address Information</div></a>
								</div>
								<div class="row2">
									<a href="plm_mobile_controller.jsp?page=smtInfo&<%=queryStr%>"><div class="viewmenu">View SMT Information</div></a>
								</div>
								<div class="row1">
									<a href="plm_mobile_controller.jsp?page=aliasInfo&<%=queryStr%>"><div class="viewmenu">View Alias information</div></a>
								</div>
								<div class="row2">
									<a href="plm_mobile_controller.jsp?page=monikerInfo&<%=queryStr%>"><div class="viewmenu">View Moniker information</div></a>
								</div>
								<div class="row1">
									<a href="plm_mobile_controller.jsp?page=offenseInfo&<%=queryStr%>"><div class="viewmenu">View Offense Information</div></a>
								</div>
								<div class="row2">
									<a href="plm_mobile_controller.jsp?page=jobInfo&<%=queryStr%>"><div class="viewmenu">View Job Information</div></a>
								</div>
								<div class="row1">
									<a href="plm_mobile_controller.jsp?page=vehicleInfo&<%=queryStr%>"><div class="viewmenu">View Vehicle Information</div></a>
								</div>
							</div>
						</div>
					</div>
					<div id="Footer"></div>
				</div>
			</div>
		</div>
	</body>
</html>