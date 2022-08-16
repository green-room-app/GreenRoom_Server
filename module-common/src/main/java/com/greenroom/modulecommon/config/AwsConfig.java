package com.greenroom.modulecommon.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "cloud.aws.s3")
public class AwsConfig {

    private final String region;

    private final String url;

    private final String bucket;

    private final String accessKey;

    private final String secretKey;
}
