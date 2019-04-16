<%@ page errorPage="error.jsp" %>
<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.util.*,java.io.*,java.sql.*"%>
<%@ page import="com.endeca.ui.constants.UI_Props"%>
<%@ page import="com.util.MapsList" %>
<%@ page import="com.plm.util.PLMSearchUtil" %>
<%@ page import="com.plm.constants.PLMConstants" %>
<%@ page import="com.plm.util.database.PLMDatabaseUtil" %>
<%
	String url = request.getParameter("url").toString();
	String nameString = PLMSearchUtil.convertNValueToName(url);
	UrlGen ug = new UrlGen(nameString, "UTF-8");
	nameString = ug.toString();
	String title = request.getParameter("title").toString();
	String userName= (String)session.getAttribute("userId");
	String insertString = PLMConstants.INSERT_SAVE_SEARCH_STRING;// CONSTANSTS
	ArrayList<String> values = new ArrayList<String>();
	values.add(userName);
	values.add(title);
	values.add(nameString);
	PLMDatabaseUtil.insert(insertString,values);
	UrlGen redirectUrl = new UrlGen(request.getParameter("url"), "UTF-8");
	String sRedirectUrl = UI_Props.getInstance().getValue(UI_Props.CONTROLLER)+"?"+redirectUrl.toString();
%>
<script>
	parent.window.location.href ='<%= sRedirectUrl%>'
</script>