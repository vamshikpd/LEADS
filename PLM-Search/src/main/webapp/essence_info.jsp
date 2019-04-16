<div class="essenceinfodivboxcontent">
<%
//Added to blank the Status field for PRCS - LBB
String sUnitSubstrEssensePg = "";
try {
    // 2013-03-10 emil special processing for units with shorter names
	sUnitSubstrEssensePg = sUntNm.substring(0, 4);
} catch (StringIndexOutOfBoundsException e) {}

if (sUnitSubstrEssensePg.equals("PRCS") || sUnitSubstrEssensePg.equals("DAI-") == true) {
	sStatus = "";	
}
%>
	<div id="essenceinfodivboxrow">
		<div class="col1"><span class="essenceinfoname">CDC#:</span> <%=sCDCNum%></div>
		<div class="col1"><span class="essenceinfoname">Name:</span> <%=sFstNm%>&nbsp;<%=sLstNm%></div>
		<div class="col1"><span class="essenceinfoname">Parole Status:</span> <%=sStatus%></div>
		<div class="col1"><span class="essenceinfoname">Ethnicity:</span> <%=sRace%></div>
		<div class="col1"><span class="essenceinfoname">Gender:</span> <%=sSx%></div>
	</div>
</div>