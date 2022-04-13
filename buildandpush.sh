#!/bin/sh
mvn clean install
HOST=$(oc get route default-route -n openshift-image-registry --template='{{ .spec.host }}')
docker login -u $(oc whoami) -p $(oc whoami -t) $HOST
docker build -f src/main/docker/Dockerfile.jvm -t $HOST/address-dev/dx-sample-app:1.0.0 .
helm upgrade --install dx-sample-app --set is.enabled=false --set istag.enabled=false chart/
helm upgrade --install dx-sample-app --set is.enabled=false --set istag.enabled=false chart/ --autoscaling.replica=20
helm upgrade --install push_app --set is.enabled=false --set istag.enabled=false chart/ --autoscaling.replica=20
helm upgrade --install dx-sample-app --set --image="push.app:1.0" is.enabled=false --set istag.enabled=false chart/ --autoscaling.replica=20

docker push  $HOST/address-dev/dx-sample-app:1.0.0
