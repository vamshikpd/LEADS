<%@ page import="com.endeca.ui.constants.UI_Props"%>
<%@ page import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Assign Users</title>
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
				//window.opener.location.href = window.opener.location.href;
			}

			function validateInput(){
				var val = document.getElementById("userlist").value;
				if(!val) {
					val = document.getElementById("userlistfile").value;
				}
				if(!val) {
					alert("Either values/file field should be populated.");
					return false;
				}
				return true;
			}
		</script>
		<script language="JavaScript" src="media/js/plm_ohw.js"></script>
	</head>
<%
	String userCount = (String)request.getAttribute("recordsProcessed");
	request.removeAttribute("recordsProcessed");
	//System.out.println("Processed users..." + userCount);
	List<String> failedUsers = (ArrayList<String>)request.getAttribute("failedUsers");
%>	
	<body>
		<form name="moveusers" method="post" action="assignAdminUsers" enctype="multipart/form-data">
			<div id="emailAgentTop">  
				<div id="helpdiv"><a href="JavaScript:plmHelpPopup('<%=request.getContextPath() %>/ohw_help.jsp?topic_id=assign_pwdadmin');"><img src="media/images/global/help_icon.gif" alt="" /></a></div>
			</div>  
			<div id="emailAgentTop">
				<div>			
					<p>Please enter the Login ID or browse to the text file containing the comma seperated values for multiple users 
							to be assigned to the LEADSPasswordAdmins role.</p>
				</div>				
				<div><img src="media/images/global/spacer.gif" alt="" width="10" /></div>
			</div>		
			<div id="emailAgentTop">
				<div class="inputname">Enter one or more Login IDs (Seperate Login IDs by comma ",")</div>
				<div>   
					<div class="inputnameMessage">
						<textarea name="userlist" id="userlist" cols="44" rows="5"></textarea>			
					</div>
				</div>
				<div><img src="media/images/global/spacer.gif" alt="" width="10" /></div>
			</div>
			<div id="emailAgentTop">
				<div class="fromaddress">
					<label>OR</label>
				</div>
				<div><img src="media/images/global/spacer.gif" alt="" width="10" /></div>
			</div>
			<div id="emailAgentTop">
				<div class="inputname">Select a comma seperated file :</div>
				<div>   
					<div class="inputnamesubject">
						<input name="userlistfile" id="userlistfile" type="file"></input>
					</div>
				</div>	
				<div><img src="media/images/global/spacer.gif" alt="" width="10" /></div>
				<br clear="all"/>
			</div>
<%	
	if(userCount != null) {
%>
			<div id="emailAgentTop">
				<div class="fromaddress">
					Processed Records :  <b><%=userCount%></b><br/>      
					Failed Records :  <b>
					<%=(failedUsers.size()>0)? failedUsers : 0 %>
					</b>
				</div>
				<div><img src="media/images/global/spacer.gif" alt="" width="10" /></div>
				<br clear="all"/>
			</div>
<%
	}
%>			
		    <div id="advSearchMiddle2" class="buttons">
				<div id="advSearchbottom2main" style="float:none">
					<div class="advsearchbutton"><input type="image" name="Submit" value="Submit" src="media/images/global/submit_button.gif" onClick="javascript:return validateInput();"/></div>
					<div class="advsearchbutton"><input type="image" onClick="this.form.reset();return false;" name="clearfields" value="ClearFields" src="media/images/global/clear_fields_button.gif"/></div>
					<div class="advsearchbutton"><a title="Close" onClick="parent.tb_remove()" href="#"><img src="media/images/global/cancel_button.gif" border="0"/></a></div>
				</div>
			</div>	
		</form>
	</body>
</html>