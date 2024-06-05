#!/bin/sh

#Parameter mg dann andere Umgebung verwenden
if [ $1 = "mg" ]
then
	echo $1
	export JAVA_HOME="/home/mg/graalvm-svm-java17-linux-gluon-22.1.0.1-Final"
	export GRAALVM_HOME="/home/mg/graalvm-svm-java17-linux-gluon-22.1.0.1-Final"
	export MAVEN_HOME="/usr/share/maven"

	export ANDROID_SDK="/home/mg/.gluon/substrate/Android/"
	export ANDROID_NDK="/home/mg/.gluon/substrate/Android/ndk/25.2.9519653/"
else
	#Umgebung fuer Michael
	export JAVA_HOME="/opt/graalvm-svm-java17-linux-gluon-22.1.0.1-Final"
	export GRAALVM_HOME="/opt/graalvm-svm-java17-linux-gluon-22.1.0.1-Final"
	export MAVEN_HOME="/opt/apache-maven-3.6.3"

	export ANDROID_NDK="/home/michael/.gluon/substrate/Android/cmdline-tools/ndk/25.2.9519653"
	export ANDROID_SDK="/home/michael/.gluon/substrate/Android/cmdline-tools"
fi







./gradlew clean publishToMavenLocal

