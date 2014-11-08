#!/bin/bash

if [[ "$#" -ne 1 ]]; then
   echo "$0 host"
   exit 1
fi

HOST=$1

COMMAND="curl $HOST/api/vote -H \"Content-type: application/json\""

echo $COMMAND
eval $COMMAND
echo ""

