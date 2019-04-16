package com.plm.servlets;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.endeca.navigation.DimVal;
import com.endeca.navigation.DimValList;
import com.endeca.navigation.Dimension;
import com.endeca.navigation.ENEConnectionException;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.HttpENEConnection;
import com.endeca.ui.AdvancedENEQuery;
import com.endeca.ui.constants.UI_Props;
import com.endeca.ui.export.XmlENEResultsWriter;
import com.plm.util.PLMSearchUtil;


public class PLMGetCities extends HttpServlet
{

    private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PLMGetCities.class);
	
    public PLMGetCities()
    {
    }

    @SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {    	
        response.setContentType("text/xml");
        String eneHost = PLMSearchUtil.getEndecaHost();
        String enePort = PLMSearchUtil.getEndecaPort();

        com.endeca.navigation.ENEConnection nec = new HttpENEConnection(eneHost, enePort);
        Document resultDocument;
        String county = request.getParameter("county");
        try
        {
        	String sQuery ="N="+county+"&Ns=City%7C0"; // emil 2018-03-07 Fix | character issue in Tomcat
        	//String sQuery ="N=0&Ns=City|0&Nrs=collection()/record[ endeca:matches(., " + "\""+"County Name"+"\"," +"\""+county+"\""+")]";
            AdvancedENEQuery query = new AdvancedENEQuery(sQuery, nec);
            
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
            		logger.debug("counter: " + cnt);
            	}while(!query.dgraphIsAlive() && cnt<=PLMSearchUtil.getEndecaPortSize());
            	
            	resultMap = query.process().get(0);
            }
            
        	long afterTime=System.currentTimeMillis() - beforeTime;
        	logger.info("Time endeca took to process following query is " + afterTime + " milliseconds");
        	logger.info("Query: " + sQuery);
            ENEQueryResults qr = (ENEQueryResults)resultMap.get("RESULT");
            XmlENEResultsWriter xmlWriter = new XmlENEResultsWriter(qr);
            resultDocument = xmlWriter.getDocument();
          
            Dimension dim =  null;
            Element recsElm = resultDocument.getRootElement().getChild("Records");
            Element recElm = null;
            dim = qr.getNavigation().getRefinementDimensions().getDimension("Res City");
            if(dim != null){
            	DimValList refs = dim.getRefinements();
            	 if(refs != null){
            		 for(int i = 0; i < refs.size(); i++){
            			 DimVal ref = refs.getDimValue(i);
            			 recElm = new Element("Record"); 
                 		 //recElm.setAttribute("city",ref.getName());
                 		 recElm.setAttribute("city",ref.getName() + "|" + ref.getId());
                 		 //recsElm.addContent(ref.getName() + "|" + ref.getId());                 		 
         	             recsElm.addContent(recElm);	                
            		 }
            	 }
            }        
        }
        catch(ENEQueryException e)
        {
            Element resultsElement = new Element("Endeca_Response");
            resultDocument = new Document(resultsElement);
            Element recsElement = new Element("Records");
            recsElement.setAttribute("TotalNumResults", "Error");
            recsElement.setAttribute("TotalNumERecs", "Error");
            resultsElement.addContent(recsElement);
        }
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        outputter.output(resultDocument, response.getOutputStream());
    }
}
