@echo off
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::
setlocal enableDelayedExpansion
set max=0
for /f "tokens=1* delims=-.0" %%A in ('dir /b /a-d %SELENIUM_PLUS%\libs\selenium-server-standalone*.jar') do if %%B gtr !max! set max=%%B
set SELENIUM_SERVER_JAR_LOC=%SELENIUM_PLUS%\libs\selenium-%max%
set CMDCLASSPATH="%SELENIUM_PLUS%\libs\seleniumplus.jar;%SELENIUM_PLUS%\libs\JSTAFEmbedded.jar;%SELENIUM_SERVER_JAR_LOC%"
set EXECUTE=%SELENIUM_PLUS%\Java\jre\bin\java.exe

:: DON'T MODIFY ABOVE SETTING UNLESS NECESSARY
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

:: How to override App Map variable
:: EXAMPLE:  %EXECUTE% -cp %CMDCLASSPATH%;bin regression.testruns.Regression -safsvar:GoogleUser=email@gmail.com

:: How to load external App Map order file
:: EXAMPLE:  %EXECUTE% -cp %CMDCLASSPATH%;bin regression.testruns.Regression -Dtestdesigner.appmap.order=AppMap_en.order

"%EXECUTE%" -cp %CMDCLASSPATH%;bin regression.testruns.Regression

Echo "Test Job is Done."
