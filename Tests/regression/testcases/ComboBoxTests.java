package regression.testcases;

import java.util.ArrayList;
import java.util.List;

import org.safs.Domains;
import org.safs.StringUtils;
import org.safs.model.tools.EmbeddedHookDriverRunner;

import regression.Map;
import regression.testruns.Regression;

public class ComboBoxTests extends Regression{

	public static final String COUNTER = StringUtils.getClassName(0, false);
	static Utilities utils = null;

	/**
	 * <B>NOTE:</B>
	 * <pre>
	 * 1. The static field 'utils' should have been initialized, utils = new Utilities(Runner.jsafs());
	 * 2. TestBrowserName is suggested to be defined in the map file, if not defined, only firefox browser will be tested.
	 * [ApplicationContants]
	 * TestBrowserName="firefox"
	 * ;TestBrowserName="firefox chrome explorer"
	 * </pre>
	 * 
	 * @return int, the number of error occurs
	 * @throws Throwable
	 */
	private static int testAPI() throws Throwable{
		int fail = 0;

		String browsers = Map.TestBrowserName();
		if(browsers==null || browsers.trim().isEmpty()){
			browsers = FF;
			Logging.LogTestWarning(COUNTER+" cannot get TestBrowserName from map, use "+browsers);
		}
		browsers = browsers.replaceAll(" +", " ");
		String[] browserArray = browsers.split(" ");

		for(String browser: browserArray){
			if(Domains.isHtmlEnabled()) fail += testAPIForHtml(browser);
			if(Domains.isDojoEnabled()) fail += testAPIForDojo(browser);
			if(Domains.isSapEnabled()) fail+= testAPIForSAP(browser);
		}

		return fail;
	}

	private static int testAPIForHtml(String browser) throws Throwable{
		int fail = 0;
		String mapID = MAP_FILE_HTMLAPP;

		if(Misc.SetApplicationMap(mapID)){

		}else{
			trace(++fail);
			Logging.LogTestFailure(COUNTER+"Fail to load map '"+mapID+"', cannot test in browser '"+browser+"'!");
		}
		return fail;
	}

	private static int testAPIForDojo(String browser) throws Throwable{
		int fail = 0;
		String mapID = MAP_FILE_DOJOAPP;

		if(Misc.SetApplicationMap(mapID)){

		}else{
			trace(++fail);
			Logging.LogTestFailure(COUNTER+"Fail to load map '"+mapID+"', cannot test in browser '"+browser+"'!");
		}
		return fail;
	}

	private static int testAPIForSAP(String browser) throws Throwable{
		int fail = 0;
		String mapID = MAP_FILE_SAPDEMOAPP;
		String debugmsg = StringUtils.debugmsg(false);

		if(Misc.SetApplicationMap(mapID)){
			String ID = null;

			try{
				ID = startBrowser(browser, Map.SAPDemoURL());
				if(ID!=null){
					if(TabControl.SelectTab(Map.SAPDemoPage.TabControl, Map.Tab_basc_comp())){
						fail += sap_test_combobox(Map.SAPDemoPage.Basc_ComboBox);
					}else{
						Logging.LogTestWarning(debugmsg+"SelectTab Fail to select tab '"+Map.Tab_basc_comp()+"'. All tests missed.");
						trace(++fail);
					}
				}else{
					Logging.LogTestWarning(COUNTER+"StartWebBrowser '"+browser+"' Unsuccessful.");
					trace(++fail);
				}
			}catch(Exception e){
				trace(++fail);
				Logging.LogTestFailure(COUNTER+"Fail to test SAP Application in browser '"+browser+"'! Unexpected Exception "+StringUtils.debugmsg(e));
			}finally{
				if(ID!=null) if(!StopWebBrowser(ID)) trace(++fail);
			}

			Misc.CloseApplicationMap(mapID);
		}else{
			trace(++fail);
			Logging.LogTestFailure(COUNTER+"Fail to load map '"+mapID+"', cannot test in browser '"+browser+"'!");
		}

		return fail;
	}

	private static int sap_test_combobox(org.safs.model.Component combobox) throws Throwable{
		int fail = 0;
		String debugmsg = StringUtils.debugmsg(false);

		boolean originalExpressionOn = Misc.isExpressionsOn();
		if(!Misc.Expressions(false)){
			Logging.LogTestWarning(debugmsg+" Fail to turn off expression, some tests will fail!");
			trace(++fail);
		}

		if(!ComboBox.ShowList(combobox)) trace(++fail);
		Pause(1);
		if(!ComboBox.HideList(combobox)) trace(++fail);
		Pause(1);

		if(!ComboBox.CaptureItemsToFile(combobox, "ComboBoxData.txt")) trace(++fail);

		if(!ComboBox.CaptureItemsToFile(combobox, "ComboBoxDataUTF8.txt", "UTF-8")) trace(++fail);

		String item = null;

		item = "Alaska";
		if(!ComboBox.Select(combobox, item)) trace(++fail);

		item = "kansa";
		if(!ComboBox.SelectPartialMatch(combobox, item)) trace(++fail);

		item = "Alabama";
		if(ComboBox.SelectUnverified(combobox, item)){
			if(!ComboBox.VerifySelected(combobox, item)) trace(++fail);
		}else{
			Logging.LogTestWarning(debugmsg+"SelectUnverified fail select item '"+item+"', miss VerifySelected.");
			trace(++fail);
		}

		int index = 1;
		if(!ComboBox.SelectIndex(combobox, index)) trace(++fail);

		index = 4;
		if(!ComboBox.SelectIndex(combobox, index)) trace(++fail);

		if(!Misc.Expressions(originalExpressionOn)){
			Logging.LogTestWarning(debugmsg+" Fail to turn expression back to '"+originalExpressionOn+"'.");
			trace(++fail);
		}

		return fail;
	}

	/**
	 * 
	 * @param Runner EmbeddedHookDriverRunner
	 * @return
	 * @throws Throwable
	 */
	public static int runRegressionTest(EmbeddedHookDriverRunner Runner, List<String> enabledDomains) throws Throwable{
		int fail = 0;
		Counters.StartCounter(COUNTER);

		try{
			for(String domain: enabledDomains) Domains.enableDomain(domain);
			utils = new Utilities(Runner.jsafs());
			fail += testAPI();

		}catch(Throwable t){
			trace(++fail);
			Logging.LogTestFailure(COUNTER +" fatal error due to "+t.getClass().getName()+", "+ t.getMessage());
		}

		Counters.StopCounter(COUNTER);
		Counters.StoreCounterInfo(COUNTER, COUNTER);
		Counters.LogCounterInfo(COUNTER);

		if(fail > 0){
			Logging.LogTestFailure(COUNTER+" reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(COUNTER+" did not report any UNEXPECTED test failures!");
		}
		return fail;
	}

	public void runTest() throws Throwable{
		List<String> enabledDomains = new ArrayList<String>();
		enabledDomains.add(Domains.HTML_DOMAIN);
		enabledDomains.add(Domains.HTML_DOJO_DOMAIN);
		enabledDomains.add(Domains.HTML_SAP_DOMAIN);

		runRegressionTest(Runner, enabledDomains);
	}

}