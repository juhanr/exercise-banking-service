## Introduction
Example exercise for a back-end banking service. 
Uses REST API, PostgreSQL, RabbitMQ, Java 17, Spring Framework, Gradle, Flyway, JUnit, Testcontainers, RestAssured, 
Log4j2, Docker, Lombok, Mapstruct, MyBatis (although I would've preferred Hibernate).

## Build and run
* ``gradlew build``
* ``docker-compose up``

## REST endpoints
* Add user: ``POST /accounts``
  * ``curl -u user:password -X POST http://localhost:8080/accounts -H 'Content-Type: application/json' -d '{"customerId": 1, "countryIsoCode": "EST", "currencies": ["EUR", "USD"]}'``
* Get user by ID: ``GET /accounts/{id}``
  * ``curl -u user:password -X GET http://localhost:8080/accounts/1``
* Add transaction: ``POST /transactions``
  * ``curl -u user:password -X POST http://localhost:8080/accounts -H 'Content-Type: application/json' -d '{"accountId": 1, "amount": 100, "currencyIsoCode": "EUR", "direction": "IN"}'``
* Get transactions for account: ``GET /transactions?accountId={id}``
  * ``curl -u user:password -X GET http://localhost:8080/transactions?accountId=1``

## Answers

### Explanation of important choices in your solution
* All monetary values are in cents i.e., whole numbers (long/bigint). 
  This is a "best practice" which helps avoid potential complexity and problems with floating point precision.
* Database row locking (FOR UPDATE) is used for account balance to avoid scenarios
  where parallel requests/threads may affect each other and update the balance amount incorrectly.
  As a bonus, the database-level synchronization also works well with multiple app instances.
* It's a common practice to use interfaces for service classes, but in this project I didn't see any benefit to it 
  and instead preferred better readability.
* Currently, the solution uses basic auth with a fixed user and password for the sake of simplicity.
* A unique sessionId is generated for each request, used in logs and returned with error response for better debugging.
* log4j2 was used instead of logback as the logger implementation, just because of personal preference. 
  Otherwise, the code uses SLF4J as an abstraction layer.
* Flyway is used for database schema migrations since it enables schema versioning and it's a mature and easy-to-use tool.
* Database write queries are done in transactions to enable rollbacks in case a problem appears after an update or insert.

### Estimate on how many transactions can your account application can handle per second on your development machine
* ~31 transactions per second when sending transactions for one specific account (tested with Apache JMeter).

### Describe what do you have to consider to be able to scale applications horizontally
* Multiple instances of the application must run in parallel (in cluster). 
* All instances should always have the same application version.
* The application code must be able to work in a multi-instance scenario without problems (which the current solution already should).
  In-memory state, caches and data synchronization especially must be kept in mind.
* Load balancer must be added to provide a common address and distribute traffic between all the cluster nodes.
* Another option would be to split the service into separate microservices when the project gets too large.
* It's also possible to scale the database horizontally with a master node and 1..n slave nodes.
  Read-only transactions should then be directed to the slaves and read-write transactions should be directed to the master.
* RabbitMQ cluster should be set up.
* With multiple instances the application logs will be scattered. 
  It may require a log management solution (e.g., ELK Stack) to enhance log analysis.
  
## Other notes
Some features weren't added as this is just an exercise. In reality, a proper solution should additionally:
* implement a proper authentication and authorization solution, e.g., oauth2,
* implement auditing; save the history and user info of each write operation,
* implement Swagger/OpenAPI for API documentation,
* use a separate database user, not the superadmin user,
* add comments to all database tables and columns,
* use auth for RabbitMQ,
* use a more efficient message serializer for RabbitMQ, e.g., Protobuf or CBOR.
