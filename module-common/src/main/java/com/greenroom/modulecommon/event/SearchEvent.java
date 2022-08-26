package com.greenroom.modulecommon.event;

import lombok.Getter;
import lombok.ToString;

/**
 * 사용자가 검색어를 입력한 경우
 */
@ToString
@Getter
public class SearchEvent {

    private final String keyword;

    public static SearchEvent from(String keyword) {
        return new SearchEvent(keyword);
    }

    private SearchEvent(String keyword) {
        this.keyword = keyword;
    }
}
