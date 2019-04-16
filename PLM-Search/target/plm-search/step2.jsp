<script type="text/javascript" src="media/js/ajax_js.js"></script>
<script type="text/javascript">
	jQuery(document).ready(function() {
	    setTimeout(function(){
	        $("form :input[name='keywords']:enabled:first").focus();
	    },200);
	});
	function confirmDelete(delUrl) {
	  if (confirm("Are you sure you want to delete saved search")) {
	    document.location = delUrl;
	  }
	}
	function setSavedSearch(searchVal,searchName){
		var serverResponse = "";
		var resp = execute_get("RetrieveSavedSearchKeyValuePairs?searchid="+searchVal+"&searchname="+searchName , false);
		if((resp == true)) {
			serverResponse='<%=UI_Props.getInstance().getValue(UI_Props.CONTROLLER)%>'+"?"+xmlHttp.responseText;
		}
		return serverResponse;
	}
</script>
<%
	String tab1 = request.getParameter("tab");
	if(tab1 == null){
		tab1 ="0";
	}
	String delete_search = request.getParameter("searchName") != null ? request.getParameter("searchName") : null;
%>
<div id="step2div">
	<div id="step2divtop">
		<div id="step2divtopleft"><img src="media/images/global/grey_blue_box_left.gif" alt="" /></div>
		<div id="step2divtopmiddle">
			<div id="step2textdiv"><img src="media/images/global/step2_my_query_text.gif" alt="" /></div>
			<div id="savesearchdiv">
				<span><a href="#"><img src="media/images/global/folder_icon.gif" alt="Save Search" title="Save Search" /></a></span>
				<span id="saveseachtext"><a class="thickbox" title="Save" name="Search" href="save_search_rd.jsp?keepThis=true&amp;TB_iframe=true&modal=false&amp;width=325;height=130">Save Search</a></span>
				<span>
					<select name="saved_searches" id="saved_searches" onchange='javascript:location=setSavedSearch(this.options[this.selectedIndex].value,this.options[this.selectedIndex].text);'>
						<option value="">Saved Searches</option>
<%
	String user= (String)session.getAttribute("userId");
	String selectString = PLMConstants.SELECT_SAVE_SEARCH_STRING;
	Set<String> keySet = null;
	Iterator<String> iter1 = null;
	Map<String,String> savedSearches = PLMSearchUtil.getSavedSearches(selectString,user);
	
	if(savedSearches!= null){
		keySet = savedSearches.keySet();
		if(keySet!=null){
			iter1 = keySet.iterator();
		}
	}
	
	while(iter1 !=null && iter1.hasNext()){
		String searchName = iter1.next();
		if(delete_search!= null && delete_search.equals(searchName)){
%>
						<option selected value='<%=savedSearches.get(searchName)%>'><%=searchName%></option>
<%
		}else{
%>
						<option value='<%=savedSearches.get(searchName)%>'><%=searchName%></option>
<%
		}
	}
%>
					</select>
				</span>
<%
	if(delete_search != null){
%>
				<a href="delete_save_search.jsp?delete_search=<%=delete_search%>" onclick="return confirm('Are you sure you want to delete?')"><img src="media/images/global/delete_icon.gif" alt="Delete Save Search" title="Delete Save Search" /></a>
<%
	}
%>
			</div>
		</div>
		<div id="step2divtopright"><img src="media/images/global/grey_blue_box_right.gif" alt="" /></div>
	</div>
	<div id="step2divsearchdiv">
		<form id="searchForm" style="float: left; width: 78%;" name="searchForm" action="Javascript:Search('<%=tab1 %>')">
			<div class="searchtext">
				<img src="media/images/global/search_term_text.gif" alt="" />
			</div>
			<div class="searchterminput">
				<input name="keywords" type="text" value="" />
				<br clear="all" />
				<div class='egtext'>(e.g. cdc #, parolee name, street name etc.)</div>
				<input type="hidden" name="advsearch" value="false"/>
			</div>
			<div>
				<a href="javascript:document.searchForm.submit();">
					<img src="media/images/global/search_button.gif"  name="go" alt="go" border="0" />
				</a>
			</div>
			<div class="helpicon">
				<a href="JavaScript:plmHelpPopup('<%=request.getContextPath() %>/ohw_help.jsp?topic_id=keyword_search');">
					<img src="media/images/global/help_icon.gif" alt="" />
				</a>
			</div>
			<div style="float: left;width: 10%;">
				<div>
					<div class="orangearrow">
						<img src="media/images/global/orange_arrow_button.gif" alt="" />
						<!-- Widened the search box to make room for COLLR --. LBB -->
						<a class="thickbox" title="Quick" name="Search" href="advance_search_rd.jsp#?keepThis=true&amp;TB_iframe=true&amp;width=610;height=860">Quick Search</a>
					</div>
				</div>
				<div>
					<div class="orangearrow">
						<img src="media/images/global/orange_arrow_button.gif" alt="" />
						<a class="thickbox" title="Radial" name="Search"  href="radial_search_rd.jsp#?keepThis=true&amp;TB_iframe=true&amp;width=400;height=310">Radial Search</a>
					</div>
				</div>
			</div>
		</form>
		
	</div>
</div>