# Courier Tracking

Courier Tracking is a Spring Boot REST application that processes courier geolocation data, detects entrances into Migros store areas, and calculates the total travel distance of a courier.

## Technologies

- Java 25
- Spring Boot 4
- Spring Data JPA
- PostgreSQL
- Maven
- MapStruct
- Lombok
- Swagger / OpenAPI
- JUnit 5
- Mockito

## Features

- Processes courier geolocation events
- Persists courier location history
- Detects couriers entering within 100 meters of a Migros store
- Prevents repeated entrances to the same store within one minute
- Prevents multiple entrance records while a courier remains inside the store radius
- Persists store entrance events
- Calculates total travel distance using the Haversine formula
- Loads the provided Migros store data from `stores.json`
- Provides Swagger UI for testing the REST API

## Store Entrance Rules

A store entrance is detected when a courier transitions from outside the 100-meter store radius to inside it.

If the courier remains inside the radius, subsequent location updates are not considered new entrances.

If the courier exits and re-enters the same store radius within one minute of the last recorded entrance, the re-entry is ignored.

This outside-to-inside transition is an implementation assumption based on the interpretation of "re-entry" in the case requirements.

## Distance Calculation

Distances between consecutive courier locations are calculated using the Haversine formula.

The total travel distance is returned in meters.

## Design Patterns

### Strategy Pattern

`DistanceCalculator` defines the distance calculation strategy.

`DistanceCalculatorImpl` provides the Haversine-based implementation.

This keeps the business services independent from a specific distance calculation algorithm.

### Factory Pattern

`StoreEntranceFactory` encapsulates the creation of `StoreEntrance` objects.

This keeps object creation separate from the store entrance business logic.

## Database

The application uses PostgreSQL.

Create a database named:

`COURIER_TRACKING_DB`

Default configuration:

- URL: `jdbc:postgresql://localhost:5432/COURIER_TRACKING_DB`
- Username: `postgres`
- Password: `postgres`

The configuration can be overridden using the following environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

Database tables are created or updated automatically by Hibernate.

The Migros stores provided in `stores.json` are loaded into the database when the application starts if the store table is empty.

## Running the Application

### Prerequisites

- Java 25
- Maven
- PostgreSQL

### Build

```bash
mvn clean install
```

### Run

```bash
mvn spring-boot:run
```

The application starts on port `8080`.

## API

### Add Courier Location

```http
POST /api/couriers/locations
```

Example request:

```json
{
  "courierId": "C100",
  "timestamp": "2026-10-06T15:00:20Z",
  "latitude": 40.9923307,
  "longitude": 29.1244229
}
```

Successful response:

```text
201 Created
```

Each location event is persisted and evaluated against the configured Migros store locations.

### Get Total Travel Distance

```http
GET /api/couriers/{courierId}/distance
```

Example:

```http
GET /api/couriers/C100/distance
```

Example response:

```json
{
  "courierId": "C100",
  "totalDistanceMeters": 1358.2033173796212
}
```

## Swagger

After starting the application, Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI documentation is available at:

```text
http://localhost:8080/v3/api-docs
```

## Testing

Run all tests with:

```bash
mvn test
```

The tests cover:

- Haversine distance calculation
- Courier location processing
- Total travel distance calculation
- Store entrance detection
- Remaining inside a store radius without creating duplicate entrances
- Re-entry within one minute
- Re-entry after one minute

## Project Structure

```text
src/main/java/com/hivda/couriertracking
├── config
├── controller
├── dto
├── entity
├── exception
├── factory
├── mapper
├── repository
└── service
```

## Store Data

Store locations are initialized from:

```text
src/main/resources/stores.json
```

The provided store data is persisted when the application starts and the store table is empty.

## Notes

- Coordinates are persisted using `BigDecimal` for controlled database precision.
- Location timestamps are represented using `Instant`.
- Store proximity and travelled distances are calculated using the Haversine formula.
- Store entrance events reference the corresponding store through a database relationship.
- The current implementation loads all stores when evaluating a location event. This is sufficient for the provided case data; for a larger production dataset, caching or spatial indexing could be considered.