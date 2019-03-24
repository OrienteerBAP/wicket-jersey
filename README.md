[![Build Status](https://travis-ci.org/OrienteerBAP/wicket-jersey.svg?branch=master)](https://travis-ci.org/OrienteerBAP/wicket-jersey) [![Coverage Status](https://coveralls.io/repos/github/OrienteerBAP/wicket-jersey/badge.svg?branch=master)](https://coveralls.io/github/OrienteerBAP/wicket-jersey?branch=master)

# wicket-jersey
Adaptor for Apache Wicket to support JAX-RS through embedding Jersey

## Quick start

Add to your `pom.xml` the following dependency

```xml
<dependency>
	<groupId>org.orienteer.wicket-jersey</groupId>
	<artifactId>wicket-jersey</artifactId>
	<version>1.0-SNAPSHOT</version>  <!-- Put required version-->
</dependency>
```

Use class `org.orienteer.wicketjersey.WicketJersey` to mount required REST resources  in `Application.init()`.
For example:

```java
WicketJersey.mount(new MyApplication());
//OR
WicketJersey.mount("/api", new MyApplication());
//OR
WicketJersey.mount("/rest", "org.orienteer.wicketjersey.demo.rest");
```

Done! 

### How to run demo

1. Clone current repository
2. Go to folder with your local repository
3. Run `mvn clean install`
4. Go to sub folder `wicker-jersey-demo`
5. Run `mvn jetty:run`
6. Open your internet browser and go to http://localhost:8080
