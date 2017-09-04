#!/bin/bash

# A simple example script that publishes a number of scripts to the Nexus Repository Manager
# and executes them.

# fail if anything errors
set -e
# fail if a function call is missing an argument
set -u

while getopts u:p:h: option
do
 case "${option}"
 in
 u) username=${OPTARG};;
 p) password=${OPTARG};;
 h) host=${OPTARG};;
 esac
done

echo $host

# add a script to the repository manager and run it
function addAndRunScript {
  name=$1

  curl -v -X DELETE -u $username:$password "$host/service/siesta/rest/v1/script/$name"
  printf "\nDeleted script $name\n\n"
}

printf "Provisioning Integration API Scripts Starting \n\n"
printf "Publishing and executing on $host\n"

addAndRunScript listRawAssets
addAndRunScript deleteRawAssets

printf "\nDeleting Scripts Completed\n\n"
