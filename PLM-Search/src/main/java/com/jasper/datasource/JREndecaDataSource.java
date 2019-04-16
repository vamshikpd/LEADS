package com.jasper.datasource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import org.apache.log4j.Logger;

import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.Navigation;
import com.endeca.navigation.PropertyContainer;
import com.endeca.ui.UnifiedPropertyMap;
import com.plm.util.PLMJasperEndecaUtil;
import com.plm.util.database.PLMDatabaseUtil;


public class JREndecaDataSource implements JRDataSource {
	private ENEQueryResults qr;
	private boolean isSingleResult = false;
	Navigation nav = null;
	short rowcount = 1;
	Iterator recIter = null;
	UnifiedPropertyMap properties = null;
	private static final Logger logger = Logger.getLogger(JREndecaDataSource.class);

	public JREndecaDataSource(ENEQueryResults qr, boolean isSingleResult)
	{
		this.qr = qr;
		this.isSingleResult = isSingleResult;
		if(!isSingleResult){
    		nav = qr.getNavigation();
    		int rows = 0;
    		rows = (int)nav.getTotalNumERecs();
    		List reclist = new ArrayList(rows);
    		Iterator bulkIterator = nav.getBulkERecIter();
    		recIter = bulkIterator;
    	}
	}
	
	@Override
	public Object getFieldValue(JRField field) throws JRException {
		logger.debug("JR property: " + field.getName());
		String sFieldName = PLMJasperEndecaUtil.getEndecaProperty(field.getName());
		logger.debug("Endeca property: " + sFieldName);
		String propValue = (String)properties.get(sFieldName);
		logger.debug("Endeca value: " + propValue);
		return propValue;
	}

	@Override
	public boolean next() throws JRException {
		boolean hasNext = false;
		if (this.recIter != null){
			hasNext = recIter.hasNext();
			if (hasNext){
				PropertyContainer record = (PropertyContainer)recIter.next();
				properties = new UnifiedPropertyMap(record);
				//String sFieldName = PLMJasperEndecaUtil.getEndecaProperty("CDCNumber");
				//String propValue = (String)properties.get(sFieldName);
				//properties.put("PrimaryMugShot", PLMDatabaseUtil.getPhoto(propValue, "p", defaultPhoto));
                rowcount++;
			}
		}
		return hasNext;
	}

}
