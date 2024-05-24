#!/bin/bash

touch keystore.properties
echo "key_store_file=$1" >> keystore.properties
echo "key_store_password=$2" >> keystore.properties
echo "alias=$3" >> keystore.properties
echo "key_password=$4" >> keystore.properties