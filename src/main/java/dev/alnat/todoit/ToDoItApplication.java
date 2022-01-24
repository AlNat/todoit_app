package dev.alnat.todoit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("dev.alnat.todoit.model")
@EnableJpaRepositories("dev.alnat.todoit.repository")
@SpringBootApplication
public class ToDoItApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToDoItApplication.class, args);
    }

}
