<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.endeca.ui.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.endeca.ui.logging.*" %>
<%@ page import="com.endeca.ui.charts.*" %>
<%@ page import="com.util.MapsList" %>
<%@ page import="com.report.*" %>
<%@ page import="org.dom4j.*" %>
<%@ page import="org.apache.log4j.Logger" %>
<%final Logger logger = Logger.getLogger(this.getClass()); %>
<html>
	<head>
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0;" />
		<title>Reason for search</title>
		<link href="media/style/main.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript">
			function validateCaseAndReason(saveCase,saveReason){	
				var sCase = document.getElementById(saveCase).value;
				var sReason = document.getElementById(saveReason).value;
				if(sCase =='' && sReason ==''){
						alert("Please enter Case# and Select a reason for search");
						return false;
				}else{
					if(sCase == ''){
						alert("Please enter Case#");
						return false;
					}
					if(sReason==''){
						alert("Please select Reason for Search");
						return  false;
					}
				}
			}
		</script>
	</head>
	<body>
		<div align="center">
			<div id="Maindiv">
				<div id="Container">
					<div id="Header">
						<div class="logo"><span class="logo1">PAROLE</span><span class="logo2">LEADS2.0</span></div>
						<div class="top">
							<%@ include file="logout_include_mobile.jsp" %>
							<div class="home"><a href="plm_mobile_controller.jsp?page=login">Home</a></div>
						</div>
					</div>
					<div id="Middle">
						<div id="MainContainer">
							<form name="caseAndReason" action="saved_case_and_reason_action.jsp" onsubmit="return validateCaseAndReason('case','reason');"/>
								<div class="caseandreasonform">
									<div class="formbox">
										<div class="formboxtitle">Case # and Reason for search</div>
										<div class="case">
											<div class="case">Please enter the following:</div>
											<div class="casetext">Case#:</div>
											<div class="caseinput"><input type="text" name="case" id="case"  value="<%=session.getAttribute("case_no")!=null?session.getAttribute("case_no").toString():""%>" /></div>
										</div>
										<div class="searchreasondrop">
											<div>
												<select name="reason" id="reason">
													<option value="">Select A Reason</option>
<%
												String[] reasonForSearch = UI_Props.getInstance().getValue("REASON_FOR_SEARCH").split("\\|");
												if(reasonForSearch != null && reasonForSearch.length>=0){
													for(int i=0; i<reasonForSearch.length; i++) {
														if(session.getAttribute("reason_no")!=null&& reasonForSearch[i].equals(session.getAttribute("reason_no").toString())){
%>	
													<option selected value="<%=reasonForSearch[i]%>"><%=reasonForSearch[i]%></option>
<%
														}else{
%>
													<option value="<%=reasonForSearch[i]%>"><%=reasonForSearch[i]%></option>
<%
														}
													}
												}
%>
												</select>
											</div>
										</div>
									</div>
								</div>
								<input type="hidden" name="page" id="page" value="" />
								<div class="searchtype">
									<div>Click search type:</div><br />
									<input type="submit" name="keywordSearch" value="Keyword Search" onclick="document.getElementById('page').value='keywordSearch';" /><br />
									<input type="submit" name="registrantNotification" value="PC290 Registrant Notification" onclick="document.getElementById('page').value='registrantNotification';" /><br />
									<input type="submit" name="dischargeNotification" value="PC290 Discharge Notification" onclick="document.getElementById('page').value='dischargeNotification';" /><br />
								</div>
							</form>	
						</div>
					</div>
					<div id="Footer"></div>
				</div>
			</div>
		</div>		
	</body>
</html>