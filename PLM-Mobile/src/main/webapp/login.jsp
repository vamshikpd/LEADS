<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="com.plm.util.PLMSearchUtil" %>

<%
final Logger logger = Logger.getLogger(this.getClass());

%>
<%logger.warn("Forwarding the request to mobile.htm");%>
<% 
String sUrl = PLMSearchUtil.getFormattedIISURL(request,"/mobile.htm");
%>
<script type="text/javascript">
	top.location.href='<%=sUrl%>';
</script>