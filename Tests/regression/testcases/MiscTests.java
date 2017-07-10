package regression.testcases;

/**
 * History:
 * NOV 11, 2015	(Lei Wang) Added method testBrowserTabs().
 * JUN 07, 2017 (Lei Wang) Added method testSwitchWindow_Internal().
 *
 */
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.safs.StatusCodes;
import org.safs.StringUtils;
import org.safs.selenium.webdriver.SeleniumPlus;
import org.safs.selenium.webdriver.lib.SelectBrowser;
import org.safs.selenium.webdriver.lib.WDLibrary;
import org.safs.text.FileUtilities;

import regression.Map;
import regression.testruns.Regression;
import regression.util.Utilities;

public class MiscTests extends Regression{

	public static final String COUNTER = StringUtils.getClassName(0, false);

	/** "MiscTests.MAP" */
	public static final String MISCTESTS_APPMAP = "MiscTests.MAP";

	/** "MiscTests_newGoogleLogin.map" */
	public static final String MISCTESTS_NEW_GOOGLE_LOGIN_APPMAP = "MiscTests_newGoogleLogin.map";

	/**
	 * ApplicationConstant GoogleURL: User must specify "http://.." protocol in GoogleURL.<br>
	 * ApplicationConstant GoogleBrowser: specifies the AppID/SessionID to use for the session.
	 * @param browser type Regression.IE, Regression.FF, Regression.CH, or null.
	 * Defaults to FireFox if null.
	 * @throws Exception if the StartBrowser command did not work
	 */
	public static int startGoogle(String counterPrefix, String browser) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);

		if(browser==null) browser = SelectBrowser.BROWSER_NAME_FIREFOX;
		Regression.browserID = Map.GoogleBrowser();
		if (! StartWebBrowser(Map.GoogleURL(), Regression.browserID, browser)){
			fail++;

			Counters.StopCounter(counterID);
			Counters.StoreCounterInfo(counterID, counterID);
			Counters.LogCounterInfo(counterID);

			AbortTest("StartWebBrowser Unsuccessful. Cannot Proceed.");
		}

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);

		if(fail > 0){
			Logging.LogTestFailure(counterID + " reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(counterID + " did not report any UNEXPECTED test failures!");
		}

		return fail;
	}

	/**
	 *
	 * ApplicationConstant BusyDialogURL<br>
	 * ApplicationConstant HoneycombBrowser<br>
	 * Defaults to using FireFox.
	 * @throws Throwable
	 */
	public static void startBusyDialog() throws Throwable{
		if (! StartWebBrowser(Map.BusyDialogURL(), Map.HoneycombBrowser(),  SelectBrowser.BROWSER_NAME_FIREFOX))
			AbortTest("StartWebBrowser Unsuccessful. Cannot Proceed.");
	}

	public static void startHoneycomb(String url, String browser) throws Throwable{
		if(browser==null) browser = SelectBrowser.BROWSER_NAME_FIREFOX;
		if (! StartWebBrowser(url, Map.HoneycombBrowser(),  browser))
			AbortTest("StartWebBrowser Unsuccessful. Cannot Proceed.");
	}

	public static void stopBusyDialog() throws Throwable{
		stopHoneycomb();
	}

	public static void stopHoneycomb() throws Throwable{
		StopWebBrowser(Map.HoneycombBrowser());
	}

	/**
	 *  Take picture
	 *  See Map.LogIn.SignIn for RS
	 */
	public static void takeScreenshot(){
		String filename = Map.SignInScreenshot();
		GetGUIImage(Map.LogIn.OneGoogle, filename);
		VerifyGUIImageToFile(Map.LogIn.OneGoogle, Map.SignInBenchmark());
	}

	/**
	 * <b>Prerequisite</b>Map.Google.SignIn should be clicked firstly, and we are on the Google Login Page.
	 *
	 * Check to see if the Map.LogIn.UserName (defined for user-name in Google login page) exists
	 * If not exists, load the RS of new Google login page.
	 *
	 * @throws Throwable
	 * @see {@link #LogIn(String)}
	 */
	static void LoadNewGoolgeLoginMap() throws Throwable{
		if(!Misc.IsComponentExists(Map.LogIn.UserName, "5" /* wait 5 seconds*/)){
			if(!Misc.SetApplicationMap(MISCTESTS_NEW_GOOGLE_LOGIN_APPMAP)){
				AbortTest("Failed to load '"+MISCTESTS_NEW_GOOGLE_LOGIN_APPMAP+"'.  Test Aborting");
			}
			isNewGoogleLoginPage = true;
		}
	}

	/**
	 * Log in
	 * For Map.LogIn.UserName, see App.map file
	 */
	public static int LogIn(String counterPrefix) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		WebDriver().manage().deleteAllCookies();
		// Click call with literal coords is NOT as maintainable as the one at bottom
		Window.Maximize(Map.Google.Google);

		WDLibrary.timeoutWaitClick = 7;
		if(! Click(Map.Google.SignIn, "5,5"))
			AbortTest("Google SignIn Not Found.  Test Aborting");
		WDLibrary.timeoutWaitClick = WDLibrary.DEFAULT_TIMEOUT_WAIT_CLICK;

		LoadNewGoolgeLoginMap();

		if(! Component.InputKeys(Map.LogIn.UserName, Map.GoogleUser()+"{TAB}")) fail++;
		if(! Click(Map.LogIn.UserNameNext,"5,5")) fail++;
		if(! Component.InputKeys(Map.LogIn.Passwd, Map.GooglePassword()+"{TAB}")) fail++;
		if(! Click(Map.LogIn.SignIn, Map.TopLeft)) fail++;

		if(! Click(Map.LogIn.BackArrow, Map.TopLeft)) fail++;

		//For new Google Login page, the component Circle doesn't exist any more.
		if(Misc.IsComponentExists(Map.LogIn.Circle, "2")){
			if(! Click(Map.LogIn.Circle, Map.TopLeft)) fail++;
		}

		Pause(3);

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);

		if(fail > 0){
			Logging.LogTestFailure(counterID + " reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(counterID + " did not report any UNEXPECTED test failures!");
		}

		return fail;
	}

	public static int GUIImageTests(String counterPrefix) throws Throwable{
		final String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		int fail = 0;
		Counters.StartCounter(counterID);

		//With the new Google Login page, there is an "underline animation" for the input-box if the input-box is focused
		//That animation will make the captured image un-stable and cause un-expected error.
		//Click outside of input-box to get rid of the "underline animation"
		if(isNewGoogleLoginPage){
			Click(Map.LogIn.OneGoogle, quote("15,15"));
		}

		if(! GetGUIImage(Map.LogIn.OneGoogle, Map.SignInScreenshot())) fail++;

		if(VerifyGUIImageToFile(Map.LogIn.OneGoogle, Map.SignInBenchmark(), "", "", quote("UUID=FALSE"))) fail++;
		if(VerifyGUIImageToFile(Map.LogIn.OneGoogle, Map.SignInBenchmark())) fail++;

//		ProfileImage component does NOT exist in present Google web page.
//		if(! Click(Map.LogIn.ProfileImage, quote("15,15"))) fail++;
//		Pause(1);

		if(! GetGUIImage(Map.LogIn.OneGoogle, Map.SignInInfoScreenshot())) fail++;
		File actual = new File(Runner.jsafs().getTestDir());
		File bench = new File(Runner.jsafs().getBenchDir());
		File rebench = new File(bench, Map.SignInInfoScreenshot()+"Actuals.bmp");
		File actualbmp = new File(actual, Map.SignInInfoScreenshot()+".bmp");
		FileUtilities.copyFileToFile(actualbmp, rebench);

		if(! VerifyGUIImageToFile(Map.LogIn.OneGoogle, quote(rebench.getAbsolutePath()), "", "", quote("UUID=OFF"))) fail++;

		String subdir = "SubDir/Child/";
		rebench = new File(bench, subdir + Map.SignInInfoScreenshot() + ".bmp");
		if(!rebench.getParentFile().exists()) rebench.getParentFile().mkdirs();
		FileUtilities.copyFileToFile(actualbmp, rebench);

		if(! VerifyGUIImageToFile(Map.LogIn.OneGoogle, quote(rebench.getAbsolutePath()), "", "", quote("UUID=OFF"))) fail++;

		subdir = "./SubDir/Child/";
		rebench = new File(bench, subdir + Map.SignInInfoScreenshot() + "2.bmp");
		if(!rebench.getParentFile().exists()) rebench.getParentFile().mkdirs();
		FileUtilities.copyFileToFile(actualbmp, rebench);

		if(! VerifyGUIImageToFile(Map.LogIn.OneGoogle, quote(rebench.getAbsolutePath()), "", "", quote("UUID=OFF"))) fail++;

		subdir = "/SubDir/Child/";
		rebench = new File(bench, subdir + Map.SignInInfoScreenshot() + "3.bmp");
		if(!rebench.getParentFile().exists()) rebench.getParentFile().mkdirs();
		FileUtilities.copyFileToFile(actualbmp, rebench);

		if(! VerifyGUIImageToFile(Map.LogIn.OneGoogle, quote(rebench.getAbsolutePath()), "", "", quote("UUID=OFF"))) fail++;

		if(! VerifyBinaryFileToFile(quote(rebench.getAbsolutePath()), quote(actualbmp.getAbsolutePath()))) fail++;
		if(! VerifyFileToFile(quote(rebench.getAbsolutePath()), quote(actualbmp.getAbsolutePath()))) fail++;

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);

		if(fail > 0){
			Logging.LogTestFailure(counterID + " reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(counterID + " did not report any UNEXPECTED test failures!");
		}

		return fail;
	}

	public static int PropertyExperiments(String counterPrefix) throws Throwable{
		final String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		int fail = 0;

		Counters.StartCounter(counterID);

		if(! Click(Map.LogIn.UserNameNext, "5,5")) fail++;
		if(! Component.VerifyProperty(Map.LogIn.Passwd, "visibility", "visible")) fail++;

		if(! Click(Map.LogIn.BackArrow, "5,5")) fail++;
		if(! Component.CapturePropertiesToFile(Map.LogIn.UserName, "UserNameProperties.txt")) fail++;	// Why can not?
		if(! Component.CapturePropertyToFile(Map.LogIn.UserName, "namespaceURI", "UserNameNamespaceURI.txt")) fail++;

		Logging.LogTestWarning("1 FAILURE OK/EXPECTED for VerifyPropertiesToFile!");
		if(Component.VerifyPropertiesToFile(Map.LogIn.UserName, "UserNameProperties.txt")) fail++;
		if((prevResults.getStatusCode()==StatusCodes.OK)) fail++;

		if(! Component.VerifyPropertiesSubsetToFile(Map.LogIn.UserName, "UserNamePropertiesSubset.txt")) fail++;
		if(! Component.VerifyPropertyToFile(Map.LogIn.UserName, "namespaceURI", "UserNameNamespaceURI.txt")) fail++;

		if(! Component.CapturePropertyToFile(Map.LogIn.UserName, "namespaceURI", "^UserNamespaceURIOut")) fail++;
		if(! Component.VerifyPropertyToFile(Map.LogIn.UserName, "namespaceURI", "^UserNamespaceURIBench")) fail++;

		if(! Click(Map.LogIn.UserNameNext, "5,5")) fail++;
		if(! Component.VerifyProperty(Map.LogIn.Passwd, "name", Map.LogIn_Passwd_Name())) fail++;
		if(!isNewGoogleLoginPage){
			//For new Google login page, the defined component Map.LogIn.Passwd, doesn't have these properties
			if(! Component.VerifyProperty(Map.LogIn.Passwd, "id", Map.LogIn_Passwd_ID())) fail++;
			if(! Component.VerifyProperty(Map.LogIn.Passwd, "placeholder", Map.LogIn_Passwd_PlaceHolder())) fail++;
			if(! Component.VerifyPropertyToFile(Map.LogIn.Passwd, "placeholder", "Password.dat")) fail++;
			Logging.LogTestWarning("1 FAILURE OK/EXPECTED for VerifyProperty!");
			if(Component.VerifyProperty(Map.LogIn.Passwd, "id", "Bogus")) fail++;
		}

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);

		if(fail > 0){
			Logging.LogTestFailure(counterID + " reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(counterID + " did not report any UNEXPECTED test failures!");
		}

		return fail;
	}

	public static int temp() throws Throwable{
		int fail = 0;

		if(! EditBox.SetTextValue(Map.LogIn.UserName, Map.GoogleUser())) fail++;
		if(! EditBox.SetTextValue(Map.LogIn.Passwd, Map.GooglePassword())) fail++;

		String rs = Misc.GetAppMapValue(Map.LogIn.UserName);
		if(! (prevResults.getStatusCode()==StatusCodes.OK)) fail++;
		else Logging.LogMessage("LogIn UserName recognition: "+ rs);

		VerifyValues(quote("name=Email"),quote("name=Email"));
		Logging.LogMessage("3 FAILURES OK/EXPECTED for VerifyValues!");
		VerifyValues(quote("name=Email"),quote("name !=! Email"));
		VerifyValues(null,quote("name !=! Email"));
		VerifyValues(quote("name=Email"),null);
		try{
			WebElement e = SeleniumPlus.getObject(Map.LogIn.UserName);
			if(e != null)
				Logging.LogMessage("LogIn UserName WebElement: "+ e.toString());
			else
				Logging.LogMessage("LogIn UserName WebElement: null");
		}
		catch(Exception x){
			Logging.LogTestFailure("GetObject failed to locate a valid WebElement. "+ x.getClass().getSimpleName()+": "+ x.getMessage());
		}
		rs = Misc.GetAppMapValue(Map.LogIn.Passwd, "password");
		Logging.LogTestSuccess("LogIn Password recognition: "+ rs);
		rs = SeleniumPlus.GetVariableValue("password");
		Logging.LogTestSuccess("password variable value: "+ rs);

		rs = Misc.GetAppMapValue(Map.LogIn.UserName, null);
		Logging.LogTestSuccess("LogIn UserName recognition: "+ rs);

		rs = Misc.GetAppMapValue(Map.LogIn.Passwd, "");
		Logging.LogTestSuccess("LogIn Passord recognition: "+ rs);

		Component.InputKeys(Map.LogIn.UserName, Map.GoogleUser());
		EditBox.SetTextValue(Map.LogIn.UserName, "");
		Component.InputKeys(Map.LogIn.UserName, Map.GoogleUser()+"{TAB}");
		Component.InputKeys(Map.LogIn.Passwd, Map.GooglePassword());
		EditBox.SetTextValue(Map.LogIn.Passwd, "");
		Component.InputKeys(Map.LogIn.Passwd, quote(Map.GooglePassword()+"{Tab}"));
		// This is the preferred type of Click call using coords
		EditBox.SetTextValue(Map.LogIn.UserName, "");
		EditBox.SetTextValue(Map.LogIn.UserName, Map.GoogleUser());
		EditBox.SetTextValue(Map.LogIn.Passwd, "");
		EditBox.SetTextValue(Map.LogIn.Passwd, Map.GooglePassword());

		//JavaScriptFunctions.DEBUG_OUTPUT_JAVASCRIPT_FUNCTIONS = true;

		Component.CapturePropertiesToFile(Map.LogIn.Passwd, "PasswordProperties.txt");
		Component.CaptureObjectDataToFile(Map.LogIn.UserName, "UserNameObjectData.txt");
		Component.VerifyObjectDataToFile(Map.LogIn.UserName, "UserNameObjectData.txt");

		Click(Map.LogIn.SignIn, Map.TopLeft);  // valid component?  searches forever?

		return fail;
	}


	/**
	 * Stop the WebBrowser using the Regression.browserID already started.
	 */
	public static int stopBrowser(String counterPrefix){
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);

		if(! StopWebBrowser(Regression.browserID)) fail++;

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);

		if(fail > 0){
			Logging.LogTestFailure(counterID + " reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(counterID + " did not report any UNEXPECTED test failures!");
		}

		return fail;
	}

	/**
	 * User can test cases locally here
	 *
	 * To run test
	 * Right click on TestCase1.java
	 * Run As Selenium+1 Test.
	 *
	 * Always comment testcases once tested
	 *
	 */


	public void runTestOriginal() throws Throwable {

		try{
		    startHoneycomb(Map.SimpleTableURL(), null);
		    Pause(2);
		    Component.CapturePropertiesToFile(Map.SimpleTable.VScroller, "VScrollerProperties.txt");
		    Component.VerifyPropertiesToFile(Map.SimpleTable.VScroller, "VScrollerProperties.txt");
		    Logging.LogMessage("FAILURE OK/EXPECTED for the following test!");
		    Component.VerifyPropertiesToFile(Map.SimpleTable.VScroller, "VScrollerPropertiesBAD.txt");
		    Click(Map.SimpleTable.VScroller, "8 8");
		    Component.InputKeys(Map.SimpleTable.VScroller, "{Down 4}");
		    Component.InputKeys(Map.SimpleTable.VScroller, "{PgDn 3}");
		    Pause(2);
		}
		catch(Throwable x){
		    Logging.LogTestFailure(x.getMessage());
		}
		stopHoneycomb();

	}

	public void runTestContentSelector() throws Throwable {
		Component.CapturePropertyToFile(Map.ContentSelector.Result, "value", "ContentSelectorResults.txt");
		Component.VerifyPropertyToFile(Map.ContentSelector.Result, "value", "ContentSelectorResults.txt");
		Component.CaptureObjectDataToFile(Map.ContentSelector.Result, "ContentSelectorResultsData.txt");
		Component.VerifyObjectDataToFile(Map.ContentSelector.Result, "ContentSelectorResultsData.txt");
		Component.CapturePropertiesToFile(Map.ContentSelector.Result, "ContentSelectorResultsProperties.txt");
		Component.VerifyPropertiesToFile(Map.ContentSelector.Result, "ContentSelectorResultsProperties.txt");
	}

	public void runTestSimpleTable() throws Throwable {
		Component.CaptureObjectDataToFile(Map.SimpleTable.Table, "SimpleTableData.txt");
		Component.CapturePropertiesToFile(Map.SimpleTable.Table, "SimpleTableProperties.txt");
		Component.VerifyObjectDataToFile(Map.SimpleTable.Table, "SimpleTableData.txt");
		Component.VerifyPropertiesToFile(Map.SimpleTable.Table, "SimpleTableProperties.txt");
	}


	public void runMultiBrowserTest(String counterPrefix) throws Throwable {

		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);

		StartWebBrowser("http://www.google.com", "Chrome1", SelectBrowser.BROWSER_NAME_CHROME);
		LogIn(counterID);
		StopWebBrowser("Chrome1");
		StartWebBrowser("http://www.google.com", "Explorer1", SelectBrowser.BROWSER_NAME_IE);
		LogIn(counterID);
		StopWebBrowser("Explorer1");
		StartWebBrowser("http://www.google.com", "Firefox1", SelectBrowser.BROWSER_NAME_FIREFOX);
		LogIn(counterID);
		StopWebBrowser("Firefox1");

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);
	}

	void movemouseSelenium(WebElement element){
		try{ new Actions(WebDriver()).moveToElement(element).perform();}
		catch(Throwable t){

		}
	}

	void movemouseRobot(WebElement element){
		try{
			Point p = WDLibrary.getScreenLocation(element);
			p.translate(element.getSize().width/2, element.getSize().height/2);
			HoverScreenLocation(p.x+","+p.y,"100");
		}
		catch(Throwable t){

		}
	}

	public void runTestDojo() throws Throwable {

	}

	private static boolean isNewGoogleLoginPage = false;

	public static int runGoogleTests(String counterPrefix, String browser) throws Throwable{
		final String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		int fail = 0;
		Counters.StartCounter(counterID);

		try{
			if(! Misc.SetApplicationMap(MISCTESTS_APPMAP)) fail++;
			isNewGoogleLoginPage = false;

			fail += startGoogle(counterID, browser);
			fail += LogIn(counterID);
			fail += GUIImageTests(counterID);
			fail += PropertyExperiments(counterID);

		}catch(Throwable t){
			fail++;
			Logging.LogTestFailure(counterID +" fatal error due to "+t.getClass().getName()+", "+ t.getMessage());
		}

		fail += stopBrowser(counterID);

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);

		if(fail > 0){
			Logging.LogTestFailure(counterID + " reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(counterID + " did not report any UNEXPECTED test failures!");
		}

		return fail;
	}

	/**
	 * Note: SendMail will not check the validation of the recipients address, even it is not valid, this API will still succeed.<br>
	 * In this test,
	 * we will send a mail to a valid mail address non.repond.123@gmail.com
	 * we will also send mail to an invalid mail address non.exist.user@xxx.com
	 */
	public static int testSendEmail(String counterPrefix) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);

		String from = "non.repond.123@gmail.com";
		//The recipient can be multiple users, separated by semicolon, such as "user1@yahoo.com;user2@icloud.com";
		String to = "non.repond.123@gmail.com";
		String subject = "This is a test message, do not reply.";
		String message = "Test mail is sent by SeleniumPlus API.";
		String attachment = "TestAsset\\log.txt";
		if(!Misc.SendMail(quote(from), to, subject, message, attachment)) fail++;
		else{
			Logging.LogMessage("mail has been sent to mail address '"+to+"', you can login with mail account '"+to+"' to check the receieved mail.");
		}

		to = "non.exist.user@xxx.com";
		if(!Misc.SendMail(quote(from), to, subject, message, attachment)) fail++;
		else{
			Logging.LogMessage("try to send mail to an invalid mail address '"+to+"', you can login with mail account '"+from+"' to check there is a failure delivery mail.");
		}

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);

		if(fail > 0){
			Logging.LogTestFailure(counterID + " reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(counterID + " did not report any UNEXPECTED test failures!");
		}

		return fail;
	}

	/**
	 * This method will call:
	 * <ul>
	 * <li>{@link #testBrowserTabs_Internal(String, String)} or {@link #testBrowserTabs_Public(String, String)} to
	 * <pre>
	 * 1. Open a link in a new tab instead of a new window, then switch between tabs.
	 * 2. Open a link in a new window, then switch between old window and the new one.
	 * <b>Note:</b>This test is initially added for working in firefox browser, as selenium webdriver will ignore the "Open new windows in a new tab instead" option
	 *      and will always open link in a new window. This test show how to open a link in new tab.
	 *      For IE and Chrome, only the 2th test will be run.
	 *      IE will always open a link in a new window (Ctrl+Click doesn't work for it).
	 *      Chrome will always open a link in a new tab (but these tab are considered as window by selenium webdriver).
	 * </pre>
	 * <li>{@link #testSwitchWindow_Internal(String, String)} to
	 * <pre>
	 * Test SeleniumPlus's API {@link Misc#SwitchWindow(String, String...)} to switch between windows in the same/different browsers.
	 * </pre>
	 * </ul>
	 * @param counterPrefix String, the string of the trace where this call comes from.
	 * @param browser String, the browser name. It can be {@link Regression#FF}, {@link Regression#IE} or {@link Regression#CH}.
	 *
	 * @see #testBrowserTabs_Internal(String)
	 * @see #testBrowserTabs_Public(String)
	 */
	private static int testBrowserTabs(String counterPrefix, String browser) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		String mapID = MAP_FILE_HTMLAPP;

		if(Misc.SetApplicationMap(mapID)){

			//For public user, please run testBrowserTabs_Public() instead of testBrowserTabs_Internal()
//			fail += testBrowserTabs_Internal(counterID, browser);
			//fail += testBrowserTabs_Public(counterID, browser);

			fail += testSwitchWindow_Internal(counterID, browser);

			Misc.CloseApplicationMap(mapID);
		}else{
			trace(++fail);
			Logging.LogTestFailure(counterID+"Fail to load map '"+mapID+"', cannot test in browser '"+browser+"'!");
		}

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);

		if(fail > 0){
			Logging.LogTestFailure(counterID + " reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(counterID + " did not report any UNEXPECTED test failures!");
		}

		return fail;
	}

	/**
	 * Test setting browser's preferences by a JSON file.<br>
	 * @param browser String, the browser to test with.
	 * @return int, the number of total test unexpected failures.
	 * @throws Throwable
	 */
	private static int testBrowserPreferences(String counterPrefix, String browser) throws Throwable{
		String ID = null;
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);

		try{
			String mapID = MAP_FILE_MISC;
			String zhMapID = Utilities.nlsMap(MAP_FILE_MISC, MAP_FILE_LOCALE_SUFFIX_ZH);

			//The preference JSON file specifies the locale as "zh_cn", so the browser will be started with that locale
			if(SelectBrowser.BROWSER_NAME_CHROME.equals(browser)){
				ID = startBrowser(browser, Map.GoogleURL(), SelectBrowser.KEY_CHROME_PREFERENCE, quote(utils.testAssetFile("chromePreferences.dat")));
			}else if(SelectBrowser.BROWSER_NAME_FIREFOX.equals(browser)){
				ID = startBrowser(browser, Map.GoogleURL(), SelectBrowser.KEY_FIREFOX_PROFILE_PREFERENCE, quote(utils.testAssetFile("firefoxPreferences.dat")));
			}else{
				//Currently we only implement setting preferences for chrome and firefox
				//for other browsers, we just return
				return fail;
			}

			if(Misc.SetApplicationMap(mapID) && Misc.SetApplicationMap(zhMapID)){

				if(ID!=null){
					//As we started the browser with locale "zh_cn", so we should be able to find the
					//"login button" by RS defined in MiscTests_zh.map
					if(!GetGUIImage(Map.Google.SignIn, "Google.SignIn_zh.png")) trace(++fail);

				}else{
					Logging.LogMessage("StartWebBrowser '"+browser+"' Unsuccessful.");
					trace(++fail);
				}

				Misc.CloseApplicationMap(mapID);
				Misc.CloseApplicationMap(zhMapID);

			}else{
				trace(++fail);
				Logging.LogTestFailure(counterID+"Fail to load map '"+mapID+"' or '"+zhMapID+"', cannot test in browser '"+browser+"'!");
			}

		}catch(Exception e){
			trace(++fail);
			Logging.LogTestFailure("Fail to test preferences setting with browser '"+browser+"'! Unexpected Exception "+StringUtils.debugmsg(e));
		}finally{
			if(ID!=null) if(!StopWebBrowser(ID)) trace(++fail);
		}

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);

		if(fail > 0){
			Logging.LogTestFailure(counterID + " reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(counterID + " did not report any UNEXPECTED test failures!");
		}

		return fail;
	}

	/**
	 * Called by {@link #testBrowserTabs(String)} for internal users.
	 * Test SeleniumPlus's API {@link Misc#SwitchWindow(String, String...)} to switch between windows in the same/different browsers.<br>
	 */
	private static int testSwitchWindow_Internal(String counterPrefix, String browser) throws Throwable{
		String firstBrowserID = null;
		String secondBrowserID = null;
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		boolean originalExpressionOn = Misc.isExpressionsOn();

		try{
			Misc.Expressions(false);
			Highlight(true);

			String bogusWindowTitle = "Bogus";
			String testLinkPageSnapshotInFirstBrowser = browser+".first.win.InternalTestLinkPage.png";
			String secondPageSnapshotInFirstBrowser = browser+".first.win.InternalSecondPage.png";
			String testLinkBodySnapshotInFirstBrowser = browser+".first.InternalTestLinkPage.Body.png";
			firstBrowserID = startBrowser(browser, Map.InternalTestLinkPageURL());
			if(firstBrowserID!=null){

				WebDriver wd = SeleniumPlus.WebDriver();
				Logging.LogMessage("FIRST BROWSER:\nID="+firstBrowserID+"\nWebDriver="+wd);

				String originalWinHandle = wd.getWindowHandle();
				Logging.LogMessage(counterID+" Before clicking Link, there are windows "+Arrays.toString(WDLibrary.getAllWindowTitles()));
				Logging.LogMessage(counterID+" Before clicking Link, we are in page '"+wd.getTitle()+"' of window '"+originalWinHandle+"'");

				//In the first tab page, Open the link in the new window (For chrome, it is in new tab, but its tab will be considered as new window by selenium webdriver.)
				//Test SwitchWindow() without 'browserID'
				if(!SeleniumPlus.Click(Map.InternalTestLinkPage.Link, "5,5")) trace(++fail);
				else{
					Pause(2);

					Logging.LogMessage(counterID+" After clicking Link, there are windows "+Arrays.toString(WDLibrary.getAllWindowTitles()));
					//Even the "Second Page" is shown, but WebDriver.getTitle() still return 'Test Link'.
					Logging.LogMessage(counterID+" After clicking Link, we are in page '"+wd.getTitle()+"' of window '"+wd.getWindowHandle()+"'");

					if(Misc.SwitchWindow(Map.InternalTestLinkPageTitle())){
						Logging.LogMessage(counterID+" We switched back to page '"+wd.getTitle()+"' of window '"+wd.getWindowHandle()+"'");
						if(Map.InternalTestLinkPageTitle().equals(wd.getTitle())){
							if(!SeleniumPlus.GetGUIImage(Map.InternalTestLinkPage.InternalTestLinkPage, testLinkPageSnapshotInFirstBrowser)){
								trace(++fail);
							}else{
								if(!Files.CopyFile(utils.testFile(testLinkPageSnapshotInFirstBrowser), utils.benchFile(testLinkPageSnapshotInFirstBrowser))){
									trace(++fail);
								}
							}
						}else{
							trace(++fail);
							Logging.LogTestFailure(counterID+" current window's title '"+wd.getTitle()+"' does NOT match the expected title '"+Map.InternalTestLinkPageTitle()+"'!");
						}

					}else{
						trace(++fail);
					}

					//Switch to the second window
					if(Misc.SwitchWindow(Map.InternalSecondPageFullTitle())){
						Logging.LogMessage(counterID+" We are in page '"+wd.getTitle()+"' of window '"+wd.getWindowHandle()+"'");
						if(Map.InternalSecondPageFullTitle().equals(wd.getTitle())){
							//Capture the snapshot of the page on the second window.
							if(!SeleniumPlus.GetGUIImage(Map.InternalSecondPage.InternalSecondPage, secondPageSnapshotInFirstBrowser)){
								trace(++fail);
							}else{
								if(!Files.CopyFile(utils.testFile(secondPageSnapshotInFirstBrowser), utils.benchFile(secondPageSnapshotInFirstBrowser))){
									trace(++fail);
								}
							}
						}else{
							trace(++fail);
							Logging.LogTestFailure(counterID+" current window's title '"+wd.getTitle()+"' does NOT match the expected title '"+Map.InternalSecondPageFullTitle()+"'!");
						}
						//Close the second window
						wd.close();
					}else{
						trace(++fail);
					}

					//Go back to the first window
					if(Misc.SwitchWindow(Map.InternalTestLinkPageTitle())){
						Logging.LogMessage(counterID+" We are in page '"+wd.getTitle()+"' of window '"+wd.getWindowHandle()+"'");
						if(Map.InternalTestLinkPageTitle().equals(wd.getTitle())){
							if(!SeleniumPlus.GetGUIImage(Map.InternalTestLinkPage.Body, testLinkBodySnapshotInFirstBrowser)) trace(++fail);
						}else{
							trace(++fail);
							Logging.LogTestFailure(counterID+" current window's title '"+wd.getTitle()+"' does NOT match the expected title '"+Map.InternalTestLinkPageTitle()+"'!");
						}
					}else{
						trace(++fail);
					}

				}

				//Open the second page again to test SwitchWindow() with 'browserID'
				if(!SeleniumPlus.Click(Map.InternalTestLinkPage.Link, "5,5")) trace(++fail);
				else{
					Pause(2);

					if(!Misc.SwitchWindow(Map.InternalTestLinkPageTitle(), firstBrowserID)){
						trace(++fail);
						Logging.LogTestFailure(counterID+" Failed to switch to window according to title '"+Map.InternalTestLinkPageTitle()+"'.");
					}else{
						if(!SeleniumPlus.VerifyGUIImageToFile(Map.InternalTestLinkPage.InternalTestLinkPage, testLinkPageSnapshotInFirstBrowser)){
							trace(++fail);
						}
					}

					if(!Misc.SwitchWindow(Map.InternalSecondPageWildcardTitle(), firstBrowserID)){
						trace(++fail);
						Logging.LogTestFailure(counterID+" Failed to switch to window according to title '"+Map.InternalSecondPageFullTitle()+"'.");
					}else{
						if(!SeleniumPlus.VerifyGUIImageToFile(Map.InternalSecondPage.InternalSecondPage, secondPageSnapshotInFirstBrowser)){
							trace(++fail);
						}
					}

					Logging.LogFailureOK("Expected Failure: SwitchWindow should fail as the title '"+bogusWindowTitle+"' doesn't exist.");
					if(Misc.SwitchWindow(bogusWindowTitle, firstBrowserID)){
						trace(++fail);
						Logging.LogTestFailure(counterID+" Switched to window according to title 'Bogus' which does NOT exist.");
					}
				}
			}else{
				Logging.LogTestWarning(counterID+" StartWebBrowser '"+browser+"' Unsuccessful.");
				trace(++fail);
			}

			String testLinkPageSnapshotInSecondBrowser = browser+".second.win.InternalTestLinkPage.png";
			String secondPageSnapshotInSecondBrowser = browser+".second.win.InternalSecondPage.png";
			String testLinkBodySnapshotInSecondBrowser = browser+".second.InternalTestLinkPage.Body.png";
			//Start the second browser with the same link to test SwitchWindow in different browser
			secondBrowserID = startBrowser(browser, Map.InternalTestLinkPageURL());
			if(secondBrowserID!=null){

				Window.SetPosition(Map.InternalTestLinkPage.InternalTestLinkPage, 800, 300, 1024,768);

				WebDriver wd = SeleniumPlus.WebDriver();
				Logging.LogMessage("SECOND BROWSER:\nID="+secondBrowserID+"\nWebDriver="+wd);

				String originalWinHandle = wd.getWindowHandle();
				Logging.LogMessage(counterID+" Before clicking Link, there are windows "+Arrays.toString(WDLibrary.getAllWindowTitles()));
				Logging.LogMessage(counterID+" Before clicking Link, we are in page '"+wd.getTitle()+"' of window '"+originalWinHandle+"'");

				//In the first tab page, Open the link in the new window (For chrome, it is in new tab, but its tab will be considered as new window by selenium webdriver.)
				//Calling Misc.SwitchWindow() without 'browserID', it will use the latest used browser, which is the second browser.
				if(!SeleniumPlus.Click(Map.InternalTestLinkPage.Link, "5,5")) trace(++fail);
				else{
					Pause(2);

					Logging.LogMessage(counterID+" After clicking Link, there are windows "+Arrays.toString(WDLibrary.getAllWindowTitles()));
					//Even the "Second Page" is shown, but WebDriver.getTitle() still return 'Test Link'.
					Logging.LogMessage(counterID+" After clicking Link, we are in page '"+wd.getTitle()+"' of window '"+wd.getWindowHandle()+"'");

					if(Misc.SwitchWindow(Map.InternalTestLinkPageTitle())){
						Logging.LogMessage(counterID+" We switched back to page '"+wd.getTitle()+"' of window '"+wd.getWindowHandle()+"'");
						if(Map.InternalTestLinkPageTitle().equals(wd.getTitle())){
							if(!SeleniumPlus.GetGUIImage(Map.InternalTestLinkPage.InternalTestLinkPage, testLinkPageSnapshotInSecondBrowser)){
								trace(++fail);
							}else{
								if(!Files.CopyFile(utils.testFile(testLinkPageSnapshotInSecondBrowser), utils.benchFile(testLinkPageSnapshotInSecondBrowser))){
									trace(++fail);
								}
							}
						}else{
							trace(++fail);
							Logging.LogTestFailure(counterID+" current window's title '"+wd.getTitle()+"' does NOT match the expected title '"+Map.InternalTestLinkPageTitle()+"'!");
						}

					}else{
						trace(++fail);
					}

					//Switch to the second window
					if(Misc.SwitchWindow(Map.InternalSecondPageRegexTitle())){
						Logging.LogMessage(counterID+" We are in page '"+wd.getTitle()+"' of window '"+wd.getWindowHandle()+"'");
						if(Map.InternalSecondPageFullTitle().equals(wd.getTitle())){
							//Capture the snapshot of the page on the second window.
							if(!SeleniumPlus.GetGUIImage(Map.InternalSecondPage.InternalSecondPage, secondPageSnapshotInSecondBrowser)){
								trace(++fail);
							}else{
								if(!Files.CopyFile(utils.testFile(secondPageSnapshotInSecondBrowser), utils.benchFile(secondPageSnapshotInSecondBrowser))){
									trace(++fail);
								}
							}
							//Close the second window
							wd.close();
						}else{
							trace(++fail);
							Logging.LogTestFailure(counterID+" current window's title '"+wd.getTitle()+"' does NOT match the expected title '"+Map.InternalSecondPageFullTitle()+"'!");
						}
					}else{
						trace(++fail);
					}

					//Go back to the first window
					if(Misc.SwitchWindow(Map.InternalTestLinkPageTitle())){
						Logging.LogMessage(counterID+" We are in page '"+wd.getTitle()+"' of window '"+wd.getWindowHandle()+"'");
						if(Map.InternalTestLinkPageTitle().equals(wd.getTitle())){
							if(!SeleniumPlus.GetGUIImage(Map.InternalTestLinkPage.Body, testLinkBodySnapshotInSecondBrowser)) trace(++fail);
						}else{
							trace(++fail);
							Logging.LogTestFailure(counterID+" current window's title '"+wd.getTitle()+"' does NOT match the expected title '"+Map.InternalTestLinkPageTitle()+"'!");
						}
					}else{
						trace(++fail);
					}
				}

				//Open the second page again
				if(!SeleniumPlus.Click(Map.InternalTestLinkPage.Link, "5,5")) trace(++fail);
				else{
					Pause(2);

					//Test WDLibrary.switchWindow() with 'browserID' to switch in the first opened browser
					if(!Misc.SwitchWindow(Map.InternalTestLinkPageTitle(), firstBrowserID)){
						trace(++fail);
						Logging.LogTestFailure(counterID+" Failed to switch to window according to title '"+Map.InternalTestLinkPageTitle()+"' in first browser.");
					}else{
						if(!SeleniumPlus.VerifyGUIImageToFile(Map.InternalTestLinkPage.InternalTestLinkPage, testLinkPageSnapshotInFirstBrowser)){
							trace(++fail);
						}
					}

					if(!Misc.SwitchWindow(Map.InternalSecondPageRegexTitle(), firstBrowserID)){
						trace(++fail);
						Logging.LogTestFailure(counterID+" Failed to switch to window according to title '"+Map.InternalSecondPageFullTitle()+"' in first browser.");
					}else{
						if(!SeleniumPlus.VerifyGUIImageToFile(Map.InternalSecondPage.InternalSecondPage, secondPageSnapshotInFirstBrowser)){
							trace(++fail);
						}
					}

					Logging.LogFailureOK("Expected Failure: SwitchWindow should fail as the title '"+bogusWindowTitle+"' doesn't exist.");
					if(Misc.SwitchWindow(bogusWindowTitle, firstBrowserID)){
						trace(++fail);
						Logging.LogTestFailure(counterID+" Switched to window according to title 'Bogus' which does NOT exist.");
					}

				}
			}else{
				Logging.LogTestWarning(counterID+" StartWebBrowser '"+browser+"' Unsuccessful.");
				trace(++fail);
			}
		}catch(Exception e){
			trace(++fail);
			Logging.LogTestFailure(counterID+" Fail to test html Application in browser '"+browser+"'! Unexpected Exception "+StringUtils.debugmsg(e));
		}finally{
			if(firstBrowserID!=null) if(!StopWebBrowser(firstBrowserID)) trace(++fail);
			if(secondBrowserID!=null) if(!StopWebBrowser(secondBrowserID)) trace(++fail);
			Misc.Expressions(originalExpressionOn);
			Highlight(false);
		}

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);

		if(fail > 0){
			Logging.LogTestFailure(counterID + " reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(counterID + " did not report any UNEXPECTED test failures!");
		}

		return fail;
	}

	/**
	 * Called by {@link #testBrowserTabs(String)} for internal users.
	 * This method uses the WebDriver's API directly to switch between windows/tabs.<br>
	 * Please refer to {@link #testSwitchWindow_Internal(String, String)} if you want to switch by SeleniumPlus's API.<br>
	 */
	private static int testBrowserTabs_Internal(String counterPrefix, String browser) throws Throwable{
		String ID = null;
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		boolean originalExpressionOn = Misc.isExpressionsOn();

		try{
			ID = startBrowser(browser, Map.InternalTestLinkPageURL());
			if(ID!=null){
				Misc.Expressions(false);
				Highlight(true);

				WebDriver wd = SeleniumPlus.WebDriver();

				//Test open link in new tab, ONLY for Firefox browser.
				if(FF.equals(browser)){
					String originalTab = wd.getTitle();
					Logging.LogMessage("We are in page '"+wd.getTitle()+"' of window '"+wd.getWindowHandle()+"'");
					//Open the link in the new tab
					if(!SeleniumPlus.CtrlClick(Map.InternalTestLinkPage.Link, "5,5")) fail++;
					else{
						Pause(2);

						for(int i=0;i<2;i++){
							if(!originalTab.equals(wd.getTitle())){
								//If the title is different, then we are in the new tab page.
								break;
							}
							if(SeleniumPlus.TypeKeys("^{Tab}")){
								Logging.LogMessage("Swtich to tab '"+wd.getTitle()+"'");
							}
						}

						Logging.LogMessage("We are in page '"+wd.getTitle()+"' of window '"+wd.getWindowHandle()+"'");
						//Get the picture of the page on the second tab, it will take some time to find it.
						if(!SeleniumPlus.GetGUIImage(Map.InternalSecondPage.InternalSecondPage, browser+".tab.InternalSecondPage.png")) fail++;

						//Close second tab: it seems that "Ctrl+W" will close all tabs and window??
						//SeleniumPlus.TypeKeys("^W");
						//Switch between tabs, we will go back to the first tab
						SeleniumPlus.TypeKeys("^{Tab}");
					}
				}

				//Test open link in new window, works for Firefox, Chrome and IE
				Logging.LogMessage("We are in page '"+wd.getTitle()+"' of window '"+wd.getWindowHandle()+"'");
				//In the first tab page, Open the link in the new window (For chrome, it is in new tab, but its tab will be considered as new window by selenium webdriver.)
				if(!SeleniumPlus.Click(Map.InternalTestLinkPage.Link, "5,5")) fail++;
				else{
					Pause(2);

					String originalWinHandle = wd.getWindowHandle();
					String newWinHandle = null;
					List<String> handles = new ArrayList(wd.getWindowHandles());

					if(handles.size()>1){
						handles.remove(originalWinHandle);
						//Switch to the second window
						newWinHandle = handles.get(0);
						wd.switchTo().window(newWinHandle);
						Logging.LogMessage("We are in page '"+wd.getTitle()+"' of window '"+wd.getWindowHandle()+"'");
						//Capture the snapshot of the page on the second window.
						if(!SeleniumPlus.GetGUIImage(Map.InternalSecondPage.InternalSecondPage, browser+".win.InternalSecondPage.png")) fail++;
						//Close the second window
						wd.close();
						//Go back to the first window
						wd.switchTo().window(originalWinHandle);
					}else{
						Logging.LogTestWarning("It seems there is no more window to swicht to!");
					}

					Logging.LogMessage("We are in page '"+wd.getTitle()+"' of window '"+wd.getWindowHandle()+"'");
					if(!SeleniumPlus.GetGUIImage(Map.InternalTestLinkPage.Body, browser+".InternalTestLinkPage.Body.png")) fail++;
				}

			}else{
				Logging.LogTestWarning("StartWebBrowser '"+browser+"' Unsuccessful.");
				trace(++fail);
			}
		}catch(Exception e){
			trace(++fail);
			Logging.LogTestFailure("Fail to test html Application in browser '"+browser+"'! Unexpected Exception "+StringUtils.debugmsg(e));
		}finally{
			if(ID!=null) if(!StopWebBrowser(ID)) trace(++fail);
			Misc.Expressions(originalExpressionOn);
			Highlight(false);
		}

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);

		if(fail > 0){
			Logging.LogTestFailure(counterID + " reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(counterID + " did not report any UNEXPECTED test failures!");
		}

		return fail;
	}

	/**
	 * Called by {@link #testBrowserTabs(String)} for public users.
	 */
	private static int testBrowserTabs_Public(String counterPrefix, String browser) throws Throwable{
		String ID = null;
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		boolean originalExpressionOn = Misc.isExpressionsOn();

		try{
			ID = startBrowser(browser, Map.HtmlLinkTargetPageURL());
			if(ID!=null){
				Misc.Expressions(false);
				Highlight(true);

				WebDriver wd = SeleniumPlus.WebDriver();

				//Test open link in new tab, ONLY for Firefox browser.
				if(FF.equals(browser)){
					String originalTab = wd.getTitle();
					Logging.LogMessage("We are in page '"+wd.getTitle()+"' of window '"+wd.getWindowHandle()+"'");
					//Open the link in the new tab
					if(!SeleniumPlus.CtrlClick(Map.HtmlLinkTargetPage.Link, "5,5")) fail++;
					else{
						Pause(2);

						for(int i=0;i<2;i++){
							if(!originalTab.equals(wd.getTitle())){
								//If the title is different, then we are in the new tab page.
								break;
							}
							if(SeleniumPlus.TypeKeys("^{Tab}")){
								Logging.LogMessage("Swtich to tab '"+wd.getTitle()+"'");
							}
						}

						Logging.LogMessage("We are in page '"+wd.getTitle()+"' of window '"+wd.getWindowHandle()+"'");
						//Get the picture of the page on the second tab, it will take some time to find it.
						if(!SeleniumPlus.GetGUIImage(Map.W3CPage.W3CPage, browser+".tab.W3CPage.png")) fail++;

						//Close second tab: it seems that "Ctrl+W" will close all tabs and window??
						//SeleniumPlus.TypeKeys("^W");
						//Switch between tabs, we will go back to the first tab
						SeleniumPlus.TypeKeys("^{Tab}");
					}
				}

				//Test open link in new window, works for Firefox, Chrome and IE
				Logging.LogMessage("We are in page '"+wd.getTitle()+"' of window '"+wd.getWindowHandle()+"'");
				//In the first tab page, Open the link in the new window (For chrome, it is in new tab, but its tab will be considered as new window by selenium webdriver.)
				if(!SeleniumPlus.Click(Map.HtmlLinkTargetPage.Link, "5,5")) fail++;
				else{
					Pause(2);

					String originalWinHandle = wd.getWindowHandle();
					String newWinHandle = null;
					List<String> handles = new ArrayList(wd.getWindowHandles());

					if(handles.size()>1){
						handles.remove(originalWinHandle);
						//Switch to the second window
						newWinHandle = handles.get(0);
						wd.switchTo().window(newWinHandle);
						Logging.LogMessage("We are in page '"+wd.getTitle()+"' of window '"+wd.getWindowHandle()+"'");
						//Capture the snapshot of the page on the second window.
						if(!SeleniumPlus.GetGUIImage(Map.W3CPage.W3CPage, browser+".win.W3CPage.png")) fail++;
						//Close the second window
						wd.close();
						//Go back to the first window
						wd.switchTo().window(originalWinHandle);
					}else{
						Logging.LogTestWarning("It seems there is no more window to swicht to!");
					}

					Logging.LogMessage("We are in page '"+wd.getTitle()+"' of window '"+wd.getWindowHandle()+"'");

					// For IE driver, it may fail because taking screenshot here will REDUCE the browser window size.
					// Thus it will consider the 'component is totally outside of browser'.
					// This is the problem of IE driver, and 3rd party software Selenium treated it as no fix.
					// Reference: https://github.com/SeleniumHQ/selenium/issues/410 ,
					// 			  https://code.google.com/p/selenium/issues/detail?id=6395 .
					if(!SeleniumPlus.GetGUIImage(Map.HtmlLinkTargetPage.Body, browser+".HtmlLinkTargetPage.Body.png")) fail++;
				}

			}else{
				Logging.LogTestWarning("StartWebBrowser '"+browser+"' Unsuccessful.");
				trace(++fail);
			}
		}catch(Exception e){
			trace(++fail);
			Logging.LogTestFailure("Fail to test html Application in browser '"+browser+"'! Unexpected Exception "+StringUtils.debugmsg(e));
		}finally{
			if(ID!=null) if(!StopWebBrowser(ID)) trace(++fail);
			Misc.Expressions(originalExpressionOn);
			Highlight(false);
		}

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);

		if(fail > 0){
			Logging.LogTestFailure(counterID + " reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(counterID + " did not report any UNEXPECTED test failures!");
		}

		return fail;
	}

	public static int runRegressionTest() throws Throwable{

		Counters.StartCounter(COUNTER);
		int fail = 0;
		fail += testSendEmail(COUNTER);

		String browsers = Map.TestBrowserName();
		if(browsers==null || browsers.trim().isEmpty()){
			browsers = FF;
			Logging.LogTestWarning(COUNTER+" cannot get TestBrowserName from map, use "+browsers);
		}
		browsers = browsers.replaceAll(" +", " ");
		String[] browserArray = browsers.split(" ");

		for(String browser: browserArray){
			fail += runGoogleTests(COUNTER, browser);
			fail += testBrowserTabs(COUNTER, browser);//IE, CH could be used to test switch between windows.
			fail += testBrowserPreferences(COUNTER, browser);
		}

		Counters.StopCounter(COUNTER);
		Counters.StoreCounterInfo(COUNTER, COUNTER);
		Counters.LogCounterInfo(COUNTER);

		if(fail > 0){
			Logging.LogTestFailure(COUNTER + " reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(COUNTER + " did not report any UNEXPECTED test failures!");
		}

		return fail;
	}


	public void runTest() throws Throwable{
		initUtils();
		long start = System.currentTimeMillis();
		System.out.println("started at "+StringUtils.getTimeString(new Date(start), true));
		runRegressionTest();
		long end = System.currentTimeMillis();
		System.out.println("stopped at "+StringUtils.getTimeString(new Date(end), true));
		System.out.println("consumed  "+ (end-start)/1000+" seconds." );
	}

}