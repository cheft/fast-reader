package com.topway.reader.server.convert;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ConvertListener implements ServletContextListener {

	
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("Application Exit ... ");
	}
	
	public void contextInitialized(ServletContextEvent arg0) {
		ConvertThread convertThread = new ConvertThread();
		convertThread.start();
	}
	
}
