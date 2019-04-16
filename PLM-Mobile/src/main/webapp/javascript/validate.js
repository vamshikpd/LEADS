/*function validateZipcode(ZipCode,formName,elementName){
	if(isNaN(ZipCode)){
		alert("enter only digits");		
		document.forms[formName][elementName].value ='';
		return;
	}
}*/


/*function validateDateOnKeyUp(bDate,formName,elementName){
  if(isNaN(bDate)){	 
	  alert("Enter Date in mmddyyyy format");
	  document.forms[formName][elementName].value ='';
	  return;
  }
}*/

/*function validateDate(bDate,formName,elementName){
     if(bDate != '' && bDate.length != 8 ){
		alert("Enter Date in mmddyyyy format");
		document.forms[formName][elementName].value ='';
		return false;
	}
	else{
        return true;
	}
}*/

/*function validateCaseAndReason(saveCase,saveReason,formName,page){	
	var sCase = document.getElementById(saveCase).value;
	var sReason = document.getElementById(saveReason).value;
	if(sCase =='' && sReason ==''){
			alert("Please enter Case# and Select a reason for search");
			return false;
	}else{
		if(sCase == ''){
			alert("Please enter Case#");
			return false;
		}
		if(sReason==''){
			alert("Please select Reason for Search");
			return  false;
		}
	}
	document.getElementById('page').value=page;
	document.forms[formName].action = "saved_case_and_reason_action.jsp";
	document.forms[formName].submit();
}*/

function validateCaseAndReason(saveCase,saveReason){	
	var sCase = document.getElementById(saveCase).value;
	var sReason = document.getElementById(saveReason).value;
	if(sCase =='' && sReason ==''){
			alert("Please enter Case# and Select a reason for search");
			return false;
	}else{
		if(sCase == ''){
			alert("Please enter Case#");
			return false;
		}
		if(sReason==''){
			alert("Please select Reason for Search");
			return  false;
		}
	}
}

function validateLogin(userName ,  password){
	var sUserName = document.getElementById(userName);
	var sPassword = document.getElementById(password);
	if(sUserName == '' && sPassword == ''){
		alert("Please Enter User Name and Password");
		return false;
	}
	else{
		if(sUserName ==''){
			alert("Please Enter User Name");
			return false;
		}if(sPassword == ''){
			alert("please Enter Password");
			return false;
		}
	}
}