package com.greenroom.modulecommon.event;

import lombok.Getter;
import lombok.ToString;

/**
 * 사용자가 검색어를 입력한 경우
 */
@ToString
@Getter
public class SearchEvent {

    private final String searchWord;

    public static SearchEvent from(String searchWord) {
        return new SearchEvent(searchWord);
    }

    private SearchEvent(String searchWord) {
        this.searchWord = searchWord;
    }
}
