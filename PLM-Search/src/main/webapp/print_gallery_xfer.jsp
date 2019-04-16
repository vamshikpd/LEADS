<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Print Preview - Photo Lineup</title>

<script type="text/javascript" src="media/js/jquery-1.3.2.js"></script>
<script type="text/javascript" src="media/js/jquery-ui-1.7.2.custom.min.js"></script>
<script type="text/javascript" src="media/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="media/js/ui/ui.core.js"></script>
<script type="text/javascript" src="media/js/ui/ui.draggable.js"></script>
<script type="text/javascript" src="media/js/ui/ui.droppable.js"></script>
<script type="text/javascript" src="javascript/endeca_util.js"></script>
<script language="javascript" type="text/javascript">
function printPDF(){
	document.getElementById("htForm").submit();
}
</script>
</head>

<body id="radialSearchbody" onload="javascript:printPDF();">
<div id="printlineupsmtdiv">
<form id="htForm" method="post" action="GeneratePDFServlet">
	<input id="cdcNum" type="hidden" name="cdcNum" value="<%=request.getParameter("cdcNum")%>"/>
	<input id="pdfdisptype" type="hidden" name="pdfdisptype" value="gallery"/>
	<input id="psize" type="hidden" name="psize" value="p"/>
	
</form>
</div>
</body>
</html>