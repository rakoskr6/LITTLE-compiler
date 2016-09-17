#!/bin/bash
# Author: Brian Rieder
# Date:   17 September 2016

make
for testfile in `ls testcases/input/ | sort -V` ; do
  extstrip=${testfile%.micro}
  allstrip=${extstrip##*/}
  echo "Testing file: $testfile"
  if [[ $OSTYPE == "cygwin" ]] ; then
    echo -n "Output:   "
    java -cp "classes/;lib/antlr.jar" Micro testcases/input/$testfile
    echo -n "Expected: "
    cat testcases/output/$allstrip.out
  elif [[ $OSTYPE == "linux-gnu" ]] ; then
    echo -n "Output:   "
    java -cp classes/:lib/antlr.jar Micro testcases/input/$testfile
    echo -n "Expected: "
    cat testcases/output/$allstrip.out
  else
    echo "Error: Unsupported OSTYPE $OSTYPE"
    exit
  fi
  echo "" # newline
done
