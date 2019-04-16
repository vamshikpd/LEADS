package com.plm.servlets;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.endeca.ui.constants.UI_Props;
import com.plm.constants.PLMConstants;
import com.plm.util.PLMUtil;
import org.apache.log4j.Logger;

/*
----------------------------------------------------------------------------------------------------------------+
Reason
Defect #91   | Emil  | 5/8/18 | Modified code to pull displayName attribute from request parameters             |
----------------------------------------------------------------------------------------------------------------+
*/

public class SomsAccessServlet extends HttpServlet {

	private static final long serialVersionUID = -3847542764494558354L;
	private static final Logger logger = Logger.getLogger(SomsAccessServlet.class);

	private ServletContext context;
	private static final String soms2leadslandingpage = UI_Props.getInstance().getValue(PLMConstants.SOMS_TO_LEADS_LANDING_PAGE_LABEL); 
	
	public void init(ServletConfig config) throws ServletException{
		super.init(config);
		context = config.getServletContext();
	}

    public void doPost(HttpServletRequest req,HttpServletResponse res) {
		
    	String firstName = req.getParameter("FIRSTNM");
    	String lastName = req.getParameter("LASTNM");
    	String email  = req.getParameter("MAIL");
    	String description  = req.getParameter("DESC");
    	String userId  = req.getParameter("USERID");
    	String ipAddress = req.getParameter("IPADDRESS");
    	String displayName = req.getParameter("DISPLAYNM");

    	try{
    		RequestDispatcher reqDisp = context.getRequestDispatcher("/" + soms2leadslandingpage);
    			
    			req.setAttribute("fromSOMS","Y");
    			req.setAttribute("FIRSTNM", firstName);
    			req.setAttribute("LASTNM", lastName);
    			req.setAttribute("FULLNM", firstName + " " + lastName);
    			req.setAttribute("DESC", description);
    			req.setAttribute("MAIL", email);
    			req.setAttribute("USERID", userId);
    			req.setAttribute("IPADDRESS", ipAddress);
    			req.setAttribute("DISPLAYNM", displayName);

    			reqDisp.forward(req, res);
    		
    	}catch (ServletException | IOException e) {
        	logger.error(PLMUtil.getStackTrace(e));
	    }
	}
}
