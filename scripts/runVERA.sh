#!/bin/bash

iterNum="$1"

rm -f ../src/*.class
javac -cp ../src/ ../src/Main.java
java -cp ../src/ Main $iterNum $(realpath "$2")

