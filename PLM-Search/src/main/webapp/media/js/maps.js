var resp;
var schoolMarkerArray = new Array();
var schoolInfoWindowArray = new Array();
var infowindow = new google.maps.InfoWindow(); 

var schoolsHidden=true;
function plotSchools(showSchools1,search,map){
	if(resp){
		var xml=xmlHttp.responseXML;
		var schools = xml.getElementsByTagName("School");
		var image = 'media/images/maps/school.png';
		var markerOptions = {
				icon:image
		};
		if(showSchools1==true){
			markerOptions.visible = true;
		}else{
			markerOptions.visible = false;
		}
		removeSchools();
		for (var i = 0; i < schools.length; i++) {
			var school = schools[i];
			var point = new google.maps.LatLng(school.getAttribute("latitude"),school.getAttribute("longitude"));
			var marker= new google.maps.Marker({
				position: point,
				icon: image,
				map:map
			});
			marker.setOptions(markerOptions);
			var schoolInfo = "<b>School Name: " + school.getAttribute("schoolname") +"</b><br/>" + 
							 "District: " + school.getAttribute("district") + "<br/>" + 
							 "Grade: " + school.getAttribute("schoolgrade") + "<br/>" +
							 "Type: " + school.getAttribute("schooltype") + "<br/>" +
							 "Address: " + school.getAttribute("address");
			marker=showSchoolInfo(marker,schoolInfo);
			schoolMarkerArray[i] = marker;
			marker.setMap(map);
		}
	}
}
function loadLatsLongs(map,showSchools1,centerLat,centerLng,search){
		var bounds = map.getBounds();
		var southWest = bounds.getSouthWest();
		var northEast = bounds.getNorthEast();
		var swLong = ((southWest.lng())+"").replace(".","p");
		var swLat  = ((southWest.lat())+"").replace(".","p");
		var neLong = ((northEast.lng())+"").replace(".","p");
		var neLat  = ((northEast.lat())+"").replace(".","p");
		var url = "school.map?swLong="+swLong+"&swLat="+swLat+"&neLong="+neLong+"&neLat="+neLat+"&sid="+Math.random();
		resp = execute_get(url, false);
		plotSchools(showSchools1,search,map);
}
function showSchoolInfo(marker,schoolInfo) { // shows info about record
	google.maps.event.addListener(marker, "click", function() {
		infowindow.close();
		infowindow.setContent(schoolInfo);
		infowindow.open(map, marker);
	});
	return marker;
}
function setCheckBoxValue(box){
	if(box.checked){
		box.value='Y';
	}else{
		box.value ='';
	}
}
function showSchools(flag){
	if(flag==true){
		for (var i = 0; i < schoolMarkerArray.length; i++) {
			var marker = schoolMarkerArray[i];
			marker.setVisible(true);
		}
		schoolsHidden=false;
	}else{
		for (var i = 0; i < schoolMarkerArray.length; i++) {
			var marker = schoolMarkerArray[i];
			marker.setVisible(false);
		}
		schoolsHidden=true;
	}
}
function removeSchools(){
	if (schoolMarkerArray) {
		for (i in schoolMarkerArray) {
			schoolMarkerArray[i].setMap(null);
		}
		schoolMarkerArray.length = 0;
	}
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
function setZoomLevel(sDistance){
	var zoomLevel = 0;
	if(sDistance.indexOf('Feet') > 0){
		zoomLevel = 15;
	}else if(sDistance.indexOf('Mile') > 0){
		zoomLevel = 10;
	}else if(isNaN(sDistance)==false){
		zoomLevel=Number(sDistance);
	}
	return zoomLevel;
}
function showMarkerInfo(marker,url,desc) { // shows info about record
	var infowindow = new google.maps.InfoWindow({
		content: desc
	});
	google.maps.event.addListener(marker, "click", function() { //on click on marker shows details about record
		//marker.openInfoWindowHtml("<b>CDC #: " + props[0]+"<br/>First Name:" +props[1]+ "<br/>Last Name: "+props[2]+ "<br/>Status: "+props[3]+ "<br/>Ethnticity: "+props[4]+ "<br/>Gender: "+props[5]+"</b>");
		infowindow.open(map, marker);
	});
	//GEvent.addListener(marker, "click", function() { //on click on marker shows details about record
	//window.location = url;
	// 	marker.openInfoWindowHtml(desc);
	//});
	//GEvent.addListener(marker, "mouseover", function() { // on mouse over on marker shows latitude and longitude
	//	marker.openInfoWindowHtml(desc);
	//});
	return marker;
}
Number.prototype.toRad = function() {  // converts degrees to radians
	return this * Math.PI / 180;
}
function createCirclePoints(centerLat,centerLng,d,map) { // creates circle of radius equal to number of miles specified
	var circlePoints  = new Array();
	for (var a = 0 ; a < 361 ; a++ ) { // calculates circle points for each degree
		var tc = (Math.PI/180)*a;
		var y = Math.asin(Math.sin(centerLat)*Math.cos(d)+Math.cos(centerLat)*Math.sin(d)*Math.cos(tc));
		var dlng = Math.atan2(Math.sin(tc)*Math.sin(d)*Math.cos(centerLat),Math.cos(d)-Math.sin(centerLat)*Math.sin(y));
		var x = ((centerLng-dlng+Math.PI) % (2*Math.PI)) - Math.PI ; // MOD function
		var point = new google.maps.LatLng(parseFloat(y*(180/Math.PI)),parseFloat(x*(180/Math.PI)));
		if(point!='(NaN, NaN)'){
			circlePoints.push(point);
		}
	}
	var polygon = new google.maps.Polygon	({
		paths:circlePoints,
		strokeColor:"#FF0000",
		strokeOpacity:0.8,
		strokeWeight:2,
		fillColor:"#FF0000",
		fillOpacity:0.07
	});	 // creates circle(polygon) using the points
	polygon.setMap(map);  //plots circle on map
}
function getClockTime()
{
   var now    = new Date();
   var hour   = now.getHours();
   var minute = now.getMinutes();
   var second = now.getSeconds();
   var ap = "AM";
   if (hour   > 11) { ap = "PM";             }
   if (hour   > 12) { hour = hour - 12;      }
   if (hour   == 0) { hour = 12;             }
   if (hour   < 10) { hour   = "0" + hour;   }
   if (minute < 10) { minute = "0" + minute; }
   if (second < 10) { second = "0" + second; }
   var timeString = hour +
                    ':' +
                    minute +
                    ':' +
                    second +
                    " " +
                    ap;
   return timeString;
}