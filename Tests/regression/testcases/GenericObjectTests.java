package regression.testcases;

import java.util.ArrayList;
import java.util.List;

import org.safs.ComponentFunction;
import org.safs.Domains;
import org.safs.StringUtils;
import org.safs.model.tools.EmbeddedHookDriverRunner;

import regression.Map;
import regression.testruns.Regression;

public class GenericObjectTests extends Regression{

	public static final String COUNTER = StringUtils.getClassName(0, false);
	static Utilities utils = null;
	static final String FILTER = ComponentFunction.PARAM_FILTER;
	
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
			if(Domains.isSapEnabled()) fail += testAPIForSAP(browser);
		}

		return fail;
	}

	private static int testAPIForHtml(String browser) throws Throwable{
		int fail = 0;
		if(Misc.SetApplicationMap(MAP_FILE_HTMLAPP)){
			String ID = null;
			
			try{
				ID = startBrowser(browser, Map.GOJS_MINIMAL_URL());
				if(ID!=null){
					fail +=html_gojs_testDrag();
					
				}else{
					Logging.LogTestWarning(COUNTER+"StartWebBrowser '"+browser+"' Unsuccessful.");
					fail++;
				}
			}catch(Exception e){
				fail++;
				Logging.LogTestFailure(COUNTER+"Fail to test SAP Application in browser '"+browser+"'! Unexpected Exception "+StringUtils.debugmsg(e));
			}finally{
				if(ID!=null) if(!StopWebBrowser(ID)) fail++;
			}
			
		}else{
			fail++;
			Logging.LogTestFailure(COUNTER+"Fail to load map '"+MAP_FILE_HTMLAPP+"', cannot test in browser '"+browser+"'!");
		}
		
		return fail;
	}
	
	private static int html_gojs_testDrag(){
		int fail = 0;
	
		//Drag one node
		LeftDrag(Map.GoJSMinimal.Diagram, "coords=160, 160, 72, 160");
		//Drag two nodes together
		ShiftLeftDrag(Map.GoJSMinimal.Diagram, "coords=160, 235, 160, 350");
		//Copy 2 nodes
		CtrlShiftLeftDrag(Map.GoJSMinimal.Diagram, "coords=160, 350, 130, 350");
		//Click one node
		Click(Map.GoJSMinimal.Diagram, "225, 165");
		//Copy one node, and put the copy at its right
		CtrlLeftDrag(Map.GoJSMinimal.Diagram, "coords=225, 165, 300, 165");
		//Alt Drag the copy to up
		AltLeftDrag(Map.GoJSMinimal.Diagram, "coords=300, 165, 300, 55");
		//Right Drag the copy to right
		RightDrag(Map.GoJSMinimal.Diagram, "coords=300, 55, 350, 55");
		//Make an copy of the copy, put it to the left
		CtrlAltLeftDrag(Map.GoJSMinimal.Diagram, "coords=350, 55, 300, 55");
		
		return fail;
	}
	
	private static int testAPIForDojo(String browser) throws Throwable{
		int fail = 0;	
		if(Misc.SetApplicationMap(MAP_FILE_DOJOAPP)){
	
		}else{
			fail++;
			Logging.LogTestFailure(COUNTER+"Fail to load map '"+MAP_FILE_DOJOAPP+"', cannot test in browser '"+browser+"'!");
		}
		return fail;
	}
	
	private static int testAPIForSAP(String browser) throws Throwable{
		int fail = 0;	
		if(Misc.SetApplicationMap(MAP_FILE_SAPDEMOAPP)){
	
		}else{
			fail++;
			Logging.LogTestFailure(COUNTER+"Fail to load map '"+MAP_FILE_SAPDEMOAPP+"', cannot test in browser '"+browser+"'!");
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
			fail++;
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