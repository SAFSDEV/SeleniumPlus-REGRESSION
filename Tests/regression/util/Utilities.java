/** 
 ** Copyright (C) SAS Institute, All rights reserved.
 ** General Public License: http://www.opensource.org/licenses/gpl-license.php
 **/
package regression.util;

import java.io.File;

import org.safs.StringUtils;
import org.safs.tools.CaseInsensitiveFile;
import org.safs.tools.drivers.JSAFSDriver;

public class Utilities {
	protected static final String TEST_ASSET = "TestAsset";
	JSAFSDriver jsafs;
	
	public Utilities(JSAFSDriver jsafs) throws Exception{
		if(jsafs==null) throw new Exception(StringUtils.debugmsg(false)+"Fail new instance.");
		this.jsafs = jsafs;
	}
	
	public String logFile(String filename){ return jsafs.getLogsDir()+File.separator+filename; }
	public String testFile(String filename){ return jsafs.getTestDir()+File.separator+filename; }
	public String benchFile(String filename){ return jsafs.getBenchDir()+File.separator+filename; }
	public String projectFile(String filename){ return jsafs.getProjectRootDir()+File.separator+filename; }
	public String diffFile(String filename){ return jsafs.getDifDir()+File.separator+filename; }
	public String datapoolFile(String filename){ return jsafs.getDatapoolDir()+File.separator+filename; }
	public String mapFile(String filename){ return jsafs.getDatapoolDir()+File.separator+filename; }
	public String testAssetFile(String filename){ return jsafs.getProjectRootDir()+File.separator+TEST_ASSET+File.separator+filename; }
	
	public String projectDir(){ return jsafs.getProjectRootDir()+File.separator; }
	public String testDir(){ return jsafs.getTestDir()+File.separator; }
	public String benchDir(){ return jsafs.getBenchDir()+File.separator; }
	public String diffDir(){ return jsafs.getDifDir()+File.separator; }
	public String datapoolDir(){ return jsafs.getDatapoolDir()+File.separator; }
	public String mapsDir(){ return jsafs.getDatapoolDir()+File.separator; }
	public String logsDir(){ return jsafs.getLogsDir()+File.separator; }
	public String testAssetDir(){ return jsafs.getProjectRootDir()+File.separator+TEST_ASSET+File.separator; }
	
	public File getLogsDir(){
		File logsdir = null;
		try{ logsdir = new CaseInsensitiveFile(jsafs.getLogsDir()).toFile();}
		catch(Throwable t){
			System.err.println(StringUtils.debugmsg(false)+" Failed. Met "+StringUtils.debugmsg(t));
		}
		return logsdir;
	}
	
	public static String appendDir(String parentDir, String relativeName){
		return appendDir(parentDir, relativeName, false);
	}
	public static String appendDir(String parentDir, String relativeName, boolean withEndingSeparator){
		if(parentDir==null) return null;
		if(relativeName==null) return parentDir;
		
		String directory = null;
		if(parentDir.endsWith(File.separator)) directory = parentDir+relativeName;
		else directory = parentDir+File.separator+relativeName;
		
		if(withEndingSeparator) directory += File.separator;
		
		return directory;
	}
	
	/**
	 * This method will combine a locale suffix with a map file to form a NLS map file name.<br>
	 * For example, the map file is application.map, the locale suffix is _en, the the NLS map file will be application_en.map<br>
	 * @param mapFile		String, the map file name
	 * @param localeSuffix	String, the locale suffix. Such as _zh, _en etc.
	 * @return String, the NLS map file.<br> 
	 *                 null if the map file is given as null;<br>
	 *                 map file itself if the locale suffix is null.<br> 
	 */
	public static String nlsMap(String mapFile, String localeSuffix){
		if(mapFile==null || localeSuffix==null) return mapFile;
		
		int index = mapFile.indexOf(".");
		
		if(index<0) return mapFile;
		
		String nlsMapFile = mapFile.substring(0, index)+localeSuffix+mapFile.substring(index);
		
		return nlsMapFile;
	}
}
