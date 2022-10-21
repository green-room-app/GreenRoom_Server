package com.greenroom.moduleapi.controller.interview;

import com.greenroom.moduleapi.controller.interview.InterviewQuestionDto.*;
import com.greenroom.moduleapi.service.interview.InterviewQuestionService;
import com.greenroom.moduleapi.service.interview.query.InterviewQuestionQueryService;
import com.greenroom.modulecommon.entity.interview.InterviewQuestion;
import com.greenroom.modulecommon.entity.interview.QuestionType;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.jwt.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.greenroom.modulecommon.entity.interview.QuestionType.BASIC_QUESTION;
import static com.greenroom.modulecommon.exception.EnumApiException.FORBIDDEN;
import static java.util.stream.Collectors.toList;

@RequestMapping("/api/interview-questions")
@RequiredArgsConstructor
@RestController
public class InterviewQuestionController {

    private final InterviewQuestionService questionService;
    private final InterviewQuestionQueryService questionQueryService;

    /**
     * 사용자는 면접 연습용 질문을 조회할 수 있다
     *
     * GET /api/interview-questions
     */
    @GetMapping
    public List<GetResponse> getInterviewQuestions(@AuthenticationPrincipal JwtAuthentication authentication,
                                                   InterviewQuestionSearchOption searchOption,
                                                   Pageable pageable) {

        return questionQueryService.findAll(searchOption, authentication.getId(), pageable)
                .stream()
                .map(InterviewQuestionDto.GetResponse::from)
                .collect(toList());
    }

    /**
     * 사용자는 면접 연습용 질문 1건을 조회할 수 있다
     * GET /api/interview-questions/:id
     */
    @GetMapping("/{id}")
    public GetDetailResponse getMyQuestion(@PathVariable("id") Long id) {

        InterviewQuestion interviewQuestion = questionService.getInterviewQuestion(id);

        switch (interviewQuestion.getQuestionType()) {
            case BASIC_QUESTION_WITH_GROUP:
            case MY_QUESTION_WITH_GROUP:
            case GREENROOM_QUESTION:
                return GetDetailResponse.from(interviewQuestion);
            default:
                throw new IllegalArgumentException("해당 질문은 면접 연습용 질문이 아닙니다.");
        }
    }

    /**
     * 사용자는 면접 연습용 질문을 수정할 수 있다
     *
     * PUT /api/interview-questions/:id
     */
    @PutMapping("/{id}")
    public UpdateResponse updateInterviewQuestion(@PathVariable("id") Long id,
                                                  @AuthenticationPrincipal JwtAuthentication authentication,
                                                  @Valid @RequestBody UpdateQuestionRequest request) {

        if (!questionService.isOwner(id, authentication.getId())) {
            throw new ApiException(FORBIDDEN, "질문 생성자만 질문을 수정할 수 있습니다");
        }

        Long updatedId = questionService.updateQuestion(id, request.getCategoryId(), request.getQuestion());

        return UpdateResponse.from(updatedId);
    }

    /**
     * 사용자는 면접 연습용 질문에 대한 답변/키워드를 등록/수정할 수 있다
     *
     * PUT /api/interview-questions/answer/{id}
     */
    @PutMapping("/answer/{id}")
    public UpdateResponse updateInterviewQuestionAnswerAndKeywords(
                                                            @PathVariable("id") Long id,
                                                            @AuthenticationPrincipal JwtAuthentication authentication,
                                                            @Valid @RequestBody UpdateAnswerAndKeywordsRequest request) {

        if (!questionService.isOwner(id, authentication.getId())) {
            throw new ApiException(FORBIDDEN, "질문 생성자만 답변/키워드를 수정할 수 있습니다");
        }

        Long updatedId = questionService.updateAnswerAndKeywords(id, request.getAnswer(), request.getKeywords());

        return UpdateResponse.from(updatedId);

    }

    /**
     * 사용자는 면접 연습용 질문을 그룹에 담을 수 있다
     *
     * POST /api/interview-questions
     */
    @PostMapping
    public CreateResponse createGroupQuestion(@AuthenticationPrincipal JwtAuthentication authentication,
                                              @Valid @RequestBody CreateRequest request) {

        QuestionType questionType = QuestionType.from(request.getQuestionTypeCode());

        if (questionType.equals(BASIC_QUESTION)) {
            Long id = questionService.createByBasicQuestion(request.getGroupId(),
                    request.getQuestionId(),
                    authentication.getId());

            return CreateResponse.from(id);
        }

        Long id = questionService.createByMyQuestionWithGroup(request.getGroupId(),
                request.getQuestionId(),
                authentication.getId());

        return CreateResponse.from(id);
    }
}
