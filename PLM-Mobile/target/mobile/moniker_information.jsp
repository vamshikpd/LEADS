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
		<title>Moniker Information</title>
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
	Collection monikerInfoColl = tempPropsMap.getValues("Moniker Info");
	StringBuffer sbf = new StringBuffer();
	if (monikerInfoColl != null && monikerInfoColl.size() > 0){
		Iterator monikerInfoCollIter = (monikerInfoColl != null)?monikerInfoColl.iterator():null;
		while (monikerInfoCollIter != null && monikerInfoCollIter.hasNext()){
			String moniInfo = (String)monikerInfoCollIter.next();
			String[] moniInfo_result = moniInfo.split(PLMConstants.SEPARATOR);
			if (moniInfo_result.length > 0 && (String)moniInfo_result[0] != null && !((String)moniInfo_result[0]).equals("")){
				sbf.append(" / ");
				sbf.append((String)moniInfo_result[0]);
			}
			// second filed is last change date, that we do not need to display...it's for data download
		}
	}
%>
								<div class="row2">
									<div class="name1">Moniker</div>
									<div class="value1"><%=sbf.length()>0?sbf.toString().substring(3):"NONE"%></div>
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