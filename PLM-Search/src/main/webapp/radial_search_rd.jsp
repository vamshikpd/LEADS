<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.endeca.ui.constants.UI_Props"%>
<%@ page import="com.plm.util.*" %>
<%@ page import="com.plm.google.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Radial Search</title>
		<script src="media/js/jquery-1.3.2.js" type="text/javascript"></script>
		<script type="text/javascript">
			jQuery(document).ready(function() {
				setTimeout(function(){
					$("form :input[type='text']:enabled:first").focus();
				},800);
				$("form").keypress(function (e) {
					 if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
						$("form :input[name='find']").click();
						 return false;
						} else {
						 return true;
						}
				 });
			});
		</script>
		<link rel="stylesheet" href="media/style/main.css" type="text/css"/>
		<script language="JavaScript" src="media/js/plm_ohw.js"></script>
		<script src="<%= GoogleMapUtil.getGoogleMapUrl(request.isSecure()) %>" type="text/javascript"></script>
<%
	Navigation nav = (Navigation)request.getAttribute("navigation");
	String geocodeprop = UI_Props.getInstance().getValue(UI_Props.GEOCODE);
	PermissionInfo permissionInfoRd = null;
	permissionInfoRd = (PermissionInfo)session.getAttribute("permissionInfo");
%>
		<script language="javascript" type="text/javascript">
			var geocoder;
			var lat;
			var map;
			var lng;
			var pointsLat = new Array();
			var pointsLng = new Array();
			var urlArray = new Array();
			var labelArray = new Array();
			var geocodeProp = "<%=geocodeprop%>";
			function validateZipcode(zipcode){
				if(zipcode!=""){
					if(isNaN(zipcode)){
						alert("enter only digits");
						document.getElementById("zipcode").value="";
						return;
					}
				}
			}
			/*function openurl(link1){
				self.close();
				var ReferURL = parent.window.location.href;
				parent.window.location.href = link1;
				//window.opener.location.href = window.opener.location.href;
			}*/

			function processRequest() {
				if(lat==null || lng==null){
					alert("Geocode was not successful.\nPlease try again later.");
				}else{
					var url = 'plm_controller.jsp';//top.location.toString();
					// myFinalLatLong = geocodeProp+'|GCLT+' + lat + ',' + lng + '+' + radiusKm ;
					var myFinalLatLong = geocodeProp+'%7CGCLT+' + lat + ',' + lng + '+' + radiusKm ; // // Emil, 2018-02-28: changed | pipe to %7C html char
					if('<%=permissionInfoRd.canViewMaps()%>' ==  'true'){
						url += '?N=0&tab=2';
					} else {
						url += '?N=0&tab=0';
					}
					if(myFinalLatLong !== ""){
						myFinalLatLong = "&Nf="+myFinalLatLong+"&distance="+document.getElementById("miles").value;
						url+=myFinalLatLong;
					}
					console.log("Radial search: URL --> " + url);
					parent.location.href=url;
				}
			}

			function initialize() {
				geocoder = new google.maps.Geocoder();
				var latlng = new google.maps.LatLng(-34.397, 150.644);
				var myOptions = {
					zoom: 8,
					center: latlng,
					mapTypeId: google.maps.MapTypeId.ROADMAP
				}
				//	map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
				//	alert("map"+map);
			}
			function showLocation() {
				var kmArray = new Array();
				kmArray[0] = 0;
				kmArray[1] = 24.134999999999998;
				kmArray[2] = 8.045;
				kmArray[3] = 1.609;
				kmArray[4] = 0.30473484848484783;
				kmArray[5] = 0.15236742424242392;
				var radius = document.forms['searchAddress']['miles'].selectedIndex;
				var variable;
				radiusKm = kmArray[radius];
				var zipcode = document.getElementById("zipcode").value
				var laddress = document.getElementById("location").value;
				var locAddress = laddress+","+zipcode;
				var miles = document.getElementById("miles").value;
				var intStreet1= document.getElementById("intStreet1").value;
				var intStreet2= document.getElementById("intStreet2").value;
				var intAdress = intStreet1+" & "+intStreet2+","+zipcode;
				var address = '';
				if((laddress == null || laddress =='')&&(intStreet1==''||intStreet2=='')){
					alert("Please enter a Location or Intersection Streets ");
					return;
				}
				if(miles == 0){
					alert("Please enter Radius");
					return;
				}
				if(laddress != ''){
					address = locAddress;
				}else{
					address = intAdress ;
				}
				geocoder.geocode( { 'address': address}, function(results, status) {
			  		if (status == google.maps.GeocoderStatus.OK) {
						var marker = new google.maps.Marker({
						  	position: results[0].geometry.location
					 	});
						lat = results[0].geometry.location.lat();
						lng = results[0].geometry.location.lng();
			  		} else  {
						alert("Geocode was not successful for the following reason: " + status);
			  		}
				});
			  	setTimeout( "processRequest()" , 1000);
			}
		</script>
	</head>
	<body id="radialSearchbody" onload="initialize()">
		<form name="searchAddress" action="#" onsubmit="showLocation(); return false;">
			<div id="radialSearchTop" style="width:397px; padding-right:6px;">
				<div id="helpdiv">
					<a href="JavaScript:plmHelpPopup('<%=request.getContextPath() %>/ohw_help.jsp?topic_id=radial_search');">
						<img src="media/images/global/help_icon.gif" alt="" />
					</a>
				</div>
				<div id="radialSearchTopfirstrow">
					<div><strong>Enter a Location Address -OR- Street Intersections below</strong></div>
				</div>
				<div>
					<div style="float: left; width: auto; padding-top: 0px;">
						<div>
							Location Address<br />
							<input type="text" name="location" id="location" value="">
						</div>
					</div>
					<span style="width: auto; float: left; padding-top: 20px; padding-left: 17px; font-weight: bold;">- OR -</span>
					<div style="width: auto; clear: none; float:left; padding-left:17px;">
						<div>
							<div>
								Intersection Street 1<br />
								<input type="text" name="intStreet1" id="intStreet1" value="">
							</div>
						</div>
						<br clear="all">
						<div id="radialSearchTopMiddle">
							<div>
								Intersection Street 2<br />
								<input type="text" name="intStreet2" id="intStreet2" value="">
							</div>
						</div>
					</div>
				</div>
				<div style="border-top:1px solid #ccc; margin-top:20px; width:380px;">
					<div>
						<div>
							Zipcode<br />
							<input type="text" name="zipcode" id="zipcode" value="" onKeyUp="validateZipcode(this.value)" maxLength="5">
						</div>
					</div>
					<div id="radialSearchTopMiddle" style="padding-left:20px">
						<div>
							Search Radius<br />
							<select name="miles" id='miles'>
								<option value='0'>Select Miles/Feet</option>
								<option value='15 Miles'>15 Miles</option>
								<option value='5 Miles'>5 Miles</option>
								<option value='1 Mile'>1 Mile</option>
								<option value='1000 Feet'>1000 Feet</option>
								<option value='500 Feet'>500 Feet</option>
							</select>
						</div>
					</div>
				</div>
			</div>
			<div id="advSearchMiddle2" class="radialbuttons">
				<div id="advSearchbottom2main" style="float:none; padding-left:60px">
					<div class="advsearchbutton"><input type="image" name="find" value="Search" src="media/images/global/search_button.gif"/></div>
					<div class="advsearchbutton"><input type="image" onClick="this.form.reset();return false;" name="clearfields" value="ClearFields" src="media/images/global/clear_fields_button.gif"/></div>
					<div class="advsearchbutton"><a title="Close" onClick="parent.tb_remove()" href="#"><img src="media/images/global/cancel_button.gif" border="0"/></a></div>
				</div>
			</div>
		</form>
	</body>
</html>