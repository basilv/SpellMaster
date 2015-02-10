@REM Build SpellMaster plugin, install into CanaryMod server, and start server
@REM
@REM Required ENV vars:
@REM JAVA_HOME (for Maven)
@REM ----------------------------------------------------------------------------

@echo off

cmd /c buildDeploy.bat
if %ERRORLEVEL% GEQ 1 exit /B %ERRORLEVEL%

set MINECRAFT_SERVER=..\..\Minecraft\server
cmd /c startServer.bat %MINECRAFT_SERVER%


