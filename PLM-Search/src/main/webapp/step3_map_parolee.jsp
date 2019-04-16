<%
	// Format results
	PermissionInfo permissionInfoStep3 = null;
	permissionInfoStep3 = (PermissionInfo)session.getAttribute("permissionInfo");
	NumberFormat nf = NumberFormat.getInstance();
	String numTotalRecords = "1";
	long numTotalRecs = 1l;
	String searchResults = null;
	searchResults = request.getParameter("searchResults");
	if(searchResults == null){
		searchResults ="paroleeSearch";
	}
%>
	<%@ include file="step3_header.jsp" %>
	<div id="refinments">
		<%@ include file="step3_bread.jsp" %>
		<div style="color:#E17236;font-weight: bold;" align="right"> Show Schools:
			<input name="plot_schools" id="plot_schools"  type="checkbox"  onClick="javascript:showSchools(this.checked)" class="checkbox" value="" />
		</div>
<%
			// Pagination code
			// Set constants
	ERecList recs = qr.getERecs();
	//PropertyContainer rec = qr.getERec();
	int inc = Integer.parseInt(UI_Props.getInstance().getValue(UI_Props.NUM_ITEMS));
	int maxPages = 10; //TD modified the no of Pages Link from 100 to 10
	int numLocalRecs = recs.size();
	long startRec = 1l;
	if(recs.size() >0){
%>
		<%@ include file="pagination.jsp" %>
		<div id="mapimage"></div>
		<%@ include file="step3_map_records.jsp" %>
<%
	} else{
		String tab="0";
		if(request.getParameter("tab")!=null){
			tab= request.getParameter("tab");
		}
		String urlBack = "plm_controller.jsp"+"?N=0&tab="+tab;
%>
		<div class="noResult" align=" center">No Results Found<br /><a href="<%=urlBack%>"><u>&laquo; Back to Search Page</u></a></div>
<%
	}
%>
	</div>
</div>