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

// Open a new windows with the given url and title
function ConstructURL(url,removeterms,addterms) {
	for (var i=0; i<params.length; i++) {
	for (var i=0; i<addterms.length; i++) {
	if (newurl.length > 0) {
	}
	}
	return newurl;
function BuildURLArray(oldurl) {
	var returnArray = new Array();
	if (oldurl == "CURRENTURL"){
	}else if (oldurl == "BLANKURL") {
	}
	}		
		url = url.substr(1);
	}
function CheckArray(removeterms,checkterm) {
/*function GetValue(url, term) {