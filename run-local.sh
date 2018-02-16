#!/usr/bin/env bash

mvn clean package -DskipTests
java -jar -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5006 target/clarksnut-*-swarm.jar -Dswarm.port.offset=100 -Dsso.auth.server.url="$CLARKSNUT_SSO_SERVER_URL" -Dsso.clientSecret="$CLARKSNUT_CLIENT_MAIL_COLLECTOR_CLIENT_SECRET" -Dswarm.project.stage=peru

