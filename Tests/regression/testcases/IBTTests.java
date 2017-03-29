package regression.testcases;

import org.safs.StringUtils;
import org.safs.selenium.webdriver.SeleniumPlus;

import regression.Map;
import regression.testruns.Regression;

/**
 * <pre>
 * 	 java -cp %CLASSPATH% regression.testcases.IBTTests
 * </pre>
 * @see org.safs.selenium.webdriver.SeleniumPlus#main(java.lang.String[])
 */
public class IBTTests extends SeleniumPlus {
	public static final String COUNTER = StringUtils.getClassName(0, false);

	/** "IBTImageTests.MAP" */
	public static String IBTTESTS_APPMAP = "IBTImageTests.MAP";


	/**
	 * @return the number of unexpected failures encountered.
	 * @throws Throwable
	 */
	public static int LaunchSwingApp(String counterPrefix) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);

		if(! Misc.LaunchApplication(Map.SwingAppID(), quote("java -jar "+ Map.SwingAppJar()))) fail++;
		Pause(2);

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
	 * Click on the SwingApp titlebar to activate the window.
	 * @return the number of unexpected failures encountered.
	 * @throws Throwable
	 */
	public static int ActivateSwingApp(String counterPrefix) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);

		if( ! Click(Map.SwingApp.SwingApp) ) fail++;

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
	 * Click on the SwingApp titlebar to activate the window.
	 * @return the number of unexpected failures encountered.
	 * @throws Throwable
	 */
	public static int TestIBTWindows(String counterPrefix) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);

		if( ! Click(Map.SwingApp.TitleBar) ) fail++;
		Pause(1);
		if( ! Click(Map.SwingApp.JDragTab) ) fail++;
		Pause(1);
		TypeKeys("{RIGHT}");
		Pause(1);

		if( ! Click(Map.IBTWinIR.TitleBar) ) fail++;
		Pause(1);
		if( ! Click(Map.IBTWinIR.JDragTab) ) fail++;
		Pause(1);
		TypeKeys("{RIGHT}");
		Pause(1);
		if( ! Click(Map.IBTWinSR.TitleBar) ) fail++;
		Pause(1);
		if( ! Click(Map.IBTWinSR.JDragTab) ) fail++;
		Pause(1);
		TypeKeys("{RIGHT}");
		Pause(1);

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
	 * @return the number of unexpected failures encountered.
	 * @throws Throwable
	 */
	public static int SelectJDragTab(String counterPrefix) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);

		fail += ActivateSwingApp(counterID);
		if( ! DoubleClick(Map.SwingApp.JDragTab) ) fail++;

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
	 * @return the number of unexpected failures encountered.
	 * @throws Throwable
	 */
	public static int CloseSwingApp(String counterPrefix) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);

		fail += ActivateSwingApp(counterID);
		if(! TypeKeys(quote("%{F4}"))) fail++;

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
	 * Run all IBT regression tests.
	 * @return the total number of UNEXPECTED failures encountered.
	 * @throws Throwable
	 */
	public static int runRegressionTest() throws Throwable{
		int fail = 0;
		Counters.StartCounter(COUNTER);

		if(! Misc.SetApplicationMap(IBTTESTS_APPMAP)) fail++;
		fail += LaunchSwingApp(COUNTER);

		fail += SelectJDragTab(COUNTER);
		Pause(1);
		TypeKeys("{RIGHT}");
		Pause(1);

		fail += TestIBTWindows(COUNTER);

		fail += CloseSwingApp(COUNTER);

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
	 * Simply runs the Regression Test.
	 * @see #runRegressionTest()
     * @see org.safs.selenium.webdriver.SeleniumPlus#main(java.lang.String[])
	 */
	@Override
	public void runTest() throws Throwable {
		runRegressionTest();
	}
}
