	<div id="step3divtop">
		<div id="step3divtopleft"><img src="media/images/global/grey_blue_box_left.gif" alt="" /></div>
		<div id="step3divtopmiddle">
			<div id="step2textdiv"><img src="media/images/global/step3_parolee_result_text.gif" alt="" /></div>
		</div>
<%
	//if(Integer.parseInt(numTotalRecords)>0){
		if(!"y".equalsIgnoreCase(request.getParameter("singleParoleeMap"))){
%>
		<div style="float: left; padding-top: 7px; padding-left: 20px; width:auto">
			<div style="float: left;"><img src="media/images/global/left_half_curve_button_grey_bg.gif" alt=""/></div>
			<div style="float: left;"><a href="#" onClick="javascript:printToPDF(<%=numTotalRecs%>);void(0);" style="padding: 5px; width: auto; background-color: #E17236; color: #fff; font-weight:bold; float:left; text-decoration:none; padding-bottom:4px">Print Results</a></div>
			<div style="float: left;"><img src="media/images/global/right_half_curve_button_grey_bg.gif" alt=""/></div>
		</div>
<%
		}
	//}
%>
		<div style="float:right">
			<%@ include file="step3_tabs.jsp" %>
		</div>
		<div id="savesearchdiv">
			<span id="saveseachtext">Matching Records:<%=numTotalRecords%>&nbsp;&nbsp;&nbsp;</span>
		</div>
	</div>