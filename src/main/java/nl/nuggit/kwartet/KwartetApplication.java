package nl.nuggit.kwartet;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KwartetApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(KwartetApplication.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "8081"));
        app.run(args);
    }

}
