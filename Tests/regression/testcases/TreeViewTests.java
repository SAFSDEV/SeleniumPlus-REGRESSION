package regression.testcases;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.safs.Domains;
import org.safs.StringUtils;
import org.safs.model.tools.EmbeddedHookDriverRunner;
import org.safs.selenium.webdriver.lib.SeleniumPlusException;
import org.safs.tools.stringutils.StringUtilities;

import regression.Map;
import regression.testruns.Regression;

public class TreeViewTests extends Regression{

	public static final String COUNTER = StringUtils.getClassName(0, false);
	private static java.util.Map<String/*domain name*/, Set<String>/*a set of map ID*/> domain2Map = null;
	
	/** MUST be called from runTest(), or a method called from runTest()*/
	protected synchronized static void initializeDomain2Map(){
		if(domain2Map!=null){
			//this means that we have already initialized this map.
			return;
		}
		
		domain2Map = new HashMap<String, Set<String>>();
		Set<String> maps = null;
		
		maps = new HashSet<String>();
		maps.add(MAP_FILE_HTMLAPP);
		File[] files = null;
		File dir = new File(utils.mapsDir()+Domains.HTML_DOMAIN);
		if(dir!=null && dir.exists() && dir.isDirectory()){
			//TODO to handle the map order
			files = dir.listFiles();
			for(File file:files){
				maps.add(file.getAbsolutePath());
			}
		}
		domain2Map.put(Domains.HTML_DOMAIN, maps);
		
		maps = new HashSet<String>();
		maps.add(MAP_FILE_DOJOAPP);
		dir = new File(utils.mapsDir()+Domains.HTML_DOJO_DOMAIN);
		if(dir!=null && dir.exists() && dir.isDirectory()){
			files = dir.listFiles();
			for(File file:files){
				maps.add(file.getAbsolutePath());
			}
		}
		domain2Map.put(Domains.HTML_DOJO_DOMAIN, maps);
		
		maps = new HashSet<String>();
		maps.add(MAP_FILE_SAPDEMOAPP);
		dir = new File(utils.mapsDir()+Domains.HTML_SAP_DOMAIN);
		if(dir!=null && dir.exists() && dir.isDirectory()){
			files = dir.listFiles();
			for(File file:files){
				maps.add(file.getAbsolutePath());
			}
		}
		domain2Map.put(Domains.HTML_SAP_DOMAIN, maps);
		
	}
	
	protected static void addMapIDFor(String domain, String mapID){
		Set<String> maps = domain2Map.get(domain);
		if(maps==null) maps = new HashSet<String>();
		maps.add(mapID);
	}
	
	protected static Set<String> getMapIDs(String domain) throws SeleniumPlusException{
		if(!domain2Map.containsKey(domain)){
			throw new SeleniumPlusException("No MapID for domain '"+domain+"'");
		}
		return domain2Map.get(domain);
	}
	
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
			Logging.LogTestWarning(COUNTER+" cannot get TestBrowserName from map, use "+browsers);
		}
		browsers = browsers.replaceAll(" +", " ");
		String[] browserArray = browsers.split(" ");

		for(String browser: browserArray){
			if(Domains.isHtmlEnabled()) fail += testAPI(counterID, browser, Domains.HTML_DOMAIN);
			if(Domains.isDojoEnabled()) fail += testAPI(counterID, browser, Domains.HTML_DOJO_DOMAIN);
			if(Domains.isSapEnabled()) fail+= testAPI(counterID, browser, Domains.HTML_SAP_DOMAIN);
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
	
	private static int testAPI(String counterPrefix, String browser, String domain) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		
		//Load domain-related maps, the map order???
		Set<String> mapIDs = getMapIDs(domain);
		Set<String> openedMapIDs = new HashSet<String>();
		
		for(String mapid:mapIDs){
			if(Misc.SetApplicationMap(mapid)){
				openedMapIDs.add(mapid);
			}else{
				Logging.LogTestWarning(COUNTER+"Fail to load map '"+mapid+", this may affect following test!");
				trace(++fail);
			}
		}
		
		//If we have successfully opened some maps, then we should try the test
		if(!openedMapIDs.isEmpty()){
			if(Domains.HTML_SAP_DOMAIN.equals(domain)) fail += sap_test(counterID, browser);
			if(Domains.HTML_DOJO_DOMAIN.equals(domain)) fail += dojo_test(counterID, browser);
			if(Domains.HTML_DOMAIN.equals(domain)) fail += html_test(counterID, browser);
			
			//After testing, close the Map files
			for(String mapid:openedMapIDs){
				if(!Misc.CloseApplicationMap(mapid)){
					Logging.LogTestWarning(COUNTER+"Fail to close '"+mapid+", this may affect other test!");
					trace(++fail);
				}
			}
		}else{
			Logging.LogTestFailure(COUNTER+"No related map is loaded, cannot test in browser '"+browser+"' for domain '"+domain+"'!");
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

	private static int html_test(String counterPrefix, String browser){
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		
		
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
	
	private static int dojo_test(String counterPrefix, String browser){
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		
		
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
	
	private static int sap_test(String counterPrefix, String browser){
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		String debugmsg = StringUtils.debugmsg(false);
		String ID = null;
		
		try{
			ID = startBrowser(browser, Map.SAPDemoURL());
			if(ID!=null){
				String tab = Map.Tab_jtree2();
				if(TabControl.SelectTab(Map.SAPDemoPage.TabControl, tab)){
					fail += sap_test_treeview(counterID, Map.SAPDemoPage.TreeView);
					
				}else{
					Logging.LogTestWarning(debugmsg+"SelectTab Fail to select tab '"+tab+"'. All tests missed.");
					trace(++fail);
				}
			}else{
				Logging.LogTestWarning(COUNTER+"StartWebBrowser '"+browser+"' Unsuccessful.");
				trace(++fail);
			}
		}catch(Throwable e){
			trace(++fail);
			Logging.LogTestFailure(COUNTER+"Fail to test Application in browser '"+browser+"'! Unexpected Exception "+StringUtils.debugmsg(e));
		}finally{
			if(ID!=null) if(!StopWebBrowser(ID)) trace(++fail);
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
	
	private static int sap_test_treeview(String counterPrefix, org.safs.model.Component treeview) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		String debugmsg = StringUtils.debugmsg(false);

		String path =  "Jim Goodnight->Armistead Sapp";
		String unselectedPath = "";
		String variable = "existence";
		String filename = "armis.dat";
		
		boolean originalExpressionOn = Misc.isExpressionsOn();
		//We must turn off expression, otherwise "Tools->JTree Viewer" will be evaluated to "0"
		if(!Misc.Expressions(false)){
			Logging.LogTestWarning(debugmsg+" Fail to turn off expression, some tests will fail!");
			trace(++fail);
		}
		
		//"Jim Goodnight->Armistead Sapp" should exist
		if(Tree.SetTreeContainsNode(treeview, path, variable)){
			if(!StringUtilities.convertBool(GetVariableValue(variable))) trace(++fail);
		}else{
			trace(++fail);
		}
		
		//capture content of node "Armistead Sapp" to file "armis.dat"
		if(!Tree.CaptureTreeDataToFile(treeview, filename, path, "\t", "UTF-8")) trace(++fail);
		
		//capture the whole tree content
		filename = "tree.dat";
		path = "";
		if(!Tree.CaptureTreeDataToFile(treeview, filename, path, "--", "UTF-8")) trace(++fail);
		
		path =  "Jim Goodnight->Armistead->reas Diggel->Steve Beatr->Liu";
		if(!Tree.ClickPartial(treeview, path)) trace(++fail);
		
		//VerifyTreeContainsNode should not verify 'partial path'
		if(Tree.VerifyTreeContainsNode(treeview, path)) trace(++fail);
		
		path =  "Jim Goodnight->Armistead->reas Diggel->u";
		if(!Tree.ClickPartial(treeview, path, 3)) trace(++fail);
		
		if(!Tree.VerifyTreeContainsPartialMatch(treeview, path)) trace(++fail);
		
		path =  "Jim Goodnight->Armistead Sapp";
		if(!Tree.ClickTextNode(treeview, path)) trace(++fail);
		
		if(!Tree.VerifySelectedNode(treeview, path)) trace(++fail);
		
		unselectedPath = "Jim Goodnight";
		if(!Tree.VerifyNodeUnselected(treeview, unselectedPath)) trace(++fail);
		
		path = "Jim Goodnight->Armistead Sapp->Andreas Diggelmann->Steve Beatrous->Alfred Liu";
		if(!Tree.VerifyTreeContainsNode(treeview, path)) trace(++fail);
		
		if(!Tree.ClickUnverifiedTextNode(treeview, path)) trace(++fail);
		
		if(!Tree.VerifySelectedNode(treeview, path)) trace(++fail);
		
		path = "Jim Goodnight";
		if(!Tree.Collapse(treeview, path)) trace(++fail);
		
		path = "Jim Goodnight->Armistead Sapp->Andreas Diggelmann->Steve Beatrous->Alfred Liu";
		if(!Tree.Expand(treeview, path)) trace(++fail);
		
		path = "Jim Goodnight->Armistead";
		if(!Tree.CollapsePartial(treeview, path)) trace(++fail);
		
		path = "Jim Goodnight->Armistead Sapp->Andreas Diggelmann->Steve Beatrous->Alfred Liu";
		if(!Tree.Select(treeview, path)) trace(++fail);
		
		path = "Jim Goodnight->Armistead Sapp->Andreas Diggelmann->Steve Beatrous->Craig Martin";
		if(!Tree.ShiftClickUnverifiedTextNode(treeview, path)) trace(++fail);
		
		path = "Jim Goodnight->Armistead Sapp->Andreas Diggelmann->Steve Beatrous->Craig Martin";
		if(!Tree.CtrlClickUnverifiedTextNode(treeview, path)) trace(++fail);
		
		path = "Jim Goodnight->Armistead Sapp->Andreas Diggelmann->Steve Beatrous->Alfred Liu";
		if(!Tree.RightClickTextNode(treeview, path)) trace(++fail);
		if(!TypeKeys("{Esc}")) trace(++fail);

		path = "Jim Goodnig->istead Sapp->Andreas Diggelm";
		if(!Tree.RightClickPartial(treeview, path)) trace(++fail);
		if(!TypeKeys("{Esc}")) trace(++fail);
		
//		path = "Jim Goodnight->Armistead Sapp->Andreas Diggelmann->Steve Beatrous";
		path = "Jim Goodnight->Armistead Sapp->Andreas Diggelmann->St[a-z]* Beatrous";
		if(!Tree.RightClickUnverifiedTextNode(treeview, path)) trace(++fail);
		if(!TypeKeys("{Esc}")) trace(++fail);
		
		
		path = "Jim Goodnight->Keith Collins";
		if(!Tree.DoubleClickTextNode(treeview, path)) trace(++fail);

		//Set back the "Expression"
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
	 * 
	 * @param Runner EmbeddedHookDriverRunner
	 * @return
	 * @throws Throwable
	 */
	public static int runRegressionTest(EmbeddedHookDriverRunner Runner, List<String> enabledDomains) throws Throwable{
		int fail = 0;
		Counters.StartCounter(COUNTER);

		try{
			initializeDomain2Map();
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
		runRegressionTest(Runner, enabledDomains);
	}

}