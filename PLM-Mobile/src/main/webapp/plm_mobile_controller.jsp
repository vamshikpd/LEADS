<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ page import="com.endeca.navigation.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.endeca.ui.*" %>
<%@ page import="com.endeca.ui.constants.*" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="com.plm.util.PLMSearchUtil" %>
<%@ page import="com.plm.constants.PLMConstants" %>
<%@ page import="com.plm.servlets.PLMLogPc290Results" %>
<%
	final Logger logger = Logger.getLogger(this.getClass());
	// set the encoding so all calls to getParameter assume utf8 input.
	request.setCharacterEncoding("UTF-8");
	String user = (String)session.getAttribute("userId");
	String queryStr = request.getQueryString();
	String sPage = request.getParameter("page");
	//String prevPage = request.getParameter("prevPage");
//	logger.info("user=" + user);
//	logger.info("queryStr=" + queryStr);

	//if(prevPage != null){
	//	sPage = prevPage;
	//}
	if(sPage == null){
		logger.info("page is null");
		if(user == null){
			logger.error("user is null");
			RequestDispatcher disp = request.getRequestDispatcher("login.jsp");
			disp.forward(request, response);
			return;
		}else{
			//RequestDispatcher disp = request.getRequestDispatcher("/reason_for_search.jsp");
			logger.error("user is not null");
			RequestDispatcher disp = request.getRequestDispatcher("/terms.jsp");
			disp.forward(request, response);
			return;
		}
	}else{
		logger.info("page is not null");
		if(user == null){
			logger.error("user is null");
			String requestUser = (String) request.getAttribute("userName"); 
			//String requestPassword = request.getParameter("password");
			if(requestUser  != null /*&& requestPassword != null*/){
				logger.warn("request user is not null");
				session.setAttribute("userId",requestUser);
				RequestDispatcher disp = request.getRequestDispatcher("/reason_for_search.jsp");
				disp.forward(request, response);
				return;
			}else{
				logger.warn("request user is null");
				RequestDispatcher disp = request.getRequestDispatcher("login.jsp");
				disp.forward(request, response);
				return;
			}
		}else{
			logger.info("user is not null");
			logger.info("page is " + sPage);
			if(sPage.equals("login")){
				RequestDispatcher disp = request.getRequestDispatcher("/reason_for_search.jsp");
				disp.forward(request, response);
				return;
			}
			if (sPage.equals("keywordSearch")){
				RequestDispatcher disp = request.getRequestDispatcher("/keyword_search.jsp");
				disp.forward(request, response);
				return;
			}
			if(sPage.equals("dischargeNotification")){
				RequestDispatcher disp = request.getRequestDispatcher("/pc290_discharge.jsp");
				disp.forward(request, response);
				return;
			}
			if(sPage.equals("registrantNotification")){
				RequestDispatcher disp = request.getRequestDispatcher("/pc290_registrant.jsp");
				disp.forward(request, response);
				return;
			}
			if(sPage.equals("searchresults")){
				String doAudit = request.getParameter("doAudit");
				if(doAudit!=null && "true".equals(doAudit)){
					logger.warn("browser does not support ajax...");
					String county = request.getParameter("county");
					String city = request.getParameter("city");
					String userName = request.getParameter("userName");
					String ipAddress = request.getParameter("ipAddress");
					String qry_type = request.getParameter("qry_type");
					PLMLogPc290Results.doAudit(county,city,userName,ipAddress,qry_type);
				}
			}
		}
	}
  	//LogWriter.startLogging(request);

	// SET ENE & REQUEST --------------------------------------------------
	// Get ENE and logserver host and port from app.properties in classpath
	String eneHost = PLMSearchUtil.getEndecaHost();
	String enePort = PLMSearchUtil.getEndecaPort();
	
	// If values are missing, set host and port to blanks
	if (eneHost == null) {
		eneHost = "";
	}
	if (enePort == null) {
		enePort = "";
	}
	
	// Set ENE connection
	ENEConnection nec = new HttpENEConnection(eneHost,enePort);
	
	//************************ AUTHENTICATION ***************************
	
	// Set authentication cookie
	Cookie c = new Cookie(UI_Props.getInstance().getValue(UI_Props.APP_TITLE).replaceAll("[^\\w\\d]", ""),"1");
	response.addCookie(c);
	
	//********************* DONE WITH AUTHENTICATION ********************
	AdvancedENEQuery query = new AdvancedENEQuery(queryStr, nec);
	query.setNumRecsDefault(Integer.parseInt(UI_Props.getInstance().getValue(PLMConstants.DEFAULT_NUM_RESULTS_MOBILE_APP)));
	
	//*********************** SET PROPERTIES TO RETURN ******************
	String props = "";
	long beforeTime=System.currentTimeMillis();
	//Map resultMap = query.process().get(0);
	
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
			logger.warn("counter: " + cnt);
		}while(!query.dgraphIsAlive() && cnt<=PLMSearchUtil.getEndecaPortSize());
		resultMap = query.process().get(0);
	}
	
	long afterTime=System.currentTimeMillis() - beforeTime;
	logger.info("Time endeca took to process following query is " + afterTime + " milliseconds");
	logger.info("Query: " + queryStr);
	
	// Place query results object into current request
	request.setAttribute("eneQuery", resultMap.get("QUERY"));
	
	// Place query results object into current request
	ENEQueryResults qr = (ENEQueryResults)resultMap.get("RESULT");
	request.setAttribute("eneQueryResults", qr);
	
	// Fork to record page
	if(qr.containsNavigation()){
		logger.info("query results contains navigation");
		request.setAttribute("navigation", qr.getNavigation());
		RequestDispatcher disp = request.getRequestDispatcher("search_result.jsp");
		disp.forward(request, response);
		return;
	}
	if (qr.containsERec() || qr.containsAggrERec()) {
		logger.info("query result contains erec or aggrerec");
		logger.info("page is " + sPage);
		request.setAttribute("record", qr.getERec());
		if(sPage.equals("paroleedetails")){
			// Forward to interactive record page
			RequestDispatcher disp = null;
			disp = request.getRequestDispatcher("parolee_mobile_details.jsp");
			disp.forward(request, response);
			return;
		}else{
			RequestDispatcher disp = null;
			if (("mobileDetails").equals(sPage)){
				disp = request.getRequestDispatcher("parole_status_notification.jsp");
			}
			if (("personalInfo").equals(sPage)){
				disp = request.getRequestDispatcher("personal_information.jsp");
			}
			if (("paroleInfo").equals(sPage)){
				disp = request.getRequestDispatcher("parole_information.jsp");
			}
			if (("photo").equals(sPage)){
				disp = request.getRequestDispatcher("photo.jsp");
			}
			if (("residenceInfo").equals(sPage)){
				disp = request.getRequestDispatcher("residence_information.jsp");
			}
			if (("smtInfo").equals(sPage)){
				disp = request.getRequestDispatcher("SMT_information.jsp");
			}
			if (("aliasInfo").equals(sPage)){
				disp = request.getRequestDispatcher("alias_information.jsp");
			}
			if (("monikerInfo").equals(sPage)){
				disp = request.getRequestDispatcher("moniker_information.jsp");
			}
			if (("offenseInfo").equals(sPage)){
				disp = request.getRequestDispatcher("offense_information.jsp");
			}
			if (("jobInfo").equals(sPage)){
				disp = request.getRequestDispatcher("job_information.jsp");
			}
			if (("vehicleInfo").equals(sPage)){
				disp = request.getRequestDispatcher("vehicle_information.jsp");
			}
			if (("googleMap").equals(sPage)){
				disp = request.getRequestDispatcher("google_map.jsp");
			}
			if (("emailAgent").equals(sPage)){
				disp = request.getRequestDispatcher("email_agent.jsp");
			}
			disp.forward(request, response);
		return;
		}
	}
%>