# Scheduler

### 1. Build Code
```shell script
mvn clean install
```

### 2. Build Docker Image
```shell script

docker build -t jinternals/scheduler -f ./target/docker-resources/Dockerfile target/
```
