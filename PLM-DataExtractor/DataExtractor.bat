echo on

echo Setting the required path variables
SET HOME=${dataextract.home}
SET TEMPHOME=${dataextract.temphome}
SET JAVA_HOME=${dataextract.javahome}
SET ORACLE_HOME=${dataextract.oraclehome}
SET ORACLE_SID=${dataextract.oraclesid}
SET ENDECADIR=${dataextract.endecadir}

SET ORACLE_BIN=%ORACLE_HOME%\bin

SET DATADIR=%HOME%\output
SET ENDECADATADIR=%ENDECADIR%\Apps\PLM\test_data
SET BACKUPDIR=%TEMPHOME%\outputBackup

SET CLASSPATHBASE=%HOME%\classes
SET CLASSPATH=%CLASSPATHBASE%
SET CLASSPATH=%CLASSPATHBASE%\lib\ojdbc6-11.2.0.jar;%CLASSPATH%
SET CLASSPATH=%CLASSPATHBASE%\plm-dataextractor.jar;%CLASSPATH%
SET CLASSPATH=%CLASSPATHBASE%\lib\aopalliance-1.0.jar;%CLASSPATH%
SET CLASSPATH=%CLASSPATHBASE%\lib\commons-dbcp-1.2.2.jar;%CLASSPATH%
SET CLASSPATH=%CLASSPATHBASE%\lib\commons-logging-1.1.1.jar;%CLASSPATH%
SET CLASSPATH=%CLASSPATHBASE%\lib\commons-pool-1.3.jar;%CLASSPATH%
SET CLASSPATH=%CLASSPATHBASE%\lib\spring-beans-2.5.6.jar;%CLASSPATH%
SET CLASSPATH=%CLASSPATHBASE%\lib\spring-context-2.5.6.jar;%CLASSPATH%
SET CLASSPATH=%CLASSPATHBASE%\lib\spring-core-2.5.6.jar;%CLASSPATH%
SET CLASSPATH=%CLASSPATHBASE%\lib\spring-jdbc-2.5.6.jar;%CLASSPATH%
SET CLASSPATH=%CLASSPATHBASE%\lib\spring-tx-2.5.6.jar;%CLASSPATH%
SET TEMPPATH=%PATH%
SET PATH=%ORACLE_BIN%;%PATH%

for /f "tokens=2-4 delims=/ " %%a in ('date /T') do set year=%%c
for /f "tokens=2-4 delims=/ " %%a in ('date /T') do set month=%%a
for /f "tokens=2-4 delims=/ " %%a in ('date /T') do set day=%%b
set TODAY=%month%%day%%year%

for /f "tokens=1 delims=: " %%h in ('time /T') do set hour=%%h
for /f "tokens=2 delims=: " %%m in ('time /T') do set minutes=%%m
for /f "tokens=3 delims=: " %%a in ('time /T') do set ampm=%%a
set NOW=%hour%%minutes%%ampm%

SET BASEDIR=%HOME%
SET LOGFOLDER=%TEMPHOME%\logs\%TODAY%
SET PROCESSLOGFILE=%LOGFOLDER%\EndecaUpdate_%TODAY%_%NOW%.LOG
SET ERRORLOGFILE=%LOGFOLDER%\Error_%TODAY%_%NOW%.LOG
SET EXTRACTMODE=PARTIAL

IF "%1" == "FULL" SET EXTRACTMODE=FULL

IF NOT EXIST %LOGFOLDER% mkdir %LOGFOLDER%

echo [%date% %time%] Setting EXTRACTMODE to %EXTRACTMODE% >> %PROCESSLOGFILE%
echo [%date% %time%] Setting LOGFOLDER to %LOGFOLDER% >> %PROCESSLOGFILE%

SET DATADIR=%DATADIR%\%EXTRACTMODE%
echo [%date% %time%] Setting DATADIR to %DATADIR% >> %PROCESSLOGFILE%

SET BACKUPDIR=%BACKUPDIR%\%EXTRACTMODE%
echo [%date% %time%] Setting BACKUPDIR to %BACKUPDIR% >> %PROCESSLOGFILE%
mkdir %BACKUPDIR%\%TODAY%_%NOW%
echo [%date% %time%] Creating %BACKUPDIR%\%TODAY%_%NOW% directory >> %PROCESSLOGFILE%
SET BACKUPDIR=%BACKUPDIR%\%TODAY%_%NOW%
echo [%date% %time%] Setting BACKUPDIR to %BACKUPDIR% >> %PROCESSLOGFILE%

goto executeParoleeGenSQL

:executeParoleeGenSQL
echo [%date% %time%] Starting executeParoleeGenSQL >> %PROCESSLOGFILE%
sqlplus ${db.user}/${db.pass}@${dataextract.oraclesid} @%BASEDIR%\scripts\loadEligibleParolees.sql >> %PROCESSLOGFILE% 2> %ERRORLOGFILE%

:executeGatherPC290SQL
echo [%date% %time%] Starting executeGatherPC290SQL >> %PROCESSLOGFILE%
sqlplus ${db.user}/${db.pass}@${dataextract.oraclesid} @%BASEDIR%\scripts\PC290_BATCH.sql >> %PROCESSLOGFILE% 2> %ERRORLOGFILE%

:executeParoleeDataExtract
echo [%date% %time%] Starting executeParoleeDataExtract >> %PROCESSLOGFILE%
%JAVA_HOME%\bin\java -classpath %CLASSPATH% -DExtractMode=%EXTRACTMODE% com.plm.dataextract.DataExtractor >> %PROCESSLOGFILE% 2> %ERRORLOGFILE%
echo [%date% %time%] Data Extract complete >> %PROCESSLOGFILE%

IF "%EXTRACTMODE%" == "FULL" goto fullupdate

IF "%EXTRACTMODE%" == "PARTIAL" goto partialupdate

:fullupdate
echo [%date% %time%] Copying files from %DATADIR% to %ENDECADATADIR%\baseline directory >> %PROCESSLOGFILE%
copy %DATADIR%\*.* %ENDECADATADIR%\baseline
IF not %ERRORLEVEL 0 goto failed   
echo [%date% %time%] Starting  executeEndecaBaselineUpdate >> %PROCESSLOGFILE%
echo calling %ENDECADIR%\Apps\PLM\control\execute_full_update.bat
call %ENDECADIR%\Apps\PLM\control\execute_full_update.bat
IF not %ERRORLEVEL 0 goto failed
echo [%date% %time%] Moving files to %BACKUPDIR% directory >> %PROCESSLOGFILE%
copy %DATADIR%\*.* %BACKUPDIR%\*.*
del %DATADIR%\*.* /q
goto eof

:partialupdate
echo [%date% %time%] Copying files from %DATADIR% to %ENDECADATADIR%\partial directory >> %PROCESSLOGFILE%
copy %DATADIR%\*.* %ENDECADATADIR%\partial
IF not %ERRORLEVEL 0 goto failed   
echo [%date% %time%] Starting executeEndecaPartialUpdate >> %PROCESSLOGFILE%
call %ENDECADIR%\Apps\PLM\control\execute_partial_update.bat
IF not %ERRORLEVEL 0 goto failed   
echo [%date% %time%] Moving files to %BACKUPDIR% directory >> %PROCESSLOGFILE%
copy %DATADIR%\*.* %BACKUPDIR%\*.*
del %DATADIR%\*.* /q
goto eof

:failed
echo [%date% %time%] ERROR Please check logs for further details, %ERRORLEVEL >> %PROCESSLOGFILE%
SET PATH=%TEMPPATH%
exit

:eof
echo [%date% %time%] Data Extract and Endeca %EXTRACTMODE% update complete. >> %PROCESSLOGFILE%
SET PATH=%TEMPPATH%
exit