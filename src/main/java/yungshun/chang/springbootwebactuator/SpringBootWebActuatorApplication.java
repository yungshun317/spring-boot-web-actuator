package yungshun.chang.springbootwebactuator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import yungshun.chang.springbootwebactuator.repository.PersonRepository;

import javax.annotation.PostConstruct;

@RestController
@SpringBootApplication
public class SpringBootWebActuatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebActuatorApplication.class, args);
    }

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

    private static final Logger log = LoggerFactory.getLogger(SpringBootWebActuatorApplication.class);

    @Bean
    CommandLineRunner findAll(PersonRepository repo){
        return args ->{
            log.info("> Persons in Database: ");
            repo.findAll().forEach(person -> log.info(person.toString()));
        };
    }
}
