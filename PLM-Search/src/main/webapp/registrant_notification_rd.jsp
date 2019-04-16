<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.endeca.ui.constants.UI_Props"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.plm.util.PLMSearchUtil" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>PC290 Registrant Notification</title>
		<link rel="stylesheet" href="media/style/main.css" type="text/css"/>
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
		<script language="JavaScript" src="media/js/ajax_js.js"></script>
		<script language="JavaScript" src="media/js/plm_ohw.js"></script>
		<script language="JavaScript" src="media/js/notification.js"></script>
		<script language="JavaScript" src="media/js/citycounty.js"></script>
	</head>
	<body id="dischargeNotificationbody">
		<form name="registrantNotificationSearch">
			<div id="dischargeNotificationTop">
				<div id="helpdiv">
					<a href="JavaScript:plmHelpPopup('<%=request.getContextPath() %>/ohw_help.jsp?topic_id=registrant_notification');">
						<img src="media/images/global/help_icon.gif" alt="" />
					</a>
				</div>
				<div id="dischargeNotificationTopfirstrow">
				
				<!--  commented out by P.Knapp, new verbage being inserted per DAPO request. 09/15/11
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
							<li>are within 60 days of their 'Release Date'</li>
							<li>have a recent address change</li>
							<li>have been reclassified as a High Risk Sex Offender (HRSO)</li>
							<li>have had a change in classification to GPS caseload</li>
						</ul>
						Parolees subject to the criteria above will remain on this PC290 Registrant Notification search for 15 days.
					</div>					
				-->
				
				<div>
					<strong>
						Search by County or City for the following parolees<br/>
						Note:  Postrelease Community Supervision (PRCS)PC290 registrants are NOT included
					</strong>
				</div>
					<br clear="all" />
					<br clear="all" />
				<div>
					<strong>
					Your search results will be comprised of PC290 offenders under Parole Supervision who:
					</strong>
				</div>
					<br clear="all" />
					<div>
						<ul>
							<li>are within 60 days of their 'Release Date'</li>
							<li>have a recent address change</li>
							<!-- Commented out by Vamshi Kapidi on 12/04/2014
							<li>have been reclassified as a High Risk Sex Offender (HRSO)</li>
							<li>have had a change in classification resulting in placement on GPS Specialized Caseload</li> -->
						</ul>
						Parolees subject to the criteria above will remain on this PC290 Registrant Notification search for 15 days.
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
						<br clear="all"/>
						<br clear="all"/>
						<div class="city">
							<div>City</div>
							<div>
								<select name="City" id="City">
									<option name="City Name" value="">City Name</option>
								</select>
							</div>
						</div>
					</div>
					<br clear="all" />
					<div id="advSearchMiddle2" class="buttons">
						<div id="advSearchbottom2main" style="float:none; padding-left:145px">
							<div class="button">
								<div class="buttonleft"><img src="media/images/global/spacer.gif" alt="" /></div>
								<div class="buttonbg"><a href="#" onclick="registrantNotifySearch('registrantNotificationSearch','<%=userName%>','<%=ip_address%>')" id="submitbut">Search</a></div>
								<div class="buttonright"><img src="media/images/global/spacer.gif" alt="" /></div>
							</div>
							<div class="button">
								<div class="buttonleft"><img src="media/images/global/spacer.gif" alt="" /></div>
								<div class="buttonbg"><a href="#" onclick="parent.tb_remove()" >Cancel</a></div>
								<div class="buttonright"><img src="media/images/global/spacer.gif" alt="" /></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</form>
	</body>
</html>
