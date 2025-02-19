#!/bin/bash

BACKUP_DIR="/opt/backup"
TIMESTAMP=$(date +'%Y%m%d_%H%M%S')
BACKUP_FILE="${BACKUP_DIR}/backup_${TIMESTAMP}.zip"
sudo mkdir -p $BACKUP_DIR

process=/opt/bwg-service/bwg-service.jar
if pgrep -f "$process" > /dev/null; then
 bwg_pid=$(pgrep -f "$process")
 sudo kill -9 $bwg_pid
 sudo zip -r $BACKUP_FILE /opt/bwg-service
else
 echo "Process Not Running"
 exit
fi