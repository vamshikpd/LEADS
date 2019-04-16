package com.plm.servlets;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import coldfusion.xml.rpc.CFCInvocationException;

import com.endeca.ui.constants.UI_Props;
import com.plm.constants.PLMConstants;
import com.plm.util.PLMUtil;
import com.plm.ws.BookProxy;
import com.plm.ws.LeadsAuth;

import org.apache.log4j.Logger;


public class CalParoleAccessServlet extends HttpServlet {

	private static final long serialVersionUID = -3847542764494558354L;
	private static final Logger logger = Logger.getLogger(CalParoleAccessServlet.class);

	private ServletContext context;
	private static final String cp2leadslandingpage = UI_Props.getInstance().getValue(PLMConstants.CALPAROLE_TO_LEADS_LANDING_PAGE_LABEL); 
	
	public void init(ServletConfig config) throws ServletException{
		super.init(config);
		context = config.getServletContext();
	}

    public void doPost(HttpServletRequest req,HttpServletResponse res) throws IOException, ServletException  {
		String secretTokenFromCalparole = req.getParameter("secureTokenStr");
		String calparoleServer = req.getParameter("calparoleServer");
		LeadsAuth book = new BookProxy(calparoleServer);
		try {
			String retStr = book.echoString(secretTokenFromCalparole);
			StringTokenizer strToken = null;
			if (retStr != null)
				strToken = new StringTokenizer(retStr, "|");

			String[] retStrArray = new String[7];  
            for(int aa=0; strToken.hasMoreTokens(); aa++){
            	retStrArray[aa] = strToken.nextElement().toString();    
            }           
			
			if (retStrArray != null && "Y".equals(retStrArray[0])) {
				try {
				      RequestDispatcher reqDisp = context.getRequestDispatcher("/" + cp2leadslandingpage);
			      
				      req.setAttribute("fromCP","Y");
				      req.setAttribute("FIRSTNM", retStrArray[3]);
				      req.setAttribute("LASTNM", retStrArray[4]);
				      req.setAttribute("FULLNM", retStrArray[3] + " " + retStrArray[4]);
				      req.setAttribute("DESC", retStrArray[2]);
				      req.setAttribute("MAIL", retStrArray[5]);
				      req.setAttribute("USERID", retStrArray[1]);
				      req.setAttribute("IPADDRESS", retStrArray[6]);
				      
				      reqDisp.forward(req, res);
				      
				    } catch (ServletException e) {
	                	logger.error(PLMUtil.getStackTrace(e));
				    } catch (IOException e) {
	                	logger.error(PLMUtil.getStackTrace(e));
				    }				
			}
		} catch (CFCInvocationException e) {
        	logger.error(PLMUtil.getStackTrace(e));
		} catch (RemoteException e) {
        	logger.error(PLMUtil.getStackTrace(e));
		}
	}
}
