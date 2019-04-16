<%@ page import="com.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.plm.util.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.endeca.navigation.*" %>
<%@ page import="com.endeca.ui.*" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="com.endeca.ui.UnifiedPropertyMap" %>
<%@ page import="com.plm.google.*" %>
<%@ page import="java.net.URL" %>
<%@ page import="com.endeca.ui.gis.*" %>
<%@ page import="com.plm.util.PLMSearchUtil" %>
<%
	// Retrieve navigation object from current request
	final Logger logger = Logger.getLogger(this.getClass());
	Navigation nav = (Navigation)request.getAttribute("navigation");
	ENEQuery usq = (ENEQuery)request.getAttribute("eneQuery");
	int refineTabIdInt = Integer.valueOf(request.getParameter("refineTabID") != null ?request.getParameter("refineTabID") :"1").intValue();
	String tempQueryString = URLDecoder.decode(request.getQueryString(),"UTF-8");
	String queryString = PLMSearchUtil.encodeGeoCodeCriteria(tempQueryString);
	String searchQuery ="";
	if(request.getParameter("singleParoleeMap")!=null && "y".equalsIgnoreCase(request.getParameter("singleParoleeMap"))){
		searchQuery = (String)session.getAttribute("searchQuery");
	}else{
		searchQuery = PLMSearchUtil.getSearchQuery(request);
		searchQuery = PLMSearchUtil.decodeGeoCodeCriteria(searchQuery);
	}

	// emil 2018-03-08 replace | and " characters with ASCII codes
	searchQuery = PLMSearchUtil.encodeSpecialCharsInSearchQuery(searchQuery);

	//logger.debug("searchQuery:" + searchQuery);
	session.setAttribute("searchQuery",searchQuery);
	session.removeAttribute("searchQueryFromTools");
	boolean removeSearchQueryFromTools = true;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<title>Parole LEADS 2.0</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<script src="<%= GoogleMapUtil.getGoogleMapUrl(request.isSecure()) %>" type="text/javascript"></script>
		<script language="JavaScript" src="media/js/prototype.js"></script>
		<script language="JavaScript" src="media/js/endeca_util.js"></script>
		<script language="JavaScript">
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
					return "mode matchboolean";
				else
					return "<%= UI_Props.getInstance().getValue(UI_Props.MATCHMODE) %>";
			}
			function Search(tab) {
				var terms = document.searchForm.keywords.value;
				var property = "<%= UI_Props.getInstance().getValue(UI_Props.SEARCHINTERFACE) %>";
				var mode = getMode(terms);
				var within = false;
				var curTerms = "<%= request.getParameter("Ntt")!=null?request.getParameter("Ntt"):"" %>";
				var curProps = "<%= request.getParameter("Ntk")!=null?request.getParameter("Ntk"):"" %>";
				var curModes = "<%= request.getParameter("Ntx")!=null?request.getParameter("Ntx"):"" %>";
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
					addterms = ["Ntk="+curProps+property,"Ntt="+encodeURIComponent(curTerms+terms),"Nty=1","Ntx="+curModes+mode];
				}else {
					removeterms = ["N","Nf","Nn","Ne","No","Nao","Ntk","Ntt","D","Nty","Ntx","Dx","Dn"];
					addterms = ["N=<%= UI_Props.getInstance().getValue(UI_Props.ENE_ROOT) %>","Ntk="+property,"Ntt="+encodeURIComponent(terms),"Nty=1","Ntx="+mode];
				}
				var newurl = '<%=  UI_Props.getInstance().getValue("CONTROLLER")%>'+ ConstructURL("BLANKURL", removeterms, addterms)+"&tab="+tab;
				location.href = newurl;
			}
		</script>
		<link href="media/style/thickbox.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" href="media/style/main.css" type="text/css"/>
		<script type="text/javascript" src="media/js/ajax_js.js"></script>
		<script type="text/javascript" src="media/js/jquery-1.3.2.js"></script>
		<script type="text/javascript" src="media/js/thickbox.js"></script>
		<script type="text/javascript" src="media/js/common.js"></script>
		<script type="text/javascript" src="media/js/maps.js"></script>
	</head>
	<body>
		<div align="center">
			<div id="Maindiv">
				<div id="Container" >
<!-- Middle Part Start -->
					<div id="Middle"  class="content_box" >
						<div  id="middletop">
							<div  id="middletopleft">
								<%@ include file="step1.jsp" %>
								<%@ include file="PC290_notification.jsp" %>
								<%@ include file="data_download_button.jsp" %>
							</div>
							<div  id="middletopright">
								<%@ include file="step2.jsp" %>
								<%@ include file="refine_by.jsp" %>
							</div>
							<div  id="middlebottom">
								<%@ include file="step3_map.jsp" %>
							</div>
						</div>
					</div>
<!-- Middle Part End -->
				</div>
			</div>
		</div>
	</body>
</html>