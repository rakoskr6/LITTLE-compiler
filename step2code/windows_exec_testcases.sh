#!/bin/bash

make
for testfile in `ls -d testcases/input/* | sort -V` ; do
  echo "Testing file: $testfile"
  java -cp "classes/;lib/antlr.jar" Micro $testfile
  echo "" # newline
done
