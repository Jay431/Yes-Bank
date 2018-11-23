package com.in10s.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.in10s.applog.AppLogger;
import com.in10s.appprop.CRSLoadmqProperties;
import com.in10s.dao.BoneCPDBPool;

public class ApplicationListener implements ServletContextListener{

	public static String strConfigurationFilePath = "";
	public static String strErrorCodesFilePath = "";
	public static String strLogFilePath = "";
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		System.out.println("HTMLStatementgeneratorService - Application Context Initialized ");
		ClassLoader classLoader = null;
		CRSLoadmqProperties objLoadProperties = null;
		
		try {
			
			classLoader = getClass().getClassLoader();
			strConfigurationFilePath = classLoader.getResource("Service_Configuration.properties").getFile();
			strErrorCodesFilePath = classLoader.getResource("ERRORCODES.xml").getFile();
			strLogFilePath = classLoader.getResource("log4j.xml").getFile();
			
			objLoadProperties = new CRSLoadmqProperties();
			
			if(objLoadProperties.loadProperties()){
				
				AppLogger.info("Application properties loaded Successfully");
								
			} else {
				
				System.out.println("HTMLStatementgeneratorService - Failed to load application properties");
								
			}
			
		} catch (Exception e) {
			
			System.out.println("HTMLStatementgeneratorService - Exception occurred while loading application properties");
			
		}
		
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
		AppLogger.info("Shutting down the BoneCPDBPool Connection");
        BoneCPDBPool.shutdown();
		
	}

}
