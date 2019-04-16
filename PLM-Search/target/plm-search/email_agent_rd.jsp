<!-- email_agent_rd.jsp
	10-18-2011 Added a box to hold the CC addresses -- L. Baird  
 -->
 <%@ page errorPage="error.jsp" %>
<%@ page import="com.endeca.ui.constants.UI_Props"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		
		<title>Email Agent</title>
		
		<script src="media/js/jquery-1.3.2.js" type="text/javascript"></script>
		<script type="text/javascript">
			jQuery(document).ready(function() {
				setTimeout(function(){
					$("form :textarea[name='message']:enabled:first").focus();
				},200);
			});
		</script>
		
		<link rel="stylesheet" href="media/style/main.css" type="text/css"/>
		
		<script language="javascript" type="text/javascript">
			function openurl(link1){
				self.close();
				var ReferURL = parent.window.location.href;
				parent.window.location.href = link1;
			}
		</script>
		<script language="JavaScript" src="media/js/plm_ohw.js"></script>
	</head>
	
	<body id="radialSearchbody">
		<form name="send_agent_email" id="savesearchform" action="send_agent_email.jsp">
			<input type="hidden" name="action" value="save" />
			
			<div id="emailAgentTop">
				<div id="helpdiv">
					<a href="JavaScript:plmHelpPopup('<%=request.getContextPath() %>/ohw_help.jsp?topic_id=email_agent');"><img src="media/images/global/help_icon.gif" alt="" /></a>
				</div>
				
				<div>
					<div>
						<div class="inputname">From</div>
						
						<div class="fromaddress">
							<%
								 //String fromAddress = request.getHeader("MAIL");
								 String fromAddress = (String) session.getAttribute("email");
								 session.setAttribute("email",fromAddress);
							%>
							<label><%=fromAddress%></label>
						</div>
						
						<div><img src="media/images/global/spacer.gif" alt="" width="10" /></div>
						
						<br clear="all">
						
						<div>
							<div class="inputname">To</div>
							<div class="toaddress"><label><%=request.getParameter("toAddress")%></label></div>
						</div>
						
						<br clear="all">

						<!-- Added for Address CC -- L. Baird -->
						<div>
							<div class="inputname">CC</div>
							<div class="addcc"><label><%=request.getParameter("addCC")%></label></div>
						</div>
					</div>
					<br clear="all">
					<div>
						<div class="inputname">Subject</div>
						<div class="inputnamesubject">
							<% String subject = "CDC# "+request.getParameter("cdcNum")+" - " +request.getParameter("firstName")+ " " + request.getParameter("lastName");%>
							<label><%=subject%></label>
						</div>
					</div>
					
					<br clear="all">
					
					<div class="inputname">Message</div>
					
					<div class="inputnameMessage">
						<textarea name="message" cols="44" rows="14"></textarea>
						<input type="hidden" name="cdcNum" value='<%=request.getParameter("cdcNum")%>' />
						<input type="hidden" name="subject" value='<%=subject%>' />
						<input type="hidden" name="toAddress" value='<%=request.getParameter("toAddress")%>' />
						<!-- Added for Address CC -- L. Baird -->
						<input type="hidden" name="addCC" value='<%=request.getParameter("addCC")%>' />
					</div>
				</div>
			</div>
			
			<div id="advSearchMiddle2" class="buttons">
				<div id="advSearchbottom2main" style="float:none">
					<div class="advsearchbutton"><input type="image" name="find" value="Save" src="media/images/global/send_email_button.gif" /></div>
					<div class="advsearchbutton"><input type="image" onClick="this.form.reset();return false;" name="clearfields" value="ClearFields" src="media/images/global/clear_fields_button.gif"/></div>
					<div class="advsearchbutton"><a title="Close" onClick="parent.tb_remove()" href="#"><img src="media/images/global/cancel_button.gif" border="0"/></a></div>
				</div>
			</div>
			
			<br clear="all">
			
			 <div id="emailAgentTop">
					<p><%=UI_Props.getInstance().getValue("EMAIL_SENDER_DISCLAIMER_TEXT")%></p>
			</div>
		</form>
	</body>
</html>