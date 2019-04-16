package com.plm.servlets.jasper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.log4j.Logger;

import com.endeca.navigation.ENEConnection;
import com.endeca.navigation.ENEConnectionException;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.HttpENEConnection;
import com.endeca.navigation.Navigation;
import com.endeca.navigation.UrlGen;
import com.endeca.ui.AdvancedENEQuery;
import com.endeca.ui.BreadcrumbHandler;
import com.jasper.datasource.JREndecaDataSource;
import com.lowagie.text.FontFactory;
import com.plm.util.PLMSearchUtil;
import com.plm.util.PLMUtil;
import com.util.MapsList;
import com.util.http.QueryHandler;

/**
 * Servlet implementation class PLMJasperReportsServlet
 */
public class PLMJasperReportsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PLMJasperReportsServlet.class);
	private ENEConnection nec = null; 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PLMJasperReportsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String rootPath = "";
    	String filename = "Endeca_Export.pdf";
    	String queryString = request.getQueryString();
    	boolean isSingleResult = Boolean.parseBoolean(request.getParameter("isSingleResult"));

		// 2018-03-14 emil added additional replacements for ASCII codes for < > characters
		queryString = queryString.replaceAll("%3E=","%3E%3D");
		queryString = queryString.replaceAll(">=","%3E%3D");
		queryString = queryString.replaceAll("%3C=","%3C%3D");
		queryString = queryString.replaceAll("<=","%3C%3D");

    	ServletContext context = this.getServletConfig().getServletContext();


    	QueryHandler qh = new QueryHandler(request.getQueryString(), "UTF-8");
        if(qh.getParam("eneHost") == null) {
            queryString = queryString + "&eneHost=" +PLMSearchUtil.getEndecaHost();
        } else {
        	queryString = queryString + "&eneHost=" +qh.getParam("eneHost");
        }
        
        if(qh.getParam("enePort") == null) {
            queryString = queryString + "&enePort=" + PLMSearchUtil.getEndecaPort();
        } else {
        	queryString = queryString + "&enePort=" +qh.getParam("enePort");
        }         
        
        if(qh.getParam("Ef") != null) {
            filename = qh.getParam("Ef");
        }
        
        try{
            rootPath = getServletContext().getResource("/").getPath();
        }catch(MalformedURLException e){
        	e.printStackTrace();
        }

		if(rootPath != null) {
			int index = rootPath.indexOf("WEB-INF");
			if(index >= 0 ) {
				rootPath = rootPath.substring(0,index);
			}
		}	  
		queryString = queryString + "&rootPath=" + rootPath;
        //EndecaPDFWriter epw = new EndecaPDFWriter(queryString);
		UrlGen sUrlg = null;
		ENEQueryResults qr = null;
    	Navigation nav = null;

    	if(queryString != null){
			sUrlg = new UrlGen(queryString, "UTF-8");; 
		}
		if(sUrlg != null){
			queryString = sUrlg.toString();
			AdvancedENEQuery query = null; 
        	//Map resultMap = null;
			String eneHost = PLMSearchUtil.getEndecaHost();
			int enePort = PLMSearchUtil.getEndecaPort();
			ENEConnection nec = new HttpENEConnection(eneHost, enePort);
        	try{
				query = new AdvancedENEQuery(queryString, nec);
				//			query.process().getResult(0).getNavigation();
				qr = query.process().getResult(0);
				//resultMap = query.process().get(0);
        	}catch(ENEConnectionException e){
        		logger.error(PLMUtil.getStackTrace(e));
        		nec = getLiveEndecaConnection();
        		try{
        			query = new AdvancedENEQuery(queryString, nec);
        			qr = query.process().getResult(0);
        		}catch(ENEConnectionException eneConExcp){
        			logger.error(PLMUtil.getStackTrace(eneConExcp));
        		}catch(ENEQueryException eneQryExcp){
        			logger.error(PLMUtil.getStackTrace(eneQryExcp));
        		}
        	}catch(ENEQueryException e){
        		logger.error(PLMUtil.getStackTrace(e));
        	}
        	nav = qr.getNavigation();
    		String NrsQryString = qh.getParam("Nrs");
            MapsList crumbs = BreadcrumbHandler.getBreadcrumbs(nav, qh.toString());
            String keywords = "";
            List<String> lstQuery = null;
            if(crumbs.size() > 0 || NrsQryString != null) {            	
            	if (NrsQryString != null) {
              		String sRecord = NrsQryString.substring((NrsQryString.indexOf("record[")+7),NrsQryString.lastIndexOf("]"));
    				lstQuery = PLMSearchUtil.getQueryParameters(sRecord);
                }
            	
            	StringBuffer sb = new StringBuffer();
            	if(crumbs.size() > 0 ) {	            	
	                Map posMap;
	                for(Iterator iter = crumbs.iterator(); iter.hasNext(); sb.append(new StringBuilder("  ")).append(posMap.get("Label")).append(": ").append(posMap.get("Filter")).toString(), FontFactory.getFont("Helvetica", 9F)) {
	                    posMap = (Map)(Map)iter.next();                 
	                }
            	}
            	if (lstQuery != null) {
            		String rep = "";
	                for(String s: lstQuery){
	                	rep = s.replace("_"," ");
	                	sb.append("  " + rep);
	                }
                }
                if(keywords != null) {
            		keywords = keywords + "  " + sb.toString();
                }                
            } else {
                keywords="All";
            }
        	HashMap hmBreadCrumbs = new HashMap();
        	hmBreadCrumbs.put("breadcrumb", keywords);
        	hmBreadCrumbs.put("reporttitle", "Parolee Search Results");
        	hmBreadCrumbs.put("mediadir", rootPath + "/media");
        	
        	JREndecaDataSource jrEDS = new JREndecaDataSource(qr,isSingleResult);
        	JasperReport jasperReport = null;
        	JasperPrint jasperPrint = null;
        	try{
        		JasperDesign jasperDesign = JRXmlLoader.load(context.getRealPath("/reports/searchlist.jrxml"));
        		jasperDesign.setLanguage("java");
        		//JasperCompileManager.compileReportToFile(context.getRealPath("/reports/searchlist.jrxml"));
        		jasperReport = JasperCompileManager.compileReport(jasperDesign);

        		//String reportFileName = context.getRealPath("/reports/searchlist.jasper");
    			//File reportFile = new File(reportFileName);
    			//if (!reportFile.exists())
    			//	throw new JRRuntimeException("File searchlist.jasper not found. The report design must be compiled first.");


        		//jasperReport = JasperCompileManager.compileReport(rootPath + "/reports/searchlist.jrxml");
    			jasperPrint =JasperFillManager.fillReport(jasperReport,hmBreadCrumbs,jrEDS);
        	}catch(JRException jre){
    			logger.error(PLMUtil.getStackTrace(jre));
        	}
        	JRPdfExporter exporter = new JRPdfExporter();
        	List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			jasperPrintList.add(jasperPrint);	
			exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
			OutputStream ouputStream = response.getOutputStream();
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, ouputStream);
			try{
				exporter.exportReport();
			}catch (JRException e){
				throw new ServletException(e);
			}finally{
				if (ouputStream != null){
					try{
						ouputStream.close();
					}catch (IOException ex){
					}
				}
			}
		}
        //if(qh.getParam("Page").equals(PLMConstants.PRINT_PAROLEE_PAGE)) {
        	//epw.writeParoleePDF(out);
        //} else if(qh.getParam("Page").equals(PLMConstants.PRINT_RESULTS_PAGE)) {
        	//epw.writeResultsPDF(out);
        //}
       
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	public ENEConnection getLiveEndecaConnection() {
		String eneHost = "";
		int newEnePort = 0;
		int noOfAttempts = 0;
		boolean attemptSuccessful = false;
		int currEnePort = ((HttpENEConnection)nec).getPort();
		do{
			try {
				eneHost = PLMSearchUtil.getEndecaHost();
				newEnePort = PLMSearchUtil.changeEndecaPort(currEnePort);
				URL localURL = new URL("http", eneHost, newEnePort, "/admin?op=stats");
				localURL.getContent();
				attemptSuccessful = true;
			} catch (IOException e) {
				currEnePort = newEnePort;
				noOfAttempts++;
				logger.error(PLMUtil.getStackTrace(e));
			}
		}while(noOfAttempts<3 && !attemptSuccessful);
		
		if(!attemptSuccessful){
			logger.error("endeca Connection failed more than 3 times");
			nec = null;
		}else{
			nec = new HttpENEConnection(eneHost, newEnePort);
		}
		return nec;
	}
}
