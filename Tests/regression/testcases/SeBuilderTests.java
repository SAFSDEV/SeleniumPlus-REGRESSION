package regression.testcases;

import org.safs.selenium.util.JavaScriptFunctions;
import org.safs.selenium.webdriver.SeleniumPlus;
import org.safs.selenium.webdriver.SeleniumPlus.Logging;
import org.safs.selenium.webdriver.lib.WDLibrary;

import regression.Map;

/** 
 * Used to hold a number of related testcase methods invocable from any class needing them. 
 * <p>
 * To execute as a SeleniumPlus Unit testfor this class, the runTest() method must exist and 
 * should contain appropriate testcase method invocations. 
 * The following JARs must be in the JVM CLASSPATH for such a Unit test invocation. 
 * This is the same as any other SeleniumPlus test invocation: 
 * <pre>
 * 	 pathTo/yourClasses/bin or yourTest.jar,
 * 	 pathTo/seleniumplus.jar,
 * 	 pathTo/JSTAFEmbedded.jar, (or JSTAF.jar if using STAF and other external tools or engines.)
 * </pre>
 * Then, you can execute this test with an invocation similar to:
 * <pre>
 * 	 java -cp %CLASSPATH% regression.testcases.SeBuilder
 * </pre>
 * 
 * @see org.safs.selenium.webdriver.SeleniumPlus#main(java.lang.String[])
 */ 
public class SeBuilderTests extends SeleniumPlus {


	/** "SeBuilderTests.map" */
	public static String SEBUILDERTESTS_APPMAP = "SeBuilderTests.map";

	public static int runFormsTest()throws Throwable{
		int fail = 0;
		boolean launched = Misc.CallScript(Map.FormsTest());
		if(!launched) fail++;
		try{
			WDLibrary.stopBrowser(Map.FormsBrowser());
		}catch(Exception ignore){}
				
		if(fail > 0){
			Logging.LogTestFailure("SeBuilderTest.FormsSRTest reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess("SeBuilderTest.FormsSRTest did not report any UNEXPECTED test failures!");
		}
		return fail;
	}

	/* 
	 * Insert (generally) static testcase methods below. 
	 * You call these from your TestRun runTest() method for normal testing, 
 	 * your TestCase runTest() method for Unit testing, 
	 * or from other testcases, testcase classes, or anywhere they are needed. 
	 */ 
	public static int runGoogleSampleEscp()throws Throwable{
		int fail = 0;
		boolean launched = Misc.CallScript(Map.SampleEscpScript());
		if(!launched) fail++;
		if(! Misc.WaitForGUI(Map.GoogleResults.AmazonOfficialSiteLink, "7")) fail++;
		if(! Component.VerifyPropertyContains(Map.GoogleResults.AmazonAdCite, "innerHTML", Map.AmazonText())) fail++;
		String id = WDLibrary.getIDForWebDriver(WDLibrary.getWebDriver());
		if(! StopWebBrowser(id)) fail++;

		if(fail > 0){
			Logging.LogTestFailure("SeBuilderTest.GoogleSampleEscp reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess("SeBuilderTest.GoogleSampleEscp did not report any UNEXPECTED test failures!");
		}
		return fail;
	}

	public static int runRegressionTest() throws Throwable{
		
		Misc.SetApplicationMap(SEBUILDERTESTS_APPMAP);
		
		// *****************************************
		// JavaScriptFunctions.jsDebugLogEnable = true;
		// *****************************************
		
		int fail = runFormsTest();		
		fail += runGoogleSampleEscp();
		
		if(fail > 0){
			Logging.LogTestFailure("SeBuilderTests reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess("SeBuilderTests did not report any UNEXPECTED test failures!");
		}
		return fail;
	}
	
	
	/** 
	 * Normally not used for TestCase classes. 
	 * Can be used to implement a Unit test for this TestCase class, or as a test suite. 
	 * <p>
	 * Within this method, add calls to the testcase methods you wish to execute. 
	 * You are not limited to calling methods in this class only. 
	 * <p>
	 * @see org.safs.selenium.webdriver.SeleniumPlus#main(java.lang.String[])
	 */
	@Override
	public void runTest() throws Throwable {
		runRegressionTest();
	}
}
