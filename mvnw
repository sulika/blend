#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.2
#
# Optional ENV vars:
# ------------------
#   JAVA_HOME - location of a JDK home dir, required when JAVA is not on PATH
#   MAVEN_OPTS - parameters passed to the Java VM when running Maven
#     e.g. to debug Maven itself, use
#       set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
#   MAVEN_SKIP_RC - flag to disable loading of mavenrc files
# ----------------------------------------------------------------------------

if [ -z "$MAVEN_SKIP_RC" ]; then
  if [ -f /usr/local/etc/mavenrc ]; then
    . /usr/local/etc/mavenrc
  fi
  if [ -f /etc/mavenrc ]; then
    . /etc/mavenrc
  fi
  if [ -f "$HOME/.mavenrc" ]; then
    . "$HOME/.mavenrc"
  fi
fi

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
darwin=false
mingw=false
case "$(uname)" in
  CYGWIN*)
    cygwin=true
    ;;
  MINGW*)
    mingw=true
    ;;
  Darwin*)
    darwin=true
    ;;
esac

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ]; do
  ls=$(ls -ld "$PRG")
  link=$(expr "$ls" : '.*-> \(.*\)$')
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG="$(dirname "$PRG")/$link"
  fi
done

SAVED="$(pwd)"
cd "$(dirname "$PRG")/" >/dev/null
APP_HOME="$(pwd -P)"
cd "$SAVED" >/dev/null

APP_NAME="mvnw"
APP_BASE_NAME=$(basename "$0")

# Add default JVM options here. You can also use JAVA_OPTS and MAVEN_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Find the java executable
if [ -n "$JAVA_HOME" ]; then
  if [ -x "$JAVA_HOME/jre/sh/java" ]; then
    # IBM's JDK on AIX uses strange locations for the executables
    JAVACMD="$JAVA_HOME/jre/sh/java"
  else
    JAVACMD="$JAVA_HOME/bin/java"
  fi
  if [ ! -x "$JAVACMD" ]; then
    die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
  fi
else
  JAVACMD="java"
  if ! command -v java >/dev/null 2>&1; then
    die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
  fi
fi

# Increase the maximum file descriptors if we can.
if ! "$cygwin" && ! "$darwin" && ! "$mingw"; then
  case $MAX_FD in
  max*)
    # In POSIX sh, ulimit -H is undefined. That's why the result is checked to see if it worked.
    # shellcheck disable=SC2039,SC3045
    MAX_FD=$(ulimit -H -n) ||
      warn "Could not query maximum file descriptor limit"
    ;;
  esac
  case $MAX_FD in
  '' | soft) :;;
  *)
    # In POSIX sh, ulimit -n is undefined. That's why the result is checked to see if it worked.
    # shellcheck disable=SC2039,SC3045
    ulimit -n "$MAX_FD" ||
      warn "Could not set maximum file descriptor limit to $MAX_FD"
    ;;
  esac
fi

# Collect all arguments for the java command;
# * $DEFAULT_JVM_OPTS, $JAVA_OPTS, and $MAVEN_OPTS can contain fragments of
#   shell commands, and must be treated as such -- especially to handle
#   the case where they contain whitespace -- hence use of eval.
# * At the same time, we want each user-provided arg to be treated separately
#   (to behave as if the user had wrapped each arg in quotes) so args are
#   passed as individual "$@" arguments to java.

WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

# shellcheck disable=SC2086
exec "$JAVACMD" \
  $MAVEN_OPTS \
  $MAVEN_DEBUG_OPTS \
  -classpath "$APP_HOME/.mvn/wrapper/maven-wrapper.jar" \
  "-Dmaven.multiModuleProjectDirectory=${APP_HOME}" \
  ${WRAPPER_LAUNCHER} $MAVEN_CONFIG "$@"
