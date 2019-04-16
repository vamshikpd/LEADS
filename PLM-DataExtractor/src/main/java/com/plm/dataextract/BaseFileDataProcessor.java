package com.plm.dataextract;

import java.util.Hashtable;

import javax.swing.event.EventListenerList;

import com.plm.dataextract.cfg.BaseFieldConfig;
import com.plm.dataextract.cfg.ConfigDataProvider;
import com.plm.dataextract.cfg.DataExtractConfig;
import com.plm.dataextract.formatter.IFormatter;

public class BaseFileDataProcessor {

	private EventListenerList listenerList = new EventListenerList();
    private transient FileProcessEvent processEvent = null;
    private static Hashtable<String, IFormatter> htFormatter = new Hashtable<String, IFormatter>();

    public BaseFileDataProcessor(String extractMode) {
    	if(htFormatter != null && htFormatter.size() <= 0) {
	    	DataExtractConfig deConfig = ConfigDataProvider.getInstance().getDataExtractConfig();
	    	htFormatter.putAll(deConfig.getFormatters());
	    	//FOR DEBUG ONLY
	    	for(String key : htFormatter.keySet() ) {
	    		System.out.println("Formatters Key: " + key + " - " + htFormatter.get(key).getClass().getName() );
	    	}
    	}
	}

    /**
     * Adds a <code>FileProcessorListener</code> to the listener list.
     * @param l  the new listener to be added
     */
    public void addFileProcessorListener(FileProcessorListener l) {
    	listenerList.add(FileProcessorListener.class, l);
    }

    /**
     * Removes a <code>FileProcessorListener</code> from the listener list.
     * @param l  the listener to be removed
     */
    public void removeFileProcessorListener(FileProcessorListener l) {
    	listenerList.remove(FileProcessorListener.class, l);
    }
    
    protected void fireProcessingStart() {
    	// Guaranteed to return a non-null array
    	Object[] listeners = listenerList.getListenerList();
    	// Process the listeners last to first, notifying
    	// those that are interested in this event
    	for (int i = listeners.length-2; i>=0; i-=2) {
    	    if (listeners[i]==FileProcessorListener.class) {
    		// Lazily create the event:
    		if (processEvent == null)
    			processEvent = new FileProcessEvent(this);
    			((FileProcessorListener)listeners[i+1]).startProcessingFile(processEvent);
    	    }	       
    	}
	}
	
    protected void fireProcessingStop() {
    	// Guaranteed to return a non-null array
    	Object[] listeners = listenerList.getListenerList();
    	// Process the listeners last to first, notifying
    	// those that are interested in this event
    	for (int i = listeners.length-2; i>=0; i-=2) {
    	    if (listeners[i]==FileProcessorListener.class) {
    		// Lazily create the event:
    		if (processEvent == null)
    			processEvent = new FileProcessEvent(this);
    			((FileProcessorListener)listeners[i+1]).stopProcessingFile(processEvent);
    	    }	       
    	}
	}

    public IFormatter getFormatter(String formatterName) {
    	if( formatterName != null && htFormatter.containsKey(formatterName) ) {
    		return htFormatter.get(formatterName);
    	}
    	return htFormatter.get(BaseFieldConfig.STRING_FORMATTER_NAME);
    }

}
