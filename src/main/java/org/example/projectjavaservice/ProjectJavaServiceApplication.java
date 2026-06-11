package org.example.projectjavaservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing //
public class ProjectJavaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectJavaServiceApplication.class, args);
    }

}
