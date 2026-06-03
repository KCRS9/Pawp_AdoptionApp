#!/bin/bash
set -e

echo "Running Gradle build..."
cd Frontend
./gradlew wasmJsMainDistribution
