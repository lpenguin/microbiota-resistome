#!/bin/bash

while getopts i:t:o:c: option
do
 case "${option}"
 in
 i) repNum=${OPTARG};;
 t) iterNum=${OPTARG};;
 o) outdir=$(readlink --canonicalize "${OPTARG}");;
 c) confile=$(readlink --canonicalize "${OPTARG}");;
 esac
done

abund=$outdir"/abundTables"
trans=$outdir"/transTables"

mkdir -p $abund
mkdir -p $trans

echo "Created folder $abund"
echo "Created folder $trans"

for (( i=1; i<=$repNum; i++ ))
do
   abPath=$abund"/rep$i.txt"
   trPath=$trans"/trRep$i.txt"
   java -jar ../target/microbiomeres-0.1-jar-with-dependencies.jar $iterNum $abPath $confile $trPath -quiet
   echo "Proccessed run "$i
done
