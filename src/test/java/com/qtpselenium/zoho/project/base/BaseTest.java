package com.qtpselenium.zoho.project.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.qtpselenium.zoho.project.util.ExtentManager;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.openqa.selenium.remote.RemoteWebDriver;
public class BaseTest {

	public WebDriver driver;
	public Properties prop;
	public Properties envProp;
	public ExtentReports rep = ExtentManager.getInstance();
	public ExtentTest test;
	boolean gridRun=false;
	public SoftAssert softAssert = new SoftAssert();
	
	public static String classpath = System.getProperty("user.dir");

	public void init() {
		// init the prop file
		if (prop == null) {
			prop = new Properties();
			envProp = new Properties();
			try {
				FileInputStream fs = new FileInputStream(
						System.getProperty("user.dir") + "//src//test//resources//projectconfig.properties");
				prop.load(fs);

				String env = prop.getProperty("env");
				fs = new FileInputStream(
						System.getProperty("user.dir") + "//src//test//resources//" + env + ".properties");
				envProp.load(fs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void openBrowser(String bType) {
		test.log(LogStatus.INFO, "Opening Browser  " + bType);
		
		if(!gridRun){
		if (bType.equals("Mozilla")) {		
			System.setProperty("webdriver.gecko.driver", classpath + "//drivers//geckodriver.exe");	
			System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "null");
			driver = new FirefoxDriver();
			
		} else if (bType.equals("Chrome")) {
			System.setProperty("webdriver.chrome.driver", classpath + "//drivers//chromedriver.exe");
			driver = new ChromeDriver();
			
		} else if (bType.equals("IE")) {
			driver = new InternetExplorerDriver();

		} else if (bType.equals("Edge")) {
			driver = new EdgeDriver();
		}
		}else {
			// grid run
			
						DesiredCapabilities cap=null;
						if(bType.equals("Mozilla")){
							cap = DesiredCapabilities.firefox();
							cap.setBrowserName("firefox");
							cap.setJavascriptEnabled(true);
							cap.setPlatform(org.openqa.selenium.Platform.WINDOWS);
							
						}else if(bType.equals("Chrome")){
							 cap = DesiredCapabilities.chrome();
							 cap.setBrowserName("chrome");
							 cap.setPlatform(org.openqa.selenium.Platform.WINDOWS);
						}
						try {
							driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), cap);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		}
		

		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		test.log(LogStatus.INFO, "Browser opened successfully  " + bType);
	}

	public void navigate(String urlKey) {
		test.log(LogStatus.INFO, "Navigating to " + prop.getProperty(urlKey));
		driver.get(envProp.getProperty(urlKey));
	}

	public void click(String loacatorKey) {
		test.log(LogStatus.INFO, "Clicking on  " + loacatorKey);
		getElement(loacatorKey).click();
		test.log(LogStatus.INFO, "Clicked successfully on  " + loacatorKey);
	}

	public void type(String loacatorKey, String data) {
		test.log(LogStatus.INFO, "Typing in  " + loacatorKey + ". Data-" + data);
		getElement(loacatorKey).sendKeys(data);
		test.log(LogStatus.INFO, "Typed successfully in  " + loacatorKey);
	}

	public void enter(String loacatorKey) {
		WebElement textbox = getElement(loacatorKey);
		textbox.sendKeys(Keys.ENTER);
	}

	// finding element and return
	public WebElement getElement(String loacatorKey) {
		WebElement e = null;
		try {
			if (loacatorKey.endsWith("_id"))
				e = driver.findElement(By.id(prop.getProperty(loacatorKey)));
			else if (loacatorKey.endsWith("_name"))
				e = driver.findElement(By.name(prop.getProperty(loacatorKey)));
			else if (loacatorKey.endsWith("_xpath"))
				e = driver.findElement(By.xpath(prop.getProperty(loacatorKey)));
			else {
				reportFailure("Locator not correct - " + loacatorKey);
				Assert.fail("Locator not correct - " + loacatorKey);
			}

		} catch (Exception ex) {
			// fail the test and report the error
			reportFailure(ex.getMessage());
			ex.printStackTrace();
			Assert.fail("Failed the test - " + ex.getMessage());
		}
		return e;
	}

	/********************************** validations ***************/

	public boolean verifyTitle() {
		return false;
	}

	public boolean isElementPresent(String loacatorKey) {
		List<WebElement> elementList = null;
		if (loacatorKey.endsWith("_id"))
			elementList = driver.findElements(By.id(prop.getProperty(loacatorKey)));
		else if (loacatorKey.endsWith("_name"))
			elementList = driver.findElements(By.name(prop.getProperty(loacatorKey)));
		else if (loacatorKey.endsWith("_xpath"))
			elementList = driver.findElements(By.xpath(prop.getProperty(loacatorKey)));
		else {
			reportFailure("Locator not correct - " + loacatorKey);
			Assert.fail("Locator not correct - " + loacatorKey);
		}

		if (elementList.size() == 0)
			return false;
		else
			return true;
	}

	public boolean verifyText(String loacatorKey, String expectedTextKey) {
		String actualText = getElement(loacatorKey).getText().trim();
		String expectedText = prop.getProperty(expectedTextKey);
		if (actualText.equals(expectedText))
			return true;
		else
			return false;
	}

	public void clickAndWait(String locator_clicked, String locator_press) {
		test.log(LogStatus.INFO, "Clicking and waiting on " + locator_clicked);
		int count = 5;
		for (int i = 0; i < count; i++) {
			getElement(locator_clicked).click();
			wait(2);
			if (isElementPresent(locator_press))
				break;

		}

	}

	/********************* reporting ****************************/
	public void reportPass(String msg) {
		test.log(LogStatus.PASS, msg);
	}

	public void reportFailure(String msg) {
		test.log(LogStatus.FAIL, msg);
		takeScreenshot();
		Assert.fail(msg);

	}

	public void takeScreenshot() {
		// filename of the screenshot
		Date d = new Date();
		String screenshotFile = d.toString().replace(":", "_").replace(" ", "_") + ".png";
		// store screenshot in that file
		File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(srcFile, new File(System.getProperty("user.dir") + "//screenshots//" + screenshotFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// put screenshot file in reports
		test.log(LogStatus.INFO, "Screenshot->"
				+ test.addScreenCapture(System.getProperty("user.dir") + "//screenshots//" + screenshotFile));
	}

	public void acceptAlert() {
		WebDriverWait wait = new WebDriverWait(driver, 7);
		wait.until(ExpectedConditions.alertIsPresent());
		test.log(LogStatus.INFO, "Accepting the alert ");
		driver.switchTo().alert().accept();
		driver.switchTo().defaultContent();
	}

	public void waitForPageToLoad() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String state = (String) js.executeScript("return document.readyState");
		if (!state.equals("complete")) {
			wait(2);
			state = (String) js.executeScript("return document.readyState");

		}
	}

	public void wait(int time) {
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public String getText(String locatorKey) {
		test.log(LogStatus.INFO, "Getting text from " + locatorKey);
		return getElement(locatorKey).getText();

	}

	/************************ App functions ***************************/
	public boolean doLogin(String username, String password) {
		test.log(LogStatus.INFO, "trying to login with " + username + password);
		click("loginLink_xpath");
		// wait(1);
		// wait for page to load
		// waitForPageToLoad();
		// Switch to frame
		// driver.switchTo().frame(0);
		type("loginid_xpath", username);
		type("password_xpath", password);
		click("signinButton_xpath");
		if (isElementPresent("crmlink_xpath"))
			return true;
		else
			return false;

	}

	public int getLeadRowNum(String leadName) {
		test.log(LogStatus.INFO, "Finding the lead " + leadName);
		List<WebElement> leadNames = driver.findElements(By.xpath(prop.getProperty("leadNameCol_xpath")));
		for (int i = 0; i < leadNames.size(); i++) {
			System.out.println(leadNames.get(i).getText());
			if (leadNames.get(i).getText().trim().equals(leadName)) {
				test.log(LogStatus.INFO, "lead found in row num " + (i + 1));
				return (i + 1);
			}
		}
		test.log(LogStatus.INFO, "lead  not found ");
		return -1;
	}

	public int getAccountRowNum(String leadCompany) {
		test.log(LogStatus.INFO, "Finding the converted lead in accounts " + leadCompany);
		List<WebElement> leadCompanies = driver.findElements(By.xpath(prop.getProperty("accountNameCol_xpath")));
		for (int i = 0; i < leadCompanies.size(); i++) {
			System.out.println(leadCompanies.get(i).getText());
			if (leadCompanies.get(i).getText().trim().equals(leadCompany)) {
				test.log(LogStatus.INFO, "converted lead found in accounts row num " + (i + 1));
				return (i + 1);
			}
		}
		test.log(LogStatus.INFO, "lead is not found in accounts ");
		return -1;
	}

	public int getDealRowNum(String potentialName) {
		test.log(LogStatus.INFO, "Finding the Deal " + potentialName);
		List<WebElement> potentialNames = driver.findElements(By.xpath(prop.getProperty("dealNameCol_xpath")));
		for (int i = 0; i < potentialNames.size(); i++) {
			System.out.println(potentialNames.get(i).getText());
			if (potentialNames.get(i).getText().trim().equals(potentialName)) {
				test.log(LogStatus.INFO, "deal found in row num " + (i + 1));
				return (i + 1);
			}
		}
		test.log(LogStatus.INFO, "Deal  not found ");
		return -1;
	}

	public void clickOnLead(String leadName) {
		test.log(LogStatus.INFO, "Clicking the lead " + leadName);
		int rNum = getLeadRowNum(leadName);
		driver.findElement(By.xpath(prop.getProperty("leadpart1_xpath") + rNum + prop.getProperty("leadpart2_xpath")))
				.click();

	}

	public void clickOnDeal(String potentialName) {
		test.log(LogStatus.INFO, "Clicking the Deal " + potentialName);
		int rNum = getDealRowNum(potentialName);
		driver.findElement(By.xpath(prop.getProperty("dealpart1_xpath") + rNum + prop.getProperty("dealpart2_xpath")))
				.click();

	}

	public void selectDate(String d) {
		test.log(LogStatus.INFO, "Selecting the date " + d);
		// convert the string date(input) into date object
		click("datemenu_xpath");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date dateTobeSelected = sdf.parse(d);
			Date currentDate = new Date();
			sdf = new SimpleDateFormat("MMMM");
			String monthTobeSelected = sdf.format(dateTobeSelected);
			sdf = new SimpleDateFormat("yyyy");
			String yearTobeSelected = sdf.format(dateTobeSelected);
			sdf = new SimpleDateFormat("d");
			String dayToBeSelected = sdf.format(dateTobeSelected);
			// June 2016
			String monthyearTobeSelected = monthTobeSelected + " " + yearTobeSelected;

			while (true) {
				if (currentDate.compareTo(dateTobeSelected) == 1) {
					// back
					click("back_xpath");
				} else if (currentDate.compareTo(dateTobeSelected) == -1) {
					// front
					click("forward_xpath");
				}

				if (monthyearTobeSelected.equals(getText("monthYearDisplayed_xpath"))) {
					break;
				}
			}
			driver.findElement(By.xpath("//td[text()='" + dayToBeSelected + "']")).click();
			test.log(LogStatus.INFO, "Date Selection Successful " + d);

		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
}
