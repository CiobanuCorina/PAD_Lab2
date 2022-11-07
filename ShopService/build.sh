#!/bin/bash

rm -rf ./master/data/*
rm -rf ./slave/data/*
docker-compose up -d
sleep 20

statement='CREATE USER "replication"@"%" IDENTIFIED WITH mysql_native_password BY "slave123"; GRANT REPLICATION SLAVE ON *.* TO "replication"@"%"';
docker exec mysql_master sh -c "mysql -u root -pMastermaster123 -e '$statement'"
docker exec mysql_master sh -c "mysql -u root -pMastermaster123 -e 'CREATE DATABASE store;'"
MS_STATUS=`docker exec mysql_master sh -c "mysql -u root -pMastermaster123 -e 'SHOW MASTER STATUS;'"`
CURRENT_LOG=`echo $MS_STATUS | awk '{print $6}'`
CURRENT_POS=`echo $MS_STATUS | awk '{print $7}'`
echo $CURRENT_LOG
echo $CURRENT_POS

docker exec mysql_slave sh -c "mysql -u root -pSlaveslave123 -e \"CHANGE MASTER TO MASTER_HOST='mysql_master', MASTER_USER='replication', MASTER_PASSWORD='slave123', MASTER_LOG_FILE='$CURRENT_LOG', MASTER_LOG_POS=$CURRENT_POS; START SLAVE;\""
docker exec mysql_slave sh -c "mysql -u root -pSlaveslave123 -e 'SHOW SLAVE STATUS \G'"
