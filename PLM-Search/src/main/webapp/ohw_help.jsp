<%@page import="java.util.*"%>
<%@ page import="com.endeca.ui.constants.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<HTML>
	<HEAD>
		<TITLE> OHW Help </TITLE>
	</HEAD>
<%
String sReqTopic = request.getParameter("topic_id") != null ? "?topic="+request.getParameter("topic_id") : "";
%>
	<BODY>
		<iframe height="560" width="100%" frameborder=0 src="<%=request.getContextPath()%>/help/state<%=sReqTopic%>"></iframe>
	</BODY>
</HTML>