#!/bin/bash
rm -rf bin
mkdir -p bin

javac -cp "lib/*" -d bin $(find src -name "*.java")
