#!/bin/bash

iterNum="$1"

if [ -z "$4" ]
	then
		java -jar ../target/microbiomeres-0.1.jar $iterNum $(realpath "$2") $(realpath "$3")
	else
		java -jar ../target/microbiomeres-0.1.jar $iterNum $(realpath "$2") $(realpath "$3") $(realpath "$4")
fi

