package com.greenroom.moduleapi.service.question.search;

import com.greenroom.moduleapi.service.greenroom.question.search.GreenRoomQuestionSearchService;
import org.awaitility.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static com.greenroom.moduleapi.service.greenroom.question.search.GreenRoomQuestionSearchService.SEARCH_WORD_PATTERN;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringBootTest
class GreenRoomQuestionSearchServiceTest {

    @SpyBean
    private GreenRoomQuestionSearchService greenRoomQuestionSearchService;

    @Nested
    @DisplayName("addWordsCount 테스트")
    class addWordsCount {

        @Test
        public void regex1() {
            assertThat(SEARCH_WORD_PATTERN.matcher("띄어쓰기는 괜찮습니다").matches()).isTrue();
            assertThat(SEARCH_WORD_PATTERN.matcher("자음ㄱ이 있으면 안됩니다").matches()).isFalse();
            assertThat(SEARCH_WORD_PATTERN.matcher("한글만있어야합니다").matches()).isTrue();
            assertThat(SEARCH_WORD_PATTERN.matcher("영어 검색어도 제외입니다").matches()).isTrue();
            assertThat(SEARCH_WORD_PATTERN.matcher("This is Search word").matches()).isFalse();
        }
    }

    @Nested
    @DisplayName("updateWordsCount 테스트")
    class updateWordsCount {

        @Test
        @DisplayName("십초마다_updateWordsCount_스케줄링이_수행된다")
        public void success1() {
            await()
                .atMost(new Duration(30, SECONDS))
                .untilAsserted(() -> verify(greenRoomQuestionSearchService,atLeast(3)).updateWordsCount());
        }
    }
}