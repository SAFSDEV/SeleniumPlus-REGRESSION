package regression.testcases;

import org.safs.StringUtils;
import org.safs.selenium.webdriver.SeleniumPlus;
import org.safs.selenium.webdriver.lib.WDLibrary;

import regression.Map;
import regression.testruns.Regression;

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
	public static final String COUNTER = StringUtils.getClassName(0, false);

	/** "SeBuilderTests.map" */
	public static String SEBUILDERTESTS_APPMAP = "SeBuilderTests.map";

	public static int runFormsTest(String counterPrefix)throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		boolean launched = Misc.CallScript(Map.FormsTest());

		if(!launched) fail++;

		try{
			WDLibrary.stopBrowser(Map.FormsBrowser());
		}catch(Exception ignore){}

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

	public static int testJScodeAndStoredVars(String counterPrefix)throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		boolean launched = Misc.CallScript(Map.JSCodeAndStoredVarTest());

		if(!launched) fail++;

		try{
			WDLibrary.stopBrowser(Map.JSCodeAndStoredVarBrowser());
		}catch(Exception ignore){}

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

	/*
	 * Insert (generally) static testcase methods below.
	 * You call these from your TestRun runTest() method for normal testing,
 	 * your TestCase runTest() method for Unit testing,
	 * or from other testcases, testcase classes, or anywhere they are needed.
	 */
	public static int runGoogleSampleEscp(String counterPrefix)throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);

		boolean launched = Misc.CallScript(Map.SampleEscpScript());

		if(!launched) fail++;
		if(! Misc.WaitForGUI(Map.GoogleResults.AmazonOfficialSiteLink, "7")) fail++;
		if(! Component.VerifyPropertyContains(Map.GoogleResults.AmazonCite, "innerHTML", Map.AmazonText())) fail++;
		String id = WDLibrary.getIDForWebDriver(WDLibrary.getWebDriver());
		if(! StopWebBrowser(id)) fail++;

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

		Misc.SetApplicationMap(SEBUILDERTESTS_APPMAP);

		// *****************************************
		// JavaScriptFunctions.jsDebugLogEnable = true;
		// *****************************************

		int fail = 0;
		fail = runFormsTest(COUNTER);
		fail += testJScodeAndStoredVars(COUNTER);
		fail += runGoogleSampleEscp(COUNTER);

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
