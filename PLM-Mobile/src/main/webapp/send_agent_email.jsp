<%@ page import="com.endeca.ui.constants.UI_Props"%>
<%@ page import="com.plm.util.PLMSearchUtil" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Send Email to Agent</title>
		<link rel="stylesheet" href="media/style/main.css" type="text/css" />
	</head>
	<body>
<%
	PLMSearchUtil.postAgentMail(request.getParameter("toAddress"),request.getParameter("subject"),request.getParameter("message"),(String)session.getAttribute("email"));
	String cdcNum = request.getParameter("cdcNum");
	String sUrl = PLMSearchUtil.getFormattedPLMURL(request,"/plm_mobile_controller.jsp")+"?page=paroleInfo"+"&R="+cdcNum;
%>
		<script type="text/javascript">
			parent.window.location.href ='<%=sUrl%>';	 
		</script>
	</body>
</html>
