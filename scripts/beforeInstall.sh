#!/bin/bash

sudo yum -y install java-17-amazon-corretto-headless

# Creating directories
# Directory paths
DIR1="/opt/bwg-service"
DIR2="/opt/bwg-service/config"
DIR3="/opt/bwg-service/logs"

# Check and create the first directory
if [ -d "$DIR1" ]; then
  echo "Directory $DIR1 already exists."
else
  sudo mkdir -p "$DIR1"
  echo "Directory $DIR1 created."
fi

# Check and create the second directory
if [ -d "$DIR2" ]; then
  echo "Directory $DIR2 already exists."
else
  sudo mkdir -p "$DIR2"
  echo "Directory $DIR2 created."
fi

# Check and create the second directory
if [ -d "$DIR3" ]; then
  echo "Directory $DIR3 already exists."
else
  sudo mkdir -p "$DIR3"
  echo "Directory $DIR3 created."
fi
