#!/bin/bash

qp=""

while getopts t:o:c:q option
do
 case "${option}"
 in
 t) iterNum=${OPTARG};;
 o) outdir=$(readlink --canonicalize "${OPTARG}");;
 c) confile=${OPTARG};;
 q) qp="-quiet"
 esac
done

mkdir -p $outdir
abund=$outdir"/abundLog"
trans=$outdir"/transLog"

# echo "qp = $qp"

java -jar ../target/microbiomeres-0.1-jar-with-dependencies.jar $iterNum $abund $confile $trans $qp

