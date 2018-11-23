package com.in10s.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.in10s.applog.AppLogger;
import com.in10s.config.LoadApplicationProperties;
import com.in10s.common.BoneCPDBPool;

public class ApplicationListener implements ServletContextListener{

	public static String strConfigurationFilePath = "";
	public static String strLogFilePath = "";
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		System.out.println("EMI_ClickCaptureService - Application Context Initialized ");
		ClassLoader classLoader = null;
		LoadApplicationProperties objLoadProperties = null;
		
		try {
			
			classLoader = getClass().getClassLoader();
			strConfigurationFilePath = classLoader.getResource("Service_Configuration.properties").getFile();
			strLogFilePath = classLoader.getResource("log4j.xml").getFile();
			
			objLoadProperties = new LoadApplicationProperties();
			
			if(objLoadProperties.loadProperties()){
				
				AppLogger.info("Application properties loaded Successfully");
								
			} else {
				
				System.out.println("EMI_ClickCaptureService - Failed to load application properties");
								
			}
			
		} catch (Exception e) {
			
			System.out.println("EMI_ClickCaptureService - Exception occurred while loading application properties");
			
		}
		
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
		AppLogger.info("Shutting down the BoneCPDBPool Connection");
        BoneCPDBPool.shutdown();
		
	}

}
