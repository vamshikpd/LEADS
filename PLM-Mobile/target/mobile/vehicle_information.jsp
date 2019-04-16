<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.util.*" %>
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
		<title>Vehicle Information</title>
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
	Collection vehInfoColl = tempPropsMap.getValues("Vehicle Information");
	Iterator vehInfoCollIter = (vehInfoColl != null)?vehInfoColl.iterator():null;
	int cnt = 0;
	String srow = "";
	while (vehInfoCollIter != null && vehInfoCollIter.hasNext()){
		HashMap hmapVeh = new HashMap();
		String vehInfo = (String)vehInfoCollIter.next();
		String[] vehInfo_result = vehInfo.split(PLMConstants.SEPARATOR);
		if ((String)vehInfo_result[1] != null && !((String)vehInfo_result[1]).equals("")){
			hmapVeh.put("Make", (String)vehInfo_result[1]);
		}else{
			hmapVeh.put("Make", "&nbsp;");
		}
		if ((String)vehInfo_result[3] != null && !((String)vehInfo_result[3]).equals("")){
			hmapVeh.put("Model", (String)vehInfo_result[3]);
		}else{
			hmapVeh.put("Model", "&nbsp;");
		}
		if ((String)vehInfo_result[5] != null && !((String)vehInfo_result[5]).equals("")){
			hmapVeh.put("Style", (String)vehInfo_result[5]);
		}else{
			hmapVeh.put("Style", "&nbsp;");
		}
		if ((String)vehInfo_result[6] != null && !((String)vehInfo_result[6]).equals("")){
			hmapVeh.put("Vehicle Class", (String)vehInfo_result[6]);
		}else{
			hmapVeh.put("Vehicle Class", "&nbsp;");
		}
		if ((String)vehInfo_result[7] != null && !((String)vehInfo_result[7]).equals("")){
			hmapVeh.put("Year", (String)vehInfo_result[7]);
		}else{
			hmapVeh.put("Year", "&nbsp;");
		}
		if ((String)vehInfo_result[9] != null && !((String)vehInfo_result[9]).equals("")){
			hmapVeh.put("Color 1", (String)vehInfo_result[9]);
		}else{
			hmapVeh.put("Color 1", "&nbsp;");
		}
		if ((String)vehInfo_result[11] != null && !((String)vehInfo_result[11]).equals("")){
			hmapVeh.put("Color 2", (String)vehInfo_result[11]);
		}else{
			hmapVeh.put("Color 2", "&nbsp;");
		}
		if ((String)vehInfo_result[12] != null && !((String)vehInfo_result[12]).equals("")){
			hmapVeh.put("License Plate", (String)vehInfo_result[12]);
		}else{
			hmapVeh.put("License Plate", "&nbsp;");
		}
		if ((String)vehInfo_result[14] != null && !((String)vehInfo_result[4]).equals("")){
			hmapVeh.put("State", (String)vehInfo_result[14]);
		}else{
			hmapVeh.put("State", "&nbsp;");
		}
		if ((String)vehInfo_result[17] != null && !((String)vehInfo_result[17]).equals("")){
			hmapVeh.put("Owned", (String)vehInfo_result[17]);
		}else{
			hmapVeh.put("Owned", "&nbsp;");
		}
		cnt++;
		if (cnt%2 != 0){
			srow = "row1";
		}else{
			srow = "row2";
		}
%>
								<div class="<%=srow%>">
									<div class="name1">Make</div><div class="value1"><%=hmapVeh.get("Make")%></div>
									<div class="name1">Model</div><div class="value1"> <%=hmapVeh.get("Model")%></div>
									<div class="name1">Style</div><div class="value1"> <%=hmapVeh.get("Style")%></div>
									<div class="name1">Class</div><div class="value1"><%=hmapVeh.get("Vehicle Class")%></div>
									<div class="name1">Year</div><div class="value1"><%=hmapVeh.get("Year")%></div>
									<div class="name1">Color 1</div><div class="value1"><%=hmapVeh.get("Color 1")%></div>
									<div class="name1">Color 2</div><div class="value1"><%=hmapVeh.get("Color 2")%></div>
									<div class="name1">License Plate</div><div class="value1"><%=hmapVeh.get("License Plate")%></div>
									<div class="name1">State</div><div class="value1"><%=hmapVeh.get("State")%></div>
									<div class="name1">Owned</div><div class="value1"><%=hmapVeh.get("Owned")%></div>
								</div>
<%
	}
	if(cnt == 0 ){
%>
								<div class="row2">
									<div class="name1">Vehicle</div>
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