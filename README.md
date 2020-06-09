In the project directory, you can run and start on port 8080:

### `mvn clean spring-boot:run`
or if you don't have maven localy
### `./mvnw clean spring-boot:run`

Deploy to GCP:

### `mvn appengine:run`

### `mvn -DskipTests appengine:deploy`