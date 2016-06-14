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
	static final String FILTER = ComponentFunction.PARAM_FILTER;
	
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
		String counterID = counterPrefix + ".testAPI";
		Counters.StartCounter(counterID);

		String browsers = Map.TestBrowserName();
		if(browsers==null || browsers.trim().isEmpty()){
			browsers = FF;
			Logging.LogTestWarning(COUNTER+" cannot get TestBrowserName from map, use "+browsers);
		}
		browsers = browsers.replaceAll(" +", " ");
		String[] browserArray = browsers.split(" ");

		for(String browser: browserArray){
			if(Domains.isHtmlEnabled()) fail += testAPIForHtml(counterID, browser);
			if(Domains.isDojoEnabled()) fail += testAPIForDojo(counterID, browser);
			if(Domains.isSapEnabled()) fail += testAPIForSAP(counterID, browser);
		}

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);
		
		return fail;
	}

	private static int testAPIForHtml(String counterPrefix, String browser) throws Throwable{
		int fail = 0;
		String counterID = counterPrefix + ".testAPIForHtml";
		Counters.StartCounter(counterID);
		
		if(Misc.SetApplicationMap(MAP_FILE_HTMLAPP)){
			String ID = null;
			
			try{
				ID = startBrowser(browser, Map.GOJS_MINIMAL_URL());
				if(ID!=null){
					fail +=html_gojs_testDrag(counterID);
					
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

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);
		
		return fail;
	}
	
	private static int html_gojs_testDrag(String counterPrefix){
		int fail = 0;
		String counterID = counterPrefix + ".html_gojs_testDrag";
		Counters.StartCounter(counterID);
	
		//1. Test some drag keywords on Minimal page "http://www.gojs.net/latest/samples/minimal.html"
		//Drag one node
		if(!LeftDrag(Map.GoJSSamples.Diagram, "coords=160, 160, 72, 160")) fail++;
		//Drag two nodes together
		if(!ShiftLeftDrag(Map.GoJSSamples.Diagram, "coords=160, 235, 160, 350")) fail++;
		//Copy 2 nodes
		if(!CtrlShiftLeftDrag(Map.GoJSSamples.Diagram, "coords=160, 350, 130, 350")) fail++;
		//Click one node
		if(!Click(Map.GoJSSamples.Diagram, "225, 165")) fail++;
		//Copy one node, and put the copy at its right
		if(!CtrlLeftDrag(Map.GoJSSamples.Diagram, "coords=225, 165, 300, 165")) fail++;
		//Alt Drag the copy to up
		if(!AltLeftDrag(Map.GoJSSamples.Diagram, "coords=300, 165, 300, 55")) fail++;
		//Right Drag the copy to right
		if(!RightDrag(Map.GoJSSamples.Diagram, "coords=300, 55, 350, 55")) fail++;
		//Make an copy of the copy, put it to the left
		if(!CtrlAltLeftDrag(Map.GoJSSamples.Diagram, "coords=350, 55, 300, 55")) fail++;
		
		//2. Test DragTo on PageFlow page "http://www.gojs.net/latest/samples/pageFlow.html"
		Logging.LogMessage("Going to page 'Page Flow, try to test DragTo'");
		/**
		 * Problem Statements:
		 *     Using below '1-parameter version Click()' will make the 'DragTo()' fail with IE browser in VM environment.
		 *     More specifically, after the '1-parameter version Click()', the 'getCurrentURL()' method in 'SearchObject.java',
		 *     which will be called in the 'DragTo()' method, will throw 'Unable to get browser' exception.
		 * 
		 * Notes:
		 *     1. If we use Chrome or Firefox, it can succeed in any environment.
		 *     2. If we use IE in local, NOT in VM environment, it also can success.
		 *  
		 * Reason:
		 *     This is probably due to the 3rd party Selenium's IE driver. 
		 *         1. If we put a break point at 'Click(Map.GoJSSamples.MenuPageFlow)', then the whole test could pass successfully.
		 *         2. If we make the IE lose the focus before executing 'Click(Map.GoJSSamples.MenuPageFlow)', then the test will succeed.     
		 *     [Lei] The Selenium webdriver must do something extra in this situation. (This sounds strange, normally we need focus on component before performing
		 *           something. But Selenium is different, it calls native methods/events and doesn't need focus.), I would consider this as Selenium IE driver's bug, not ours.
		 * 
		 * Solution:
		 *     As it's Selenium's bug not ours, we'll leave it here with comments as no fix.
		 *     In order to make the Regression test pass, we'll use the 'Click()' method with 
		 *     coordinate, which will use Robot to execute the Click action.
		 * 
		 */
//		if(Click(Map.GoJSSamples.MenuPageFlow)){
		Component.ShowOnPage(Map.GoJSSamples.MenuPageFlow, "true");
		if(Click(Map.GoJSSamples.MenuPageFlow, "5,5")){
			//Drag some component from "palate" to "my diagram"			
			if(!Component.DragTo(Map.GoJSSamples.Palette, Map.GoJSSamples.Diagram, "40, 20, 5%, 5%")) fail++;
			Pause(1);
			if(!Component.DragTo(Map.GoJSSamples.Palette, Map.GoJSSamples.Diagram, "10%, 20%, 20%, 20%")) fail++;
			Pause(1);
			//Draw a line between 2 components
			if(!Component.DragTo(Map.GoJSSamples.Diagram, Map.GoJSSamples.Diagram, "6%, 6%, 21%, 21%")) fail++;
			Pause(1);
		}else{
			fail++;
		}

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);
		
		return fail;
	}
	
	private static int testAPIForDojo(String counterPrefix, String browser) throws Throwable{
		int fail = 0;	
		String counterID = counterPrefix + ".testAPIForDojo";
		Counters.StartCounter(counterID);
		if(Misc.SetApplicationMap(MAP_FILE_DOJOAPP)){
	
		}else{
			fail++;
			Logging.LogTestFailure(COUNTER+"Fail to load map '"+MAP_FILE_DOJOAPP+"', cannot test in browser '"+browser+"'!");
		}

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);
		
		return fail;
	}
	
	private static int testAPIForSAP(String counterPrefix, String browser) throws Throwable{
		int fail = 0;	
		String counterID = counterPrefix + ".testAPIForSAP";
		Counters.StartCounter(counterID);
		if(Misc.SetApplicationMap(MAP_FILE_SAPDEMOAPP)){
	
		}else{
			fail++;
			Logging.LogTestFailure(COUNTER+"Fail to load map '"+MAP_FILE_SAPDEMOAPP+"', cannot test in browser '"+browser+"'!");
		}

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);
		
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
			fail += testAPI(COUNTER);

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
		initUtils();
		runRegressionTest(Runner, enabledDomains);
	}

}