#!/bin/sh
# Gradle Wrapper 引导脚本
APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`
export GRADLE_USER_HOME="$HOME/.gradle"
exec sh "$(dirname "$0")/gradle/wrapper/gradle-wrapper.jar" "$@"