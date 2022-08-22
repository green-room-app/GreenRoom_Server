package com.greenroom.moduleapi.controller.question;

import com.greenroom.moduleapi.service.answer.UserQuestionAnswerService;
import com.greenroom.moduleapi.service.answer.query.RefQuestionAnswerQueryService;
import com.greenroom.moduleapi.service.question.query.RefQuestionQueryService;
import com.greenroom.moduleapi.service.question.query.UserQuestionQueryService;
import com.greenroom.modulecommon.jwt.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.*;

@RequestMapping("/api/questions")
@RequiredArgsConstructor
@RestController
public class QuestionApiController {

    private final RefQuestionQueryService refQuestionQueryService;
    private final UserQuestionQueryService userQuestionQueryService;
    private final RefQuestionAnswerQueryService refQuestionAnswerQueryService;
    private final UserQuestionAnswerService userQuestionAnswerService;

    /**
     * 키워드 연습 대상 질문 목록 조회
     * GET /api/questions
     */
    @GetMapping
    public List<QuestionDto.GetResponse> getQuestions(QuestionSearchOption searchOption,
                                                      @AuthenticationPrincipal JwtAuthentication authentication,
                                                      Pageable pageable) {

        //키워드 옵션 ON
        if (searchOption.isRefQuestion()) {
            //기본 질문에서 처리
            return refQuestionQueryService.getRefQuestions(authentication.getId(),
                                                            searchOption.getCategories(),
                                                            searchOption.getTitle(),
                                                            pageable)
                    .stream()
                    .map(QuestionDto.GetResponse::from)
                    .collect(toList());
        }

        return userQuestionQueryService.getUserQuestions(authentication.getId(),
                                                            searchOption.getCategories(),
                                                            searchOption.getTitle(),
                                                            pageable)
                .stream()
                .map(QuestionDto.GetResponse::from)
                .collect(toList());
    }

    /**
     * 키워드 연습 대상 질문 1건 조회
     * GET /api/questions/{questionId}
     */
    @GetMapping("/{id}")
    public QuestionDto.GetDetailResponse getQuestion(@PathVariable("id") Long id,
                                                     @AuthenticationPrincipal JwtAuthentication authentication,
                                                     @RequestParam("type") String type) {

        if (type.equals("basic")) {
            return QuestionDto.GetDetailResponse
                .from(refQuestionAnswerQueryService.getRefQuestionAnswer(id, authentication.getId()));
        }

        return QuestionDto.GetDetailResponse
            .from(userQuestionAnswerService.getUserQuestionAnswer(id, authentication.getId()));
    }
}
