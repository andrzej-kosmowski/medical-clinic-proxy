# Medical Clinic Proxy

Microservice that sits in front of the [Medical Clinic API](https://github.com/andrzej-kosmowski/medical-clinic) and exposes the operations needed by patient-facing clients: finding doctors, searching visits and booking them.
It calls the downstream API through a declarative Feign client and protects callers from its outages with a circuit breaker and a fallback.

## Features

- Doctors by specialization
- Visits of a patient or a doctor, available slots of a doctor, visit search by specialization and date or time range
- Assigning a patient to a visit and deleting a visit
- **Resilience**: Resilience4j circuit breaker with a fallback factory
  - business errors from the downstream API (for example "patient not found") are passed through to the caller
  - outages and timeouts end in a controlled "service unavailable" response instead of a raw exception
- Custom error decoder that maps downstream error responses to proxy exceptions, handled by a global exception handler
- OpenAPI (Swagger) documentation, structured JSON logging for ELK

## Tech stack

Java 21, Spring Boot, Spring Cloud OpenFeign (OkHttp), Resilience4j, springdoc-openapi, Logback with Logstash encoder, Docker.
Tests: JUnit 5, Mockito, MockMvc, WireMock.

## How it works

```
client -> [ controller -> service -> Feign client ] -> Medical Clinic API
                                          |
                          circuit breaker + fallback factory
                          error decoder -> proxy exceptions -> exception handler
```

## Getting started

Requirements: JDK 21, Docker, a running Medical Clinic API.

```bash
# 1. Build the jar (the Dockerfile copies it from target/)
./mvnw clean package -DskipTests

# 2. Create the shared networks (once, skip if they already exist)
docker network create elk-network
docker network create medical-network

# 3. Start the proxy (the Medical Clinic API must run on medical-network)
docker compose up --build
```

| Service | URL |
| --- | --- |
| Proxy | http://localhost:8084 |
| Swagger UI | http://localhost:8084/swagger-ui.html |

When run without Docker (`./mvnw spring-boot:run`) the proxy listens on port 8081 and expects the Medical Clinic API at `http://localhost:8080`. Change it with `app.medical-clinic.url`.

## Endpoints

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/doctors/specialization/{specialization}` | Doctors with a given specialization |
| `GET` | `/visits/patient/{patientId}` | Visits of a patient |
| `GET` | `/visits/doctor/{doctorId}` | Visits of a doctor |
| `GET` | `/visits/doctor/{doctorId}/available` | Free slots of a doctor |
| `GET` | `/visits/search` | Search by `specialization`, `date`, `from`, `to`, `availableOnly` |
| `PATCH` | `/visits/{visitId}/patient/{patientId}` | Book a visit for a patient |
| `DELETE` | `/visits/{visitId}` | Delete a visit |

## Tests

```bash
./mvnw test
```

Besides service and controller tests, the Feign client is tested against WireMock stubs (JSON fixtures for successful and error responses), so the error decoder is verified without running the real API.
