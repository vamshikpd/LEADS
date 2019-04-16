<%
	int lastRowColor =0;
	boolean bNoresultsFound = false;
	// Format results
	PermissionInfo permissionInfoStep3 = null;
	permissionInfoStep3 = (PermissionInfo)session.getAttribute("permissionInfo");
	NumberFormat nf = NumberFormat.getInstance();
	long numTotalRecs = nav.getTotalNumERecs();
	//logger.debug("numTotalRecs:" + numTotalRecs);
	String numTotalRecords = nf.format(numTotalRecs);
	if(usq.getNavRollupKey() != null)
		numTotalRecords = nf.format(nav.getTotalNumAggrERecs());
	ERecSortKeyList srtKeys = nav.getSortKeys();
	//String address = null;
	String searchResults = null;
	searchResults = request.getParameter("searchResults");
	if(searchResults == null){
		searchResults ="paroleeSearch";
	}
	String searchResultsTab = null;
	searchResultsTab = request.getParameter("tab");
	if(searchResultsTab == null){
		searchResultsTab ="0";
	}
%>
	<script type="text/javascript" src="media/js/HorizontalSlide.js"></script>
	<script type="text/javascript">
		function makeSlide(objName){
			new HorizontalSlide(objName);
		}
	</script>
	<%@ include file="step3_header.jsp" %>
	<div id="refinments">
		<%@ include file="step3_bread.jsp" %>
<%
	// Pagination code
	ERecList recs = nav.getERecs();
	int inc = Integer.parseInt(UI_Props.getInstance().getValue(UI_Props.NUM_ITEMS));
	int maxPages = 10;
	int numLocalRecs = recs.size();
	long startRec = nav.getERecsOffset() + 1;
	if(recs.size() >0){
%>
		<%@ include file="pagination.jsp" %>
<%
		String paroleeResultsListId="";
		if((searchResults.equals("paroleeSearch") || searchResults.equals("advancedSearch")) && searchResultsTab.equals("0")){
			paroleeResultsListId = "paroleeresultslist1";
		} else if((searchResults.equals("paroleeSearch") || searchResults.equals("advancedSearch")) && searchResultsTab.equals("1")){
			paroleeResultsListId = "paroleeresultslist1withphoto";
		} else if(searchResults.equals("pc290Registrant") && searchResultsTab.equals("0")){
			paroleeResultsListId = "paroleeresultslist1pc290Registrant";
		} else if(searchResults.equals("pc290Registrant") && searchResultsTab.equals("1")){
			paroleeResultsListId = "paroleeresultslist1pc290Registrantphoto";
		} else if(searchResults.equals("pc290Discharge") && searchResultsTab.equals("0")){
			paroleeResultsListId = "paroleeresultslist1pc290Discharge";
		} else if(searchResults.equals("pc290Discharge") && searchResultsTab.equals("1")){
			paroleeResultsListId = "paroleeresultslist1pc290Dischargephoto";
		}
%>
		<div id="<%=paroleeResultsListId%>" class="show">
			<table BORDER="0" cellpadding="0" cellspacing="0" width="99%">
				<%@ include file="step3_table_header.jsp" %>
				<%@ include file="step3_records.jsp" %>
			</table>
		</div>
<%
	} else {
		bNoresultsFound = true;
		String tab="0";
		if(request.getParameter("tab")!=null){
			tab = request.getParameter("tab");
		}
		String urlBack = "";
		urlBack = "plm_controller.jsp"+"?N=0&tab="+tab;
		if(request.getParameter("fromTools")!=null
				&& "y".equals(request.getParameter("fromTools"))){
%>
			<div class="noResult" align="center" height="750px">
				No Results Found<br /><p>Close the window to go back to Search Page</p>
			</div>
<%
		}else{
%>
		<div class="noResult" align="center" height="750px">
			No Results Found<br /><a href='<%=urlBack%>'><u>&laquo; Back to Search Page</u></a>
		</div>
<%
		}
	}
%>
		<table BORDER="0" width="99%" cellpadding="0" cellspacing="0" class="blanktable">
<%
	int iEmptyRowNumber =Integer.parseInt(UI_Props.getInstance().getValue("SEARCH_RESULTS_EMPTY_ROW_NUMBER"));
	int iExtraErecs = 0;
	if(bNoresultsFound){
		iExtraErecs =10;
		lastRowColor = 0;
	} else {
		if(numLocalRecs < iEmptyRowNumber)
			iExtraErecs = iEmptyRowNumber-numLocalRecs;
	}
	String sRow = "";
	int iColspan = 0;
	for(int e=lastRowColor+1;e<=iExtraErecs+lastRowColor;e++){
		if (e%2==0){
			sRow = "row1";
		} else{
			sRow = "";
		}
		if(searchResults.equals("paroleeSearch")||searchResults.equals("advancedSearch")){
			iColspan = 14;
		} else if(searchResults.equals("pc290Discharge")){
			iColspan = 10;
		} else if(searchResults.equals("pc290Registrant")){
			iColspan = 13;
		}
%>
			<tr class="<%=sRow%>">
				<td colspan="<%=iColspan%>" height="30" align="left" valign="middle">&nbsp;</td>
			</tr>
<%
	}
%>
		</table>
	</div>
	<script language="JavaScript">
		function openWindow(url){
			var myRef = window.open(url, 'mywin');
			myRef.focus();
		}
		function closeWindow(url){
			parent.location.href=url;
			window.close();
		}
	</script>