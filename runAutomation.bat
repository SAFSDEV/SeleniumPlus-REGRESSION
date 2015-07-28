::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::
set CMDCLASSPATH="%SELENIUM_PLUS%/libs/seleniumplus.jar;%SELENIUM_PLUS%/libs/JSTAFEmbedded.jar;%SELENIUM_PLUS%/libs/selenium-server-standalone-2.44.0.jar"
set EXECUTE=%SELENIUM_PLUS%/Java/bin/java

:: DON'T MODIFY ABOVE SETTING UNLESS NECESSARY
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

:: How to override App Map variable
:: EXAMPLE:  %EXECUTE% -cp %CMDCLASSPATH%;bin regression.testruns.Regression -safsvar:GoogleUser=email@gmail.com

:: How to load external App Map order file
:: EXAMPLE:  %EXECUTE% -cp %CMDCLASSPATH%;bin regression.testruns.Regression -Dtestdesigner.appmap.order=AppMap_en.order

%EXECUTE% -cp %CMDCLASSPATH%;bin regression.testruns.Regression
