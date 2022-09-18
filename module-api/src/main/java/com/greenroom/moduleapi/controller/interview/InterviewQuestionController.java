package com.greenroom.moduleapi.controller.interview;

import com.greenroom.moduleapi.controller.interview.InterviewQuestionDto.CreateRequest;
import com.greenroom.moduleapi.controller.interview.InterviewQuestionDto.CreateResponse;
import com.greenroom.moduleapi.controller.interview.InterviewQuestionDto.GetResponse;
import com.greenroom.moduleapi.service.interview.InterviewQuestionService;
import com.greenroom.moduleapi.service.interview.query.InterviewQuestionQueryService;
import com.greenroom.modulecommon.entity.interview.QuestionType;
import com.greenroom.modulecommon.jwt.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.greenroom.modulecommon.entity.interview.QuestionType.BASIC_QUESTION;
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
