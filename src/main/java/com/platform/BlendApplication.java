package com.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.platform")
public class BlendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlendApplication.class, args);
    }
}
