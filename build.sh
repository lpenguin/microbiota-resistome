#!/bin/bash
version=0.1
artifact_name="microbiomeres-${version}-jar-with-dependencies.jar"
artifact_file="target/${artifact_name}"

scripts=(
  scripts/estimationReportRUN.sh
  scripts/functionsSet.R
  scripts/modelPlots.R scripts/multiVERA.sh
  scripts/multyrunning_esmimation.R
  scripts/ooiReportGenerating.Rmd
  scripts/optim_rate_estimation.R
  scripts/reportGenerating.Rmd
  scripts/reportRUN.sh
  scripts/runVERA.sh
)

mvn clean compile assembly:single

build_name="microbiomeres-${version}"
build_dir="builds/${build_name}"
rm -rf ${build_dir}

mkdir -p ${build_dir}
cp ${scripts[*]} ${build_dir}
cp ${artifact_file} ${build_dir}/microbiomeres.jar

cp resources/config.properties ${build_dir}
cd builds/

rm -f ${build_name}.zip
zip -r ${build_name}.zip ${build_name}
cd ..

echo "Stored package in builds/${build_name}.zip"