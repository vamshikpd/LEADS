package com.plm.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.endeca.navigation.AggrERec;
import com.endeca.navigation.ENEConnection;
import com.endeca.navigation.ENEConnectionException;
import com.endeca.navigation.ENEException;
import com.endeca.navigation.ENEQuery;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.ERec;
import com.endeca.navigation.ERecList;
import com.endeca.navigation.HttpENEConnection;
import com.endeca.navigation.Navigation;
import com.endeca.navigation.PropertyContainer;
import com.endeca.navigation.PropertyMap;
import com.endeca.navigation.UrlGen;
import com.endeca.ui.AdvancedENEQuery;
import com.endeca.ui.constants.UI_Props;
import com.plm.constants.PLMConstants;
import com.plm.util.PLMSearchUtil;
import com.plm.util.PLMUtil;
import com.plm.util.database.PLMDatabaseUtil;


/**
 * Servlet implementation class GeneratePhotoLineupServlet
 */
public class GeneratePhotoLineupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(GeneratePhotoLineupServlet.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GeneratePhotoLineupServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		request.setCharacterEncoding("UTF-8");
		String queryStr = request.getQueryString();
		if(queryStr!=null && queryStr.indexOf("#")>0) 
			queryStr = queryStr.substring(0, queryStr.indexOf("#"));
		else if(queryStr==null ||queryStr.equals(""))
			queryStr = "N="+UI_Props.getInstance().getValue(UI_Props.ENE_ROOT);

		String eneHost	= PLMSearchUtil.getEndecaHost();
		int enePort	= PLMSearchUtil.getEndecaPort();;

		// If values are missing, set host and port to blanks
		if (eneHost == null) {
			eneHost = "";
		}
		// Place ENE host, port in current request
		request.setAttribute("eneHost", eneHost);
		request.setAttribute("enePort", enePort);

		ENEConnection nec = new HttpENEConnection(eneHost,enePort);

		//************************ AUTHENTICATION ***************************

		// Set authentication cookie
		Cookie c = new Cookie(UI_Props.getInstance().getValue(UI_Props.APP_TITLE).replaceAll("[^\\w\\d]", ""),"1");
		response.addCookie(c);

		UI_Props.getInstance().getValue(UI_Props.DEFAULT_USER);
		if(request.getUserPrincipal()!=null) {
			request.getUserPrincipal().getName();
		}
		AdvancedENEQuery query = null;
		try {
			query = new AdvancedENEQuery(queryStr, nec);
		} catch (ENEException e) {
			e.printStackTrace();
		}
		query.setNumRecsDefault(Integer.parseInt(
				UI_Props.getInstance().getValue(PLMConstants.DEFAULT_NUM_RESULTS_ENDECA_LINEUP)));
				
		Map resultMap = null;
		int cnt = 1;
    	long beforeTime=System.currentTimeMillis();
		try {
			resultMap = query.process().get(0);
        }catch(ENEConnectionException eneConn){
			do{
				eneHost	= PLMSearchUtil.getEndecaHost();
				enePort	= PLMSearchUtil.getEndecaPort();
				nec = new HttpENEConnection(eneHost,enePort);
				query.setConnection(nec);
				cnt++;
			}while(!query.dgraphIsAlive() && cnt<=PLMSearchUtil.getEndecaPortSize());
			try {
				resultMap = query.process().get(0);
			} catch (ENEQueryException e) {
            	logger.error(PLMUtil.getStackTrace(e));
			}
		} catch (ENEQueryException e) {
        	logger.error(PLMUtil.getStackTrace(e));
		}
		
		if(resultMap == null)
			throw new ServletException();
		
    	long afterTime=System.currentTimeMillis() - beforeTime;

		// Place query results object into current request
		request.setAttribute("eneQuery", resultMap.get("QUERY"));

		// Place query results object into current request
		ENEQueryResults qr = (ENEQueryResults)resultMap.get("RESULT");
		request.setAttribute("eneQueryResults", qr);

		UrlGen urlg = new UrlGen(request.getQueryString(), "UTF-8");
		Navigation nav = qr.getNavigation();
		ENEQuery usq = (ENEQuery)resultMap.get("QUERY");
		
		PropertyContainer rec = qr.getERec();
	
		if(rec == null)
			rec = qr.getAggrERec();
	
		String spec = "";
		if(rec instanceof ERec) {
			spec = ((ERec)rec).getSpec();
		}else {
			spec = ((AggrERec)rec).getSpec();
		}
		
		ERecList recs = nav.getERecs();
		int inc 		= Integer.parseInt(UI_Props.getInstance().getValue(PLMConstants.DEFAULT_NUM_RESULTS_ENDECA_LINEUP));
		int maxPages 		= 10;				
		int numLocalRecs 	= recs.size();	
		long startRec 		= nav.getERecsOffset() + 1;
		long numTotalRecs 	= nav.getTotalNumERecs();
		
		int divHeight = 375;
		if(numLocalRecs>4 & numLocalRecs<7){
			divHeight=555;
		}else if(numLocalRecs>=7 & numLocalRecs<9){
			divHeight=725;
		}else if(numLocalRecs>=9){
			divHeight=915;
		}
		StringBuffer sb = new StringBuffer();
		String url = "";
		
		if (numTotalRecs > inc) {
			sb.append("<div id=\"resultpagenumber\" class=\"pagenumberlineup\">&nbsp;&nbsp;&nbsp;&nbsp;");
											// Compute the page set and the number of pages needed
			int activePage 	= (int)((startRec+inc-1)/inc);
			int startPage 	= (int)((((startRec/(inc*maxPages))) * maxPages) + 1);
			int endPage 	= (int)((numTotalRecs/inc) + 1);		
			// Exception for even number sets
			if ((numTotalRecs%inc) == 0) {
				endPage--;
			}
			// Truncate number of pages if overridden with local constant
			// and determine if there is a next page set
			int finalPage = endPage;
			if (endPage > (startPage + maxPages - 1)) {
				endPage = startPage + maxPages - 1;
			}
			// Determine if there is a previous page set
			if (startPage > 1) {		
				// Create previous page set request
				String off = Long.toString((startPage - maxPages - 1)*inc);
				urlg = new UrlGen(request.getQueryString(), "UTF-8");
				urlg.removeParam("No");
				urlg.addParam("No",off);			
				urlg.removeParam("sid");
				urlg.removeParam("in_dym");
				urlg.removeParam("in_dim_search");
				urlg.addParam("sid",(String)request.getAttribute("sid"));
				//url = "plm_controller.jsp"+"?"+urlg;
				url = "GeneratePhotoLineupServlet"+"?"+urlg;
				//sb.append("<a href=\"" + url + "\" style=\"color:rgb(0,64,128);\"><<</a>&nbsp;\">");
				sb.append("<a href=\"#\" onclick=\"javascript:getPhotoLineupResults('"+url+"')\" style=\"color:rgb(0,64,128);\"><<</a>&nbsp;");
				
			}
											// Determine if there is a previous page
			if (activePage > 1) {
				// Create previous page request
				String off = Long.toString(startRec - inc - 1);
				urlg = new UrlGen(request.getQueryString(), "UTF-8");
				urlg.removeParam("No");
				urlg.addParam("No",off);			
				urlg.removeParam("sid");
				urlg.removeParam("in_dym");
				urlg.removeParam("in_dim_search");
				urlg.addParam("sid",(String)request.getAttribute("sid"));
				//url = "plm_controller.jsp"+"?"+urlg;
				url = "GeneratePhotoLineupServlet"+"?"+urlg;
				//sb.append("<a href=\""+url+"\" style=\"color:rgb(0,64,128);\">Prev</a>&nbsp;");
				sb.append("<a href=\"#\" onclick=\"javascript:getPhotoLineupResults('"+url+"')\" style=\"color:rgb(0,64,128);\">Prev</a>&nbsp;");
			}
											// Create direct page index
			for (int i = startPage; i <= endPage; i++) {	
				if (i == activePage) {
					sb.append("<b style=\"font-size:18px\">" + i+"</b>&nbsp;");
				}else {
					// Create direct page request
					String off = Long.toString((i-1)*inc);
					urlg = new UrlGen(request.getQueryString(), "UTF-8");
					urlg.removeParam("No");
					urlg.addParam("No",off);				
					urlg.removeParam("sid");
					urlg.removeParam("in_dym");
					urlg.removeParam("in_dim_search");
					urlg.addParam("sid",(String)request.getAttribute("sid"));
					//url = "plm_controller.jsp"+"?"+urlg;
					url = "GeneratePhotoLineupServlet"+"?"+urlg;

					//sb.append("<a href=\""+url+"\" style=\"color:rgb(0,64,128);\">"+i+"</a>&nbsp;");
					sb.append("<a href=\"#\" onclick=\"javascript:getPhotoLineupResults('"+url+"')\" style=\"color:rgb(0,64,128);\">"+i+"</a>&nbsp;");
				}
			}
			// Determine if there is a next page
			if (finalPage > activePage){
				// Create next page request
				String off = Long.toString(startRec + inc - 1);
				urlg = new UrlGen(request.getQueryString(), "UTF-8");
				urlg.removeParam("No");
				urlg.addParam("No",off);			
				urlg.removeParam("sid");
				urlg.removeParam("in_dym");
				urlg.removeParam("in_dim_search");
				urlg.addParam("sid",(String)request.getAttribute("sid"));
				//url = "plm_controller.jsp"+"?"+urlg;
				url = "GeneratePhotoLineupServlet"+"?"+urlg;
				//sb.append("<a href=\""+url+"\" style=\"color:rgb(0,64,128);\">Next</a>&nbsp;");
				sb.append("<a href=\"#\" onclick=\"javascript:getPhotoLineupResults('"+url+"')\" style=\"color:rgb(0,64,128);\">Next</a>&nbsp;");
			}
											// Determine if there is a next page set
			if (finalPage > (startPage + maxPages - 1)){	
				// Create next page set
				String off = Long.toString(endPage*inc);
				urlg = new UrlGen(request.getQueryString(), "UTF-8");
				urlg.removeParam("No");
				urlg.addParam("No",off);			
				urlg.removeParam("sid");
				urlg.removeParam("in_dym");
				urlg.removeParam("in_dim_search");
				urlg.addParam("sid",(String)request.getAttribute("sid"));
				//url = "plm_controller.jsp"+"?"+urlg;
				url = "GeneratePhotoLineupServlet"+"?"+urlg;
				//sb.append("<a href=\""+url+"\" style=\"color:rgb(0,64,128);\">>></a>");
				sb.append("<a href=\"#\" onclick=\"javascript:getPhotoLineupResults('"+url+"')\" style=\"color:rgb(0,64,128);\">>></a>");
			}
			sb.append("</div>");
		}
		sb.append("<ul id=\"fs_results\" class=\"currentsimilarresultphotobuttom\">");
		// Get record list iterator, first from bulk export if it was specified
		Iterator recIter;
		if(usq.getNavRollupKey() == null) {
			recIter = nav.getBulkERecIter();
			if(recIter == null)
				recIter = nav.getERecs().iterator();
		}else {
			recIter = nav.getBulkAggrERecIter();
			if(recIter == null)
				recIter = nav.getAggrERecs().iterator();
		}
		int i=0;
		PropertyMap propsMap = null;
		while(recIter.hasNext()) {
			i++;
			String fsim_spec = "";
			Object fsim_rec = recIter.next();
			if(fsim_rec instanceof ERec) {
				fsim_spec = ((ERec)fsim_rec).getSpec();
				propsMap = ((ERec)fsim_rec).getProperties();	//sk
			}else {
				fsim_spec = ((AggrERec)fsim_rec).getSpec();
				propsMap = ((ERec)fsim_rec).getProperties(); //sk
			}
			if (fsim_spec.equals(spec)){
				// don't show the suspect in find similar results
				continue;
			}
			String showid = null;
			try {
				showid = PLMDatabaseUtil.getPrimaryMugshotID(fsim_spec);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			String id = fsim_spec+":"+propsMap.get("First Name")+" "+propsMap.get("Last Name");
			sb.append("<li class=\"currentsimilarresultphoto\" id=\""+id+"\" style=\"display:inline;border: 2px #889 solid; margin: 5px\">");
			sb.append("<div class=\"photo\" id=\"photo\">");
			sb.append("<img alt=\"\" src=\"image.jsp?showid="+showid+"&psize=p\" width=\"120\" height=\"150\" title=\""+id+"\"/><br/>");
			sb.append("</div>");
			sb.append("</li>");
		}
		sb.append("</ul>");
		sb.append("</div>");
		out.print(sb.toString());
		out.close();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
