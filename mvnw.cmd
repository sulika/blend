@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.2
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET "APP_BASE_NAME=%~n0") ELSE (SET "APP_BASE_NAME=%__MVNW_ARG0_NAME__%")
@SET "APP_HOME=%~dp0"

@REM Strip trailing backslash
IF "%APP_HOME:~-1%"=="\" SET "APP_HOME=%APP_HOME:~0,-1%"

@SET "MAVEN_PROJECTBASEDIR=%APP_HOME%"

@IF NOT "%MAVEN_SKIP_RC%"=="" GOTO skipRcParsing
@FOR /F "usebackq tokens=1,2 delims==" %%a IN (`wmic ComSpec get /value 2^>NUL`) DO @IF "%%a"=="ComSpec" SET COMSPEC=%%b
@IF NOT EXIST "%COMSPEC%" (
  @echo 'ERROR: WMI returned invalid ComSpec value.' >&2
  @exit /b 1
)
:skipRcParsing

@SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@SET "WRAPPER_JAR=%APP_HOME%\.mvn\wrapper\maven-wrapper.jar"
@SET "MAVEN_CONFIG="

@REM Find JAVA_HOME
@IF NOT "%JAVA_HOME%"=="" GOTO findJavaFromJavaHome
@SET "JAVA_HOME=%ProgramFiles%\Java\jdk-21"
:findJavaFromJavaHome
@SET "JAVA_EXE=%JAVA_HOME%/bin/java.exe"
@IF EXIST "%JAVA_EXE%" GOTO execute

@echo 'ERROR: JAVA_HOME is not set or does not point to a valid JDK.' >&2
@exit /b 1

:execute
@SET "MAVEN_OPTS=-Xmx64m -Xms64m"
@"%JAVA_EXE%" %MAVEN_OPTS% -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" %WRAPPER_LAUNCHER% %MAVEN_CONFIG% %*
