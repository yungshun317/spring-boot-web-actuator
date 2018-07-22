package yungshun.chang.springbootwebactuator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class SpringBootWebActuatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebActuatorApplication.class, args);
    }

    @RequestMapping("/")
    public String index(){
        return "Spring Boot Actuator";
    }
}
