# Mastering API Architecture Attendees 

GitHub: https://github.com/masteringapi/attendees

Welcome to the primary repository accompanyting the book [Mastering API Architecture](https://www.oreilly.com/library/view/mastering-api-architecture/9781492090625/).
We welcome all feedback as you are working through the book, if you would like to see anything additional please raise an issue.

## What is in this Repo?

In this repo you will find Chapter 1's Attendees API, built in Java Spring.
Using Apache Maven you can build the project or run the docker image (detail below) to experiment with

* On port 8080 a HTTP service
* On port 9090 a gRPC service

### Building and Running using Maven

* In order to run in your IDE or via Maven, you need to generate the gRPC classes using `mvnw package`
* `mvn spring-boot:run`

### Building using Docker

The [Dockerfile](/Dockerfile) contains a multistage build, this will both compile the java code and create an image.
You can simply run `docker build -t <your-org>/attendees .`   
Once the build has completed run using `docker run -p 8080:8080 -p 9090:9090 <your-org>/attendees`

### Running using DockerHub

You can run the latest example directly from our [DockerHub](https://hub.docker.com/r/masteringapi/attendees) using:      
`docker run -p 8080:8080 -p 9090:9090 masteringapi/attendees`.

## Querying the Attendees Service

### REST

You can find the OpenAPI Specification documented on the live instance: http://localhost:8080/swagger-ui.html.   
Clicking Try It Out will hit the endpoint.

### gRPC

Using [gRPC UI](https://github.com/fullstorydev/grpcui) you can use the reflection service and invoke endpoints:

```bash
grpcui --plaintext localhost:9090
```

## Next steps

* [Deploying with Kubernetes and Istio](/docs/k8s-istio.md)   

_More examples coming soon, please see the issues for the upcoming sections and raise an issue if something is missing that would be helpful for you._