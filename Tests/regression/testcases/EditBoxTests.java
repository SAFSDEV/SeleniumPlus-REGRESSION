package regression.testcases;

import java.util.ArrayList;
import java.util.List;

import org.safs.Domains;
import org.safs.StringUtils;

import regression.Map;
import regression.testruns.Regression;

public class EditBoxTests extends Regression{

	public static final String COUNTER = StringUtils.getClassName(0, false);

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

		//If the expression is on, turn it off; otherwise it will affect the "input text string"
		boolean isExpressionOn = Misc.isExpressionsOn();
		if(isExpressionOn) Misc.Expressions(false);

		for(String browser: browserArray){
			if(Domains.isHtmlEnabled()) fail += testAPIForHtml(counterID, browser);
			if(Domains.isDojoEnabled()) fail += testAPIForDojo(counterID, browser);
			if(Domains.isSapEnabled()) fail+= testAPIForSAP(counterID, browser);
		}

		//Set back to original expression
		Misc.Expressions(isExpressionOn);

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
		String debugmsg = StringUtils.debugmsg(false);

		if(Misc.SetApplicationMap(mapID)){
			String ID = null;

			try{
				ID = startBrowser(browser, Map.SAPDemoURL());
				if(ID!=null){
					if(TabControl.SelectTab(Map.SAPDemoPage.TabControl, Map.Tab_basc_comp())){
						fail += sap_test_editbox(counterID, Map.SAPDemoPage.Basc_TextArea);
						fail += sap_test_editbox(counterID, Map.SAPDemoPage.Basc_Input);
					}else{
						Logging.LogTestWarning(debugmsg+"SelectTab Fail to select tab '"+Map.Tab_basc_comp()+"'. All tests missed.");
						trace(++fail);
					}
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

	private static int sap_test_editbox(String counterPrefix, org.safs.model.Component editbox) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);

		String value = "Some Text";
		if(!EditBox.SetTextValue(editbox, value)) trace(++fail);
		if(!EditBox.SetUnverifiedTextValue(editbox, value)) trace(++fail);
		if(!EditBox.SetTextCharacters(editbox, value)) trace(++fail);
		if(!EditBox.SetUnverifiedTextCharacters(editbox, value)) trace(++fail);

		value = "Some Text with special keys +(abcd)";
		if(!EditBox.SetTextValue(editbox, value)) trace(++fail);
		if(!EditBox.SetUnverifiedTextValue(editbox, value)) trace(++fail);
		if(!EditBox.SetTextCharacters(editbox, value)) trace(++fail);
		if(!EditBox.SetUnverifiedTextCharacters(editbox, value)) trace(++fail);

		//Test input string with leading/ending spaces
		boolean originalExpressionOn = Misc.isExpressionsOn();
		Misc.Expressions(true);//Turn on the expression so that DDVarialbe will be evaluated
		String variableName = "tempvar";
		String variableValue = "HHHH";
		Misc.SetVariableValueEx(variableName, variableValue);
		//If we turn on the 'normalizeTextForInput', leading/ending spaces will be kept and DDVariable will not be parsed
		boolean originalNormalizeText = setNormalizeTextForInput(true);
		//" " is expected
		if(!EditBox.SetTextCharacters(editbox, " ")) trace(++fail);
		//" ^tempvar " is expected
		if(!EditBox.SetTextCharacters(editbox, " ^"+variableName+" ")) trace(++fail);

		//If we turn off the 'normalizeTextForInput', leading/ending spaces will be trimmed and DDVariable will be parsed
		setNormalizeTextForInput(false);
		//"" is expected
		if(!EditBox.SetTextCharacters(editbox, " ")) trace(++fail);
		//"HHHH" is expected
		if(!EditBox.SetTextCharacters(editbox, " ^"+variableName+" ")) trace(++fail);

		//Set 'normalizeTextForInput' and 'Expression' to original value.
		setNormalizeTextForInput(originalNormalizeText);
		Misc.Expressions(originalExpressionOn);

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