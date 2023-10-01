# Weather Service Application

---


### The Weather Service Application provides any user an API
#### 'WeatherOpenAPI' provides
* To learn current weather report for requested city name.


### How does the application works?
* Users do not need to authorize themselves. 
* Application has one API which is `/v1/api/open-weather/{cityName}`, `{cityName}` is a path variable
* There is a validation for cityName parameter. CityName value can not be decimal or a blank value.
    * If the city value is not valid, api returns `400 - Http Bad Request` response
* Current weather report can be fetch either from database or WeatherStackAPI which is a OpenAPI with the API_KEY
    * If the latest data is not older than 30 minutes for that cityName value, data is fetching from database.
    * Either city does not exist or older than 30 minutes in DB, a request sends to WeatherStackAPI by application and the result puts to Cache by keys.
    * If there is a value with cityName filter by key in cache, the response is returns from cache directly
    * Caches clearing by Spring Schedule

On the swagger page you can find the relevant api endpoint.
You can reach the openapi page by `http://localhost:8080/swagger-ui/index.html` url.

You can define <b>WEATHER_STACK_API_KEY </b> in the `.env` file

## Technologies

---
- Java 17
- Spring Boot 3.1
- Open API Documentation
- Spring Data JPA
- H2 In Memory Database
- Restful API
- Maven
- Docker
- Docker Compose
- Git
- Prometheus
- Grafana


## Prerequisites

---
- Maven or Docker
---

## Docker Run
The application can be built and run by the `Docker` engine. The `Dockerfile` has multistage build, so you do not need to build and run separately.

Please follow the below directions in order to build and run the application with Docker Compose;

```sh
$ cd weather
$ docker-compose up -d
```

Docker compose creates 3 replicas (instances) of the application

#### You can reach the open-api-ui via  `http://{HOST}:{9595-9597}/swagger-ui.html`
### Prometheus
#### You can reach prometheus page via `http://{HOST}:9090`
### Grafana
#### You can reach grafana page via `http://{HOST}:3000` - GF_SECURITY_ADMIN_PASSWORD=admin

---
## Maven Run
To build and run the application with `Maven`, please follow the directions below;

```sh
$ cd weather
$ mvn clean install
$ mvn spring-boot:run
```
You can reach the swagger-ui via  `http://{HOST}:8080/swagger-ui.html`

---