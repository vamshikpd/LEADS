<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ page errorPage="error.jsp" %>
<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.endeca.ui.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="com.endeca.ui.logging.*" %>
<%@ page import="com.endeca.ui.charts.*" %>
<%@ page import="com.util.MapsList" %>
<%@ page import="com.report.*" %>
<%@ page import="org.dom4j.*" %>
<%@ page import="com.plm.constants.PLMConstants" %>
<%@ page import="com.plm.util.PLMSearchUtil" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.net.URLDecoder" %>
<%
final Logger logger = Logger.getLogger(this.getClass());
// set the encoding so all calls to getParameter assume utf8 input.
request.setCharacterEncoding("UTF-8");

// convert '+' into '%2B' only for Nf=geocode
String queryStr = PLMSearchUtil.encodeGeoCodeCriteria(request.getQueryString());

queryStr = URLDecoder.decode(queryStr,"UTF-8");

queryStr = PLMSearchUtil.decodeGeoCodeCriteria(queryStr);

// emil 2018-03-10 replace | and " characters with ASCII codes
//queryStr = PLMSearchUtil.encodeSpecialCharsInSearchQuery(queryStr);

// remove any #anchors from query string to prevent errors in UrlENEQuery
if (queryStr != null && queryStr.indexOf("#") > 0) {
	queryStr = queryStr.substring(0, queryStr.indexOf("#"));
}else if(queryStr == null || queryStr.equals("")){
	queryStr = "N=" + UI_Props.getInstance().getValue(UI_Props.ENE_ROOT);
}

if(request.getParameter("removeSearchQueryFromTools") != null 
		&& "y".equals(request.getParameter("removeSearchQueryFromTools"))){
	session.removeAttribute("searchQueryFromTools");
}

// 	Get ENE and logserver host and port from app.properties in classpath
String eneHost	= PLMSearchUtil.getEndecaHost();
int enePort	= PLMSearchUtil.getEndecaPort();

// Set ENE connection
ENEConnection nec = new HttpENEConnection(eneHost,enePort);

//************************ AUTHENTICATION ***************************

// Set authentication cookie
Cookie c = new Cookie(UI_Props.getInstance().getValue(UI_Props.APP_TITLE).replaceAll("[^\\w\\d]", ""),"1");
response.addCookie(c);

String user = UI_Props.getInstance().getValue(UI_Props.DEFAULT_USER);

if(request.getUserPrincipal() != null) {
	user = request.getUserPrincipal().getName();
} else {
	if (request.getHeader("USERID") != null) {
	    user = request.getHeader("USERID");
	} else if (session.getAttribute("userId") != null) {
		user = (String) session.getAttribute("userId");
	}
}

session.setAttribute("user", user);

//********************* DONE WITH AUTHENTICATION ********************

// SNS change ....remove back param
if(request.getParameter("back") != null) {
	UrlGen sUrlg = new UrlGen(queryStr, "UTF-8");
	sUrlg.removeParam("back");
	queryStr = sUrlg.toString();
} else{	
	UrlGen sUrlg = new UrlGen(queryStr, "UTF-8");
	queryStr = sUrlg.toString();
}

//logger.debug(String.format("plm_controller: query string --> %s", queryStr));

AdvancedENEQuery query = new AdvancedENEQuery(queryStr, nec);

if(("2").equals(request.getParameter("ptab"))){
	query.setNumRecsDefault(Integer.parseInt(
		UI_Props.getInstance().getValue(PLMConstants.DEFAULT_NUM_RESULTS_ENDECA_LINEUP)));
}else{
	if(("3").equals(request.getParameter("tab"))){
		query.setNumRecsDefault(Integer.parseInt(
		 	UI_Props.getInstance().getValue(PLMConstants.DEFAULT_NUM_RESULTS_VIEW_ALL_PAROLEE)));
	
	}else{
		query.setNumRecsDefault(Integer.parseInt(
			UI_Props.getInstance().getValue(UI_Props.NUM_ITEMS)));
	}
}
		
//*********************** SET PROPERTIES TO RETURN ******************
String props = "";

if(request.getParameter("N")!=null && request.getParameter("Sp")==null) {
	if(UI_Props.getInstance().getValue(UI_Props.RECORD_URL_PROP)!=null
			&& !UI_Props.getInstance().getValue(UI_Props.RECORD_URL_PROP).matches("^\\s*$"))
		props = UI_Props.getInstance().getValue(UI_Props.RECORD_URL_PROP)+"||";
	
	// Set report return props
	query.setReturnProps(props);
	if (("2").equals(request.getParameter("tab"))
			|| ("3").equals(request.getParameter("tab"))){
		props += "||"+UI_Props.getInstance().getValue(UI_Props.GEOCODE_LABEL);
		props += "||"+UI_Props.getInstance().getValue("IMAGE_PROPERTY");
		props += "||"+UI_Props.getInstance().getValue("FIRST_NAME");
		props += "||"+UI_Props.getInstance().getValue(UI_Props.GEOCODE);
		props += "||"+UI_Props.getInstance().getValue("HEIGHT_FEET");
		props += "||"+UI_Props.getInstance().getValue("HEIGHT_INCHES");
		props += "||"+UI_Props.getInstance().getValue("WEIGHT");
		props += "||"+UI_Props.getInstance().getValue("ETHNICITY");
		props += "||"+UI_Props.getInstance().getValue("GENDER");
		props += "||"+UI_Props.getInstance().getValue("STATUS");
		query.setReturnProps(props);
	}
	
	// Commented below code for Data session issues in parole information tabs after upgrading to Endeca 11.1 version
	/* if ((request.getParameter("tab")== null)
			|| ("0").equals(request.getParameter("tab"))
			|| ("1").equals(request.getParameter("tab"))){		
		props += "||"+UI_Props.getInstance().getValue("HEIGHT_FEET");
		props += "||"+UI_Props.getInstance().getValue("HEIGHT_INCHES");		
		props += "||"+UI_Props.getInstance().getValue("WEIGHT");
		props += "||"+UI_Props.getInstance().getValue("STREET");
		props += "||"+UI_Props.getInstance().getValue("CITY");
		props += "||"+UI_Props.getInstance().getValue("COUNTY_CODE");
		props += "||"+UI_Props.getInstance().getValue("ZIP");		
		props += "||"+UI_Props.getInstance().getValue(UI_Props.GEOCODE);	
		props += "||"+UI_Props.getInstance().getValue("ACTION_DATE");	
		props += "||"+UI_Props.getInstance().getValue("ADDRESS_CHANGED_DATE");	
		props += "||"+UI_Props.getInstance().getValue("HRSO");	
		props += "||"+UI_Props.getInstance().getValue("GPS");	
		props += "||"+UI_Props.getInstance().getValue("GENDER");
		props += "||"+UI_Props.getInstance().getValue("LAST_NAME");	
		props += "||"+UI_Props.getInstance().getValue("FIRST_NAME");	
		props += "||"+UI_Props.getInstance().getValue("ETHNICITY");	
		props += "||"+UI_Props.getInstance().getValue("HAIRCOLOR");	
		props += "||"+UI_Props.getInstance().getValue("EYECOLOR");	
		props += "||"+UI_Props.getInstance().getValue("UNIT_NAME");	
		props += "||"+UI_Props.getInstance().getValue("STATUS");	
		props += "||"+UI_Props.getInstance().getValue("BIRTH_DATE");
		props += "||"+UI_Props.getInstance().getValue("FULL_PHONE");
		query.setReturnProps(props);
	} */
}
//*********************** DONE SETTING RETURN PROPS *****************

Map resultMap = null;
int cnt = 1;

try{
	resultMap = query.process().get(0);
}catch(ENEConnectionException eneConn){
	do{
		eneHost	= PLMSearchUtil.getEndecaHost();
		enePort	= PLMSearchUtil.getEndecaPort();
		nec = new HttpENEConnection(eneHost,enePort);
		query.setConnection(nec);
		cnt++;
	}while(!query.dgraphIsAlive() && cnt<=PLMSearchUtil.getEndecaPortSize());	
	resultMap = query.process().get(0);
}

// Place query results object into current request
request.setAttribute("eneQuery", resultMap.get("QUERY"));

// Place query results object into current request
ENEQueryResults qr = (ENEQueryResults)resultMap.get("RESULT");
request.setAttribute("eneQueryResults", qr);

//Fork to PDF Generate Servlet page
if ("Y".equalsIgnoreCase(request.getParameter("PAL"))){
	request.setAttribute("navigation", qr.getNavigation());
	request.setAttribute("pdfdisptype",PLMConstants.PDF_DISPLAY_TYPE_PAL_POSTER);
	request.setAttribute("psize", "p");
	RequestDispatcher disp = request.getRequestDispatcher("GeneratePDFServlet");
	disp.forward(request, response);
	 out.clear();
	 out = pageContext.pushBody();
	return;
}

// Fork to navigation page
if (("Y".equals(request.getParameter("back"))) || (qr.containsNavigation() && !(qr.containsERec() || qr.containsAggrERec()))) {
	// Set navigation object in current request
	request.setAttribute("navigation", qr.getNavigation());

	//Get the number of records from the request.
	Navigation nav = qr.getNavigation();
 	long nERecs = nav.getTotalNumERecs();

	// Forward to navigation page
	RequestDispatcher disp = null;

	if (("2").equals(request.getParameter("tab"))){
		disp = request.getRequestDispatcher("map_rd.jsp");
	}else if (("3").equals(request.getParameter("tab"))){
		if (nERecs > 500){
			disp = request.getRequestDispatcher("plm_controller.jsp?tab=0");
		}else {
			disp = request.getRequestDispatcher("map_rd.jsp");
		}
	}else{
		if("y".equals(request.getParameter("fromTools"))){
			disp = request.getRequestDispatcher("search_screen_rd_tools.jsp");
		}else{
			disp = request.getRequestDispatcher("search_screen_rd.jsp");
		}
	}
	disp.forward(request, response);
	return;
}else if (request.getParameter("ptab") != null){
	// for find similar we have both R and N in query but we want to stay on photo lineup page.  That can be identified by ptab param.  
	// Set navigation object in current request
	request.setAttribute("navigation", qr.getNavigation());
	RequestDispatcher disp = null;
	if (request.getParameter("ptab") == null || request.getParameter("ptab").equals("0")){
		disp = request.getRequestDispatcher("parolee_details_rd.jsp");
	}else if (("2").equals(request.getParameter("ptab"))){
		disp = request.getRequestDispatcher("photo_lineup_rd.jsp");
	}else if (("1").equals(request.getParameter("ptab"))){
		disp = request.getRequestDispatcher("photo_gallery_rd.jsp");
	}
	disp.forward(request, response);
	return;
}

// Fork to record page
if (qr.containsERec() || qr.containsAggrERec()) {
	// 	Forward to interactive record page
	RequestDispatcher disp = null;
	if (request.getParameter("ptab") == null || request.getParameter("ptab").equals("0")){
		if("y".equalsIgnoreCase(request.getParameter("singleParoleeMap"))){
			request.setAttribute("navigation", qr.getNavigation());
			disp = request.getRequestDispatcher("map_rd_parolee.jsp");
		}else{
			disp = request.getRequestDispatcher("parolee_details_rd.jsp");
		}
	}else if (("2").equals(request.getParameter("ptab"))){
		disp = request.getRequestDispatcher("photo_lineup_rd.jsp");
	}else if (("1").equals(request.getParameter("ptab"))){
		disp = request.getRequestDispatcher("photo_gallery_rd.jsp");
	}
	disp.forward(request, response);
	return;
}
%>