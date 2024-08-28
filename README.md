MockWebServer problem that appeared after updating from Spring Boot 3.3.2 to 3.3.3.

To reproduce it - go to project directory and run maven command:
mvn clean test

The following error appears:
java.lang.IllegalArgumentException: invalid version format: VERSION=9HTTP/1.1

It looks the HTTP version attribute of last request is prepended with the body of the previous request causing the exception.

It works without exception in Spring Boot 3.3.2 but appears in 3.3.3.

It can be fixed in Spring Boot 3.3.3 by changing line in ControllerTest from:
return new MockResponse().setResponseCode(204).setBody("version=9");

to
return new MockResponse().setResponseCode(204);

Why the body is accepted in Spring Boot 3.3.2 but not in 3.3.3?