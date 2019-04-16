<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.net.*" %>
<%@ page import="com.util.format.*" %>
<%@ page import="com.endeca.ui.*" %>
<%
	Navigation nav = (Navigation)request.getAttribute("navigation");
	ENEQuery usq = (ENEQuery)request.getAttribute("eneQuery");
	ENEQueryResults qr = (ENEQueryResults)request.getAttribute("eneQueryResults");

	PropertyContainer rec = qr.getERec();

	if(rec == null)
		rec = qr.getAggrERec();

	PropertyMap tempPropsMap = null;
	String spec ="";
	if(rec instanceof ERec) {
		spec = ((ERec)rec).getSpec();
		tempPropsMap = ((ERec)rec).getProperties();	//sk
	}else {
		spec = ((AggrERec)rec).getSpec();
		tempPropsMap = ((AggrERec)rec).getProperties();	//sk
	}
%>
<html>
	<head>
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0;" />
		<title>Personal Information</title>
		<link href="media/style/main.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
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
									<div class="name1">Last Name</div><div class="value1"><%=tempPropsMap.get("Last Name")!=null?tempPropsMap.get("Last Name"):""%></div>
								</div>
								<div class="row1">
									<div class="name1">First Name</div><div class="value1"><%=tempPropsMap.get("First Name")!=null?tempPropsMap.get("First Name"):""%></div>
								</div>
								<div class="row2">
									<div class="name1">Middle Name</div><div class="value1"><%=tempPropsMap.get("Middle Name")!=null?tempPropsMap.get("Middle Name"):"&nbsp;"%></div>
								</div>
								<div class="row1">
									<div class="name1">Birth Date</div><div class="value1"><%=tempPropsMap.get("Birth Date Display")!=null?tempPropsMap.get("Birth Date Display"):"&nbsp;"%></div>
								</div>
								<div class="row2">
									<div class="name1">Birth State</div><div class="value1"><%=tempPropsMap.get("Birth State Name")!=null?tempPropsMap.get("Birth State Name"):"&nbsp;"%></div>
								</div>
								<div class="row1">
									<div class="name1">Gender</div><div class="value1"><%=tempPropsMap.get("Sex")!=null?tempPropsMap.get("Sex"):"&nbsp;"%></div>
								</div>
								<div class="row2">
									<div class="name1">Ethnicity</div><div class="value1"><%=tempPropsMap.get("Race")!=null?tempPropsMap.get("Race"):"&nbsp;"%></div>
								</div>
								<div class="row1">
									<div class="name1">FBI#</div>
									<div class="value1"><%=tempPropsMap.get("FBI Number")!=null?tempPropsMap.get("FBI Number"):"&nbsp;"%></div>
								</div>
								<div class="row2">
									<div class="name1">CII#</div>
									<div class="value1"><%=tempPropsMap.get("CII Number")!=null?tempPropsMap.get("CII Number"):"&nbsp;"%></div>
								</div>
								<div class="row1">
									<div class="name1">SSA#</div>
									<div class="value1"><%=tempPropsMap.get("SSA Number")!=null?tempPropsMap.get("SSA Number"):"&nbsp;"%></div>
								</div>
								<div class="row2">
									<div class="name1">USINS#</div>
									<div class="value1"><%=tempPropsMap.get("USINS Number")!=null?tempPropsMap.get("USINS Number"):"&nbsp;"%></div>
								</div>
								<div class="row1">
									<div class="name1">Drivers License#</div>
									<div class="value1"><%=tempPropsMap.get("Driver License Number")!=null?tempPropsMap.get("Driver License Number"):"&nbsp;"%></div>
								</div>
<%
	String hei1 = null;
	if (tempPropsMap.get("Height Feet")!=null){
	  hei1 = tempPropsMap.get("Height Feet") + "'" + tempPropsMap.get("Height Inches") + "\"";
	}else{
	  hei1 = "&nbsp;";
	}
%>
								<div class="row1">
									<div class="name1">Height</div><div class="value1"><%=hei1%></div>
								</div>
								<div class="row2">
									<div class="name1">Weight</div><div class="value1"><%=tempPropsMap.get("P_Weight")!=null?tempPropsMap.get("P_Weight"):"&nbsp;"%></div>
								</div>
								<div class="row1">
									<div class="name1">Hair Color</div><div class="value1"><%=tempPropsMap.get("Haircolor")!=null?tempPropsMap.get("Haircolor"):"&nbsp;"%></div>
								</div>
								<div class="row2">
									<div class="name1">Eye Color</div><div class="value1"><%=tempPropsMap.get("Eyecolor")!=null?tempPropsMap.get("Eyecolor"):"&nbsp;"%></div>
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
