@echo off
setlocal

:: Define directories
set SOURCE_LIST=javafiles.txt
set OUTPUT_DIR=.\vnaJ.3.4.8
set JAR_NAME=vnaJ.3.4.8.jar
set DEPENDENCIES_DIR=lib
set MANIFEST_FILE=MANIFEST.MF
set MAIN_CLASS=krause.vna.gui.VNAMain

:: Clean up old output
if exist %OUTPUT_DIR% rd /s /q %OUTPUT_DIR%
mkdir %OUTPUT_DIR%

:: Compile Java source files using a list
echo Compiling Java source files...
javac -cp .;lib\* -d %OUTPUT_DIR% @%SOURCE_LIST%
if errorlevel 1 exit /b %errorlevel%

:: Create manifest file if it doesn't exist
if not exist %MANIFEST_FILE% (
    echo Manifest-Version: 1.0 > %MANIFEST_FILE%
    echo Main-Class: %MAIN_CLASS% >> %MANIFEST_FILE%
)

:: Create the JAR file
echo Creating JAR file...
jar cvfm %JAR_NAME% META-INF\MANIFEST.MF -C %OUTPUT_DIR% .
if errorlevel 1 exit /b %errorlevel%


jar uvf %JAR_NAME% images\*
jar uvf %JAR_NAME% krause\vna\resources\*
jar uvf %JAR_NAME% krause\vna\resources\help\*
jar uvf %JAR_NAME% krause\vna\resources\help\de\*
jar uvf %JAR_NAME% krause\vna\resources\help\en\*
jar uvf %JAR_NAME% krause\vna\resources\help\es\*
jar uvf %JAR_NAME% krause\vna\resources\help\images\*
jar uvf %JAR_NAME% run.cmd 
jar uvf %JAR_NAME% system.properties.max6 
jar uvf %JAR_NAME% system.properties
cd lib
jar uvf ..\%JAR_NAME% *
:: Verify JAR file contents
echo Verifying JAR file...
jar tf %JAR_NAME%
echo Done.
endlocal

