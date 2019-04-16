package com.plm.util;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.apache.log4j.Logger;

import com.endeca.navigation.ENEConnection;
import com.endeca.navigation.ENEConnectionException;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.HttpENEConnection;
import com.endeca.navigation.UrlGen;
import com.endeca.ui.AdvancedENEQuery;
import com.plm.dataretrival.DataException;
import com.plm.dataretrival.cfg.ConfigDataProvider;



public class PLMSearchUtil {
	private static final Logger logger = Logger.getLogger(PLMSearchUtil.class);
	private static int portIndex_ = 0;
	private static int hostIndex_ = 0;
	private static final String[] eneHost_ = ConfigDataProvider.getInstance().getWSConfig().getEndecaHost().split("\\s");
	//private static final int[] enePort_ = ConfigDataProvider.getInstance().getWSConfig().getEndecaPorts().split("\\s");
	private static final int[] enePort_ = getEndecaPorts(ConfigDataProvider.getInstance().getWSConfig().getEndecaPorts());
	private static final int PAGE_MAX_RECORDS = ConfigDataProvider.getInstance().getWSConfig().getRecordsPerPage();

	public synchronized static int getEndecaPort(){
		if(enePort_.length==1)
			return enePort_[0];
		
		if(portIndex_==enePort_.length)
			portIndex_=0;
		
		return enePort_[portIndex_++];
	}
	
	public static int getEndecaPortSize(){
		return enePort_.length;
	}
	
	public synchronized static String getEndecaHost(){
		if(eneHost_.length==1)
			return eneHost_[0];
		
		if(hostIndex_==eneHost_.length)
			hostIndex_=0;
		
		return eneHost_[hostIndex_++];
	}
	
	public static ENEQueryResults getQueryResults(String url, ENEConnection nec) throws DataException{
		UrlGen sUrlg = null;
		ENEQueryResults qr = null;
		if(url != null){
			sUrlg = new UrlGen(url, "UTF-8");; 
		}
		if(sUrlg != null){
			String queryString = sUrlg.toString();
			AdvancedENEQuery query = null; 
        	Map resultMap = null;
        	try{
				query = new AdvancedENEQuery(queryString, nec);
				query.setNumRecsDefault(PAGE_MAX_RECORDS);
        		resultMap = query.process().get(0);
        	}catch(ENEConnectionException e){
        		logger.error(PLMUtil.getStackTrace(e));
        		throw new DataException(PLMConstants.ERR_PARTIAL_UPDATE_INPROGRESS);
        	}catch(ENEQueryException e){
        		logger.error(PLMUtil.getStackTrace(e));
        		throw new DataException(PLMConstants.ERR_SEARCH_ENGINE);
        	}
			qr = (ENEQueryResults)resultMap.get("RESULT");
		}
		return qr;
	}
	
	public static int changeEndecaPort(int deadPort){
		int changePortIndex = 0;
		for(int i=0; i< getEndecaPortSize();i++){
			if(getEndecaPort(i) == deadPort){
				if(i==getEndecaPortSize()-1){
					changePortIndex = 0;
				}else{
					changePortIndex = i+1;
				}
				break;
			}
		}
		return getEndecaPort(changePortIndex);
	}
	public static int getEndecaPort(int index){
		return enePort_[index];
	}
	public static boolean dgraphIsAlive(ENEConnection conn) {
		String host = ((HttpENEConnection) conn).getHostname();
		int port = ((HttpENEConnection) conn).getPort();
		try {
			URL localURL = new URL("http", host, port, "/admin?op=stats");
			localURL.getContent();
		} catch (IOException e) {
			logger.error(PLMUtil.getStackTrace(e));
			return false;
		}
		return true;
	}
	private static int[] getEndecaPorts(String endecaPorts) {
		String[] saPorts = endecaPorts.split("\\s");
		int[] iaPorts = null;
		if(saPorts!=null && saPorts.length>0){
			iaPorts = new int[saPorts.length];
			int i = 0;
			for(String s:saPorts){
				iaPorts[i] = Integer.parseInt(s);
				i++;
			}
		}
		return iaPorts;
	}
}