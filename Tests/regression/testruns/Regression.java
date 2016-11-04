package regression.testruns;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.safs.Domains;
import org.safs.StringUtils;
import org.safs.selenium.webdriver.SeleniumPlus;
import org.safs.selenium.webdriver.lib.SelectBrowser;
import org.safs.tools.CaseInsensitiveFile;
import org.safs.xml.XMLTransformer;

import regression.testcases.AssertTests;
import regression.testcases.AutoItTests;
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
import regression.util.Utilities;

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
	public static final String MAP_FILE_MISC = "MiscTests.map";
	
	public static final String MAP_FILE_LOCALE_SUFFIX_EN = "_en";
	public static final String MAP_FILE_LOCALE_SUFFIX_ZH = "_zh";

	public static File logsdir = null; //deduced at runtime 
	
	/** The utilities to deduce test folders, files and assets etc.*/
	protected static Utilities utils = null;
	
	protected static String generateID(){
		return String.valueOf((new Date()).getTime());
	}
	
	/**
	 * Generate the counterID for testing method.
	 * @param counterPrefix, the counterID of the method calling current method.
	 * @param methodName, the current method's full name. E.g. 'className.lastMethodName'.
	 * @return 'counterID.lastMethodName' format String.
	 */
	public static String generateCounterID(String counterPrefix, String methodName){
		int pos = methodName.lastIndexOf(".");
		
		if (-1 == pos) {
			return counterPrefix + "." + methodName;
		} else{			
			return counterPrefix + methodName.substring(methodName.lastIndexOf("."), methodName.length());
		}
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
	 * @return int, the local error count.
	 */
	protected static int trace(int errorCount){
		System.out.println("Regression Trace Error '"+errorCount+"' at "+Thread.currentThread().getStackTrace()[2]);
		return errorCount;
	}
	
	/**
	 * Initialize the Utilities for getting test directories/files.<br>
	 * Called at the beginning of our local {@link #runTest()} and 
	 * the subclass's local runTest such as {@link FilesTests#runTest()}.<br>
	 * @throws Throwable
	 */
	protected void initUtils() throws Throwable{
		try {
			if(utils==null) utils = new Utilities(Runner.jsafs());
		} catch (Exception e) {
			SeleniumPlus.AbortTest("Cannot initialize the Test Utilities! Met "+StringUtils.debugmsg(e));
		}
	}
	
	/**
	 * Internal. Can only be run AFTER the test runtime environment is initialized.
	 * Called at the beginning of our local runTest() and after calling of {@link #initUtils()}
	 * 
	 */
	void preparePostProcessing(){
		logsdir = utils.getLogsDir();
	}
	
	/**
	 * Run ALL enabled regression tests. 
	 * @see org.safs.selenium.webdriver.SeleniumPlus#main(java.lang.String[])
	 */
	@Override
	public void runTest() throws Throwable {
		
		initUtils();
		preparePostProcessing();
				
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
		fail += FilesTests.runRegressionTest(true);
		fail += DriverMiscCommandTests.runRegressionTest();
		
		fail += GenericMasterTests.runRegressionTest(enabledDomains);
		fail += GenericObjectTests.runRegressionTest(enabledDomains);
		fail += CheckBoxTests.runRegressionTest(enabledDomains);
		fail += ComboBoxTests.runRegressionTest(enabledDomains);
		fail += ListViewTests.runRegressionTest(enabledDomains);
		fail += MenuTests.runRegressionTest(enabledDomains);
		fail += TabControlTests.runRegressionTest(enabledDomains);
		fail += TreeViewTests.runRegressionTest(enabledDomains);
		fail += EditBoxTests.runRegressionTest(enabledDomains);
		fail += AutoItTests.runRegressionTest(enabledDomains);
		
		if(fail > 0){
			Logging.LogTestFailure("Regression reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess("Regression did not report any UNEXPECTED test failures!");
		}
		
		//if running from Eclipse, no local main() is needed.
		//but if running from command-line, a local main() is usually needed.
		setExitCode(fail);
		setAllowExit(false); // already false by default, but just in-case
	}
	
	/** Added to accomodate post-test processing for HTML reports. */
	public static void main(String[] args){
		
		SeleniumPlus.main(args);
		
		// Continue with post-test processing of HTML reports.
		
		if(logsdir instanceof File){
			try {
				File xmlfile = new CaseInsensitiveFile(logsdir, "Regression.xml").toFile();
				File xslfile = new CaseInsensitiveFile(logsdir, "regressionsummary.xsl").toFile();
				File outfile = new File(logsdir, "Regression_Summary.htm");
				XMLTransformer.transform(xmlfile, xslfile, outfile);
				
				xslfile = new CaseInsensitiveFile(logsdir, "failuresummary.xsl").toFile();
				outfile = new File(logsdir, "Regression_Failures.htm");
				XMLTransformer.transform(xmlfile, xslfile, outfile);
				
				xslfile = new CaseInsensitiveFile(logsdir, "timeConsumedSummary.xsl").toFile();
				outfile = new File(logsdir, "Regression_ConsumedTimeSummary.htm");
				XMLTransformer.transform(xmlfile, xslfile, outfile, XMLTransformer.XSLT_VERSION_2);
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		System.exit(exitCode);
	}
	
}
