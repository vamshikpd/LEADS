<%
	// Get refinement dimension groups
	DimGroupList DimGroups = nav.getRefinementDimGroups();
	Iterator iterG = DimGroups.iterator();
	ArrayList groups = new ArrayList();
	while(iterG.hasNext()) {
		DimGroup dg = (DimGroup)iterG.next();
		if(!dg.getName().equals("")){
			groups.add(dg);
		}
	}
	request.removeAttribute("refineTabID");
%>
	<script langauge="javascript">
	function refineSearchResult(url) {
		window.location = url + '&refineTabID=<%=refineTabIdInt%>';
	}
	</script>
	<div id="refinebydiv">
		<div id="refinebydivtop">
			<div id="refinebydivtopleft"><img src="media/images/global/grey_blue_box_left.gif" alt="" /></div>
			<div id="refinebydivtopmiddle">
				<div id="step2textdiv">Refine By</div>
				<div id="refinebybuttondiv">
					<ul id="refinebybutton">
<%
	Iterator iterGr = groups.iterator();
	int count = 0;
	while(iterGr.hasNext()) {
		DimGroup dg = (DimGroup)iterGr.next();
		count++;
		if (count == 1){
%>
						<li <%if(refineTabIdInt == count){ %>class="selected" <%} %>><a href="#" onclick="paroleedata(this, '<%=count%>')"><%=dg.getName()%></a></li>
<%
		}else{
%>
						<li <%if(refineTabIdInt == count){ %>class="selected" <%} %>><a href="#" onclick="paroleedata(this, '<%=count%>')"><%=dg.getName()%></a></li>
<%
		}
	}
%>
					</ul>
				</div>
			</div>
		</div>
		<div id="searchbydiv">
<%
	Iterator iterGrp = groups.iterator();
	int cnt = 0;
	while(iterGrp.hasNext()) {
		DimGroup dg = (DimGroup)iterGrp.next();
		cnt++;
%>
			<div id="searchby<%=cnt%>" <%if(refineTabIdInt == cnt){%> class="show" <%}else{ %> class="hide" <%} %>>
				<div id="refinebydivsearchdiv">
					<div>
<%
			// Loop over dimensions in group
		for (int j=0; j<dg.size(); j++) {
			// Get dimension object
			Dimension dim0 = (Dimension)dg.get(j);
			DimVal root0 = dim0.getRoot();
			long rootId0 = root0.getId();
			// Get refinement list for dimension
			DimValList refs0 = dim0.getRefinements();
			// Create request to expose dimension values if refinements are
			// not displayed by default
			String sClass = "";
			if (((j+1)%4)==0){
				sClass="class=\"right\"";
			}
%>
						<select name='searchlist' id='searchlist' onChange="javascript:refineSearchResult(this.options[this.selectedIndex].value)" <%=sClass%>>
							<option value="" name=<%= dim0.getName() %>><%= dim0.getName() %></option>
<%
			// Display dimension (open row here, close later)
			for (int k=0; k < refs0.size(); k++) {
				// Get refinement dimension value
				DimVal ref0 = refs0.getDimValue(k);
				// Get properties for refinement value
				PropertyMap pmap0 = ref0.getProperties();
				// Get dynamic stats
				String dstats0 = "";
				if (pmap0.get("DGraph.Bins") != null) {
					dstats0 = " <font style='color:gray;font-size:.8em;'>("+pmap0.get("DGraph.Bins")+")</font>";
				}
				// Create request to select refinement value
				UrlGen urlg0 = new UrlGen(queryString, "UTF-8");
				// If refinement is navigable, change the Navigation parameter
				if (ref0.isNavigable()) {
					urlg0.removeParam("N");
					urlg0.addParam("N",
							(ENEQueryToolkit.selectRefinement(nav,ref0)).toString());
					urlg0.removeParam("Ne");
					urlg0.addParam("Ne",Long.toString(rootId0));
				}
				// If refinement is non-navigable, change only
				// the exposed dimension parameter
				// (Leave the Navigation parameter as is)
				else {
					urlg0.removeParam("Ne");
					urlg0.addParam("Ne",Long.toString(ref0.getId()));
				}
				if(ref0.getName().equals("More...")) {
					urlg0.removeParam("D1");
					urlg0.addParam("Dl", dim0.getName());
				}
				urlg0.removeParam("No");
				urlg0.removeParam("Nao");
				urlg0.removeParam("Nty");
				urlg0.removeParam("D");
				urlg0.removeParam("Dx");
				urlg0.removeParam("sid");
				urlg0.removeParam("in_dym");
				urlg0.removeParam("Dg");
				urlg0.removeParam("in_dim_search");
				urlg0.addParam("sid",(String)request.getAttribute("sid"));
				urlg0.addParam("Dg", dg.getName());
				urlg0.removeParam("refineTabID");
				String url0 = "plm_controller.jsp"+"?"+urlg0;
				// Display refinement
%>
							<option value='<%= url0 %>' name=<%= ref0.getName()%>><%= ref0.getName() %><%= dstats0 %></option>
<%
			}
			if (((j+1)%4)==0 && !(j==(dg.size()-1))){
%>
						</select>
					</div>
					<div>
<%
			}else if (j==(dg.size()-1)){
%>
						</select>
					</div>
<%
			}else{
%>
						</select>
<%
			}
		}
%>
				</div>
			</div>
<%
	}// end while
%>
		</div>
	</div>