#!/bin/sh

export JAVA_HOME="/opt/graalvm-svm-java17-linux-gluon-22.1.0.1-Final"
export GRAALVM_HOME="/opt/graalvm-svm-java17-linux-gluon-22.1.0.1-Final"
export MAVEN_HOME="/opt/apache-maven-3.6.3"

export ANDROID_NDK="/home/michael/.gluon/substrate/Android/cmdline-tools/ndk/25.2.9519653"
export ANDROID_SDK="/home/michael/.gluon/substrate/Android/cmdline-tools"

./gradlew clean

