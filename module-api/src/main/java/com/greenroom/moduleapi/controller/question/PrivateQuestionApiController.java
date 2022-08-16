package com.greenroom.moduleapi.controller.question;

import com.greenroom.moduleapi.controller.question.PrivateQuestionDto.*;
import com.greenroom.moduleapi.service.answer.UserQuestionAnswerService;
import com.greenroom.moduleapi.service.question.UserQuestionService;
import com.greenroom.modulecommon.controller.ApiResult;
import com.greenroom.modulecommon.entity.answer.UserQuestionAnswer;
import com.greenroom.modulecommon.entity.question.UserQuestion;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.exception.EnumApiException;
import com.greenroom.modulecommon.jwt.JwtAuthentication;
import com.greenroom.modulecommon.util.PresignerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.greenroom.modulecommon.controller.ApiResult.OK;
import static com.greenroom.modulecommon.entity.question.QuestionType.PRIVATE;
import static java.util.stream.Collectors.*;

@RequestMapping("/api/private-questions")
@RequiredArgsConstructor
@RestController
public class PrivateQuestionApiController {

    private final UserQuestionService userQuestionService;
    private final UserQuestionAnswerService userQuestionAnswerService;
    private final PresignerUtils presignerUtils;

    /**
     * POST /api/private-questions
     *
     * 사용자는 질문리스트(나만 볼 수 있는 질문)를 생성할 수 있다
     */
    @PostMapping
    public ApiResult<CreateResponse> createQuestion(@AuthenticationPrincipal JwtAuthentication authentication,
                                                    @Valid @RequestBody CreateQuestionRequest request) {

        Long id = userQuestionAnswerService.createPrivateQuestion(authentication.getId(),
                                                                  request.getQuestion(),
                                                                  request.getCategoryId());

        return OK(CreateResponse.from(id));
    }

    /**
     * POST /api/private-questions/answer
     *
     * 사용자는 질문리스트(나만 볼 수 있는 질문)에 대한 답변을 생성할 수 있다
     */
    @PostMapping("/answer")
    public ApiResult<CreateResponse> createQuestionAnswer(@AuthenticationPrincipal JwtAuthentication authentication,
                                                          @Valid @RequestBody CreateAnswerRequest request) {

        Long id = userQuestionAnswerService.createPrivateQuestionAnswer(request.getQuestionId(),
                                                                        authentication.getId(),
                                                                        request.getAnswer());

        return OK(CreateResponse.from(id));
    }

    /**
     * GET /api/private-questions
     *
     * 사용자는 자신이 작성한 질문리스트(나만 볼 수 있는 질문) 목록을 조회할 수 있다
     */
    @GetMapping
    public ApiResult<List<GetResponse>> getQuestions(@AuthenticationPrincipal JwtAuthentication authentication,
                                                    Pageable pageable) {

        List<UserQuestionAnswer> userQuestionAnswers = userQuestionAnswerService.getUserQuestionAnswers(authentication.getId(), PRIVATE, pageable);
        List<UserQuestion> userQuestions = userQuestionAnswers.stream().map(UserQuestionAnswer::getUserQuestion).collect(toList());

        List<GetResponse> responses = userQuestions
            .stream()
            .map(userQuestion -> {
                String url = presignerUtils.getPresignedGetUrl(userQuestion.getUser().getProfileImage());
                return GetResponse.of(userQuestion, url);
        }).collect(toList());

        return OK(responses);
    }

    /**
     * GET /api/private-questions/:id
     *
     * 사용자는 자신이 작성한 질문리스트(나만 볼 수 있는 질문)를 조회할 수 있다
     */
    @GetMapping("/{id}")
    public ApiResult<GetDetailResponse> getQuestion(@PathVariable("id") Long id,
                                                    @AuthenticationPrincipal JwtAuthentication authentication) {

        UserQuestionAnswer answer = userQuestionAnswerService.getUserQuestionAnswer(id, authentication.getId());
        String url = presignerUtils.getPresignedGetUrl(answer.getUserQuestion().getUser().getProfileImage());

        return OK(GetDetailResponse.of(answer, url));
    }

    /**
     * PUT /api/private-questions/:id
     *
     * 사용자는 질문리스트(나만 볼 수 있는 질문)를 수정할 수 있다
     */
    @PutMapping("/{id}")
    public ApiResult<Void> updateQuestion(@PathVariable("id") Long id,
                                          @AuthenticationPrincipal JwtAuthentication authentication,
                                          @Valid @RequestBody UpdateQuestionRequest request) {

        if (!userQuestionService.isWriter(id, authentication.getId())) {
            throw new ApiException(EnumApiException.UNAUTHORIZED, "질문리스트 작성자만 수정할 수 있습니다");
        }

        userQuestionService.update(id, request.getQuestion(), request.getCategoryId());

        return OK();
    }

    /**
     * PUT /api/private-questions/answer/:id
     *
     * 사용자는 질문리스트(나만 볼 수 있는 질문)에 대한 답변을 수정할 수 있다
     */
    @PutMapping("/answer/{id}")
    public ApiResult<Void> updateQuestionAnswer(@PathVariable("id") Long id,
                                                @AuthenticationPrincipal JwtAuthentication authentication,
                                                @Valid @RequestBody UpdateAnswerRequest request) {

        if (!userQuestionAnswerService.isWriter(id, authentication.getId())) {
            throw new ApiException(EnumApiException.UNAUTHORIZED, "질문리스트 답변 작성자만 수정할 수 있습니다");
        }

        userQuestionAnswerService.update(id, request.getAnswer());

        return OK();
    }

    /**
     * DELETE /api/private-questions/:id
     *
     * 사용자는 질문리스트(나만 볼 수 있는 질문)를 삭제할 수 있다
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> deleteQuestion(@PathVariable("id") Long id,
                                          @AuthenticationPrincipal JwtAuthentication authentication) {

        if (!userQuestionService.isWriter(id, authentication.getId())) {
            throw new ApiException(EnumApiException.UNAUTHORIZED, "질문리스트 작성자만 수정할 수 있습니다");
        }

        userQuestionService.deletePrivateQuestion(id);

        return OK();
    }
}
