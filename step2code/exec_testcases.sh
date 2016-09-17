#!/bin/bash

make
for testfile in `ls -d testcases/input/* | sort -V` ; do
  echo "Testing file: $testfile"
  if [[ $OSTYPE == "cygwin" ]] ; then
    java -cp "classes/;lib/antlr.jar" Micro $testfile
  elif [[ $OSTYPE == "linux-gnu" ]] ; then
    java -cp classes/:lib/antlr.jar Micro $testfile
  else
    echo "Error: Unsupported OSTYPE $OSTYPE"
    exit
  fi
  echo "" # newline
done
