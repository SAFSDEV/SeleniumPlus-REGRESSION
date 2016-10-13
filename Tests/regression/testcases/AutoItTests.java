/** 
 * Copyright (C) SAS Institute, All rights reserved.
 * General Public License: http://www.opensource.org/licenses/gpl-license.php
 */

/**
 * regression.testcases.AutoItTests.java:
 * Logs for developers, not published to API DOC.
 *
 * History:
 * 18 DEC 2015    (Lei Wang) Initial release.
 * 26 SEP 2016	  (Tao Xie) Add 'testDotNetApp()' to test groups of CLICK keywords through Windows .Net Application.
 * 						   Add 'DoubleClick' test in 'testCaculator()'.
 */
package regression.testcases;

import java.util.ArrayList;
import java.util.List;

import org.safs.Domains;
import org.safs.StringUtils;
import org.safs.autoit.AutoIt;
import org.safs.autoit.AutoItRs;
import org.safs.autoit.lib.AutoItXPlus;
import org.safs.model.tools.EmbeddedHookDriverRunner;

import regression.Map;
import regression.testruns.Regression;

/**
 * This class is used to test implementation of AUTOIT.<br>
 * It is supposed to run ONLY on Windows Operating System.<br>
 *
 */
public class AutoItTests extends Regression{
	public static final String COUNTER = StringUtils.getClassName(0, false);
	public static final String MAP_FILE_AUTOITAPP = "AutoItApp.map";

	/**
	 * Test with Windows calculator application.
	 * @return int, the number of unexpected failure.
	 * @throws Throwable
	 */
	private static int testCaculator(String counterPrefix) throws Throwable{
		int fail = 0;
		String applicationID = "calculator";
		String executableCalc = "calc.exe";
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);

		if(Misc.LaunchApplication(applicationID, executableCalc)){

			if(!GetGUIImage(Map.Calculator.Btn5, "Btn5.png")) trace(++fail);
			if(!GetGUIImage(Map.Calculator.BtnEqual, "BtnEqual.png")) trace(++fail);
			
			if(Window.SetFocus(Map.Calculator.Calculator)){
				//make an addition "1+2"
				if(!Click(Map.Calculator.Btn1)) trace(++fail); else Pause(1);
				if(!Click(Map.Calculator.BtnPlus)) trace(++fail); else Pause(1);
				if(!Click(Map.Calculator.Btn2)) trace(++fail); else Pause(1);
				if(!Click(Map.Calculator.BtnEqual)) trace(++fail); else Pause(1);
				// TODO: We may use 'WinGetText', which hasn't been implemented, to verify the result of computation.
				
				// Test DoubleClick by making "11 + 22"
				if (!DoubleClick(Map.Calculator.Btn1))  trace(++fail); else Pause(1);
				if(!Click(Map.Calculator.BtnPlus)) trace(++fail); else Pause(1);
				if(!DoubleClick(Map.Calculator.Btn2)) trace(++fail); else Pause(1);
				if(!Click(Map.Calculator.BtnEqual)) trace(++fail); else Pause(1);
				// TODO: We may use 'WinGetText', which hasn't been implemented, to verify the result of computation.
				
			}else{
				trace(++fail);
			}

			if(!Misc.CloseApplication(applicationID)) trace(++fail);
		}else{
			trace(++fail);
		}
		
		Pause(1);
		
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
	 * Test with Windows notepad application.
	 * @return int, the number of unexpected failure.
	 * @throws Throwable
	 */
	private static int testNotepad(String counterPrefix) throws Throwable{
		int fail = 0;
		String applicationID = "notepad";
		String executableNotepad = "notepad.exe";
		AutoItXPlus it = null;
		AutoItRs panel = null;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false)); 
		Counters.StartCounter(counterID);

		if(Misc.LaunchApplication(applicationID, executableNotepad)){
			try{
				it = AutoIt.AutoItObject();
			}catch(Exception e){
				Logging.LogTestWarning("Fail to get instance of AUTOIT. Met "+StringUtils.debugmsg(e));
			}
			try{
				String winrs = Misc.GetAppMapValue(Map.Notepad.Notepad);
				String comprs = Misc.GetAppMapValue(Map.Notepad.EditBox);
				panel = new AutoItRs(winrs, comprs);
			}catch(Exception e){
				Logging.LogTestWarning("Fail to initialize the components. Met "+StringUtils.debugmsg(e));
			}
			
			if(!GetGUIImage(Map.Notepad.Notepad, "Notepad.png")) trace(++fail);
			if(!GetGUIImage(Map.Notepad.EditBox, "EditBox.png")) trace(++fail);
			
			if(!Window.SetPosition(Map.Notepad.Notepad, org.safs.ComponentFunction.Window.MAXIMIZED)){
				trace(++fail);
			}
			if(!Window.SetPosition(Map.Notepad.Notepad, org.safs.ComponentFunction.Window.MINIMIZED)){
				trace(++fail);
			}
			if(!Window.SetPosition(Map.Notepad.Notepad, org.safs.ComponentFunction.Window.NORMAL)){
				trace(++fail);
			}
			//"preset" in section [Notepad], it is "0,0,640,480;Status=NORMAL"
			if(!Window.SetPosition(Map.Notepad.Notepad, Map.Notepad.preset.getName())){
				trace(++fail);
			}
			//"0,0,700,300;Status=NORMAL"
			if(!Window.SetPosition(Map.Notepad.Notepad, Map.preset())){
				trace(++fail);
			}
			if(!Window.SetPosition(Map.Notepad.Notepad, 0,0,800,600)){
				trace(++fail);
			}
			
			if(Window.SetFocus(Map.Notepad.Notepad)){
				//input some string and verify the input characters.
				String text = "Value to input";
				if(EditBox.SetTextValue(Map.Notepad.EditBox, text)){
					try{
						//Try to use AUTOIT to call its API directly, to verify the 'SetTextValue'
						String result = it.controlGetText(panel.getWindowsRS(), "", panel.getComponentRS());
						if(!text.equals(result)){
							Logging.LogTestWarning("Notepad verification failed.");
						}

					}catch(Exception e){
						Logging.LogTestWarning("Fail to verify the editbox value. Met "+StringUtils.debugmsg(e));
					}
					Misc.Pause(1);
					
					//Open the "replace" popup, and replace some characters in editbox and verify
					if(TypeKeys("%ER")){//Alt+E, then R
						String replacedStr = "input";
						String replacingStr = "INPUT";
						
						if(Window.SetFocus(Map.Notepad_Replace.EditBoxFind)){
							if(!TypeChars(replacedStr)) trace(++fail); else Pause(1);
							
							if(Window.SetFocus(Map.Notepad_Replace.EditBoxReplace)){
								if(!TypeChars(replacingStr)) trace(++fail); else Pause(1);
							}else{
								trace(++fail);
							}
							if(!Click(Map.Notepad_Replace.ButtonReplaceAll)) trace(++fail);
							else{
								Pause(1);
								//dispose the "replace" dialog
								if(Click(Map.Notepad_Replace.ButtonCancel)){
									//verify the string has been replaced
									try{
										//Try to use AUTOIT to call its API directly
										String result = it.controlGetText(panel.getWindowsRS(), "", panel.getComponentRS());
										String expectedStr = text.replaceAll(replacedStr, replacingStr);
										if(!expectedStr.equals(result)){
											Logging.LogTestWarning("Notepad verification failed. Expected '"+expectedStr+"'!='"+result+"'");
										}

									}catch(Exception e){
										Logging.LogTestWarning("Fail to verify the editbox value. Met "+StringUtils.debugmsg(e));
									}
								}else{
									trace(++fail);
								}
							}
							
						}else{
							trace(++fail);
						}
						
					}else{
						trace(++fail);
					}
					
				}else{
					trace(++fail);
				}

			}else{
				trace(++fail);
			}

			Misc.Pause(1);
			Misc.CloseApplication(applicationID);
		}else{
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
	 * Test groups of CLICK keywords through Windows .Net application: '%SAFSDIR%\sample\DotNetApp\WinDemo.exe'.
	 * 
	 * @return int, the number of unexpected failure.
	 * @throws Throwable
	 * 
	 * @author Tao Xie
	 */
	private static int testDotNetApp(String counterPrefix) throws Throwable{
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		int fail = 0;
		Counters.StartCounter(counterID);
		String applicationID = "dotNetApp";
		String executableWinDemo = System.getenv("SAFSDIR") + "\\samples\\DotNetApp\\WinDemo.exe";

		/**
		 * Test groups of CLICK keywords: RightClick, CtrlClick, ShiftClick.
		 */
		if(Misc.LaunchApplication(applicationID, executableWinDemo)){
			if(!RightClick(Map.DotNetApp.TableControl, "30, 10")) trace(++fail); else Pause(1);
			if(!Click(Map.DotNetApp.TableControl, "214, 12")) trace(++fail); else Pause(1);
			if(!CtrlClick(Map.DotNetApp.DataView, "40, 80")) trace(++fail); else Pause(1);
			if(!CtrlClick(Map.DotNetApp.DataView, "40, 125")) trace(++fail); else Pause(1);
			if(!ShiftClick(Map.DotNetApp.DataView, "40, 196")) trace(++fail); else Pause(1);
			
			if(!Misc.CloseApplication(applicationID)) trace(++fail);
		} else{
			trace(++fail);
		}
		
		Pause(1);

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);

		if(fail > 0){
			Logging.LogTestFailure(counterID + " " + fail + " UNEXPECTED test failures!");
		 }else{
			Logging.LogTestSuccess(counterID + " did not report any UNEXPECTED test failures!");
		}

		return fail;
	}	

	/**
	 * 
	 * @param Runner EmbeddedHookDriverRunner
	 * @return int, the number of unexpected failure.
	 * @throws Throwable
	 */
	public static int runRegressionTest(EmbeddedHookDriverRunner Runner, List<String> enabledDomains) throws Throwable{
		int fail = 0;
		Counters.StartCounter(COUNTER);

		try{
			for(String domain: enabledDomains) Domains.enableDomain(domain);
			
			String mapID = MAP_FILE_AUTOITAPP;
			if(Misc.SetApplicationMap(mapID)){
				
				fail += testDotNetApp(COUNTER);
				fail += testCaculator(COUNTER);
				fail += testNotepad(COUNTER);
				
				if(!Misc.CloseApplicationMap(mapID)) trace(++fail);
				
			}else{
				trace(++fail);
				Logging.LogTestFailure(COUNTER+"Fail to load map '"+mapID+"', cannot "+StringUtils.debugmsg(false));
			}

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
