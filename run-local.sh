#!/usr/bin/env bash

mvn clean package -DskipTests
java -jar -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5006 target/clarksnut-*-swarm.jar -Dswarm.port.offset=100 -Dsso.auth.server.url="http://localhost:8081/auth" -Dswarm.project.stage=peru

