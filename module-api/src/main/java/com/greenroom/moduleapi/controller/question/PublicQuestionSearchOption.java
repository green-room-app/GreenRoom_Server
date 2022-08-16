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
public class PublicQuestionSearchOption {
    List<Long> categories;

    public static PublicQuestionSearchOption from(String categories) {
        if (isEmpty(categories)) {
            return PublicQuestionSearchOption
                    .builder()
                    .categories(Collections.emptyList())
                    .build();
        } else {
            /**
             * <예시>
             * category = 1,2,3,4,5
             * categoryIds = [1,2,3,4,5]
             */
            List<Long> categoryIds = Arrays.stream(categories.split(","))
                    .map(Long::valueOf).collect(toList());

            return PublicQuestionSearchOption
                    .builder()
                    .categories(categoryIds)
                    .build();
        }
    }
}
