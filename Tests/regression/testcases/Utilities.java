/** 
 ** Copyright (C) SAS Institute, All rights reserved.
 ** General Public License: http://www.opensource.org/licenses/gpl-license.php
 **/
package regression.testcases;

import java.io.File;

import org.safs.StringUtils;
import org.safs.tools.drivers.JSAFSDriver;

public class Utilities {
	protected static final String TEST_ASSET = "TestAsset";
	JSAFSDriver jsafs;
	
	public Utilities(JSAFSDriver jsafs) throws Exception{
		if(jsafs==null) throw new Exception(StringUtils.debugmsg(false)+"Fail new instance.");
		this.jsafs = jsafs;
	}
	
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
	public String testAssetDir(){ return jsafs.getProjectRootDir()+File.separator+TEST_ASSET+File.separator; }
	
	public String appendDir(String parentDir, String relativeName){
		return appendDir(parentDir, relativeName, false);
	}
	public String appendDir(String parentDir, String relativeName, boolean withEndingSeparator){
		if(parentDir==null) return null;
		if(relativeName==null) return parentDir;
		
		String directory = null;
		if(parentDir.endsWith(File.separator)) directory = parentDir+relativeName;
		else directory = parentDir+File.separator+relativeName;
		
		if(withEndingSeparator) directory += File.separator;
		
		return directory;
	}
}
