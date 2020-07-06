#!/bin/bash

if [[ $# -eq 0 ]] ; then
    echo 'This script needs the the target hybris path as parameter. Aborting.'
    exit 0
fi

HYBRIS_PATH=$1
TEMPLATE_PATH="./1905.0/yacceleratorstorefront"
TARGET_PATH="${HYBRIS_PATH}/hybris/bin/modules/base-accelerator"

echo "Starting modifying templates.."

cp -avr ${TEMPLATE_PATH} ${TARGET_PATH}

echo "Finished modifying templates."
