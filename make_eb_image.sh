#!/bin/sh

# clear up stuff left behind from previous builds
[ -d web/target/docker ] && rm -vrf web/target/docker

# build the zip
sbt web/docker:stage && \
  cd web/target/docker && \
  zip -r ../../../app.zip *

echo "I've built app.zip as a docker image. Now upload this as new version to elasticbeanstalk, and deploy"
