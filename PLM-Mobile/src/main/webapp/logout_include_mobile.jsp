<%@ page import="com.plm.util.PLMSearchUtil" %>
<%-- <%
	String sUrlLogOut = PLMSearchUtil.getFormattedPLMURL(request, "/invalidate_session_mobile.jsp");
%> --%>
<script type="text/javascript">
function logoff() {
	<%--location.href='<%=sUrlLogOut%>';--%>
	location.href = "<%=request.getContextPath()%>/mobilelogout";
}
</script>
<div class="logout"><a href="#" onclick="javascript:logoff();">Logout</a></div>
