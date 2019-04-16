<%@ page errorPage="error.jsp" %>
<%@page import="java.util.*"%>
<%@page import= "javax.servlet.ServletConfig"%>
<%@page import= "javax.servlet.ServletContext"%>
<%@page import= "javax.servlet.ServletException"%>
<%@page import= "javax.servlet.http.HttpServlet"%>
<%@page import= "javax.servlet.http.HttpServletRequest"%>
<%@page import= "javax.servlet.http.HttpServletResponse"%>
<%@ page import="com.endeca.ui.constants.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<title>Olat Redirect Page</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<script type="text/javascript">
			function olatSubmit(){
				var form = document.forms['olatredirect'];
				document.forms['olatredirect'].submit();
			}
		</script>
<%
	String olatPageURL =null;
	olatPageURL = UI_Props.getInstance().getValue("OLAT_LANDING_URL");
	String userId = request.getHeader("USERID");
	if(userId == null){
		userId = (String)session.getAttribute("userId");
	}
	String groupName = request.getHeader("GROUPS");
	if(groupName == null) {
		groupName = (String)session.getAttribute("groupName");
	}
	String firstName = request.getHeader("NAME.FIRST") != null ? request.getHeader("NAME.FIRST") : userId;
	if(firstName == null){
		firstName = (String)session.getAttribute("firstName");
	}
	String lastName = request.getHeader("NAME.LAST") != null ? request.getHeader("NAME.LAST") : "";
	if(lastName == null){
		lastName = (String)session.getAttribute("lastName");
	}
	String email = request.getHeader("EMAIL") != null ? request.getHeader("EMAIL") : "";
	if(email == null){
		email = (String)session.getAttribute("email");
	}
	String userType = (String)session.getAttribute("userType");
	String fromLoginPage = (String)session.getAttribute("fromLoginPage");
	session.setAttribute("fromLoginPage", null);
%>
	</head>
	<body onload="olatSubmit()">
		<form name="olatredirect" id="olatredirect" action="<%=olatPageURL%>" method="post">
			<input type="hidden" name="group" value="<%= groupName%>"/>
			<input type="hidden" name="userId" value="<%= userId%>"/>
			<input type="hidden" name="firstName" value="<%= firstName%>"/>
			<input type="hidden" name="lastName" value="<%= lastName%>"/>
			<input type="hidden" name="email" value="<%= email%>"/>
			<input type="hidden" name="fromLoginPage" value="<%= fromLoginPage%>"/>
		</form>
	</body>
</html>