<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="com.plm.util.PLMSearchUtil" %>
<html>
<%
	final Logger logger = Logger.getLogger(this.getClass());
	logger.warn("not getting from cache");
	
	String sUrl = PLMSearchUtil.getFormattedPLMURL(request,"/plm_mobile_controller.jsp")+"?page=login";
%>
<head>
	<title>Parole LEADS 2.0 Terms and Conditions</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
	<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
	<link href="media/style/main.css" rel="stylesheet" type="text/css"/>
	<script type="text/javascript">
		function closeWindow() {
			location.href="<%=sUrl%>";
		}
	</script>
</head>
<body>
    <div align="center">
		<div id="Maindiv">
			<div id="Container">
				<div id="Header">
					<div class="logo"><span class="logo1">PAROLE</span><span class="logo2">LEADS2.0</span></div>
				</div>
				<div id="Middle">
					<div id="MainContainer">
                        <form name="tc" id="tc" action="">
							<jsp:include page="terms.html" flush="true" />
						</form>
					</div>
				</div>
				<div id="Footer"></div>
			</div>
		</div>
	</div>
</body>
</html>
