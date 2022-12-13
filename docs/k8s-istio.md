# Deploying to Kubernetes and Istio

In this tutorial we will deploy the attendees service to both Kubernetes and Istio.
This will allow you to explore the deployment and figure out how APIs work in this environment.
This is not intended to be a complete introduction to Kubernetes or Istio.

## Pre-reqs

Required Tools:   

- [kubectl](https://kubernetes.io/docs/tasks/tools/#kubectl)
- [istioctl](https://istio.io/latest/docs/setup/getting-started/#download)

Optional Tools: 

If you want to deploy an out-of-the-box setup with Azure you will need the following along with an Azure account: 

- [az](https://docs.microsoft.com/en-us/cli/azure/)

## Optional: Deploying a Kubernetes Cluster on Azure

Running the following commands from the root of the project will set up a Kubernetes cluster with a single node in Azure.
* The first command will create a resource group in UK South.
* The second command will use the [templates](/operations/azure/template) to configure a Kubernetes cluster in the resource group.
* The final step will merge the context of your application into `kubectl` allowing you to run commands using the CLI against the cluster.

```
az group create --name mastering-api --location "UK South"
az deployment group create --name MasteringAPI --resource-group mastering-api --template-file operations/azure/template/template.json --parameters operations/azure/template/parameters.json

#Wait for k8s cluster to create
az aks get-credentials --resource-group mastering-api --name mastering-api
```

When you have finished with the demonstration, you can clean up everything with the following command:    

`az group delete --resource-group mastering-api`

## Step 1: Deploying Attendees to Kubernetes

### Deployment

The [attendees-deployment.yaml](/operations/k8s/attendees-deployment.yaml) describes the [deployment](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/) of the attendees service.
By default, this configuration will use the [image on DockerHub](https://hub.docker.com/repository/docker/masteringapi/attendees).
Note that we have both ports 8080 and 9090 open for the REST and gRPC service respectively.   

`kubectl apply -f operations/k8s/attendees-deployment.yaml`   

We can validate that this has deployed successfully by running `kubectl get pods`, which should show something like:

```
NAME                             READY   STATUS    RESTARTS   AGE
attendees-api-7b4d4cb7cb-rhdss   1/1     Running   0          27s
```

Feel free to play around with updating the number of replicas or possibly killing the pod.
Pods are ephemeral, meaning that we should use service to locate our deployments at runtime.

### Services

A [Kubernetes Service](https://kubernetes.io/docs/concepts/services-networking/service/) allows you to specify discovery of pods at runtime and can be used directly for DNS routing in the cluster.
It is also possible to set the type of the service to be `LoadBalancer`, creating an IP address external to the cluster.
The two services in the examples are:

- [HTTP Service](/operations/k8s/http-service.yaml)
- [gRPC Service](/operations/k8s/grpc-service.yaml)

```
kubectl apply -f operations/k8s/grpc-service.yaml 
kubectl apply -f operations/k8s/http-service.yaml 
```

You can find where the IP address of the services are located (note that external IP addresses can take a minute or two to allocate).   
`kubectl get svc`

```
NAME             TYPE           CLUSTER-IP     EXTERNAL-IP   PORT(S)        AGE
attendees-grpc   LoadBalancer   10.0.84.235    20.108.87.2   80:32632/TCP   13s
attendees-http   LoadBalancer   10.0.139.185   20.108.87.17  80:30105/TCP   3s
```

In the above example you can now query this API directly by hitting the HTTP URL from a browser, e.g. `http://20.108.87.17/swagger-ui/index.html`

Using [gRPC UI](https://github.com/fullstorydev/grpcui) you can use the reflection service and invoke endpoints:

```
grpcui --plaintext 20.108.87.2:80
```

### Deploy Istio

```shell
kubectl create namespace istio-system
istioctl install
kubectl label namespace default istio-injection=enabled
```

Istio will now be installed to your cluster.
If you rollout our deployment, you will see Istio start to take over some of the standard k8s control.

```
$kubectl get pods
NAME                             READY   STATUS    RESTARTS   AGE
attendees-api-7b4d4cb7cb-rhdss   1/1     Running   0          18m

$kubectl rollout restart deployment attendees-api 
deployment.apps/attendees-api restarted

$kubectl get pods
NAME                             READY   STATUS        RESTARTS   AGE
attendees-api-57df7445f6-mwxbk   2/2     Running       0          9s
attendees-api-7b4d4cb7cb-rhdss   1/1     Terminating   0          18m
```

You can see that the new pod has 2/2 containers running, the new container running in the pod is the Istio sidecar.
Let's make a few changes so that we can see Istio's traffic management impact, for simplicity we will just look at HTTP for now.

### Take over with Istio

In order to take full advantage of Istio, our first job is to expose the attendees service on the Istio Ingress Gateway.
You can do this by applying the [attendees-gateway.yaml](/operations/istio/attendees-gateway.yaml).   

`kubectl apply -f operations/istio/attendees-gateway.yaml`

Once this has deployed you can execute the following commands to run a request against the /attendees endpoint.

```
export INGRESS_NAME=istio-ingressgateway  
export INGRESS_NS=istio-system
export INGRESS_HOST=$(kubectl -n "$INGRESS_NS" get service "$INGRESS_NAME" -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
export INGRESS_PORT=$(kubectl -n "$INGRESS_NS" get service "$INGRESS_NAME" -o jsonpath='{.spec.ports[?(@.name=="http2")].port}')

curl "http://$INGRESS_HOST:$INGRESS_PORT/attendees"
```

#### Fault Injection

One useful feature of service mesh is the fine grain ability to control traffic. 
In this next demo we will introduce a 7s delay to all requests on the gateway.
You can see the configuration in the [delayed-gateway.yaml](/operations/istio/delayed-gateway.yaml).   

`kubectl apply -f operations/istio/delayed-gateway.yaml`   

We can try another example where 50% (approximately) of requests will fail with a 500 status.
You can see the configuration in the [half-broken.yaml](/operations/istio/half-broken-gateway.yaml).  

`kubectl apply -f operations/istio/half-broken-gateway.yaml`  

### Carry on Exploring

At this point you have a fully configured, setup cluster capable of running attendees on Kubernetes and Istio.
You can try out some more of the mesh features and possibly try releasing a new version of the attendees service and routing between the two.
