package com.gd.networks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages={
        "com.gd.networks", "com.gd.sql"})
@EntityScan("com.gd.sql")
@EnableJpaRepositories("com.gd.sql")
public class RestServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(RestServiceApplication.class, args);
    }

}
