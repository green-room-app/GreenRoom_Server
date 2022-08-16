package com.greenroom.modulecommon.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@RequiredArgsConstructor
@PropertySource(value = {"classpath:/aws.properties"}, ignoreResourceNotFound = true)
public class AwsConfigLoader {
    private final AwsConfig config;
}
