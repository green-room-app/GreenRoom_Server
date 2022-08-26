package com.greenroom.moduleapi.service.question.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.data.redis.core.ScanOptions.*;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class QuestionSearchService {

    public static Pattern SEARCH_WORD_PATTERN = Pattern.compile("^[\\s|가-힣]+$");

    private static final long UPDATE_SEARCH_WORD_INTERVAL = 10000L;  //10초
    private static final long DECREASE_SEARCH_WORD_SCORE_INTERVAL = 60000L;     //1분
    private static final String SEARCH_WORDS_KEY = "search-words";

    private static final ConcurrentHashMap<String, Integer> searchWordMap = new ConcurrentHashMap<>();

    private final StringRedisTemplate redisTemplate;

    public void addWordsCount(String keyword) {
        if (isBlank(keyword)) return;
        if (!SEARCH_WORD_PATTERN.matcher(keyword).matches()) return;

        searchWordMap.put(keyword, searchWordMap.getOrDefault(keyword, 0) + 1);
    }

    @Scheduled(fixedDelay = UPDATE_SEARCH_WORD_INTERVAL)
    @Transactional
    public void updateWordsCount() {
        log.info("updateProductViewCount call");

        searchWordMap.forEach((searchWord, searchCount) -> {
            log.info("searchWord: {}, searchCount: {}", searchWord, searchCount);
            redisTemplate.opsForZSet().incrementScore(SEARCH_WORDS_KEY, searchWord, searchCount);
        });

        searchWordMap.clear();
    }

    @Scheduled(fixedDelay = DECREASE_SEARCH_WORD_SCORE_INTERVAL)
    @Transactional
    public void removeAndDecreaseWordsCount() {
        log.info("iterate call");

        redisTemplate.opsForZSet().removeRangeByScore(SEARCH_WORDS_KEY, -5.0, 0.0);

        try (Cursor<TypedTuple<String>> cursor = redisTemplate.opsForZSet().scan(SEARCH_WORDS_KEY, NONE)) {
            while (cursor.hasNext()) {
                final TypedTuple<String> searchWord = cursor.next();
                if (isEmpty(searchWord.getValue())) continue;
                if (searchWord.getScore() == null || searchWord.getScore() < 0) continue;

                log.info("word: {}, score: {}", searchWord.getValue(), searchWord.getScore());

                redisTemplate.opsForZSet().incrementScore(SEARCH_WORDS_KEY, searchWord.getValue(), -1.0);
            }
        } catch (Exception e) {
            log.warn("{}", e.getMessage(), e);
        }
    }

    public List<String> getPopularSearchWords() {
        Set<TypedTuple<String>> typedTuples = redisTemplate.opsForZSet()
            .reverseRangeWithScores(SEARCH_WORDS_KEY, 0, 4);  //score순으로 상위 5개

        return typedTuples.stream().map(TypedTuple::getValue).collect(toList());
    }
}
