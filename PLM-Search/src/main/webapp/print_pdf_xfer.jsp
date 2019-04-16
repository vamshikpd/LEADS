<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Print Preview - Photo Lineup</title>
		<script type="text/javascript" src="media/js/jquery-1.3.2.js"></script>
		<script type="text/javascript" src="media/js/jquery-ui-1.7.2.custom.min.js"></script>
		<script type="text/javascript" src="media/js/jquery-1.3.2.min.js"></script>
		<script type="text/javascript" src="media/js/ui/ui.core.js"></script>
		<script type="text/javascript" src="media/js/ui/ui.draggable.js"></script>
		<script type="text/javascript" src="media/js/ui/ui.droppable.js"></script>
		<script type="text/javascript" src="media/js/endeca_util.js"></script>
		<script language="javascript" type="text/javascript">
			function printPDF(){
				//var uls1 = window.top.document.getElementById("photoselectedinnerdivcontent").getElementsByTagName('ul');
				//alert(parent.document.getElementById("photoselectedinnerdivcontent").name);
				var uls1 = parent.document.getElementById("photoselectedinnerdivcontent").getElementsByTagName('ul');
				var ul1 = uls1[0];
				var lis1 = ul1.getElementsByTagName('li');
				var li1;
				var pdpc;
				//$('#printlineupshotselecteddivcontent').append('<div id="printlineupshotselecteddivcontentsub" style="margin-left:110px; margin-top:20px; height:360px;">');
				for (var i=0, iLen=lis1.length; i<iLen; i++){
					li1 = lis1[i];
					if (li1.id) {
						var liid1 = jQuery(li1).attr("id");
						if (pdpc != null){
							pdpc = pdpc + ","+liid1;
						}else{
							pdpc = liid1;
						}
					}
				}
				document.getElementById("pdpc").value = pdpc;
				document.getElementById("htForm").submit();
			}
		</script>
	</head>
	<body id="radialSearchbody" onload="javascript:printPDF();">
		<div id="printlineupsmtdiv">
			<form id="htForm" name="htForm" method="post" action="GeneratePDFServlet">
				<input id="pdpc" type="hidden" name="pdpc" />
				<input id="layout" type="hidden" name="layout" value="<%=request.getParameter("layout")%>" />
				<input id="susp" type="hidden" name="susp" value="<%=request.getParameter("susp")%>" />
				<input id="pdfdisptype" type="hidden" name="pdfdisptype" value="photolineup"/>
			</form>
		</div>
	</body>
</html>
