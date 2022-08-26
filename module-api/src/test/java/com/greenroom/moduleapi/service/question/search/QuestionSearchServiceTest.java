package com.greenroom.moduleapi.service.question.search;

import org.junit.jupiter.api.Test;

import static com.greenroom.moduleapi.service.question.search.QuestionSearchService.SEARCH_WORD_PATTERN;
import static org.assertj.core.api.Assertions.assertThat;

class QuestionSearchServiceTest {

    @Test
    public void func1() {
        assertThat(SEARCH_WORD_PATTERN.matcher("띄어쓰기는 괜찮습니다").matches()).isTrue();
        assertThat(SEARCH_WORD_PATTERN.matcher("자음ㄱ이 있으면 안됩니다").matches()).isFalse();
        assertThat(SEARCH_WORD_PATTERN.matcher("한글만있어야합니다").matches()).isTrue();
        assertThat(SEARCH_WORD_PATTERN.matcher("영어 검색어도 제외입니다").matches()).isTrue();
        assertThat(SEARCH_WORD_PATTERN.matcher("This is Search word").matches()).isFalse();
    }
}