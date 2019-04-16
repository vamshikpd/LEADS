<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.net.*" %>
<%@ page import="com.util.format.*" %>
<%@ page import="com.endeca.ui.*" %>
<%@ page import="com.plm.constants.PLMConstants" %>
<html>
	<head>
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0;" />
		<title>SMT Information</title>
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
	Collection smtInfoColl = tempPropsMap.getValues("SMT Information");
	Iterator smtInfoCollIter = (smtInfoColl != null)?smtInfoColl.iterator():null;

	int cnt = 0;
	String srow = "";
	while (smtInfoCollIter != null && smtInfoCollIter.hasNext()){
		cnt++;
		if (cnt%2==0){
			srow = "row2";
		}else{
			srow = "row1";
		}
%>
								<div class="<%=srow%>">
<%
		String smtInfo = (String)smtInfoCollIter.next();
		HashMap hmapSmt = new HashMap();
		String[] smtInfo_result = smtInfo.split(PLMConstants.SEPARATOR);
			
		if (smtInfo_result.length > 0 && (String)smtInfo_result[0] != null && !((String)smtInfo_result[0]).equals("")){
			hmapSmt.put("Code/Location", (String)smtInfo_result[0]);
		}else{
			hmapSmt.put("Code/Location", "&nbsp;");
		}				
		if (smtInfo_result.length > 2 && (String)smtInfo_result[2] != null && !((String)smtInfo_result[2]).equals("")){
			hmapSmt.put("Picture", (String)smtInfo_result[2]);
		}else{
			hmapSmt.put("Picture", "&nbsp;");
		}
		if (smtInfo_result.length > 3 && (String)smtInfo_result[3] != null && !((String)smtInfo_result[3]).equals("")){
			hmapSmt.put("Text", (String)smtInfo_result[3]);
		}else{
			hmapSmt.put("Text", "&nbsp;");
		}
%>
								 	<div class="name1">Code/Location</div><div class="value1"><%=hmapSmt.get("Code/Location")%></div>
									<div class="name1">Picture</div><div class="value1"><%=hmapSmt.get("Picture")%></div>
									<div class="name1">Text</div><div class="value1"><%=hmapSmt.get("Text")%></div>
								</div>
<%
	}// end while
	
	if(cnt == 0 ){
%>
								<div class="row2">
									<div class="name1">SMT</div>
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