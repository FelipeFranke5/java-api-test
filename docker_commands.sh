#!/bin/bash

CONTAINERS=$(docker ps -a -q)

if [ -n "$CONTAINERS" ]; then
  sudo docker stop $CONTAINERS
  sudo docker rm $CONTAINERS
fi