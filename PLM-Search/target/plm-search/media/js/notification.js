function registrantNotifySearch(formName,userName,ipAddress){	
	var oldUrl = parent.location.href;
	var tab = "";
	if(oldUrl.indexOf("tab")>0){
		tab = oldUrl.substring(oldUrl.indexOf("tab")+4,oldUrl.indexOf("tab")+5);
	}else{
		tab="0";
	}
	var condition ='';
	var cityValue = '';
	var countyValue = '';
	var cityText = '';
	var countyText = '';
	var endecaMatchesQuery = '';

	//for city
	var cityIndex = document.forms[formName]['City'].selectedIndex;
	if(cityIndex ==0){
		cityValue ='';
		cityText = '';
	}else{
		cityValue=document.forms[formName]['City'].options[cityIndex].value;
		cityText = document.forms[formName]['City'].options[cityIndex].text;
	}
	
	//for county
	var countyIndex = document.forms[formName]['County'].selectedIndex;	
	if(countyIndex == 0){
		countyValue ='';
		countyText = '';
	}else{
		countyValue=document.forms[formName]['County'].options[countyIndex].value;
		countyText=document.forms[formName]['County'].options[countyIndex].text;
	}
		
    if(cityValue == '' && countyValue == ''){
		alert("Please Select a County");		
		return;
	}
   
    var nURL = "";
    
	if(cityValue == ''){
		nURL = nURL + countyValue;
	}else{
		nURL = nURL + countyValue + "+" + cityValue;
	}	
	endecaMatchesQuery = "endeca:matches(., "+'\"Action Type\"'+","+'\"'+"R"+'\"'+")"+" or endeca:matches(., "+'\"Has Address Changed\"'+","+'\"'+"Y"+'\"'+")";

    // emil 2018-02-28 fix issue in IE: double quotes in request string cause 400 at Tomcat
    // replace double quotes in endecaMatchesQuery with %22 special character
    endecaMatchesQuery = endecaMatchesQuery.replace(/"/g, "%22");

	var startURL = "Nrs=collection()/record[";
	var endURL = "]";	   

	var finalQuery = startURL+endecaMatchesQuery+endURL;	
    var url  = 'plm_controller.jsp';
    url = url+'?N=' + nURL + '&tab='+tab+"&";
	url = url+finalQuery+'&searchResults=pc290Registrant';
	  		  
	var ajaxUrl = "pc290Audit?county="+countyText+"&city="+cityText+"&userName="+userName+"&ipAddress="+ipAddress+"&qry_type=R";
	execute_get(ajaxUrl, true);	
	parent.location.href=url;
}	

function dischargeNotifySearch(formName,userName,ipAddress){	
	var oldUrl = parent.location.href; //top.location.toString();
	var tab = "";
	if(oldUrl.indexOf("tab")>0){
		tab = oldUrl.substring(oldUrl.indexOf("tab")+4,oldUrl.indexOf("tab")+5);
	}else{
		tab="0";
	}
	var condition ='';
	var cityValue = '';
	var countyValue = '';
	var cityText = '';
	var countyText = '';

	var endecaMatchesQuery = '';

	//for city
	var cityIndex = document.forms[formName]['City'].selectedIndex;	
	if(cityIndex ==0){
		cityValue ='';
		cityText = '';
	}else{
		cityValue=document.forms[formName]['City'].options[cityIndex].value;
		cityText=document.forms[formName]['City'].options[cityIndex].text;
	}
	
	//for county
	var countyIndex = document.forms[formName]['County'].selectedIndex;
	if(countyIndex ==0){
		countyValue ='';
		countyText = '';
	}else{
		countyValue=document.forms[formName]['County'].options[countyIndex].value;
		countyText=document.forms[formName]['County'].options[countyIndex].text;
	}
	
    if(cityValue == '' && countyValue == ''){
    	alert("Please Select a County");		
		return;
	}

    var nURL = "";

	if(cityValue == ''){
		nURL = nURL + countyValue;
	}else{
		nURL = nURL + countyValue + "+" + cityValue;
	}
	endecaMatchesQuery = "endeca:matches(., "+'\"Action Type\"'+","+'\"'+"D"+'\"'+")";

    // emil 2018-02-28 fix issue in IE: double quotes in request string cause 400 at Tomcat
    // replace double quotes in endecaMatchesQuery with %22 special character
    endecaMatchesQuery = endecaMatchesQuery.replace(/"/g, "%22");

	var startURL = "Nrs=collection()/record[";
	var endURL = "]";	   

	var finalQuery = startURL+endecaMatchesQuery+endURL;
	
	var url  = 'plm_controller.jsp';
    url = url+'?N=' + nURL + '&tab='+tab+"&";
	url = url+finalQuery+'&searchResults=pc290Discharge';

	var ajaxUrl = "pc290Audit?county="+countyText+"&city="+cityText+"&userName="+userName+"&ipAddress="+ipAddress+"&qry_type=D";
	execute_get(ajaxUrl, true);	
	
	parent.location.href=url;
}