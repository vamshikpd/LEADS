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
		<title>Offense Information</title>
		<link href="media/style/main.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
<%
	String spec ="";
	PropertyContainer rec = (PropertyContainer)request.getAttribute("record");
	PropertyMap tempPropsMap = null;
	if(rec instanceof ERec) {
		spec = ((ERec)rec).getSpec();
		tempPropsMap = ((ERec)rec).getProperties();
	}else {
		spec = ((AggrERec)rec).getSpec();
		tempPropsMap = ((AggrERec)rec).getProperties();
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
<%
	Collection offenseInfoColl = tempPropsMap.getValues("Offense Information");
	Iterator offenseInfoCollIter = (offenseInfoColl != null)?offenseInfoColl.iterator():null;

	int cnt = 0;
	String srow = "";
	while (offenseInfoCollIter != null && offenseInfoCollIter.hasNext()){
		cnt++;
		if (cnt%2 != 0){
			srow = "row1";
		}else{
			srow = "row2";
		}
%>
								<div class="<%=srow%>">
<%
		String offInfo = (String)offenseInfoCollIter.next();
		HashMap hmapOff = new HashMap();
		//String[] offInfo_result = offInfo.split("\\<\\>");
		String[] offInfo_result = offInfo.split(PLMConstants.SEPARATOR);
		if (offInfo_result.length > 0 && (String)offInfo_result[0] != null && !((String)offInfo_result[0]).equals("")){
			hmapOff.put("Offense Code", (String)offInfo_result[0]);
		}else{
			hmapOff.put("Offense Code", "");
		}			
		if (offInfo_result.length > 4 && (String)offInfo_result[4] != null && !((String)offInfo_result[4]).equals("")){
			hmapOff.put("Controlling Offense", (String)offInfo_result[4]);
		}else{
			hmapOff.put("Controlling Offense", "");
		}
		if (offInfo_result.length > 1 && (String)offInfo_result[1] != null && !((String)offInfo_result[1]).equals("")){
			hmapOff.put("Description", (String)offInfo_result[1]);
		}else{
			hmapOff.put("Description", "");
		}
%>
									<div class="name1">Offense Code</div><div class="value1"><%=hmapOff.get("Offense Code")%></div>
									<div class="name1">Description</div><div class="value1"><%=hmapOff.get("Description")%></div>
									<div class="name1">Controlling Offense</div><div class="value1"><%=hmapOff.get("Controlling Offense")%></div>
								</div>
<%
	}
%>
<%
	if(cnt == 0 ){
%>
								<div class="row2">
									<div class="name1">Offense</div>
									<div class="value1">NONE</div>
								</div>
<%
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