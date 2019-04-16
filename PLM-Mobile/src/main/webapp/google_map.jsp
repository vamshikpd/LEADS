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
<%@ page import="com.plm.util.*" %>
<%@ page import="com.plm.google.*" %>
<html>
	<head>
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0;" />
		<title>Map</title>
		<link href="media/style/main.css" rel="stylesheet" type="text/css" />
		<script src="<%= GoogleMapUtil.getGoogleMapUrl(request.isSecure()) %>" type="text/javascript"> </script>
		<script type="text/javascript">
		var props = new Array(7);
		function showMarkerInfo(marker,lat,lng,url,props) { // shows info about record 
			GEvent.addListener(marker, "click", function() { //on click on marker shows details about record
				window.location = url;
				});
			GEvent.addListener(marker, "mouseover", function() { // on mouse over on marker shows latitude and longitude
				marker.openInfoWindowHtml("<b>CDC #: " + props[0]+"<br/>First Name:" +props[1]+ "<br/>Last Name: "+props[2]+ "<br/>Status: "+props[3]+ "<br/>Ethenticity: "+props[4]+ "<br/>Gender: "+props[5]+"</b>");
			});
			return marker;
		}
		</script>
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
							<div class="back"><a href="plm_mobile_controller.jsp?page=residenceInfo&R=<%=spec%>">Back</a></div>
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
								<div id="mapimage"></div>
<%
	String geocode = (String)tempPropsMap.get(UI_Props.getInstance().getValue(UI_Props.GEOCODE));
	if (geocode != null) {
		// Build URL and alt-name tag for avatar on map
		String displayKey = (String)tempPropsMap.get(UI_Props.getInstance().getValue(UI_Props.GEOCODE_LABEL));
		String prop_firstName = (String)tempPropsMap.get(UI_Props.getInstance().getValue("FIRST_NAME"));
		String prop_lastName = (String)tempPropsMap.get(UI_Props.getInstance().getValue("LAST_NAME"));
		String prop_ethnicity = (String)tempPropsMap.get(UI_Props.getInstance().getValue("ETHNICITY"));
		String prop_gender = (String)tempPropsMap.get(UI_Props.getInstance().getValue("GENDER"));
		String prop_cdcnum = spec;
		String prop_status = (String)tempPropsMap.get(UI_Props.getInstance().getValue("STATUS"));
		String lat = geocode.substring(0, geocode.indexOf(","));
		String lng = geocode.substring(geocode.indexOf(",")+1);	
		UrlGen url = new UrlGen("", "UTF-8");
		url.addParam("R",spec);
		url.addParam("page","paroleedetails");
		String sUrl = PLMSearchUtil.getFormattedPLMURL(request,"/plm_mobile_controller.jsp")+"?"+url;
		URL refURL =  null;
		if(sUrl!= null && sUrl.indexOf("?")>=0){
			refURL = new URL(sUrl.substring(0,sUrl.indexOf("?"))+"?"+url);
		}
%>
		<script type="text/javascript">	
			props[0] = '<%= prop_cdcnum%>';
			props[1] = '<%= prop_firstName%>';
			props[2] = '<%= prop_lastName%>';
			props[3] = '<%= prop_status%>';
			props[4] = '<%= prop_ethnicity%>';
			props[5] = '<%= prop_gender%>';
			var  icon = new GIcon(G_DEFAULT_ICON);
			var lat = Number('<%=lat%>');
			var lng = Number('<%=lng%>');
			var point = new GLatLng(lat,lng);
			var marker = new GMarker(point);
			var url ='<%= refURL%>';
			var map = new GMap2(document.getElementById("mapimage"));
			map.setUIToDefault();
			map.setCenter(new GLatLng(lat, lng),10);
			if(!isNaN(lat)&& !isNaN(lng)) {
				map.addOverlay(showMarkerInfo(marker,lat,lng,url,props));	// plots the processed record on the map
			}
		</script>
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