#!/bin/bash

# bash estimationReportRUN.sh ../out/simulations/Jun_23_time_17_07/ ../resources/config.properties ..out/estimationOfOOI


abTabFolder=$(readlink --canonicalize "$1")

echo "read $abTabFolder"
#bufVar=$(date +"%b")"_"$(date +"%e") #"_time_"$(date +"%H")"_"$(date +"%M")

#echo "try read $($(dirname $0)/$3)"
mkdir -p "$(dirname $0)/$3"
outPath=$(readlink --canonicalize "$3")
echo "Created folder $outPath"
mkdir -p $outPath"/tmp"

cpTab=$(readlink --canonicalize "$2")

cpN="../resources/cp_notes"

Rscript --vanilla "$(dirname $0)/optim_rate_estimation.R" $abTabFolder $cpTab $outPath $cpN >> "$(dirname $0)/../out/log/log.est"

rm -rf $outPath"/tmp"

