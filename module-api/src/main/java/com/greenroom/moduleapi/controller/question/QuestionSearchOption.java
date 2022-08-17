package com.greenroom.moduleapi.controller.question;

import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Getter
@Builder
public class QuestionSearchOption {
    List<Long> categories;
    private String title;
    private boolean refQuestion;
    private boolean keyword;

    public static QuestionSearchOption of(String title, String category, boolean refQuestion, boolean keyword) {
        if (isEmpty(category)) {
            return QuestionSearchOption.builder()
                    .title(title)
                    .categories(Collections.emptyList())
                    .refQuestion(refQuestion)
                    .keyword(keyword)
                    .build();
        } else {
            /**
             * <예시>
             * queryString = ?title=살면서&category=1,2,3,4,5
             * category = 1,2,3,4,5
             * categoryIds = [1,2,3,4,5]
             */
            List<Long> categoryIds = Arrays.stream(category.split(","))
                    .map(Long::valueOf).collect(toList());

            return QuestionSearchOption.builder()
                    .title(title)
                    .categories(categoryIds)
                    .refQuestion(refQuestion)
                    .keyword(keyword)
                    .build();
        }
    }
}
