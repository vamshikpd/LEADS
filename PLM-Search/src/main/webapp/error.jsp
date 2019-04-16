<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page isErrorPage="true" %>
<%@ page import="com.plm.util.PLMSearchUtil" %>
<%@ page import="java.io.*" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.net.URLDecoder" %>
<!DOCTYPE html>
<html>
	<head>
		<title>Exception occurred</title>
	</head>
	<body>
		<div style="float:left; height:450px; padding-left:10px">
			<h3>Parole LEADS 2.0 Search is currently not available. Please try again later or contact your Help Desk coordinator.</h3>
<%
			final Logger logger = Logger.getLogger("Search_Error");

			String queryString = "";
			if (request.getQueryString() != null) {
				queryString = request.getQueryString();

				try {
					String tempQueryString = URLDecoder.decode(queryString, "UTF-8");
					queryString = PLMSearchUtil.encodeGeoCodeCriteria(tempQueryString);
				} catch (Exception e) {
				    queryString = request.getQueryString();
				}
			}

			if(exception != null){ 
			 	StringWriter sw = new StringWriter();
			 	exception.printStackTrace(new PrintWriter(sw));
			 	StringBuffer sbTrace = new StringBuffer();
			 	sbTrace.append("\n");
			 	sbTrace.append("LoginId		:")
                        .append(session.getAttribute("userId"))
                        .append(", Full Name		:")
                        .append(session.getAttribute("firstName"))
                        .append(" ")
                        .append(session.getAttribute("lastName"));
			 	sbTrace.append("\n");
			 	sbTrace.append("Group		:")
                        .append(session.getAttribute("groupName"))
                        .append(", UserType 	:")
                        .append(session.getAttribute("userType"));
			 	sbTrace.append("\n");
			 	sbTrace.append("Request		:").append(queryString);
			 	sbTrace.append("\n");
			 	sbTrace.append("Exception	:").append(sw.toString());
			 	logger.error(sbTrace);
			}

%>
		</div>
	</body>
</html>
