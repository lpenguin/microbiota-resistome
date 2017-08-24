#!/bin/bash

# bash reportRUN.sh ../out/simulations/buftable_15_06.txt ../out/plots/ ../out/log/transLog_14_06_2ver.txt Shigella


abTab=$(realpath "$1")

bufVar=$(date +"%b")"_"$(date +"%e") #"_time_"$(date +"%H")"_"$(date +"%M")

outPath=$(realpath "$2$bufVar")
mkdir -p $outPath
echo "Created folder $outPath"
mkdir -p $outPath"/tmp"

trTab=$(realpath "$3")

titleN="$4"

Rscript --vanilla "$(dirname $0)/modelPlots.R" $abTab $outPath $trTab $titleN

rm -rf $outPath"/tmp"

