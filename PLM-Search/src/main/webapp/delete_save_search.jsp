<%@ page errorPage="error.jsp" %>
<%@ page import="com.endeca.ui.constants.UI_Props"%>
<%@ page import="com.plm.util.database.PLMDatabaseUtil" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Delete Save Search</title>
		<link rel="stylesheet" href="media/style/main.css" type="text/css"/>
	</head>
	<body>
<%
String delete_search = request.getParameter("delete_search") != null ? request.getParameter("delete_search") : null;
PLMDatabaseUtil.deleteSaveSearch(delete_search);
String sRedirectUrl =	UI_Props.getInstance().getValue(UI_Props.CONTROLLER)+"?N=0&keepThis=true&modal=true";
%>
		<script>
			location.href ='<%= sRedirectUrl%>';
		</script>
	</body>
</html>