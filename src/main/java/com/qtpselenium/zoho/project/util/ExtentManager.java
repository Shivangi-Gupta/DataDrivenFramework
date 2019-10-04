package com.qtpselenium.zoho.project.util;

import java.io.File;
import java.util.Date;

import com.relevantcodes.extentreports.DisplayOrder;
import com.relevantcodes.extentreports.ExtentReports;

public class ExtentManager {
	private static ExtentReports extent;
	
	public static ExtentReports getInstance() {
		if(extent==null) {
			Date d=new Date();
			String fileName=d.toString().replace(":","_").replace(" ", "_")+".html";
			extent=new ExtentReports(System.getProperty("user.dir")+"\\report1\\"+fileName,true,DisplayOrder.NEWEST_FIRST);
			
			
			extent.loadConfig(new File(System.getProperty("user.dir")+"//Reportsconfig.xml"));
			extent.addSystemInfo("Selenium Version","3.141.59").addSystemInfo("Environment", "QA");
		}
		return extent;
	}
	

}
