<%--
----------------------------------------------------------------------------------------------------------------+
 Defect #91   | Emil, 5/8/18: modified page to show user's displayName in header instead of firstName+lastName  |
----------------------------------------------------------------------------------------------------------------+
--%>

<%@ page errorPage="error.jsp" %>
<%@ page import="java.util.*"%>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.plm.util.database.*" %>
<%@ page import="com.plm.util.*" %>
<%@ page import="org.apache.log4j.Logger" %>
<%--
<% response.setHeader("Cache-Control","no-cache"); 
//HTTP 1.1 response.setHeader("Pragma","no-cache"); 
//HTTP 1.0 response.setDateHeader ("Expires", 0); 
//prevents caching at the proxy server 
%>
--%>

<%
final Logger logger = Logger.getLogger(this.getClass());
	String userType, userId, groupName;
	boolean shouldValidateSearchAccess = false;
	boolean shouldRestrictSearchAccess = false;
/* 	int userPassedTest=0;	
	boolean isUserCreatedMoreThan21Days=false;
	boolean isUserLogOutFromSearchLogin=false; */
// 	boolean shouldAllowAccesstoAMS = false;
	String machineIP = UI_Props.getInstance().getValue("MACHINE_IP");
	// String idcsAdminConsole = UI_Props.getInstance().getValue("idcshost")+ UI_Props.getInstance().getValue("adminconsoleuri");

	// 2018-03-25 emil read config params for CA IDM URLs from db rather than property file for ease of udpate
//	String CAIdentityPortal = UI_Props.getInstance().getValue("CA_PORTAL_URL");
//	String CAIdentityPortalPasswordChangeURL = UI_Props.getInstance().getValue("CA_PORTAL_PASSWORD_CHANGE_URL");
	String CAIdentityPortal = PLMSearchUtil.getConfigParam("ACCOUNT_MANAGEMENT_URL");
	String CAIdentityPortalPasswordChangeURL = PLMSearchUtil.getConfigParam("CHANGE_PASSWORD_URL");

	String welcomePageURL = request.getContextPath() + UI_Props.getInstance().getValue("WELCOME_PAGE_LANDING_URL");
	String loginURL = request.getContextPath() + UI_Props.getInstance().getValue("LOGINURL"); // FOR WHEN SESSION EXPIRED, IT REDIRECT TO LOGIN PAGE.
	shouldValidateSearchAccess = UI_Props.getInstance().getValue("SHOULD_VALIDATE_SEARCH_ACCESS").equalsIgnoreCase("true")
			|| UI_Props.getInstance().getValue("SHOULD_VALIDATE_SEARCH_ACCESS").equalsIgnoreCase("yes");

	// F5 session tracking related code
	boolean addF5SessionTimeoutMonitoring = UI_Props.getInstance().getValue("F5_SESSION_TIMEOUT_MONITORING").equalsIgnoreCase("true");
	String strFromSOMS = (String) session.getAttribute("fromSOMS");
	boolean fromSOMS = false;
	if ("Y".equalsIgnoreCase(strFromSOMS)) {
	    fromSOMS = true;
	}

	//  ******************** temp ************************
	shouldValidateSearchAccess = false;
	//  ******************** temp ************************

	userType = (String) session.getAttribute("userType");
	if (userType == null) {
		userType = (String) request.getAttribute("userType");
	}
	userId = (String) session.getAttribute("userId");
	if (userId == null) {
		userId = (String) request.getAttribute("userName");
	}
	groupName = (String) session.getAttribute("groupName");
	if (groupName == null) {
		groupName = (String) request.getAttribute("groupName");
	}
	String showTC = (String) session.getAttribute("showTC");
	if (showTC == null) {
		showTC = "Y";
		session.setAttribute("showTC", "N");
	}
	if(shouldValidateSearchAccess) {
		shouldRestrictSearchAccess = PLMDatabaseUtil.isSearchAccessAllowed(userId);
	}
	
/* 	 String userTestName=UI_Props.getInstance().getValue("TEST_NAME");
	 String adminTestName=UI_Props.getInstance().getValue("ADMIN_TEST_NAME");	 
	 String userCreatedDate=request.getHeader("CREATEDT");
	 String userCompleteCertification = request.getHeader("COMPCERT");
	 String isProsLeadsAccountAdmin = request.getHeader("PROSLEADSADM");
	 
	if(userCompleteCertification != null && userCompleteCertification.equals("true")){
		 userPassedTest = OLATUtil.getTestResultsForUser(userId, userTestName);
		 if(userPassedTest == 0){
			 shouldRestrictSearchAccess = true;
			 isUserCreatedMoreThan21Days = LDAPUtil.compareDates(userCreatedDate);		 
		 } else if(userPassedTest == 1){
			LDAPModifyUserAttribute.updateAttribute(userId,PLMConstants.USER_NEEDS_TO_COMPLETE_CERTIFICATION, PLMConstants.USER_MODIFY_VALUE);
		 }
		 if(isUserCreatedMoreThan21Days){
			 LDAPModifyUserAttribute.updateAttribute(userId,PLMConstants.OB_USER_ACCOUNT_CONTROL, PLMConstants.OB_USER_ACCOUNT_CONTROL_VALUE);
			 //logout from the application
			 isUserLogOutFromSearchLogin=true;			 		 
		 }
	 }else if(isProsLeadsAccountAdmin != null && isProsLeadsAccountAdmin.equals("true")){
		 userPassedTest = OLATUtil.getTestResultsForUser(userId, adminTestName);
		 if(userPassedTest == 1){
// 			shouldAllowAccesstoAMS = true;
				LDAPModifyUserAttribute.moveUser(userId, PLMConstants.USER_GROUP, PLMConstants.NEW_GROUP);
				LDAPModifyUserAttribute.updateAttribute(userId,PLMConstants.USER_IS_A_PROSPECTIVE_LEADS_ACCOUNT_ADMIN, PLMConstants.USER_MODIFY_VALUE);
			 	PermissionInfo permission = (PermissionInfo)session.getAttribute("permissionInfo");
			 	permission.setCanViewAMS(true);
			 	session.removeAttribute("permissionInfo");
			 	session.setAttribute("permissionInfo", permission);
		 }
	 }  */
	 
	boolean bCanQuery = ((PermissionInfo)session.getAttribute("permissionInfo")).canQuery();
	boolean bCanViewAMS = ((PermissionInfo)session.getAttribute("permissionInfo")).canViewAMS();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<title>Parole LEADS 2.0</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7, IE=EmulateIE9, IE=EDGE" />
		<script type="text/javascript" src="media/js/jquery-1.3.2.js"></script>
		<link href="media/style/thickbox.css" rel="stylesheet" type="text/css" />
		<link href="media/style/main_welcome.css" rel="stylesheet" type="text/css"/>

		<script type="text/javascript">
			<%-- 			var shouldAllowAccesstoAMS = <%=shouldAllowAccesstoAMS%>;
			if(shouldAllowAccesstoAMS){
				alert('Your previous session did not terminate properly. Please log back after some time');
				logoff();
			} --%>
			//Input the IDs of the IFRAMES you wish to dynamically resize to match its content height:
			//Separate each ID with a comma. Examples: ["myframe1", "myframe2"] or ["myframe"] or [] for none:
			var iframeids=["maincntnt"];
			//Should script hide iframe from browsers that don't support this script (non IE5+/NS6+ browsers. Recommended):
			var iframehide="no";
			var getFFVersion=navigator.userAgent.substring(navigator.userAgent.indexOf("Firefox")).split("/")[1];
			function resizeCaller() {
				var dyniframe = new Array();
				for (i=0; i<iframeids.length; i++){
					if (document.getElementById)
						resizeIframe(iframeids);
					//reveal iframe for lower end browsers? (see var above):
					if ((document.all || document.getElementById) && iframehide=="no"){
						var tempobj=document.all? document.all[iframeids] : document.getElementById(iframeids);
						tempobj.style.display="block";
					}
				}
			}
			function resizeIframe(frameid){
				var currentfr=document.getElementById(frameid);
				var tabid = document.getElementById("tabSelected").value;
				if (currentfr){
					currentfr.style.display="block";
					if(currentfr.contentDocument && currentfr.contentDocument.body && currentfr.contentDocument.body.offsetHeight && tabid=='welcomett_nav') { //ns6 syntax && LMS Tab
						currentfr.height = currentfr.contentDocument.body.offsetHeight+25;
						currentfr.height = 915;
						currentfr.scrolling='no';
					}
					else if (currentfr.contentDocument && currentfr.contentDocument.documentElement && currentfr.contentDocument.documentElement.offsetHeight) { ////ns6 syntax && Generic Tab
						currentfr.height = currentfr.contentDocument.documentElement.offsetHeight+25;
						if(tabid=='seach_nav') {
							currentfr.scrolling='no';
						}
						else if(tabid=='usermanagement_nav'){
							//alert("1");
							currentfr.scrolling='yes';
						}
						else if(tabid=='welcomett_nav') {
							currentfr.scrolling='no';
							currentfr.height = 915;
						}
					}
					else if (currentfr.Document && currentfr.Document.body && currentfr.Document.body.scrollHeight) //ie5+ syntax
					{
						currentfr.height = currentfr.Document.body.scrollHeight+25;
						if(tabid=='seach_nav') {
							currentfr.scrolling='no';
						}
						else if(tabid=='usermanagement_nav'){
							//alert("1.1");
							currentfr.scrolling='yes';
						}
						else if(tabid=='welcomett_nav') {
							currentfr.scrolling='no';
							currentfr.height = 915;
						}
					}
					else if (currentfr.Document && currentfr.Document.documentElement && currentfr.Document.documentElement.scrollHeight) //ie5+ syntax
					{
						currentfr.height = currentfr.Document.documentElement.scrollHeight+25;
						if(tabid=='seach_nav') {
							currentfr.scrolling='no';
						}
						else if(tabid=='usermanagement_nav'){
							//alert("1.2");
							currentfr.scrolling='yes';
						}
						else if(tabid=='welcomett_nav') {
							currentfr.scrolling='no';
							currentfr.height = 915;
						}
					}
					else if (document.body.clientWidth){
						currentfr.height = currentfr.contentDocument.documentElement.scrollHeight+25;
						currentfr.scrolling='no';
					}
					if (currentfr.addEventListener)
					{
						currentfr.addEventListener("load", readjustIframe, false);
					}
					else if (currentfr.attachEvent){
						currentfr.detachEvent("onload", readjustIframe); // Bug fix line
						currentfr.attachEvent("onload", readjustIframe);
					}
				}
			}
			function readjustIframe(loadevt) {
				//alert(("readjustIframe");
				var crossevt=(window.event)? event : loadevt;
				var iframeroot=(crossevt.currentTarget)? crossevt.currentTarget : crossevt.srcElement;
				if (iframeroot)
					resizeIframe(iframeroot.id);
			}
			function loadintoIframe(iframeid, url){
				//alert(("loadintoIframe");	
<%-- 				var isUserLogOutSearch = <%=isUserLogOutFromSearchLogin%>
				if(isUserLogOutSearch){
					logoff();
				} --%>
				if (document.getElementById)
					document.getElementById(iframeid).src=url;
			}
			if (window.addEventListener){
				window.addEventListener("load", resizeCaller, false);
			}else if (window.attachEvent){
				window.attachEvent("onload", resizeCaller);
			}else{
				window.onload=resizeCaller;
			}
			function loadSearch2(){
				//alert(("loadSearch2");
				var shouldRestrictSearch = <%=shouldRestrictSearchAccess%>;
				if(shouldRestrictSearch) {
					alert("Please complete the mandatory test before accessing LEADS 2.0 Search application.");
				} else {
					window.frames["maincntnt"].location = '<%=request.getContextPath()%>'+"/plm_controller.jsp?N=0";
				}
			}
			function changePassword(){
				// location.href = "<%=request.getContextPath()%>/cpredirect";
                window.open("<%= CAIdentityPortalPasswordChangeURL %>","_blank");
                <%--tb_show("Change Password","<%= CAIdentityPortalPasswordChangeURL %>","","");--%>
                <%--window.open("<%= CAIdentityPortalPasswordChangeURL %>", "caPortal", "location=no,width=1000,height=600,resizable=yes,status=no,toolbar=no,titlebar=no");--%>
            }
			function loadAMS(){
				window.open("<%=CAIdentityPortal%>","_blank");
			}
			function logoff() {
				// var logoffURL= "<%=request.getContextPath()%>/logout";
				// location.href=logoffURL;
                var logoffURL = "<%=request.getContextPath()%>/invalidate_session.jsp";
                location.href=logoffURL;
			}
		</script>
		<script language="JavaScript" src="media/js/plm_ohw.js"></script>
		<script type="text/javascript">
			function selectemenu(elm) {
				var thisli = elm.parentNode;
				var getli = document.getElementById('GlobalNavButton');
				var getulicount = getli.getElementsByTagName('li').length;
				for (a=0;a<getulicount;a++) {
					getli.getElementsByTagName('li')[a].className='';
				}
				thisli.className='selected';
				setTabSelected(thisli.id);
			}
			function loadtc() {
				//alert(("loadtc");
				showTC = '<%=showTC%>';
				if(showTC== 'Y'){
					window.showModalDialog("<%=request.getContextPath()%>/terms.jsp","","dialogWidth:500px; dialogHeight:500px; center:yes");
					showTC = 'N';
				}
			}
			function setTabSelected(val) {
				document.getElementById("tabSelected").value=val;
			}
		</script>
		<script type="text/javascript">
			function gotop() {
				//alert(("gotop");
				//scroll(0,0);
			}
		</script>

		<!-- // 2018-03-21 emil checking for F5 session timeouts -->
		<% if (addF5SessionTimeoutMonitoring && !fromSOMS) { %>
			<script type="text/javascript" src="media/js/session_check.js" ></script>
			<script type="text/javascript">
				function sessionTimedOut() {
                    logoff();
				}
			</script>
		<% } %>

	</head>
	<body>
		<input type='hidden' name="loginURL" id="loginURL" value="<%=loginURL%>" />
		<div align="center">
			<div id="Maindiv">
				<div id="Container" >
<!-- Header Part Start -->
					<div id="Header">
						<div id="logoimg">
							<a href="plmredirect"><img border="0" alt="CDCR" src="media/images/global/logo.gif"/></a>
						</div>
						<div id="resetlogout_text">
							<a href="#" onclick="javascript:changePassword();">Change Password</a>&nbsp;|&nbsp;<a href="#" onclick="javascript:logoff();">Logout</a>
						</div>
						<div id="topglobalnav">
							<div id="Top">
								<div>
									<div class="headerNav">
										<div id="paroleesearch_text"><img src="media/images/global/paroleesearch_text.gif" alt="" /></div>
										<%--
											Fix per defect #91: display user's displayName is application header
											<div id="welcomelogin_text">Welcome <%=session.getAttribute("firstName") + " " + session.getAttribute("lastName")%></div>
										--%>
										<div id="welcomelogin_text">Welcome <%=session.getAttribute("displayName") %></div>
									</div>
								</div>
							</div>
<!-- Start Global Nav Table -->
							<div id="GlobalNav">
								<div>
									<ul id="GlobalNavButton">
										<li id="welcomett_nav" class="selected">
											<a href="<%=welcomePageURL%>" onclick="javaScript:selectemenu(this);" target="maincntnt" >
												<img src="media/images/global/spacer.gif" alt="" />
											</a>
										</li>
<%
	if (bCanQuery) {
%>
										<li id="seach_nav">
											<a class="thickbox" href="#" onclick="javascript:loadSearch2(); selectemenu(this);" >
												<img src="media/images/global/spacer.gif" alt="" />
											</a>
										</li>
<%
	}
	if (bCanViewAMS) {
%>
										<li id="usermanagement_nav">
											<a class="thickbox" href="#" onclick="javascript:loadAMS(); selectemenu(this);" >
												<img src="media/images/global/spacer.gif" alt="" />
											</a>
										</li>
<%
	}
%>
										<li id="help_nav">
											<a href="javaScript:plmHelpPopup('<%=request.getContextPath()%>/ohw_help.jsp');" onclick="selectemenu(this);">
												<img src="media/images/global/spacer.gif" alt="" />
											</a>
										</li>
									</ul>
								</div>
							</div>
<!--End Global Nav Table -->
						</div>
					</div>
<!-- Header Part End -->
<!-- Middle Part Start -->
					<div id="Middle"  class="content_box" >
						<div  id="middletop">
							<!--  iframe onload="gotop();" src="" id="maincntnt" name="maincntnt" marginwidth="0" marginheight="0" frameborder="0" style="overflow:visible; width:100%; " scrolling="no" vspace="0" hspace="0" ></iframe -->
							<iframe onload="gotop();" src="" id="maincntnt" name="maincntnt" marginwidth="0" marginheight="0" frameborder="0" height="800" width="100%" scrolling="yes"></iframe>
						</div>
<!-- Middle Part End -->
					</div>
<!-- Footer Part Start -->
					<div id="Footer" >
					</div>
<!-- Footer Part End -->
				</div>
			</div>
		</div>
		<script type="text/javascript">
			loadintoIframe('maincntnt', '<%=welcomePageURL%>');
			loadtc();

		</script>
		<input type="hidden" name="tabSelected" id="tabSelected" value="welcomett_nav"/>

		<!-- // 2018-03-21 emil checking for F5 session timeouts -->
		<% if (addF5SessionTimeoutMonitoring && !fromSOMS) { %>
			<script type="text/javascript">
				window.setTimeout("sessionTimeoutCheck(sessionTimedOut)", globalTimoutInterval);
			</script>
		<% } %>

		<%--<script type="text/javascript" src="media/js/thickbox.js"></script>--%>
	</body>
</html>