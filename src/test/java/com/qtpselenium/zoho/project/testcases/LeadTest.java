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

public class LeadTest extends BaseTest {

	Xls_Reader xls;
	SoftAssert softAssert;

	@Test(priority = 1, dataProvider = "getData")
	public void createLeadTest(Hashtable<String, String> data) {
		test = rep.startTest("CreateLeadTest");
		if (!DataUtil.isRunnable("CreateLeadTest", xls) || data.get("Runmode").equals("N")) {
			test.log(LogStatus.SKIP, "Skipping the test as runmode is N");
			throw new SkipException("Skipping the test as runmode is N");
		}

		openBrowser(data.get("Browser"));
		navigate("appurl");
		doLogin(envProp.getProperty("username"), envProp.getProperty("password"));
		click("crmlink_xpath");
		click("leadsTab_xpath");
		click("newLeadButton_xpath");
		type("leadCompany_xpath", data.get("LeadCompany"));
		type("leadLastName_xpath", data.get("LeadLastName"));
		click("leadSaveButton_xpath");
		clickAndWait("leadsTab_xpath", "newLeadButton_xpath");

		// validate
		int rNum = getLeadRowNum(data.get("LeadLastName"));
		if (rNum == -1) {
			reportFailure("Lead not found in lead table " + data.get("LeadLastName"));
			takeScreenshot();
		}
		reportPass("Lead found in lead table " + data.get("LeadLastName"));
		takeScreenshot();

	}

	@Test(priority = 2, dataProvider = "getDataConvertLead")
	public void convertLeadTest(Hashtable<String, String> data) {
		test = rep.startTest("ConvertLeadTest");
		if (!DataUtil.isRunnable("ConvertLeadTest", xls) || data.get("Runmode").equals("N")) {
			test.log(LogStatus.SKIP, "Skipping the test as runmode is N");
			throw new SkipException("Skipping the test as runmode is N");
		}
		openBrowser(data.get("Browser"));
		navigate("appurl");
		doLogin(envProp.getProperty("username"), envProp.getProperty("password"));
		click("crmlink_xpath");
		click("leadsTab_xpath");
		clickOnLead(data.get("LeadLastName"));
		click("convertLead_xpath");
		// click("createNewAccountButton_xpath");
		click("convertLeadandSave_xpath");

		click("gotoLeadBotton_xpath");
		waitForPageToLoad();
		// validate
		int rNum = getLeadRowNum(data.get("LeadLastName"));
		if (rNum != -1)
			reportFailure("Lead found in lead table " + data.get("LeadLastName"));

		reportPass("Lead not found in lead table " + data.get("LeadLastName"));

		takeScreenshot();

		click("accountsTab_xpath");

		rNum = getAccountRowNum(data.get("LeadCompany"));
		if (rNum == -1)
			reportFailure("Lead not found in accounts table " + data.get("LeadCompany"));

		reportPass("Lead found in accounts table " + data.get("LeadCompany"));
		takeScreenshot();

	}

	@Test(priority = 3, dataProvider = "getDataDeleteLead")
	public void deleteLeadAccountTest(Hashtable<String, String> data) {

		test = rep.startTest("DeleteLeadAccountTest");
		test.log(LogStatus.INFO, data.toString());
		if (!DataUtil.isRunnable("DeleteLeadAccountTest", xls) || data.get("Runmode").equals("N")) {
			test.log(LogStatus.SKIP, "Skipping the test as runmode is N");
			throw new SkipException("Skipping the test as runmode is N");
		}

		openBrowser(data.get("Browser"));
		navigate("appurl");
		doLogin(envProp.getProperty("username"), envProp.getProperty("password"));
		click("crmlink_xpath");
		click("leadsTab_xpath");
		clickOnLead(data.get("LeadLastName"));
		waitForPageToLoad();
		click("dropdownmenu_xpath");
		click("deleteLead_xpath");
		// acceptAlert();
		click("deleteConfirm_xpath");
		wait(4);
		// click("backButtonLead_xpath");
		click("leadsTab_xpath");
		int rNum = getLeadRowNum(data.get("LeadLastName"));
		if (rNum != -1)
			reportFailure("Could not delete the lead");

		reportPass("Lead deleted successfully");
		takeScreenshot();

		
	}

	@DataProvider
	public Object[][] getData() {
		super.init();
		xls = new Xls_Reader(prop.getProperty("xlspath"));
		return DataUtil.getTestData(xls, "CreateLeadTest");
	}

	@DataProvider
	public Object[][] getDataConvertLead() {
		super.init();
		xls = new Xls_Reader(prop.getProperty("xlspath"));
		return DataUtil.getTestData(xls, "ConvertLeadTest");
	}

	@DataProvider
	public Object[][] getDataDeleteLead() {
		super.init();
		xls = new Xls_Reader(prop.getProperty("xlspath"));
		Object[][] data = DataUtil.getTestData(xls, "DeleteLeadAccountTest");
		return data;

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
		if (driver != null)
			driver.quit();
	}

}
