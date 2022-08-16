package com.greenroom.moduleapi.config.support;

import com.greenroom.moduleapi.controller.question.PublicQuestionSearchOption;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

public class PublicQuestionSearchArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String CATEGORY_PARAMETER = "category";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return PublicQuestionSearchOption.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        String categoryParam = defaultIfEmpty(webRequest.getParameter(CATEGORY_PARAMETER), "");

        return PublicQuestionSearchOption.from(categoryParam);
    }
}
