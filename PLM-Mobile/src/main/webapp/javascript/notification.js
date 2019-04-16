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
	var endecaMatchesQuery = '';

	//for city
	var cityIndex = document.forms[formName]['City'].selectedIndex;
	if(cityIndex ==0){
		cityValue ='';
	}else{
		//cityValue=document.forms[formName]['City'].options[cityIndex].text;
		cityValue=document.forms[formName]['City'].options[cityIndex].value;
	}
	
	//for county
	var countyIndex = document.forms[formName]['County'].selectedIndex;	
	if(countyIndex == 0){
		countyValue ='';
	}else{
		//countyValue=document.forms[formName]['County'].options[countyIndex].text;
		countyValue=document.forms[formName]['County'].options[countyIndex].value;
	}
		
    if(cityValue == '' && countyValue == ''){
    	alert("Please Select a County");		
		return;
	}
    
    //var nURL = "0";
    var nURL = "";
    
	if(cityValue == ''){
		nURL = nURL + countyValue;
		//endecaMatchesQuery = endecaMatchesQuery+"endeca:matches(.,"+'\"County Name\"'+","+'\"'+countyValue+'\"'+") and ";		
	}/*else if(countyValue == ''){
		//endecaMatchesQuery = endecaMatchesQuery+"endeca:matches(.,"+'\"City\"'+","+'\"'+cityValue+'\"'+") and ";	
	}*/else{
		nURL = nURL + countyValue + "+" + cityValue;
		//endecaMatchesQuery = endecaMatchesQuery+"endeca:matches(., "+'\"City\"'+","+'\"'+cityValue+'\"'+") and " + endecaMatchesQuery+"endeca:matches(.," +'\"County Name\"'+","+'\"'+countyValue+'\"'+") and ";
	}
	endecaMatchesQuery = endecaMatchesQuery+"( endeca:matches(., "+'\"Action Type\"'+","+'\"'+"R"+'\"'+")"+" or endeca:matches(., "+'\"Has Address Changed\"'+","+'\"'+"Y"+'\"'+"))";

    // emil 2018-03-10 fix issue in IE: double quotes in request string cause 400 at Tomcat
    // replace double quotes in endecaMatchesQuery with %22 special character
    endecaMatchesQuery = endecaMatchesQuery.replace(/"/g, "%22");

	var startURL = "Nrs=collection()/record[";
	var endURL = "]";	   

	var finalQuery = startURL+endecaMatchesQuery+endURL;	
	var url  = 'plm_mobile_controller.jsp';
	url = url+'?N=' + nURL + '&tab='+tab+"&";
	url = url+finalQuery+'&page=searchresults&prevPage=registrantNotification';
	
	var ajaxUrl = "pc290Audit?county="+countyValue+"&city="+cityValue+"&userName="+userName+"&ipAddress="+ipAddress+"&qry_type=R";
	execute_get(ajaxUrl, true);
	top.location=url;
}

function dischargeNotifySearch(formName,userName,ipAddress){
	var isCountySelected = document.forms[formName]['countyselected'].value;
	if(isCountySelected == 0) {
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
		var endecaMatchesQuery = '';
	
		//for city
		var cityIndex = document.forms[formName]['City'].selectedIndex;
		if(cityIndex ==0){
			cityValue ='';
		}else{
			//cityValue=document.forms[formName]['City'].options[cityIndex].text;
			cityValue=document.forms[formName]['City'].options[cityIndex].value;
		}
		
		//for county
		var countyIndex = document.forms[formName]['County'].selectedIndex;	
		if(countyIndex ==0){
			countyValue ='';
		}else{
			//countyValue=document.forms[formName]['County'].options[countyIndex].text;
			countyValue=document.forms[formName]['County'].options[countyIndex].value;
		}
		
	    if(cityValue == '' && countyValue == ''){
	    	alert("Please Select a County");		
			return;
		}
	    
	    //var nURL = "0";
	    var nURL = "";
	    
		if(cityValue == ''){
			nURL = nURL + countyValue;
			//endecaMatchesQuery = endecaMatchesQuery+"endeca:matches(.,"+'\"County Name\"'+","+'\"'+countyValue+'\"'+") and ";		
		}/*else if(countyValue == ''){
			//endecaMatchesQuery = endecaMatchesQuery+"endeca:matches(.,"+'\"City\"'+","+'\"'+cityValue+'\"'+") and ";	
		}*/else{
			nURL = nURL + countyValue + "+" + cityValue;
			//endecaMatchesQuery = endecaMatchesQuery+"endeca:matches(., "+'\"City\"'+","+'\"'+cityValue+'\"'+") and " + endecaMatchesQuery+"endeca:matches(.," +'\"County Name\"'+","+'\"'+countyValue+'\"'+") and ";
		}
		endecaMatchesQuery = endecaMatchesQuery+" endeca:matches(., "+'\"Action Type\"'+","+'\"'+"D"+'\"'+")";

        // emil 2018-03-10 fix issue in IE: double quotes in request string cause 400 at Tomcat
        // replace double quotes in endecaMatchesQuery with %22 special character
        endecaMatchesQuery = endecaMatchesQuery.replace(/"/g, "%22");

		var startURL = "Nrs=collection()/record[";
		var endURL = "]";	   
	
		var finalQuery = startURL+endecaMatchesQuery+endURL;	
		var url  = 'plm_mobile_controller.jsp';
		//url = url+'?N=0&page=searchresults&';
		url = url+'?N=' + nURL + '&tab='+tab+"&";
		//url = url+finalQuery+'&prevPage=dischargeNotification';
		url = url+finalQuery+'&page=searchresults&prevPage=dischargeNotification';
		
		var ajaxUrl = "pc290Audit?county="+countyValue+"&city="+cityValue+"&userName="+userName+"&ipAddress="+ipAddress+"&qry_type=D";
		execute_get(ajaxUrl, true);
		top.location=url;
	}
}