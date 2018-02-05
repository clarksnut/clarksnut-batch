#!/usr/bin/env bash

mvn clean package -DskipTests
java -jar -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5006 target/clarksnut-mail-collector-1.0.0-SNAPSHOT-swarm.jar -Dswarm.port.offset=100 -Dsso.auth.server.url="$CLARKSNUT_SSO_SERVER_URL" -Dsso.clientSecret="$CLARKSNUT_CLIENT_MAIL_COLLECTOR_CLIENT_SECRET"

