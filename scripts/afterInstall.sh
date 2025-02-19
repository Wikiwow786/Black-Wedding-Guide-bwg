#!/bin/bash

# Service name
SERVICE="bwg-service"

# Check if the service exists and get its status
if sudo service "$SERVICE" status > /dev/null 2>&1; then
  echo "Service $SERVICE exists."
else
  echo "Service $SERVICE does not exist."
  sudo chmod o+x /opt/bwg-service/bwg-service.jar
  sudo ln -s /opt/bwg-service/bwg-service.jar /etc/init.d/bwg-service
  sudo ln -s /etc/init.d/bwg-service /etc/rc.d/bwg-service
  sudo chkconfig --add bwg-service
  sudo chkconfig bwg-service on
fi






