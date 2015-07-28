package regression.testcases;

import org.safs.StringUtils;
import org.safs.selenium.webdriver.SeleniumPlus;

public class StringsTests extends SeleniumPlus{
	
	protected static String COUNTER = StringUtils.getClassName(0, false);
	
	protected static boolean verify(String expectedResult, String variable){
		try{
			String result = SeleniumPlus.GetVariableValue(variable);
			if(expectedResult.equals(result)) return true;
			else{
				Logging.LogTestFailure(COUNTER +" '"+expectedResult+"' does NOT equal to '"+result+"'");
				return false;
			}
		}catch(Throwable t){
			Logging.LogTestFailure(COUNTER +" fatal error due to "+t.getClass().getName()+", "+ t.getMessage());
			return false;
		}
	}

	protected static int testAPI() throws Throwable{
		boolean expression = Misc.isExpressionsOn();
		if(expression){
			if(!Misc.Expressions(false)){
				Logging.LogTestWarning(COUNTER+" Fail to turn off Expression! Some SeleniumPlus Strings API may fail!");
			}
		}
		
		int fail = 0;

		String result = "result";
		String controlA = "\u0001";
		String content = "  ab"+controlA+"cd\n";
		String expectedResult = "  ab cd ";
		if(!(Strings.CleanString(content, result) && verify(expectedResult, result))) fail++;

		String source = "hello";
		String dest = " hello";
		expectedResult = "false";
		if(!(Strings.Compare(source, dest, result) && verify(expectedResult, result))) fail++;

		String str1 = "hello ";
		String str2 = " world";
		expectedResult = "hello  world";
		if(!(Strings.Concatenate(str1, str2, result) && verify(expectedResult, result))) fail++;

		source = "a|bc|d";
		int index = 1;
		String delimiter = "|";
		expectedResult = "bc";
		if(!(Strings.GetField(source, index, delimiter, result) && verify(expectedResult, result))) fail++;

		source = "a|bc|d";
		index = 0;
		delimiter = "|";
		expectedResult = "3";
		if(!(Strings.GetFieldCount(source, index, delimiter, result) && verify(expectedResult, result))) fail++;

		source = "abcdef";
		int fieldID = 1;
		int fixedWidth = 2;
		expectedResult = "cd";
		if(!(Strings.GetFixedWidthField(source, fieldID, fixedWidth, result) && verify(expectedResult, result))) fail++;

		source = "a->b->c->d->e->f";
		fieldID = 2;
		int startIndex = 5;
		String delimiters = "->";
		expectedResult = "c";
		if(!(Strings.GetMultiDelimitedField(source, fieldID, startIndex, delimiters, result) && verify(expectedResult, result))) fail++;

		source = "a->b->c->d->e->f";
		startIndex = 1;
		delimiters = "->";
		expectedResult = "6";
		if(!(Strings.GetMultiDelimitedFieldCount(source, startIndex, delimiters, result) && verify(expectedResult, result))) fail++;

		source = "a/|/";
		startIndex = 0;
		delimiters = "/|";
		expectedResult = "1";
		if(!(Strings.GetNextDelimiterIndex(source, startIndex, delimiters, result) && verify(expectedResult, result))) fail++;

		source = "a|b&cd-efghi";
		index = 3;
		delimiters = "[\\|&\\-g]";
		expectedResult = "cd";
		if(!(Strings.GetREDelimitedField(source, index, delimiters, result) && verify(expectedResult, result))) fail++;


		source = "a|b&cd-efghi";
		startIndex = 0;
		delimiters = "[\\|&\\-g]";
		expectedResult = "5";
		if(!(Strings.GetREDelimitedFieldCount(source, startIndex, delimiters, result) && verify(expectedResult, result))) fail++;

		source = "XaaaB";
		String start = "X";
		String end = "B";
		expectedResult = "aaa";
		if(!(Strings.GetSubstringsInString(source, start, end, result) && verify(expectedResult, result))) fail++;

		String systemVariable = "OS";
		if(!(Strings.GetSystemEnviron(systemVariable, result))) fail++;

		if(!(Strings.GetSystemUser(result))) fail++;

		source = "a|bc|d";
		index = 1;
		delimiters = "|";
		expectedResult = "bc";
		if(!(Strings.GetTrimmedField(source, index, delimiters, result) && verify(expectedResult, result))) fail++;

		source = "abc";
		index = 0;
		String findString = "bc";
		expectedResult = "1";
		if(!(Strings.Index(index, source, findString, result) && verify(expectedResult, result))) fail++;

		source = "abc";
		int length = 1;
		expectedResult = "a";
		if(!(Strings.Left(source, length, result) && verify(expectedResult, result))) fail++;

		source = "   abc   ";
		expectedResult = "abc   ";
		if(!(Strings.LeftTrim(source, result) && verify(expectedResult, result))) fail++;

		source = "abc";
		expectedResult = "3";
		if(!(Strings.Length(source, result) && verify(expectedResult, result))) fail++;

		source = "abc";
		String find = "bc";
		String replace = "123";
		expectedResult = "a123";
		if(!(Strings.Replace(source, find, replace, result) && verify(expectedResult, result))) fail++;

		source = "abc";
		length = 2;
		expectedResult = "bc";
		if(!(Strings.Right(source, length, result) && verify(expectedResult, result))) fail++;

		source = "   abc   ";
		expectedResult = "   abc";
		if(!(Strings.RightTrim(source, result) && verify(expectedResult, result))) fail++;

		source = "abc";
		int offset = 1;
		length = 1;
		expectedResult = "b";
		if(!(Strings.SubString(source, offset, length, result) && verify(expectedResult, result))) fail++;

		source = "ABC";
		expectedResult = "abc";
		if(!(Strings.ToLowerCase(source, result) && verify(expectedResult, result))) fail++;

		source = "abc";
		expectedResult = "ABC";
		if(!(Strings.ToUpperCase(source, result) && verify(expectedResult, result))) fail++;

		source = "   abc \t  ";
		expectedResult = "abc";
		if(!(Strings.Trim(source, result) && verify(expectedResult, result))) fail++;

		Misc.Expressions(expression);
		return fail;
	}

	public static int runRegressionTest() throws Throwable{
		int fail = 0;
		Counters.StartCounter(COUNTER);

		try{
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
		runRegressionTest();	
	}

}