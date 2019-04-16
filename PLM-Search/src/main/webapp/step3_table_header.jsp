		<tr bgcolor="#EAEAEB">
<%
		if(("1").equals(request.getParameter("tab"))) {
%>
			<th style="width:3%"><img src="media/images/global/spacer.gif" alt="" /></th>
<%
		}
		ERecSortKeyList sortList1  = nav.getSortKeys();
		//list for displaying properties for which sort is enabled
		ArrayList dispSortList= new ArrayList(20);
		//list for labels or  displaying properties for which sort is disabled
		ArrayList dispUnSortedList = new ArrayList(20);
		String sSearchResultKeys = null;
		String[] saSearchResultKeys = null;
		//Get all keys to be displayed on the search results page
		if(searchResults.equals("pc290Discharge")){
			sSearchResultKeys = UI_Props.getInstance().getValue("DISCHARGE_SEARCH_RESULTS");
		}else if(searchResults.equals("pc290Registrant")){
			sSearchResultKeys = UI_Props.getInstance().getValue("REGISTRANT_SEARCH_RESULTS");
		}else if(searchResults.equals("paroleeSearch")||searchResults.equals("advancedSearch")){
			if(request.getParameter("fromTools")!=null
					&& "y".equals(request.getParameter("fromTools"))){
				sSearchResultKeys = UI_Props.getInstance().getValue("SEARCH_RESULTS_TOOLS");
			}else{
				sSearchResultKeys = UI_Props.getInstance().getValue("SEARCH_RESULTS");
			}
		}
		if(sSearchResultKeys!= null){
			saSearchResultKeys  =sSearchResultKeys.split("\\|");
		}
		ArrayList alSearchResultKeys = null;
		if(saSearchResultKeys!=null && saSearchResultKeys.length >=0){
			alSearchResultKeys = new ArrayList(saSearchResultKeys.length);
			for(int i =0;i<saSearchResultKeys.length;i++){
				alSearchResultKeys.add(i,saSearchResultKeys[i]);
			}
		}
		// if key is sortable add it to dispSortList1 else add it to dispUnSortedList1
		if(alSearchResultKeys!=null && alSearchResultKeys.size() >=0){
			String dispsrtKey2 = "";
			for(int i = 0;i<alSearchResultKeys.size();i++){
				if(sortList1.getKey((String)alSearchResultKeys.get(i)) != null){
					ERecSortKey srtKey2 = (ERecSortKey)sortList1.getKey((String)alSearchResultKeys.get(i));
					// Create default sort navigation request
					UrlGen urlgRH = new UrlGen(queryString, "UTF-8");
					urlgRH.removeParam("Ns");
					urlgRH.removeParam("Nso");
					urlgRH.removeParam("No");
					urlgRH.removeParam("Nao");
					urlgRH.removeParam("sid");
					urlgRH.removeParam("R");
					urlgRH.addParam("sid",(String)request.getAttribute("sid"));
					String urlRH = "plm_controller.jsp"+"?"+urlgRH;
					String Ns = request.getParameter("Ns");
					boolean currentSortAsc = false;
					boolean currentSortDsc = false;
					if(Ns!=null){
						if (Ns.equalsIgnoreCase(srtKey2.getName()+"|0")){
							currentSortAsc = true;
							currentSortDsc = false;
						}else if (Ns.equalsIgnoreCase(srtKey2.getName()+"|1")){
							currentSortAsc = false;
							currentSortDsc = true;
						}else{
							currentSortAsc = false;
							currentSortDsc = false;
						}
					}else{
						currentSortAsc = false;
						currentSortDsc = false;
					}
					urlgRH.removeParam("Ns");
					urlgRH.addParam("Ns",srtKey2.getName()+"|0");
					String urlRHAsc = "plm_controller.jsp"+"?"+urlgRH;
					urlgRH.addParam("Ns",srtKey2.getName()+"|1");
					String urlRHDsc = "plm_controller.jsp"+"?"+urlgRH;
					// Height and Weight are dimensions as well as Properties.  We can use same name for both but for display we need it to be same
					// Properties are created as P_Height and P_Weight and we strip off the P_ for display purposes
					if (srtKey2.getName().indexOf("P_") > -1){
						// strip off
						dispsrtKey2 = srtKey2.getName().replace("P_", "");
					}else{
						dispsrtKey2 = srtKey2.getName();
					}
					//changing some of the property Names for display purpose
					//Showing CDC# instead of CDC Number... This is only done for display purpose... on all the search results pages
					if(dispsrtKey2.equals("CDC Number")){
						dispsrtKey2 ="CDC#";
					}
					if(dispsrtKey2.equals("Action Date Search")){
						 if(searchResults.equals("pc290Discharge")){
							dispsrtKey2 ="Discharge Dt";
						 }
						 if(searchResults.equals("pc290Registrant")){
							dispsrtKey2 ="Release Dt";
						 }
					}
					if(dispsrtKey2.equals("Sex")){
						dispsrtKey2 ="Gender";
					}
					if(dispsrtKey2.equals("Race")){
						dispsrtKey2 ="Ethnicity";
					}
					if(dispsrtKey2.equals("Birth Date Search")){
						dispsrtKey2 ="Birth Date";
					}
					if(dispsrtKey2.equals("Address Changed Date Search")){
						dispsrtKey2 ="Address Chg Dt";
					}
					// 10/24/09 - Keep it simple.  Provide one sort at a time, that is on one field, ascending or descending....not combination of
					// fields...perf hit as Endeca has to do lot of work on entire search result...hence commented out the code where we add to current Ns..
					// see above.  TBD: use reports.xml to display field name CDC # insetad of CDC Number
					String csskey = srtKey2.getName().toLowerCase();
					String newcsskey= csskey.replace(" ","_");
					//out.println(newcsskey);
%>
			<th align="left" valign="middle" class="<%=newcsskey%>">
				<table cellspacing="0" cellpadding="0" border="0" style="font-size:11px; text-decoration:none; font-weight:bold">
					<tr>
						<th>
<%
					if (currentSortAsc){
%>
							<%=(dispsrtKey2.indexOf("Number") > -1)?dispsrtKey2.replace("Number", "#"):dispsrtKey2%><br clear="all"/>
							<div class="paroleedetailsheaderdivarrowleft" align="left" ><a href='<%=urlRHAsc%>'><img src="media/images/parolee_details/arrow_up_on.gif" alt="Ascending" border="0" /></a></div>
<%
					}else{
%>
							<%=(dispsrtKey2.indexOf("Number") > -1)?dispsrtKey2.replace("Number", "#"):dispsrtKey2%><br clear="all"/>
							<div class="paroleedetailsheaderdivarrowleft" align="left"><a href='<%=urlRHAsc%>'><img src="media/images/parolee_details/arrow_up.gif" alt="Ascending" border="0" /></a></div>
<%
					}
					if (currentSortDsc){
%>
							<div class="paroleedetailsheaderdivarrowright" align="left"><a href='<%=urlRHDsc%>'><img src="media/images/parolee_details/arrow_down_on.gif" alt="Descending" border="0" /></a></div>
<%
					}else{
%>
							<div class="paroleedetailsheaderdivarrowright" align="left"><a href='<%=urlRHDsc%>'><img src="media/images/parolee_details/arrow_down.gif" alt="Descending" border="0" /></a></div>
<%
					}
%>
						</th>
					</tr>
				</table>
			</th>
<%
				}else{
					dispsrtKey2 = (String)alSearchResultKeys.get(i);
					String csskey =((String)alSearchResultKeys.get(i)).toLowerCase();
					String newcsskey= csskey.replace(" ","_");
					if(dispsrtKey2.equals("Sequence Number")){
						dispsrtKey2 ="#";
					}
%>
			<th align="left" valign="middle" bgcolor="#EAEAEB" class="<%=newcsskey%>">
				<div class="paroleedetailsheaderdiv">
					<div class="paroleedetailsheaderdivtext"><%= dispsrtKey2%></div>
				</div>
			</th>
<%
				}
			}
		}
		if(request.getParameter("fromTools")==null || !"y".equals(request.getParameter("fromTools"))){
%>
			<th align="left" valign="middle" bgcolor="#EAEAEB">
					<div>&nbsp;</div>
			</th>
<%
		}
%>
		</tr>