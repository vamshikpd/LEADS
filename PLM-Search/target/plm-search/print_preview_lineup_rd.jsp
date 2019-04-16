<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Print Preview - Photo Lineup</title>
<link rel="stylesheet" href="media/style/main.css" type="text/css"/>
<STYLE TYPE='text/css'>
P.pagebreakhere {page-break-before: always}
</STYLE>

<script type="text/javascript" src="media/js/jquery-1.3.2.js"></script>
<script type="text/javascript" src="media/js/jquery-ui-1.7.2.custom.min.js"></script>
<script type="text/javascript" src="media/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="media/js/ui/ui.core.js"></script>
<script type="text/javascript" src="media/js/ui/ui.draggable.js"></script>
<script type="text/javascript" src="media/js/ui/ui.droppable.js"></script>
<script language="javascript" type="text/javascript">
function printFn(){
	var cnt =  window.open('','LineUpPrintWindow','width=720,height=6,left=-1000,top=-1000', config='toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, directories=no, status=no');

	//var text = document.getElementById("printlineupsmtdiv").innerHTML;
	var text = document.getElementById("printlineupshotselecteddiv").innerHTML;
	//alert(text);
	//alert(text);
    //var html = ('<html><head><title>Print Photo Lineup</title></head><body>').append((window.top.document.getElementById("radialSearchbody").innerHTML).clone()).html() + '</body></html>';
	var html = '<html><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8" /><title>Print Preview - Photo Lineup</title><link rel="stylesheet" href="media/style/print.css" type="text/css"/><STYLE TYPE=\'text/css\'>P.pagebreakhere {page-break-before: always}</STYLE><title>Print Photo Lineup</title></head><body><div id=\"xyz\" align=\"center\">' + text + '</div></body></html>';

    cnt.document.open();
    cnt.document.write(html);
	cnt.print();
	cnt.document.close();
	return false;
}
</script>

<script language="javascript" type="text/javascript">
function openurl(link1){
self.close();
var ReferURL = parent.window.location.href;
parent.window.location.href = link1;
//window.opener.location.href = window.opener.location.href;
}
</script>
<script language="javascript" type="text/javascript">

function printbyfn(){
var getiddiv = document.getElementById("printlineupshotselecteddivcontent");

var printby = document.getElementsByName('printby');
for (a=0;a<2;a++){
if (printby[a].checked){
//alert(printby[a].value);
if(printby[a].value=="hr"){
//var getiddiv1 = document.getElementById("printlineupshotselecteddivcontentvertical");
//alert("HR");
getiddiv.className="printlineupshotselecteddivcontent";
}
else if(printby[a].value=="vr"){
//alert("VR");
//alert(getiddiv.id);
getiddiv.className="printlineupshotselecteddivcontentvertical";
}
}
}
}
</script>
</head>

<body id="radialSearchbody">
<div id="printlineupsmtdiv"><br clear="all" />

       		<div id="printlineupshotselecteddiv">
        	<div id="printlineupshotselecteddivcontent" class="printlineupshotselecteddivcontent">
            <!--div id="printlineupshotselectedrow1" class="printlineupshotselectedrow1">
            </div-->
            </div>
			</div>

</div>
<br clear="all" /><br clear="all" />
<div><strong>Select Orientation to print:</strong></div>
<div id="changeprintlayout">
<input name="printby" type="radio" value="vr" onclick="printbyfn()"/> Vertical<br clear="all" />
<input name="printby" type="radio" value="hr" checked="checked" onclick="printbyfn()" /> Horizontal
</div>

<div class="searchbutton"><a href="#" onclick="printFn(); return false;"><img src="media/images/global/print_layout_button.gif" alt="" border="0" /></a></div>
<script language="javascript" type="text/javascript">

//alert(window.top.document.getElementById("photoselectedinnerdivcontent").innerHTML);
document.getElementById("printlineupshotselecteddivcontent").innerHTML = window.top.document.getElementById("photoselectedinnerdivcontent").innerHTML;
//alert(document.getElementById("printlineupshotselecteddivcontent").innerHTML);
$('#photoselectedinnerdivcontentdiv').attr("style", "height:800px; margin:20px;");
var uls = document.getElementById("printlineupshotselecteddivcontent").getElementsByTagName('ul');
var ul = uls[0];

var lis = ul.getElementsByTagName('li');
var li;

for (var i=0, iLen=lis.length; i<iLen; i++){
	li = lis[i];
	if (li.id) {
		jQuery(li).attr("style", 'border:2px #889 solid; display: inline; margin:5px');
		$('#photo').find('img').animate({ height: '198px' }).animate({ width: '162px' });
		$('#photo').attr("id","photo"+i);
		$('#photo'+i).append("<br />#");
		$('#photo'+i).append(i+1);
	}
}
$('#printlineupshotselecteddivcontent').append('<div style="border: 2px #CECECE solid; background-color: #A6AEC3; height: 1px; position:relative"><br /><P CLASS="pagebreakhere"></div>');

var uls1 = window.top.document.getElementById("photoselectedinnerdivcontent").getElementsByTagName('ul');
var ul1 = uls1[0];

var lis1 = ul1.getElementsByTagName('li');
var li1;

$('#printlineupshotselecteddivcontent').append('<div id="printlineupshotselecteddivcontentsub" style="margin-left:110px; margin-top:20px; height:360px;">');
for (var i=0, iLen=lis1.length; i<iLen; i++){
	li1 = lis1[i];
	if (li1.id) {
		var liid1 = jQuery(li1).attr("id");
		var str1 = liid1.split(":");
		// get contents of li and put those as contents of 'printlineupshotselectedphoto' div
		$('#printlineupshotselecteddivcontentsub').append('<div id="printlineupshotselectedphoto" class="printlineupshotselectedphoto">');
		$('#printlineupshotselectedphoto').append(li1.innerHTML);
		$('#printlineupshotselecteddivcontentsub').append('</div>');
		$('#printlineupshotselectedphoto').attr("id","printlineupshotselectedphoto"+i);
		$('#photo').css({ "border": li1.style.border });
		//$('#printlineupshotselectedphoto'+i).append('<div>' + str1[0] + '<br />' + str1[1] + '</div>');

		$('#photo',$('#printlineupshotselectedphoto'+i)).attr("id","photo"+i);
		$('#photo'+i,$('#printlineupshotselectedphoto'+i)).append('<br />'+ str1[0] + '<br />' + str1[1]);
		//$('#photo'+i).append('<br />'+ str1[0] + '<br />' + str1[1]);
	}
}
</script>
</body>
</html>
