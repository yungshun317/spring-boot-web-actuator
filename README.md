# Spring Boot Web Actuator

[![Powered by Spring](https://img.shields.io/badge/Powered%20by-Spring-blue.svg)](https://img.shields.io/badge/Powered%20by-Spring-blue.svg) [![Made with JAVA](https://img.shields.io/badge/Made%20with-JAVA-red.svg)](https://img.shields.io/badge/Made%20with-JAVA-red.svg) [![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

The goal of this project is learning how to use Actuator to monitor the Spring Boot Application. Also, we are going to discuss custom metrics with Micrometer and perform database migrations with Flyway or Liquibase.

## spring-boot-starter-actuator

In essence, Actuator brings production-ready features to our application. Monitoring our app, gathering metrics, understanding traffic or the state of our database becomes trivial with this dependency.

Actuator is mainly used to expose operational information about the running application, including health, metrics, info, dump, env, etc. It uses HTTP endpoints or JMX beans to enable us to interact with it. 

Once this dependency is on the classpath several endpoints are available for us out of the box. Run `SpringBootWebActuatorApplication`. Go to `localhost:8080/actuator`.
```sh
{"_links":{"self":{"href":"http://localhost:8080/actuator","templated":false},"auditevents":{"href":"http://localhost:8080/actuator/auditevents","templated":false},"beans":{"href":"http://localhost:8080/actuator/beans","templated":false},"health":{"href":"http://localhost:8080/actuator/health","templated":false},"conditions":{"href":"http://localhost:8080/actuator/conditions","templated":false},"shutdown":{"href":"http://localhost:8080/actuator/shutdown","templated":false},"configprops":{"href":"http://localhost:8080/actuator/configprops","templated":false},"env-toMatch":{"href":"http://localhost:8080/actuator/env/{toMatch}","templated":true},"env":{"href":"http://localhost:8080/actuator/env","templated":false},"flyway":{"href":"http://localhost:8080/actuator/flyway","templated":false},"info":{"href":"http://localhost:8080/actuator/info","templated":false},"logfile":{"href":"http://localhost:8080/actuator/logfile","templated":false},"loggers":{"href":"http://localhost:8080/actuator/loggers","templated":false},"loggers-name":{"href":"http://localhost:8080/actuator/loggers/{name}","templated":true},"heapdump":{"href":"http://localhost:8080/actuator/heapdump","templated":false},"threaddump":{"href":"http://localhost:8080/actuator/threaddump","templated":false},"metrics":{"href":"http://localhost:8080/actuator/metrics","templated":false},"metrics-requiredMetricName":{"href":"http://localhost:8080/actuator/metrics/{requiredMetricName}","templated":true},"scheduledtasks":{"href":"http://localhost:8080/actuator/scheduledtasks","templated":false},"httptrace":{"href":"http://localhost:8080/actuator/httptrace","templated":false},"mappings":{"href":"http://localhost:8080/actuator/mappings","templated":false}}}
```

### metrics

Let's explore some interesting endpoints. For example, `http://localhost:8080/actuator/metrics`:
```sh
{"names":["jvm.memory.max","jdbc.connections.active","process.files.max","jvm.gc.memory.promoted","tomcat.cache.hit","system.load.average.1m","tomcat.cache.access","jvm.memory.used","jvm.gc.max.data.size","jdbc.connections.max","jdbc.connections.min","jvm.gc.pause","jvm.memory.committed","system.cpu.count","logback.events","counter.index.invoked","tomcat.global.sent","jvm.buffer.memory.used","tomcat.sessions.created","jvm.threads.daemon","system.cpu.usage","jvm.gc.memory.allocated","tomcat.global.request.max","hikaricp.connections.idle","hikaricp.connections.pending","tomcat.global.request","tomcat.sessions.expired","hikaricp.connections","jvm.threads.live","jvm.threads.peak","tomcat.global.received","hikaricp.connections.active","hikaricp.connections.creation","process.uptime","tomcat.sessions.rejected","process.cpu.usage","tomcat.threads.config.max","jvm.classes.loaded","hikaricp.connections.max","hikaricp.connections.min","jvm.classes.unloaded","tomcat.global.error","tomcat.sessions.active.current","tomcat.sessions.alive.max","jvm.gc.live.data.size","tomcat.servlet.request.max","hikaricp.connections.usage","tomcat.threads.current","tomcat.servlet.request","hikaricp.connections.timeout","process.files.open","jvm.buffer.count","jvm.buffer.total.capacity","tomcat.sessions.active.max","hikaricp.connections.acquire","tomcat.threads.busy","process.start.time","tomcat.servlet.error"]}```

Then, choose one of these metrics, such as `jvm.memory.max`. Go to `http://localhost:8080/actuator/metrics/jvm.memory.max`:
```sh
{"name":"jvm.memory.max","measurements":[{"statistic":"VALUE","value":3.395289087E9}],"availableTags":[{"tag":"area","values":["heap","nonheap"]},{"tag":"id","values":["Compressed Class Space","PS Survivor Space","PS Old Gen","Metaspace","PS Eden Space","Code Cache"]}]}
```

Note that `counter.index.invoked` is a custom metrics created by ourselves with `micrometer`. `CounterService` in the [Pro Spring Boot](https://github.com/Apress/pro-spring-boot) book has been deprecated. Micrometer is the metrics collection facility included in Spring Boot 2’s Actuator.
```java
@Autowired
private MeterRegistry registry;
private Counter counter;

@PostConstruct
public void init() {
    counter = Counter
            .builder("counter.index.invoked")
            .description("indicates invoked counts of the index page")
            .tags("pageviews", "index")
            .register(registry);
}

@RequestMapping("/")
public String index(){
    counter.increment(1.0);
    return "Spring Boot Actuator";
}
```

Go to `http://localhost:8080/actuator/metrics/counter.index.invoked`.
```sh
{"name":"counter.index.invoked","measurements":[{"statistic":"COUNT","value":0.0}],"availableTags":[{"tag":"pageviews","values":["index"]}]}
```

After we visit the index page, `http://localhost:8080`, once. The value should becomes:
```sh
{"name":"counter.index.invoked","measurements":[{"statistic":"COUNT","value":1.0}],"availableTags":[{"tag":"pageviews","values":["index"]}]}
```

### info

These properties in `application.properties` will be shown in `http://localhost:8080/actuator/info`.
``` sh
info.app.name=Spring Boot Web Actuator Application
info.app.description=This is an example of the Actuator module
info.app.version=1.0.0
```

### logfile

If you configure `logging.file=mylog.log` in `application.properties`, you can get the content from `/logfile`.

Some endpoints, such as `/env`, `/configprops`, `/health`, `/info`, `/httptrace`, should be useful in some cases.

### shutdown

If you set these properties in `application.properties`, you can shutdown the application from the `/shutdown` endpoint.
```sh
management.endpoints.web.exposure.include=*
management.endpoint.shutdown.enabled=true
```

But you must send a POST request not a GET operation.
```sh
~/workspace/java/spring-boot-web-actuator$ curl -X POST localhost:8080/actuator/shutdown
{"message":"Shutting down, bye..."}
```

## Flyway

You can get full control of your database by versioning your schemas using database migration scripts with Flyway. Both Flyway and Liquibase implement the concept of **evolutionary database**.

Create 2 `.sql` files in the `resources/db/migration` directory. The first one, `V1__init.sql`, defines the table schema, creates the table, and inserts one record.
```sql
DROP TABLE IF EXISTS PERSON;

CREATE TABLE PERSON (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY,
	first_name varchar(255) not null,
	last_name varchar(255) not null
);

insert into PERSON (first_name, last_name) values ('Red', 'Lobster');
```

And the second one, `V2__init.sql`, inserts more records.
```sql
insert into PERSON (first_name, last_name) values ('Ronald', 'McDonald');
insert into PERSON (first_name, last_name) values ('Jack', 'InTheBox');
insert into PERSON (first_name, last_name) values ('Carl', 'Jr');
```

Set `spring.jpa.hibernate.ddl-auto=validate` in `application.properties`. Then run `SpringBootWebActuatorApplication`.
```sh
INFO 24068 --- [           main] y.c.s.SpringBootWebActuatorApplication   : > Persons in Database: 
INFO 24068 --- [           main] o.h.h.i.QueryTranslatorFactoryInitiator  : HHH000397: Using ASTQueryTranslatorFactory
INFO 24068 --- [           main] y.c.s.SpringBootWebActuatorApplication   : Person (firstName=Red, lastName=Lobster)
INFO 24068 --- [           main] y.c.s.SpringBootWebActuatorApplication   : Person (firstName=Ronald, lastName=McDonald)
INFO 24068 --- [           main] y.c.s.SpringBootWebActuatorApplication   : Person (firstName=Jack, lastName=InTheBox)
INFO 24068 --- [           main] y.c.s.SpringBootWebActuatorApplication   : Person (firstName=Carl, lastName=Jr)
```

Go to `http://localhost:8080/actuator/flyway`.
```sh
{"contexts":{"application":{"flywayBeans":{"flyway":{"migrations":[{"type":"SQL","checksum":-1607907871,"version":"1","description":"init","script":"V1__init.sql","state":"SUCCESS","installedBy":"SA","installedOn":"2019-08-24T09:36:01.071Z","installedRank":1,"executionTime":10},{"type":"SQL","checksum":23347749,"version":"2","description":"init","script":"V2__init.sql","state":"SUCCESS","installedBy":"SA","installedOn":"2019-08-24T09:36:01.089Z","installedRank":2,"executionTime":1}]}},"parentId":null}}}
```

## Liquibase

Liquibase is another option of database migrations.

One of the requirements of Liquibase is `resources/db/changelog/db.changelog-master.yaml`. In this file you have 2 `changeSet`s:
* The first one will create the table with their columns and types.
* The second one will insert a record in the table.
```yaml
databaseChangeLog:
  - changeSet:
      id: 1
      author: mrfood
      changes:
        - createTable:
            tableName: person
            columns:
              - column:
                  name: id
                  type: int
                  autoIncrement: true
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: first_name
                  type: varchar(255)
                  constraints:
                    nullable: false
              - column:
                  name: last_name
                  type: varchar(255)
                  constraints:
                    nullable: false
  - changeSet:
      id: 2
      author: mrfood
      changes:
        - insert:
            tableName: person
            columns:
              - column:
                  name: first_name
                  value: Bobs
              - column:
                  name: last_name
                  value: Burguers
```
Set `spring.jpa.hibernate.ddl-auto=none` in `application.properties` to avoid JPA generating your table; this now should be handled by Liquibase.

Run `SpringBootWebActuatorApplication`.
```sh
INFO 1363 --- [           main] y.c.s.SpringBootWebActuatorApplication   : > Persons in Database: 
INFO 1363 --- [           main] o.h.h.i.QueryTranslatorFactoryInitiator  : HHH000397: Using ASTQueryTranslatorFactory
INFO 1363 --- [           main] y.c.s.SpringBootWebActuatorApplication   : Person (firstName=Bobs, lastName=Burguers)
```
The record inserted by the second `changeSet` is available.

Now go to `http://localhost:8080/actuator/liquibase`, you can see:
```sh
{"contexts":{"application":{"liquibaseBeans":{"liquibase":{"changeSets":[{"author":"mrfood","changeLog":"classpath:/db/changelog/db.changelog-master.yaml","comments":"","contexts":[],"dateExecuted":"2019-08-24T08:53:25.225Z","deploymentId":"6636805203","description":"createTable tableName=person","execType":"EXECUTED","id":"1","labels":[],"checksum":"7:b8f2ae9c88deabd32666dff9bc5d7f5d","orderExecuted":1,"tag":null},{"author":"mrfood","changeLog":"classpath:/db/changelog/db.changelog-master.yaml","comments":"","contexts":[],"dateExecuted":"2019-08-24T08:53:25.232Z","deploymentId":"6636805203","description":"insert tableName=person","execType":"EXECUTED","id":"2","labels":[],"checksum":"7:febbfe7873884e5e1e8f309ad353f5a0","orderExecuted":2,"tag":null}]}},"parentId":null}}}
```

## Tech
The tech stack I use in this project:
* [IntelliJ](https://www.jetbrains.com/idea/) - a Java integrated development environment (IDE) for developing computer software developed by JetBrains.
* [Spring Boot](http://spring.io/projects/spring-boot) - a new way to create Spring applications with ease.
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready) - provides all of Spring Boot's production-ready features.
* [Micrometer](https://spring.io/blog/2018/03/16/micrometer-spring-boot-2-s-new-application-metrics-collector) - Spring Boot 2's new application metrics collector.
* [Flyway](https://flywaydb.org/) - provides version control for databases, robust schema evolution across all environments with ease, pleasure and plain SQL.
* [Liquibase](https://www.liquibase.org/) - the leading open source database change and deployment solution providing a database-independent way to deliver fast, safe, repeatable database deployments.

## Todos

Metrics and database migrations are important features in the production environment.
* Spring Boot Actuator metrics monitoring with Prometheus and Grafana in Kubernetes. Refer to this [post](https://www.callicoder.com/spring-boot-actuator-metrics-monitoring-dashboard-prometheus-grafana/).
* Create production-grade standards for building Spring Boot RESTful APIs, including modules such as Actuator, HATEOAS, Swagger, Redis, etc. 

## License
[Spring Boot Web Actuator](https://github.com/yungshun317/spring-boot-web-actuator) is released under the [MIT License](https://opensource.org/licenses/MIT) by [yungshun317](https://github.com/yungshun317).
