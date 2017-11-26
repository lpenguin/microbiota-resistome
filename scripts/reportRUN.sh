#!/bin/bash

# bash reportRUN_v2.sh -i ../out/simulations/oct27v2 -t Shigella -c ../resources/config.properties

while getopts i:c:t: option
do
 case "${option}"
 in
 t) titleN=${OPTARG};;
 i) inpDir=$(readlink --canonicalize "${OPTARG}");;
 c) confile=$(readlink --canonicalize "${OPTARG}");;
 esac
done

abund=$inpDir"/abundLog"
trans=$inpDir"/transLog"


outPath=$inpDir"/plots" #$(realpath "$2$bufVar")
mkdir -p $outPath"/tmp"
echo "Created folder $outPath"


cpN=$(readlink --canonicalize "$(dirname $0)/../resources/cp_notes")

Rscript --vanilla "$(dirname $0)/modelPlots.R" $abund $outPath $trans $titleN $confile $cpN >> $(readlink --canonicalize "$(dirname $0)/../out/log/log.report")

rm -rf $outPath"/tmp"

