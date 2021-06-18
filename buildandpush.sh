#!/bin/sh
mvn clean install
HOST=$(oc get route default-route -n openshift-image-registry --template='{{ .spec.host }}')
docker login -u $(oc whoami) -p $(oc whoami -t) $HOST
docker build -f src/main/docker/Dockerfile.jvm -t $HOST/address-dev/dx-address-extract-nzad:1.0.0 .
docker push  $HOST/address-dev/dx-address-extract-nzad:1.0.0
helm upgrade --install dx-address-extract-nzad --set is.enabled=false --set istag.enabled=false chart/
