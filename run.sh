#!/bin/bash
source env.sh
cd ims-db
docker-compose up -d
cd ..
mvn clean spring-boot:run
