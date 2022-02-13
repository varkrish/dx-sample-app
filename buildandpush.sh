!/bin/sh
mvn clean install
HOST=$(oc get route default-route -n openshift-image-registry --template='{{ .spec.host }}')
docker login -u $(oc whoami) -p $(oc whoami -t) $HOST
docker build -f src/main/docker/Dockerfile.jvm -t $HOST/sample/dx-sample-app:1.0.0 .
docker push  $HOST/sample/dx-sample-app:1.0.0
helm upgrade --install dx-sample-app --set is.enabled=false --set istag.enabled=false chart/
