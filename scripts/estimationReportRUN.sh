#!/bin/bash

# bash estimationReportRUN.sh -i ../out/simulations/mruns -c  ../resources/config.properties

while getopts i:c: option
do
 case "${option}"
 in
 i) inpDir=$(readlink --canonicalize "${OPTARG}");;
 c) confile=$(readlink --canonicalize "${OPTARG}");;
 esac
done

abund=$inpDir"/abundTables"
outPath=$inpDir"/OOIestimation"

echo "Created folder $outPath"
mkdir -p $outPath"/tmp"

cpN=$(readlink --canonicalize "$(dirname $0)/../resources/cp_notes")
#echo "cp notes doc $cpN"

Rscript --vanilla $(readlink --canonicalize "$(dirname $0)/optim_rate_estimation.R") $abund $confile $outPath $cpN >> $(readlink --canonicalize "$(dirname $0)/../out/log/log.est")

rm -rf $outPath"/tmp"

