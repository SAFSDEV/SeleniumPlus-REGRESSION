# SeleniumPlus Regression

This project is used for SeleniumPlus Regression testing. It gives the basic usage of SeleniumPlus and contains script ```runAutomation.bat``` to run whole Regression in Windows environment.
* ```Tests.regression``` folder contains all the concrete test cases.
* ```Logs``` folder contains the log information file and generated Summary/Failure Report, which is convenient to read.
* ```runAutomation.bat``` script is used for running whole project without Eclipse. If you're using Jenkins to integrate your testing, this script is good example to show how to organize your jobs in Windows side. Then, you can use Jenkins to call this script to automatize your testing work.

## Test case specifications in Regression

In order to generate detailed Regression summary report, there're some specifications need to be followed when creating new test case. In every test case, we should use the ```Counters``` to indicate testing case boundary. (More details about ```Counters``` can be found in ```SelniumPlus.java``` in [Core](https://github.com/SAFSDEV/Core) project.) It'll give testing case clear structure and accurate testing positions, which is convenient for developers to debug.

### 0. Testing method template in Regression testcase 
All the specifications below are integrated in the SeleniumPlus option in Ecipse. You can use this template through steps:
* Click the right button of mouse in the java source code area in Eclipse.
* Choose the 'Selenium+' option.
* Choose the 'Insert Regression Testing Method' option.
Then, the testing method template will be inserted into your source code.

#### 1. Specification in 'runRegressionTest()' method

In this Regression project, every test case in ```Tests.regression.testcases``` contains one ```runRegressionTest()``` method, which is called by ```Tests.regression.testruns/Regression.java``` to run whole Regression testing. So, the meat of every test case is in the ```runRegressionTest()``` method. 

In this method, we should use one ```Counters``` with the name of test case to indicate the boundary of this test case. For example, in ```AXXXZ``` test case, we should define ```Counters``` like this:

~~~~
public class AXXXZTests extends Regression{
    public static final String COUNTER = StringUtils.getClassName(0, false);

    public static int runRegressionTest(XXXX) throws Throwable{ 
        int fail = 0;
        Counters.StartCounter(COUNTER); 

        ... ... ... ... ... ...

        Counters.StopCounter(COUNTER);
        Counters.StoreCounterInfo(COUNTER, COUNTER);
        Counters.LogCounterInfo(COUNTER); 
        
        return fail;
    }
}
~~~~


#### 2. Specification in concrete testing method

In order to show the structure of testing methods, we should use the ```Counters``` with the name of '*previousCounterName.methodName*' to indicate specific testing method position in test case. For example, if the ```runRegressionTest()``` method calls ```testAPI()``` method, the ```Counters``` definition in ```testAPI()``` should be like this:

~~~~
public class AXXXZTests extends Regression{
    public static final String COUNTER = StringUtils.getClassName(0, false);
    
    private static int testAPI(String counterPrefix) throws Throwable{ 
        String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
        int fail = 0;
        Counters.StartCounter(counterID);

        ... ... ... 

        Counters.StopCounter(counterID);
        Counters.StoreCounterInfo(counterID, counterID);
        Counters.LogCounterInfo(counterID);

        return fail;
    }

    public static int runRegressionTest(XXXX) throws Throwable{ 
        int fail = 0;
        Counters.StartCounter(COUNTER); 
        
        fail += testAPI(COUNTER);

        ... ... ... ... ... ...

        Counters.StopCounter(COUNTER);
        Counters.StoreCounterInfo(COUNTER, COUNTER);
        Counters.LogCounterInfo(COUNTER); 
        
        return fail;
    }
}
~~~~

Further more, if we have deeper nested testing method calling, we should keep this kind of ```Counters``` structure to indicate testing position. To be clear, let's say if the ```testAPI()``` method calls ```testAPIForSAP()``` method, the ```Counters``` definition in ```testAPIForSAP()``` method should be like this:

~~~~
public class AXXXZTests extends Regression{
    public static final String COUNTER = StringUtils.getClassName(0, false);
    
    private static int testAPIForSAP(String browser, String counterPrefix) throws Throwable{   
        String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
        int fail = 0;
        Counters.StartCounter(counterID);

        ... ... ... 

        Counters.StopCounter(counterID);
        Counters.StoreCounterInfo(counterID, counterID);
        Counters.LogCounterInfo(counterID);

        return fail;
    }

    
    private static int testAPI(String counterPrefix) throws Throwable{ 
        String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
        int fail = 0;
        Counters.StartCounter(counterID);

        fail += testAPIForSAP(browser, counterID);
        ... ... ... 

        Counters.StopCounter(counterID);
        Counters.StoreCounterInfo(counterID, counterID);
        Counters.LogCounterInfo(counterID);

        return fail;
    }

    public static int runRegressionTest(XXXX) throws Throwable{ 
        int fail = 0;
        Counters.StartCounter(COUNTER); 
        
        fail += testAPI(COUNTER);

        ... ... ... ... ... ...

        Counters.StopCounter(COUNTER);
        Counters.StoreCounterInfo(COUNTER, COUNTER);
        Counters.LogCounterInfo(COUNTER); 
        
        return fail;
    }
}
~~~~

#### 3. Specification of using 'Counters.LogCounterInfo()'
For every concrete testing method, we have ```Counters``` structure ```Counters.StartCounter()``` and ```Counters.LogCounterInfo()```. And, before we return the ```fail```, which is the number of failures in one testing method, we **should** log the failures information. And this logging behavior should just be following the ```Counters.LogCounterInfo()``` but before ```return fail;``` like this:
~~~~
private static int testXXXAPI(String counterPrefix) throws Throwable{ 
    String counterID = Regression.generateCounterID(counterPrefix, StringUtils.getMethodName(0, false));
    int fail = 0;
    Counters.StartCounter(counterID);

    ... ... ... 

    Counters.StopCounter(counterID);
    Counters.StoreCounterInfo(counterID, counterID);
    Counters.LogCounterInfo(counterID);

//  booleanCounter = Counters.GetCounterStatus(counterID);
//	if(fail == 0){
//		if(booleanCounter.getTestFailures() != 2 ){
//			Logging.LogTestFailure(counterID + " expected Test Failure count of 2, but got " + equalsCounter.getTestFailures());
//			fail++;
//		}
//
//		if(booleanCounter.getTestPasses() != 3 ){
//			Logging.LogTestFailure(counterID + " expected Test Passes count of 3, but got " + equalsCounter.getTestPasses());
//			fail++;
//		}
//	}

    if(fail > 0){
		Logging.LogTestFailure(counterID + " " + fail + " UNEXPECTED test failures!");
	}else{
		Logging.LogTestSuccess(counterID + " did not report any UNEXPECTED test failures!");
	}

    return fail;
}
~~~~
One reason that we call ```Counters.LogCounterInfo()``` before logging failure is: sometimes the ```Counters``` will be used before logging, just like the above comments shows. And also, the consistent order of ```Counters.LogCounterInfo()``` and *logging failure information* will make the Summary Report abstraction easier.


