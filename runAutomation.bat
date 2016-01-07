@echo off
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::
setlocal enableDelayedExpansion
set max=0
for /f "tokens=1* delims=-.0" %%A in ('dir /b /a-d %SELENIUM_PLUS%\libs\selenium-server-standalone*.jar') do if %%B gtr !max! set max=%%B
set SELENIUM_SERVER_JAR_LOC=%SELENIUM_PLUS%\libs\selenium-%max%
set CMDCLASSPATH="%SELENIUM_PLUS%\libs\seleniumplus.jar;%SELENIUM_PLUS%\libs\JSTAFEmbedded.jar;%SELENIUM_SERVER_JAR_LOC%"
set EXECUTE=%SELENIUM_PLUS%\Java\jre\bin\java.exe
set COMPILE=%SELENIUM_PLUS%\Java\bin\javac.exe

:: DON'T MODIFY ABOVE SETTING UNLESS NECESSARY
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: First compile the project
rmdir /s /q bin
mkdir bin
"%COMPILE%" -cp %CMDCLASSPATH% -encoding UTF-8 -d bin -nowarn Tests/regression/*.java Tests/regression/testcases/*.java Tests/regression/testruns/*.java
Echo "Project has been compiled." 
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Run the regression test 
 
:: How to override App Map variable
:: EXAMPLE:  %EXECUTE% -cp %CMDCLASSPATH%;bin regression.testruns.Regression -safsvar:GoogleUser=email@gmail.com

:: How to load external App Map order file
:: EXAMPLE:  %EXECUTE% -cp %CMDCLASSPATH%;bin regression.testruns.Regression -Dtestdesigner.appmap.order=AppMap_en.order

"%EXECUTE%" -cp %CMDCLASSPATH%;bin regression.testruns.Regression

Echo "Test Job is Done with exit code %ERRORLEVEL%"
Exit /b %ERRORLEVEL%
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
