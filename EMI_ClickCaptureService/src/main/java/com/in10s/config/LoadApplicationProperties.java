package com.in10s.config;

import com.in10s.common.WFPropertyManager;
import com.in10s.listener.ApplicationListener;
import com.in10s.applog.AppLogger;
import org.apache.log4j.xml.DOMConfigurator;


public class LoadApplicationProperties {

	public static String LOGGER_ROOTPATH = "";
	public static String LOGGER_LEVEL = "";
	public static String LOGGER_MAXSIZE = "";
	
	public static String QUERY_TRANS_REFNUM_COUNT = "";
	public static String QUERY_CAPTURE_EMIREQUEST = "";
	public static String QUERY_AUDIT_EMIREQUEST = "";
	public static String QUERY_AUDIT_EMIREQUEST_FAIL = "";
	
	public static String ERROR_TEXT_1 = "";
	public static String ERROR_TEXT_2 = "";
	public static String ERROR_TEXT_3 = "";
	public static String ERROR_TEXT_4 = "";
	public static String ERROR_TEXT_5 = "";
	public static String ERROR_TEXT_6 = "";
	public static String ERROR_TEXT_7 = "";
	public static String ERROR_TEXT_8 = "";
	public static String ERROR_TEXT_9 = "";
	
	public static String SUCCESS_MESSAGE = "";
	
       
    public LoadApplicationProperties() {
    }

    public boolean loadProperties() {
        
    	boolean bLoadPropStatus = false;
    	
    	
        try {
        	
            synchronized (LoadApplicationProperties.class) {
            	
                System.out.println("EMI_ClickCaptureService - Loading application properties");
                
        			LOGGER_ROOTPATH = WFPropertyManager.getInstance().getWfPropertyMap().get("CLICKCAPTURE_SERVICE").getString("LOGGER_ROOTPATH").trim();
        			System.out.println("log path : "+LOGGER_ROOTPATH);
        			LOGGER_LEVEL = WFPropertyManager.getInstance().getWfPropertyMap().get("CLICKCAPTURE_SERVICE").getString("LOGGER_LEVEL").trim();
        			LOGGER_MAXSIZE = WFPropertyManager.getInstance().getWfPropertyMap().get("CLICKCAPTURE_SERVICE").getString("LOGGER_MAXSIZE").trim();
        			
        			QUERY_TRANS_REFNUM_COUNT = WFPropertyManager.getInstance().getWfPropertyMap().get("CLICKCAPTURE_SERVICE").getString("QUERY_TRANS_REFNUM_COUNT").trim();
        		    QUERY_CAPTURE_EMIREQUEST = WFPropertyManager.getInstance().getWfPropertyMap().get("CLICKCAPTURE_SERVICE").getString("QUERY_CAPTURE_EMIREQUEST").trim();
        		    QUERY_AUDIT_EMIREQUEST = WFPropertyManager.getInstance().getWfPropertyMap().get("CLICKCAPTURE_SERVICE").getString("QUERY_AUDIT_EMIREQUEST").trim();
        		    QUERY_AUDIT_EMIREQUEST_FAIL = WFPropertyManager.getInstance().getWfPropertyMap().get("CLICKCAPTURE_SERVICE").getString("QUERY_AUDIT_EMIREQUEST_FAIL").trim();
        		    
        		    ERROR_TEXT_1 = WFPropertyManager.getInstance().getWfPropertyMap().get("CLICKCAPTURE_SERVICE").getString("ERROR_TEXT_1").trim();
        		    ERROR_TEXT_2 = WFPropertyManager.getInstance().getWfPropertyMap().get("CLICKCAPTURE_SERVICE").getString("ERROR_TEXT_2").trim();
        		    ERROR_TEXT_3 = WFPropertyManager.getInstance().getWfPropertyMap().get("CLICKCAPTURE_SERVICE").getString("ERROR_TEXT_3").trim();
        			ERROR_TEXT_4 = WFPropertyManager.getInstance().getWfPropertyMap().get("CLICKCAPTURE_SERVICE").getString("ERROR_TEXT_4").trim();
        		    ERROR_TEXT_5 = WFPropertyManager.getInstance().getWfPropertyMap().get("CLICKCAPTURE_SERVICE").getString("ERROR_TEXT_5").trim();
        		    ERROR_TEXT_6 = WFPropertyManager.getInstance().getWfPropertyMap().get("CLICKCAPTURE_SERVICE").getString("ERROR_TEXT_6").trim();
        			ERROR_TEXT_7 = WFPropertyManager.getInstance().getWfPropertyMap().get("CLICKCAPTURE_SERVICE").getString("ERROR_TEXT_7").trim();
        			ERROR_TEXT_8 = WFPropertyManager.getInstance().getWfPropertyMap().get("CLICKCAPTURE_SERVICE").getString("ERROR_TEXT_8").trim();
        			ERROR_TEXT_9 = WFPropertyManager.getInstance().getWfPropertyMap().get("CLICKCAPTURE_SERVICE").getString("ERROR_TEXT_9").trim();
        		    
        		    SUCCESS_MESSAGE = WFPropertyManager.getInstance().getWfPropertyMap().get("CLICKCAPTURE_SERVICE").getString("SUCCESS_MESSAGE").trim();
        		    
//        		    SPLITSYMBOL = WFPropertyManager.getInstance().getWfPropertyMap().get("CLICKCAPTURE_SERVICE").getString("SPLITSYMBOL").trim();
        		    
        			System.setProperty("clickCapture_logger_RootPath", LOGGER_ROOTPATH);
        			System.setProperty("clickCapture_logger_Level", LOGGER_LEVEL);
        			System.setProperty("clickCapture_logger_MaxSize", LOGGER_MAXSIZE);
        			
        			DOMConfigurator.configure(ApplicationListener.strLogFilePath);
                    
        			System.out.println("EMI_ClickCaptureService - Application properties loaded successfully");
        			bLoadPropStatus = true;
        			              
               
            }
            
        } catch (Exception ex) {
        	
            System.out.println("Excpeion : "+ex);
            
        }
        return bLoadPropStatus;
    }
}

