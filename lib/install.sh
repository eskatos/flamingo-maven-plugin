#!/bin/sh

# This script install locally needed artifacts not found in any public maven repository


install_jar() {
	mvn install:install-file -Dfile=${1} -DgroupId=${2} -DartifactId=${3} -Dversion=${4} -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true
	echo "Installed ${1} with GAV: ${2}:${3}:${4}"
}


install_jar "batik-all.jar" "batik" "batik-all" "1.7-with-r608262"
install_jar "flamingo-4.2.jar" "net.java.desktop" "flamingo" "4.2"


