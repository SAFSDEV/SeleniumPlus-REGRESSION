package regression.testcases;

import org.safs.StringUtils;
import org.safs.image.ImageUtils.SubArea;
import org.safs.model.tools.EmbeddedHookDriverRunner;

import regression.Map;
import regression.testruns.Regression;

public class DriverMiscCommandTests extends Regression{
	
	public static final String COUNTER = StringUtils.getClassName(0, false);
	private static Utilities utils = null;
	
	/**
	 * Test keywords:<br>
	 * <pre>
	 * {@link Misc#GetAppMapValue(String)}
	 * {@link Misc#GetAppMapValue(org.safs.model.Component)}
	 * {@link Misc#SetApplicationMap(String)}
	 * {@link Misc#CloseApplicationMap(String)}
	 * {@link #SetVariableValue(String, String)}
	 * {@link Misc#CopyVariableValueEx(String, String)}
	 * {@link #GetVariableValue(String)}
	 * {@link Misc#WaitForRegistryKeyExists(String, String...)}
	 * {@link Misc#WaitForRegistryKeyValue(String, String, String, String...)}
	 * {@link Misc#GetRegistryKeyValue(String, String)}
	 * {@link Misc#GetRegistryKeyValue(String, String, String)}
	 * {@link Misc#GetSystemDate()}
	 * {@link Misc#GetSystemTime(boolean)}
	 * {@link Misc#GetSystemDateTime(boolean)}
	 * {@link Misc#AssignClipboardVariable(String)}
	 * {@link Misc#SetClipboard(String)}
	 * {@link Misc#ClearClipboard()}
	 * {@link Misc#SaveClipboardToFile(String, String...)}
	 * {@link Misc#VerifyClipboardToFile(String, String...)}
	 * {@link Misc#AssignClipboardVariable(String)}
	 * {@link Misc#SetVariableValues(String, String...)}
	 * {@link Misc#SetVariableValueEx(String, String)}
	 * {@link Misc#TakeScreenShot(String)}
	 * {@link Misc#TakeScreenShot(String, SubArea)}
	 * </pre>
	 * 
	 * @return int, the total number of failures
	 * @throws Throwable
	 */
	private static int testAPIBrowserless() throws Throwable{
		int fail = 0;
		
		//1. ============== Test Map related APIs =======================
		String mapID = "DriverMiscCommand.map";
		String overridingMapID = "DriverMiscCommandOverriding.map";
		
		if(Misc.SetApplicationMap(mapID)){
			String defaultMapID = Runner.jsafs().getMapsInterface().getDefaultMap().getUniqueID().toString();
			Logging.LogMessage(COUNTER+" current default map ID = "+defaultMapID);
			if(!mapID.equals(defaultMapID)) fail++;
			
			//Before SetApplicationMap
			String locationKey = Misc.GetAppMapValue(Map.LocationKey);
			String tComponent = Misc.GetAppMapValue(Map.T_Window.T_Component);
			
			if(Misc.SetApplicationMap(overridingMapID)){
				Logging.LogMessage(COUNTER+" The default map has been set to '"+overridingMapID+"'");			
				//SetApplicationMap, the map value should change
				if(Misc.GetAppMapValue(Map.LocationKey).equals(locationKey)){
					Logging.LogTestFailure(COUNTER+"'"+Map.LocationKey+"' map value SHOULD change!");
					fail++;
				}
				if(Misc.GetAppMapValue(Map.T_Window.T_Component).equals(tComponent)){
					Logging.LogTestFailure(COUNTER+"'"+Map.T_Window.T_Component.getName()+"' map value SHOULD change!");
					fail++;
				}
				
				//CloseApplicationMap
				Logging.LogMessage(COUNTER+" Close map '"+overridingMapID+"'");			
				if(Misc.CloseApplicationMap(overridingMapID)){
					if(!Misc.GetAppMapValue(Map.LocationKey).equals(locationKey)){
						Logging.LogTestFailure(COUNTER+"'"+Map.LocationKey+"' map value should NOT change!");
						fail++;
					}
					if(!Misc.GetAppMapValue(Map.T_Window.T_Component).equals(tComponent)){
						Logging.LogTestFailure(COUNTER+"'"+Map.T_Window.T_Component.getName()+"' map value should NOT change!");
						fail++;
					}
				}else{
					Logging.LogTestFailure(COUNTER+" Fail to close Map '"+overridingMapID+"'");
					fail++;
				}
				
			}else{
				Logging.LogTestFailure(COUNTER+" Fail to set Map '"+overridingMapID+"'");
				fail++;
			}
			
			if(!Misc.CloseApplicationMap(mapID)) fail++;
		}else{
			Logging.LogTestFailure(COUNTER+" Fail to set Map '"+mapID+"'");
			fail++;
		}
		

		//2. ============== Test SetVariableValue/CopyVariableValueEx =======================
		String var = "myvar";
		String dest = "mydest";
		String value = "myvar_value";
		if(SetVariableValue(var, value)){
			if( Misc.CopyVariableValueEx(var, dest)){
				if(!GetVariableValue(dest).equals(value)){
					fail++;
				}
			}else{
				fail++;
			}
		}else{
			fail++;
		}
		
		//3. ============== Test Registry keys =======================
		final String result = "result";
		String key = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion";
		String keyValue = "SystemRoot";
		String expectedValue = "C:\\Windows";
		if(Misc.WaitForRegistryKeyExists(key, keyValue)){
			if(Misc.GetRegistryKeyValue(key, keyValue, result)){
				expectedValue = GetVariableValue(result);
				Logging.LogMessage(COUNTER+" get registry key "+key+":"+keyValue+"="+expectedValue);
				if(!expectedValue.equals(Misc.GetRegistryKeyValue(key, keyValue))) fail++;
			}else{
				fail++;
			}
			
			if(!Misc.WaitForRegistryKeyValue(key, keyValue, expectedValue, "2")){
				Logging.LogTestWarning(COUNTER+" "+key+":"+keyValue+" does NOT equal to "+expectedValue);
				fail++;
			}
		}else{
			Logging.LogTestFailure(COUNTER+" Cannot wait registry key "+key+":"+keyValue);
			fail++;
		}
		
		//4. ============== Test SystemDataTime=======================
		Logging.LogMessage(COUNTER+"SystemDate "+Misc.GetSystemDate());
		Logging.LogMessage(COUNTER+"Military SystemTime "+Misc.GetSystemTime(true));
		Logging.LogMessage(COUNTER+"SystemTime "+Misc.GetSystemTime(false));
		Logging.LogMessage(COUNTER+"Military SystemDateTime "+Misc.GetSystemDateTime(true));
		Logging.LogMessage(COUNTER+"SystemDateTime "+Misc.GetSystemDateTime(false));
		
		
		//5. ============== Test ClipBoards=======================
		String clipboardFile = "clipboard.txt";
		String clipboardValue = "THIS IS SOMETHING TO BE SET TO CLIPBOARD!\n被设置为 MAKE a Test... 使用参数\n";
		if(Misc.SetClipboard(clipboardValue)){
			if(Misc.SaveClipboardToFile(clipboardFile)){
				if(Files.CopyFile(utils.testFile(clipboardFile), utils.benchFile(clipboardFile))){
					if(!Misc.VerifyClipboardToFile(clipboardFile)) fail++;
				}else{
					Logging.LogTestFailure(COUNTER+"Fail to copy test file '"+clipboardFile+"' to bench directory, cannot verify.");
					fail++;
				}
				
				if(Misc.AssignClipboardVariable(result)){
					if(!clipboardValue.equals(GetVariableValue(result))) fail++;
					if(Misc.ClearClipboard()){
						if(Misc.AssignClipboardVariable(result)){
							if(!"".equals(GetVariableValue(result))) fail++;
						}else{
							fail++;
						}
					}else{
						Logging.LogTestFailure(COUNTER+"Fail to clear clipboard");
						fail++;
					}
				}else{
					Logging.LogTestFailure(COUNTER+"Fail to assign clipboard value to variable '"+result+"'.");
					fail++;
				}
				
			}else{
				Logging.LogTestFailure(COUNTER+"Fail to save clipboard value to file.");
				fail++;
			}
		}else{
			Logging.LogTestFailure(COUNTER+"Fail to set clipboard.");
			fail++;
		}
		
		//6. ============== Test DDVariables ======================
		boolean originalExpressionOn = Misc.isExpressionsOn();
		Misc.Expressions(true);
		String usernamevar = "user.name";
		String passwordvar = "user.password";
		String usernameValue = "UserA";
		String passwordValue = "Password1";
		if(Misc.SetVariableValues("^"+usernamevar+"="+usernameValue+"","^"+passwordvar+"="+passwordValue)){
			if(!usernameValue.equals(GetVariableValue(usernamevar))) fail++;
			if(!passwordValue.equals(GetVariableValue(passwordvar))) fail++;
			
			//Clear the variable "user.name"
			if(Misc.SetVariableValues("^"+usernamevar+"=")){
				if(usernameValue.equals(GetVariableValue(usernamevar))) fail++;
				
				usernameValue = "NewUserName";
				//Create a reference "refusernamevar" to variable "user.name"
				String refToUsernameVar = "refusernamevar";
				Misc.SetVariableValueEx(refToUsernameVar, usernamevar);
				//Use the reference to set value to variable "user.name"
				Misc.SetVariableValueEx("^"+refToUsernameVar, usernameValue);
				if(!usernameValue.equals(GetVariableValue(usernamevar))) fail++;
				
			}else{
				Logging.LogTestFailure(COUNTER+"Fail to clear variable '"+usernamevar+"'.");
				fail++;
			}
		}else{
			fail++;
		}
		Misc.Expressions(originalExpressionOn);
		
		//7. ============== Test Takescreenshot ======================
		SubArea subarea = new SubArea(0,0,"20%","30%");
		if(!Misc.TakeScreenShot("screenshot.png")) fail++;
		if(!Misc.TakeScreenShot("sub_screenshot.png", subarea)) fail++;
		
		return fail;
	}
	/**
	 * Test keywords:<br>
	 * <pre>
	 * {@link Misc#WaitForGUI(org.safs.model.Component, String...)}
	 * {@link Misc#WaitForGUIGone(org.safs.model.Component, String...)}
	 * </pre>
	 * 
	 * @return int, the total number of failures
	 * @throws Throwable
	 */
	private static int testAPI(String browser) throws Throwable{
		int fail = 0;
		
		String ID = null;
		try{
			if(!Misc.SetApplicationMap(MAP_FILE_SAPDEMOAPP)){
				Logging.LogTestFailure(COUNTER+"Fail to load map '"+MAP_FILE_SAPDEMOAPP+"', cannot test in browser '"+browser+"'!");
				fail++;
				return fail;
			}
			
			ID = startBrowser(browser, Map.SAPDemoURL());
			if(ID!=null){
				
				//9. ==============  Test WaitForGuiGone ======================
				if(Misc.WaitForGUI(Map.SAPDemoPage.Basc_Button, "2")){
					fail++;
				}else{
					Logging.LogMessage(COUNTER+" Expected failure: "+Map.SAPDemoPage.Basc_Button.getName()+" does show on page yet.");
				}
				
				if(TabControl.ClickTab(Map.SAPDemoPage.TabControl, Map.Tab_basc_comp())){
					if(!Misc.WaitForGUI(Map.SAPDemoPage.Basc_Button)) fail++;
					if(Misc.WaitForGUIGone(Map.SAPDemoPage.Basc_Button, "2")) fail++;
					
					if(TabControl.ClickTab(Map.SAPDemoPage.TabControl, Map.Tab_jpan())){
						Misc.Delay(2000);
						if(!Misc.WaitForGUIGone(Map.SAPDemoPage.Basc_Button)) fail++;
					}else{
						Logging.LogTestFailure(COUNTER+"Fail click tab "+Map.Tab_jpan()+", cannot test WaitForGUIGone.");
						fail++;
					}
					
				}else{
					Logging.LogTestFailure(COUNTER+"Fail click tab "+Map.Tab_basc_comp()+", cannot test WaitForGUI/WaitForGUIGone.");
					fail++;
				}
				
				//8. ==============TODO Test SetContext SetFocus, Not implemented yet!!! ======================
//				TabControl.ClickTab(Map.SAPDemoPage.TabControl, Map.Tab_basc_comp());
//				Misc.SetFocus(Map.SAPDemoPage.SAPDemoPage);
//				Misc.SetFocus(Map.SAPDemoPage.Basc_TextArea);
//				Misc.SetContext(Map.SAPDemoPage.Basc_Password);
				
			}else{
				Logging.LogTestWarning(COUNTER+"StartWebBrowser '"+browser+"' Unsuccessful.");
				fail++;
			}
			
		}catch(Exception e){
			fail++;
			Logging.LogTestFailure(COUNTER+"Fail to test SAP Application in browser '"+browser+"'! Unexpected Exception "+StringUtils.debugmsg(e));
		}finally{
			if(ID!=null) if(!StopWebBrowser(ID)) fail++;
			if(!Misc.CloseApplicationMap(MAP_FILE_SAPDEMOAPP)) fail++;
		}
		
		return fail;
	}

	/**
	 * 
	 * @param Runner EmbeddedHookDriverRunner
	 * @return
	 * @throws Throwable
	 */
	public static int runRegressionTest(EmbeddedHookDriverRunner Runner) throws Throwable{
		int fail = 0;
		Counters.StartCounter(COUNTER);

		try{
			//Initialize Utilities
			utils = new Utilities(Runner.jsafs());
			
			//Get initial browser informations
			String browsers = Map.TestBrowserName();
			if(browsers==null || browsers.trim().isEmpty()){
				browsers = FF;
				Logging.LogTestWarning(COUNTER+" cannot get TestBrowserName from map, use "+browsers);
			}
			browsers = browsers.replaceAll(" +", " ");
			String[] browserArray = browsers.split(" ");

			for(String browser: browserArray){
				fail += testAPI(browser);				
			}
			
			fail += testAPIBrowserless();
			
		}catch(Throwable t){
			fail++;
			Logging.LogTestFailure(COUNTER +" fatal error due to "+t.getClass().getName()+", "+ t.getMessage());
		}

		Counters.StopCounter(COUNTER);
		Counters.StoreCounterInfo(COUNTER, COUNTER);
		Counters.LogCounterInfo(COUNTER);

		if(fail > 0){
			Logging.LogTestFailure(COUNTER+" XXX reports "+ fail +" UNEXPECTED test failures! XXX");
		}else{
			Logging.LogTestSuccess(COUNTER+" did not report any UNEXPECTED test failures!");
		}
		return fail;
	}

	public void runTest() throws Throwable{
		runRegressionTest(Runner);
	}

}