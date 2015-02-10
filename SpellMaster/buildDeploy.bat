@REM Build SpellMaster plugin and install into CanaryMod server
@REM
@REM Required ENV vars:
@REM JAVA_HOME (for Maven)
@REM ----------------------------------------------------------------------------

@echo off

@REM Need cmd because mvn.bat kills current shell upon completion
cmd /c mvn package
if %ERRORLEVEL% GEQ 1 exit /B %ERRORLEVEL%

set MINECRAFT_SERVER=..\..\Minecraft\server
copy target\SpellMaster-*.jar %MINECRAFT_SERVER%\plugins



