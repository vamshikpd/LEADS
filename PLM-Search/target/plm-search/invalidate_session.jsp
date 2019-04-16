<%@ page import="org.apache.log4j.Logger" %>
<%-- <%@ page import="com.endeca.ui.constants.*" %>
<%@page import="com.plm.constants.PLMConstants"%>
<%@ page import="com.plm.oam.utils.OLATUtil" %>  
<%@ page import="com.plm.oam.apps.LDAPModifyUserAttribute" %>
 --%>
<%
	final Logger logger = Logger.getLogger(this.getClass());
/* 	
	String isProsLeadsAccountAdmin = request.getHeader("PROSLEADSADM");
	String adminTestName = UI_Props.getInstance().getValue("ADMIN_TEST_NAME");	
	String userId = (String) session.getAttribute("userId");
	if (userId == null) {
		userId = (String) request.getAttribute("userName");
	}
	
	logger.debug("isProsLeadsAccountAdmin :: " + isProsLeadsAccountAdmin);
	logger.debug("adminTestName :: " + adminTestName);
	logger.debug("userId :: " + userId);
	if(isProsLeadsAccountAdmin != null && isProsLeadsAccountAdmin.equals("true")){
		 int userPassedTest = OLATUtil.getTestResultsForUser(userId, adminTestName);
		 if(userPassedTest == 1){
			LDAPModifyUserAttribute.moveUser(userId, PLMConstants.USER_GROUP, PLMConstants.NEW_GROUP);
			LDAPModifyUserAttribute.updateAttribute(userId,PLMConstants.USER_IS_A_PROSPECTIVE_LEADS_ACCOUNT_ADMIN, PLMConstants.USER_MODIFY_VALUE);
		 }
	} 

 */	
 

//	logger.info("logout--------------");
	String userId = (String) session.getAttribute("userId");

	if (userId == null) {
	    logger.warn("Invalidate Session: UserId is null...");
	}
	
//	logger.info("userId--------------"+userId);
	String redirectURL = request.getContextPath() + "/logout";
	if(userId == null){
		redirectURL = "plmredirect";
	} 
	String fromCP = (String) session.getAttribute("fromCP");
	String fromSOMS = (String) session.getAttribute("fromSOMS");
	if(fromCP == null){
		fromCP = "N";
	}
	if(fromSOMS == null){
		fromSOMS = "N";
	}

%>
<script type="text/javascript">
	var jFromCP = "<%=fromCP%>";
	var jFromSOMS = "<%=fromSOMS%>";
	if(jFromCP == "Y"){
		self.close();
	}else if(jFromSOMS == "Y"){
	    console.log("SOMS LEADS logoff...");
		window.open('','_self','');
		window.close();
	}else{
		top.location.href='<%=redirectURL%>';
	}
</script>

<% session.invalidate(); %>