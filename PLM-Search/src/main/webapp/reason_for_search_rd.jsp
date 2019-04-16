<%@ page errorPage="error.jsp" %>
<%@ page import="com.endeca.ui.constants.UI_Props"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Reason for Search</title>
        <script src="media/js/jquery-1.3.2.js" type="text/javascript"></script>
		<script type="text/javascript">
			jQuery(document).ready(function() {
				setTimeout(function(){
					$("form :input[type='text']:enabled:first").focus();
				},800);
				$("form").keypress(function (e) {
					if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
						$("form :input[name='find']").click();
						return false;
					} else {
						return true;
					}
				});
			});
		</script>
		<link rel="stylesheet" href="media/style/main.css" type="text/css"/>
		<script language="JavaScript" src="media/js/validate.js" type="text/javascript"></script>
		<script language="javascript" type="text/javascript">
			function openurl(link1){
				self.close();
				var ReferURL = parent.window.location.href;
				parent.window.location.href = link1;
				//window.opener.location.href = window.opener.location.href;
			}
		</script>
	</head>
	<body id="advsearchbody">
		<form name="caseAndReason" id="caseAndReason" action="saved_case_and_reason_action.jsp">
			<input type="hidden" name="url" value="<%= request.getQueryString() %>" />
			<div id="advSearchTop">
				<div>
					<div><strong>Please enter Case # and Reason for search</strong></div>
					<br clear="all" /><br clear="all" />
					<div>
						<div>
							<div class="inputname">* Case #</div>
							<div><input name="case" id="case" type="text" value="" maxLength="30"/></div>
						</div>
					</div>
					<br clear="all" />
					<div>
						<div>
							<div class="inputname">* Reason</div>
							<div>
								<select name="reason" id="reason">
									<option value="">Select A Reason</option>
<%
	String[] reasonForSearch = UI_Props.getInstance().getValue("REASON_FOR_SEARCH").split("\\|");
	if(reasonForSearch != null && reasonForSearch.length>=0){
		for(int i=0; i<reasonForSearch.length; i++) {
%>
									<option value='<%=reasonForSearch[i]%>'><%=reasonForSearch[i]%></option>
<%
		}
	}
%>
								</select>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="searchbutton">
				<input type="image" name="find" value="Search" src="media/images/global/proceed_to_search_button.gif" onClick="validateCaseAndReason('case', 'reason', 'caseAndReason');return false;"/>
				<br><br>
			</div>
		</form>
	</body>
</html>
