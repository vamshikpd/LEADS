<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.endeca.ui.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.endeca.ui.logging.*" %>
<%@ page import="com.endeca.ui.charts.*" %>
<%@ page import="com.util.MapsList" %>
<%@ page import="com.report.*" %>
<%@ page import="org.dom4j.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.net.*" %>
<%@ page import="com.plm.constants.PLMConstants" %>
<html>
	<head>
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0;" />
		<title>Address Information</title>
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
								<div class="row2">
<%
	String strDiscDate = null;
	strDiscDate = (String)tempPropsMap.get("Address Effective Date");
	if(strDiscDate == null || strDiscDate.trim().length() == 0) {
		strDiscDate = "";
	}

	String currAdd = "";
	if (tempPropsMap.get("Care of (live with)") != null) 
		currAdd = currAdd + tempPropsMap.get("Care of (live with)") + "<br/>";
	if (tempPropsMap.get("Street") != null) 
		currAdd = currAdd + tempPropsMap.get("Street") + "<br/>";
	if (tempPropsMap.get("City") != null) 
		currAdd = currAdd + tempPropsMap.get("City");
	if (tempPropsMap.get("County Code") != null) 
		currAdd = currAdd + ", " + tempPropsMap.get("County Code");
	if (tempPropsMap.get("State Code") != null) 
		currAdd = currAdd + ", " + tempPropsMap.get("State Code");
	if (tempPropsMap.get("Zip") != null) 
		currAdd = currAdd + ", " + tempPropsMap.get("Zip");
	if (tempPropsMap.get("Zip4") != null) 
		currAdd = currAdd + "-" + tempPropsMap.get("Zip4") + "<br/>";
	else 
		currAdd = currAdd + "<br/>";
	if (tempPropsMap.get("Map") != null) 
		currAdd = currAdd + "Map Reference:" + tempPropsMap.get("Map") + "<br/>";
	if (tempPropsMap.get("Full Phone") != null)					 
		currAdd = currAdd + "Phone:" + tempPropsMap.get("Full Phone").toString()+"<br/>";
	if (tempPropsMap.get("Message Phone") != null) 
		currAdd = currAdd + "Msg Phone:" + tempPropsMap.get("Message Phone").toString()+"<br/>";

	if("".equals(currAdd.trim())){
		currAdd = "NONE";
	}
	String queryStr = "page=googleMap&"+"R="+request.getParameter("R");
	String geocode = (String)tempPropsMap.get(UI_Props.getInstance().getValue(UI_Props.GEOCODE));
	if(geocode != null){
%>
									<a href="plm_mobile_controller.jsp?<%=queryStr%>">
<%
	}
%>
										<div class="name1">Latest Address<br /><br /><span class="gmaptext">Find on Map</span></div>
										<div class="value1"><%=strDiscDate%><br/><%=currAdd%></div>
<%
	if(geocode != null){
%>
									</a>
<%
	}
%>
								</div>

<%
	String sortedPrevAddresses = (String)tempPropsMap.get("Sorted Prev Address");
	String[] prevAddresses = null;
	if(sortedPrevAddresses != null) {
		prevAddresses = sortedPrevAddresses.split("@@");
	}
	
	int cnt = 0;
	String srow = "";
	while (prevAddresses != null && cnt<prevAddresses.length){
		if (cnt%2==0){
			srow = "row1";
		}else{
			srow = "row2";
		}
%>
								<div class="<%=srow%>">
<%
		String prevAdd1 = prevAddresses[cnt];
		String prevAddress = "";
		String[] result = null;
		HashMap hmap = new HashMap();
		if(prevAdd1 != null){
			result = prevAdd1.split(PLMConstants.SEPARATOR);
		}
		if(result != null){
			if(result[0] != null && result[0].length()>0 && !"".equals(result[0])){
				strDiscDate = result[0];	
			}else{
				strDiscDate ="";
			}
			//careof
			if(result.length > 1 && result[1] != null && result[1].length()>0 && !"".equals(result[1]))
				prevAddress = prevAddress+(String)result[1]+"<br/>";
			//street
			if(result.length > 2 && result[2] != null && result[2].length()>0 && !"".equals(result[2]))
				prevAddress = prevAddress+(String)result[2]+"<br/>";
			//city						
			if(result.length > 3 && result[3] != null && result[3].length()>0 && !"".equals(result[3])){
				prevAddress = prevAddress+(String)result[3]+",&nbsp;";
			}
			//county
			if(result.length > 5 && result[5] != null && result[5].length()>0 && !"".equals(result[5])){
				prevAddress = prevAddress+(String)result[5]+",&nbsp;";
			}
			//state
			if(result.length > 4 && result[4] != null && result[4].length()>0 && !"".equals(result[4])){
				prevAddress = prevAddress+(String)result[4]+",&nbsp;";
			}
			//zip
			if(result.length > 7 && result[7] != null && result[7].length()>0 && !"".equals(result[7])){
				prevAddress = prevAddress+(String)result[7];	
			}
			//zip4
			if(result.length > 8 && result[8] != null && result[8].length()>0 && !"".equals(result[8])){
				prevAddress = prevAddress+"-"+(String)result[8]+"<br />";	
			} else {
				prevAddress = prevAddress+"<br />";
			}

			//map
			if(result.length > 11 && result[11] != null && result[11].length()>0 && !"".equals(result[11])) {
				prevAddress = prevAddress+"Map Reference:"+(String)result[11]+"<br />";
			} 
			//phone
			if(result.length > 9 && result[9] != null && result[9].trim().length()>0 && !"".equals(result[9].trim())){
				prevAddress = prevAddress+ "Phone:" + (String)result[9]+"<br />";	
			}
			//msg phone
			if(result.length > 10 && result[10] != null && result[10].trim().length()>0 && !"".equals(result[10].trim())){
				prevAddress = prevAddress+ "Msg Phone:" + (String)result[10]+"<br />";	
			}
		}
%>
									<div class="name1">Previous Address</div>
									<div class="value1"><%=strDiscDate%><br/><%=prevAddress%></div>
								</div>
<%
		cnt = cnt+1;
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