<ul id="stepsortNavButton">
<%
	UrlGen urlgTab = new UrlGen(queryString, "UTF-8");
	urlgTab.removeParam("tab");
	urlgTab.addParam("tab", "0");
	urlgTab.removeParam("refineTabID");
	urlgTab.addParam("refineTabID","" + refineTabIdInt);
	String urlTab0 = "plm_controller.jsp"+"?"+urlgTab;
	urlgTab.removeParam("tab");
	urlgTab.addParam("tab", "1");
	String urlTab1 = "plm_controller.jsp"+"?"+urlgTab;
	urlgTab.removeParam("tab");
	urlgTab.addParam("tab", "2");
	String urlTab2 = "plm_controller.jsp"+"?"+urlgTab;
	urlgTab.removeParam("tab");
	urlgTab.addParam("tab", "3");
	String urlTab3 = "plm_controller.jsp"+"?"+urlgTab;
	String keys="";
	String repTitle="";
	String filename="";
	DateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
	java.util.Date date = new java.util.Date();
	if(searchResults.equals("pc290Discharge")){
		keys = UI_Props.getInstance().getValue("DISCHARGE_SEARCH_RESULTS_FOR_EXPORT");
		repTitle = UI_Props.getInstance().getValue("PC290DIS_REP_TITLE");
		filename=request.getHeader("USERID")+"_"+dateFormat.format(date)+"_"+"PC290Discharge.pdf";
	} else if(searchResults.equals("pc290Registrant")){
		keys = UI_Props.getInstance().getValue("REGISTRANT_SEARCH_RESULTS_FOR_EXPORT");
		repTitle = UI_Props.getInstance().getValue("PC290REG_REP_TITLE");
		filename=request.getHeader("USERID")+"_"+dateFormat.format(date)+"_"+"PC290Registrant.pdf";
	} else if(searchResults.equals("paroleeSearch")||searchResults.equals("advancedSearch")){
		keys = UI_Props.getInstance().getValue("SEARCH_RESULTS_FOR_EXPORT");
		repTitle = UI_Props.getInstance().getValue("PAROLEE_SEARCH_REP_TITLE");
		filename=request.getHeader("USERID")+"_"+dateFormat.format(date)+"_"+"SearchResults.pdf";
	}
	String sListNavSelected="";
	String sListPhotoNavSelected="";
	String sMapNavSelected="";
	String sMapAllNavSelected="";
	if(!"y".equals(request.getParameter("fromTools"))
			&& !"y".equalsIgnoreCase(request.getParameter("singleParoleeMap"))){
		if(request.getParameter("tab")==null
				|| request.getParameter("tab").equals("0")){
			sListNavSelected="class=\"selected\"";
		} else if (("1").equals(request.getParameter("tab"))){
			sListPhotoNavSelected="class=\"selected\"";
		} else if (("2").equals(request.getParameter("tab"))){
			sMapNavSelected="class=\"selected\"";
		} else if (("3").equals(request.getParameter("tab"))){
			sMapAllNavSelected="class=\"selected\"";
		}
%>
	<li id="list_nav" <%=sListNavSelected%>><a href='<%=urlTab0%>'><img src="media/images/global/spacer.gif" alt="" /></a></li>
	<li id="listphoto_nav" <%=sListPhotoNavSelected%>><a href='<%=urlTab1%>'><img src="media/images/global/spacer.gif" alt="" /></a></li>
<%
		if(permissionInfoStep3!=null && permissionInfoStep3.canViewMaps()) {
%>
	<li id="map_nav" <%=sMapNavSelected%>><a href='<%=urlTab2%>'><img src="media/images/global/spacer.gif" alt="" /></a></li>
<%
			long iMaxTotalRecordsMappAll = Long.parseLong(UI_Props.getInstance().getValue(PLMConstants.DEFAULT_NUM_RESULTS_VIEW_ALL_PAROLEE));
			if(numTotalRecs>iMaxTotalRecordsMappAll){
				urlTab3 = "javascript:alert('Search result contains more than 500 records. Please refine your search further')";
			}
%>
	<li id="map_all_nav" <%=sMapAllNavSelected%>><a href="<%=urlTab3%>"><img src="media/images/global/spacer.gif" alt="" /></a></li>
<%
		}
	}
%>
</ul>
<script language="JavaScript">
	function printToPDF(numOfRecords){
		if(numOfRecords > 1000) {
			alert("Search result contains more than 1000 records. Please refine your search further");
		} else {
			if(numOfRecords <= 0){
				alert("Search result contains no records.");
			}else{
			    <%
			    	String qs = PLMSearchUtil.decodeGeoCodeCriteria(queryString);
			    	// 2018-03-10 emil replace | and " characters with ASCII codes
			    	qs = PLMSearchUtil.encodeSpecialCharsInSearchQuery(qs);
			    %>
				window.open('exportPDF?Sn=-1&Ef=<%=filename%>&Et=<%=repTitle%>&Ek=<%=keys%>&<%=qs%>&userid=<%=request.getHeader("USERID")%>&Page=Results&tab=<%=request.getParameter("tab")%>');
			}
		}
	}
	function printToJRPDF(numOfRecords){
		if(numOfRecords > 1000) {
			alert("Search result contains more than 1000 records. Please refine your search further");
		} else {
			if(numOfRecords <= 0){
				alert("Search result contains no records.");
			}else{
				window.open('exportJRPDF?Sn=-1&Ef=<%=filename%>&Et=<%=repTitle%>&Ek=<%=keys%>&<%=PLMSearchUtil.decodeGeoCodeCriteria(queryString)%>&userid=<%=request.getHeader("USERID")%>&Page=Results&tab=<%=request.getParameter("tab")%>');
			}
		}
	}
</script>
