# Welcome to Match and Trade

Match and Trade is a website where people post their unwanted items. Next, the website suggest possible trades where you can swap items with somebody else.

## Technology Stack
* [Java JDK][1]
* Spring Boot
* RESTful Service
* [Maven][2]
* Angular 2

## Quick Start
#### Requirements
* Java JDK
* Maven

Download the source code:

`git clone https://github.com/rafasantos/matchandtrade.git`

Build the source code. The files will be generated on the `/target` folder:

`mvn package`


Run the website:

`java -jar /target/webservice-0.0.1-SNAPSHOT.jar`

Access the website:

`http://localhost:8080/swagger-ui.html`

## Unit Test and Integration Test
Unit tests and integration tests are executed via `mvn` and configured with the maven plugins `maven-surefire-plugin` and `maven-failsafe-plugin` respectively.

Run the unit tests with the command `mvn test` and the integration tests with the command `mvn verify`.

## Maven Site
Maven is also capable to generate a very useful site which describing the project dependency tree, license, plugins, and testing reports.

To generate the maven site you need to issue the command `mvn site`. Open the file `target/site/index.xml` with your web-browser.

## Development Guide
Build and run the website using maven directly:

`mvn spring-boot:run`

Run unit tests:

`mvn test`

Run integration tests (including unit tests):

`mvn verify`

Run only the integration test (excluding unit tests):

`mvn test-compile failsafe:integration-test`

Generate maven web site. Files will be generated at `/target/site`:

`mvn site`

Swagger page:

`http://localhost:8080/swagger-ui.html`

You can change the logging level with the property `logging.level.root=DESIRED_LOGGING_LEVEL` or changing the `logback.xml`.

Examples:
```
mvn spring-boot:run -Dlogging.level.root=debug
mvn test -Dlogging.level.root=debug
java -jar /target/webservice-0.0.1-SNAPSHOT.jar -Dlogging.level.root=debug
```

## Writing Unit Test and Integration Test
As mentioned on the *Unit Test and Integration Test* section, use `mvn test` to run unit tests and `mvn verify` to run integration tests.

Unit tests should be placed on the folder `src/test/*` must be atomic (does not depend on anything to run) and executed in any order. The file name also needs to end on `*UT.java`. Look at the file `src/test/java/com/matchandtrade/authentication/AuthenticationServletUT.java` for an example.

Integration tests are also placed on `src/test/*` and may not be atomic (depending on environment or other components) and must be executed within a test suite. The test suite file name needs to end on `*Suite.java` while the integration test needs to end on `*IT.java`. Look at the file `src/test/java/com/matchandtrade/rest/v1/controller/UserControllerSuite.java` for an example.

To run one single unit test use `mvn -Dtest=TEST_NAME verify`. To run one singe integration test use `mvn -Dit.test=INTEGRATION_TEST_NAME verify`. See maven [surefire][3] and [failsave][4] pulgins documentation. 


### RESTful API
The RESTful API is documented via [Swagger][5] and can be accessed from `http://localhost:8080/swagger-ui.html`
## Resourses
Generally speaking, resources and payloads have consistent formats. Furthermore, [expanding resources][7] is discouraged and multiple asynchronous calls are favored in order to load sub-resources.
## HATEOAS
The importance of HATEOAS cannot be emphasized enough. This app uses the [Spring HATEOAS][8] approach to handle hypermedia.
## Many To Many Relationships
Relationships are treated as resources similarly to what is described on this [post][6].
## Pagination
REST clients should rely on the pagination information which is included in responses with multiple results. Our pagination follows the [LinkHeader][10] specification along with [RFC5988][11]. It works similarly to [GitHub's pagination][9].

When performing a GET to resources that return an array you can pass the query parameters `_pageSize` to indicate the number of records returned by a page and `_pageNumber` to indicate which page number you want to return. Note that page numbers start at 1.

[1]: https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html "Install Java JDK"
[2]: http://maven.apache.org/install.html "Install Maven"
[3]: https://maven.apache.org/surefire/maven-surefire-plugin/examples/single-test.html "Maven surefire"
[4]: https://maven.apache.org/surefire/maven-failsafe-plugin/examples/single-test.html "Maven failsafe"
[5]: http://swagger.io/ "Swagger"
[6]: https://rafaelsantosbra.wordpress.com/2016/10/18/many-to-many-relationships-for-rest-api-with-a-relationship-attribute/ "REST API Many to Many relationship"
[7]: http://venkat.io/posts/expanding-your-rest-api/ "REST API Expand Resources"
[8]: https://spring.io/understanding/HATEOAS "Spring HATEOAS"
[9]: https://developer.github.com/guides/traversing-with-pagination/ "GitHub Pagination"
[10]: https://www.w3.org/wiki/LinkHeader "LinkHeader Specification"
[11]: http://www.rfc-editor.org/rfc/rfc5988.txt "rfc5988"
