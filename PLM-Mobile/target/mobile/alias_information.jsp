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
		<title>Alias Information</title>
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
%>
		<div align="center">
			<div id="Maindiv">
				<div id="Container">
					<div id="Header">
						<div class="logo">
							<span class="logo1">PAROLE</span>
							<span class="logo2">LEADS2.0</span>
						</div>
						<div class="top">
							<div class="back">
								<a href="plm_mobile_controller.jsp?page=paroleedetails&R=<%=spec%>">Back</a>
							</div>
							<%@ include file="logout_include_mobile.jsp" %>
							<div class="home">
								<a href="plm_mobile_controller.jsp?page=login">Home</a>
							</div>
						</div>
					</div>
					<div id="Middle">
						<div id="MainContainer">
							<div class="searchresulttable detailspage">
								<div class="row1">
									<%@ include file="essence_info.jsp" %>
								</div>
<%
	Collection aliasInfoColl = tempPropsMap.getValues("Alias Info");
	Iterator aliasInfoCollIter = (aliasInfoColl != null)?aliasInfoColl.iterator():null;
	int cnt = 0;
	String srow = "";
	while (aliasInfoCollIter != null && aliasInfoCollIter.hasNext()){
		cnt++;
		if (cnt%2 != 0){
			srow = "row1";
		}else{
			srow = "row2";
		}
%>
								<div class="<%=srow%>">
<%
		String aliasInfo = (String)aliasInfoCollIter.next();
		HashMap hmapAlias = new HashMap();
		String[] aliasInfo_result = aliasInfo.split(PLMConstants.SEPARATOR);
		if (aliasInfo_result.length > 0 && (String)aliasInfo_result[0] != null && !((String)aliasInfo_result[0]).equals("")){
			hmapAlias.put("Alias Last Name", (String)aliasInfo_result[0]);
		}else{
			hmapAlias.put("Alias Last Name", " ");
		}
		if (aliasInfo_result.length > 1 && (String)aliasInfo_result[1] != null && !((String)aliasInfo_result[1]).equals("")){
			hmapAlias.put("Alias First Name", (String)aliasInfo_result[1]);
		}else{
			hmapAlias.put("Alias First Name", " ");
		}
		if (aliasInfo_result.length > 2 && (String)aliasInfo_result[2] != null && !((String)aliasInfo_result[2]).equals("")){
			hmapAlias.put("Alias Middle Name", (String)aliasInfo_result[2]);
		}else{
			hmapAlias.put("Alias Middle Name", " ");
		}
%>
									<div class="name1">Alias Last First Middle Name</div>
									<div class="value1"><%=hmapAlias.get("Alias Last Name")%>&nbsp;<%=hmapAlias.get("Alias First Name")%>&nbsp;<%= hmapAlias.get("Alias Middle Name")%></div>
								</div>
<%
	}//end while
	if(cnt == 0 ){
%>
								<div class="row2">
									<div class="name1">Alias Last First Middle Name</div>
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