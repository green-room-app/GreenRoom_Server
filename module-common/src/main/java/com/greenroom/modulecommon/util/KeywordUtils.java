package com.greenroom.modulecommon.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KeywordUtils {

    public static List<String> toKeywordList(String keywords) {
        if (isEmpty(keywords)) {
            return Collections.emptyList();
        }

        return Arrays.stream(keywords.split("-"))
                .collect(toList());
    }

    public static String toKeywords(List<String> keywordList) {
        if (keywordList == null) {
            return EMPTY;
        }

        StringBuilder sb = new StringBuilder();

        keywordList.forEach(keyword -> {
            sb.append(keyword).append("-");
        });

        String keywords = sb.toString();
        return keywords.substring(0, keywords.length() - 1);
    }
}
