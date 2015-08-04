package regression.testcases;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.safs.ComponentFunction;
import org.safs.Domains;
import org.safs.StringUtils;
import org.safs.image.ImageUtils;
import org.safs.model.tools.EmbeddedHookDriverRunner;
import org.safs.selenium.webdriver.SeleniumPlus;
import org.safs.selenium.webdriver.SeleniumPlus.Component;
import org.safs.selenium.webdriver.lib.SeleniumPlusException;
import org.safs.selenium.webdriver.lib.WDLibrary;
import org.safs.text.FileUtilities.FilterMode;

import regression.Map;
import regression.testruns.Regression;

public class GenericMasterTests extends Regression{

	public static final String COUNTER = StringUtils.getClassName(0, false);
	public static final String KEYSTEST = "KeyboardInputTest";

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
			if(Domains.isSapEnabled()) fail+= testAPIForSAP(browser);
		}

		return fail;
	}

	private static int testAPIForHtml(String browser) throws Throwable{
		int fail = 0;
		if(Misc.SetApplicationMap(MAP_FILE_HTMLAPP)){

		}else{
			trace(++fail);
			Logging.LogTestFailure(COUNTER+"Fail to load map '"+MAP_FILE_HTMLAPP+"', cannot test in browser '"+browser+"'!");
		}
		return fail;
	}

	private static int testAPIForDojo(String browser) throws Throwable{
		int fail = 0;
		if(Misc.SetApplicationMap(MAP_FILE_DOJOAPP)){

		}else{
			trace(++fail);
			Logging.LogTestFailure(COUNTER+"Fail to load map '"+MAP_FILE_DOJOAPP+"', cannot test in browser '"+browser+"'!");
		}
		return fail;
	}

	private static int testAPIForSAP(String browser) throws Throwable{
		int fail = 0;
		if(Misc.SetApplicationMap(MAP_FILE_SAPDEMOAPP)){
			String ID = null;

			try{
				ID = startBrowser(browser, Map.SAPDemoURL());
				if(ID!=null){
					fail +=testAPIForSAP_property();
					fail +=testAPIForSAP_CaptureData();
					fail +=testAPIForSAP_Diverse();
					fail +=testAPIForSAP_Image();
					fail +=testExecuteScript();
					fail +=testZoomInOut();
					fail +=testShowOnPage();

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

		}else{
			trace(++fail);
			Logging.LogTestFailure(COUNTER+"Fail to load map '"+MAP_FILE_SAPDEMOAPP+"', cannot test in browser '"+browser+"'!");
		}

		return fail;
	}

	/**
	 * Test keywords:<br>
	 * <pre>
	 * GetGUIImage
	 * VerifyGUIImageToFile
	 * GetTextFromGUI
	 * SaveTextFromGUI
	 * </pre>
	 */
	private static int testAPIForSAP_Image() throws SeleniumPlusException{
		String debugmsg = StringUtils.debugmsg(false);

		int fail = 0;
		if(TabControl.ClickTab(Map.SAPDemoPage.TabControl, Map.Tab_basc_comp())){
			if(!GetGUIImage(Map.SAPDemoPage.MenuBar, "menubar.tif")) trace(++fail);
			if(!GetGUIImage(Map.SAPDemoPage.MenuBar, "menubarPar.tif", "0,0,80%,50%")) trace(++fail);
			if(!GetGUIImage(Map.SAPDemoPage.MenuBar, "menubar.bmp")) trace(++fail);
			if(!GetGUIImage(Map.SAPDemoPage.MenuBar, "menubar.jpeg")) trace(++fail);
			if(!GetGUIImage(Map.SAPDemoPage.MenuBar, "menubar")) trace(++fail);
			if(!GetGUIImage(Map.SAPDemoPage.TabControl, "TabControl.jpeg")) trace(++fail);
			if(!GetGUIImage(Map.SAPDemoPage.TabControl, "TabControl")) trace(++fail);
			if(!GetGUIImage(Map.SAPDemoPage.MenuBar, "menubar.png", Map.subarea)) trace(++fail);

			if(!GetGUIImage(Map.SAPDemoPage.Basc_ListBox, "listview.tif")) trace(++fail);
			if(!GetGUIImage(Map.SAPDemoPage.Basc_ListBox, "listviewpart1.png", Map.subarea)) trace(++fail);

			if(!GetGUIImage(Map.SAPDemoPage.Basc_ListBox, "filteredListview1.png")) trace(++fail);
			if(!GetGUIImage(Map.SAPDemoPage.Basc_ListBox, "filteredListview2.png", "", quote(FILTER+"=0,0,15%,15%"))) trace(++fail);
			if(!GetGUIImage(Map.SAPDemoPage.Basc_ListBox, "filteredListview3.png", "", quote(FILTER+"="+Map.FilteredAreas1))) trace(++fail);
			if(!GetGUIImage(Map.SAPDemoPage.Basc_ListBox, "filteredListview4.png", Map.subarea(), quote(FILTER+"="+Map.FilteredAreas1()))) trace(++fail);
			if(!GetGUIImage(Map.SAPDemoPage.Basc_ListBox, "filteredListview5.png", "", Map.FilteredAreas2)) trace(++fail);
			if(!GetGUIImage(Map.SAPDemoPage.Basc_ListBox, "filteredListview6.png", Map.subarea, Map.FilteredAreas2())) trace(++fail);
			if(!GetGUIImage(Map.SAPDemoPage.Basc_ListBox, "filteredListview7.png", "", quote(FILTER+"="+Map.FilteredAreas2))) trace(++fail);
			if(!GetGUIImage(Map.SAPDemoPage.Basc_ListBox, "filteredListview8.png", Map.subarea, quote(FILTER+"="+Map.FilteredAreas2()))) trace(++fail);
			//GetGUIImage with warnings
			Logging.LogMessage(COUNTER+" ==== 2 Warnings Expected: Without prefix '"+FILTER+"', the parameter FilteredAreas will be ignore, and GetGUIImage produces Warning.");
			if(!GetGUIImage(Map.SAPDemoPage.Basc_ListBox, "filteredListview9.png", "", Map.FilteredAreas1)) trace(++fail);//Without prefix Filter
			if(!GetGUIImage(Map.SAPDemoPage.Basc_ListBox, "filteredListview10.png", "", Map.FilteredAreas1())) trace(++fail);//Without prefix Filter
			Logging.LogMessage(COUNTER+" ==== 1 Warning Expected: Part of 'FilteredAreasWithWarning' are wrong and will be ignore, and GetGUIImage produces Warnings.");
			if(!GetGUIImage(Map.SAPDemoPage.Basc_ListBox, "filteredListview11.png", "", "FilteredAreasWithWarning")) trace(++fail);//Part of areas are wrong
			//GetGUIImage with error
			Logging.LogMessage(COUNTER+" ==== 2 Errors Expected: All filtered areas are wrong and will be ignore, and GetGUIImage produces Error.");
			if(GetGUIImage(Map.SAPDemoPage.Basc_ListBox, "filteredListview12.png", "", quote(FILTER+"=0,0,%,15%"))) trace(++fail);//all areas are wrong
			if(GetGUIImage(Map.SAPDemoPage.Basc_ListBox, "filteredListview13.png", "", "FilteredAreasWithError")) trace(++fail);//all areas are wrong

			String listviewPic = "listview.bmp";
			if(!GetGUIImage(Map.SAPDemoPage.Basc_ListBox, listviewPic)) trace(++fail);
			if(Files.CopyFile(utils.testFile(listviewPic), utils.benchFile(listviewPic))){
				if(!VerifyGUIImageToFile(Map.SAPDemoPage.Basc_ListBox, listviewPic)) trace(++fail);
				if(!VerifyGUIImageToFile(Map.SAPDemoPage.Basc_ListBox, listviewPic, "", "", quote("UUID=FALSE"))) trace(++fail);
				//Remove all generated actual files.
				deleteGeneratedActualFiles(listviewPic);
			}else{
				Logging.LogTestFailure(COUNTER+"Fail copy '"+listviewPic+"' to bench folder, CANNOT do VerifyGUIImageToFile!");
				trace(++fail);
			}

			listviewPic = "listview.png";
			if(!GetGUIImage(Map.SAPDemoPage.Basc_ListBox, listviewPic)) trace(++fail);
			if(Files.CopyFile(utils.testFile(listviewPic), utils.benchFile(listviewPic))){
				if(!VerifyGUIImageToFile(Map.SAPDemoPage.Basc_ListBox, listviewPic)) trace(++fail);
				if(!VerifyGUIImageToFile(Map.SAPDemoPage.Basc_ListBox, listviewPic, "", "", "", quote(FILTER+"=0,0,15%,15%"))) trace(++fail);
				if(!VerifyGUIImageToFile(Map.SAPDemoPage.Basc_ListBox, listviewPic, "", "", "", quote(FILTER+"="+Map.FilteredAreas1))) trace(++fail);
				if(!VerifyGUIImageToFile(Map.SAPDemoPage.Basc_ListBox, listviewPic, "", "", "", quote(FILTER+"="+Map.FilteredAreas1()))) trace(++fail);
				if(!VerifyGUIImageToFile(Map.SAPDemoPage.Basc_ListBox, listviewPic, "", "", "", Map.FilteredAreas2)) trace(++fail);
				if(!VerifyGUIImageToFile(Map.SAPDemoPage.Basc_ListBox, listviewPic, "", "", "", Map.FilteredAreas2())) trace(++fail);
				if(!VerifyGUIImageToFile(Map.SAPDemoPage.Basc_ListBox, listviewPic, "", "", "", quote(FILTER+"="+Map.FilteredAreas2))) trace(++fail);
				if(!VerifyGUIImageToFile(Map.SAPDemoPage.Basc_ListBox, listviewPic, "", "", "", quote(FILTER+"="+Map.FilteredAreas2()))) trace(++fail);
				//VerifyGUIImageToFile with warnings
				Logging.LogMessage(COUNTER+" ==== 2 Warnings Expected: Without prefix '"+FILTER+"', the parameter FilteredAreas will be ignore, and VerifyGUIImageToFile produces Warning.");
				if(!VerifyGUIImageToFile(Map.SAPDemoPage.Basc_ListBox, listviewPic, "", "", "", Map.FilteredAreas1)) trace(++fail);
				if(!VerifyGUIImageToFile(Map.SAPDemoPage.Basc_ListBox, listviewPic, "", "", "", Map.FilteredAreas1())) trace(++fail);
				Logging.LogMessage(COUNTER+" ==== 1 Warning Expected: Part of 'FilteredAreasWithWarning' are wrong and will be ignore, and VerifyGUIImageToFile produces Warnings.");
				if(!VerifyGUIImageToFile(Map.SAPDemoPage.Basc_ListBox, listviewPic, "", "", "", "FilteredAreasWithWarning")) trace(++fail);
				//VerifyGUIImageToFile with error
				Logging.LogMessage(COUNTER+" ==== 2 Errors Expected: All filtered areas are wrong, and VerifyGUIImageToFile produces Error.");
				if(VerifyGUIImageToFile(Map.SAPDemoPage.Basc_ListBox, listviewPic, "", "", "", quote(FILTER+"=0,0,%,15%"))) trace(++fail);
				if(VerifyGUIImageToFile(Map.SAPDemoPage.Basc_ListBox, listviewPic, "", "", "", "FilteredAreasWithError")) trace(++fail);

				//Remove all generated actual files.
				deleteGeneratedActualFiles(listviewPic);

			}else{
				Logging.LogTestFailure(COUNTER+"Fail copy '"+listviewPic+"' to bench folder, CANNOT do VerifyGUIImageToFile!");
				trace(++fail);
			}

			String listviewBenchPic = "listviewBench.png";
			boolean benchReady = false;
			//Test the tolerance of VerifyGUIImageToFile and VerifyBinaryFileToFile
			try{
				//First, modify the actual image and save to a bench image.
				BufferedImage bufimg = ImageUtils.getStoredImage(utils.testFile(listviewPic));
				int width = bufimg.getWidth();
				int height = bufimg.getHeight();
				int startx = width/4, endx=3*width/4;
				int starty = height/4, endy=3*height/4;

				Graphics g = bufimg.getGraphics();
				g.setColor(Color.red);
				g.drawLine(startx, starty, endx, endy);
				g.drawLine(endx, starty, startx, endy);

				for(int i=startx;i<endx;i++) ImageUtils.paintOnImage(bufimg, i, starty, 2, 2, Color.cyan, false);
				for(int i=startx;i<endx;i++) ImageUtils.paintOnImage(bufimg, i, endy, 2, 2, Color.cyan, false);
				for(int j=starty;j<endy;j++) ImageUtils.paintOnImage(bufimg, startx, j, 2, 2, Color.cyan, false);
				for(int j=starty;j<endy;j++) ImageUtils.paintOnImage(bufimg, endx, j, 2, 2, Color.cyan, false);
				ImageUtils.saveImageToFile(bufimg, new File(utils.benchFile(listviewBenchPic)));
				benchReady = true;
			}catch(Throwable th){
				benchReady = false;
			}

			if(benchReady){
				Logging.LogMessage(COUNTER+" ==== 1 Error Expected: Tolerance is not enough, VerifyGUIImageToFile produces Error.");
				if(VerifyGUIImageToFile(Map.SAPDemoPage.Basc_ListBox, listviewBenchPic, "", "98")) trace(++fail);
				if(!VerifyGUIImageToFile(Map.SAPDemoPage.Basc_ListBox, listviewBenchPic, "", "90")) trace(++fail);

				if(VerifyBinaryFileToFile(listviewBenchPic, listviewPic, FilterMode.TOLERANCE.name, "98")) trace(++fail);
				if(!VerifyBinaryFileToFile(listviewBenchPic, listviewPic, FilterMode.TOLERANCE.name, "90")) trace(++fail);

				//Remove all generated actual files.
				deleteGeneratedActualFiles(listviewBenchPic);
			}

			String output = "textVariable";
			Component.GetTextFromGUI(Map.SAPDemoPage.Basc_Password_L, output);

			Component.GetTextFromGUI(Map.SAPDemoPage.Basc_Button, output, "", "GOCR");

			output = "textVariable-partial";
			Component.GetTextFromGUI(Map.SAPDemoPage.Basc_Button, output, "0, 0, 70%, 100%", "TOCR");

			output = "lableTextArea.txt";
			Component.SaveTextFromGUI(Map.SAPDemoPage.Basc_TextArea_L, output);

			output = "checkbox.txt";
			Component.SaveTextFromGUI(Map.SAPDemoPage.Basc_CheckBox, output, "", "GOCR");

			output = "checkbox-partial.txt";
			Component.SaveTextFromGUI(Map.SAPDemoPage.Basc_CheckBox, quote(output), "0, 0, 70%, 100%", "TOCR");

		}else{
			Logging.LogTestWarning(debugmsg+" Fail to ClickTab '"+Map.Tab_basc_comp()+"', some APIs not tested!");
			trace(++fail);
		}

		return fail;
	}

	/**
	 * Test keywords:<br>
	 * <pre>
	 * AssignPropertyVariable
	 * CapturePropertiesToFile
	 * CapturePropertyToFile
	 * IsPropertyExist
	 * VerifyPropertyContains
	 * VerifyPropertyToFile
	 * VerifyPropertiesToFile
	 * </pre>
	 */
	private static int testAPIForSAP_property() throws SeleniumPlusException{
		String debugmsg = StringUtils.debugmsg(false);

		int fail = 0;

		if(TabControl.ClickTab(Map.SAPDemoPage.TabControl, Map.Tab_basc_comp())){

			String labelVar = "labelVar";
			String property = "textContent";
			String labelContent = "Password";
			if(Component.AssignPropertyVariable(Map.SAPDemoPage.Basc_Password_L , property, labelVar)){
				String label = GetVariableValue(labelVar);

				if(!VerifyValues(label, labelContent)){
					trace(++fail);
					Logging.LogTestFailure(COUNTER+" the value of property '"+property+"' is '"+label+"', NOT equal to '"+labelContent+"'!");
				}
			}else{
				trace(++fail);
			}

			String filename = "combobox.properties";
			String encoding = quote("utf-8");
			if(!Component.CapturePropertiesToFile(Map.SAPDemoPage.Basc_ComboBox, filename, encoding)) trace(++fail);

			if(Files.CopyFile(utils.testFile(filename), utils.benchFile(filename))){
				if(!Component.VerifyPropertiesToFile(Map.SAPDemoPage.Basc_ComboBox, filename, encoding)) trace(++fail);
			}else{
				Logging.LogTestWarning(debugmsg+" Fail to copy '"+filename+"' to bench directory.");
				trace(++fail);
			}

			property = "color";
			filename = "combobox."+property+".dat";
			if(!Component.CapturePropertyToFile(Map.SAPDemoPage.Basc_ComboBox, property, filename)) trace(++fail);

			property = "display";
			if(Component.IsPropertyExist(Map.SAPDemoPage.Basc_ComboBox, property)){
				filename = "combobox."+property+".dat";
				if(!Component.CapturePropertyToFile(Map.SAPDemoPage.Basc_ComboBox, property, filename, encoding)) trace(++fail);
			}else{
				Logging.LogTestWarning(debugmsg+"property '"+property+"' of '"+Map.SAPDemoPage.Basc_ComboBox.getName()+"' does NOT exist.");
				trace(++fail);
			}

			property = "width";
			if(Component.IsPropertyExist(Map.SAPDemoPage.Basc_ComboBox, property)){
				filename = "combobox."+property+".dat";
				if(!Component.CapturePropertyToFile(Map.SAPDemoPage.Basc_ComboBox, property, filename, encoding)) trace(++fail);
			}else{
				Logging.LogTestWarning(debugmsg+"property '"+property+"' of '"+Map.SAPDemoPage.Basc_ComboBox.getName()+"' does NOT exist.");
				trace(++fail);
			}

			property = "BOGUSPROPERTY";
			if(Component.IsPropertyExist(Map.SAPDemoPage.Basc_ComboBox, property)){
				Logging.LogTestWarning(debugmsg+"Unexpected: property '"+property+"' of '"+Map.SAPDemoPage.Basc_ComboBox.getName()+"' exists.");
				trace(++fail);
			}

			property = "font-family";//need quoted to avoid expression-evaluation
			String containedValue = "Helvetica";
			if(!Component.VerifyPropertyContains(Map.SAPDemoPage.Basc_ComboBox, quote(property), containedValue)) trace(++fail);

			containedValue = "HELVETICA";
			if(!Component.VerifyPropertyContains(Map.SAPDemoPage.Basc_ComboBox, quote(property), containedValue, false)) trace(++fail);

			if(Component.VerifyPropertyContains(Map.SAPDemoPage.Basc_ComboBox, quote(property), containedValue, true)){
				Logging.LogTestWarning(debugmsg+"Unexpected: property '"+property+"' of '"+Map.SAPDemoPage.Basc_ComboBox.getName()+"' contains '"+containedValue+"'.");
				trace(++fail);
			}

			property = "display";
			filename = "combobox."+property+".dat";
			if(Files.CopyFile(utils.testFile(filename), utils.benchFile(filename))){
				if(!Component.VerifyPropertyToFile(Map.SAPDemoPage.Basc_ComboBox, property, filename, encoding)) trace(++fail);
			}else{
				Logging.LogTestWarning(debugmsg+" Fail to copy '"+filename+"' to bench directory.");
				trace(++fail);
			}

		}else{
			Logging.LogTestWarning(debugmsg+" Fail to ClickTab '"+Map.Tab_basc_comp()+"', some APIs not tested!");
			trace(++fail);
		}
		return fail;
	}

	/**
	 * Test keywords:<br>
	 * <pre>
	 * CaptureObjectDataToFile, it can work on Menu, Tree, List etc.
	 * CaptureTreeDataToFile
	 * </pre>
	 */
	private static int testAPIForSAP_CaptureData() throws SeleniumPlusException{
		int fail = 0;
		String debugmsg = StringUtils.debugmsg(false);

		if(!Component.CaptureObjectDataToFile(Map.SAPDemoPage.MenuBar, "menubar.dat", quote("utf-8"))) trace(++fail);

		if(TabControl.ClickTab(Map.SAPDemoPage.TabControl, Map.Tab_basc_comp())){
			if(!Component.CaptureObjectDataToFile(Map.SAPDemoPage.Basc_Link, "link.dat", quote("utf-8"))) trace(++fail);

			if(!Component.CaptureObjectDataToFile(Map.SAPDemoPage.Basc_ListBox, "listview.dat", quote("utf-8"))) trace(++fail);

			if(!Component.CaptureObjectDataToFile(Map.SAPDemoPage.Basc_ComboBox, "combobox.dat", quote("utf-8"))) trace(++fail);

		}else{
			trace(++fail);
			Logging.LogTestWarning(debugmsg+" Fail to ClickTab '"+Map.Tab_basc_comp()+"', some APIs not tested!");
		}

		if(TabControl.ClickTab(Map.SAPDemoPage.TabControl, Map.Tab_jtree2())){
			String testfile = "tree.dat";
			String benchfile = "tree_bench.dat";

			if(!Component.CaptureObjectDataToFile(Map.SAPDemoPage.TreeView, testfile, quote("utf-8"))) trace(++fail);
			else{
				if(Files.CopyFile(utils.testFile(testfile), utils.benchFile(benchfile))){
					if(!Component.VerifyObjectDataToFile(Map.SAPDemoPage.TreeView, "tree_bench.dat", quote("utf-8"))) trace(++fail);
				}else{
					Logging.LogTestWarning(debugmsg+" Fail to copy test file '"+testfile+"' to bench file '"+benchfile+"'.");
					trace(++fail);
				}
			}

			if(!Tree.CaptureTreeDataToFile(Map.SAPDemoPage.TreeView, "Jims.dat")) trace(++fail);
			if(!Tree.CaptureTreeDataToFile(Map.SAPDemoPage.TreeView, "Armisteads.dat", quote("Jim Goodnight->Armistead Sapp"), "\t", quote("utf-8"))) trace(++fail);
		}else{
			trace(++fail);
			Logging.LogTestWarning(debugmsg+" Fail to ClickTab '"+Map.Tab_jtree2()+"', some APIs not tested!");
		}

		return fail;
	}
	/**
	 * Test keywords:<br>
	 * <pre>
	 * HoverMouse
	 * LocateScreenImage
	 * </pre>
	 */
	private static int testAPIForSAP_Diverse() throws SeleniumPlusException{
		int fail = 0;
		String debugmsg = StringUtils.debugmsg(false);

		if(TabControl.ClickTab(Map.SAPDemoPage.TabControl, Map.Tab_basc_comp())){

			if(!Component.HoverMouse(Map.SAPDemoPage.Basc_Radio)) trace(++fail);
			if(!Component.HoverMouse(Map.SAPDemoPage.Basc_Radio, "10,5", "4000")) trace(++fail);
			if(!Component.HoverMouse(Map.SAPDemoPage.Basc_ToggleButton, Map.locationA, "1000")) trace(++fail);
			if(!Component.HoverMouse(Map.SAPDemoPage.Basc_Link, "offset", "3000")) trace(++fail);

			String variable = "editboxRect";
			String rect = null;
			if(Component.LocateScreenImage(Map.SAPDemoPage.Basc_ListBox, variable)){
				rect = GetVariableValue(variable);
				String x = GetVariableValue(variable+".x");
				String y = GetVariableValue(variable+".y");
				String w = GetVariableValue(variable+".w");
				String h = GetVariableValue(variable+".h");
				Logging.LogMessage("ListBox screen rectangle "+rect);
				Logging.LogMessage("ListBox screen rectangle ["+x+","+y+","+w+","+h+"]");
			}else{
				trace(++fail);
			}

			variable = "editboxRectRelative";
			if(Component.LocateScreenImage(Map.SAPDemoPage.Basc_ListBox, variable, RELATIVE_TO_PARENT)){
				rect = GetVariableValue(variable);
				Logging.LogMessage("ListBox relative rectangle "+rect);
			}else{
				trace(++fail);
			}
		}else{
			trace(++fail);
			Logging.LogTestWarning(debugmsg+" Fail to ClickTab '"+Map.Tab_basc_comp()+"', some APIs not tested!");
		}

		return fail;
	}

	/**
	 * Test different forms of keyboard input (SAFS and Selenium).
	 * @return
	 * @throws Throwable
	 */
	public static int testKeyboardInput() throws Throwable{
		int fail = 0;
		Counters.StartCounter(KEYSTEST);
		String CLEAR = "^a{DELETE}";

		if(! StartWebBrowser("http://www.google.com", KEYSTEST, "explorer")) trace(++fail);
		if(fail == 0){
			if(! Misc.SetApplicationMap("MiscTests")) trace(++fail);
			if(! Click(Map.Google.SignIn)) trace(++fail);
			// test InputCharacters
			if(! Component.InputKeys(Map.LogIn.UserName, quote(CLEAR))) trace(++fail);
			if(! Component.VerifyProperty(Map.LogIn.UserName, "value", "")) trace(++fail);
			if(! Component.InputCharacters(Map.LogIn.UserName, Map.GoogleUser())) trace(++fail);
			if(! Component.VerifyProperty(Map.LogIn.UserName, "value", Map.GoogleUser())) trace(++fail);
			if(! Component.InputKeys(Map.LogIn.Passwd, quote(CLEAR))) trace(++fail);
			if(! Component.InputCharacters(Map.LogIn.Passwd, Map.GooglePassword())) trace(++fail);

			WebElement e = getObject(Map.LogIn.UserName);
			if(e != null){
				try{
					WDLibrary.inputChars(e,  Map.GoogleUser());}catch(Throwable t){ trace(++fail);
					Logging.LogTestFailure(KEYSTEST+" failed WDLibrary.inputChars "+ Map.GoogleUser());}
				if(! Component.VerifyProperty(Map.LogIn.UserName, "value", Map.GoogleUser()+Map.GoogleUser())) trace(++fail);

				try{ WDLibrary.inputKeys(e,  CLEAR);}catch(Throwable t){ trace(++fail);
					Logging.LogTestFailure(KEYSTEST+" failed WDLibary.inputKeys "+ CLEAR);}
				if(! Component.VerifyProperty(Map.LogIn.UserName, "value", "")) trace(++fail);

				try{ WDLibrary.inputKeys(e,  Map.GoogleUser());}catch(Throwable t){ trace(++fail);
					Logging.LogTestFailure(KEYSTEST+" failed WDLibary.inputKeys "+ Map.GoogleUser());}
				if(! Component.VerifyProperty(Map.LogIn.UserName, "value", Map.GoogleUser())) trace(++fail);

				try{ WDLibrary.inputKeysSAFS2Selenium(e,  CLEAR);}catch(Throwable t){ trace(++fail);
					Logging.LogTestFailure(KEYSTEST+" failed WDLibary.inputKeysSAFS2Selenium "+ CLEAR);}
				if(! Component.VerifyProperty(Map.LogIn.UserName, "value", "")) trace(++fail);

				try{ WDLibrary.inputKeysSAFS2Selenium(e,  Map.GoogleUser());}catch(Throwable t){ trace(++fail);
					Logging.LogTestFailure(KEYSTEST+" failed WDLibary.inputKeysSAFS2Selenium "+ Map.GoogleUser());}
				if(! Component.VerifyProperty(Map.LogIn.UserName, "value", Map.GoogleUser())) trace(++fail);

				// TODO: try direct selenium and it still does not seem to work for ie, chrome, or firefox.
				// this needs to be removed when the problem is resolved

					e.sendKeys("");
					Actions actions = new Actions(WDLibrary.getWebDriver());
					Action series = actions.moveToElement(e)
									.keyDown(e, Keys.CONTROL)
									.sendKeys(e, "a")
									.keyUp(e, Keys.CONTROL)
									.sendKeys(e, Keys.DELETE)
									.build();
					series.perform();
					if(! Component.VerifyProperty(Map.LogIn.UserName, "value", "")) trace(++fail);

				// TODO: end of above section to be removed


			}else{
				fail++;
				Logging.LogTestFailure(KEYSTEST+" failed to retrieve Google UserName EditBox field for testing.");
			}

			if(! StopWebBrowser(KEYSTEST)) trace(++fail);
		}
		Counters.StopCounter(KEYSTEST);
		Counters.StoreCounterInfo(KEYSTEST, KEYSTEST);
		Counters.LogCounterInfo(KEYSTEST);
		if(fail > 0){
			Logging.LogTestFailure(KEYSTEST+" reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(KEYSTEST+" did not report any UNEXPECTED test failures!");
		}
		return fail;
	}

	/**
	 * Test keywords:<br>
	 * <pre>
	 * ExecuteScript
	 * executeScript
	 * </pre>
	 */
	private static int testExecuteScript() throws Throwable{
		int fail = 0;
		String debugmsg = StringUtils.debugmsg(false);

		if(TabControl.ClickTab(Map.SAPDemoPage.TabControl, Map.Tab_basc_comp())){
			String script = "arguments[0].innerHTML=arguments[1];";
			String text = "My Text Area Label";
			if(!ExecuteScript(Map.SAPDemoPage.Basc_TextArea_L, script, text)) trace(++fail);
			//Verify the innerHtml has been set correctly
			script = "return arguments[0].innerHTML;";
			if(ExecuteScript(Map.SAPDemoPage.Basc_TextArea_L, script)){
				String value = prevResults.getStatusInfo();
				if(!value.equals(text)) trace(++fail);
			}else{
				trace(++fail);
			}

			WebElement we = getObject(Map.SAPDemoPage.Basc_TextArea_L);
			script = "arguments[0].innerHTML=arguments[1];";
			text = "Some Other Label Text Testing";
			try{
				executeScript(script, we, text);
			}catch(SeleniumPlusException e){
				trace(++fail);
			}
			//Verify the innerHtml has been set correctly
			script = "return arguments[0].innerHTML;";
			try{
				Object result = executeScript(script, we);
				if(!text.equals(result)) trace(++fail);
			}catch(SeleniumPlusException e){
				trace(++fail);
			}

		}else{
			trace(++fail);
			Logging.LogTestWarning(debugmsg+" Fail to ClickTab '"+Map.Tab_basc_comp()+"', some APIs not tested!");
		}

		return fail;
	}

	/**
	 * Test keywords:<br>
	 * <pre>
	 * {@link WDLibrary#keyPress(int)}
	 * {@link WDLibrary#mouseWheel(int)}
	 * {@link WDLibrary#keyRelease(int)}
	 * {@link WDLibrary#inputKeys(WebElement, String)}
	 * </pre>
	 */
	private static int testZoomInOut() throws Throwable{
		int fail = 0;
		String debugmsg = StringUtils.debugmsg(false);
		
		if(TabControl.ClickTab(Map.SAPDemoPage.TabControl, Map.Tab_basc_comp())){
			WebElement textarea = SeleniumPlus.getObject(Map.SAPDemoPage.Basc_TextArea);
			WDLibrary.focus(textarea);

			try{
				//by Robot Ctrl+MouseWheel (local robot or remote RMI server)
				WDLibrary.keyPress(KeyEvent.VK_CONTROL);
				WDLibrary.mouseWheel(5);
				WDLibrary.keyRelease(KeyEvent.VK_CONTROL);
				Pause(5);
			}catch(Exception e){
				//If running on remote grid/node and SAFS RMI server is not running, we will fail
				Logging.LogTestWarning(debugmsg+" Fail to zoom by Ctrl+MouseWheel. '"+StringUtils.debugmsg(e));
				fail++;
			}

			try{
				//by keys Ctrl+, Ctrl-, Ctrl0, thru WDLibrary
				WDLibrary.inputKeys(textarea, "^{+}");//Will not work with Selenium API, Ctrl-down, Shift-down, Equals, Shift-Up, Ctrl-up
				WDLibrary.inputKeys(textarea, "^{Num+}");//With Selenium API, Ctrl-down, Keys.ADD, Ctrl-up
				WDLibrary.inputKeys(textarea, "^{Num-}");
				WDLibrary.inputKeys(textarea, "^{Num+ 5}");//zoom in 5 times
				WDLibrary.inputKeys(textarea, "^{Num- 2}");//zoom out 2 times
				WDLibrary.inputKeys(textarea, "^{Num0}");
				Pause(5);
			}catch(Exception e){
				Logging.LogTestWarning(debugmsg+" Fail to zoom by WDLibrary Ctrl+/-. '"+StringUtils.debugmsg(e));
				fail++;
			}
		
			try{
				//by keys Ctrl+, Ctrl-, Ctrl0, thru wrapper class Component
				Component.InputKeys(Map.SAPDemoPage.Basc_TextArea, "^{Num+}");
				Component.InputKeys(Map.SAPDemoPage.Basc_TextArea, "^{Num-}");
				Component.InputKeys(Map.SAPDemoPage.Basc_TextArea, "^{Num0}");
				Pause(5);
			}catch(Exception e){
				Logging.LogTestWarning(debugmsg+" Fail to zoom by SeleniumPlus Ctrl+/-. '"+StringUtils.debugmsg(e));
				fail++;
			}
			
			try{
				//by keys Ctrl+, Ctrl-, Ctrl0, thru Selenium API directly
				//Selenium API is not reliable, Ctrl+ cannot work on firefox
				WebDriver wd = SeleniumPlus.WebDriver();
				Actions actions = null;
				actions = new Actions(wd);
				WDLibrary.focus(textarea);
				actions.keyDown(Keys.CONTROL).perform();
				
				actions.sendKeys("+").perform();//can NOT zoom in
				actions.sendKeys("-").perform();//can zoom out
				actions.sendKeys("0").perform();//can zoom to normal size
				
				//can NOT zoom in, by Shift=
				actions.keyDown(Keys.SHIFT).perform();
				actions.sendKeys(Keys.EQUALS).perform();
				actions.keyUp(Keys.SHIFT).perform();
				
				actions.sendKeys(Keys.ADD).perform();
				actions.sendKeys(Keys.SUBTRACT).perform();
				actions.sendKeys(Keys.NUMPAD0).perform();//can zoom to normal size
				
				actions.keyUp(Keys.CONTROL).perform();
			}catch(Exception e){
				Logging.LogTestWarning(debugmsg+" Fail to zoom by Selenium Actions Ctrl+/-. '"+StringUtils.debugmsg(e));
				//fail++;//It is not SE+ bug, don't increment the number of fail
			}
			
		}else{
			trace(++fail);
			Logging.LogTestWarning(debugmsg+" Fail to ClickTab '"+Map.Tab_basc_comp()+"', some APIs not tested!");
		}
		
		return fail;
	}
	
	private static int testShowOnPage() throws Throwable{
		int fail = 0;
		String debugmsg = StringUtils.debugmsg(false);
		
		if(TabControl.ClickTab(Map.SAPDemoPage.TabControl, Map.Tab_basc_comp())){
			
			try{
				Window.SetPosition(Map.SAPDemoPage.SAPDemoPage, 0, 0, 1024, 768);
				if(!Component.ShowOnPage(Map.SAPDemoPage.Basc_ListBox)) fail++;
				Pause(2);
				if(!Component.ShowOnPage(Map.SAPDemoPage.Basc_Link)) fail++;
				Pause(2);
				if(!Component.ShowOnPage(Map.SAPDemoPage.Basc_ListBox, "true")) fail++;
				Pause(2);
				if(!Component.ShowOnPage(Map.SAPDemoPage.Basc_Link, "true")) fail++;
				Pause(2);

				//Make the window small enough so that the component cannot be fully shown
				Window.SetPosition(Map.SAPDemoPage.SAPDemoPage, 0, 0, 400, 300);
				if(!Component.ShowOnPage(Map.SAPDemoPage.Basc_ListBox)) fail++;
				Pause(2);
				if(!Component.ShowOnPage(Map.SAPDemoPage.Basc_Link)) fail++;
				Pause(2);
				if(!Component.ShowOnPage(Map.SAPDemoPage.Basc_ListBox, "true")){
					Logging.LogTestWarning("Verification error, it is caused by Selenium's API getLocation return old location after element has been moved. ");
				}
				Pause(2);
				if(!Component.ShowOnPage(Map.SAPDemoPage.Basc_Link, "true")){
					Logging.LogTestWarning("Verification error, it is caused by Selenium's API getLocation return negative value for x-coordinates. ");
				}
				Pause(2);
				
				//Set back the window's location
				Window.SetPosition(Map.SAPDemoPage.SAPDemoPage, 0, 0, 1024, 768);
				
			}catch(Exception e){
				//If running on remote grid/node and SAFS RMI server is not running, we will fail
				Logging.LogTestWarning(debugmsg+" Fail to zoom by Ctrl+MouseWheel. '"+StringUtils.debugmsg(e));
				fail++;
			}

		}else{
			trace(++fail);
			Logging.LogTestWarning(debugmsg+" Fail to ClickTab '"+Map.Tab_basc_comp()+"', some APIs not tested!");
		}
		
		return fail;
	}
	
	
	
	/**
	 * As the VerifyGUIImageToFile will generate a lot of acutal files, if they
	 * are not useful, we can delete them by this method.
	 * @param filename String
	 */
	private static void deleteGeneratedActualFiles(final String filename){
		final File actualDir = new File(utils.testDir());
		String[] names = filename.split("\\.");
		if(names.length==2) deleteFiles(actualDir, names[0]+"[0-9]+\\."+names[1]);
		else{
			System.err.println("Cannot delete generated acutal file for file '"+filename+"'");
		}
	}
	private static void deleteFiles(final File directory, final String regex){
		try{
			File[] files = directory.listFiles(new FilenameFilter(){
				public boolean accept(File dir, String name) {
					if(dir.getAbsolutePath().equals(directory.getAbsolutePath())){
						return Pattern.matches(regex, name);
					}
					return false;
				}
			});
			for(File file:files) file.delete();
		}catch(Exception ignore){
			System.err.println("Met "+StringUtils.debugmsg(ignore));
		}
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

		fail += testKeyboardInput();

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

		// TODO: remove when run as part of runRegressionTest()
		testKeyboardInput();

		List<String> enabledDomains = new ArrayList<String>();
		enabledDomains.add(Domains.HTML_DOMAIN);
		enabledDomains.add(Domains.HTML_DOJO_DOMAIN);
		enabledDomains.add(Domains.HTML_SAP_DOMAIN);

		runRegressionTest(Runner, enabledDomains);
	}

}