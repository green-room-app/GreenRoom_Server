package com.greenroom.moduleapi.controller.interview;

import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Getter
@Builder
public class InterviewQuestionSearchOption {
    List<Long> categories;
    private String title;

    public static InterviewQuestionSearchOption of(String title, String category) {
        if (isEmpty(category)) {
            return InterviewQuestionSearchOption.builder()
                    .title(title)
                    .categories(Collections.emptyList())
                    .build();
        } else {
            /**
             * <예시>
             * queryString = ?title=취미&category=1,2,3,4,5
             * category = 1,2,3,4,5
             * categoryIds = [1,2,3,4,5]
             */
            List<Long> categoryIds = Arrays.stream(category.split(","))
                    .map(Long::valueOf).collect(toList());

            return InterviewQuestionSearchOption.builder()
                    .title(title)
                    .categories(categoryIds)
                    .build();
        }
    }
}
