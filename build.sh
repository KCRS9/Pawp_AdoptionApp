#!/bin/bash
set -e

echo "Installing Java 17..."
apt-get update
apt-get install -y openjdk-17-jdk-headless

echo "Setting JAVA_HOME..."
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

echo "Running Gradle build..."
cd Frontend
./gradlew wasmJsMainDistribution
