## Attendees Deployment Workshop

## Overview

This repository contains the code base for a very basic attendees API which models a get request in 2 ways:

* On port 8080 as a REST over HTTP service
* On port 9090 as a gRPC service

There is also [a version 2 branch](https://github.com/jpgough/attendees/tree/v2) containing backwards compatible changes to v1 (not semver).

The two builds are provided as docker images for easy use in the rest of this guide.
These will be used in the YAML Examples for use with Istio. 

* [DockerHub Attendees Version 1 Image](https://hub.docker.com/layers/196621151/jpgough/attendees/v1/images/sha256-6e3534ba9091c379f04e77c06ee76e643d756c426f86fea498c5f2385b78e569?context=repo)
* [DockerHub Attendees Version 2 Image](https://hub.docker.com/layers/196652410/jpgough/attendees/v2/images/sha256-864888d83102dfb23d7f00e5f0309a929cc7ab7a5ce1d64ac70c3129f0d5d66d?context=repo)

### Setting up AKS

#### Pre-Requisites 

* You will need an Azure account or Azure subscription, alternatively you can feel free to deploy to your own k8s cluster

#### Steps

On Kubernetes Services create a new cluster, the following configuration worked well for me:

* Cluster Preset Configuration change to Dev/Test
* Node size change to B2ms - saving a small amount of credit
* Node count set to 2
* Review and Create

Grab a coffee as that usually takes about 5 minutes to fire up. 

The next step is to connect to the cluster using the az tool, install guide is [here](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli).
Click connect on your AKS cluster (on browser) and run the `az aks get-credentials` command.
Running this command will set up `kubectl` to connect to the cluster for you, magic!

`kubectl get pods` should now return.

### Deploying Istio 

Using Helm is probably the best way of deploying Istio onto AKS, with full instruction [here](https://istio.io/latest/docs/setup/install/helm/).
Once this has completed, you may wish to turn on autoside car injection for the default namespace:

`kubectl label namespace default istio-injection=enabled --overwrite`

### Deploying and Testing the APIs

Now Istio is up and running it is time to deploy our services and run a few tests against them. 
To create an interactive shell running inside your cluster, your can use the following command.

`kubectl run my-shell --rm -i --tty --image ubuntu -- bash`

Keep this to one side and we will use it to test the HTTP and gRPC deployments in k8s.

#### Deploying The Container

Using kubectl we can deploy version 1 and 2 of our image using [attendees-deployment.yaml](/operations/attendees-deployment.yaml):

`kubectl create -f attendees-deployment.yaml`

You can test whether this is up and running by using 

`kubectl get pods` and seeing a similar output to below

```shell
NAME                                READY   STATUS    RESTARTS   AGE
attendees-api-v1-6c75f87dfb-q6kb8   2/2     Running   0          5h3m
attendees-api-v2-6d99ff76f4-lclcn   2/2     Running   0          5h3m
my-shell                            2/2     Running   0          5h16m
```

The 2/2 of two is important to see, as this will be both the running attendees container and the Istio envoy sidecar.

#### Testing HTTP Traffic

Pods are ephemeral and therefore need a service to act as the conduit within the cluster DNS for routing to those service.
We can deploy a service using the [http-service.yaml](/operations/http-service.yaml) and we can test it using our my-shell on the cluster.

`kubectl create -f http-service.yaml`

In the my-shell interactive pod:
```shell
apt update
apt install curl
curl http://attendees-http/attendees
```

You should see that you get responses from both v1 and v2 of the service, this is standard k8s routing. 
Shortly we will use Istio to start coordinating the traffic to the different services

#### Testing gRPC Traffic

Now deploy the service using the [grpc-service.yaml](/operations/grpc-service.yaml) and test using the following in my-shell"

```shell
apt install wget
wget https://github.com/fullstorydev/grpcurl/releases/download/v1.8.6/grpcurl_1.8.6_linux_x86_64.tar.gz
tar xvfz grpcurl_1.8.6_linux_x86_64.tar.gz
./grpcurl --plaintext attendees-grpc:80 com.masteringapi.attendees.grpc.server.AttendeesService/getAttendees
```

This should now respond with the information from the gPRC API. 

### Exploring Traffic Management

In the [operations/istio](/operations/istio) folder you will find a few items to get start with experimenting with the types of service mesh routing.
We will look at two examples and then you can try your own:

First lets introduce the [destination-rule.yaml](/operations/istio/destination-rule.yaml), which will allow us to identify the two versions.
You can find out more about [Destination Rules here](https://istio.io/latest/docs/reference/config/networking/destination-rule/).

`kubectl create -f destination-rule.yaml`

Testing the HTTP service now will show a random routing rather than a round-robin routing that we saw previously.
We can tweak exactly hor routing occurs using a [Virtual Service](https://istio.io/latest/docs/reference/config/networking/virtual-service/).
This allows us to create rules around how traffic is routed, in this example we split the load 90:10 across v1 and v2.

`kubectl create -f weighted-virtual-service.yaml`

There is also a further example that introduces [fault-virtual-service](/operations/istio/fault-virtual-service.yaml).ß
