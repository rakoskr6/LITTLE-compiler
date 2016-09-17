#!/bin/bash

make
for testfile in testcases/input/* ; do
  java -cp "classes/;lib/antlr.jar" Micro $testfile
done
