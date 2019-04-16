package com.plm.google;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.Implfactory.ImplementationFactory;
import com.singletone.GoogleMapModelImpl;

public class LatLanResolver extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Document doc;
	Document docas;
	public LatLanResolver() {
		doc = null;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {
		response.setContentType("text/xml");
		ServletContext sc = getServletContext();
		String sourceType = sc.getInitParameter("sourceType");
		ImplementationFactory factory = new ImplementationFactory(sourceType);
		GoogleMapModelImpl gmapDataModel = factory.getGoogleMapImplInstance();
		double NELatitude = Double.parseDouble(request.getParameter("neLat").replace('p', '.'));
		double NELongitude = Double.parseDouble(request.getParameter("neLang").replace('p', '.'));
		double SWLatitude = Double.parseDouble(request.getParameter("swLat").replace('p', '.'));
		double SWLongitude = Double.parseDouble(request.getParameter("swLang").replace('p', '.'));	
		Map<Integer,ArrayList<String>> map = gmapDataModel.getDataMap(NELatitude, NELongitude, SWLatitude, SWLongitude,sc);
		Iterator<Integer> itLat = map.keySet().iterator();
		doc = new Document();
		Element eleSchools = new Element("Schools");
		doc.addContent(eleSchools);
		while(itLat.hasNext()) {
			
			Integer key = itLat.next().intValue();
			ArrayList<String> values = map.get(key);
			Element eleSchool = new Element("School");			
			eleSchool.setAttribute("latitude", values.get(0).toString());
			eleSchool.setAttribute("longitude", values.get(1).toString());
			eleSchool.setAttribute("schoolname", values.get(2).toString());
			eleSchool.setAttribute("streetname", values.get(3).toString());
			
			eleSchools.addContent(eleSchool);
		}
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		outputter.output(doc, response.getOutputStream());
	
	}     // 

}
