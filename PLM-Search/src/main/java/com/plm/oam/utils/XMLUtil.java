package com.plm.oam.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * XML utility methods.
 * 
 * @author Ashish Chaphekar
 */

public class XMLUtil {

	private static final Logger logger = Logger.getLogger(XMLUtil.class);

	public static String getValueByXPath(String xpathExpression,
			String domStream) {
		return getValueByXPath(xpathExpression, new InputSource(new StringReader(domStream)));
	}
	
	public static String getValueByXPath(String xpathExpression,
			InputSource domStream) {
		DocumentBuilder parser;
		String result = null;
		try {
			parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = parser.parse(domStream);
			XPath xpath = XPathFactory.newInstance().newXPath();
			result = xpath.evaluate(xpathExpression, doc);
			logger.debug("Output :: " + result);
		} catch (Exception e) {
			logger.error(e);
		}
		return result;
	}

	public static void readNodesByXPath(String xpathExpression,
			String fileName) {
		DocumentBuilder parser;
		try {
			parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = parser.parse(new File(fileName));
			XPath xpath = XPathFactory.newInstance().newXPath();
			logger.debug("Evaluate Result : "
					+ xpath.evaluate(xpathExpression, doc));

			NodeList nodes = (NodeList) xpath.evaluate(xpathExpression, doc,
					XPathConstants.NODESET);
			for (int i = 0, n = nodes.getLength(); i < n; i++) {
				Node node = nodes.item(i);
				logger.debug("Node : " + node);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public static Document getDocument(File file) {
		DocumentBuilder parser;
		Document document = null;
		try {
			parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = parser.parse(file);
		}
		catch (Exception ex){
			logger.error(ex);
		} 
		return document;
	}
	
	public static String readFile(String fileName) {
		StringBuffer sb = new StringBuffer();; 
		try {
			FileReader reader = new FileReader(fileName);
			BufferedReader br = new BufferedReader(reader);
			String eachLine = br.readLine();

			while (eachLine != null) {
				sb.append(eachLine);
				sb.append("\n");
				eachLine = br.readLine();
			}
			System.out.println(sb.toString());

		} catch (IOException e) {
			logger.error(e);
		}
		return sb.toString();
	}
	
	public static String readFile(File file) {
		StringBuffer sb = new StringBuffer();; 
		try {
			FileReader reader = new FileReader(file);
			BufferedReader br = new BufferedReader(reader);
			String eachLine = br.readLine();

			while (eachLine != null) {
				sb.append(eachLine);
				sb.append("\n");
				eachLine = br.readLine();
			}
			System.out.println(sb.toString());

		} catch (IOException e) {
			logger.error(e);
		}
		return sb.toString();
	}

}