<%@ page import="java.util.*,java.text.*"%>
<%@ page import="com.plm.constants.PLMConstants" %>
<%@ page import="com.plm.util.database.PLMDatabaseUtil" %>
<%@ page import="com.endeca.navigation.*" %>
<%@ page import="com.endeca.ui.constants.UI_Props"%>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="com.plm.util.PLMSearchUtil" %>
<%final Logger logger = Logger.getLogger(this.getClass()); %>
<%
	String sPage = request.getParameter("page");
	logger.warn("sPage:"+sPage);
	String case_no = request.getParameter("case").toString();
	logger.warn("case_no:"+case_no);
	session.setAttribute("case_no",case_no);
	String reason_no = request.getParameter("reason").toString();
	logger.warn("reason_no:"+reason_no);
	session.setAttribute("reason_no",reason_no);
	//UrlGen redirectUrl = new UrlGen(request.getParameter("url"), "UTF-8");
	//String url = "plm_mobile_controller.jsp?page="+sPage;
	String url=PLMSearchUtil.getFormattedPLMURL(request,"/plm_mobile_controller.jsp")+"?page="+sPage;
	logger.warn("url:"+url);
%>
<script type="text/javascript">
	parent.window.location.href ="<%=url%>";
</script>