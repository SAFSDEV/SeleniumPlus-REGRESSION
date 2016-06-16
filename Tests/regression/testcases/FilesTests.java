package regression.testcases;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.safs.StringUtils;
import org.safs.image.ImageUtils.SubArea;
import org.safs.model.tools.EmbeddedHookDriverRunner;
import org.safs.text.FileUtilities;
import org.safs.text.FileUtilities.Access;
import org.safs.text.FileUtilities.DateType;
import org.safs.text.FileUtilities.FileAttribute;
import org.safs.text.FileUtilities.FileAttribute.Type;
import org.safs.text.FileUtilities.Mode;
import org.safs.text.FileUtilities.PatternFilterMode;
import org.safs.text.FileUtilities.Placement;
import org.safs.tools.ocr.OCREngine;

import regression.testruns.Regression;

public class FilesTests extends Regression{
	
	public static final String COUNTER = StringUtils.getClassName(0, false);
	
	/**
	 * 
	 * @param cleanAll boolean, if user wants to keep the generated files/directories, then provide false.
	 *                          if true, program will delete all the generated files/directories.
	 * @return
	 * @throws Throwable
	 */
	private static int testAPI(String counterPrefix, boolean cleanAll) throws Throwable{
		String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		Counters.StartCounter(counterID);
		boolean expression = Misc.isExpressionsOn();
		if(expression){
			if(!Misc.Expressions(false)){
				Logging.LogTestWarning(counterID+" Fail to turn off Expression! Some APIs may fail!");
			}
		}
		
		int fail = 0;
				
		final String result = "result";
		String resultValue = null;
		String baseDirecotory = utils.appendDir(utils.testDir(), "TestFilesAPI", true);// %Project%/Actulas/TestFilesAPI/
		String directory = baseDirecotory;
		String file = null;
		String dest = null;
		String fileNo = null;
		
		//CreateDirectory
		if(!Files.CreateDirectory(directory)){
			throw new Exception(counterID+" Fail create directory '"+directory+"', cannot continue.");
		}
		
		//CopyFile
		file = utils.testAssetFile("SASStudioDaily.htm");
		dest = utils.appendDir(baseDirecotory, "newSASStudioDaily.htm");
		if(!Files.CopyFile(quote(file), quote(dest))) fail++;

		//FilterTextFile
		String stringToReplace = "html";
		String replaceString = "HTML";
		if(!Files.FilterTextFile(dest, stringToReplace, replaceString, "UTF-8")) fail++;
		else{
			//We check the content has been modified to what we expected.
			String content = null;
			boolean verified = false;
			try{
				content = FileUtilities.readStringFromUTF8File(dest);
				if(!content.contains(stringToReplace) && content.contains(replaceString)) verified = true;
			}catch(Exception ignore){}
			
			if(!verified){
				Logging.LogTestFailure(counterID+"'"+dest+"' still contains string '"+stringToReplace+"'.");
				fail++;
			}
		}

		//====== Relative to File Attributes =====
		String[] files = {
				utils.testAssetFile("safs_zh.properties"),
				utils.testAssetFile("safs_zh_cn.properties"),
				utils.testAssetFile("chromePreferences.dat"),
				utils.testAssetFile("sample_escp.json"),
				utils.testAssetFile("firefoxPreferences.dat"),
				utils.testAssetFile("test.json"),
				utils.testAssetFile("log.txt"),
				utils.testAssetFile("history.dat"),
				utils.testAssetFile("keyword.png"),
				utils.testAssetFile(".hidden.test"),
				utils.testAssetFile(".gitk"),
				utils.testAssetFile("ntuser.pol"),
				utils.testAssetFile("SASStudioDaily.htm")
				};
		file = files[9];//utils.testAssetFile("ntuser.pol");
		int attributes = -1;
		FileAttribute attribute = null;
		//GetFileProtections
		if(Files.GetFileProtections(file, result)){
			resultValue = GetVariableValue(result);
			try{
				attributes = Integer.parseInt(resultValue);
				attribute = FileAttribute.instance(attributes);
				Logging.LogTestSuccess(counterID+" GetFileProtections: code="+resultValue+" "+attribute.toString());
			}catch(NumberFormatException ufe){
				fail++;
				Logging.LogTestFailure(counterID+" GetFileProtections: code'"+resultValue+"' is not a number!");
			}
		}else{
			fail++;
		}
		
		//SetFileProtections
		FileAttribute expectedAttribute = null;
		expectedAttribute = FileAttribute.instance(FileAttribute.Type.SYSTEMFILE);
		expectedAttribute.add(FileAttribute.Type.READONLYFILE);
		expectedAttribute.add(FileAttribute.Type.ARCHIVEFILE);
		if(Files.SetFileProtections(file, expectedAttribute)){
			if(Files.GetFileProtections(file, result)){
				resultValue = GetVariableValue(result);
				try{
					attributes = Integer.parseInt(resultValue);
					attribute = FileAttribute.instance(attributes);
					if(!expectedAttribute.equals(attribute)){
						fail++;
						Logging.LogTestFailure(counterID+" SetFileProtections: fail to set as '"+expectedAttribute+"', it is '"+attribute+"'");
					}
				}catch(NumberFormatException ufe){
					fail++;
					Logging.LogTestFailure(counterID+" GetFileProtections: code'"+resultValue+"' is not a number!");
				}
			}else{
				fail++;
			}
		}else{
			fail++;
		}
		
		//GetFiles
		//Set all file in array files to normal file.
		expectedAttribute = FileAttribute.instance(FileAttribute.Type.NORMALFILE);
		for(String afile:files) if(!Files.SetFileProtections(afile, expectedAttribute)) fail++;
		String directoryToCheck = utils.testAssetDir();
		String fileList = null;
		//Verify that GetFiles get all the normal files
		fileList = directory+"normalList2.txt";
		if(Files.GetFiles(directoryToCheck, fileList)){//GetFiles first API
			String contents = FileUtilities.readStringFromUTF8File(fileList);
			for(String afile:files){
				if(!contents.contains(afile)){
					fail++;
					Logging.LogTestFailure(counterID+" GetFiles of type '"+expectedAttribute.toString()+"', miss return file '"+afile+"'");
					break;
				}
			}
		}else{
			fail++;
		}
		
		fileList = directory+"normalList.txt";
		if(Files.GetFiles(directoryToCheck, fileList, FileAttribute.instance())){//GetFiles second API
			String contents = FileUtilities.readStringFromUTF8File(fileList);
			for(String afile:files){
				if(!contents.contains(afile)){
					fail++;
					Logging.LogTestFailure(counterID+" GetFiles of type '"+expectedAttribute.toString()+"', miss return file '"+afile+"'");
					break;
				}
			}
		}else{
			fail++;
		}
		
		//Set all file in array files to readonly file.
		//As Files.GetFiles will always return "normal file", to avoid returning all files,
		//change them to a special type "readonly"
		expectedAttribute = FileAttribute.instance(FileAttribute.Type.READONLYFILE);
		for(String afile:files) if(!Files.SetFileProtections(afile, expectedAttribute)) fail++;
		
		//Verify that GetFiles get all the archive files + normal files
		String[] archiveFiles = {files[4], files[5]};
		expectedAttribute = FileAttribute.instance(FileAttribute.Type.ARCHIVEFILE);
		for(String afile:archiveFiles) if(!Files.SetFileProtections(afile, expectedAttribute)) fail++;
		fileList = directory+"archiveList.txt";
		if(Files.GetFiles(directoryToCheck, fileList, new FileAttribute(Type.ARCHIVEFILE))){
			String contents = FileUtilities.readStringFromUTF8File(fileList);
			for(String afile:archiveFiles){
				if(!contents.contains(afile)){
					fail++;
					Logging.LogTestFailure(counterID+" GetFiles of type '"+expectedAttribute.toString()+"', miss return file '"+afile+"'");
					break;
				}
			}
		}else{
			fail++;
		}
		
		//Verify that GetFiles get all the hidden files + normal files
		String[] hiddenFiles = {files[7], files[8]};
		expectedAttribute = FileAttribute.instance(FileAttribute.Type.HIDDENFILE);
		for(String afile:hiddenFiles) if(!Files.SetFileProtections(afile, expectedAttribute)) fail++;
		fileList = directory+"hiddenList.txt";
		if(Files.GetFiles(directoryToCheck, fileList, new FileAttribute(Type.HIDDENFILE))){
			String contents = FileUtilities.readStringFromUTF8File(fileList);
			for(String afile:hiddenFiles){
				if(!contents.contains(afile)){
					fail++;
					Logging.LogTestFailure(counterID+" GetFiles of type '"+expectedAttribute.toString()+"', miss return file '"+afile+"'");
					break;
				}
			}
		}else{
			fail++;
		}
		
		//Verify that GetFiles get all the archive files, hidden files and normal files
		String[] archiveAndhiddenFiles = {files[4], files[5], files[7], files[8]};
		expectedAttribute = FileAttribute.instance(FileAttribute.Type.HIDDENFILE).add(FileAttribute.Type.ARCHIVEFILE);
		for(String afile:archiveAndhiddenFiles) if(!Files.SetFileProtections(afile, expectedAttribute)) fail++;
		fileList = directory+"archiveAndHiddenList.txt";
		if(Files.GetFiles(directoryToCheck, fileList, new FileAttribute(Type.HIDDENFILE).add(FileAttribute.Type.ARCHIVEFILE))){
			String contents = FileUtilities.readStringFromUTF8File(fileList);
			for(String afile:archiveAndhiddenFiles){
				if(!contents.contains(afile)){
					fail++;
					Logging.LogTestFailure(counterID+" GetFiles of type '"+expectedAttribute.toString()+"', miss return file '"+afile+"'");
					break;
				}
			}
		}else{
			fail++;
		}
		
		fileList = directory+"allFilesList.txt";
		if(Files.GetFiles(directoryToCheck, fileList, new FileAttribute(Type.ALLFILES))){
			String contents = FileUtilities.readStringFromUTF8File(fileList);
			for(String afile:files){
				if(!contents.contains(afile)){
					fail++;
					Logging.LogTestFailure(counterID+" GetFiles of type 'ALLFILES', miss return file '"+afile+"'");
					break;
				}
			}
		}else{
			fail++;
		}

		directory = utils.appendDir(directory, "New Directory");
		file = utils.appendDir(directory, "new test file3.txt");
		if(Files.CreateDirectory(directory)){
			
			if(Files.CreateFile(file, Mode.OUTPUT, Access.W, result)){
				fileNo = GetVariableValue(result);
				
				Logging.LogMessage(counterID+" '"+file+"' is created and opened with file number '"+fileNo+"' for output.");
				
				int charsToWrite = 10;
				String content = "'Files.CreateFile(file, Mode.OUTPUT, Access.W, result)'";
				if(!Files.WriteFileChars(fileNo, charsToWrite, content)) fail++;
				
				if(!Files.PrintToFile(fileNo, content, Placement.NEWLINE)) fail++;
				
				String separator = "=================================================";
				if(!Files.PrintToFile(fileNo, separator, Placement.NEWLINE)) fail++;
				
				if(!Files.PrintToFile(fileNo, content, Placement.IMMIDIATE)) fail++;
				
				if(!Files.PrintToFile(fileNo, separator, Placement.NEWLINE)) fail++;
				
				if(!Files.PrintToFile(fileNo, content, Placement.TABULATION)) fail++;
				
				if(!Files.PrintToFile(fileNo, separator, Placement.NEWLINE)) fail++;
				
				if(!Files.PrintToFile(fileNo, content)) fail++;
				
				if(!Files.CloseFile(fileNo)) fail++;
				
				if(!Files.GetFileSize(file, result)) fail++;
				
				if(!Files.GetFileDateTime(file, result)) fail++;
				
				if(!Files.GetFileDateTime(file, result, true, DateType.LASTACCESSED)) fail++;
				
				if(!Files.GetFileDateTime(file, result, true, DateType.CREATED)) fail++;
				
				if(!Files.GetFileDateTime(file, result, true, DateType.LASTMODIFIED)) fail++;
				
			}else{
				Logging.LogTestFailure(counterID+" Fail to CreateFile '"+file+"', WriteFileChars/PrintToFile/CloseFile/GetFileSize/GetFileDateTime APIs are not tested!!!");
				fail++;
			}
			
			if(Files.OpenFile(file, Mode.INPUT, Access.R, result)){
				fileNo = GetVariableValue(result);
				Logging.LogMessage(counterID+" '"+file+"' has been opened with file number '"+fileNo+"' for input.");
				int charsToRead = 15;
				if(!Files.ReadFileChars(fileNo, charsToRead, result)) fail++;
				
				charsToRead = 18;
				if(!Files.ReadFileChars(fileNo, charsToRead, result)) fail++;
				
				if(!Files.ReadFileLine(fileNo, result)) fail++;
				
				String line = null;
				while(true){
					if(Files.IsEndOfFile(fileNo, result)){
						if(Boolean.parseBoolean(GetVariableValue(result))) break;
						if(!Files.ReadFileLine(fileNo, result)) fail++;
						line = GetVariableValue(result);
						//do some thing with the line
					}else{
						fail++;
						Logging.LogTestFailure(counterID+" IsEndOfFile failed, can NOT continue!!!");
						break;
					}
				}
				
				if(!Files.CloseFile(fileNo)) fail++;
				
			}else{
				Logging.LogTestFailure(counterID+" Fail to OpenFile '"+file+"', ReadFileChars/ReadFileLine/IsEndOfFile/CloseFile APIs may not be tested!!!");
				fail++;
			}
			
			if(Files.OpenFile(file, Mode.APPEND, Access.W, result)){
				fileNo = GetVariableValue(result);
				Logging.LogMessage(counterID+" '"+file+"' has been opened with file number '"+fileNo+"' for append.");
				
				String separator = "====================  APPENDING CONTENT =============================";
				if(!Files.PrintToFile(fileNo, separator, Placement.NEWLINE)) fail++;
				
				int charsToWrite = 25;
				String content = "'Files.CreateFile(file, Mode.OUTPUT, Access.W, result)'";
				if(!Files.WriteFileChars(fileNo, charsToWrite, content)) fail++;
				
				if(!Files.CloseFile(fileNo)) fail++;
				
			}else{
				Logging.LogTestFailure(counterID+" Fail to OpenFile '"+file+"', PrintToFile/WriteFileChars/CloseFile APIs may not be tested!!!");
				fail++;
			}
			
			dest = utils.appendDir(directory, "copy of file3.txt");
			if(!Files.CopyFile(quote(file), quote(dest))) fail++;
			
			dest = utils.appendDir(directory, "second copy of file3.txt");
			String fileDriverCommand = "CopyFile";
			if(!Files.IfExistFile(file, fileDriverCommand, file, dest)) fail++;
			
			String newdest = utils.appendDir(directory, "renamed second copy of file3.txt");
			if(!Files.RenameFile(dest, newdest, true)) fail++;
			
			String regexPattern = "CreateFile\\(.*\\)";
			String replace = "FilterTextFile(file, regexPattern, replace)";
			boolean caseSensitive = true;
			Pattern pattern = caseSensitive? Pattern.compile(regexPattern):Pattern.compile(regexPattern,Pattern.CASE_INSENSITIVE);
			if(Files.FilterTextFile(file, regexPattern, replace, caseSensitive)){
				//Verify the file does NOT contain regex "CreateFile\\(.*\\)" anymore.
				if(Files.OpenFile(file, Mode.INPUT, Access.R, result)){
					fileNo = GetVariableValue(result);

					String line = null;
					while(true){
						if(Files.IsEndOfFile(fileNo, result)){
							if(Boolean.parseBoolean(GetVariableValue(result))) break;
							if(!Files.ReadFileLine(fileNo, result)) fail++;
							line = GetVariableValue(result);

							if(pattern.matcher(line).find()){
								fail++;
								Logging.LogTestFailure(counterID+" IsEndOfFile failed, can NOT continue!!!");
								break;
							}
						}else{
							fail++;
							Logging.LogTestFailure(counterID+" IsEndOfFile failed, can NOT continue!!!");
							break;
						}
					}
					
					if(!Files.CloseFile(fileNo)) fail++;
				}else{
					fail++;
				}
			}else{
				fail++;
			}
			
			String regexStart = "\\.";
			String regexStop = "\\(";
			String method = "Method";
			String countVar = method+"Count";
			if(Files.GetSubstringsInFile(file, regexStart, regexStop, method)){
				int count = Integer.parseInt(GetVariableValue(countVar));
				for(int i=0;i<count;i++){
					Logging.LogMessage(counterID+" "+method+(i+1)+"="+GetVariableValue(method+(i+1)));
				}
			}else{
				fail++;
			}
			
			file = utils.appendDir(directory, "UTF8 FILE.txt");
			if(Files.OpenUTF8File(file, Mode.OUTPUT, Access.W, result)){
				fileNo = GetVariableValue(result);
				Logging.LogMessage(counterID+" '"+file+"' is created and opened with file number '"+fileNo+"' for output UTF8 strings.");
				
				int charsToWrite = 10;
				String content = "中文字符  输入UTF8 文件 saturday sat sunday sun";
				if(!Files.WriteFileChars(fileNo, charsToWrite, content)) fail++;
				
				if(!Files.PrintToFile(fileNo, content, Placement.NEWLINE)) fail++;
				
				content = "中文字符  输入UTF8 文件 Saturday Sat Sunday Sun";
				if(!Files.PrintToFile(fileNo, content, Placement.NEWLINE)) fail++;
				
				content = "中文字符  输入UTF8 文件 SATURDAY SAT SUNDAY SUN";
				if(!Files.PrintToFile(fileNo, content, Placement.NEWLINE)) fail++;
				
				String separator = "====================  中文 =============================";
				if(!Files.PrintToFile(fileNo, separator, Placement.NEWLINE)) fail++;
				
				if(!Files.CloseFile(fileNo)) fail++;
				
			}else{
				Logging.LogTestFailure(counterID+" Fail to OpenFile '"+file+"', WriteFileChars/PrintToFile/CloseFile APIs may not be tested!!!");
				fail++;
			}
			
			newdest = utils.appendDir(directory, "Copy of UTF8 FILE.txt");
			if(Files.CopyFile(quote(file), quote(newdest))){
				regexPattern = "saturday|sat|sunday|sun";
				replace = "weekend";
				
				caseSensitive = false;//true
				pattern = caseSensitive? Pattern.compile(regexPattern):Pattern.compile(regexPattern,Pattern.CASE_INSENSITIVE);
				if(Files.FilterTextFile(file, regexPattern, replace, false)){
					//Verify the file does NOT contain regex "saturday|sat|sunday|sun" case-insensitively anymore.
					if(Files.OpenFile(file, Mode.INPUT, Access.R, result)){
						fileNo = GetVariableValue(result);

						String line = null;
						while(true){
							if(Files.IsEndOfFile(fileNo, result)){
								if(Boolean.parseBoolean(GetVariableValue(result))) break;
								if(!Files.ReadFileLine(fileNo, result)) fail++;
								line = GetVariableValue(result);

								if(pattern.matcher(line).find()){
									fail++;
									Logging.LogTestFailure(counterID+" IsEndOfFile failed, can NOT continue!!!");
									break;
								}
							}else{
								fail++;
								Logging.LogTestFailure(counterID+" IsEndOfFile failed, can NOT continue!!!");
								break;
							}
						}
						
						if(!Files.CloseFile(fileNo)) fail++;
					}
				}else{
					fail++;
				}
				

				
			}else{
				fail++;
			}
			
			String toDirectory = utils.appendDir(baseDirecotory, "Copy Directory");
			if(Files.CreateDirectory(toDirectory)){
				if(!Files.CopyMatchingFiles(directory, toDirectory, "[a-z ]*d.*", PatternFilterMode.REGEXP)) fail++;
				if(!Files.CopyMatchingFiles(directory, toDirectory, "UTF*.*", PatternFilterMode.WILDCARD)) fail++;
			}else{
				fail++;
			}
			
			String newdir = utils.appendDir(directory, "NewDirectory");
			fileDriverCommand = "CreateDirectory";
			if(!Files.IfExistDir(directory, fileDriverCommand, newdir)) fail++;
			
		}else{
			Logging.LogTestFailure(counterID+" Fail to CreateDirctory '"+directory+"', A LOF OF APIs not tested!!!");
			fail++;
		}
		
		String image = utils.testAssetFile("keyword.png");
		if(!Files.GetTextFromImage(image, result, OCREngine.OCR_T_ENGINE_KEY, Locale.ENGLISH.getLanguage(), "3.0")) fail++;
		if(!Files.GetTextFromImage(image, result, OCREngine.OCR_G_ENGINE_KEY, Locale.ENGLISH.getLanguage(), "2.0")) fail++;
		
		String textFile = utils.appendDir(baseDirecotory, "image_T.txt");
		if(!Files.SaveTextFromImage(image, textFile, OCREngine.OCR_T_ENGINE_KEY, Locale.ENGLISH.getLanguage(), "2.0")) fail++;
		textFile = utils.appendDir(baseDirecotory, "image_G.txt");
		if(!Files.SaveTextFromImage(image, textFile, OCREngine.OCR_G_ENGINE_KEY, Locale.ENGLISH.getLanguage(), "2.5")) fail++;
		
		String coords = "0,0,70%,50%";
		String subimage = utils.appendDir(baseDirecotory, "subimage.png");
		if(!Files.FilterImage(image, subimage, coords)) fail++;	
		subimage = utils.appendDir(baseDirecotory, "subimage2.png");
		//filter 2 areas
		coords = "0,0,70%,50% 80%;80%;100%;100%";
		if(!Files.FilterImage(image, subimage, coords)) fail++;
		//filter multiple areas
		subimage = utils.appendDir(baseDirecotory, "subimage3.png");
		List<SubArea> subareas = new ArrayList<SubArea>();
		subareas.add(new SubArea(0,0,"20%","30%"));
		subareas.add(new SubArea("50%", "50%", "60%", "70%"));
		subareas.add(new SubArea("80%", "80%", "90%", "90%"));
		if(!Files.FilterImage(image, subimage, subareas)) fail++;
	
		file = utils.projectFile("test.ini");
		String section = null;
		String item = null;
		section = "SAFS_TEST";
		item = "TestName";
		if(!Files.GetINIFileValue(file, section, item, result)) fail++;
		
		section = "SAFS_DIRECTORIES";
		item = "TESTDIR";
		if(!Files.GetINIFileValue(file, section, item, result)) fail++;
		
		if(cleanAll){
			//Test to delete non-empty folder
			if(Files.DeleteDirectory(directory)){//"New Directory"
				Logging.LogTestFailure(counterID+" directory '"+directory+"' is not empty, should NOT be deleted");
				fail++;
			}else{
				Logging.LogMessage(counterID+" Expected result: directory '"+directory+"' is not empty, cannot be deleted.");
			}
			
			if(!Files.DeleteDirectoryContents(directory)) fail++;
			
			if(!Files.DeleteDirectory(directory)) fail++;

			//Test to delect an empty folder
			directory = utils.appendDir(baseDirecotory, "Empty Folder");
			if(Files.CreateDirectory(directory)){
				if(!Files.DeleteDirectory(directory)) fail++;
			}else{
				fail++;
			}
			
			//Delete the whole test folder
			if(!Files.DeleteDirectoryContents(baseDirecotory, true)) fail++;
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
	 * @param cleanAll boolean, if user wants to keep the generated files/directories, then provide false.
	 *                          if true, program will delete all the generated files/directories.
	 * @return
	 * @throws Throwable
	 */
	public static int runRegressionTest(EmbeddedHookDriverRunner Runner, boolean cleanAll) throws Throwable{
		int fail = 0;
		Counters.StartCounter(COUNTER);

		try{
			fail += testAPI(COUNTER, cleanAll);
			
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
		initUtils();
		runRegressionTest(Runner, false);//the generated files/directories will be kept
//		runRegressionTest(Runner, true);//the generated files/directories will be deleted
	}

}