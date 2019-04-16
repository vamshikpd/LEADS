package com.plm.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPBody;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.ws.soap.axiom.AxiomMtomClient;

import com.endeca.ui.export.PDFExportServlet;
import com.plm.util.PLMDataDownloadUtil;


public class PLMStartDataDownloadProcess extends HttpServlet {
	private static int counter = 0;
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PLMStartDataDownloadProcess.class);

	public PLMStartDataDownloadProcess() {
	}

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String responseStatus = "";
		PrintWriter out = response.getWriter();
		boolean isPhotoDownloadAvailable = PLMDataDownloadUtil.isPhotoDownloadAvailable();
		
		//String sCity = request.getParameter("City");
		String sCaseNumber = request.getParameter("casenumber");
		if (sCaseNumber == null) {
			sCaseNumber = "default";
		}
		String sReason = request.getParameter("reason");
		if (sReason == null) {
			sReason = "default";
		}
		
		String sCounty = request.getParameter("county");
		//logger.debug("sCounty:"+sCounty);
		
		String sLastupdatedate = request.getParameter("lastupdatedate");
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		AxiomMtomClient axiomDataClient = (AxiomMtomClient) applicationContext.getBean("axiomDataClient");

		/*String desc = request.getHeader("DESC");
		String sGroupCode = "";
		if (desc.indexOf("=") > -1 && desc.indexOf(";") > 1)
			sGroupCode = desc.substring(desc.indexOf("=") + 1, desc.indexOf(";"));*/
		
		String sGroupCode = null;

		String sIncludeStateWidePAL = request.getParameter("includeStateWidePAL");
		
		String userName = request.getHeader("USERID");

        // emil 2018-03-08 fix for userId missing from Headers
		if (userName == null) {
            HttpSession httpSession = request.getSession();
            if (httpSession != null) {
                userName = (String) httpSession.getAttribute("userId");
                if (userName == null) {
                    userName = (String) request.getAttribute("userName");
                }
            }
        }

		counter++;

		logger.debug("Start Data Download ---> userName=" + userName + " sGroupCode=" + sGroupCode + " sCounty=" + sCounty + " sCaseNumber=" + sCaseNumber + " sReason=" + sReason + " IncludeStateWidePAL=" + sIncludeStateWidePAL);

		SOAPBody bodyData = axiomDataClient.doIt(sLastupdatedate, sGroupCode, userName, sCounty, sCaseNumber, sReason, "CSV", sIncludeStateWidePAL);

		Iterator<OMElement> iteDataResp = bodyData.getChildElements();
		while (iteDataResp.hasNext()) {
			OMElement omDataResp = iteDataResp.next(); // ParoleeDataResponse

			if (omDataResp.getLocalName().equalsIgnoreCase("InternalParoleeDataResponse")) {
				Iterator<OMElement> iteDataRespSub = omDataResp.getChildElements();
				while (iteDataRespSub.hasNext()) {
					OMElement omDataRespSub = iteDataRespSub.next();
					if (omDataRespSub.getLocalName().equalsIgnoreCase("TxnStatus")) {
						responseStatus = omDataRespSub.getText();
						break;
					}
				}
			}// End ParoleeDataResponse
		}
		if(responseStatus.equalsIgnoreCase("success") && isPhotoDownloadAvailable){
			AxiomMtomClient axiomPhotoClient = (AxiomMtomClient) applicationContext.getBean("axiomPhotoClient");
			SOAPBody bodyPhoto = axiomPhotoClient.doIt(sLastupdatedate,sGroupCode, userName, sCounty, sCaseNumber,sReason, "CSV", sIncludeStateWidePAL);
			Iterator<OMElement> itePhotoResp = bodyPhoto.getChildElements();
			while (itePhotoResp.hasNext()) {
				OMElement omPhotoResp = itePhotoResp.next(); // ParoleeDataResponse
	
				if (omPhotoResp.getLocalName().equalsIgnoreCase("InternalParoleePhotoResponse")) {
					Iterator<OMElement> itePhotoRespSub = omPhotoResp.getChildElements();
					while (itePhotoRespSub.hasNext()) {
						OMElement omPhotoRespSub = itePhotoRespSub.next();
						if (omPhotoRespSub.getLocalName().equalsIgnoreCase("TxnStatus")) {
							responseStatus = omPhotoRespSub.getText();
							break;
						}
					}
				}// End ParoleeDataResponse
			}
		}
		out.print(responseStatus);
		out.close();
	}
}
