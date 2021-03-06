package regression.testcases;

import java.awt.Point;
import java.io.File;

import org.safs.StatusCodes;
import org.safs.StringUtils;
import org.safs.image.ImageUtils.SubArea;
import org.safs.selenium.webdriver.DCDriverCommand;
import org.safs.selenium.webdriver.SeleniumPlus;
import org.safs.selenium.webdriver.lib.SelectBrowser;
import org.safs.selenium.webdriver.lib.SeleniumPlusException;
import org.safs.selenium.webdriver.lib.WDLibrary;
import org.safs.text.FileUtilities;

import regression.Map;
import regression.testruns.Regression;

public class DriverMiscCommandTests extends Regression{

	public static final String COUNTER = StringUtils.getClassName(0, false);

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
	private static int testAPIBrowserless(String counterPrefix) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);

		//1. ============== Test Map related APIs =======================
		String mapID = "DriverMiscCommand.map";
		String overridingMapID = "DriverMiscCommandOverriding.map";

		if(Misc.SetApplicationMap(mapID)){
			String defaultMapID = Runner.jsafs().getMapsInterface().getDefaultMap().getUniqueID().toString();
			Logging.LogMessage(counterID + " current default map ID = "+defaultMapID);
			if(!mapID.equals(defaultMapID)) fail++;

			//Before SetApplicationMap
			String locationKey = Misc.GetAppMapValue(Map.LocationKey);
			String tComponent = Misc.GetAppMapValue(Map.T_Window.T_Component);

			if(Misc.SetApplicationMap(overridingMapID)){
				Logging.LogMessage(counterID + " The default map has been set to '"+overridingMapID+"'");
				//SetApplicationMap, the map value should change
				if(Misc.GetAppMapValue(Map.LocationKey).equals(locationKey)){
					Logging.LogTestFailure(counterID + "'"+Map.LocationKey+"' map value SHOULD change!");
					fail++;
				}
				if(Misc.GetAppMapValue(Map.T_Window.T_Component).equals(tComponent)){
					Logging.LogTestFailure(counterID + "'"+Map.T_Window.T_Component.getName()+"' map value SHOULD change!");
					fail++;
				}

				//CloseApplicationMap
				Logging.LogMessage(counterID + " Close map '"+overridingMapID+"'");
				if(Misc.CloseApplicationMap(overridingMapID)){
					if(!Misc.GetAppMapValue(Map.LocationKey).equals(locationKey)){
						Logging.LogTestFailure(counterID + "'"+Map.LocationKey+"' map value should NOT change!");
						fail++;
					}
					if(!Misc.GetAppMapValue(Map.T_Window.T_Component).equals(tComponent)){
						Logging.LogTestFailure(counterID + "'"+Map.T_Window.T_Component.getName()+"' map value should NOT change!");
						fail++;
					}
				}else{
					Logging.LogTestFailure(counterID + " Fail to close Map '"+overridingMapID+"'");
					fail++;
				}

			}else{
				Logging.LogTestFailure(counterID + " Fail to set Map '"+overridingMapID+"'");
				fail++;
			}

			if(!Misc.CloseApplicationMap(mapID)) fail++;
		}else{
			Logging.LogTestFailure(counterID + " Fail to set Map '"+mapID+"'");
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
				Logging.LogMessage(counterID + " get registry key "+key+":"+keyValue+"="+expectedValue);
				if(!expectedValue.equals(Misc.GetRegistryKeyValue(key, keyValue))) fail++;
			}else{
				fail++;
			}

			if(!Misc.WaitForRegistryKeyValue(key, keyValue, expectedValue, "2")){
				Logging.LogTestWarning(counterID + " "+key+":"+keyValue+" does NOT equal to "+expectedValue);
				fail++;
			}
		}else{
			Logging.LogTestFailure(counterID + " Cannot wait registry key "+key+":"+keyValue);
			fail++;
		}

		//4. ============== Test SystemDataTime=======================
		Logging.LogMessage(counterID + "SystemDate "+Misc.GetSystemDate());
		Logging.LogMessage(counterID + "Military SystemTime "+Misc.GetSystemTime(true));
		Logging.LogMessage(counterID + "SystemTime "+Misc.GetSystemTime(false));
		Logging.LogMessage(counterID + "Military SystemDateTime "+Misc.GetSystemDateTime(true));
		Logging.LogMessage(counterID + "SystemDateTime "+Misc.GetSystemDateTime(false));


		//5. ============== Test ClipBoards=======================
		String clipboardFile = "clipboard.txt";
		String clipboardValue = "THIS IS SOMETHING TO BE SET TO CLIPBOARD!\n被设置为 MAKE a Test... 使用参数\n";
		if(Misc.SetClipboard(clipboardValue)){
			if(Misc.SaveClipboardToFile(clipboardFile)){
				if(Files.CopyFile(quote(utils.testFile(clipboardFile)), quote(utils.benchFile(clipboardFile)))){
					if(!Misc.VerifyClipboardToFile(clipboardFile)) fail++;
				}else{
					Logging.LogTestFailure(counterID + "Fail to copy test file '"+clipboardFile+"' to bench directory, cannot verify.");
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
						Logging.LogTestFailure(counterID + "Fail to clear clipboard");
						fail++;
					}
				}else{
					Logging.LogTestFailure(counterID + "Fail to assign clipboard value to variable '"+result+"'.");
					fail++;
				}

			}else{
				Logging.LogTestFailure(counterID + "Fail to save clipboard value to file.");
				fail++;
			}
		}else{
			Logging.LogTestFailure(counterID + "Fail to set clipboard.");
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
				Logging.LogTestFailure(counterID + "Fail to clear variable '"+usernamevar+"'.");
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
	 * @return int, the total number of failures
	 * @throws Throwable
	 * @see {@link #testAPI_WaitForGUI(String)}
	 * @see {@link #testAPI_Misc_URL(String)}
	 * @see #testAPI_Misc_Alert(String)
	 */
	private static int testAPI(String counterPrefix, String browser) throws Throwable{
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);

		fail += testAPI_WaitForGUI(counterID, browser);
		fail += testAPI_Misc_URL(counterID, browser);

		/**
		 * As this is a known defect of 3rd party software Selenium: https://github.com/SeleniumHQ/selenium/issues/2068,
		 * ignore this test when using IE browser. If this defect can be solved in the future, we'll cancel this
		 * 'ignore' behavior.
		 *
		 * @author Tao Xie
		 */
		if (! browser.toLowerCase().equals(SelectBrowser.BROWSER_NAME_IE)) {
			fail += testAPI_Misc_Alert(counterID, browser);
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
	 * Test keywords:<br>
	 * <pre>
	 * {@link Misc#OnGUIExistsGotoBlockID(org.safs.model.Component, String, String...)}
	 * {@link Misc#OnGUINotExistGotoBlockID(org.safs.model.Component, String, String...)}
	 * </pre>
	 *
	 * @param component		org.safs.model.Component, the component to test with
	 * @param timeout		String, the timeout in seconds
	 * @param expectedExist	boolean, the expectation of the component's existence
	 * @return	int, the total unexpected failures happened in this method
	 */
	private static int test_ongui_xxx_gotoblockid(String counterPrefix, org.safs.model.Component component, String timeout/*in seconds*/, boolean expectedExist){
		String dbg = StringUtils.debugmsg(false);
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		String BranchExist = component.getName()+"_Exist_Block";
		String BranchNotExist = component.getName()+"_Not_Exist_Block";

		long start = System.currentTimeMillis();
		String blockid = Misc.OnGUIExistsGotoBlockID(component, BranchExist, timeout);
		Logging.LogMessage("OnGUIExistsGotoBlockID return blockid '"+blockid+"', time used: "+(System.currentTimeMillis()-start));
		if(blockid==null) trace(++fail);
		else{
			if(expectedExist){
				if(blockid.trim().isEmpty()) trace(++fail);
			}else{
				if(BranchExist.equals(blockid)) trace(++fail);
			}
		}

		start = System.currentTimeMillis();
		blockid = Misc.OnGUINotExistGotoBlockID(component, BranchNotExist, timeout);
		Logging.LogMessage("OnGUINotExistGotoBlockID return blockid '"+blockid+"', time used: "+(System.currentTimeMillis()-start));
		if(blockid==null) trace(++fail);
		else{
			if(expectedExist){
				if(BranchNotExist.equals(blockid)) trace(++fail);
			}else{
				if(blockid.trim().isEmpty()) trace(++fail);
			}
		}

		if(fail > 0) Logging.LogMessage(dbg+" sub reports "+ fail +" UNEXPECTED test failures!");

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);

		return fail;
	}

	/**
	 * Test keywords:<br>
	 * <pre>
	 * {@link Misc#WaitForGUI(org.safs.model.Component, String...)}
	 * {@link Misc#WaitForGUIGone(org.safs.model.Component, String...)}
	 * {@link Misc#IsComponentExists(org.safs.model.Component, String...)}
	 * {@link Misc#OnGUIExistsGotoBlockID(org.safs.model.Component, String, String...)}
	 * {@link Misc#OnGUINotExistGotoBlockID(org.safs.model.Component, String, String...)}
	 * </pre>
	 *
	 * @return int, the total number of failures
	 * @throws Throwable
	 */
	private static int testAPI_WaitForGUI(String counterPrefix, String browser) throws Throwable{
		String preMsg = StringUtils.getMethodName(0, false);
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		String ID = null;
		try{
			if(!Misc.SetApplicationMap(MAP_FILE_SAPDEMOAPP)){
				Logging.LogTestFailure(preMsg+"Fail to load map '"+MAP_FILE_SAPDEMOAPP+"', cannot test in browser '"+browser+"'!");
				fail++;
				return fail;
			}

			ID = startBrowser(browser, Map.SAPDemoURL());
			if(ID!=null){

				if(Misc.IsComponentExists(Map.SAPDemoPage.Basc_Button, "2")) trace(++fail);
				fail += test_ongui_xxx_gotoblockid(counterID, Map.SAPDemoPage.Basc_Button, "2", false);

				if(Misc.WaitForGUI(Map.SAPDemoPage.Basc_Button, "2")){
					fail++;
				}else{
					Logging.LogMessage(preMsg+" Expected failure: "+Map.SAPDemoPage.Basc_Button.getName()+" does not show on page yet.");
				}

				if(TabControl.ClickTab(Map.SAPDemoPage.TabControl, Map.Tab_basc_comp())){
					if(!Misc.WaitForGUI(Map.SAPDemoPage.Basc_Button)) fail++;
					if(Misc.WaitForGUIGone(Map.SAPDemoPage.Basc_Button, "2")) fail++;

					if(!Misc.IsComponentExists(Map.SAPDemoPage.Basc_Button)) trace(++fail);
					fail += test_ongui_xxx_gotoblockid(counterID, Map.SAPDemoPage.Basc_Button, "2", true);

					if(TabControl.ClickTab(Map.SAPDemoPage.TabControl, Map.Tab_jpan())){
						Misc.Delay(2000);
						// TODO The waitForGUIGone() will presume the Component exist first. But as we jumped into another Tab,
						// the Basc_Button will disappear first, which will make the waitForGUIGone() return a false value
						// with log warning information. We may comment the waitForGUIGone() here first, and try to figure out
						// another testing situation.
//						if(!Misc.WaitForGUIGone(Map.SAPDemoPage.Basc_Button)) fail++;
					}else{
						Logging.LogTestFailure(preMsg+"Fail click tab "+Map.Tab_jpan()+", cannot test WaitForGUIGone.");
						fail++;
					}

				}else{
					Logging.LogTestFailure(preMsg+"Fail click tab "+Map.Tab_basc_comp()+", cannot test WaitForGUI/WaitForGUIGone.");
					fail++;
				}

				// ==============TODO Test SetContext SetFocus, Not implemented yet!!! ======================
//				TabControl.ClickTab(Map.SAPDemoPage.TabControl, Map.Tab_basc_comp());
//				Misc.SetFocus(Map.SAPDemoPage.SAPDemoPage);
//				Misc.SetFocus(Map.SAPDemoPage.Basc_TextArea);
//				Misc.SetContext(Map.SAPDemoPage.Basc_Password);

			}else{
				Logging.LogTestFailure(preMsg+"StartWebBrowser '"+browser+"' Unsuccessful.");
				fail++;
			}

		}catch(Exception e){
			fail++;
			Logging.LogTestFailure(preMsg+"Fail to test SAP Application in browser '"+browser+"'! Unexpected Exception "+StringUtils.debugmsg(e));
		}finally{
			if(ID!=null) if(!StopWebBrowser(ID)) fail++;
			if(!Misc.CloseApplicationMap(MAP_FILE_SAPDEMOAPP)) fail++;
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
	 * Test keywords:<br>
	 * <pre>
	 * {@link Misc#GetURL(String, String, String...)}
	 * {@link Misc#SaveURLToFile(String, String, String...)}
	 * {@link Misc#VerifyURLContent(String, String, String...)}
	 * {@link Misc#VerifyURLToFile(String, String, String...)}
	 * </pre>
	 *
	 * @return int, the total number of failures
	 * @throws Throwable
	 */
	private static int testAPI_Misc_URL(String counterPrefix, String browser) throws Throwable{
		String preMsg = StringUtils.getMethodName(0, false);
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		String ID = null;

		boolean originalExpression = Misc.isExpressionsOn();

		try{
			if(!Misc.SetApplicationMap(MAP_FILE_HTMLAPP)){
				Logging.LogTestFailure(preMsg+"Fail to load map '"+MAP_FILE_HTMLAPP+"', cannot test in browser '"+browser+"'!");
				trace(++fail);
				return fail;
			}

			//Turn off the expression
			Misc.Expressions(false);

			ID = startBrowser(browser, Map.THOMAS_BAYER_URL());
			if(ID!=null){

				if(Misc.WaitForGUI(Map.ThomasBayerPage.ThomasBayerPage)){
					String restURL = null;
					//1. Test Misc.GetURL
					String contentVariable = "content";
					try{
						String var = contentVariable;
						restURL = Map.THOMAS_BAYER_SQLREST_URL();
						if(Misc.GetURL(restURL, contentVariable)){
							StringBuffer result = new StringBuffer();

							result.append(var+"="+GetVariableValue(var)+"\n");
							var = contentVariable+DCDriverCommand.SUFFIX_VARIABLE_HEADERS;
							result.append(var+"="+GetVariableValue(var)+"\n");
							var = contentVariable+DCDriverCommand.SUFFIX_VARIABLE_READY_STATE;
							result.append(var+"="+GetVariableValue(var)+"\n");
							var = contentVariable+DCDriverCommand.SUFFIX_VARIABLE_STATUS;
							result.append(var+"="+GetVariableValue(var)+"\n");
							var = contentVariable+DCDriverCommand.SUFFIX_VARIABLE_STATUS_TEXT;
							result.append(var+"="+GetVariableValue(var)+"\n");
							var = contentVariable+DCDriverCommand.SUFFIX_VARIABLE_XML;
							result.append(var+"="+GetVariableValue(var)+"\n");

							Logging.LogTestSuccess("Executing Misc.GetURL('"+restURL+"', '"+contentVariable+"'), the result is \n", result.toString());
						}else{
							if(SeleniumPlus.prevResults.getStatusCode()!=StatusCodes.SCRIPT_NOT_EXECUTED){
								trace(++fail);
								Logging.LogTestFailure("Fail to get content for url '"+restURL+"'!");
							}
						}
					}catch(Exception e){
						trace(++fail);
						Logging.LogTestFailure("Executing Misc.GetURL('"+restURL+"', '"+contentVariable+"'), met error ", StringUtils.debugmsg(e));
					}

					boolean fileSaved = false;
					String file = "product_content.txt";
					String filecontent = null;
					//2. Test Misc.SaveURLToFile
					try{
						restURL = Map.THOMAS_BAYER_SQLREST_PRODUCT_URL();

						if(Misc.SaveURLToFile(restURL, file)){
							fileSaved = true;
							filecontent = FileUtilities.readStringFromUTF8File(utils.testFile(file));
							Logging.LogTestSuccess("Executing Misc.SaveURLToFile('"+restURL+"', '"+file+"'), the file content is \n", filecontent);

						}else{
							if(SeleniumPlus.prevResults.getStatusCode()!=StatusCodes.SCRIPT_NOT_EXECUTED){
								trace(++fail);
								Logging.LogTestFailure("Fail to get save url '"+restURL+"' to file '"+file+"'!");
							}
						}
					}catch(Exception e){
						trace(++fail);
						Logging.LogTestFailure("Executing Misc.SaveURLToFile('"+restURL+"', '"+file+"'), met error ", StringUtils.debugmsg(e));
					}

					//3. Test Misc.VerifyURLToFile
					try{
						if(fileSaved){
							//Move the test file to bench directory
							FileUtilities.copyFileToFile(new File(utils.testFile(file)), new File(utils.benchFile(file)));

							restURL = Map.THOMAS_BAYER_SQLREST_PRODUCT_URL();

							if(Misc.VerifyURLToFile(restURL, file)){
								Logging.LogTestSuccess("Executing Misc.VerifyURLToFile('"+restURL+"', '"+file+"') succeed.");
							}else{
								if(SeleniumPlus.prevResults.getStatusCode()!=StatusCodes.SCRIPT_NOT_EXECUTED){
									trace(++fail);
									Logging.LogTestFailure("Fail to get verify url '"+restURL+"' to file '"+file+"'!");
								}
							}
						}else{
							Logging.LogTestWarning("Previous SaveURLToFile failed, NO file to verify. Skip VerifyURLToFile!");
						}
					}catch(Exception e){
						trace(++fail);
						Logging.LogTestFailure("Executing Misc.VerifyURLToFile('"+restURL+"', '"+file+"'), met error ", StringUtils.debugmsg(e));
					}

					//4. Test Misc.VerifyURLContent
					try{
						restURL = Map.THOMAS_BAYER_SQLREST_PRODUCT_URL();

						if(Misc.VerifyURLContent(restURL, filecontent)){
							Logging.LogTestSuccess("Executing Misc.VerifyURLContent('"+restURL+"', '"+filecontent+"') succeed.");
						}else{
							if(SeleniumPlus.prevResults.getStatusCode()!=StatusCodes.SCRIPT_NOT_EXECUTED){
								trace(++fail);
								Logging.LogTestFailure("Fail to get verify url '"+restURL+"' to content '"+filecontent+"'!");
							}
						}
					}catch(Exception e){
						trace(++fail);
						Logging.LogTestFailure("Executing Misc.VerifyURLContent('"+restURL+"', '"+filecontent+"'), met error ", StringUtils.debugmsg(e));
					}


				}else{
					trace(++fail);
					Logging.LogMessage(preMsg+" fail: "+Map.ThomasBayerPage.ThomasBayerPage.getName()+" does show on page yet.");
				}


			}else{
				trace(++fail);
				Logging.LogTestFailure(preMsg+"StartWebBrowser '"+browser+"' Unsuccessful.");
			}

		}catch(Exception e){
			trace(++fail);
			Logging.LogTestFailure(preMsg+"Fail to test URL API in browser '"+browser+"'! Unexpected Exception "+StringUtils.debugmsg(e));
		}finally{
			if(ID!=null) if(!StopWebBrowser(ID)) fail++;
			if(!Misc.CloseApplicationMap(MAP_FILE_HTMLAPP)) fail++;
			Misc.Expressions(originalExpression);
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
	 * This method is only called by {@link #testAPI_Misc_Alert(String)}.<br>
	 * Test keywords:<br>
	 * <pre>
	 * {@link Misc#IsAlertPresent(String...)}
	 * </pre>
	 *
	 * @param timeout int, the timeout to wait for the presence of Alert
	 * @param alertName String, "Alert", "Confirm" or "Prompt"
	 * @return int the number of un-expected test error
	 */
	private static int test_misc_alert_presence(String counterPrefix, int timeout, String alertName){
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);

		try {
			String timeoutString = (timeout==0? "immediately" : "within "+timeout+" seconds");
			boolean exist = false;
			if(WDLibrary.DEFAULT_TIMEOUT_WAIT_ALERT==timeout){
				exist = Misc.IsAlertPresent();
			}else{
				exist = Misc.IsAlertPresent(String.valueOf(timeout));
			}

			if(exist) Logging.LogMessage(alertName+" has been detected "+timeoutString);
			else Logging.LogMessage(alertName+" has NOT been detected "+timeoutString);
		} catch (SeleniumPlusException e) {
			return fail++;
		}

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);

		return fail;
	}

	/**
	 * Test keywords:<br>
	 * <pre>
	 * {@link Misc#AlertAccept(String)}
	 * {@link Misc#AlertDismiss(String)}
	 * {@link SeleniumPlus#ClickUnverified(org.safs.model.Component, Point)}
	 * {@link SeleniumPlus#Click(org.safs.model.Component, String...)}
	 * {@link Misc#IsAlertPresent(String...)}
	 * </pre>
	 *
	 * This method will test the ability of "turn off click listener if Alert is present" for {@link SeleniumPlus#Click(org.safs.model.Component, String...)}<br>
	 * <br>
	 *
	 * @return int, the total number of failures
	 * @throws Throwable
	 */
	private static int testAPI_Misc_Alert(String counterPrefix, String browser) throws Throwable{
		String preMsg = StringUtils.getMethodName(0, false);
		int fail = 0;
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		String url = null;
		String browserAlertID = null;
		String browserConfirmID = null;
		String browserPromptID = null;

		boolean originalExpression = Misc.isExpressionsOn();

		try{
			if(!Misc.SetApplicationMap(MAP_FILE_HTMLAPP)){
				trace(++fail);
				return fail;
			}

			//Turn off the expression
			Misc.Expressions(false);

			//Test Alert
			url = Map.W3CAlertURL();
			browserAlertID = startBrowser(browser, url);
			if(browserAlertID==null){
				trace(++fail);
				Logging.LogTestFailure(preMsg+"StartWebBrowser '"+url+"' Unsuccessful.");
			}else{
				//Before Alert appears, we can not find the Alert.
				fail += test_misc_alert_presence(counterID, WDLibrary.DEFAULT_TIMEOUT_WAIT_ALERT, "Alert");

				if(ClickUnverified(Map.W3CAlertPage.Button, new Point(10,10))){
					fail += test_misc_alert_presence(counterID, WDLibrary.DEFAULT_TIMEOUT_WAIT_ALERT, "Alert");
					Pause(1);
					if(!Misc.AlertAccept()) trace(++fail);
				}else{
					trace(++fail);
					Logging.LogMessage(preMsg+" fail to click the button to show Alert Dialog, cannot test AlertAccept.");
				}
			}
			if(browserAlertID!=null) if(!StopWebBrowser(browserAlertID)) fail++;

			//Test Confirm
			url = Map.W3CConfirmURL();
			browserConfirmID = startBrowser(browser, url);
			if(browserConfirmID==null){
				trace(++fail);
				Logging.LogTestFailure(preMsg+"StartWebBrowser '"+url+"' Unsuccessful.");
			}else{
				//Before Confirm appears, we can not find the Alert.
				fail += test_misc_alert_presence(counterID, 0, "Confirm");

				if(Click(Map.W3CAlertPage.Button, "10, 10")){
					Pause(1);
					fail += test_misc_alert_presence(counterID, 0, "Confirm");
					if(!Misc.AlertAccept()) trace(++fail);
				}else{
					trace(++fail);
					Logging.LogMessage(preMsg+" fail to click the button to show Confirm Dialog, cannot test AlertAccept.");
				}
				if(Click(Map.W3CAlertPage.Button, "10, 10")){
					Pause(1);
					if(!Misc.AlertDismiss()) trace(++fail);
				}else{
					trace(++fail);
					Logging.LogMessage(preMsg+" fail to click the button to show Confirm Dialog, cannot test AlertDismiss.");
				}
			}
			if(browserConfirmID!=null) if(!StopWebBrowser(browserConfirmID)) fail++;

			//Test Prompt
			url = Map.W3CPromptURL();
			browserPromptID = startBrowser(browser, url);
			if(browserPromptID==null){
				trace(++fail);
				Logging.LogTestFailure(preMsg+"StartWebBrowser '"+url+"' Unsuccessful.");
			}else{
				//Before Prompt appears, we can not find the Alert.
				fail += test_misc_alert_presence(counterID, 0, "Prompt");

				if(Click(Map.W3CAlertPage.Button, "10, 10")){
					Pause(1);
					fail += test_misc_alert_presence(counterID, 0, "Prompt");
					if(!Misc.AlertAccept()) trace(++fail);
				}else{
					trace(++fail);
					Logging.LogMessage(preMsg+" fail to click the button to show Prompt Dialog, cannot test AlertAccept.");
				}
				if(Click(Map.W3CAlertPage.Button, "10, 10")){
					Pause(1);
					if(!Misc.AlertDismiss()) trace(++fail);
				}else{
					trace(++fail);
					Logging.LogMessage(preMsg+" fail to click the button to show Prompt Dialog, cannot test AlertDismiss.");
				}
			}
			if(browserPromptID!=null) if(!StopWebBrowser(browserPromptID)) fail++;

		}catch(Exception e){
			trace(++fail);
			Logging.LogTestFailure(preMsg+"Fail to test Alert API! Unexpected Exception "+StringUtils.debugmsg(e));
		}finally{
			if(!Misc.CloseApplicationMap(MAP_FILE_SAPDEMOAPP)) fail++;
			Misc.Expressions(originalExpression);
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
	public static int runRegressionTest() throws Throwable{
		int fail = 0;
		Counters.StartCounter(COUNTER);

		try{
			//Get initial browser informations
			String browsers = Map.TestBrowserName();
			if(browsers==null || browsers.trim().isEmpty()){
				browsers = FF;
				Logging.LogTestWarning(COUNTER+" cannot get TestBrowserName from map, use "+browsers);
			}
			browsers = browsers.replaceAll(" +", " ");
			String[] browserArray = browsers.split(" ");

			for(String browser: browserArray){
				fail += testAPI(COUNTER, browser);
			}

			fail += testAPIBrowserless(COUNTER);

		}catch(Throwable t){
			fail++;
			Logging.LogTestFailure(COUNTER +" fatal error due to "+t.getClass().getName()+", "+ t.getMessage());
		}

		Counters.StopCounter(COUNTER);
		Counters.StoreCounterInfo(COUNTER, COUNTER);
		Counters.LogCounterInfo(COUNTER);

		if(fail > 0){
			Logging.LogTestFailure(COUNTER+" runRegressionTest reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(COUNTER+" did not report any UNEXPECTED test failures!");
		}
		return fail;
	}

	public void runTest() throws Throwable{
		initUtils();
		runRegressionTest();
	}

}
