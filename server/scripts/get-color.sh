#!/bin/bash

if [[ "$#" -ne 4 ]]; then
   echo "$0 host red green blue"
   exit 1
fi

HOST=$1
RED=$2
GREEN=$3
BLUE=$4
NAME=$5

COMMAND="curl $HOST/api/color/rgb/$GREEN/$RED/$BLUE -H \"Content-type: application/json\""

echo $COMMAND
eval $COMMAND
echo ""


