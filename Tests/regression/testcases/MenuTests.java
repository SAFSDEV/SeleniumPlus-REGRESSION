package regression.testcases;

import java.util.ArrayList;
import java.util.List;

import org.safs.Domains;
import org.safs.StringUtils;
import org.safs.model.tools.EmbeddedHookDriverRunner;

import regression.Map;
import regression.testruns.Regression;

public class MenuTests extends Regression{

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

		if(Misc.SetApplicationMap(mapID)){
			String ID = null;

			try{
				ID = startBrowser(browser, Map.SAPDemoURL());
				if(ID!=null){
					fail += sap_test_menu(Map.SAPDemoPage.MenuBar);

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

	private static int sap_test_menu(org.safs.model.Component menubar) throws Throwable{
		int fail = 0;
		String debugmsg = StringUtils.debugmsg(false);

		boolean originalExpressionOn = Misc.isExpressionsOn();
		//We must turn off expression, otherwise "Tools->JTree Viewer" will be evaluated to "0"
		if(!Misc.Expressions(false)){
			Logging.LogTestWarning(debugmsg+" Fail to turn off expression, some tests will fail!");
			trace(++fail);
		}
		
		String path =  "Tools->JTree Viewer";
		String indexPath = "";

		if(!Menu.SelectMenuItem(menubar, path)) trace(++fail);

		path =  "Tools->Basic Components";
		if(!Menu.SelectMenuItem(menubar, path, "1->1")) trace(++fail);

		path =  "Too->Viewer";
		indexPath = "1->3";
		if(!Menu.SelectMenuItemContains(menubar, path, indexPath)) trace(++fail);

		path =  "Too[l|L]s->[A-Z]*able[1-4] Viewer";
		if(!Menu.SelectMenuItem(menubar, path)) trace(++fail);

		String expectedStatus = "Enabled";
		path = "Tools->JTree Viewer";
		indexPath = "";
		if(!Menu.VerifyMenuItem(menubar, path, expectedStatus, indexPath)) trace(++fail);

		expectedStatus = "Enabled Bitmap";
		path = "Tools->JTree Viewer";
		indexPath = "";
		if(Menu.VerifyMenuItem(menubar, path, expectedStatus, indexPath)) trace(++fail);

		expectedStatus = "Enabled Menu With 10 MenuItems";
		path = "Tool";
		indexPath = "";
		if(!Menu.VerifyMenuItemContains(menubar, path, expectedStatus, indexPath)) trace(++fail);

		path =  "Weird->Second->Third";
		if(!Menu.SelectMenuItem(menubar, path)) trace(++fail);

		//Test some menu-items defined with qualifier "Path" in the map
		//Important note: the defined menu-item must be visible on the page, you can click to show it.
		String itemPic = "MenuToolsItem.png";
		if(GetGUIImage(Map.SAPDemoPage.MenuToolsItem, itemPic)){
			if(Files.CopyFile(utils.testFile(itemPic), utils.benchFile(itemPic))){
				if(!VerifyGUIImageToFile(Map.SAPDemoPage.MenuToolsItem, utils.benchFile(itemPic))) trace(++fail);
			}else{
				Logging.LogTestWarning(debugmsg+" Fail to copy file '"+itemPic+"', miss VerifyGUIImageToFile test.");
				trace(++fail);
			}
		}else{
			trace(++fail);
		}
		
		itemPic = "MenuBasicComponentsItem.png";
		//Click to show the Tools submenu, so that menu-item in it will be shown
		//if(Menu.SelectMenuItem(Map.SAPDemoPage.MenuBar, "Tools")){//This cannot keep the submenu shown, and following tests will fail
		if(Click(Map.SAPDemoPage.MenuToolsItem, path)){
			if(!GetGUIImage(Map.SAPDemoPage.MenuBasicComponentsItem, itemPic)) trace(++fail);
			itemPic = "MenuToolViewerItem.png";
			if(!GetGUIImage(Map.SAPDemoPage.MenuToolViewerItem, itemPic)) trace(++fail);
		}else{
			Logging.LogTestFailure(debugmsg+"fail select item 'Tools', missed some tests");
			trace(++fail);
		}

		//Set back the "Expression"
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