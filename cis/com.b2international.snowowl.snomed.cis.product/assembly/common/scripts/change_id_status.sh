#!/bin/bash
new_status=''

print_usage() {
  echo "Usage: 
   -a:    Sets id status to available
   -p:    Sets id status to published 
  "
}

while getopts 'ap' flag; do
  case "${flag}" in
    a) new_status='Available' ;;
    p) new_status='Published' ;;
    *) print_usage
       exit 1 ;;
  esac
done

for i in `cat idlist`; do 
    curl \
    -H "Content-Type: application/json" \
    "http://localhost:9200/snomedids-sctid/_update/${i}?refresh" \
    -d "{ \"doc\": { \"status\": \"${new_status}\" } }"
done