package com.greenroom.moduleapi.controller.greenroom;

import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Getter
@Builder
public class CategorySearchOption {
    List<Long> categories;

    public static CategorySearchOption from(String category) {
        if (isEmpty(category)) {
            return CategorySearchOption.builder()
                    .categories(Collections.emptyList())
                    .build();
        } else {
            /**
             * <예시>
             * queryString = ?category=1,2,3,4,5
             * category = 1,2,3,4,5
             * categoryIds = [1,2,3,4,5]
             */
            List<Long> categoryIds = Arrays.stream(category.split(","))
                    .map(Long::valueOf).collect(toList());

            return CategorySearchOption.builder()
                    .categories(categoryIds)
                    .build();
        }
    }
}
