#!/bin/bash

if [[ "$#" -ne 5 ]]; then
   echo "$0 host red green blue name"
   exit 1
fi

HOST=$1
RED=$2
GREEN=$3
BLUE=$4
NAME=$5

JSON="{\"result\": \"selected\", \"value\": \"$NAME\", \"color\": {\"red\": $RED, \"green\": $GREEN, \"blue\": $BLUE}}"
COMMAND="curl $HOST/api/vote -H \"Content-type: application/json\" -X POST -d '$JSON'"

echo $COMMAND
eval $COMMAND


