<%
	if (numTotalRecs > inc) {
%>
		<div id="resultpagenumber" class="pagenumber">
<%
		// Compute the page set and the number of pages needed
		int activePage 	= (int)((startRec+inc-1)/inc);
		int startPage 	= (int)((((startRec/(inc*maxPages))) * maxPages) + 1);
		int endPage 	= (int)((numTotalRecs/inc) + 1);
		// Exception for even number sets
		if ((numTotalRecs%inc) == 0) {
			endPage--;
		}
		// Truncate number of pages if overridden with local constant
		// and determine if there is a next page set
		int finalPage = endPage;
		if (endPage > (startPage + maxPages - 1)) {
			endPage = startPage + maxPages - 1;
		}
		// Determine if there is a previous page set
		if (startPage > 1) {
			// Create previous page set request
			String off = Long.toString((startPage - maxPages - 1)*inc);
			UrlGen urlg = new UrlGen(queryString, "UTF-8");
			urlg.removeParam("No");
			urlg.addParam("No",off);
			urlg.removeParam("sid");
			urlg.removeParam("in_dym");
			urlg.removeParam("in_dim_search");
			urlg.removeParam("refineTabID");
			urlg.addParam("sid",(String)request.getAttribute("sid"));
			String url = "plm_controller.jsp"+"?"+urlg;
			url = url+"&refineTabID="+refineTabIdInt;
%>
			<a href='<%= url %>'><<</a>&nbsp;
<%
		}
		// Determine if there is a previous page
		if (activePage > 1) {
			// Create previous page request
			String off = Long.toString(startRec - inc - 1);
			UrlGen urlg = new UrlGen(queryString, "UTF-8");
			urlg.removeParam("No");
			urlg.addParam("No",off);
			urlg.removeParam("sid");
			urlg.removeParam("in_dym");
			urlg.removeParam("in_dim_search");
			urlg.removeParam("refineTabID");
			urlg.addParam("sid",(String)request.getAttribute("sid"));
			String url = "plm_controller.jsp"+"?"+urlg;
			url = url+"&refineTabID="+refineTabIdInt;
%>
			<a href='<%= url %>'>Prev</a>&nbsp;
<%
		}
		// Create direct page index
		for (int i = startPage; i <= endPage; i++) {
			if (i == activePage) {
%>
			<b style="font-size:18px"><%= i %></b>&nbsp;
<%
			}else {
				// Create direct page request
				String off = Long.toString((i-1)*inc);
				UrlGen urlg = new UrlGen(queryString, "UTF-8");
				urlg.removeParam("No");
				urlg.addParam("No",off);
				urlg.removeParam("sid");
				urlg.removeParam("in_dym");
				urlg.removeParam("in_dim_search");
				urlg.removeParam("refineTabID");
				urlg.addParam("sid",(String)request.getAttribute("sid"));
				String url = "plm_controller.jsp"+"?"+urlg;
				url = url+"&refineTabID="+refineTabIdInt;
%>
			<a href='<%= url %>'><%= i %></a>&nbsp;
<%
			}
		}
		// Determine if there is a next page
		if (finalPage > activePage){
			// Create next page request
			String off = Long.toString(startRec + inc - 1);
			UrlGen urlg = new UrlGen(queryString, "UTF-8");
			urlg.removeParam("No");
			urlg.addParam("No",off);
			urlg.removeParam("sid");
			urlg.removeParam("in_dym");
			urlg.removeParam("in_dim_search");
			urlg.removeParam("refineTabID");
			urlg.addParam("sid",(String)request.getAttribute("sid"));
			String url = "plm_controller.jsp"+"?"+urlg;
			url = url+"&refineTabID="+refineTabIdInt;
%>
			<a href='<%= url %>'>Next</a>&nbsp;
<%
		}
		// Determine if there is a next page set
		if (finalPage > (startPage + maxPages - 1)){
			// Create next page set
			String off = Long.toString(endPage*inc);
			UrlGen urlg = new UrlGen(queryString, "UTF-8");
			urlg.removeParam("No");
			urlg.addParam("No",off);
			urlg.removeParam("sid");
			urlg.removeParam("in_dym");
			urlg.removeParam("in_dim_search");
			urlg.removeParam("refineTabID");
			urlg.addParam("sid",(String)request.getAttribute("sid"));
			String url = "plm_controller.jsp"+"?"+urlg;
			url = url+"&refineTabID="+refineTabIdInt;
%>
			<a href='<%= url %>'>>></a>
<%
		}
%>
		</div>
<%
	}
%>