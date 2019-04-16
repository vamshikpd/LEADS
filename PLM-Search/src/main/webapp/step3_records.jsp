<!-- step3_records.jsp
     08- 23-2011 Added code to not display the Status -- L. Baird 
     10-04-2011 Added code to mask fields for DAI Units -- L. Baird-->
<%
		// Search results
		// Check for search terms to do hit hightlighting on
		String[] searchTerms = null;
		
		if(request.getParameter("Ntt") != null) {
			String terms = "";
			Iterator recSrchIter = nav.getESearchReports().entrySet().iterator();
			while(recSrchIter.hasNext()) {
				ESearchReport searchReport = (ESearchReport)((Map.Entry)recSrchIter.next()).getValue();
				terms += " "+searchReport.getTerms();
				// Get the List of auto-correct values
				Iterator autoCorrectIter = searchReport.getAutoSuggestions().iterator();
				while(autoCorrectIter.hasNext()) {
					ESearchAutoSuggestion autoSug = (ESearchAutoSuggestion)autoCorrectIter.next();
					// Display autocorrect message
					terms += " "+autoSug.getTerms();
				}
			}
			terms = terms.replaceAll("(?i)\\s(and|or|not|near\\:)\\s", "\\s");
			searchTerms = terms.split("[^\\w\\d]+");
		}
		
		// Get record list iterator, first from bulk export if it was specified
		Iterator recIter;
		
		if(usq.getNavRollupKey() == null) {
			recIter = nav.getBulkERecIter();
			if(recIter == null)
				recIter = nav.getERecs().iterator();
		}else {
			recIter = nav.getBulkAggrERecIter();
			if(recIter == null)
				recIter = nav.getAggrERecs().iterator();
		}
		
		// Loop over record list
		String lastName = "";
		int i=0;
		PropertyMap propsMap = null;
		String imageLoc = "";
		
		UrlGen urlgTools = new UrlGen("", "UTF-8");
		urlgTools.addParam("N","0");
		urlgTools.addParam("tab","0");
		urlgTools.addParam("fromTools", "y");
		urlgTools.addParam("searchResults","advancedSearch");

		while(recIter.hasNext()) {
			i++;
			String spec = "";
			Object rec = recIter.next();
			if(rec instanceof ERec) {
				spec = ((ERec)rec).getSpec();
				propsMap = ((ERec)rec).getProperties();	//sk
			}else {
				spec = ((AggrERec)rec).getSpec();
				propsMap = ((ERec)rec).getProperties(); //sk
			}
			String sRow = "";
			//added for PRCS - LBB
			String sUnitNm = (String)propsMap.get("Unit Name");
			if (i%2==0){
				sRow = "row1";
			}
%>
		<tr class="<%=sRow%>">
<%
			UrlGen urlg = new UrlGen("", "UTF-8");

			urlg.addParam("R",spec);
			urlg.addParam("displayKey",(String)request.getParameter("displayKey"));
			urlg.addParam("sid",(String)request.getAttribute("sid"));

			// save the current N as backN for back to result page link from photo lineup page
			// Photo line up page filters find similar results using N value.  That N value need 
			// to be replaced by the N that was existing on results page as user may have refined 
			// the results and may have visited the parolee details page.
			String backN = request.getParameter("N");
			urlg.addParam("backN", backN);
			
			if(request.getParameter("fromTools")!=null && "y".equals(request.getParameter("fromTools"))){
				urlg.addParam("fromToolsListPage","y");
			}
			
			if(removeSearchQueryFromTools){
				urlg.addParam("removeSearchQueryFromTools", "y");
			}
			
			if(request.getParameter("Ntt") != null) {
				String terms = "";
				for(int t=0; t<searchTerms.length; t++) {
					terms += searchTerms[t]+" ";
				}
				urlg.addParam("hterms", terms.trim());
			}
			
			String url = "plm_controller.jsp"+"?"+urlg;
			
			urlgTools.removeParam("R");
			urlgTools.removeParam("Nrs");
			urlgTools.addParam("singleParoleeMap","y");
			urlgTools.addParam("R",spec);
			String urlTab4 = "plm_controller.jsp"+"?"+urlgTools+"&keepThis=true&amp;TB_iframe=true&amp;width=800;height=650";
			urlgTools.removeParam("singleParoleeMap");
			urlgTools.removeParam("R");
			
			String showid = null;
			
			if(("1").equals(request.getParameter("tab"))) {
				showid = PLMDatabaseUtil.getPrimaryMugshotID(spec);
			}
			
			if(searchResults.equals("paroleeSearch")||searchResults.equals("advancedSearch")){
				if(request.getParameter("fromTools")==null || !"y".equals(request.getParameter("fromTools"))){
					String sLastName = propsMap.get("Last Name")!=null?(String) propsMap.get("Last Name"):"&nbsp;";
					String sFirstName = propsMap.get("First Name")!=null?(String) propsMap.get("First Name"):"&nbsp;";
					String sBirthDispName = propsMap.get("Birth Date Display")!=null?(String) propsMap.get("Birth Date Display"):"&nbsp;";
					String sSex = propsMap.get("Sex")!=null?(String) propsMap.get("Sex"):"&nbsp;";
					String sRace = propsMap.get("Race")!=null?(String) propsMap.get("Race"):"&nbsp;";
					String sHeight = propsMap.get("Height Feet")!=null && propsMap.get("Height Inches")!=null?propsMap.get("Height Feet") + "'" + propsMap.get("Height Inches") + "&#8221":"&nbsp;";
					String sWeight = propsMap.get("P_Weight")!=null?(String)propsMap.get("P_Weight"):"&nbsp;";
					String sHairColor = propsMap.get("Haircolor")!=null?(String) propsMap.get("Haircolor"):"&nbsp;";
					String sEyeColor = propsMap.get("Eyecolor")!=null?(String) propsMap.get("Eyecolor"):"&nbsp;";
					String sUnit = propsMap.get("Unit Name")!=null?(String) propsMap.get("Unit Name"):"&nbsp;";
					/*  Added for PRCS and DAI-- LBB*/
					String sStatus=" ";

					// emil 2018-03-07 fix exception in substring call
					String sUnitSubstr = "";
					try {
					    sUnitSubstr = sUnit.substring(0,4);
					} catch (StringIndexOutOfBoundsException e) {}

					if (sUnitSubstr.equals("PRCS")
							|| sUnitSubstr.equals("DAI-")) {
						 sStatus = "&nbsp;";
					}else{
						 sStatus = propsMap.get("Status")!=null?(String) propsMap.get("Status"):"&nbsp;";
					}
					String sGeo = propsMap.get("geocode")!=null?"<img src=\"media/images/global/glob_icon.gif\" alt=\"\" border=\"0\" />":"&nbsp;";
					String sFullPhone = propsMap.get("Full_Phone")!=null?(String) propsMap.get("Full_Phone"):"&nbsp;";

					//urlgTools.addParam("Nrs","collection()/record[endeca:matches(.,\"Full_Phone\",\"" + sFullPhone + "\")]");
                    String nrsParam = "collection()/record[endeca:matches(.,\"Full_Phone\",\"" + sFullPhone + "\")]";
                    // emil 2018-03-07 Fix " character issue in Tomcat
//                    nrsParam = nrsParam.replaceAll("\"", "%22");
					urlgTools.addParam("Nrs", nrsParam);

					String urlToolsPhone = "plm_controller.jsp"+"?"+urlgTools+"&keepThis=true&amp;TB_iframe=true&amp;width=800;height=650";
					urlgTools.removeParam("Nrs");

					//urlgTools.addParam("Nrs","collection()/record[endeca:matches(.,\"geocode\",\"" + propsMap.get("geocode") + "\")]");
					nrsParam = "collection()/record[endeca:matches(.,\"geocode\",\"" + propsMap.get("geocode") + "\")]";
                    // emil 2018-03-07 Fix " character issue in Tomcat
//                    nrsParam = nrsParam.replaceAll("\"", "%22");

                    urlgTools.addParam("Nrs", nrsParam);

					String urlToolsAddress = "plm_controller.jsp"+"?"+urlgTools+"&keepThis=true&amp;TB_iframe=true&amp;width=800;height=650";
					String sTool = "<img src=\"media/images/global/tool_icon.gif\" alt=\"\" border=\"0\"  style=\"position:relative; z-index:2; background-color:#fff\" />";

					if(("1").equals(request.getParameter("tab"))) {
%>
			<td style="width:65px"><a href="<%=url%>"><img src="image.jsp?showid=<%=showid%>&psize=t" alt="" border="0" /></a></td>
<%
					}
%>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=spec%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sLastName%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sFirstName%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sBirthDispName%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sSex%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sRace%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sHeight %></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sWeight%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sHairColor%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sEyeColor%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sUnit%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sStatus%></a></td>
			<td height="30" align="left" valign="middle"><a href="<%=urlTab4%>" class="thickbox"><%=sGeo%></a></td>
			<td height="30" align="left" valign="middle" width="21">
				<ul id="sddm" style="display:block">
					<li><a href="javascript:void(0);" onclick="mopen('m<%=i%>')" onmouseout="mclosetime()" style="position:relative; z-index:1"><%=sTool %></a>
						<div id="m<%=i%>" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
							<span class="extrabrd">
								<a href="<%=urlToolsPhone%>" name="Contact Numbers" title="Find Same" class="thickbox">Find same Contact Number</a>
								<a href="<%=urlToolsAddress%>" name="Address" title="Find Same" class="thickbox">Find same Address</a>
							</span>
						</div>
					</li>
				</ul>
			</td>
<%
				}else{
					String sLastName = propsMap.get("Last Name")!=null?(String) propsMap.get("Last Name"):"&nbsp;";
					String sFirstName = propsMap.get("First Name")!=null?(String) propsMap.get("First Name"):"&nbsp;";
					String sBirthDispName = propsMap.get("Birth Date Display")!=null?(String) propsMap.get("Birth Date Display"):"&nbsp;";
					String sSex = propsMap.get("Sex")!=null?(String) propsMap.get("Sex"):"&nbsp;";
					String sRace = propsMap.get("Race")!=null?(String) propsMap.get("Race"):"&nbsp;";
					String sUnit = propsMap.get("Unit Name")!=null?(String) propsMap.get("Unit Name"):"&nbsp;";
					String sStatus = propsMap.get("Status")!=null?(String) propsMap.get("Status"):"&nbsp;";
%>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=spec%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sLastName%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sFirstName%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sBirthDispName%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sSex%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sRace%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sUnit%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sStatus%></a></td>
<%
				}
			} else if(searchResults.equals("pc290Discharge")){
				String sGeo = propsMap.get("geocode")!=null?"<img src=\"media/images/global/glob_icon.gif\" alt=\"\" border=\"0\" />":"&nbsp;";
				String sActDateDisp = propsMap.get("Action Date Display")!=null?(String) propsMap.get("Action Date Display"):"&nbsp;";
				String sFirstName = propsMap.get("First Name")!=null?(String) propsMap.get("First Name"):"&nbsp;";
				String sLastName = propsMap.get("Last Name")!=null?(String) propsMap.get("Last Name"):"&nbsp;";
				String sStatus = propsMap.get("Status")!=null?(String) propsMap.get("Status"):"&nbsp;";
				String sStreet = propsMap.get("Street")!=null?(String) propsMap.get("Street"):"&nbsp;";
				String sCity = propsMap.get("City")!=null?(String) propsMap.get("City"):"&nbsp;";
				String sZip = propsMap.get("Zip")!=null?(String) propsMap.get("Zip"):"&nbsp;";
				int iSequeceNo =0;
				if(request.getParameter("No")!=null){
					iSequeceNo = i+ Integer.parseInt(request.getParameter("No"));
				} else {
					iSequeceNo =i;
				}
				if(("1").equals(request.getParameter("tab"))) {
%>
			<td style="width:65px"><a href="<%=url%>"><img src="image.jsp?showid=<%=showid%>&psize=t" alt="" border="0" /></a></td>
<%
				}
%>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=iSequeceNo%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=spec%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sLastName%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sFirstName%></a></td>
		    <td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sActDateDisp%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sStreet%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sCity%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sZip%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sStatus%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=urlTab4%>" class="thickbox"><%=sGeo%></a></td>
<%
			} else if(searchResults.equals("pc290Registrant")){
				String sGeo = propsMap.get("geocode")!=null?"<img src=\"media/images/global/glob_icon.gif\" alt=\"\" border=\"0\" />":"&nbsp;";
				String sActDateDisp = propsMap.get("Action Date Display")!=null?(String) propsMap.get("Action Date Display"):"&nbsp;";
				String sAddrChgDate = propsMap.get("Address Changed Date Display")!=null?(String) propsMap.get("Address Changed Date Display"):"&nbsp;";
				String sLastName = propsMap.get("Last Name")!=null?(String) propsMap.get("Last Name"):"&nbsp;";
				String sFirstName = propsMap.get("First Name")!=null?(String) propsMap.get("First Name"):"&nbsp;";
				String sStatus = propsMap.get("Status")!=null?(String) propsMap.get("Status"):"&nbsp;";
				String sStreet = propsMap.get("Street")!=null?(String) propsMap.get("Street"):"&nbsp;";
				String sCity = propsMap.get("City")!=null?(String) propsMap.get("City"):"&nbsp;";
				String sZip = propsMap.get("Zip")!=null?(String) propsMap.get("Zip"):"&nbsp;";
				String sHrso = propsMap.get("HRSO")!=null?(String) propsMap.get("HRSO"):"&nbsp;";
				String sGps = propsMap.get("GPS")!=null?(String) propsMap.get("GPS"):"&nbsp;";
				int iSequeceNo =0;
				
				if(request.getParameter("No")!=null){
					iSequeceNo = i+ Integer.parseInt(request.getParameter("No"));
				} else {
					iSequeceNo =i;
				}
				if(("1").equals(request.getParameter("tab"))) {
%>
			<td style="width:65px"><a href="<%=url%>"><img src="image.jsp?showid=<%=showid%>&psize=t" alt="" border="0" /></a></td>
<%
		}
%>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=iSequeceNo%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=spec%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sLastName%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sFirstName%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sActDateDisp%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sAddrChgDate%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sHrso%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sGps%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sStreet%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sCity%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sZip%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=url%>"><%=sStatus%></a></td>
			<td height="30" align="left" valign="middle"><a style="color:#e17236;" href="<%=urlTab4%>" class="thickbox"><%=sGeo%></a></td>
<%
			}
%>
		</tr>
<%
		}
		lastRowColor = i;
%>