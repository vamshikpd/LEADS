@echo off
rem
rem Copyright (c) 2007 Endeca Technologies Inc. All rights reserved.
rem COMPANY CONFIDENTIAL
rem
rem - madhu menon 06-07-2010 - added mail-notification.jar for emailing facility

setlocal

call %~dp0..\config\script\set_environment.bat

if not defined ENDECA_ROOT (
  echo ERROR: ENDECA_ROOT is not set.
  exit /b 1
)

set JAVA=java
if exist %ENDECA_ROOT%\j2sdk\bin\java.exe (
  set JAVA=%ENDECA_ROOT%\j2sdk\bin\java.exe
)

set APP_CONFIG_XML=%~dp0..\config\script\AppConfig.xml
if not exist %APP_CONFIG_XML% (
  echo ERROR: Cannot find AppConfig.xml at %APP_CONFIG_XML%
  exit /b 1
)

set CLASSPATH=%ENDECA_ROOT%\lib\java\eacclient.jar
set CLASSPATH=%CLASSPATH%;%ENDECA_ROOT%\lib\java\jaxrpc.jar
set CLASSPATH=%CLASSPATH%;%ENDECA_ROOT%\lib\java\mail.jar
set CLASSPATH=%CLASSPATH%;%ENDECA_ROOT%\lib\java\saaj.jar
set CLASSPATH=%CLASSPATH%;%ENDECA_ROOT%\lib\java\wsdl4j-1.5.1.jar
set CLASSPATH=%CLASSPATH%;%ENDECA_ROOT%\lib\java\activation.jar
set CLASSPATH=%CLASSPATH%;%ENDECA_ROOT%\lib\java\axis.jar
set CLASSPATH=%CLASSPATH%;%ENDECA_ROOT%\lib\java\commons-discovery-0.2.jar
set CLASSPATH=%CLASSPATH%;%ENDECA_ROOT%\lib\java\commons-logging-1.0.4.jar
set CLASSPATH=%CLASSPATH%;%~dp0\..\config\lib\java\eacToolkit.jar
set CLASSPATH=%CLASSPATH%;%~dp0\..\config\lib\java\eacComponents.jar
set CLASSPATH=%CLASSPATH%;%~dp0\..\config\lib\java\eacHandlers.jar
set CLASSPATH=%CLASSPATH%;%~dp0\..\config\lib\java\spring.jar
set CLASSPATH=%CLASSPATH%;%~dp0\..\config\lib\java\bsh-2.0b4.jar
set CLASSPATH=%CLASSPATH%;%~dp0\..\config\lib\java\casStubs.jar
set CLASSPATH=%CLASSPATH%;%~dp0\..\config\lib\java\mail-notification.jar
set CLASSPATH=%CLASSPATH%;%~dp0\..\config\script

set JAVA_ARGS=%JAVA_ARGS% "-Djava.util.logging.config.file=%~dp0..\config\script\logging.properties"

if exist %ENDECA_CONF%\conf\ca.ks (
  set TRUSTSTORE=%ENDECA_CONF%\conf\ca.ks
) else (
  echo WARNING: Cannot find keystore at %ENDECA_CONF%\conf\eac.ks. Secure EAC communication may fail.
)

if exist %ENDECA_CONF%\conf\eac.ks (
  set KEYSTORE=%ENDECA_CONF%\conf\eac.ks
) else (
  echo WARNING: Cannot find truststore at %ENDECA_CONF%\conf\ca.ks. Secure EAC communication may fail.
)

set JAVA_ARGS=%JAVA_ARGS% "-Djavax.net.ssl.trustStore=%TRUSTSTORE%" "-Djavax.net.ssl.trustStoreType=JKS" "-Djavax.net.ssl.trustStorePassword=eacpass"
set JAVA_ARGS=%JAVA_ARGS% "-Djavax.net.ssl.keyStore=%KEYSTORE%" "-Djavax.net.ssl.keyStoreType=JKS" "-Djavax.net.ssl.keyStorePassword=eacpass"

set CONTROLLER_ARGS=--app-config AppConfig.xml

set OVERRIDE_PROPERTIES=%~dp0..\config\script\environment.properties
if exist %OVERRIDE_PROPERTIES% (
  set OVERRIDE_ARG=--config-override environment.properties
)
set CONTROLLER_ARGS=%CONTROLLER_ARGS% %OVERRIDE_ARG%

"%JAVA%" %JAVA_ARGS% -cp "%CLASSPATH%" com.endeca.soleng.eac.toolkit.Controller %CONTROLLER_ARGS% %*

if not %ERRORLEVEL%==0 goto :FAILURE
endlocal
exit /b 0

:FAILURE
endlocal
exit /b 1



