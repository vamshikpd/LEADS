@echo off
rem
rem Copyright (c) 2006 Endeca Technologies Inc. All rights reserved.
rem COMPANY CONFIDENTIAL
rem

call %~dp0..\config\script\set_environment.bat
copy %ENDECA_PROJECT_DIR%\test_data\partial\* %ENDECA_PROJECT_DIR%\data\partials\incoming
call %~dp0set_partial_data_ready_flag.bat PLMAddressSourceFile.txt
call %~dp0set_partial_data_ready_flag.bat PLMAliasSourceFile.txt
call %~dp0set_partial_data_ready_flag.bat PLMJobSourceFile.txt
call %~dp0set_partial_data_ready_flag.bat PLMMonikerSourceFile.txt
call %~dp0set_partial_data_ready_flag.bat PLMOffenseSourceFile.txt
call %~dp0set_partial_data_ready_flag.bat PLMParoleeDataSourceFile.txt
call %~dp0set_partial_data_ready_flag.bat PLMPrevAddressSourceFile.txt
call %~dp0set_partial_data_ready_flag.bat PLMProblemAreaSourceFile.txt
call %~dp0set_partial_data_ready_flag.bat PLMSMTSourceFile.txt
call %~dp0set_partial_data_ready_flag.bat PLMSpecialConditionSourceFile.txt
call %~dp0set_partial_data_ready_flag.bat PLMVehicleSourceFile.txt

