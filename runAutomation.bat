@echo off
setlocal enableDelayedExpansion

REM
REM      Name    : runAutomation.BAT
REM                 Use this batch file to run whole Regression tests.
REM
REM
REM      Syntax  : runAutomation.bat [/browser BrowserNames] [/keepserver]
REM                 '/browser'       If specified, the Regression test will use it as browser engine. 
REM                                  E.g. '/browser IE' or '/browser "IE Chrome"'.
REM
REM                 '/keepserver'    If specified, after running, the Remote server will be kept.
REM
REM      Example :
REM                 runAutomation.bat
REM                 runAutomation.bat /browser IE
REM                 runAutomation.bat /keepserver
REM                 runAutomation.bat /browser "IE Chrome"
REM                 runAutomation.bat /browser "IE Chrome" /keepserver
REM


REM Initialize environment variables
Set BrowserName=
Set KeepRemoteServer=FALSE
REM

REM
:ParseArguments
If NOT [%1]==[] (
	If [%1]==[/browser] GOTO SetBrowserName
	If [%1]==[/keepserver] GOTO SETKeepServer
)
GOTO Begin
REM

REM
:: Set browser name of Regression testing.
:SetBrowserName
If [%2]==[] (
	Echo "No browser name value."
	Set ERRORLEVEL=1
	GOTO END
) else (
	SET BrowserName=%2
	Echo "BrowserName is set as !BrowserName!."
	SHIFT
	SHIFT
	GOTO ParseArguments
)
REM


REM
:: Keep the Remote server after finish the Regression testing.
:SETKeepServer
Echo "Keep the Remote server after finish the Regression testing."
Set KeepRemoteServer=TRUE
SHIFT
GOTO ParseArguments
REM


:Begin
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::
set max=0
for /f "tokens=1* delims=-.0" %%A in ('dir /b /a-d %SELENIUM_PLUS%\libs\selenium-server-standalone*.jar') do if %%B gtr !max! set max=%%B
set SELENIUM_SERVER_JAR_LOC=%SELENIUM_PLUS%\libs\selenium-%max%
set CMDCLASSPATH="%SELENIUM_PLUS%\libs\seleniumplus.jar;%SELENIUM_PLUS%\libs\JSTAFEmbedded.jar;%SELENIUM_SERVER_JAR_LOC%"
set EXECUTE=%SELENIUM_PLUS%\Java\jre\bin\java.exe
set COMPILE=%SELENIUM_PLUS%\Java\bin\javac.exe

:: DON'T MODIFY ABOVE SETTING UNLESS NECESSARY
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: First compile the project
rmdir /s /q bin
mkdir bin
:: caret ^ is just used to escape the newline so that we can write a dos command in multiple lines
"%COMPILE%" -cp %CMDCLASSPATH% -encoding UTF-8 -d bin -nowarn Tests/regression/*.java ^
                                                              Tests/regression/testcases/*.java ^
															  Tests/regression/testruns/*.java ^
															  Tests/regression/util/*.java
Echo "Project has been compiled." 
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Run the regression test 
 
:: How to override App Map variable
:: EXAMPLE:  %EXECUTE% -cp %CMDCLASSPATH%;bin regression.testruns.Regression -safsvar:GoogleUser=email@gmail.com

:: How to load external App Map order file
:: EXAMPLE:  %EXECUTE% -cp %CMDCLASSPATH%;bin regression.testruns.Regression -Dtestdesigner.appmap.order=AppMap_en.order

if [!BrowserName!] == [] (
Echo "Run Regression with 'TestBrowserName' value in MAP file."
"%EXECUTE%" -cp %CMDCLASSPATH%;bin regression.testruns.Regression
) else (
Echo "Run Regression with 'TestBrowserName' value: '!BrowserName!'."
"%EXECUTE%" -cp %CMDCLASSPATH%;bin regression.testruns.Regression -safsvar:TestBrowserName=!BrowserName!
)

If !KeepRemoteServer!==FALSE (
Echo "Terminate the Remote server after Regression."
Call %SELENIUM_PLUS%/extra/RemoteServerTerminate.bat
) Else (
Echo "Keep the Remote server after Regression."
)


:END
Echo "Test Job is Done with exit code %ERRORLEVEL%"
Exit /b %ERRORLEVEL%
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::