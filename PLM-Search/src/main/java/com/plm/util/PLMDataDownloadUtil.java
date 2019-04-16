package com.plm.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.endeca.ui.constants.UI_Props;
import com.plm.constants.PLMConstants;

public class PLMDataDownloadUtil {

	public static String isPhotoDownloadAvailable_ = UI_Props.getInstance().getValue(PLMConstants.PHOTO_DOWNLOAD_AVAILABLE_FLAG);
/*
	<?xml version="1.0"?>
	<InternalParoleeDataResponse>
		<TxnStatus>ParoleeDataEndpoint.RESPONSE_STATUS_ERROR</TxnStatus>
		<Error>
			<ErrorCode>errorCode</ErrorCode>
			<ErrorMessage>errMsg</ErrorMessage>
		</Error>
	</InternalParoleeDataResponse>
*/
	
	public static HashMap<String,String> getErrorMessageForDownload(File file) {
		HashMap<String,String> hm = new HashMap<String,String>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document doc = null;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(file);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		if(doc!=null && doc.getDocumentElement()!=null){
			doc.getDocumentElement().normalize();
			NodeList errorNodeLst = doc.getElementsByTagName("Error");
	
			String sErrorCode = null;
			String sErrorMsg = null;
			for (int s = 0; s < errorNodeLst.getLength(); s++) {
	
				Node errorNode = errorNodeLst.item(s);

				if (errorNode.getNodeType() == Node.ELEMENT_NODE) {
					Element errorElmnt = (Element) errorNode;
					
					NodeList errorCodeElmntLst = errorElmnt.getElementsByTagName("ErrorCode");
					Element errorCodeElmnt = (Element) errorCodeElmntLst.item(0);
					NodeList errorCode = errorCodeElmnt.getChildNodes();
					
					NodeList errorMsgElmntLst = errorElmnt.getElementsByTagName("ErrorMessage");
					Element errorMsgElmnt = (Element) errorMsgElmntLst.item(0);
					NodeList errorMsg = errorMsgElmnt.getChildNodes();
					
					sErrorCode = errorCode.item(0).getNodeValue();
					sErrorMsg = errorMsg.item(0).getNodeValue();
					if(sErrorCode!=null)
						hm.put(sErrorCode, sErrorMsg);
				}	
	
			}
		}
		return hm;
	}
	
	public static boolean isPhotoDownloadAvailable(){
		return "true".equalsIgnoreCase(isPhotoDownloadAvailable_);
	}
}
