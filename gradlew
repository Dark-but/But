#!/usr/bin/env sh

# Android Gradlew Web-Stub For GitHub Actions
# This script launches Gradle from the wrapper jar.

if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

if [ ! -x "$JAVACMD" ] ; then
    die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
fi

# डाउनलोड और रन करने का कमांड (GitHub Actions के लिए मिनिमल वर्जन)
exec gradle "$@"
