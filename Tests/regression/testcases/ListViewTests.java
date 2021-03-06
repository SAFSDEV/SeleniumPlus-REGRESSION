package regression.testcases;

import java.util.ArrayList;
import java.util.List;

import org.safs.Domains;
import org.safs.StringUtils;
import org.safs.selenium.webdriver.SeleniumPlus;
import org.safs.tools.stringutils.StringUtilities;

import regression.Map;
import regression.testruns.Regression;

public class ListViewTests extends Regression{

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
		String debugmsg = StringUtils.debugmsg(false);

		if(Misc.SetApplicationMap(mapID)){
			String ID = null;

			try{
				ID = startBrowser(browser, Map.SAPDemoURL());
				if(ID!=null){
					if(TabControl.SelectTab(Map.SAPDemoPage.TabControl, Map.Tab_basc_comp())){
						fail += sap_test_selection(counterID, Map.SAPDemoPage.ListView);
						fail += sap_test_capturedata(counterID, Map.SAPDemoPage.ListView);
						fail += sap_test_rs_defined_list_item(counterID);//Test list-item in Map.SAPDemoPage.ListView
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

	private static int sap_test_selection(String counterPrefix, org.safs.model.Component listview) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);

		String text = "Kansas";
		int index = -1;
		String variable = "existence";
		String coords = "3;3";
		String debugmsg = StringUtils.debugmsg(false);

		if(ListView.SetListContains(listview, text, variable)){
			Logging.LogMessage("Successfully get existence of item '"+text+"', and set "+GetVariableValue(variable)+" to variable '"+variable+"'");
			boolean exist = StringUtilities.convertBool(GetVariableValue(variable));
			if(!exist) trace(++fail);
		}else{
			Logging.LogTestWarning(debugmsg+"SetListContains Failed. Some tests missed.");
			trace(++fail);
		}

		if(!ListView.SelectTextItem(listview, text)) trace(++fail);

		text = "tucky";
		if(!ListView.SelectPartialMatch(listview, text)) trace(++fail);

		text = "Kansas";
		if(!ListView.ActivateTextItemCoords(listview, text, coords)) trace(++fail);

		text = "New Jersey";
		if(!ListView.ActivateUnverifiedTextItemCoords(listview, text, coords)) trace(++fail);

		text = "Puerto Rico";
		if(!ListView.SelectTextItemCoords(listview, text, coords)) trace(++fail);

		index = 25;
		coords = "10;10";
		if(!ListView.SelectIndexItemCoords(listview, index, coords)) trace(++fail);

		text = "Wyoming";
		if(!ListView.SelectUnverifiedTextItemCoords(listview, text, coords)) trace(++fail);

		text = "North Dakota";
		if(!ListView.RightClickTextItem(listview, text)) trace(++fail);

		text = "Maryland";
		if(!ListView.RightClickTextItemCoords(listview, text, coords)) trace(++fail);

		text = "sas";
		if(ListView.SelectPartialMatch(listview, text, 2)){//Kansas
			Logging.LogMessage("Successfully select 2th item containing '"+text+"'");

			//Verify the selection
			text = "Kansas";//this is the second "sas" in list
			if(!ListView.VerifySelectedItem(listview, text)) trace(++fail);

			text = "Arkansas";//this is the first "sas" in list, should not be selected, should fail
			Logging.LogFailureOK("Expected Failure: 'Arkansas' is the first 'sas' in list, and it is not selected, verification will fail.");
			if(ListView.VerifySelectedItem(listview, text)) trace(++fail);

			//Arkansas not selected, will pass
			if(!ListView.VerifyItemUnselected(listview, text)) trace(++fail);

		}else{
			Logging.LogTestWarning(debugmsg+"SelectPartialMatch:Fail to select the second '"+text+"' in list. Some tests missed.");
			trace(++fail);
		}

		text = "Arkansas";
		if(ListView.SelectUnverifiedTextItem(listview, text)){//Arkansas
			//Verify the selection
			if(!ListView.VerifySelectedItem(listview, text)) trace(++fail);

			//'Arkansas' is selected, VerifyItemUnselected will fail
			Logging.LogFailureOK("Expected Failure: 'Arkansas' is selected, VerifyItemUnselected will fail.");
			if(ListView.VerifyItemUnselected(listview, text)) trace(++fail);

		}else{
			Logging.LogTestWarning(debugmsg+"SelectUnverifiedTextItem:Fail to select the '"+text+"' in list. Some tests missed.");
			trace(++fail);
		}

		index = 1;
		if(!ListView.SelectIndex(listview, index)) trace(++fail);

		index = 5;
		if(!ListView.SelectIndexItem(listview, index)) trace(++fail);

		index = -1;//out of boundary, should fail
		Logging.LogFailureOK("Expected Failure: the index '"+index+"' is out of boundary, SelectIndex will fail.");
		if(ListView.SelectIndex(listview, index)) trace(++fail);

		index = 282;//out of boundary, should fail
		Logging.LogFailureOK("Expected Failure: the index '"+index+"' is out of boundary, SelectIndex will fail.");
		if(ListView.SelectIndexItem(listview, index)) trace(++fail);

		text = "Montana";
		if(ListView.SelectTextItem(listview, text)){
			text = "Nevada";
			if(!ListView.ExtendSelectionToTextItem(listview, text)) trace(++fail);

			text = "Ohio";
			if(!ListView.SelectAnotherTextItem(listview, text)) trace(++fail);

			text = "sylvania";
			if(!ListView.SelectAnotherPartialMatch(listview, text)) trace(++fail);

		}else{
			Logging.LogTestWarning(debugmsg+"SelectTextItem:Fail to select the '"+text+"' in list. Some tests missed.");
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

	private static int sap_test_capturedata(String counterPrefix, org.safs.model.Component listview){
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		String debugmsg = StringUtils.debugmsg(false);

		String filename = "listview_content.txt";

		if(ListView.CaptureItemsToFile(listview, quote(filename))){
			if(Files.CopyFile(quote(utils.testFile(filename)), quote(utils.benchFile(filename)))){
				if(!SeleniumPlus.VerifyFileToFile(quote(utils.testFile(filename)), quote(utils.benchFile(filename)))) trace(++fail);
			}else{
				Logging.LogTestWarning(debugmsg+"CopyFile Failed. Some tests missed.");
				trace(++fail);
			}
		}else{
			Logging.LogTestWarning(debugmsg+"CaptureItemsToFile Failed. Some tests missed.");
			trace(++fail);
		}

		filename = "Actuals/listview-project.txt";
		if(!ListView.CaptureItemsToFile(listview, quote(filename))) trace(++fail);

		filename = "/Actuals/listview-project2.txt";
		if(!ListView.CaptureItemsToFile(listview, quote(filename))) trace(++fail);

		filename = "./Actuals/listview-project3.txt";
		if(!ListView.CaptureItemsToFile(listview, quote(filename))) trace(++fail);

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

	private static int sap_test_rs_defined_list_item(String counterPrefix){
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		String debugmsg = StringUtils.debugmsg(false);

		String file = "ListViewItem26_ComputedStyle.dat";
		//ListViewItem26 is defined with qualifier "ItemIndex"
		if(Component.GetComputedStyle(Map.SAPDemoPage.ListViewItem26, file)){
			if(Files.CopyFile(quote(utils.testFile(file)), quote(utils.benchFile(file)))){
				if(!Component.VerifyComputedStyle(Map.SAPDemoPage.ListViewItem26, file)) trace(++fail);
			}else{
				Logging.LogTestWarning(debugmsg+"CopyFile Failed. Some tests missed.");
				trace(++fail);
			}
		}else{
			Logging.LogTestWarning(debugmsg+"CaptureItemsToFile Failed. Some tests missed.");
			trace(++fail);
		}

		//ListViewItemAr is defined with qualifier "Path"
		if(!Component.CapturePropertiesToFile(Map.SAPDemoPage.ListViewItemAr, "ItemAr.txt")) trace(++fail);
		//ListViewItemFederated is defined with qualifier "Path"
		if(!Component.CapturePropertiesToFile(Map.SAPDemoPage.ListViewItemFederated, "ItemFederated.txt")) trace(++fail);
		//ListViewItem26 is defined with qualifier "ItemIndex"
		if(!Component.CapturePropertiesToFile(Map.SAPDemoPage.ListViewItem26, "Item26.txt")) trace(++fail);

		//Some list-items defined with xpath
		if(!Component.CapturePropertiesToFile(Map.SAPDemoPage.ListViewItem5, "Item5.txt")) trace(++fail);
		if(!Component.CapturePropertiesToFile(Map.SAPDemoPage.ListViewItemArizona, "ItemArizona.txt")) trace(++fail);
		if(!Component.CapturePropertiesToFile(Map.SAPDemoPage.ListViewItemAlaska, "ItemAlaska.txt")) trace(++fail);

		file = "ItemAlaska.properties";
		if(Component.CapturePropertiesToFile(Map.SAPDemoPage.ListViewItemAlaska, file, quote("utf-8"))){
			if(Files.CopyFile(quote(utils.testFile(file)), quote(utils.benchFile(file)))){
				if(!Component.VerifyPropertiesToFile(Map.SAPDemoPage.ListViewItemAlaska, file, quote("utf-8"))) trace(++fail);
			}else{
				Logging.LogTestWarning(debugmsg+"CopyFile Failed. Some tests missed.");
				trace(++fail);
			}
		}else{
			Logging.LogTestWarning(debugmsg+"CaptureItemsToFile Failed. Some tests missed.");
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
	 *
	 * @param Runner EmbeddedHookDriverRunner
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
		String counterID = StringUtils.debugmsg(false);
		List<String> enabledDomains = new ArrayList<String>();
		enabledDomains.add(Domains.HTML_DOMAIN);
		enabledDomains.add(Domains.HTML_DOJO_DOMAIN);
		enabledDomains.add(Domains.HTML_SAP_DOMAIN);
		initUtils();

		boolean originalExpression = Misc.isExpressionsOn();
		try{
			if(originalExpression){
				if(!Misc.Expressions(false)){
					Logging.LogTestWarning(counterID+"Failed to turned off Expression!");
				}
			}
			runRegressionTest(enabledDomains);
		}finally{
			if(!Misc.Expressions(originalExpression)){
				Logging.LogTestWarning(counterID+"Failed to set Expression to its original value '"+originalExpression+"'");
			}
		}
	}

}