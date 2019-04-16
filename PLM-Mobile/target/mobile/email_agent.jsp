<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.endeca.ui.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.endeca.ui.logging.*" %>
<%@ page import="com.endeca.ui.charts.*" %>
<%@ page import="com.util.MapsList" %>
<%@ page import="com.report.*" %>
<%@ page import="org.dom4j.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.net.*" %>
<%@ page import="com.plm.constants.PLMConstants" %>
<html>
	<head>
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0;" />
		<title>Email Parole Agent</title>
	    <link href="media/style/main.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
<%
	String spec ="";
	PropertyContainer rec = (PropertyContainer)request.getAttribute("record");
	PropertyMap tempPropsMap = null;
	if(rec instanceof ERec) {
		spec = ((ERec)rec).getSpec();
		tempPropsMap = ((ERec)rec).getProperties();
	}else {
		spec = ((AggrERec)rec).getSpec();
		tempPropsMap = ((AggrERec)rec).getProperties();
	}
%>
		<div align="center">
			<div id="Maindiv">
				<div id="Container">
					<div id="Header">
						<div class="logo">
							<span class="logo1">PAROLE</span>
							<span class="logo2">LEADS2.0</span>
						</div>
						<div class="top">
							<div class="back"><a href="plm_mobile_controller.jsp?page=paroleInfo&R=<%=spec%>">Back</a></div>
							<%@ include file="logout_include_mobile.jsp" %>
							<div class="home"><a href="plm_mobile_controller.jsp?page=login">Home</a></div>
						</div>
					</div>
					<div id="Middle">
						<div id="MainContainer">
							<div class="searchresulttable detailspage">
								<div class="row1">
									<%@ include file="essence_info.jsp" %>
								</div>
								<form name="send_agent_email" id="savesearchform" action="send_agent_email.jsp">
								<input type="hidden" name="action" value="save" />
								<div id="emailAgentTop">				
									<div>
										<div>
                                        	<div>From: </div>
											<div>
<%
	//String fromAddress = request.getHeader("MAIL");
	String fromAddress = (String) session.getAttribute("email");
	session.setAttribute("email",fromAddress);
%>
												<label><%=fromAddress%></label>
											</div>
										</div>
										<div>
											<div>To: </div>
											<div><label><%=request.getParameter("toAddress")%></label></div>
										</div>
										<div>
											<div>Subject: </div>
											<div>
<% 
	String subject = "CDC# "+request.getParameter("cdcNum")+" - " +request.getParameter("firstName")+ " " + request.getParameter("lastName");
%>
												<label><%=subject%></label>
											</div>
										</div>
										<div>Message: </div>
										<div>
											<textarea name="message" cols="30" rows="6"></textarea>			
											<input type="hidden" name="cdcNum" value='<%=request.getParameter("cdcNum")%>' />
											<input type="hidden" name="subject" value='<%=subject%>' />
											<input type="hidden" name="toAddress" value='<%=request.getParameter("toAddress")%>' />
										</div>
									</div>
								</div>
								<input type="image" name="sendmail" value="send" src="media/images/global/send_email_button.gif" width="50" style="border:none" />
								<p style="font-size:9px"><%=UI_Props.getInstance().getValue("EMAIL_SENDER_DISCLAIMER_TEXT")%></p>				
								</form>
							</div>
						</div>
					</div>
					<div id="Footer"></div>
				</div>
			</div>
		</div>
	</body>
</html>