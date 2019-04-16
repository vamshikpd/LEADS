//Browser Print function
/*function printit() { 
     if(window.print) {
         window.print();  
     }else {
         if(confirm("an active object will be used in your browser, to print your report")) {
	    var WebBrowser = '<OBJECT ID="WebBrowser1" WIDTH=0 HEIGHT=0 CLASSID="CLSID:8856F961-340A-11D0-A96B-00C04FD705A2"></OBJECT>';
	    document.body.insertAdjacentHTML('beforeEnd', WebBrowser);
	    WebBrowser1.ExecWB(6, 2);
	    //Use a 1 vs. a 2 for a prompting dialog box 
	    WebBrowser1.outerHTML = "";  
	 }
     } 
}*/

/*function findPosX(obj) {
	var curleft = 0;
	if (obj.offsetParent)
	{
		while (obj.offsetParent)
		{
			curleft += obj.offsetLeft
			obj = obj.offsetParent;
		}
	}
	else if (obj.x)
		curleft += obj.x;
	return curleft;
}*/

/*function findPosY(obj) {
	var curtop = 0;
	if (obj.offsetParent)
	{
		while (obj.offsetParent)
		{
			curtop += obj.offsetTop
			obj = obj.offsetParent;
		}
	}
	else if (obj.y)
		curtop += obj.y;
	return curtop;
}*/

// Open a new windows with the given url and title/*function openWindow(url, title) {	OpenWin = this.open(url, title, "toolbar=yes,status=yes,menubar=yes,location=yes,scrollbars=yes,resizable=yes");}*/// Dynamic DIV display/hide functions/*function displayBTWN(funcval, divid) {	var btwndiv = document.getElementById(divid);	if(funcval == "BTWN") {		btwndiv.style.display="block";	}else {		btwndiv.style.display="none";	}}*//*function displayelement(divid) {	var divelement = document.getElementById(divid);	if(divelement.style.display == "none") {		divelement.style.display="block";	}else {		divelement.style.display="none";	}}*///---------------------------------------------------------------------------// Utility routines used by various javascript functions to create new querys// from existing urls.  If current browser url does not reflect current query,// these functions are not useful (since they are not aware of the current// application state.)  These functions are used sparingly in the reference// implementation only in conjunction with modules that contain form element.//---------------------------------------------------------------------------
function ConstructURL(url,removeterms,addterms) {	var params = BuildURLArray(url);	var newurl = "";
	for (var i=0; i<params.length; i++) {		val = CheckArray(removeterms,params[i][0]);		if (val == -1)			newurl = newurl + "&" + params[i][0] + "=" + params[i][1];	}
	for (var i=0; i<addterms.length; i++) {		newurl = newurl + "&" + addterms[i];	}
	if (newurl.length > 0) {		newurl = newurl.substr(1);		newurl = "?"+newurl;
	}	else {		newurl = "?N="+eneroot;
	}
	return newurl;}
function BuildURLArray(oldurl) {
	var returnArray = new Array();	var url;
	if (oldurl == "CURRENTURL"){		url = location.search;
	}else if (oldurl == "BLANKURL") {
	}	else {		var tokens = oldurl.split("?");		url = "?"+tokens[1];
	}			if (url) {
		url = url.substr(1);		var params = url.split("&");		for (var i=0; i<params.length; i++) {			var param = params[i].split("=");			returnArray[i] = param;		}
	}	return returnArray;}
function CheckArray(removeterms,checkterm) {	for (var i=0; i<removeterms.length; i++) {		if (removeterms[i] == checkterm)			return 1;	}	return -1;}
/*function GetValue(url, term) {	var params = BuildURLArray(url);	for (var i=0; i<params.length; i++) {		if (params[i][0] == term) {			return params[i][1];		}	}	return -1;}*/