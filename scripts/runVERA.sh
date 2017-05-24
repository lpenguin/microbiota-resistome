#!/bin/bash

iterNum="$1"

rm -f ../src/com/ripcm/microbiomeresistom/*.class
javac -cp ../src/ ../src/com/ripcm/microbiomeresistom/Main.java
java -cp ../src/ com.ripcm.microbiomeresistom.Main $iterNum $(realpath "$2")

