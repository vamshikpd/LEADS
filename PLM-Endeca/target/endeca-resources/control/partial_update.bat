@echo off
rem
rem Copyright (c) 2006 Endeca Technologies Inc. All rights reserved.
rem COMPANY CONFIDENTIAL
rem

call %~dp0..\config\script\set_environment.bat
move %ENDECA_PROJECT_DIR%\logs\partial_update.out %ENDECA_PROJECT_DIR%\logs\partial 2> nul
call %~dp0runcommand.bat PartialUpdate 2>&1 | perl.exe %~dp0tee.pl %ENDECA_PROJECT_DIR%\logs\partial_update.out