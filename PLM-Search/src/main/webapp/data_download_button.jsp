<%@ page import="com.plm.util.*" %>
<%@ page import="javax.servlet.ServletContext" %>
<div id="PC290">
	<div class="clearboth">
<%
	PermissionInfo permissionInfo = null;
	permissionInfo = (PermissionInfo) session.getAttribute("permissionInfo");
	/*String desc = request.getHeader("DESC");
	String sGroupCode = "";
	 if (desc!=null && desc.indexOf("=") > -1 && desc.indexOf(";") > 1) {
		sGroupCode = desc.substring(desc.indexOf("=") + 1, desc.indexOf(";"));
	} */
	String userType = (String) session.getAttribute("userType");
	if (userType == null) {
		userType = (String) request.getAttribute("userType");
	}
	if (permissionInfo != null && permissionInfo.canDownload()
			/*&& !sGroupCode.equals("")*/) {
%>
		<div class="orangearrow">
			<img src="media/images/global/orange_arrow_button.gif" alt="" />
			<a class="thickbox" title="Data" name="Download" href="data_download_rd.jsp#?keepThis=true&amp;TB_iframe=true&amp;width=600;height=300">Data Download</a>
		</div>
<%
	}
%>
	</div>
<%-- <%
	if (permissionInfo.isAdmin()
			|| permissionInfo.canViewAMS()) {
%>
	<div class="clearboth">
		<div class="orangearrow">
			<img src="media/images/global/orange_arrow_button.gif" alt="" />
			<a href="generate_report.jsp">Inactive Users Report</a>
		</div>
	</div>
<%
	}
%>
<%
	if (permissionInfo.isAdmin()
			&& permissionInfo.canViewAMS()) {
%>
    <div class="clearboth">
		<div class="orangearrow">
			<img src="media/images/global/orange_arrow_button.gif" alt="" />
			<a class="thickbox" title="Assign" name="LEADSPasswordAdmins" href="assign_ldapusers.jsp?keepThis=true&amp;TB_iframe=true&amp;width=500;height=450">Assign LEAPasswordAdmins</a>
		</div>
	</div>
<%
	}
%> --%>
</div>