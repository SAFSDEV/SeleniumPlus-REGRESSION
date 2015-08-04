package regression.testruns;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.safs.Domains;
import org.safs.selenium.webdriver.SeleniumPlus;
import org.safs.selenium.webdriver.lib.SelectBrowser;

import regression.testcases.AssertTests;
import regression.testcases.CheckBoxTests;
import regression.testcases.ComboBoxTests;
import regression.testcases.DriverMiscCommandTests;
import regression.testcases.EditBoxTests;
import regression.testcases.FilesTests;
import regression.testcases.GenericMasterTests;
import regression.testcases.GenericObjectTests;
import regression.testcases.IBTTests;
import regression.testcases.ListViewTests;
import regression.testcases.MenuTests;
import regression.testcases.MiscTests;
import regression.testcases.SeBuilderTests;
import regression.testcases.StringsTests;
import regression.testcases.TabControlTests;
import regression.testcases.TreeViewTests;

/** 
 * <pre>
 * 	 java -cp %CLASSPATH% regression.testruns.Regression
 * </pre>
 * 
 * @see org.safs.selenium.webdriver.SeleniumPlus#main(java.lang.String[])
 */ 
public class Regression extends SeleniumPlus {

	/* 
	 * Insert (generally) static testcase methods or setup/teardown methods below. 
	 * You call these from your runTest() method for normal testing, 
 	 * or from other testcases, testcase classes, or anywhere they are needed. 
	 */ 

	public static final String IE = "explorer";
	public static final String CH = "chrome";
	public static final String FF = "firefox";

	public static String browserID;
	public static final String MAP_FILE_SAPDEMOAPP = "SapDemoApp.map";
	public static final String MAP_FILE_DOJOAPP = "DojoApp.map";
	public static final String MAP_FILE_HTMLAPP = "HtmlApp.map";

	protected static String generateID(){
		return String.valueOf((new Date()).getTime());
	}
	
	public static String startBrowser(String browser, String url, String... params){
		if(browser==null) browser = SelectBrowser.BROWSER_NAME_FIREFOX;
		String browserID = generateID();
		
		String[] parameters = combineParams(params, browser, ""/*timeout will be default 30 seconds*/, ""/*isRemote default is true*/);

		if (!StartWebBrowser(url, browserID, parameters)) return null;

		return browserID;
	}

	/**
	 * Print out the stack-trace (where error occur) to console.
	 * In the Eclipse console, it will be easy to trace the source code where the error occur by clicking the link.
	 * @param errorCount int, the local error count.
	 */
	protected static void trace(int errorCount){
		System.out.println("Regression Trace Error '"+errorCount+"' at "+Thread.currentThread().getStackTrace()[2]);
	}
	
	/**
	 * Run ALL enabled regression tests. 
	 * @see org.safs.selenium.webdriver.SeleniumPlus#main(java.lang.String[])
	 */
	@Override
	public void runTest() throws Throwable {
		List<String> enabledDomains = new ArrayList<String>();
		enabledDomains.add(Domains.HTML_DOMAIN);
		enabledDomains.add(Domains.HTML_DOJO_DOMAIN);
		enabledDomains.add(Domains.HTML_SAP_DOMAIN);
		
		int fail = 0; 
		fail += AssertTests.runRegressionTest();
		fail += IBTTests.runRegressionTest();
		fail += MiscTests.runRegressionTest();
		fail += SeBuilderTests.runRegressionTest();
		fail += StringsTests.runRegressionTest();
		fail += FilesTests.runRegressionTest(Runner, true);
		fail += DriverMiscCommandTests.runRegressionTest(Runner);
		
		fail += GenericMasterTests.runRegressionTest(Runner, enabledDomains);
		fail += GenericObjectTests.runRegressionTest(Runner, enabledDomains);
		fail += CheckBoxTests.runRegressionTest(Runner, enabledDomains);
		fail += ComboBoxTests.runRegressionTest(Runner, enabledDomains);
		fail += ListViewTests.runRegressionTest(Runner, enabledDomains);
		fail += MenuTests.runRegressionTest(Runner, enabledDomains);
		fail += TabControlTests.runRegressionTest(Runner, enabledDomains);
		fail += TreeViewTests.runRegressionTest(Runner, enabledDomains);
		fail += EditBoxTests.runRegressionTest(Runner, enabledDomains);
		
		if(fail > 0){
			Logging.LogTestFailure("Regression reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess("Regression did not report any UNEXPECTED test failures!");
		}
	}
}
