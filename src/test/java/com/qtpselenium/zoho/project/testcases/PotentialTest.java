package com.qtpselenium.zoho.project.testcases;

import java.util.Hashtable;


import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;


import com.qtpselenium.zoho.project.base.BaseTest;
import com.qtpselenium.zoho.project.util.DataUtil;
import com.qtpselenium.zoho.project.util.Xls_Reader;
import com.relevantcodes.extentreports.LogStatus;

public class PotentialTest extends BaseTest {
	SoftAssert softAssert;
	Xls_Reader xls;

	@Test(priority = 1, dataProvider = "getData")
	public void createPotentialTest(Hashtable<String, String> data) {
		test = rep.startTest("CreatePotentialTest");
		test.log(LogStatus.INFO, data.toString());

		if (!DataUtil.isRunnable("CreatePotentialTest", xls) || data.get("Runmode").equals("N")) {
			test.log(LogStatus.SKIP, "Skipping the test as runmode is N");
			throw new SkipException("Skipping the test as runmode is N");
		}
		openBrowser(data.get("Browser"));
		navigate("appurl");
		doLogin(envProp.getProperty("username"), envProp.getProperty("password"));
		click("crmlink_xpath");
		click("dealsTab_xpath");
		click("newdealButton_xpath");
		type("dealName_xpath", data.get("PotentialName"));
		type("accountName_xpath", data.get("AccountName"));
		click("stagemenu_xpath");
		type("stageinput_xpath", data.get("Stage"));
		enter("stageinput_xpath");
		selectDate(data.get("ClosingDate"));
		click("saveDealButton_xpath");
		clickAndWait("dealsTab_xpath", "newdealButton_xpath");
		// validate
		int rNum = getDealRowNum(data.get("PotentialName"));
		if (rNum == -1)
			reportFailure("Potential not found in deal table " + data.get("PotentialName"));

		reportPass("Potential found in deal table " + data.get("PotentialName"));
		takeScreenshot();
		
		reportPass("Test Passed");

	}
	

	@Test(priority = 2,dataProvider = "getData")
	public void deletePotentialAccountTest(Hashtable<String, String> data) {
		test = rep.startTest("deletePotentialAccountTest");
		test.log(LogStatus.INFO, data.toString());
		if (!DataUtil.isRunnable("CreatePotentialTest", xls) || data.get("Runmode").equals("N")) {
			test.log(LogStatus.SKIP, "Skipping the test as runmode is N");
			throw new SkipException("Skipping the test as runmode is N");
		}
		openBrowser(data.get("Browser"));
		navigate("appurl");
		doLogin(envProp.getProperty("username"), envProp.getProperty("password"));
		click("crmlink_xpath");
		click("dealsTab_xpath");
		clickOnDeal(data.get("PotentialName"));
		waitForPageToLoad();
		click("dealDropMenu_xpath");
		click("deleteButton_xpath");
		click("deleteConfirm_xpath");
		wait(4);
		click("dealsTab_xpath");
		int rNum = getDealRowNum(data.get("PotentialName"));
		if (rNum != -1)
			reportFailure("Potential found in deal table " + data.get("PotentialName"));

		reportPass("Potential not found in deal table " + data.get("PotentialName"));
		takeScreenshot();
		
		reportPass("Test Passed");
	}

	
	@DataProvider
	public Object[][] getData() {
		super.init();
		xls = new Xls_Reader(prop.getProperty("xlspath"));
		return DataUtil.getTestData(xls, "CreatePotentialTest");
	}

	@BeforeMethod
	public void init() {
		softAssert = new SoftAssert();
	}

	@AfterMethod
	public void quit() {
		try {
			softAssert.assertAll();
		} catch (Error e) {
			test.log(LogStatus.FAIL, e.getMessage());
		}
		if (rep != null) {
			rep.endTest(test);
			rep.flush();
		}
	if(driver!=null)
		 driver.quit();
	}

}
