#! /bin/bash

if [[ "$#" -ne 1 || ! -r $1 ]] ; then
    echo "$0 file.csv"
    exit 1
fi

cat $1 | while IFS="," read name r g b ; do
    ./vote-post.sh http://panchromious.herokuapp.com "$r" "$g" "$b" "$name"
done
