<% 
	String query = (String)session.getAttribute("searchQuery");
	query = query.replaceAll(" ","%20"); // for replacing space by %20
	query = query.replaceAll("\"","%22"); //for replacing double qoutes by %22
%>
<a href="plm_mobile_controller.jsp?<%=query%>" class="backtoresult">&laquo; Back to Search Results</a>
<div class="cdcnumheader">CDC#: <span><%=tempPropsMap.get("CDC Number")%></span></div>
<div class="nameheader floatright">Name: <span><%=tempPropsMap.get("First Name")%>&nbsp;<%=tempPropsMap.get("Last Name")%></span></div>