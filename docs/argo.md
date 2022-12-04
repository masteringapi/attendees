Follow instructions here: https://argoproj.github.io/argo-rollouts/installation/

kubectl create -f k8s-rollout-demo.yaml

```shell
kubectl argo rollouts set image rollouts-demo attendees=jpgough/attendees:v2
kubectl argo rollouts promote rollouts-demo

#Terminate 
 kubectl argo rollouts abort rollouts-demo
```

kubectl argo rollouts dashboard