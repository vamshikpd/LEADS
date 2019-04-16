<%
String errormsg = "";
if((String)request.getAttribute("status")!=null){
	errormsg = (String)request.getAttribute("status");
}

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>Parole LEADS 2.0</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript" src="media/js/jquery-1.3.2.js"></script>
<link href="media/style/thickbox.css" rel="stylesheet" type="text/css" />
<link href="media/style/main_welcome.css" rel="stylesheet"
	type="text/css" />
	<link href="media/style/change_password.css" rel="stylesheet"
	type="text/css" />
<script type="text/javascript">
      
      function changePassword()
      {
            var res = validate_oldpassword();
            if(res == false){
            	return false;
            }else{
            	document.cpform.success.value="Y";
            }
            
      }
      function cancel(){
    	  document.cpform.success.value="N";
      }
     
           
      function validate_oldpassword(){
    	 
    	  var pattern = new RegExp("^.*(?=.{8,})(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])");
    	  var oldpwd = document.getElementById("oldpassword").value;
    	  var result = pattern.test(oldpwd);
    	  if(result == false){
    		  alert("Old password doesn't meet the password policy. Please Enter Old password again!!");
    		  document.getElementById("oldpassword").value = "";
    		  document.getElementById("newpassword").value ="";
    		  document.getElementById("confirmpassword").value = "";
    		  return false;
    	  }else{
    		  var res = validate_newpassword();
    		  if(res == false){
              	return false;
              }
    	  }
    	  
      }
      function validate_newpassword()
      {
    	  
    	  var pattern = new RegExp("^.*(?=.{8,})(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])");
    	  var newpwd = document.getElementById("newpassword").value;
    	  var oldpwd = document.getElementById("oldpassword").value;
    	  var result = pattern.test(newpwd);
    	  if(newpwd == oldpwd){
    		  alert("New password and Old passwords are matched. Please Enter New password again!!");
    		  document.getElementById("newpassword").value ="";
    		  document.getElementById("confirmpassword").value = "";
    		  return false;
    	  }else if(result == false){
    		  alert("New password doesn't meet the password policy. Please Enter New password again!!");
    		  document.getElementById("newpassword").value ="";
    		  document.getElementById("confirmpassword").value = "";
    		  return false;
    	  }else{
    		  var res = validate_retypepassword(); 
    		  if(res == false){
                	return false;
                }
    	  } 
    	 
      }
      function validate_retypepassword()
      {
    	  
    	  var cfrmpwd = document.getElementById("confirmpassword").value;    	
    	  var newpwd = document.getElementById("newpassword").value;
    	  if(cfrmpwd != newpwd){
    		  alert("New password and Retype passwords are not matched. Please Enter Retype password again!!");
    		  document.getElementById("confirmpassword").value = "";
    		  return false;
    	  }
    	 
      }
</script>
</head>
<body>
	<div align="center">
		<div id="cpMaindiv">
			<div id="cpContainer">
				<div id="Header">
					<div id="cplogoimg">
						<img border="0" alt="CDCR" src="media/images/global/logo.gif" />
					</div>
					<div id="cptopglobalnav">
						<div id="Top">
							<div>
								<div style="float: left; width: 100%; height: 41px">
									<div id="paroleesearch_text">
										<img alt="" src="media/images/global/paroleesearch_text.gif">
									</div>
								</div>
							</div>
						</div>
						<!-- Start Global Nav Table -->
						<div id="GlobalNav">
							<div></div>
						</div>
						<!--End Global Nav Table -->
					</div>
				</div>
				<div class="content_box"
					id="cpMiddle">
					<div id="cpMiddletop">
						<div style="margin-left: 3em; margin-right: 3em;" id="b_col3">
							<div class="b_clearfix" id="b_col3_content">
								<div class="b_floatscrollbox" id="b_col3_content_inner">
									<div class="o_login">
										
										<form name="cpform" action="cpredirect" method="post">
											<input type="hidden" name="success" id="success" value=""/>
											<h3 id="cpheader">Parole
												Leads 2.0</h3>
											<div class="o_login_form"
												id="cploginform">
												<fieldset
													id="cpfieldset">
													<legend>Change Password </legend>

													<div class="b_form">
														<b></b>
														<font color="red"><%=errormsg%></font>
														<div class="b_clearfix" id="ber_lf_loginloginForm">
															<table border="0" cellpadding="0" cellspacing="0">
																<tr class="b_form_element_wrapper">
																	<td border="0" class="b_form_element_label"><div
																			id="cpoldpassword">Old
																			Password</div>
																	</td>
																	<td class="b_form_element"><div
																			id="cpoldpasswordtext">
																			<input name="oldpassword" id="oldpassword"
																				type="password" value=""
																				style="border: 1px #889 solid;" size="">
																		</div>
																	</td>
																	<td style="padding-left: 80px"></td>
																	<td style="padding-left: 120px"></td>


																</tr>
																<tr class="b_form_element_wrapper">
																	<td border="0" class="b_form_element_label">
																		<div id="cpnewpassword">Enter
																			new password:</div>
																	</td>
																	<td class="b_form_element"><div
																			id="cpnewpasswordtext">
																			<input name="newpassword" id="newpassword" type="password" value=""
																				style="border: 1px #889 solid;" size=""
																				maxlength="128">
																		</div>
																	</td>
																	<td><div
																			style="padding-top: 20px; padding-left: 100px;"></div>
																	</td>
																	<td>
																		<div
																			id="cpwarning">
																			<p>
																				<font color="red">WARNING</font><span> - YOU
																					MUST SET A NEW PASSWORD.</span>
																			</p>
																		</div>
																	</td>

																</tr>
																<tr class="b_form_element_wrapper">
																	<td border="0" class="b_form_element_label">
																		<div id="cpretyprpassword">Retype
																			password:</div>
																	</td>
																	<td class="b_form_element">
																		<div id="cpretyprepasswordtext">
																			<input name="confirmpassword" id="confirmpassword" type="password"
																				value="" size="" maxlength="128"
																				style="border: 1px #889 solid;">
																		</div>
																	</td>
																</tr>
																<tr class="b_form_element_wrapper">
																	<td border="0" class="b_form_element_label">
																		<div style="padding-top: 3px; font-size: 13px;"></div>
																	</td>
																	<td class="b_form_element">
																		<div class="b_button_group"
																			id="cpbuttongroup">
																			<input type="image" onclick="return changePassword();" style="border: 0px;"
																				SRC="media/images/change_password/NAVsave.gif">
																				<input type="image"
																				onclick="javascript:cancel();"
																				style="border: 0px"
																				SRC="media/images/change_password/NAVcancel.gif">
																		</div>
																	</td>
																</tr>
																<tr>
																	<td id="cpbottomcancel"></td>
																	<td id="cpbottomcancel"></td>
																	<td id="cpbottomcancel"></td>
																	<td
																		id="cppasswordpolicy">
																		<b><i>Password Policy:</i> </b>
																		<ul style="padding-left: 15px;">
																			<li id="cppasswordpolicyli">At least eight
																				characters long.</li>
																			<li id="cppasswordpolicyli">At least one
																				uppercase letter.</li>
																			<li id="cppasswordpolicyli">At least one
																				lowercase letter.</li>
																			<li id="cppasswordpolicyli">At least one
																				number.</li>
																			<li id="cppasswordpolicyli">Cannot repeat current password.</li>
																		</ul></td>
																</tr>

															</table>
														</div>

													</div>
												</fieldset>
											</div>
										</form>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>