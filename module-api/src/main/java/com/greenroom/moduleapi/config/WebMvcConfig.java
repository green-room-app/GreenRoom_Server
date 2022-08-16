package com.greenroom.moduleapi.config;

import com.greenroom.moduleapi.config.support.PublicQuestionSearchArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(publicQuestionSearchArgumentResolver());
    }

    @Bean
    public PublicQuestionSearchArgumentResolver publicQuestionSearchArgumentResolver() {
        return new PublicQuestionSearchArgumentResolver();
    }
}
