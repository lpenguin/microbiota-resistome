#!/bin/bash

iterNum="$1"

java -jar ../target/microbiomeres-0.1.jar $iterNum $(realpath "$2")

