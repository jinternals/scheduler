# Scheduler

[![Build Status](https://travis-ci.org/jinternals/scheduler.svg?branch=master)](https://travis-ci.org/jinternals/scheduler)
[![Coverage Status](https://coveralls.io/repos/github/jinternals/scheduler/badge.svg?branch=master)](https://coveralls.io/github/jinternals/scheduler?branch=master)

### 1. Build Code
```shell script
mvn clean install
```

### 2. Build Docker Image
```shell script

docker build -t jinternals/scheduler -f ./target/docker-resources/Dockerfile target/
```
