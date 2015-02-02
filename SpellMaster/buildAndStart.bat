@REM Build SpellMaster plugin, install into CanaryMod server, and start server
@REM
@REM Required ENV vars:
@REM JAVA_HOME (for Maven)
@REM ----------------------------------------------------------------------------

@REM @echo off

@REM Need cmd because mvn.bat kills current shell upon completion
cmd /c mvn package

set MINECRAFT_SERVER=..\server

copy target\SpellMaster-*.jar %MINECRAFT_SERVER%\plugins

cmd /c startServer.bat %MINECRAFT_SERVER%
