<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
String title = (String)request.getAttribute("title");
String url = request.getContextPath() + "/" + (String)request.getAttribute("pdf_file");
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><%= title %></title>
</head>
<body>
<form name="display_pdf" id="display_pdf" action="<%= url%>" method="post">
	<input type="hidden" name="temp_object" value="1"/>
</form>
</body>
<script>
	document.getElementById("display_pdf").submit();
</script>
</html>
