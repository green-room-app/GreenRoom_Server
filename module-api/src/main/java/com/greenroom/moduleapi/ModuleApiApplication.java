package com.greenroom.moduleapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@EntityScan(basePackages = {"com.greenroom.modulecommon"})
@EnableJpaRepositories(basePackages = {"com.greenroom.modulecommon"})
@ConfigurationPropertiesScan(basePackages = {"com.greenroom.modulecommon", "com.greenroom.moduleapi"})
@SpringBootApplication(scanBasePackages = {"com.greenroom.modulecommon", "com.greenroom.moduleapi"})
public class ModuleApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleApiApplication.class, args);
    }

}
