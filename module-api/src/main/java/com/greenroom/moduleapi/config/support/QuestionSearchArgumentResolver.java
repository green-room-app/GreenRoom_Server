package com.greenroom.moduleapi.config.support;

import com.greenroom.moduleapi.controller.question.QuestionSearchOption;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class QuestionSearchArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String CATEGORY_PARAMETER = "category";
    public static final String TITLE_PARAMETER = "title";
    public static final String TYPE_PARAMETER = "type";
    public static final String KEYWORD_PARAMETER = "keyword";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return QuestionSearchOption.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        String categoryParam = defaultIfEmpty(webRequest.getParameter(CATEGORY_PARAMETER), "");
        String titleParam = defaultIfEmpty(webRequest.getParameter(TITLE_PARAMETER), "");
        String typeParam = defaultIfEmpty(webRequest.getParameter(TYPE_PARAMETER), "");
        String keywordParam = defaultIfEmpty(webRequest.getParameter(KEYWORD_PARAMETER), "");

        boolean isRefQuestion = isRefQuestion(typeParam);
        boolean hasKeywordOption = isEmpty(keywordParam);

        return QuestionSearchOption.of(titleParam, categoryParam, isRefQuestion, hasKeywordOption);
    }

    private boolean isRefQuestion(String questionType) {
        if (isEmpty(questionType)) {
            return true;
        }

        String toLowerCase = questionType.toLowerCase();
        return toLowerCase.equals("basic");
    }
}
