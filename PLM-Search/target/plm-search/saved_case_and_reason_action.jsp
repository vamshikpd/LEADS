<%@ page errorPage="error.jsp" %>
<%@ page import="java.util.*,java.text.*"%>
<%@ page import="com.plm.constants.PLMConstants" %>
<%@ page import="com.plm.util.database.PLMDatabaseUtil" %>
<%@ page import="com.endeca.navigation.*" %>
<%@ page import="com.endeca.ui.constants.UI_Props"%>
<%@ page import="org.apache.log4j.Logger" %>
<%
final Logger logger = Logger.getLogger(this.getClass());
	String userName=(String)session.getAttribute("user");
	String ip_address= request.getServerName();
	String cdc_num = "temp";
	String case_no = request.getParameter("case").toString();
	session.setAttribute("case_no",case_no);
	String reason_no = request.getParameter("reason").toString();
	session.setAttribute("reason_no",reason_no);
	String url = null;
	String sNParam =request.getParameter("N");
	if(sNParam==null){
		sNParam = "&N="+UI_Props.getInstance().getValue(UI_Props.ENE_ROOT);
	}
	url = request.getParameter("url")+sNParam;
	// 11/23/09 - It is now understood that we are supposed to write to table only when user clicks on parolee details, hence following call is commented out
	//PLMDatabaseUtil.insert(insertString,values);
	//chnaged the Url to clear all results when case and reason is sumitted
	String newUrl = "N=0";
	UrlGen redirectUrl = new UrlGen(newUrl, "UTF-8");
	url = UI_Props.getInstance().getValue(UI_Props.CONTROLLER)+"?" + redirectUrl.toString();
%>
<script>
	if('<%= request.getParameter("page")%>'=='step1'){
		location.href ="<%= url %>";
	}
	else{
		parent.location.href ="<%= url %>";
	}
</script>