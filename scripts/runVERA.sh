#!/bin/bash

iterNum="$1"

if [ -z "$3" ]
	then
		java -jar ../target/microbiomeres-0.1.jar $iterNum $(realpath "$2")
	else
		java -jar ../target/microbiomeres-0.1.jar $iterNum $(realpath "$2") $(realpath "$3")
fi

