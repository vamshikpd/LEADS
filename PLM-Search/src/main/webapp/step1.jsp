<%@ page import="com.endeca.ui.constants.UI_Props"%>
<script language="JavaScript" src="media/js/validate.js"></script>
<script language="JavaScript" src="media/js/plm_ohw.js"></script>
<form name="populateCaseAndResult" action="saved_case_and_reason_action.jsp" onSubmit="return validateCaseAndReason('case', 'reason', 'populateCaseAndResult');">
	<div id="step1div">
		<div class="bl">
			<div class="br">
				<div class="tr">
					<div class="tb">
						<div class="tl">
							<div id="step1divmiddlepart">
<%
	String tempQryStr = URLEncoder.encode( queryString, "UTF-8");;
	tempQryStr = tempQryStr.replaceAll("\"","%22");
%>
								<input type="hidden" name="url" value="<%= tempQryStr %>" />
								<input type="hidden" name="N" value="0" />
								<input type="hidden" name="page" value="step1" />
								<div>* Case Number #<br />
									<input name="case" type="text" id="case" value="<%=session.getAttribute("case_no")!=null?session.getAttribute("case_no").toString():""%>" maxLength="30"/>
								</div>
								<div>* Reason For Search<br />
									<select name="reason" id="reason" value="<%=session.getAttribute("reason_no")!=null?session.getAttribute("reason_no").toString():""%>">
										<option value="">Select A Reason</option>
<%
	String[] reasonForSearch = UI_Props.getInstance().getValue("REASON_FOR_SEARCH").split("\\|");
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
%>
									</select>
								</div>
								<div>
									<div>* Required Fields</div>
									<div class="helpicon"><a href="JavaScript:plmHelpPopup('<%=request.getContextPath() %>/ohw_help.jsp?topic_id=step1');"><img src="media/images/global/help_icon.gif" alt="" /></a></div>
								</div>
								<div class="button" >
									<div id="step1divbottompart">
										<input class="searchbutton1" type="image" name="find" value="Search" src="media/images/global/submit_button.gif" />
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</form>