#!/bin/bash

while getopts i:t:o:c: option
do
 case "${option}"
 in
 i) repNum=${OPTARG};;
 t) iterNum=${OPTARG};;
 o) preOutdir=${OPTARG};;
 c) confile=$(readlink --canonicalize "${OPTARG}");;
 esac
done

mkdir -p $preOutdir
outdir=$(readlink --canonicalize "$preOutdir")

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
   java -jar ./microbiomeres.jar $iterNum $abPath $confile $trPath -quiet
   echo "Proccessed run "$i
done
