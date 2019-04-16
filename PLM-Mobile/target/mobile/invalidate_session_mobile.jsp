<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="com.plm.util.PLMSearchUtil" %>
<%
final Logger logger = Logger.getLogger(this.getClass());
logger.info("Initiated Mobile Session Invalidation...");

String sUrl = PLMSearchUtil.getFormattedIISURL(request,"/mobilelogout");
session.invalidate();

logger.info("Mobile Session Invalidated...");
%>
<script type="text/javascript">
	top.location.href='<%=sUrl%>';
</script>   