#!/bin/bash
# Author: Brian Rieder
# Date:   17 September 2016

make
cases_passed=0
cases_total=0
for testfile in `ls testcases/input/ | sort -V` ; do
  extstrip=${testfile%.micro}
  allstrip=${extstrip##*/}
  echo "Testing file: $testfile"
  if [[ $OSTYPE == "cygwin" ]] ; then
    output=$(java -cp "classes/;lib/antlr.jar" Micro testcases/input/$testfile)
    echo "Output:   $output"
    expected=$(cat testcases/output/$allstrip.out)
    echo "Expected: $expected"
    output=$(echo $output | tr -d '\r')
    if [ "$output" = "$expected" ] ; then
      printf "\033[0;32mPASSED\n\033[0m"
      (( cases_passed += 1 ))
      (( cases_total += 1 ))
    else
      printf "\033[0;31mFAILED\n\033[0m"
      (( cases_total += 1 ))
    fi
  elif [[ $OSTYPE == "linux-gnu" ]] ; then
    output=$(java -cp classes/:lib/antlr.jar Micro testcases/input/$testfile)
    echo "Output:   $output"
    expected=$(cat testcases/output/$allstrip.out)
    echo "Expected: $expected"
    output=$(echo $output | tr -d '\r')
    if [ "$output" = "$expected" ] ; then
      printf "\033[0;32mPASSED\n\033[0m"
      (( cases_passed += 1 ))
      (( cases_total += 1 ))
    else
      printf "\033[0;31mFAILED\n\033[0m"
      (( cases_total += 1 ))
    fi
  else
    echo "Error: Unsupported OSTYPE $OSTYPE"
    exit
  fi
  echo "" # newline
done
echo "Results: $cases_passed / $cases_total"
