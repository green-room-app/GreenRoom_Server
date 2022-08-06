package com.greenroom.modulecommon.config;

import com.greenroom.modulecommon.util.MessageUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * 프로젝트에서 공통으로 사용하는 Util Class Bean 모음
 */
@Configuration
public class UtilConfig {

    @Bean
    public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
        MessageUtils.setMessageSourceAccessor(messageSourceAccessor);
        return messageSourceAccessor;
    }
}
