<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.endeca.ui.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.endeca.ui.logging.*" %>
<%@ page import="com.endeca.ui.charts.*" %>
<%@ page import="com.util.MapsList" %>
<%@ page import="com.report.*" %>
<%@ page import="org.dom4j.*" %>
<%@ page import="com.plm.util.database.PLMDatabaseUtil" %>
<html>
	<head>
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0;" />
		<title>Photo</title>
		<link href="media/style/main.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
<%
	String spec = "";
	PropertyContainer rec = (PropertyContainer)request.getAttribute("record");
	PropertyMap tempPropsMap = null;
	if(rec instanceof ERec) {
		tempPropsMap = ((ERec)rec).getProperties();
		spec = ((ERec)rec).getSpec();
	}else {
		tempPropsMap = ((AggrERec)rec).getProperties();
		spec = ((AggrERec)rec).getSpec();
	}	
	String showid = PLMDatabaseUtil.getPrimaryMugshotID(spec);
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
								<div class="photo">
									<img src="image.jsp?psize=p&showid=<%=showid%>" alt="<%=spec%>" />
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
