package com.endeca.ui.export;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.endeca.ui.constants.UI_Props;
import com.plm.util.PLMSearchUtil;
import com.plm.constants.PLMConstants;
import com.util.http.QueryHandler;

import java.net.MalformedURLException;

// Referenced classes of package com.endeca.ui.export:
//            EndecaPDFWriter, ExportException

public class PDFExportServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(PDFExportServlet.class);

    public PDFExportServlet() {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        try {
        	String rootPath = "";
        	String filename = "Endeca_Export.pdf";
        	String queryString = request.getQueryString();

        	// 2018-03-14 emil added additional replacements for ASCII codes for < > characters
        	queryString = queryString.replaceAll("%3E=","%3E%3D");
        	queryString = queryString.replaceAll(">=","%3E%3D");
        	queryString = queryString.replaceAll("%3C=","%3C%3D");
        	queryString = queryString.replaceAll("<=","%3C%3D");

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
            response.reset();
            response.setContentType("application/pdf");            
            response.setHeader("Content-Disposition", (new StringBuilder("inline; filename=")).append(filename).toString());
            java.io.OutputStream out = response.getOutputStream();
            
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
            EndecaPDFWriter epw = new EndecaPDFWriter(queryString);
            
            if(qh.getParam("Page").equals(PLMConstants.PRINT_PAROLEE_PAGE)) {
            	epw.writeParoleePDF(out);
            } else if(qh.getParam("Page").equals(PLMConstants.PRINT_RESULTS_PAGE)) {
            	epw.writeResultsPDF(out);
            }
           
        } catch(ExportException ee) {
            throw new ServletException(ee);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        doGet(request, response);
    }
}

