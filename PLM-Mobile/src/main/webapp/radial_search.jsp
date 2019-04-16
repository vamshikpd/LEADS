<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.endeca.ui.constants.UI_Props"%>
<%@ page import="com.plm.google.*" %>
<%
	Navigation nav = (Navigation)request.getAttribute("navigation");
	String geocodeprop = UI_Props.getInstance().getValue(UI_Props.GEOCODE);
%>
<html>
	<head>
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0;" />
		<title>Radial Search</title>
		<link href="media/style/main.css" rel="stylesheet" type="text/css" />
		<script src="<%= GoogleMapUtil.getGoogleMapUrl(request.isSecure()) %>" type="text/javascript"> </script> 
		<script type="text/javascript">
			var geocoder;
			var lat;
			var long;
			var pointsLat = new Array();
			var pointsLng = new Array();
			var urlArray = new Array();
			var labelArray = new Array();
			var geocodeProp = "<%=geocodeprop%>";
			function validateZipcode(zipcode){
				if(isNaN(zipcode)){
					document.getElementById("zipcode").value="";
					return;
				}
			}
			function processRequest() {
				var url = 'plm_controller.jsp';//top.location.toString();	
				url = url +'?N=0&page=searchresults';
				if(myFinalLatLong!=""){
					myFinalLatLong = "&Nf="+myFinalLatLong;
					url+=myFinalLatLong;		
				}
				top.location=url;
			}
			function addAddressToMap(response) {
				  var radius = document.getElementById("miles").value;
				  if (!response || response.Status.code != 200) { // address not found
				      alert("Sorry, we were unable to geocode that address");				
				  } else {	
	
					  var place = response.Placemark[0]; // Placemark[] is obtained from response
					  lat = place.Point.coordinates[1];
					  long = place.Point.coordinates[0];
					  point = new GLatLng(lat,long);
					  marker = new GMarker(point);	
					  marker.openInfoWindowHtml(place.address + '<br>' +'<b>Country code:</b> ' + place.AddressDetails.Country.CountryNameCode);
					  var milesAroundPoint = document.getElementById('miles').value;
					  if(milesAroundPoint !=0){ // if miles is specified
						  var milesRad = milesAroundPoint * (Math.PI / 180); // convert to radians
						  var d= (milesRad * 1.609) ; // convert miles to kilometers for the following formula
						  var centerLat = lat  * Math.PI / 180; // convert degree to radians
						  var centerLng = long * Math.PI / 180; // convert degree to radians
						  var centerLatDeg = centerLat*180/Math.PI;
						  var centerLngDeg = centerLng*180/Math.PI;

						  // 2018-03-10 emil replace | with ASCII %7C
						  // myFinalLatLong = geocodeProp+'|GCLT+' + centerLatDeg + ',' + centerLngDeg + '+' + (milesAroundPoint*1.609) ;
						  myFinalLatLong = geocodeProp+'%7CGCLT+' + centerLatDeg + ',' + centerLngDeg + '+' + (milesAroundPoint*1.609) ;

						  processRequest(); //  gets records within the circle
					  }
				  }
				  
			}
		    function showLocation() {
			    var zipcode = document.getElementById("zipcode").value;
				var laddress = document.getElementById("location").value;
				var locAddress = laddress+","+zipcode;
				var miles = document.getElementById("miles").value;
				var intStreet1= document.getElementById("intStreet1").value;
				var intStreet2= document.getElementById("intStreet2").value;
				var intAdress = intStreet1+" & "+intStreet2+","+zipcode;
				var address = '';
				//validation for  entering location ,zipcode and distance  or intersection 1 and 2 , zipcode and distance
				if(zipcode == null || zipcode ==''){
					alert("Please enter zip code");
					return;
				}
				if((laddress == null || laddress =='')&&(intStreet1==''||intStreet2=='')){
					alert("Please enter a Location or Intersection Streets ");
					return;
				}
				if(miles == 0){
					alert("Please enter miles  ");
					return;
				}
				if(laddress != ''){
					address = locAddress;
				}else{
					address = intAdress ;
				}
				geocoder = new GClientGeocoder();
				geocoder.getLocations(address, addAddressToMap);
			}
			function DetectBoolean(terms) {
				var tokens = terms.split(/\s+/);
				var isBoolean = false;
				for(var i=0; i<tokens.length; i++) {
					if (tokens[i].search(/^AND$/i)>-1 ||
						tokens[i].search(/^OR$/i)>-1  ||
						tokens[i].search(/^NOT$/i)>-1 ||
						tokens[i].search(/^O?NEAR/i)>-1 ||
						tokens[i].search(/:$/)>-1 ||
						tokens[i].search(/^\(/)>-1 ||
						tokens[i].search(/\)$/)>-1) {
								
						isBoolean = true;
						break;
					}
				}
				return isBoolean;
			}
		</script>
		<script type="text/javascript">
			var iframe1 = parent.document.getElementById('iframe');
			var title1 = parent.document.getElementById('title1');
			title1.innerHTML = iframe1.document.title;
		</script>
	</head>
	<body>
		<form name="searchAddress" action="search_result.html" > 
			<div align="center">
				<div id="Maindiv">
					<div class="logo"><span class="logo1">PAROLE</span><span class="logo2">LEADS2.0</span></div>
					<div class="top">
						<div class="back"><a href="plm_mobile_controller.jsp?page=login"><< Back</a></div>
						<%@ include file="logout_include_mobile.jsp" %>
						<div class="home"><a href="plm_mobile_controller.jsp?page=login">Home</a></div>
					</div>
					<div id="Container">
						<div id="MainContainer">
							<div>
								<div class="value">
									<div>Location Address:</div>
									<div><input type="text" name="location" id="location" value=""></div>
								</div>
								<div class="value">
									<div>Intersection 1:</div>
									<div><input type="text" name="intStreet1" id="intStreet1" value=""></div>
								</div>
								<div class="value">
									<div>Intersection 2:</div>
									<div><input type="text" name="intStreet2" id="intStreet2" value=""></div>
								</div>
								<div class="value">
									<div>Zipcode:</div>
									<div>
										<input type="text" name="zipcode" id="zipcode" value="" onKeyUp="validateZipcode(this.value)">
									</div>
								</div>
							</div>
							<div class="value">
								<div>Radius:</div>
								<div class="password">
									<select name="miles" id='miles'>
										<option value='0'>Select Miles</option>
										<option value='100'>100</option>
										<option value='200'>200</option>
										<option value='500'>500</option>
										<option value='1000'>1000</option>
									</select>
								</div>
								<div class="gobutton"><input type="button" onclick="showLocation()" name="submit" value="GO" /></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</form>
	</body>
</html>
