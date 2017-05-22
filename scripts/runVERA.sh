#!/bin/bash

iterNum="$1"

javac ../src/Main.java
java -cp ../src/ Main $iterNum $(realpath "$2")

