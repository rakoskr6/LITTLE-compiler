#!/bin/bash
# Author: Brian Rieder
# Date:   7 October 2016

make
cases_passed=0
cases_total=0
for testfile in `ls testcases/input/*.micro | sort -V` ; do
  extstrip=${testfile%.micro}
  allstrip=${extstrip##*/}
  echo "Testing file: $testfile"

  if [[ $OSTYPE == "cygwin" ]] ; then
    java -cp "classes/;lib/antlr.jar" Micro $testfile > tmpfile_deleteme   
    output=$(tiny tmpfile_deleteme < $extstrip.input | head -1)
    expected=$(tiny testcases/output/$allstrip.out < $extstrip.input | head -1) 
    diff -y <(echo $output) <(echo $expected)
    if [ $? -eq 0 ] ; then
      printf "\033[0;32mPASSED\n\033[0m"
      (( cases_passed += 1 ))
      (( cases_total += 1 ))
    else
      printf "\033[0;31mFAILED\n\033[0m"
      (( cases_total += 1 ))
    fi
    rm tmpfile_deleteme
  elif [[ $OSTYPE == "linux-gnu" ]] ; then
    java -cp classes/:lib/antlr.jar Micro $testfile > tmpfile_deleteme
    output=$(tiny tmpfile_deleteme < $extstrip.input | head -1)
    expected=$(tiny testcases/output/$allstrip.out < $extstrip.input | head -1) 
    diff -y <(echo $output) <(echo $expected)
    if [ $? -eq 0 ] ; then
      printf "\033[0;32mPASSED\n\033[0m"
      (( cases_passed += 1 ))
      (( cases_total += 1 ))
    else
      printf "\033[0;31mFAILED\n\033[0m"
      (( cases_total += 1 ))
    fi
    rm tmpfile_deleteme
  else
    echo "Error: Unsupported OSTYPE $OSTYPE"
    exit
  fi
  echo "" # newline
done
echo "Results: $cases_passed / $cases_total"

