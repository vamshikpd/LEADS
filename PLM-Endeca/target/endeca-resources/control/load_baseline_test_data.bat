@echo off
rem
rem Copyright (c) 2006 Endeca Technologies Inc. All rights reserved.
rem COMPANY CONFIDENTIAL
rem

call %~dp0..\config\script\set_environment.bat
copy %ENDECA_PROJECT_DIR%\test_data\baseline\* %ENDECA_PROJECT_DIR%\data\incoming
call %~dp0set_baseline_data_ready_flag.bat

