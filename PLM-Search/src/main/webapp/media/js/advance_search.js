var globalvalue=5;
var geocoder;
var lat;
var lng;
var radiusKm;
var pointsLat = new Array();
var pointsLng = new Array();
var urlArray = new Array();
var labelArray = new Array();
var geocodeProp= 'geocode';
var finalLatLng;
var address = '';
var myFinalLatLong = '';
var url = '';
var nURL = '';
var startURL = '&Nrs=collection()/record[';
var endURL = ']';
var finalQuery='';
function setCheckBoxValue(box){
	if(box.checked){
		box.value='Y';
	}else{
		box.value ='';
	}
}
 function removeElement(liId,elementName) {
	var ul = document.getElementById(elementName);
	var li = document.getElementById(liId);
	ul.removeChild(li);
	var tattooInfos = sTattooInfo.split('|');
	sTattooInfo ='';
	for(var i = 0;i<tattooInfos.length;i++){
		if(tattooInfos[i].indexOf(liId)<0 && tattooInfos[i]!=''){
			sTattooInfo = sTattooInfo+tattooInfos[i]+'|';
		}
	}
}
function convertDateToYYYYMMDDFormat(bDate,seperator){
	var date='';
	if(bDate != ''){
		var mm = bDate.substring(0,2);
		var dd = bDate.substring(2,4);
		var yyyy = bDate.substring(4,8);
		date = yyyy+seperator+mm+seperator+dd;
	}
	return date;
}
function convertDateToMMDDYYYYFormat(bDate,seperator){
	var date='';
	if(bDate != ''){
		var mm = bDate.substring(0,2);
		var dd = bDate.substring(2,4);
		var yyyy = bDate.substring(4,8);
		date = mm+seperator+dd+seperator+yyyy;
	}
	return date;
}
function addTattooInfo(formName,picture, text,and,or){
	var condition ='';
	var li ='';
	var newLi='';
	if(picture.value==''&&text.value=='') {
		alert('Please provide Tattoo Information');
		return;
	}
	if(and.checked){
		condition=and;
	} else {
		condition=or;
	}
	var prevLi = document.getElementById('refinmentsbutton').innerHTML;
	if(picture.value == ''){
		li =   '<li id=tattoo'+tattooInfoIter+'>'+text.value+ '<a href="#"><img src="media/images/global/close_icon.gif" alt="" onClick="removeElement(\'tattoo'+tattooInfoIter+'\',\'refinmentsbutton\')"/></a></li>';
	} else if(text.value == ''){
		li = '<li id=tattoo'+tattooInfoIter+'>'+picture.value+ '<a href="#"><img src="media/images/global/close_icon.gif" alt="" onClick="removeElement(\'tattoo'+tattooInfoIter+'\',\'refinmentsbutton\')"/></a></li>';
	} else{
		li = '<li id=tattoo'+tattooInfoIter+'>'+picture.value+'/'+text.value+ '<a href="#"><img src="media/images/global/close_icon.gif" alt="" onClick="removeElement(\'tattoo'+tattooInfoIter+'\',\'refinmentsbutton\')"/></a></li>';
	}
	newLi = prevLi+li;
	document.getElementById('refinmentsbutton').innerHTML = newLi;
	sTattooInfo =sTattooInfo+'tattoo'+tattooInfoIter+','+condition.value+','+picture.value+' '+text.value+'|';
	document.forms[formName]['SMT Picture'].value='';
	document.forms[formName]['SMT Text'].value ='';
	tattooInfoIter++;
}

//Initialize Request
function initialize() {    
	geocoder = new google.maps.Geocoder();    
	var latlng = new google.maps.LatLng(-34.397, 150.644);
	var myOptions = {       
		zoom: 8,      
		center: latlng,     
		mapTypeId: google.maps.MapTypeId.ROADMAP   
	};
}
function processRequest() {
	var milesAroundPoint = radiusKm;//document.getElementById('miles').value;
	if(milesAroundPoint==undefined){
		milesAroundPoint=0;
	}	
	if(milesAroundPoint !=0){
		// 2018-03-14 emil replace | with ASCII code %7C to fix Tomcat error
		// myFinalLatLong = geocodeProp+'|GCLT+' + lat + ',' + lng + '+' + radiusKm ;
		myFinalLatLong = geocodeProp+'%7CGCLT+' + lat + ',' + lng + '+' + radiusKm ;
	}
	url = 'plm_controller.jsp'+'?N=';
	if(nURL == '') {
		nURL = '0';
	}
	url = url + nURL;
	if(finalQuery != ''){
		url = url+startURL+finalQuery+endURL;
	}
	if(myFinalLatLong!=''){
		myFinalLatLong = '&Nf='+myFinalLatLong+'&distance='+document.getElementById('miles').value;
		url+=myFinalLatLong;
	}
	tab=0;
	if(finalQuery != '' || myFinalLatLong != ''){
		url = url+'&tab='+tab+'&searchResults=advancedSearch';
	} else {
		url = url+'&tab='+tab;
	}
	parent.location.href=url;
}

function showLocation()	{
	var kmArray = new Array();
	kmArray[0] = 0;
	kmArray[1] = 24.134999999999998;
	kmArray[2] = 8.045;
	kmArray[3] = 1.609;
	kmArray[4] = 0.30473484848484783;
	kmArray[5] = 0.15236742424242392;
	var radius = document.forms['advancedSearch']['miles'].selectedIndex;
	radiusKm = kmArray[radius];
	var laddress = document.getElementById('location').value;
	var intStreet1= document.getElementById('intStreet1').value;
	var intStreet2= document.getElementById('intStreet2').value;
	var zipcode = document.getElementById('zipcode').value;
	var miles = document.getElementById('miles').value;

	var locAddress = laddress+','+zipcode;
	var intAdress = intStreet1+' & '+intStreet2+','+zipcode;
	
	if((laddress == null || laddress =='')&&(intStreet1==''||intStreet2=='')){
		alert('Please enter a Location or Intersection Streets');
		return;
	}
	if(miles == 0){
		alert('Please select a radius');
		return;
	}
	if(laddress != ''){
		address = locAddress;
	}else{
		address = intAdress ;
	}
	
	geocoder.geocode( { 'address': address}, function(results, status) {  
	if (status == google.maps.GeocoderStatus.OK) { 
		//  map.setCenter(results[0].geometry.location);     
		//  alert("status is ok");
		var marker = new google.maps.Marker
		({          
			position: results[0].geometry.location      
		}); 
		lat = results[0].geometry.location.lat();
		lng = results[0].geometry.location.lng();
	} else {
		alert('Sorry, we were unable to geocode that address'+status);  
	}     }); 
	setTimeout("processRequest()",1000);
}

function advanceSearch(formName) {
//	Added CountyOfLastLegalResidence -- LBB 
	var requiresManipulation = 'County Name,Birth Date Display,Birth State Name,Height (+/- 2 inches),Weight (+/- 10 lb),CountyOfLastLegalResidence';
	var ignoreElementsList = 'smtradio,SMT Text1,SMT Picture1,SMT Text2,SMT Picture2,daterange1,daterange2,Race1,Race2,HairColor1,HairColor2,Offense Code1,Offense Code2,offenseradio,SMT_DESC1,SMT_DESC2,smtdescradio,vehyear1,vehyear2,location,intStreet1,intStreet2,zipcode,miles';
	var oldUrl = parent.location.href;

	if(oldUrl.indexOf('tab')>0){
		tab = oldUrl.substring(oldUrl.indexOf('tab')+4,oldUrl.indexOf('tab')+5);
	}else{
		tab='0';
	}

	var form = document.forms[formName];
	var endecaMatchesQuery = '';
	for(i=0; i< form.elements.length; i++) {
		var element = form.elements[i];
		var elementName = element.name;
		var elementValue='';
		if(ignoreElementsList.indexOf(elementName)<0){
			if(requiresManipulation.indexOf(elementName)<0){//these elements do not require any manipulation
				elementValue = element.value;
			}else if(requiresManipulation.indexOf(elementName)>=0) {//some manipulation is required for these fields before query is formed
				if(elementName == 'Birth Date Display'){
					var bDate = document.forms[formName][elementName].value;
					if(bDate != ''){
						if(!validateDate(bDate,formName,elementName)){
							return;
						}
					}
					elementValue = convertDateToMMDDYYYYFormat(bDate,'/');
//					Added CountyOfLastLegalResidence -- LBB
				} else if(elementName == 'County Name' || elementName == 'Birth State Name' || elementName == 'Height (+/- 2 inches)' || elementName == 'Weight (+/- 10 lb)'|| elementName ==  'CountyOfLastLegalResidence') {
					var index = document.forms[formName][elementName].selectedIndex;
					if(index == 0){
						elementValue = '';
					}else{
						elementValue = document.getElementById(elementName).options[index].value;
					}
					if(nURL == ''){
						nURL = elementValue;
					}else{
						if(elementValue!=''){
							nURL = nURL + '+' + elementValue;
						}
					}
					elementValue='';
				}
			}
		}
    	elementName = elementName.replace("+","%2B");
    	elementValue = elementValue.replace("\"","%22%22");
    	elementValue = elementValue.replace("'","%27");        
		if(elementValue != '' ){
			endecaMatchesQuery = endecaMatchesQuery+'endeca:matches(.,'+'\"'+elementName + '\"'+','+'\"'+elementValue+'\"'+') and ';
		}
	}

	//for smt code and text
	var smtdescCodeQuery ='';
	elementValue1 ='';
	elementValue2 ='';
	index1 = document.forms[formName]['SMT_DESC1'].selectedIndex;
	if(index1 == 0) {
		elementValue1 ='';
	}else{
		elementValue1=document.getElementById('SMT_DESC1').options[index1].text;
		elementValue1=elementValue1.replace(/ /g,"_");
	}
	
	index2 = document.forms[formName]['SMT_DESC2'].selectedIndex;
	if(index2 == 0) {
		elementValue2 ='';
	}else{
		elementValue2=document.getElementById('SMT_DESC2').options[index2].text;
		elementValue2=elementValue2.replace(/ /g,"_");
	}

	elementName= 'SMT_Detail';
	var and = document.getElementById('smtdescand');
	var or = document.getElementById('smtdescor');
	var condition ='';
	var smtPic1 = document.forms[formName]['SMT Picture1'].value;
	var smtText1 = document.forms[formName]['SMT Text1'].value;
	var smtPic2 = document.forms[formName]['SMT Picture2'].value;
	var smtText2 = document.forms[formName]['SMT Text2'].value;

	if((smtPic2 != '' || smtText2!='')&&(smtPic1 == '' && smtText1=='')){
		alert('Select Tattoo criteria');
		return;
	}
	if(and.checked){
		condition= 'and';
	} else {
		condition= 'or';
	}
	var tattoCriteria1 = elementValue1 + ' ' + smtPic1 + ' ' + smtText1;
	var tattoCriteria2 = elementValue2 + ' ' + smtPic2 + ' ' + smtText2;
	var tattooInfoQuery = '';
	
	if(tattoCriteria1 == '  ' && tattoCriteria2 == '  '){
	} else if(tattoCriteria2 == ' ' || tattoCriteria2 == '  '){
		tattooInfoQuery = tattooInfoQuery+'endeca:matches(.,'+'\"'+elementName + '\"'+','+'\"'+tattoCriteria1+'\"'+')';
	} else{
		tattooInfoQuery = tattooInfoQuery+'(endeca:matches(.,'+'\"'+elementName + '\"'+','+'\"'+tattoCriteria1+'\"'+')'+' '+condition+' '+'endeca:matches(.,'+'\"'+elementName + '\"'+','+'\"'+tattoCriteria2+'\"'+'))';
	}

	if(endecaMatchesQuery == ''){
		finalQuery = tattooInfoQuery;
	} else{
		if(tattooInfoQuery == ''){			
			finalQuery = endecaMatchesQuery.substring(0,endecaMatchesQuery.length-5);
		} else{
			finalQuery = endecaMatchesQuery+tattooInfoQuery;
		}
	}
	
	//adding ethnicity and hair color to final query
	//for hair color
	var hairColorQuery ='';
	var elementValue1 ='';
	var elementValue2 ='';
	var index1 = document.forms[formName]['HairColor1'].selectedIndex;
	if(index1 == 0) {
		elementValue1 ='';
	}else{
		elementValue1=document.getElementById('HairColor1').options[index1].text;
	}
	var index2 = document.forms[formName]['HairColor2'].selectedIndex;
	if(index2 == 0) {
		elementValue2 ='';
	}else{
		elementValue2=document.getElementById('HairColor2').options[index2].text;
	}
	if(elementValue1 != '' && elementValue2 != ''){
		hairColorQuery = '(endeca:matches(.,'+'\"'+'Haircolor' + '\"'+','+'\"'+elementValue1+'\"'+')' +  ' or endeca:matches(.,'+'\"'+'Haircolor' + '\"'+','+'\"'+elementValue2+'\"'+'))';
	} else if(elementValue1 == '' && elementValue2 == ''){
		hairColorQuery ='';
	}else{
		var value ='';
		if(elementValue1!='')
			value = elementValue1;
		else
			value = elementValue2;
		hairColorQuery = 'endeca:matches(.,'+'\"'+'Haircolor' + '\"'+','+'\"'+value+'\"'+')';
	}
	if(finalQuery == '')
		finalQuery = hairColorQuery;
	else{
		if(hairColorQuery != '')
			finalQuery = finalQuery+ ' and '+hairColorQuery;
	}
	//for ethnicity
	var ethnicityQuery ='';
	elementValue1 ='';
	elementValue2 ='';
	index1 = 0;
	index2 = 0;
	index1 = document.forms[formName]['Race1'].selectedIndex;
	if(index1 == 0)   {
		elementValue1 ='';
	}else{
		elementValue1=document.getElementById('Race1').options[index1].text;
	}
	index2 = document.forms[formName]['Race2'].selectedIndex;
	if(index2 == 0) {
		elementValue2 ='';
	}else{
		elementValue2=document.getElementById('Race2').options[index2].text;
	}
	if(elementValue1 != '' && elementValue2 != ''){
		ethnicityQuery = '(endeca:matches(.,'+'\"'+'Race' + '\"'+','+'\"'+elementValue1+'\"'+')' +  ' or endeca:matches(.,'+'\"'+'Race' + '\"'+','+'\"'+elementValue2+'\"'+'))';
	}
	else if(elementValue1 == '' && elementValue2 == ''){
		ethnicityQuery ='';
	}else{
		var value ='';
		if(elementValue1!='')
			value = elementValue1;
		else
			value = elementValue2;
		ethnicityQuery = 'endeca:matches(.,'+'\"'+'Race' + '\"'+','+'\"'+value+'\"'+')';
	}
	if(finalQuery == '')
		 finalQuery = ethnicityQuery;
	else{
		 if(ethnicityQuery != '')
			   finalQuery = finalQuery+ ' and '+ethnicityQuery;
	}
	//for offense code
	var offenseCodeQuery ='';
	elementValue1 ='';
	elementValue2 ='';
	index1 = document.forms[formName]['Offense Code1'].selectedIndex;
	if(index1 == 0) {
		elementValue1 ='';
	}else{
		elementValue1=document.getElementById('Offense Code1').options[index1].text;
	}
	index2 = document.forms[formName]['Offense Code2'].selectedIndex;
	if(index2 == 0) {
		elementValue2 ='';
	}else{
		elementValue2=document.getElementById('Offense Code2').options[index2].text;
	}
	condition ='';
	var offenseAnd = document.getElementById('offenseand');
	var offenseOr = document.getElementById('offenseor');
	if(offenseAnd.checked){
		condition='and';
	} else {
		condition='or';
	}
	if(elementValue1 != '' && elementValue2 != ''){
		offenseCodeQuery = '(endeca:matches(.,'+'\"'+'Offense Code' + '\"'+','+'\"'+elementValue1+'\"'+') '+condition+  ' endeca:matches(.,'+'\"'+'Offense Code' + '\"'+','+'\"'+elementValue2+'\"'+'))';
	}
	else if(elementValue1 == '' && elementValue2 == ''){
		offenseCodeQuery ='';
	}else{
		var value ='';
		if(elementValue1!='')
			value = elementValue1;
		else
			value = elementValue2;
		offenseCodeQuery = 'endeca:matches(.,'+'\"'+'Offense Code' + '\"'+','+'\"'+value+'\"'+')';
	}
	if(finalQuery == '')
		finalQuery = offenseCodeQuery;
	else{
		if(offenseCodeQuery != '')
			finalQuery = finalQuery+ ' and '+offenseCodeQuery;
	}
	
	if(finalQuery == '')
		finalQuery = smtdescCodeQuery;
	else {
		if(smtdescCodeQuery != '')
			finalQuery = finalQuery + ' and ' + smtdescCodeQuery;
	}
	var dateRange1 = document.getElementById('daterange1').value;
	var dateRange2 = document.getElementById('daterange2').value;
	if((dateRange1 =='' &&  dateRange2 != '') ||((dateRange2 =='' &&  dateRange1 != ''))  ) {
		document.forms[formName]['daterange1'].value ='';
		document.forms[formName]['daterange2'].value ='';
		alert('Please select the date range');
		return;
	}
	if(dateRange1 != ''){
		if(!validateDate(dateRange1,formName,'daterange1')){
			return;
		}
	}
	if(dateRange2 != ''){
		if(!validateDate(dateRange2,formName,'daterange2')){
			return;
		}
	}
	var date1 = convertDateToYYYYMMDDFormat(dateRange1,'');
	var date2 = convertDateToYYYYMMDDFormat(dateRange2,'');
	var dateQuery = '';
	if(date1 != '' && date2 != '') {
		dateQuery = '((Revocation_Release_Date_Search'+'>='+date1+' and '+'Revocation_Release_Date_Search'+'<='+date2+')';
		dateQuery = dateQuery+' or (Parole_Date_Search'+'>='+date1+' and '+'Parole_Date_Search'+'<='+date2+'))';
	}
	if(finalQuery == '')
		finalQuery = dateQuery;
	else {
		if(dateQuery != '')
			finalQuery = finalQuery+ ' and '+dateQuery;
	}
	var vehyear1 = document.getElementById('vehyear1').value;
	var vehyear2 = document.getElementById('vehyear2').value;
	if((vehyear1 =='' &&  vehyear2 != '') ||((vehyear2 =='' &&  vehyear1 != ''))  ) {
		document.forms[formName]['vehyear1'].value ='';
		document.forms[formName]['vehyear2'].value ='';
		alert('Please select the vehicle year  range');
		return;
	}
	if(vehyear1 != ''){
		if(!validateYear(vehyear1,formName,'vehyear1')){
			return;
		}
	}
	if(vehyear2 != ''){
		if(!validateYear(vehyear2,formName,'vehyear2')){
			return;
		}
	}
	var vehicleQuery = '';
	if(vehyear1 != '' && vehyear2 != '') {
		if(vehyear1 > vehyear2 ){
			alert('Vehicle Year 1 must be less than Vehicle Year 2');
			document.forms[formName]['vehyear1'].value ='';
			document.forms[formName]['vehyear2'].value ='';
			return;
		}
		vehicleQuery = '(Veh_Year'+'>='+vehyear1+' and '+'Veh_Year'+'<='+vehyear2+')';
	}
	if(finalQuery == '')
		finalQuery = vehicleQuery;
	else {
		if(vehicleQuery != '')
			finalQuery = finalQuery+ ' and '+vehicleQuery;
	}

	// emil 2018-02-28 fix issue in IE: double quotes in request string cause 400 at Tomcat
    // replace double quotes in finalQuery with %22 special character
    // replace < and > characters in finalQuery with %3C and %3E ASCII codes
    if (finalQuery && finalQuery.length > 0) {
        finalQuery = finalQuery.replace(/"/g, "%22");
        finalQuery = finalQuery.replace(/</g, "%3C");
        finalQuery = finalQuery.replace(/>/g, "%3E");
        console.log("Advanced search: query string -->" + finalQuery);
    }


	//Add extra parameters for Radial Search
	var laddress = document.getElementById('location').value;
	var intStreet1= document.getElementById('intStreet1').value;
	var intStreet2= document.getElementById('intStreet2').value;
	var zipcode = document.getElementById('zipcode').value;
	var miles = document.getElementById('miles').value;
	var validateGeo = true;
	if((laddress ==null || laddress == '')
			&& (intStreet1 == null || intStreet1 == '')
			&& (intStreet2 == null || intStreet2 == '')
			&& (zipcode == null || zipcode == '')
			&& (miles == 0)
			){
		validateGeo = false;
	}
	if(validateGeo){
		showLocation();
	}else{
		processRequest();
	}
}