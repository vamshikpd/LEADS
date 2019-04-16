<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.endeca.ui.constants.UI_Props"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.plm.util.PLMSearchUtil" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>PC290 Discharge Notification</title>
		<script src="media/js/jquery-1.3.2.js" type="text/javascript"></script>
		<script type="text/javascript">
			jQuery(document).ready(function() {
				setTimeout(function(){
					$("form :select[name='County']:enabled:first").focus();
				},200);
				$("form").keypress(function (e) {
					 if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
						$("#submitbut").click();
						 return false;
						} else {
						 return true;
						}
				 });
			});
		</script>
		<link rel="stylesheet" href="media/style/main.css" type="text/css"/>
		<script language="javascript" type="text/javascript">
			var req = null;
			function openurl(link1){
				self.close();
				var ReferURL = parent.window.location.href;
				parent.window.location.href = link1;
				//window.opener.location.href = window.opener.location.href;
			}
		</script>
		<script language="JavaScript" src="media/js/ajax_js.js"></script>
		<script language="JavaScript" src="media/js/citycounty.js"></script>
		<script language="JavaScript" src="media/js/notification.js"></script>
		<script language="JavaScript" src="media/js/plm_ohw.js"></script>
	</head>
	<body id="dischargeNotificationbody">
		<form name="dischargeNotificationSearch">
			<div id="dischargeNotificationTop">
				<div id="helpdiv">
					<a href="JavaScript:plmHelpPopup('<%=request.getContextPath() %>/ohw_help.jsp?topic_id=discharge_notification');">
						<img src="media/images/global/help_icon.gif" alt="" />
					</a>
				</div>
				<div id="dischargeNotificationTopfirstrow">
					<div>
						<strong>Search by County or City for the following parolees</strong>
					</div>
					<br clear="all" />
					<br clear="all" />
					<div>
						<strong>Your search results will comprise of PC290 offenders who:</strong>
					</div>
					<br clear="all" />
					<div>
						<ul>
							<li>are within 60 days of their 'Discharge Date' or have recently discharged.</li>
						</ul>
						Parolees subject to the criteria above will remain on this PC290 Discharge Notification search for 15 days after discharge.
					</div>
					<div class="citycounty">
						<div class="county">
							<div>County</div>
							<div>
								<select name="County" onchange="javascript:getCities(this.value);">
									<option name="" value="">County Name</option>
<%
										ArrayList allCountys = PLMSearchUtil.getAllCountys();
										if(allCountys != null && allCountys.size()>=0){
											String name="";
											String value="";
											int idx = 0;
											for(int iter = 0;iter<allCountys.size();iter++){
												idx=allCountys.get(iter).toString().indexOf("|");
						  						name=allCountys.get(iter).toString().substring(0,idx);
						  						value=allCountys.get(iter).toString().substring(idx+1);
%>
												<option value="<%=value%>"><%=name%></option>
<%
											}
										}
%>
								</select>
<%
								//String userName= request.getHeader("USERID");
								//String ip_address= request.getHeader("Proxy-Client-IP");
								String userName= request.getHeader("USERID");
								if(userName==null || "".equals(userName)){
									userName = (String)session.getAttribute("userId");
									if(userName==null || "".equals(userName)){
										userName = "NULL_USERNAME";
									}
								}
								String ip_address= request.getHeader("X-FORWARDED-FOR");
								if(ip_address==null || "".equals(ip_address)){
									ip_address= (String)session.getAttribute("ipAddress");
									if(ip_address==null || "".equals(ip_address)){
										ip_address = "NULL_IPADDRESS";
									}
								}
%>
							</div>
						</div>
						<div class="andor"></div>
						<div class="city">
							<div>City</div>
							<div>
								<select name="City" id="City">
			 						<option name="City Name" value="">City Name</option>
								</select>
							</div>
						</div>
					</div>
					<div id="advSearchMiddle2" class="buttons">
						<div id="advSearchbottom2main" style="float:none; padding-left:145px">
							<div class="button">
								<div class="buttonleft"><img src="media/images/global/spacer.gif" alt="" /></div>
								<div class="buttonbg"><a href="#" onClick="dischargeNotifySearch('dischargeNotificationSearch','<%=userName%>','<%=ip_address%>')" id="submitbut">Search</a></div>
								<div class="buttonright"><img src="media/images/global/spacer.gif" alt="" /></div>
							</div>
							<div class="button">
								<div class="buttonleft"><img src="media/images/global/spacer.gif" alt="" /></div>
								<div class="buttonbg"><a href="#" onClick="parent.tb_remove()" >Cancel</a></div>
								<div class="buttonright"><img src="media/images/global/spacer.gif" alt="" /></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</form>
	</body>
</html>