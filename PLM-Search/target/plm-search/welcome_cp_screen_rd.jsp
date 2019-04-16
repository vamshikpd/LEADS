<%@ page errorPage="error.jsp" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.plm.util.database.*" %>
<%
	String userType, userId, groupName;
	boolean shouldValidateSearchAccess = false;
	boolean shouldRestrictSearchAccess = false;
	String machineIP = UI_Props.getInstance().getValue("MACHINE_IP");
	String welcomePageURL = request.getContextPath() + UI_Props.getInstance().getValue("WELCOME_PAGE_LANDING_URL");
	String loginURL = request.getContextPath() + UI_Props.getInstance().getValue("LOGINURL"); // FOR WHEN SESSION EXPIRED, IT REDIRECT TO LOGIN PAGE.
	shouldValidateSearchAccess = UI_Props.getInstance().getValue("SHOULD_VALIDATE_SEARCH_ACCESS").equalsIgnoreCase("true")
			|| UI_Props.getInstance().getValue("SHOULD_VALIDATE_SEARCH_ACCESS").equalsIgnoreCase("yes");
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
	//boolean bCanQuery = ((PermissionInfo)session.getAttribute("permissionInfo")).canQuery();
	//boolean bCanViewAMS = ((PermissionInfo)session.getAttribute("permissionInfo")).canViewAMS();
	String fromCP = (String) session.getAttribute("fromCP");
	if(fromCP == null){
		fromCP = "N";
	}
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
			//Input the IDs of the IFRAMES you wish to dynamically resize to match its content height:
			//Separate each ID with a comma. Examples: ["myframe1", "myframe2"] or ["myframe"] or [] for none:
			var iframeids=["maincntnt"];
			//Should script hide iframe from browsers that don't support this script (non IE5+/NS6+ browsers. Recommended):
			var iframehide="no";
			var getFFVersion=navigator.userAgent.substring(navigator.userAgent.indexOf("Firefox")).split("/")[1];
			function resizeCaller() {
				var dyniframe=new Array();
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
						//currentfr.scrolling='no';
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
				var crossevt=(window.event)? event : loadevt;
				var iframeroot=(crossevt.currentTarget)? crossevt.currentTarget : crossevt.srcElement;
				if (iframeroot)
					resizeIframe(iframeroot.id);
			}
			function loadintoIframe(iframeid, url){
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
				var shouldRestrictSearch = <%=shouldRestrictSearchAccess%>;
				if(shouldRestrictSearch) {
					alert("Please complete the mandatory test before accessing LEADS 2.0 Search application.");
				} else {
					window.frames["maincntnt"].location = '<%=request.getContextPath()%>'+"/plm_controller.jsp?N=0";
				}
			}
			/*function resetPassword() {
				var loginid = '<%=request.getHeader("USERID")%>';
				var redirectURL = location.href;
				var resetPasswdURL = "/identity/oblix/apps/lost_pwd_mgmt/bin/lost_pwd_mgmt.cgi?program=redirectforchangepwd&"+
					"login="+loginid+"&backURL="+redirectURL+"&target=top&style=style1";
				location.href=resetPasswdURL;
			}*/
			function logoff() {
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
				scroll(0,0);
			}
		</script>
	</head>
	<body>
		<input type='hidden' name="loginURL" id="loginURL" value="<%=loginURL%>" />
		<div align="center">
			<div id="Maindiv">
				<div id="Container" >
<!-- Header Part Start -->
					<div id="Header">
						<div id="logoimg">
							<a href="goToCPMainPage"><img border="0" alt="CDCR" src="media/images/global/logo.gif"/></a>
						</div>
						<div id="resetlogout_text">
							<a href="#" onclick="javascript:logoff();">Close Window</a>
						</div>
						<div id="topglobalnav">
							<div id="Top">
								<div>
									<div class="headerNav">
										<div id="paroleesearch_text"><img src="media/images/global/paroleesearch_text.gif" alt="" /></div>
										<div id="welcomelogin_text">Welcome <%=session.getAttribute("firstName") + " " + session.getAttribute("lastName")%></div>
									</div>
								</div>
							</div>
<!-- Start Global Nav Table -->
							<div id="GlobalNav">
								<div>
									<ul id="GlobalNavButton">
										<li id="welcomett_nav" class="selected"><a href="<%=welcomePageURL%>" onclick="javaScript:selectemenu(this);" target="maincntnt" ><img src="media/images/global/spacer.gif" alt="" /></a></li>
										<li id="seach_nav"><a class="thickbox" href="#" onclick="javascript:loadSearch2(); selectemenu(this);"><img src="media/images/global/spacer.gif" alt="" /></a></li>
										<li id="help_nav"><a href="javaScript:plmHelpPopup('<%=request.getContextPath()%>/ohw_help.jsp');" onclick="selectemenu(this);"><img src="media/images/global/spacer.gif" alt="" /></a></li>
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
							<iframe onload="gotop();" src="" height="800" width="100%" id="maincntnt" name="maincntnt" marginwidth="0" marginheight="0" frameborder="0" style="overflow:visible; width:100%; display:none" scrolling="no" vspace="0" hspace="0" ></iframe>
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
	</body>
</html>