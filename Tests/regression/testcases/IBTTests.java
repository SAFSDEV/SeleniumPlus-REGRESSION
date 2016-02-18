package regression.testcases;

import org.safs.selenium.webdriver.SeleniumPlus;
import org.safs.selenium.webdriver.SeleniumPlus.Logging;

import regression.Map;

/** 
 * <pre>
 * 	 java -cp %CLASSPATH% regression.testcases.IBTTests
 * </pre>
 * @see org.safs.selenium.webdriver.SeleniumPlus#main(java.lang.String[])
 */ 
public class IBTTests extends SeleniumPlus {

	/** "IBTImageTests.MAP" */
	public static String IBTTESTS_APPMAP = "IBTImageTests.MAP";


	/**
	 * @return the number of unexpected failures encountered.
	 * @throws Throwable
	 */
	public static int LaunchSwingApp() throws Throwable{
		if(! Misc.LaunchApplication(Map.SwingAppID(), quote("java -jar "+ Map.SwingAppJar()))) return 1;
		Pause(2);
		return 0;
	}
	
	/**
	 * Click on the SwingApp titlebar to activate the window. 
	 * @return the number of unexpected failures encountered.
	 * @throws Throwable
	 */
	public static int ActivateSwingApp() throws Throwable{
		if( ! Click(Map.SwingApp.SwingApp) ) return 1;
		return 0;
	}
	
	/**
	 * Click on the SwingApp titlebar to activate the window. 
	 * @return the number of unexpected failures encountered.
	 * @throws Throwable
	 */
	public static int TestIBTWindows() throws Throwable{
		int fail = 0;
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
		
		return fail;
	}
	
	/**
	 * @return the number of unexpected failures encountered.
	 * @throws Throwable
	 */
	public static int SelectJDragTab() throws Throwable{
		int fail = 0;
		fail += ActivateSwingApp();
		if( ! DoubleClick(Map.SwingApp.JDragTab) ) fail++;
		return fail;
	}
	
	/**
	 * @return the number of unexpected failures encountered.
	 * @throws Throwable
	 */
	public static int CloseSwingApp() throws Throwable{
		int fail = 0;
		fail += ActivateSwingApp();
		if(! TypeKeys(quote("%{F4}"))) fail++;
		return fail;
	}

	/**
	 * Run all IBT regression tests.
	 * @return the total number of UNEXPECTED failures encountered.
	 * @throws Throwable
	 */
	public static int runRegressionTest() throws Throwable{
		int fail = 0;
		if(! Misc.SetApplicationMap(IBTTESTS_APPMAP)) fail++;
		fail += LaunchSwingApp();

		fail += SelectJDragTab();
		Pause(1);
		TypeKeys("{RIGHT}");
		Pause(1);
		
		fail += TestIBTWindows();
		
		fail += CloseSwingApp();
		if(fail > 0){
			Logging.LogTestFailure("IBTTests reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess("IBTTests did not report any UNEXPECTED test failures!");
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
