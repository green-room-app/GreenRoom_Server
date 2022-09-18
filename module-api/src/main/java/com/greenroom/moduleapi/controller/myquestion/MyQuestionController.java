package com.greenroom.moduleapi.controller.myquestion;

import com.greenroom.moduleapi.controller.myquestion.MyQuestionDto.*;
import com.greenroom.moduleapi.service.interview.InterviewQuestionService;
import com.greenroom.moduleapi.service.interview.query.InterviewQuestionQueryService;
import com.greenroom.modulecommon.entity.interview.InterviewQuestion;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.jwt.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.greenroom.modulecommon.entity.interview.QuestionType.MY_QUESTION;
import static com.greenroom.modulecommon.exception.EnumApiException.FORBIDDEN;
import static java.util.stream.Collectors.toList;

@RequestMapping("/api/my-questions")
@RequiredArgsConstructor
@RestController
public class MyQuestionController {

    private final InterviewQuestionService interviewQuestionService;
    private final InterviewQuestionQueryService interviewQuestionQueryService;

    /**
     * 사용자는 자신이 작성한 마이질문(나만 볼 수 있는 질문) 목록을 조회할 수 있다 (B10 화면)
     *
     * GET /api/my-questions
     */
    @GetMapping
    public List<GetResponse> getMyQuestions(@AuthenticationPrincipal JwtAuthentication authentication,
                                            Pageable pageable) {

        return interviewQuestionQueryService.findAll(authentication.getId(), pageable)
                .stream()
                .map(GetResponse::from)
                .collect(toList());
    }

    /**
     * 사용자는 자신이 작성한 마이질문(나만 볼 수 있는 질문)를 조회할 수 있다 (B11 화면)
     * GET /api/my-questions/:id
     */
    @GetMapping("/{id}")
    public GetDetailResponse getMyQuestion(@PathVariable("id") Long id) {

        InterviewQuestion interviewQuestion = interviewQuestionService.getInterviewQuestion(id);

        switch (interviewQuestion.getQuestionType()) {
            case MY_QUESTION:
            case MY_QUESTION_WITH_GROUP:
                return GetDetailResponse.from(interviewQuestion);
            default:
                throw new IllegalArgumentException("해당 질문은 마이 질문이 아닙니다.");
        }
    }

    /**
     * 사용자는 마이질문(나만 볼 수 있는 질문)를 생성할 수 있다 (B9 화면)
     *
     * POST /api/my-questions
     */
    @PostMapping
    public CreateResponse create(@AuthenticationPrincipal JwtAuthentication authentication,
                                 @Valid @RequestBody CreateRequest request) {

        Long id = interviewQuestionService.createMyQuestionWithoutGroup(request.getCategoryId(),
                                                                        authentication.getId(),
                                                                        request.getQuestion());

        return CreateResponse.from(id);
    }

    /**
     * 사용자는 마이질문(나만 볼 수 있는 질문)를 수정할 수 있다 (B9 화면)
     *
     * PUT /api/my-questions/:id
     */
    @PutMapping("/{id}")
    public UpdateResponse updateQuestion(@PathVariable("id") Long id,
                                         @AuthenticationPrincipal JwtAuthentication authentication,
                                         @Valid @RequestBody UpdateQuestionRequest request) {

        if (!interviewQuestionService.isOwner(id, authentication.getId())) {
            throw new ApiException(FORBIDDEN, "질문 생성자만 질문을 수정할 수 있습니다");
        }

        Long updatedId = interviewQuestionService.updateQuestion(id, request.getCategoryId(), request.getQuestion());

        return UpdateResponse.from(updatedId);
    }

    /**
     * 사용자는 마이질문(나만 볼 수 있는 질문)에 대한 답변/키워드를 등록/수정할 수 있다 (B15 화면)
     *
     * PUT /api/my-questions/answer/:id
     */
    @PutMapping("/answer/{id}")
    public UpdateResponse updateAnswerAndKeywords(@PathVariable("id") Long id,
                                                  @AuthenticationPrincipal JwtAuthentication authentication,
                                                  @Valid @RequestBody UpdateAnswerAndKeywordsRequest request) {

        if (!interviewQuestionService.isOwner(id, authentication.getId())) {
            throw new ApiException(FORBIDDEN, "질문 생성자만 질문을 수정할 수 있습니다");
        }

        Long updatedId = interviewQuestionService.updateAnswerAndKeywords(id, request.getAnswer(), request.getKeywords());

        return UpdateResponse.from(updatedId);
    }

    /**
     * 사용자는 마이질문(나만 볼 수 있는 질문)를 삭제할 수 있다
     *
     * DELETE /api/my-questions/:id
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id, @AuthenticationPrincipal JwtAuthentication authentication) {

        if (!interviewQuestionService.isOwner(id, authentication.getId())) {
            throw new ApiException(FORBIDDEN, "질문 생성자만 질문을 삭제할 수 있습니다");
        }

        interviewQuestionService.delete(id);
    }
}
