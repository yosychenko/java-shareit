#!/bin/bash
mkdir -p ./logs
chmod -R 777 ./logs
docker compose up --build -d
