function validateZipcode(ZipCode,formName,elementName){
	if(isNaN(ZipCode)){
		alert("Please enter digits only");		
		document.forms[formName][elementName].value ='';
		return;
	}
}


function validateDateOnKeyUp(bDate,formName,elementName){
  if(isNaN(bDate)){	 
	  alert("Please enter digits only");
	  document.forms[formName][elementName].value ='';
	  return;
  }
}

function validateYear(year,formName,elementName){
	 if(year != '' && year.length != 4 ){
			alert("Please Enter a valid year");
			document.forms[formName][elementName].value ='';
			//alert("IT SHD BE REMOVED NE");
			return false;
		}
		else{
	        return true;		
		}
	}
function validateDate(bDate,formName,elementName){
     if(bDate != '' && bDate.length != 8 ){
		alert("Please enter date in mmddyyyy format");
		document.forms[formName][elementName].value ='';
		//alert("IT SHD BE REMOVED NE");
		return false;
	}
	else{
        return true;		
	}
}
function validateCaseAndReason(saveCase,saveReason,formName){
	var saveCase1 = document.getElementById(saveCase).value;
	var saveReason1 = document.getElementById(saveReason).value;
	if(saveCase1=="" && saveReason1 == ""){
		alert("Please enter Case# and select Reason for Search");
		return false;
	}else if(saveCase1 != "" && saveReason1 != ""){
		document.forms[formName].submit();
	}
	if(saveCase1==""){
		alert("Please enter Case#");
		return false;
	}
		if(saveReason1==""){
	alert("Please select Reason for Search");
		return  false;
	}
}