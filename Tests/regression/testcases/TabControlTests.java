package regression.testcases;

import java.util.ArrayList;
import java.util.List;

import org.safs.Domains;
import org.safs.StringUtils;

import regression.Map;
import regression.testruns.Regression;

public class TabControlTests extends Regression{

	public static final String COUNTER = StringUtils.getClassName(0, false);

	/**
	 * <B>NOTE:</B>
	 * <pre>
	 * 1. TestBrowserName is suggested to be defined in the map file, if not defined, only firefox browser will be tested.
	 * [ApplicationContants]
	 * TestBrowserName="firefox"
	 * ;TestBrowserName="firefox chrome explorer"
	 * </pre>
	 * 
	 * @return int, the number of error occurs
	 * @throws Throwable
	 */
	private static int testAPI(String counterPrefix) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);

		String browsers = Map.TestBrowserName();
		if(browsers==null || browsers.trim().isEmpty()){
			browsers = FF;
			Logging.LogTestWarning(counterID+" cannot get TestBrowserName from map, use "+browsers);
		}
		browsers = browsers.replaceAll(" +", " ");
		String[] browserArray = browsers.split(" ");

		for(String browser: browserArray){
			if(Domains.isHtmlEnabled()) fail += testAPIForHtml(counterID, browser);
			if(Domains.isDojoEnabled()) fail += testAPIForDojo(counterID, browser);
			if(Domains.isSapEnabled()) fail+= testAPIForSAP(counterID, browser);
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

	private static int testAPIForHtml(String counterPrefix, String browser) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		String mapID = MAP_FILE_HTMLAPP;

		if(Misc.SetApplicationMap(mapID)){

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

	private static int testAPIForDojo(String counterPrefix, String browser) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		String mapID = MAP_FILE_DOJOAPP;

		if(Misc.SetApplicationMap(mapID)){

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

	private static int testAPIForSAP(String counterPrefix, String browser) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		String mapID = MAP_FILE_SAPDEMOAPP;

		if(Misc.SetApplicationMap(mapID)){
			String ID = null;

			try{
				ID = startBrowser(browser, Map.SAPDemoURL());
				if(ID!=null){
					fail += sap_test_tabcontrol(counterID, Map.SAPDemoPage.TabControl);
					
				}else{
					Logging.LogTestWarning(counterID+"StartWebBrowser '"+browser+"' Unsuccessful.");
					trace(++fail);
				}
			}catch(Exception e){
				trace(++fail);
				Logging.LogTestFailure(counterID+"Fail to test SAP Application in browser '"+browser+"'! Unexpected Exception "+StringUtils.debugmsg(e));
			}finally{
				if(ID!=null) if(!StopWebBrowser(ID)) trace(++fail);
			}

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

	private static int sap_test_tabcontrol(String counterPrefix, org.safs.model.Component tabcontrol) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		String debugmsg = StringUtils.debugmsg(false);

		boolean originalExpressionOn = Misc.isExpressionsOn();
		if(!Misc.Expressions(false)){
			Logging.LogTestWarning(debugmsg+" Fail to turn off expression, some tests will fail!");
			trace(++fail);
		}

		String value = "JPan";
		int index = -1;
		
		if(!TabControl.ClickTab(tabcontrol, value)) trace(++fail);
		
		value = "Calc";
		if(!TabControl.ClickTabContains(tabcontrol, value)) trace(++fail);
		
		value = "JTable2";
		if(!TabControl.MakeSelection(tabcontrol, value)) trace(++fail);
		
		value = "JTable";
		if(!TabControl.SelectTab(tabcontrol, value)) trace(++fail);
		
		value = "Top Secret";//not visible, should fail
		if(TabControl.SelectTab(tabcontrol, value)) trace(++fail);
		
		index = 1;
		if(!TabControl.SelectTabIndex(tabcontrol, index)) trace(++fail);
		
		index = 5;
		if(!TabControl.SelectTabIndex(tabcontrol, index)) trace(++fail);
		
		index = -1;//out of boundary, should fail
		if(TabControl.SelectTabIndex(tabcontrol, index)) trace(++fail);
		
		index = 12;//out of boundary, should fail
		if(TabControl.SelectTabIndex(tabcontrol, index)) trace(++fail);
		
		value = "JTable4";
		if(!TabControl.UnverifiedClickTab(tabcontrol, value)) trace(++fail);

		if(!Misc.Expressions(originalExpressionOn)){
			Logging.LogTestWarning(debugmsg+" Fail to turn expression back to '"+originalExpressionOn+"'.");
			trace(++fail);
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
	 * @return
	 * @throws Throwable
	 */
	public static int runRegressionTest(List<String> enabledDomains) throws Throwable{
		int fail = 0;
		Counters.StartCounter(COUNTER);

		try{
			for(String domain: enabledDomains) Domains.enableDomain(domain);
			fail += testAPI(COUNTER);

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
		initUtils();
		runRegressionTest(enabledDomains);
	}

}