package com.greenroom.moduleapi.config;

import com.greenroom.moduleapi.config.support.CategorySearchArgumentResolver;
import com.greenroom.moduleapi.config.support.InterviewQuestionSearchArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(questionSearchArgumentResolver());
        argumentResolvers.add(categorySearchArgumentResolver());
    }

    @Bean
    public InterviewQuestionSearchArgumentResolver questionSearchArgumentResolver() {
        return new InterviewQuestionSearchArgumentResolver();
    }

    @Bean
    public CategorySearchArgumentResolver categorySearchArgumentResolver() {
        return new CategorySearchArgumentResolver();
    }
}
