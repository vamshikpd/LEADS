<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.net.*" %>
<%@ page import="com.util.format.*" %>
<%@ page import="com.endeca.ui.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.endeca.ui.logging.*" %>
<%@ page import="com.endeca.ui.charts.*" %>
<%@ page import="com.util.MapsList" %>
<%@ page import="com.report.*" %>
<%@ page import="org.dom4j.*" %>
<%@ page import="com.plm.constants.PLMConstants" %>
<html>
	<head>
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0;" />
		<title>Parole Information</title>
		<link href="media/style/main.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
<%
	String spec ="";
	PropertyContainer rec = (PropertyContainer)request.getAttribute("record");
	PropertyMap tempPropsMap = null;
	if(rec instanceof ERec) {
		tempPropsMap = ((ERec)rec).getProperties();
		spec = ((ERec)rec).getSpec();
	}else {
		tempPropsMap = ((AggrERec)rec).getProperties();
		spec = ((AggrERec)rec).getSpec();
	}
	
	tempPropsMap = ((ERec)rec).getProperties();
	String sAgentEmail=(String)tempPropsMap.get("Agent Email")!=null ?(String)tempPropsMap.get("Agent Email"):"";
	String cdcNum=(String)tempPropsMap.get("CDC Number")!=null ?(String)tempPropsMap.get("CDC Number"):"";
	String firstName=(String)tempPropsMap.get("First Name")!=null ?(String)tempPropsMap.get("First Name"):"";
	String lastName=(String)tempPropsMap.get("Last Name")!=null ?(String)tempPropsMap.get("Last Name"):"";
	//Added below code for agent name is empty  on 03/06/2017 - vamshi kapidi
	String sAgntNm = null;
	if(((String)tempPropsMap.get("Agent Name")).equals(",")){
		sAgntNm = "";
	}else{
		sAgntNm = (String)tempPropsMap.get("Agent Name")!=null?(String)tempPropsMap.get("Agent Name"):"";
	}

	
%>
		<div align="center">
			<div id="Maindiv">
				<div id="Container">
					<div id="Header">
						<div class="logo"><span class="logo1">PAROLE</span><span class="logo2">LEADS2.0</span></div>
						<div class="top">
							<div class="back"><a href="plm_mobile_controller.jsp?page=paroleedetails&R=<%=spec%>">Back</a></div>
							<%@ include file="logout_include_mobile.jsp" %>
							<div class="home"><a href="plm_mobile_controller.jsp?page=login">Home</a></div>
						</div>
					</div>
					<div id="Middle">
						<div id="MainContainer">
							<div class="searchresulttable detailspage">
								<div class="row1">
									<%@ include file="essence_info.jsp" %>
								</div>
								<div class="row2">
									<div class="name1">Parole Status</div>
									<div class="value1"><%=tempPropsMap.get("Status")!=null?tempPropsMap.get("Status"):""%></div>
								</div>
								<div class="row1">
									<div class="name1">Parole Date</div>
									<div class="value1"><%=tempPropsMap.get("Parole Date Display")!=null?tempPropsMap.get("Parole Date Display"):""%></div>
								</div>
								<div class="row2">
									<div class="name1">Maximum Discharge Date</div>
									<div class="value1"><%=tempPropsMap.get("Control Discharg Date")!=null?tempPropsMap.get("Control Discharg Date"):""%></div>
								</div>
								<div class="row1">
									<div class="name1">Revocation Release Date</div>
									<div class="value1"><%=tempPropsMap.get("Revocation Release Date")!=null?tempPropsMap.get("Revocation Release Date"):""%></div>
								</div>
								<div class="row2">
									<div class="name1">Supervision Level</div>
									<div class="value1"><%=tempPropsMap.get("Classification Description")!=null?tempPropsMap.get("Classification Description"):""%></div>
								</div>
								<div class="row1">
									<div class="name1">Commitment County</div>
									<div class="value1"><%=tempPropsMap.get("County Commit")!=null?tempPropsMap.get("County Commit"):""%></div>
								</div>
								<div class="row2">
									<div class="name1">Parole Unit</div>
									<div class="value1"><%=tempPropsMap.get("Unit Name")!=null?tempPropsMap.get("Unit Name"):""%></div>
								</div>
								<div class="row1">
									<div class="name1">Agent Name</div>
									<div class="value1"><%=sAgntNm%></div>
								</div>
								<div class="row2">
									<div class="name1">Agent Telephone</div>
									<div class="value1"><%=tempPropsMap.get("Agent Phone")!=null?tempPropsMap.get("Agent Phone"):""%></div>
								</div>
								<div class="row1">
									<div class="name1">Agent e-mail</div>
									<div class="value1"><a href="plm_mobile_controller.jsp?page=emailAgent&R=<%=spec %>&toAddress=<%=sAgentEmail%>&cdcNum=<%=cdcNum%>&firstName=<%=firstName%>&lastName=<%=lastName%>"><%=tempPropsMap.get("Agent Email")!=null?tempPropsMap.get("Agent Email"):""%></a></div>
								</div>
								<div class="row2">
									<div class="name1">Registration &amp; Notice Information</div>
									<div class="value1">
										HS 11590 - <%=tempPropsMap.get("HS REQ")!=null?tempPropsMap.get("HS REQ"):""%> <%=tempPropsMap.get("HS DATE")!=null?" - "+tempPropsMap.get("HS DATE"):""%><br /> 
										PC290 - <%=tempPropsMap.get("PC 290 REQ")!=null?tempPropsMap.get("PC 290 REQ"):""%> <%=tempPropsMap.get("PC 290 DATE")!=null?" - "+tempPropsMap.get("PC 290 DATE"):""%><br /> 
										PC457.1 - <%=tempPropsMap.get("PC 457 REQ")!=null?tempPropsMap.get("PC 457 REQ"):""%> <%=tempPropsMap.get("PC 457 DATE")!=null?" - "+tempPropsMap.get("PC 457 DATE"):""%><br /> 
										PC3058.6 - <%=tempPropsMap.get("PC 3058 REQ")!=null?tempPropsMap.get("PC 3058 REQ"):""%> <%=tempPropsMap.get("PC 3058 DATE")!=null?" - "+tempPropsMap.get("PC 3058 DATE"):""%>
									</div>
								</div>
								<div class="row1">
									<div class="name1">Special Parole Conditions</div>
									<div class="value1">
										Drug testing - <%=tempPropsMap.get("ANT REQ")!=null?tempPropsMap.get("ANT REQ"):""%><br /> 
										No Alcohol - <%=tempPropsMap.get("No Alcohol")!=null?tempPropsMap.get("No Alcohol"):""%><br /> 
										POC - <%=tempPropsMap.get("POC REQ")!=null?tempPropsMap.get("POC REQ"):""%> 
									</div>
								</div>
								<div class="row2">
									<div class="name1">Comments</div>
									<div class="value1"><%=tempPropsMap.get("Comments")!=null?tempPropsMap.get("Comments"):"NONE"%></div>
								</div>
								<div class="row1">
									<div class="name1">Other Special Parole Conditions</div>
									<div class="value1">
<%
	Collection spInfoColl = tempPropsMap.getValues("Special Condition Information");
	if (spInfoColl != null && spInfoColl.size()>0){
		Iterator spInfoCollIter = (spInfoColl != null)?spInfoColl.iterator():null;
		int cnt = 0;
		while (spInfoCollIter != null && spInfoCollIter.hasNext()){
			cnt++;
			HashMap hmapSp = new HashMap();
			String spInfo = (String)spInfoCollIter.next();
			//String[] spInfo_result = spInfo.split("\\<\\>");
			String[] spInfo_result = spInfo.split(PLMConstants.SEPARATOR);
			if ((String)spInfo_result[0] != null && !((String)spInfo_result[0]).equals("")){
				hmapSp.put("Special Comment", (String)spInfo_result[0]);
			}else{
				hmapSp.put("Special Comment", "&nbsp;");
			}
%>
										<%=hmapSp.get("Special Comment")%><br/>
<%
		}//end while 
%>
									</div>
<% 	
	}else{
%>
										NONE
<%
	}
%>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div id="Footer"></div>
			</div>
		</div>
	</body>
</html>
