package com.plm.endeca;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.endeca.edf.adapter.Adapter;
import com.endeca.edf.adapter.AdapterConfig;
import com.endeca.edf.adapter.AdapterException;
import com.endeca.edf.adapter.AdapterHandler;
import com.endeca.edf.adapter.PVal;
import com.endeca.edf.adapter.Record;
import com.endeca.edf.adapter.ShutdownRequestAdapterException;

public class PLMJavaManipulator implements Adapter {
	private Logger logger = Logger.getLogger(this.getClass().getName());

	public void execute(AdapterConfig config, AdapterHandler handler) throws AdapterException{		
		try{
			//loop through all input sources of this manipulator
			if (logger.isLoggable(Level.INFO)) logger.info("PLMManipulator :: execute() :: starts");
			if (logger.isLoggable(Level.INFO)) logger.info("PLMManipulator :: execute() :: handler.getNumInputs() = "+handler.getNumInputs());
			for (int inp = 0; inp != handler.getNumInputs(); inp++){
				Record rec;
//				int recCounter = 0;				
				while ((rec = handler.getRecord(inp)) != null){					
					if (rec != null){
						int recSize = rec.size();	
						String propVal="";
						String propName="";
						String sortedPrevAddresses ="";
						String sortedJob ="";
						//recCounter = recCounter+1;
						for (int z = 0; z < recSize; z++){
							//get each property
							PVal prop = rec.get(z);
							propName = prop.getName();
							propVal = prop.getValue();	
							if(propName.equalsIgnoreCase("ADD_CHANGED_DATE")){	
								if(propVal != null && !propVal.equals("")){
									rec.add(new PVal("HAS_ADDRESS_CHANGED","Y"));					
								} else {
									rec.add(new PVal("HAS_ADDRESS_CHANGED","N"));	
								}								
							}
							if(propName.equals("PREV_ADDRESSES") && propVal != null && !propVal.equals("")){
								sortedPrevAddresses = sortedPrevAddresses+ propVal+"@@";								
							}
							if(propName.equals("EMPLOYER_INFO") && propVal != null && !propVal.equals("")){
								sortedJob = sortedJob+ propVal+"@@";								
							}
							/*if(propName.equals("COUNTY_NAME") && (propVal == null || propVal.trim().length()==0)){
								rec.get(z).setValue("-No County-");
							}*/
						}
						if(sortedPrevAddresses != null && !sortedPrevAddresses.equals("") )
							rec.add(new PVal("SORTED_PREV_ADDRESS",sortedPrevAddresses));
						
						if(sortedJob != null && !sortedJob.equals(""))
							rec.add(new PVal("SORTED_JOB_INFO",sortedJob));
						
						handler.emit(rec);
					}						
				}
			}
		}
		catch (ShutdownRequestAdapterException e) {
			e.printStackTrace();
			throw e;
		}
		catch (AdapterException e) {
			e.printStackTrace();
			throw new AdapterException(e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			throw new AdapterException(e.getMessage());
		}
		if (logger.isLoggable(Level.INFO)) logger.info("PLMManipulator :: execute() :: ends");
	}//end of method execute()
}
