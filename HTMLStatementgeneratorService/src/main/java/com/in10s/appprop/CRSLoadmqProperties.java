package com.in10s.appprop;

import com.in10s.dao.CAuthenticate;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.in10s.dao.WFPropertyManager;
import com.in10s.listener.ApplicationListener;

import java.util.Map;

import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author jagadeswara
 */
public class CRSLoadmqProperties {

    public static CRSLoadmqProperties objLoadproperties = null;
    public static String query = "";
    public static String query_print = "";
    public static String html_job_command = "";
    public static String atn_serverip = "";
    public static String atn_serverport = "";
    public static String for_serverip = "";
    public static String fos_serverport = "";
    public static String atn_username = "";
    public static String atn_password = "";
    public static String connpool_size = "";
    public static String timeout = "";
    public static String orgid = ""; 
    public static String atn_password_flag=""; 
    public static String jobkeyregex = "";
    //public static String ispassEncrypted = "";
    public static String tokentime = "";
    public static String tokenenable = "";
    public static String statment_Format = "";
    public static String special_Char_To_Split = "";
   
    public static String DEEPLINK_REQUESTTOKEN_SEQ = "";
    public static String YESBANKDEEPLINKSEQ = "";
    //public static String QUERYFETCHMOFKEY = "";
    public static String QUERY_FETCH_PASSWORD = "";
    public static String QUERY_DEEPLINK_AUDIT = "";
    public static String QUERY_INTERNAL_SEQUENCE = "";
    public static String QUERY_SESSION_EXPIRY = "";
    
    public static String QUERY_VALIDATE_SESSIONID = "";
	public static String QUERY_UPDATE_PROCESSFLAG = "";
	public static String LOGGER_ROOTPATH = "";
	public static String LOGGER_LEVEL = "";
	public static String LOGGER_MAXSIZE = "";
	public static String JSON_REPLACEING_VALUES_INJOB_COMMOND = "";
	
	public static String QUERY_FETCH_PASSWORD_PRINT_JOB = "";
	
    public static Map<String,Float> html5_compatable_browsers_map;
       
    public CRSLoadmqProperties() {
    }

    public boolean loadProperties() {
        
    	boolean bLoadPropStatus = false;
    	
    	
        try {
        	
            synchronized (CRSLoadmqProperties.class) {
            	
                System.out.println("HTMLStatementgeneratorService - Loading application properties");
                if (objLoadproperties == null) {
                    objLoadproperties = new CRSLoadmqProperties();

                    LOGGER_ROOTPATH = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("LOGGER_ROOTPATH").trim();
        			LOGGER_LEVEL = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("LOGGER_LEVEL").trim();
        			LOGGER_MAXSIZE = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("LOGGER_MAXSIZE").trim();
        			
        			System.out.println("log path : "+LOGGER_ROOTPATH);
                    
                    DEEPLINK_REQUESTTOKEN_SEQ = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("DEEPLINK_REQUESTTOKEN_SEQ").trim();
                    YESBANKDEEPLINKSEQ = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("YESBANKDEEPLINKSEQ").trim();
                    query = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("QUERY").trim();
                    query_print = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("QUERY_PRINT").trim();
                    // QUERYFETCHMOFKEY = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("FETCHMOFKEY").trim();
                    html_job_command = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("UNISERVE_JOB_COMMAND").trim();
                    atn_serverip = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("ATN_SERVER_IP").trim();
                    atn_serverport = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("ATN_SERVER_PORT").trim();
                    for_serverip = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("FOS_SERVER_IP").trim();
                    fos_serverport = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("FOS_SERVER_PORT").trim();
              
                    atn_username = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("ATN_USERNAME").trim();

                    atn_password = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("ATN_PASSWORD").trim();
                    atn_password_flag=WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("ATN_PASSWORD_FLAG").trim();
                    jobkeyregex=WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("JOBKEYREGEX").trim();
                    //ispassEncrypted = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("PASSWORD_FLAG").trim();
                    if(atn_password_flag.equals("true")){
                        atn_password=new CAuthenticate().Decrypt(atn_password);
                        
                    }
                    statment_Format = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("STATEMENT_FORMAT").trim();
                    connpool_size = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("CONNPOOL_SIZE").trim();
                    timeout = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("TIMEOUT").trim();
                    orgid = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("ORG_ID").trim();
                    tokentime = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("TOKENTIME").trim();
                    tokenenable = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("TOKENENABLE").trim();
                    html5_compatable_browsers_map=WFPropertyManager.getInstance().getHtml5_compatable_browsers_map();
                    
                    QUERY_FETCH_PASSWORD=WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("FETCH_PASSWORD").trim();
                    QUERY_DEEPLINK_AUDIT = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("QUERY_DEEPLINK_AUDIT").trim();
                    QUERY_INTERNAL_SEQUENCE = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("QUERY_INTERNAL_SEQUENCE").trim();
                    QUERY_VALIDATE_SESSIONID = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("QUERY_VALIDATE_SESSIONID").trim();
        			QUERY_UPDATE_PROCESSFLAG = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("QUERY_UPDATE_PROCESSFLAG").trim();
        			JSON_REPLACEING_VALUES_INJOB_COMMOND = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("JSON_REPLACEING_VALUES_INJOB_COMMOND").trim(); 
        			QUERY_SESSION_EXPIRY = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("QUERY_SESSION_EXPIRY").trim();
        			special_Char_To_Split =  WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("SPECIAL_CHAR_TO_SPLIT").trim();    			
        			
        			QUERY_FETCH_PASSWORD_PRINT_JOB = WFPropertyManager.getInstance().getWfPropertyMap().get("HTMLSTATEMENT_GENERATOR").getString("QUERY_FETCH_PASSWORD_PRINT_JOB").trim();
        			
        			System.setProperty("htmlStatement_logger_RootPath", LOGGER_ROOTPATH);
        			System.setProperty("htmlStatement_logger_Level", LOGGER_LEVEL);
        			System.setProperty("htmlStatement_logger_MaxSize", LOGGER_MAXSIZE);        			        			
                    
        			DOMConfigurator.configure(ApplicationListener.strLogFilePath);
        			
        			bLoadPropStatus = true;
        			
        			System.out.println("HTMLStatementgeneratorService - Application properties loaded successfully");
                    System.out.println("avilable browser details::"+html5_compatable_browsers_map);

                    System.out.println("timeout--------------> "+timeout+" atn server ip--> " +atn_serverip+" atn port--->"+atn_serverport+" ord id "+orgid +" atnusername--->"+atn_username);
                                    
                }
            }
        } catch (Exception ex) {
        	
            System.out.println("Exception occurred while loading the application properties : "+ex);
            ex.printStackTrace();
            
        }
        return bLoadPropStatus;
    }
}

