<%@ page import="com.endeca.navigation.*" %>
<%@ page import="com.endeca.ui.*" %>
<%@ page import="com.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.net.URL" %>	
<%@ page import="com.endeca.ui.gis.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.endeca.ui.UnifiedPropertyMap"%>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="com.plm.util.PLMSearchUtil" %>
<%
	final Logger logger = Logger.getLogger(this.getClass());
	String sUrl = PLMSearchUtil.getFormattedPLMURL(request,"/plm_mobile_controller.jsp");

%>
<html>
	<head>
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0;" />
		<title>Keyword Search</title>
		<link href="media/style/main.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript">
		function ConstructURL(url,removeterms,addterms) {
			var params = BuildURLArray(url);
			var newurl = "";
		
			for (var i=0; i<params.length; i++) {
				val = CheckArray(removeterms,params[i][0]);
				if (val == -1)
					newurl = newurl + "&" + params[i][0] + "=" + params[i][1];
			}
			for (var i=0; i<addterms.length; i++) {
				newurl = newurl + "&" + addterms[i];
			}
			if (newurl.length > 0) {
				newurl = newurl.substr(1);
				newurl = "?"+newurl;
			}
			else {
				newurl = "?N="+eneroot;
			}
			return newurl;
		}
	
		function BuildURLArray(oldurl) {
		
			var returnArray = new Array();
			var url;
		
			if (oldurl == "CURRENTURL"){
				url = location.search;
			}else if (oldurl == "BLANKURL") {
			}
			else {
				var tokens = oldurl.split("?");
				url = "?"+tokens[1];
			}		
			if (url) {
				url = url.substr(1);
				var params = url.split("&");
				for (var i=0; i<params.length; i++) {
					var param = params[i].split("=");
					returnArray[i] = param;
				}
			}
			return returnArray;
		}
	
		function CheckArray(removeterms,checkterm) {
			for (var i=0; i<removeterms.length; i++) {
				if (removeterms[i] == checkterm)
					return 1;
			}
			return -1;
		}
		function DetectBoolean(terms) {
			var tokens = terms.split(/\s+/);
			var isBoolean = false;
			for(var i=0; i<tokens.length; i++) {
				if (tokens[i].search(/^AND$/i)>-1 ||
					tokens[i].search(/^OR$/i)>-1  ||
					tokens[i].search(/^NOT$/i)>-1 ||
					tokens[i].search(/^O?NEAR/i)>-1 ||
					tokens[i].search(/:$/)>-1 ||
					tokens[i].search(/^\(/)>-1 ||
					tokens[i].search(/\)$/)>-1) {
							
					isBoolean = true;
					break;
				}
			}
			return isBoolean;
		}
		function getMode(terms) {
			if(DetectBoolean(terms))
				return 'mode matchboolean';
			else
				return '<%= UI_Props.getInstance().getValue(UI_Props.MATCHMODE) %>';
		}
		function Search() {
		 	var terms = document.searchForm.keywords.value;
			var property = '<%= UI_Props.getInstance().getValue(UI_Props.SEARCHINTERFACE) %>';
			var mode = getMode(terms);
			var within = false;
			var curTerms = '<%= request.getParameter("Ntt")!=null?request.getParameter("Ntt"):"" %>';
			var curProps = '<%= request.getParameter("Ntk")!=null?request.getParameter("Ntk"):"" %>';
			var curModes = '<%= request.getParameter("Ntx")!=null?request.getParameter("Ntx"):"" %>';
			var numSearches = curTerms.split("|").length;
		
			// split still returns 1 if no Ntt exists in query string
			if(curTerms == "")
				numSearches = 0;
			if(numSearches>0) {
				curTerms+="|";
				curProps+="|";
				curModes+="|";
			}
			
			// Advanced search parameters
			if(document.searchForm.advsearch.value == "true") {
				property = document.searchForm.SearchProp[document.searchForm.SearchProp.selectedIndex].value;
				mode = document.searchForm.MatchMode[document.searchForm.MatchMode.selectedIndex].value;
				if(document.searchForm.RelevancyList.value != "") {
					mode += " rel " + document.searchForm.RelevancyList.value;
				}
			}
				
			if(document.searchForm.withinResults
				&& document.searchForm.withinResults.checked == true) {
				within = true;
			}
		
			var removeterms;
			var addterms;
			if(within) {
				//******************* START BUG WORKAROUND ***************
				// Merge queries being sent to same search interface to workaround
				// bug in 4.8.2 where ESearchReports get overwritten
				if(curProps.indexOf(property)>-1) {
					// remove extra | from end of the each param
					curTerms = curTerms.substring(0, curTerms.length-1);
					curProps = curProps.substring(0, curProps.length-1);
					curModes = curModes.substring(0, curModes.length-1);
					
					// get position of current property 
					var propsArray = curProps.split("|");
					var pos = 0;
					while(propsArray[pos] != property && pos<propsArray.length)
						pos++;
					
					// add the new terms to the old terms for the current property
					var termsArray = curTerms.split("|");
					termsArray[pos] += " "+terms;
					// get updated mode for complete list of terms
					mode = getMode(termsArray[pos]);
					
					curTerms = "";
					for(var i=0; i<termsArray.length; i++)
						curTerms += termsArray[i]+"|";
					curTerms = curTerms.substring(0, curTerms.length-1);
					
					// overwrite the mode
					var modeArray = curModes.split("|");
					if(modeArray[pos] != mode) {
						modeArray[pos] = mode;
						curModes = "";
						for(var i=0; i<modeArray.length; i++)
							curModes += modeArray[i]+"|";
						curModes = curModes.substring(0, curModes.length-1);
					}
								
					terms = "";
					property = "";
					mode = "";
				}
				//******************* END BUG WORKAROUND ****************
					
				removeterms = ["Ne","No","Nao","Nty","Dn","Ntt","Ntk","Ntx","D","Dx"];
				addterms = ["Ntk="+curProps+property,"Ntt="+encodeURIComponent(curTerms+terms),"Nty=1","D="+curTerms+terms,"Ntx="+curModes+mode,"Dx="+curModes+mode];
			}else {
				removeterms = ["N","Nf","Nn","Ne","No","Nao","Ntk","Ntt","D","Nty","Ntx","Dx","Dn","page"];
				addterms = ["N=<%= UI_Props.getInstance().getValue(UI_Props.ENE_ROOT) %>","Ntk="+property,"Ntt="+encodeURIComponent(terms),"Nty=1","D="+terms,"Ntx="+mode,"Dx="+mode,"page=searchresults","prevPage=keywordSearch"];
			}
		
			var newurl = ConstructURL("CURRENTURL", removeterms, addterms);
			location.href = '<%=sUrl%>' + newurl;
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
							<div class="back"><a href="plm_mobile_controller.jsp?page=login">Back</a></div>
							<%@ include file="logout_include_mobile.jsp" %>
							<div class="home"><a href="plm_mobile_controller.jsp?page=login">Home</a></div>
						</div>
					</div>
					<div id="Middle">
						<div id="MainContainer">
							<form id="searchForm" name="searchForm" action="Javascript:Search()">
<%
	logger.info("in keyword search:"); 
%>
							<div class="caseandreasonform">
								<div class="formbox">
									<div class="formboxtitle">Keyword Search</div>
									<div class="searchreasondrop">
										<div>Enter Keyword</div>
										<div class="keywordinput">
											<input type="text" name="keywords" />
											<input type="hidden" name="advsearch" value="false"/>
										</div>
										<div class="gobutton">
											<input type="button" onclick="Search()" name="submit" value="GO" />
										</div>
									</div>
								</div>
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