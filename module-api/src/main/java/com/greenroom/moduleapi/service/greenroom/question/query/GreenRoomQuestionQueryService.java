package com.greenroom.moduleapi.service.greenroom.question.query;

import com.google.common.eventbus.EventBus;
import com.greenroom.moduleapi.service.greenroom.answer.GreenRoomQuestionAnswerService;
import com.greenroom.moduleapi.service.greenroom.question.GreenRoomQuestionService;
import com.greenroom.moduleapi.service.greenroom.scrap.GreenRoomQuestionScrapService;
import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestion;
import com.greenroom.modulecommon.event.SearchEvent;
import com.greenroom.modulecommon.repository.greenroom.question.query.GreenRoomQuestionQueryDto;
import com.greenroom.modulecommon.repository.greenroom.question.query.GreenRoomQuestionQueryRepository;
import com.greenroom.modulecommon.repository.greenroom.question.query.PopularQuestionQueryDto;
import com.greenroom.modulecommon.repository.greenroom.question.query.RecentQuestionQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class GreenRoomQuestionQueryService {

    private final GreenRoomQuestionQueryRepository repository;
    private final GreenRoomQuestionService questionService;
    private final GreenRoomQuestionAnswerService answerService;
    private final GreenRoomQuestionScrapService scrapService;
    private final EventBus eventBus;

    public List<GreenRoomQuestionQueryDto> findAll(Long userId, String searchWord, Pageable pageable) {
        checkArgument(userId != null, "userId 값은 필수입니다.");

        if (isNotBlank(searchWord)) {
            eventBus.post(SearchEvent.from(searchWord));
        }

        return repository.findAll(userId, searchWord, pageable);
    }

    public List<GreenRoomQuestionQueryDto> findAll(Long userId, Long categoryId, Pageable pageable) {
        checkArgument(userId != null, "userId 값은 필수입니다.");
        checkArgument(categoryId != null, "categoryId 값은 필수입니다.");

        return repository.findAll(userId, categoryId, pageable);
    }

    public List<PopularQuestionQueryDto> findAllPopularQuestions(Pageable pageable) {
        return repository.findAllPopularQuestions(pageable);
    }

    public List<RecentQuestionQueryDto> findAllRecentQuestions(List<Long> categoryIds, Long userId, Pageable pageable) {
        checkArgument(userId != null, "userId 값은 필수입니다.");

        return repository.findAllRecentQuestions(categoryIds, userId, pageable);
    }

    public GreenRoomQuestionDetailDto getGreenRoom(Long questionId, Long userId) {
        checkArgument(questionId != null, "questionId 값은 필수입니다.");
        checkArgument(userId != null, "userId 값은 필수입니다.");

        boolean isScrap = scrapService.exist(questionId, userId);
        boolean isParticipated = answerService.isParticipated(questionId, userId);
        GreenRoomQuestion greenRoomQuestion = questionService.getGreenRoomQuestion(questionId);

        boolean isWriter = greenRoomQuestion.getUser()
                .map(user -> user.getId().equals(userId))
                .orElse(false);

        boolean isExpired = greenRoomQuestion.getExpiredAt().isBefore(LocalDateTime.now());

        return GreenRoomQuestionDetailDto.builder()
                                        .id(greenRoomQuestion.getId())
                                        .isScrap(isScrap)
                                        .isParticipated(isParticipated)
                                        .isWriter(isWriter)
                                        .isExpired(isExpired)
                                        .participants(greenRoomQuestion.getQuestionAnswers().size())
                                        .expiredAt(greenRoomQuestion.getExpiredAt())
                                        .question(greenRoomQuestion.getQuestion())
                                        .categoryName(greenRoomQuestion.getCategory().getName())
                                        .build();
    }
}
