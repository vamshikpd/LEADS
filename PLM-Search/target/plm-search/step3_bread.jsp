<%
	if(!"y".equalsIgnoreCase(request.getParameter("singleParoleeMap"))){
%>
		<div id="refinmentsbuttondiv">
<%
		//reading the crumbs here to display the "Search Criteria" text in case only refinements are used to narrow the search results
		MapsList crumbs = BreadcrumbHandler.getBreadcrumbs(nav, queryString);
		Iterator iter = crumbs.iterator();
		// The search criteria will be shown if the parameter searchResults is not null ....
		if(searchResults != null && (
					(searchResults.equals("advancedSearch"))||
					(searchResults.equals("pc290Discharge"))||
					(searchResults.equals("pc290Registrant")))
		){
			if(searchResults.equals("advancedSearch")){
%>
			<ul id="refinmentsbutton">
				<li class="selected">Quick Search:</li>
<%
			} else if(searchResults.equals("pc290Discharge")){
%>
			<ul id="refinmentsbutton">
				<li class="selected">PC290 Discharge Search:</li>
<%
			} else if(searchResults.equals("pc290Registrant")){
%>
			<ul id="refinmentsbutton">
				<li class="selected">PC290 Registrant Search:</li>
<%
			}
			//Breadcrumbs start
			boolean bShowCriteria = false;
			//if(queryString.indexOf("matches(.,")>=0){
				if(searchResults.equals("advancedSearch")){
					bShowCriteria = true;
				} else {
					bShowCriteria = false;
				}
				int recPos = -1;
				recPos=queryString.indexOf("record[");
				if (recPos != -1) {
					String sRecord = queryString.substring((queryString.indexOf("record[")+7),queryString.lastIndexOf("]"));
					List<String> lstQuery = PLMSearchUtil.getQueryParameters(sRecord);
					for(String s: lstQuery){
%>
					<li><%=s.replace("_"," ")%></li>
<%
					}
				} else {
					bShowCriteria = false;
				}
			//} 
			if(bShowCriteria
					&& !"y".equals(request.getParameter("fromTools"))
					){
%>
				<li style="background-attachment:scroll; background-color:#D26931; background-image:none; background-position:0 0; background-repeat:repeat;"><a href="<%= "plm_controller.jsp"+"?N=0"%>"><img src="media/images/global/close_icon.gif" alt="" /></a></li>
<%
			}
		} else if (iter.hasNext()) {
%>
			<ul id="refinmentsbutton">
				<li class="selected">Search Criteria:</li>
<%
		}
		// breadcrumb
		iter = crumbs.iterator();
		while(iter.hasNext()) {
			Map posMap = (Map)iter.next();
			// Display breadcrumb and any spelling corrections and suggestions (DYM)
			String sMiles = "";
			sMiles = request.getParameter("distance");
			String sToShow ="";
			if(posMap.get(BreadcrumbHandler.FILTER).toString().indexOf("km")>-1){
				int iLtIndex = posMap.get(BreadcrumbHandler.FILTER).toString().indexOf("<");
				sToShow = posMap.get(BreadcrumbHandler.FILTER).toString().substring(0,iLtIndex)+"< "+sMiles;
			}else{
				sToShow = posMap.get(BreadcrumbHandler.FILTER).toString();
			}
			if((searchResults.equals("pc290Discharge")
					|| searchResults.equals("pc290Registrant"))
					&& !"y".equals(request.getParameter("fromTools"))) {
%>
				<li><%=posMap.get(BreadcrumbHandler.LABEL)%>:<%=sToShow%><a style="background-attachment:scroll; background-color:#D26931; background-image:none; background-position:0 0; background-repeat:repeat;" href="<%="plm_controller.jsp"+"?N=0"%>"><img src="media/images/global/close_icon.gif" alt="" /></a></li>
<%
			} else {
				if(!"y".equals(request.getParameter("fromTools"))){
					//logger.debug("in here : " + posMap.get(BreadcrumbHandler.REMOVE_QUERY));
%>
				<li><%=posMap.get(BreadcrumbHandler.LABEL)%>:<%=sToShow%><a style="background-attachment:scroll; background-color:#D26931; background-image:none; background-position:0 0; background-repeat:repeat;" href="<%="plm_controller.jsp"+"?"+posMap.get(BreadcrumbHandler.REMOVE_QUERY)%>"><img src="media/images/global/close_icon.gif" alt="" /></a></li>
<%
				}
			}
		}
		Iterator iterNew = crumbs.iterator();
		if(iterNew.hasNext()){
			String tabNew = request.getParameter("tab");
			if(tabNew == null ||tabNew.equals("") ){
				tabNew ="0";
			}
			if(!"y".equals(request.getParameter("fromTools"))){
%>
				<li style="background-attachment:scroll; background-color:#D26931; background-image:none; background-position:0 0; background-repeat:repeat;"><a href='<%= "plm_controller.jsp"+"?"+"N=0&tab="+tabNew%>'><img src="media/images/global/close_icon.gif" alt="" /></a></li>
<%
			}
		}
%>
			</ul>
		</div>
<%
	}
%>