<%@ page import="com.endeca.navigation.*" %>
<%@ page import="com.endeca.ui.*" %>
<%@ page import="com.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Save Search</title>
		<link rel="stylesheet" href="media/style/main.css" type="text/css"/>
		<script src="media/js/jquery-1.3.2.js" type="text/javascript"></script>
		<script type="text/javascript">
			jQuery(document).ready(function() {
				setTimeout(function(){
					$("form :input[type='text']:enabled:first").focus();
				},200);
				$("form").keypress(function (e) {
					 if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
						$("form :input[name='find']").click();
						 return false;
						} else {
						 return true;
						}
				 });
			});
			var savedSearches = parent.document.getElementById('saved_searches').options;
			function post_value(){
					opener.document.form.parent_name.value = document.frm.child_name.value;
				self.close();
			}
			function checkSearchNameExist(save_search){
				if(save_search.replace(/^\s+|\s+$/g, '') == ''){
					document.getElementById("errMsgDiv").innerHTML = "Save Search name is empty";
					return;
				}
				for(var i = 1 ; i < eval(savedSearches.length) ; i++) {
					var savedSearch = savedSearches[i].text;
					if(savedSearch == save_search){
						document.getElementById("errMsgDiv").innerHTML = "Save Search name already exist";
						return;
					}
				}
				document.forms[0].submit();
			}
		</script>
		<script language="JavaScript" src="media/js/plm_ohw.js"></script>
	</head>
	<body id="advsearchbody">
		<form name="savesearchform" id="savesearchform" action="saved_search_action.jsp">
			<input type="hidden" name="action" value="save" />
<%
	String sReferrer  = request.getHeader("REFERER").substring(request.getHeader("REFERER").indexOf("?")+1);
	String url =  "";
	if(!request.getQueryString().equals(sReferrer)){
		url =  sReferrer;
	}
	else{
		url = request.getQueryString();
	}
%>
			<input type="hidden" name="url" value='<%= url %>' />
			<div id="advSearchTop">
				<div id="helpdiv">
					<a href="JavaScript:plmHelpPopup('<%=request.getContextPath() %>/ohw_help.jsp?topic_id=save_search');">
						<img src="media/images/global/help_icon.gif" alt="" />
					</a>
				</div>
				<div>
					<div>
						<div class="errorMsg" id="errMsgDiv"></div>
					</div>
					<div>
						<div>
							<div class="inputnameforsavesearch">Enter a name for search</div>
							<div><input name="title" id="title" type="text"/></div>
						</div>
					</div>
					<br clear="all" />
				</div>
			</div>
			<div id="advSearchMiddle2" class="savesearchbuttons">
				<div id="advSearchbottom2main" style="float:none">
					<div class="advsearchbutton">
						<input type="image" name="find" value="Save" onClick="checkSearchNameExist(document.getElementById('title').value); return false;" src="media/images/global/save_button.gif" />
					</div>
					<div class="advsearchbutton">
						<input type="image" onClick="this.form.reset();return false;" name="clearfields" value="ClearFields" src="media/images/global/clear_fields_button.gif"/>
					</div>
					<div class="advsearchbutton">
						<input type="image" onClick="parent.tb_remove();return false;" name="close" value="close" src="media/images/global/cancel_button.gif"/>
					</div>
				</div>
			</div>
		</form>
	</body>
</html>
