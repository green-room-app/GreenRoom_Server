package com.greenroom.moduleapi.controller.question;

import com.greenroom.moduleapi.controller.question.PublicQuestionDto.*;
import com.greenroom.moduleapi.service.answer.UserQuestionAnswerService;
import com.greenroom.moduleapi.service.question.UserQuestionService;
import com.greenroom.moduleapi.service.scrap.ScrapService;
import com.greenroom.modulecommon.entity.answer.UserQuestionAnswer;
import com.greenroom.modulecommon.entity.question.UserQuestion;
import com.greenroom.modulecommon.entity.scrap.Scrap;
import com.greenroom.modulecommon.jwt.JwtAuthentication;
import com.greenroom.modulecommon.util.PresignerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.greenroom.modulecommon.entity.question.QuestionType.PUBLIC;
import static java.util.stream.Collectors.toList;

@RequestMapping("/api/public-questions")
@RequiredArgsConstructor
@RestController
public class PublicQuestionApiController {

    private final UserQuestionService userQuestionService;
    private final UserQuestionAnswerService userQuestionAnswerService;
    private final ScrapService scrapService;
    private final PresignerUtils presignerUtils;

    //FIXME
    /**
     * GET /api/public-questions
     * GET /api/public-questions?category=1
     * GET /api/public-questions?category=2
     * GET /api/public-questions?category=3
     *
     * 사용자는 '직무'와 관련된 그린룸질문(지식in 타입) 목록을 조회할 수 있다
     */
    @GetMapping
    public void getPublicQuestions(Pageable pageable) {

    }

    /**
     * GET /api/public-questions/popular-questions
     *
     * 사용자는 '인기' 그린룸질문(지식in 타입) 목록을 조회할 수 있다
     */
    @GetMapping("/popular-questions")
    public List<PopularQuestionResponse> getPopularQuestions(Pageable pageable) {
        List<UserQuestion> popularQuestions = userQuestionService.getPopularQuestions(pageable);

        List<PopularQuestionResponse> responses = popularQuestions
            .stream()
            .map(userQuestion -> {
                String url = presignerUtils.getPresignedGetUrl(userQuestion.getUser().getProfileImage());
                return PopularQuestionResponse.of(userQuestion, url);
        }).collect(toList());

        return responses;
    }

    /**
     * GET /api/public-questions/recent-questions
     * GET /api/public-questions/recent-questions?category=1,2,3,4,5
     *
     * 사용자는 '최근' 그린룸질문(지식in 타입) 목록을 조회할 수 있다
     */
    @GetMapping("/recent-questions")
    public List<RecentQuestionResponse> getRecentQuestions(PublicQuestionSearchOption searchOption, Pageable pageable) {
        List<UserQuestion> userQuestions = userQuestionService.getUserQuestions(searchOption.getCategories(),
                                                                                PUBLIC,
                                                                                pageable);

        List<RecentQuestionResponse> responses = userQuestions
            .stream()
            .map(userQuestion -> {
                String url = presignerUtils.getPresignedGetUrl(userQuestion.getUser().getProfileImage());
                return RecentQuestionResponse.of(userQuestion, url);
        }).collect(toList());

        return responses;
    }

    /**
     * POST /api/public-questions
     *
     * 사용자는 그린룸질문(지식in 타입)을 생성할 수 있다
     */
    @PostMapping
    public CreateResponse createPublicQuestion(@AuthenticationPrincipal JwtAuthentication authentication,
                                               @Valid @RequestBody CreateRequest request) {

        Long publicQuestionId = userQuestionService.createPublicQuestion(authentication.getId(),
                                                                        request.getQuestion(),
                                                                        request.getCategoryId(),
                                                                        request.getExpiredAt()
        );

        return CreateResponse.from(publicQuestionId);
    }

    //FIXME
    /**
     * POST /api/public-questions/answer
     *
     * 사용자는 그린룸질문(지식in 타입)에 대한 답변을 생성할 수 있다
     */
    @PostMapping("/answer")
    public void createPublicQuestionAnswer() {

    }

    //FIXME
    /**
     * GET /api/public-questions/:id
     *
     * 사용자는 그린룸질문(지식in 타입)의 상세 정보를 조회할 수 있다
     */
    @GetMapping("/{id}")
    public void getPublicQuestion(@PathVariable("id") Long id) {
    }

    /**
     * POST /api/public-questions/scrap
     *
     * 사용자는 그린룸질문(지식in 타입)을 스크랩할 수 있다
     */
    @PostMapping("/scrap")
    public void scrapPublicQuestion(@AuthenticationPrincipal JwtAuthentication authentication,
                                    @Valid @RequestBody PublicQuestionDto.ScrapRequest request) {

        scrapService.create(authentication.getId(), request.getQuestionId());
    }

    /**
     * GET /api/public-questions/scrap
     *
     * 사용자는 자신이 스크랩한 그린룸질문(지식in 타입) 목록을 조회할 수 있다
     */
    @GetMapping("/scrap")
    public List<ScrapQuestionResponse> getScrapQuestions(@AuthenticationPrincipal JwtAuthentication authentication,
                                                         Pageable pageable) {

        List<Scrap> scraps = scrapService.getScraps(authentication.getId(), pageable);

        List<ScrapQuestionResponse> responses = scraps
            .stream()
            .map(scrap -> {
                String url = presignerUtils.getPresignedGetUrl(scrap.getUserQuestion().getUser().getProfileImage());
                return ScrapQuestionResponse.of(scrap, url);
        }).collect(toList());

        return responses;
    }

    //FIXME
    /**
     * GET /api/public-questions/create-questions
     *
     * 사용자는 자신이 생성한 그린룸질문(지식in 타입) 목록을 조회할 수 있다
     */
    @GetMapping("/create-questions")
    public void getCreateQuestions() {

    }

    /**
     * GET /api/public-questions/involve-questions
     *
     * 사용자는 자신이 참여한 그린룸질문(지식in 타입) 목록을 조회할 수 있다
     */
    @GetMapping("/involve-questions")
    public List<InvolveQuestionResponse> getInvolveQuestions(@AuthenticationPrincipal JwtAuthentication authentication,
                                                             Pageable pageable) {

        List<UserQuestionAnswer> userQuestionAnswers =
                userQuestionAnswerService.getUserQuestionAnswers(authentication.getId(), PUBLIC, pageable);

        List<InvolveQuestionResponse> responses = userQuestionAnswers
            .stream()
            .map(answer -> {
                String url = presignerUtils.getPresignedGetUrl(answer.getUserQuestion().getUser().getProfileImage());
                return InvolveQuestionResponse.of(answer, url);
        }).collect(toList());

        return responses;
    }

    //FIXME
    /**
     * DELETE /api/public-questions/:id
     *
     * 사용자는 그린룸질문(지식in 타입)을 삭제할 수 있다
     */
    @DeleteMapping("/{id}")
    public void deletePublicQuestion(@PathVariable("id") Long id) {

    }
}
