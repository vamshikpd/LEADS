<%

	qr = (ENEQueryResults)request.getAttribute("eneQueryResults");
	//String searchQuery = request.getParameter("searchQuery");
	String searchQuery = (String)session.getAttribute("searchQuery");
	//logger.debug("searchQuery:" + searchQuery);
	if(session.getAttribute("searchQueryFromTools")!=null){
		String searchQueryFromTools = (String)session.getAttribute("searchQueryFromTools");
		if(searchQueryFromTools!=null && !searchQueryFromTools.equals("")){
			searchQuery = (String)session.getAttribute("searchQueryFromTools");
			//logger.debug("searchQueryFromTools:" + searchQuery);
		}
	}
	PropertyContainer dtabrec = qr.getERec();
	if(dtabrec == null){
		dtabrec = qr.getAggrERec();
	}
	String dtabspec = "";
	if(dtabrec instanceof ERec) {
		dtabspec = ((ERec)dtabrec).getSpec();
	}else {
		dtabspec = ((AggrERec)dtabrec).getSpec();
	}
	String showid = PLMDatabaseUtil.getPrimaryMugshotID(dtabspec);
	UrlGen urlgTab = new UrlGen(queryString, "UTF-8");
	StringBuilder nValue = new StringBuilder();
	Iterator dimIter = null;
	ArrayList dimArray = new ArrayList(20);
	
	if(!(request.getParameter("fromToolsListPage")!=null && "y".equals(request.getParameter("fromToolsListPage")))){
		// when user clicks on photo line up tab, we want to find similar with certain dimensions matching the suspect.  
		// set those as N values when we navigate to photo_lineup page so that the results are displayed accordingly
		// Build Map of all unique dimensions in the propertycontainer 
		// -
		Map dims = new TreeMap();
		if(qr.getAggrERec() != null) {
			Iterator aIter = qr.getAggrERec().getERecs().iterator();
			while(aIter.hasNext()) {
				for(Iterator x=((ERec)aIter.next()).getDimValues().iterator();
					x.hasNext(); ) {
					AssocDimLocations adl = (AssocDimLocations) x.next();
					for ( Iterator y = adl.iterator(); y.hasNext(); ) {
						DimLocation dimLoc = (DimLocation)y.next();
						dims.put(dimLoc.getDimValue().getDimensionName()+"|"+
						dimLoc.getDimValue().getName(), dimLoc);
					}
				}
			}
		}else {
			for(Iterator x=qr.getERec().getDimValues().iterator(); x.hasNext();) {
				AssocDimLocations adl = (AssocDimLocations) x.next();
				for ( Iterator y = adl.iterator(); y.hasNext(); ) {
					DimLocation dimLoc = (DimLocation)y.next();
					dims.put(dimLoc.getDimValue().getDimensionName()+"|"+dimLoc.getDimValue().getName(), dimLoc);
				}
			}
		}
		dimIter = dims.entrySet().iterator();
		while(dimIter.hasNext()) {
			Map.Entry pair = (Map.Entry)dimIter.next();
			DimLocation dimLoc = (DimLocation)pair.getValue();
			DimVal dimVal = dimLoc.getDimValue();
			String dimName = dimVal.getDimensionName();
			// Skip dimensions other than set of dimensions that are applicable for find similar, specified in requirements document
			// This can be configured in may be report.xml...
			if ("Birth Year".equals(dimName) || "Ethnicity".equals(dimName) || "Gender".equals(dimName) || "Hair Color".equals(dimName) || "Eye Color".equals(dimName)){
				dimArray.add(pair);
			}
		}
		dimArray.trimToSize();
		dimIter = dimArray.iterator();
		String bYear = "";
		while(dimIter.hasNext()) {
			Map.Entry pair = (Map.Entry)dimIter.next();
			DimLocation dimLoc = (DimLocation)pair.getValue();
			DimVal dimVal = dimLoc.getDimValue();
			String dimName = dimVal.getDimensionName();
			if ("Birth Year".equals(dimName) || "Ethnicity".equals(dimName) || "Gender".equals(dimName) || "Hair Color".equals(dimName) || "Eye Color".equals(dimName)){
				// get N value and append it in URL N value
				if("Birth Year".equals(dimName)) {
                    int val = Integer.parseInt(dimVal.getName());
//						bYear = "Birth_Yr|BTWN+" + (val-7) + "+" + (val+7);
                    bYear = "Birth_Yr%7CBTWN+" + (val-7) + "+" + (val+7); // Emil, 2018-02-02: changed | pipe to %7C html char
                } /*else if ("Height (+/- 2 inches)".equals(dimName)) {
                    if(heightSelected != 1) {
                        nValue.append(" ").append(dimVal.getId());
                        heightSelected = 1;
                    }
                } else if ("Weight (+/- 10 lb)".equals(dimName)) {
                    if(weightSelected != 1) {
                        nValue.append(" ").append(dimVal.getId());
                        weightSelected = 1;
                    }
                }*/ else {
                    nValue.append(" ").append(dimVal.getId());
                }
			}
		}
		urlgTab.removeParam("N");
		urlgTab.addParam("N", nValue.toString());
		urlgTab.removeParam("showid");
		urlgTab.addParam("showid",""+showid);
		urlgTab.removeParam("ptab");
		urlgTab.addParam("ptab", "0");
		urlgTab.removeParam("Nf");
		String urlTab0 = "plm_controller.jsp"+"?"+urlgTab;
		urlgTab.removeParam("ptab");
		urlgTab.addParam("ptab", "1");
		String urlTab1 = "plm_controller.jsp"+"?"+urlgTab;
	
		// removing parameters related to advance search or search term from the query so that if 
		// we click on the photo lineup proper results will be seen
		urlgTab.removeParam("ptab");
		urlgTab.addParam("ptab", "2");
		UrlGen urlgTab2 = new UrlGen(urlgTab.toString(), "UTF-8");
		// removing parameters related to advance,radial,pc290 or search term from the query so that 
		// if we click on the photo lineup proper results will be seen
		urlgTab2.removeParam("Nrs");
		if(request.getParameter("Nf") != null &&(request.getParameter("Nf").contains("Parole Date Search") || request.getParameter("Nf").contains("GCLT"))){
			urlgTab2.removeParam("Nf");
		}
		if(request.getParameter("Ntt") != null){
			urlgTab2.removeParam("Ntt");
			urlgTab2.removeParam("Ntx");
			urlgTab2.removeParam("Ntk");
			urlgTab2.removeParam("Nty");
		}
		String urlTab2 = "plm_controller.jsp"+"?"+urlgTab2+"&Nf="+bYear;
%>
	<ul id="paroleedetailstopNavButton">
<%
		if(request.getParameter("ptab")==null || request.getParameter("ptab").equals("0")) {
%>
		<li id="parolee_info_nav" class="selected"><a href='<%=urlTab0%>'><img src="media/images/global/spacer.gif" alt="" /></a></li>
		<li id="photo_gallery_nav"><a href='<%=urlTab1%>'><img src="media/images/global/spacer.gif" alt="" /></a></li>
		<li id="photo_lineup_nav"><a href='<%=urlTab2%>'><img src="media/images/global/spacer.gif" alt="" /></a></li>
<%
		}else if (("1").equals(request.getParameter("ptab"))){
%>
		<li id="parolee_info_nav"><a href='<%=urlTab0%>'><img src="media/images/global/spacer.gif" alt="" /></a></li>
		<li id="photo_gallery_nav" class="selected"><a href='<%=urlTab1%>'><img src="media/images/global/spacer.gif" alt="" /></a></li>
		<li id="photo_lineup_nav"><a href='<%=urlTab2%>'><img src="media/images/global/spacer.gif" alt="" /></a></li>
<%
		}else if (("2").equals(request.getParameter("ptab"))){
%>
		<li id="parolee_info_nav"><a href='<%=urlTab0%>'><img src="media/images/global/spacer.gif" alt="" /></a></li>
		<li id="photo_gallery_nav"><a href='<%=urlTab1%>'><img src="media/images/global/spacer.gif" alt="" /></a></li>
		<li id="photo_lineup_nav" class="selected"><a href='<%=urlTab2%>'><img src="media/images/global/spacer.gif" alt="" /></a></li>
<%
		}
	}
%>
	</ul>
	<div class="floatr">
<%
	String sTab = request.getParameter("tab") != null? request.getParameter("tab") : "0";
	urlgTab.removeParam("ptab");
	urlgTab.removeParam("R");
	urlgTab.removeParam("tab");
	urlgTab.addParam("tab", sTab);
	urlgTab.removeParam("back");
	urlgTab.removeParam("N");
	urlgTab.removeParam("showid");
	urlgTab.addParam("N", request.getParameter("backN")!= null?request.getParameter("backN"):UI_Props.getInstance().getValue(UI_Props.ENE_ROOT));
	String sQueryString=searchQuery;
	String urlBack = "plm_controller.jsp"+"?"+sQueryString;
	if(request.getParameter("zoomLevel")!=null
			&& !request.getParameter("zoomLevel").equals("")){
		urlBack = urlBack + "&zoomLevel=" + request.getParameter("zoomLevel");
	}
	if(request.getParameter("centerPoint")!=null
			&& !request.getParameter("centerPoint").equals("")){
		urlBack = urlBack + "&centerPoint=" + request.getParameter("centerPoint");
	}
%>
		<a style="font-size:15px" href='<%=urlBack%>'>&laquo; Back to Results Page</a>
	</div>