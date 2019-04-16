<!-- send_agent_email.jsp
	10-18-2011 Modfied the postAgentMail request to include addCC. -- L. Baird
 -->
 <%@ page errorPage="error.jsp" %>
<%@ page import="com.endeca.ui.constants.UI_Props"%>
<%@ page import="com.plm.util.PLMSearchUtil" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Send Email to Agent</title>
		<link rel="stylesheet" href="media/style/main.css" type="text/css"/>
	</head>
	
	<body>
<%
		//added addCC parameter -- L. Baird
		PLMSearchUtil.postAgentMail(request.getParameter("toAddress"),request.getParameter("addCC"),request.getParameter("subject"),request.getParameter("message"),(String)session.getAttribute("email"));
		String cdcNum = request.getParameter("cdcNum");
		String sRedirectUrl = UI_Props.getInstance().getValue(UI_Props.CONTROLLER)+"?R="+cdcNum;
%>
		<script>
			parent.window.location.href ='<%= sRedirectUrl%>';
		</script>
	</body>
</html>