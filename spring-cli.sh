#!/bin/bash

JAR_PATH="$(dirname "$0")/spring-cli.jar"

if [[ ! -f "$JAR_PATH" ]]; then
  echo "❌ spring-cli.jar not found. Please run './gradlew build' first."
  exit 1
fi

java -jar "$JAR_PATH" "$@"
