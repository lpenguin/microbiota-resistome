#!/bin/bash

iterNum="$1"
bufVar=$(date +"%b")"_"$(date +"%e")"_time_"$(date +"%H")"_"$(date +"%M")
echo "outPath $bufVar"
pwd
outPath=$(realpath "$2$bufVar")
mkdir -p $outPath
echo "Created folder $outPath"
repNum="$3"

for (( i=1; i<=$repNum; i++ ))
do
   namePath=$outPath"/rep$i.txt"
   echo "Created file "$namePath
   echo "Prop file "$(realpath "$4")
   java -jar ../target/microbiomeres-0.1.jar $iterNum $namePath $(realpath "$4") /dev/null
   # rm -f ../src/*.class
   # javac -cp ../src ../src/Main.java
   # java -cp ../src/ Main $iterNum $namePath
done

