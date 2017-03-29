package regression.testcases;

import org.safs.StringUtils;
import org.safs.selenium.webdriver.SeleniumPlus;
import org.safs.tools.counters.CountStatusInterface;

import regression.testruns.Regression;

/**
 * <pre>
 * 	 java -cp %CLASSPATH% regression.testcases.AssertTests
 * </pre>
 * @see org.safs.selenium.webdriver.SeleniumPlus#main(java.lang.String[])
 */
public class AssertTests extends SeleniumPlus {
	public static final String COUNTER = StringUtils.getClassName(0, false);

	static CountStatusInterface equalsCounter = null;
	static CountStatusInterface notEqualCounter = null;
	static CountStatusInterface objectCounter = null;
	static CountStatusInterface booleanCounter = null;

	/**
	 * @return the number of UNEXPECTED failures encountered.
	 */
	public static int NotEqualTests(String counterPrefix){
		final String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		int fail = 0;
		Counters.StartCounter(counterID);

		if(! Assert.NotNull("String Object")){ fail++;                                 // test_passes
        Logging.LogTestFailure("'String Object' should have passed Asset.NotNull test.");}

		if(! Assert.NotEqual((int)4, (int)5)){ fail++;                              // test_passes
          Logging.LogTestFailure("int '4' should have compared as Not Equal to int '5'.");}

		if(! Assert.NotEqual((long)5, (long)6)){ fail++;                            // test_passes
          Logging.LogTestFailure("long '5' should have compared as Not Equal to long '6'.");}

		if(! Assert.NotEqual((double)8, (double)7)){ fail++;                        // test_passes
          Logging.LogTestFailure("double '8' should have compared as Not Equal to double '7'.");}

		if(! Assert.NotEqual((float)9, (float)8)){ fail++;                          // test_passes
          Logging.LogTestFailure("float '9' should have compared as Not Equal to float '8'.");}

		Logging.LogTestWarning("Expecting 5 Failure to follow...");                 // test_warnings

		if(Assert.NotEqual((double)8, (double)7, 2)){ fail++;                       // test_failures
          Logging.LogTestFailure("double '8' should have failed NotEqual to double '7' with range '2'.");}

		if(Assert.NotEqual((int)4, (int)4)){ fail++;                                // test_failures
          Logging.LogTestFailure("int '4' should have failed NotEqual to int '4'.");}

		if(Assert.NotEqual((long)5, (long)5)){ fail++;                              // test_failures
          Logging.LogTestFailure("long '5' should have failed NotEqual to long '5'.");}

		if(Assert.NotEqual((double)8, (double)8)){ fail++;                          // test_failures
          Logging.LogTestFailure("double '8' should have failed NotEqual to double '8'.");}

		if(Assert.NotEqual((float)9, (float)9)){ fail++;                            // test_failures
          Logging.LogTestFailure("float '9' should have failed NotEqual to float '9'.");}



		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);
		notEqualCounter = Counters.GetCounterStatus(counterID);

		if(fail == 0){
			if(notEqualCounter.getTestFailures() != 5 ){
				Logging.LogTestFailure(counterID + " expected Test Failure count of 5, but got "+ equalsCounter.getTestFailures());
				fail++;
			}
			if(notEqualCounter.getTestPasses() != 5 ){
				Logging.LogTestFailure(counterID + " expected Test Passes count of 5, but got "+ equalsCounter.getTestPasses());
				fail++;
			}
		}
		if(fail > 0){
			Logging.LogTestFailure(counterID + " reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(counterID + " did not report any UNEXPECTED test failures!");
		}
		return fail;
	}

	/**
	 * @return the number of UNEXPECTED failures encountered.
	 */
	public static int EqualsTests(String counterPrefix){
		final String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		int fail = 0;

		Counters.StartCounter(counterID);

		String expected = "String Object";

		if(! Assert.Equals("String Object", expected)){ fail++;                    // test_passes
		 Logging.LogTestFailure("'String Object' should have compared as Equal to '"+ expected +"'.");}

		if(! Assert.Equals(null, null)){ fail++;                                   // test_passes
	     Logging.LogTestFailure("'null' should have compared as Equal to 'null'.");}

		if(! Assert.Null(null)) { fail++;                                          // test_passes
         Logging.LogTestFailure("'null' should have asserted true for Assert.Null.");}

		if(! Assert.Equals(expected, expected)){ fail++;                           // test_passes
   	     Logging.LogTestFailure("'"+expected +"' should compared as Equal for '"+expected +"'.");}

		if(! Assert.Equals((double)5, (double)5)){ fail++;                          // test_passes
   	     Logging.LogTestFailure("double '5' should have compared as Equal to double '5'.");}

		if(! Assert.Equals((int)5, (int)5)){ fail++;                                // test_passes
         Logging.LogTestFailure("int '5' should have compared as Equal to int '5'.");}

		if(! Assert.Equals((float)5, (float)5, 0)){ fail++;                         // test_passes
          Logging.LogTestFailure("float '5' should have compared as Equal to float '5'.");}

		if(! Assert.Equals((long)4, (long)4, 0)){ fail++;                           // test_passes
          Logging.LogTestFailure("long '4' should have compared as Equal to long '4'.");}

		if(! Assert.Equals((int)3, (int)3, 0)){ fail++;                             // test_passes
          Logging.LogTestFailure("int '3' should have compared as Equal to int '3'.");}

		if(! Assert.Equals((double)2, (double)2, 0)){ fail++;                       // test_passes
          Logging.LogTestFailure("double '2' should have compared as Equal to double '2'.");}

		if(! Assert.Equals(-4, -4)){ fail++;                                        // test_passes
          Logging.LogTestFailure("'-4' should have compared as Equal to '-4'.");}

		if(! Assert.Equals(5, 6, 1)){ fail++;                                       // test_passes
          Logging.LogTestFailure("'5' should have compared as Equal to '6' within range '1'.");}

		Logging.LogTestWarning("Expecting Failure to follow: 4 does not equal 6");  // test_warnings

		if(Assert.Equals(4, 6)){ fail++;                                            // test_failures
          Logging.LogTestFailure("'4' should NOT compare as Equal to '6'.");}

		Logging.LogTestWarning("Expecting Failure to follow: 5 does not equal 7");   // test_warnings

		if(Assert.Equals(5, 7, 1)){ fail++;                                         // test_failures
           Logging.LogTestFailure("'5' should NOT compare as Equal to '7' within range '1'.");}

		Logging.LogTestWarning("Expecting Failure to follow: -4 does not equal 4");  // test_warnings

		if(Assert.Equals(-4, 4)){ fail++;                                           // test_failures
          Logging.LogTestFailure("'-4' should NOT compare as Equal to '4'.");}

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);
		equalsCounter = Counters.GetCounterStatus(counterID);

		if(fail == 0){
			if(equalsCounter.getTestFailures() != 3 ){
				Logging.LogTestFailure(counterID + " expected Test Failure count of 3, but got "+ equalsCounter.getTestFailures());
				fail++;
			}
			if(equalsCounter.getTestPasses() != 12 ){
				Logging.LogTestFailure(counterID + " expected Test Passes count of 12, but got "+ equalsCounter.getTestPasses());
				fail++;
			}
		}
		if(fail > 0){
			Logging.LogTestFailure(counterID + " reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(counterID + " did not report any UNEXPECTED test failures!");
		}
		return fail;
	}

	/**
	 * @return the number of UNEXPECTED failures encountered.
	 */
	public static int ObjectTests(String counterPrefix) throws Throwable{
		final String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		int fail = 0;

		Counters.StartCounter(counterID);

		String expected = "String Object";

		if(! Assert.Same("String Object", expected)){fail++; //Java says this actual == expected !                // test_passes
          Logging.LogTestFailure("'String Object' should compare the Same as Object['String Object'].");}

		if(! Assert.Same(expected, expected)){fail++;                                                             // test_passes
          Logging.LogTestFailure("'expected' should compare the Same as 'expected'.");}

		if(! Assert.NotSame(new Object(), expected)){fail++;                                                      // test_passes
          Logging.LogTestFailure("'object' should not compare the Same as 'expected'.");}

		if(! Assert.NotSame(new Object(), new Object())){fail++;                                                  // test_passes
          Logging.LogTestFailure("'new Object()' should not compare the Same as 'new Object()'.");}

		Logging.LogTestWarning("Expecting Failure: new Object() should not compare the Same as new Object()!");   // test_warnings

		if(Assert.Same(new Object(), new Object())){fail++;                                                       // test_failures
          Logging.LogTestFailure("'new Object()' should not compare the Same as 'new Object()'.");}

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);
		objectCounter = Counters.GetCounterStatus(counterID);

		if(fail == 0){
			if(objectCounter.getTestFailures() != 1 ){
				Logging.LogTestFailure(counterID + " expected Test Failure count of 1, but got "+ equalsCounter.getTestFailures());
				fail++;
			}
			if(objectCounter.getTestPasses() != 4 ){
				Logging.LogTestFailure(counterID + " expected Test Passes count of 4, but got "+ equalsCounter.getTestPasses());
				fail++;
			}
		}
		if(fail > 0){
			Logging.LogTestFailure(counterID + " reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(counterID + " did not report any UNEXPECTED test failures!");
		}
		return fail;
	}

	/**
	 * @return the number of UNEXPECTED failures encountered.
	 */
	public static int BooleanTests(String counterPrefix) throws Throwable{
		final String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
		int fail = 0;

		Counters.StartCounter(counterID);

		String expected = "String Object";

		if(! Assert.True(expected.equals(expected))){ fail++;                                                // test_passes
          Logging.LogTestFailure("'expected.equals(expected);' should have asserted 'true'.");}

		if(! Assert.True("String Object" == expected)){ fail++;                                             // test_passes
          Logging.LogTestFailure("'expected==expected' should have asserted 'true'.");}

		if(! Assert.False(!expected.equals(expected))){fail++;                                               // test passes
          Logging.LogTestFailure("'!expected.equals(expected);' should have successfuly asserted 'false'.");}

		Logging.LogTestWarning("Expecting Failure: 'expected.equals(expected) should NOT Assert False!");   // test_warnings

		if(Assert.False(expected.equals(expected))){fail++;                                                 // test failures
          Logging.LogTestFailure("'expected.equals(expected);' should NOT have successfuly asserted 'false'.");}

		Logging.LogTestWarning("Expecting Failure: 'expected != expected should NOT Assert True!");         // test_warnings

		if(Assert.True(expected != "String Object")){ fail++;                                               // test_failures
          Logging.LogTestFailure("'expected!=expected' should NOT Assert True.");}

		Counters.StopCounter(counterID);
		Counters.StoreCounterInfo(counterID, counterID);
		Counters.LogCounterInfo(counterID);
		booleanCounter = Counters.GetCounterStatus(counterID);

		if(fail == 0){
			if(booleanCounter.getTestFailures() != 2 ){
				Logging.LogTestFailure(counterID + " expected Test Failure count of 2, but got "+ equalsCounter.getTestFailures());
				fail++;
			}
			if(booleanCounter.getTestPasses() != 3 ){
				Logging.LogTestFailure(counterID + " expected Test Passes count of 3, but got "+ equalsCounter.getTestPasses());
				fail++;
			}
		}
		if(fail > 0){
			Logging.LogTestFailure(counterID + " reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(counterID + " did not report any UNEXPECTED test failures!");
		}
		return fail;
	}

	/**
	 * Don't run this unless you intend to have the test abort from this call.
	 * @return shouldn't
	 * @throws Throwable
	 */
	public static boolean AbortOnFailureTest() throws Throwable{
		final String ABORT = "AssertAbort";

		Counters.StartCounter(ABORT);

		Assert.setAbortOnFailure(true);
		Logging.SetLogAllInfoMode();
		Logging.LogTestWarning("Expecting Failure And Abort to follow: 5 does not equal 7");
		Assert.Equals(5, 7);  // expected failure ABORT
		Logging.LogTestFailure("Abort On Failure should NOT have allowed this message to be logged!");
		return false;
	}

	/**
	 * Run all Assert regression tests EXCEPT the AbortOnFailureTest.
	 * @return the total number of UNEXPECTED failures detected.
	 * @throws Throwable
	 */
	public static int runRegressionTest() throws Throwable{
		Counters.StartCounter(COUNTER);
		int fail = 0;
	    fail += EqualsTests(COUNTER);
	    fail += NotEqualTests(COUNTER);
	    fail += ObjectTests(COUNTER);
	    fail += BooleanTests(COUNTER);

		Counters.StopCounter(COUNTER);
		Counters.StoreCounterInfo(COUNTER, COUNTER);
		Counters.LogCounterInfo(COUNTER);

		if(fail > 0){
			Logging.LogTestFailure(COUNTER + " reports "+ fail +" UNEXPECTED test failures!");
		}else{
			Logging.LogTestSuccess(COUNTER + " did not report any UNEXPECTED test failures!");
		}

		return fail;
	}

	/**
	 * Simply runs the Regression Test.
	 * @see #runRegressionTest()
	 * @see org.safs.selenium.webdriver.SeleniumPlus#main(java.lang.String[])
	 */
	@Override
	public void runTest() throws Throwable {
		runRegressionTest();
	}
}
