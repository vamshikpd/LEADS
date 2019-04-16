@echo off
rem
rem Copyright (c) 2006 Endeca Technologies Inc. All rights reserved.
rem COMPANY CONFIDENTIAL
rem

call %~dp0..\config\script\set_environment.bat
echo Removing existing application provisioning...
call %~dp0runcommand.bat --remove-app
echo Setting EAC provisioning and Web Studio configuration...
call %~dp0update_web_studio_config.bat
echo Finished updating EAC and Web Studio.
