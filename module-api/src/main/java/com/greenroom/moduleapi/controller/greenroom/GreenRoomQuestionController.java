package com.greenroom.moduleapi.controller.greenroom;

import com.greenroom.moduleapi.controller.greenroom.GreenRoomQuestionDto.*;
import com.greenroom.moduleapi.service.greenroom.answer.GreenRoomQuestionAnswerService;
import com.greenroom.moduleapi.service.greenroom.answer.query.GreenRoomQuestionAnswerQueryService;
import com.greenroom.moduleapi.service.greenroom.question.GreenRoomQuestionService;
import com.greenroom.moduleapi.service.greenroom.question.query.GreenRoomQuestionQueryService;
import com.greenroom.moduleapi.service.greenroom.question.search.GreenRoomQuestionSearchService;
import com.greenroom.moduleapi.service.greenroom.scrap.GreenRoomQuestionScrapService;
import com.greenroom.moduleapi.service.interview.InterviewQuestionService;
import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestion;
import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestionAnswer;
import com.greenroom.modulecommon.entity.user.User;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.jwt.JwtAuthentication;
import com.greenroom.modulecommon.util.PresignerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

import static com.greenroom.modulecommon.exception.EnumApiException.FORBIDDEN;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@RequestMapping("/api/green-questions")
@RequiredArgsConstructor
@RestController
public class GreenRoomQuestionController {

    private final GreenRoomQuestionService questionService;
    private final GreenRoomQuestionQueryService questionQueryService;
    private final GreenRoomQuestionAnswerService answerService;
    private final GreenRoomQuestionAnswerQueryService answerQueryService;
    private final GreenRoomQuestionScrapService scrapService;
    private final GreenRoomQuestionSearchService questionSearchService;

    private final InterviewQuestionService interviewQuestionService;
    private final PresignerUtils presignerUtils;

    /**
     * 사용자는 '직무(1개)'와 관련된 그린룸질문 목록을 조회할 수 있다 (B2 화면)
     *
     * GET /api/green-questions
     */
    @GetMapping
    public List<GetCategoryQuestionResponse> getGreenRoomQuestions(@AuthenticationPrincipal JwtAuthentication authentication,
                                                                   @RequestParam("categoryId") Long categoryId,
                                                                   Pageable pageable) {

        return questionQueryService.findAll(authentication.getId(), categoryId, pageable)
            .stream().map(dto -> {
                String profileUrl = isEmpty(dto.getProfileUrl()) ? EMPTY : dto.getProfileUrl();
                String presignedUrl = isEmpty(profileUrl) ? profileUrl : presignerUtils.getPresignedGetUrl(profileUrl);
                return GetCategoryQuestionResponse.of(dto, presignedUrl);
        }).collect(toList());
    }

    /**
     * 사용자는 검색어를 사용해서 그린룸질문 목록을 조회할 수 있다 (B3, B3-1 화면)
     *
     * GET /api/green-questions/search
     */
    @GetMapping("/search")
    public List<GetCategoryQuestionResponse> getGreenRoomQuestions(@AuthenticationPrincipal JwtAuthentication authentication,
                                                                   @RequestParam(value = "title", required = false) String searchWord,
                                                                   Pageable pageable) {

        return questionQueryService.findAll(authentication.getId(), searchWord, pageable)
                .stream().map(dto -> {
                    String profileUrl = isEmpty(dto.getProfileUrl()) ? EMPTY : dto.getProfileUrl();
                    String presignedUrl = isEmpty(profileUrl) ? profileUrl : presignerUtils.getPresignedGetUrl(profileUrl);
                    return GetCategoryQuestionResponse.of(dto, presignedUrl);
                }).collect(toList());
    }

    /**
     * 사용자는 그린룸질문의 상세 정보를 조회할 수 있다
     *
     * GET /api/green-questions/:id
     */
    @GetMapping("/{id}")
    public GetQuestionDetailResponse getGreenRoomQuestion(@PathVariable("id") Long id,
                                                          @AuthenticationPrincipal JwtAuthentication authentication) {

        return GetQuestionDetailResponse.from(questionQueryService.getGreenRoom(id, authentication.getId()));
    }

    /**
     * 그린룸 답변에 참여한 사용자는 마감시간이 지난 그린룸질문의 답변자들의 답변 목록을 조회할 수 있다
     *
     * GET /api/green-questions/:id/answers
     */
    @GetMapping("/{id}/answers")
    public List<GetQuestionAnswerResponse> getGreenRoomAnswers(@PathVariable("id") Long questionId,
                                                               @AuthenticationPrincipal JwtAuthentication authentication,
                                                               Pageable pageable) {

        if (!answerService.isParticipated(questionId, authentication.getId())) {
            throw new ApiException(FORBIDDEN, "질문 참여자만 다른 사람이 남긴 답변을 조회할 수 있습니다.");
        }

        return answerService.getAnswers(questionId, authentication.getId(), pageable)
            .stream().map(answer -> {
                String profileUrl = answer.getUser().map(User::getProfileImage).orElse(EMPTY);
                String presignedUrl = isEmpty(profileUrl) ? profileUrl : presignerUtils.getPresignedGetUrl(profileUrl);

                return GetQuestionAnswerResponse.of(answer, presignedUrl);
        }).collect(toList());
    }

    /**
     * 그린룸 답변에 참여한 사용자는 그린룸질문에 달린 답변 1건을 조회할 수 있다 (B13-3(1) 화면)
     *
     * GET /api/green-questions/answer/:id
     */
    @GetMapping("/answer/{id}")
    public GetQuestionAnswerDetailResponse getGreenRoomAnswer(@PathVariable("id") Long id,
                                                              @AuthenticationPrincipal JwtAuthentication authentication) {

        GreenRoomQuestionAnswer answer = answerService.getAnswer(id);
        Long questionId = answer.getGreenRoomQuestion().getId();

        if (!answerService.isParticipated(questionId, authentication.getId())) {
            throw new ApiException(FORBIDDEN, "질문 참여자만 다른 사람이 남긴 답변을 조회할 수 있습니다.");
        }

        String profileUrl = answer.getUser().map(User::getProfileImage).orElse(EMPTY);
        String presignedUrl = isEmpty(profileUrl) ? profileUrl : presignerUtils.getPresignedGetUrl(profileUrl);

        return GetQuestionAnswerDetailResponse.of(answer, presignedUrl);
    }

    /**
     * 사용자는 그린룸질문을 생성할 수 있다
     *
     * POST /api/green-questions
     */
    @PostMapping
    public CreateResponse createGreenRoomQuestion(@AuthenticationPrincipal JwtAuthentication authentication,
                                                  @Valid @RequestBody CreateRequest request) {

        Long id = questionService.create(authentication.getId(),
                                                    request.getQuestion(),
                                                    request.getCategoryId(),
                                                    request.getExpiredAt());

        return CreateResponse.from(id);
    }

    /**
     * 사용자는 그린룸질문을 그룹에 담을 수 있다
     *
     * POST /api/green-questions/group
     */
    @PostMapping("/group")
    public CreateResponse createInterviewQuestion(@AuthenticationPrincipal JwtAuthentication authentication,
                                                  @Valid @RequestBody CreateInterviewQuestionRequest request) {

        Long id = interviewQuestionService.createByGreenRoom(request.getGroupId(),
                                                                request.getQuestionId(),
                                                                authentication.getId());

        return CreateResponse.from(id);
    }

    /**
     * 사용자는 최근 그린룸질문 목록을 조회할 수 있다 (B1 화면, B4 화면)
     *
     * GET /api/green-questions/recent-questions
     */
    @GetMapping("/recent-questions")
    public List<GetRecentQuestionResponse> getRecetQuestions(CategorySearchOption categorySearchOption,
                                                             @AuthenticationPrincipal JwtAuthentication authentication,
                                                             Pageable pageable) {

        return questionQueryService.findAllRecentQuestions(categorySearchOption.getCategories(), authentication.getId(), pageable)
            .stream().map(dto -> {
                String profileUrl = isEmpty(dto.getProfileUrl()) ? EMPTY : dto.getProfileUrl();
                String presignedUrl = isEmpty(profileUrl) ? profileUrl : presignerUtils.getPresignedGetUrl(profileUrl);
                return GetRecentQuestionResponse.of(dto, presignedUrl);
        }).collect(toList());
    }

    /**
     * 사용자는 그린룸질문에 대한 답변을 생성할 수 있다
     *
     * POST /api/green-questions/answer
     */
    @PostMapping("/answer")
    public CreateResponse createAnswer(@AuthenticationPrincipal JwtAuthentication authentication,
                                       @Valid @RequestBody CreateAnswerRequest request) {

        Long id = answerService.create(request.getId(),
                authentication.getId(),
                request.getAnswer(),
                request.getKeywords());

        return CreateResponse.from(id);
    }

    /**
     * 사용자는 그린룸질문을 스크랩할 수 있다.
     *
     * POST /api/green-questions/scrap
     */
    @PostMapping("/scrap")
    public CreateResponse createScrap(@AuthenticationPrincipal JwtAuthentication authentication,
                                       @Valid @RequestBody CreateScrapRequest request) {

        Long id = scrapService.create(request.getId(), authentication.getId());
        return CreateResponse.from(id);
    }

    /**
     * 사용자는 자신이 스크랩한 그린룸질문 목록을 조회할 수 있다 (B6 화면)
     *
     * GET /api/green-questions/scrap
     */
    @GetMapping("/scrap")
    public List<GetScrapQuestionResponse> getScrapQuestions(@AuthenticationPrincipal JwtAuthentication authentication,
                                                            Pageable pageable) {

        return scrapService.getScraps(authentication.getId(), pageable)
            .stream().map(scrap -> {
                String profileUrl = scrap.getUser().map(User::getProfileImage).orElse(EMPTY);
                String presignedUrl = isEmpty(profileUrl) ? profileUrl : presignerUtils.getPresignedGetUrl(profileUrl);
                boolean isParticipated = answerService.isParticipated(scrap.getGreenRoomQuestion().getId(),
                                                                              authentication.getId());

                return GetScrapQuestionResponse.of(scrap, presignedUrl, isParticipated);
        }).collect(toList());
    }

    /**
     * 사용자는 자신이 스크랩한 그린룸질문을 삭제할 수 있다
     *
     * POST /api/green-questions/delete-scrap
     */
    @PostMapping("/delete-scrap")
    public void deleteScrapQuestions(@AuthenticationPrincipal JwtAuthentication authentication,
                                     @Valid @RequestBody DeleteScrapRequest request) {

        scrapService.delete(request.getIds(), authentication.getId());
    }

    /**
     * 사용자는 자신이 생성한 그린룸질문 목록을 조회할 수 있다 (B7 화면)
     *
     * GET /api/green-questions/create-questions
     */
    @GetMapping("/create-questions")
    public GetMyQuestionResponse getMyGreenRoomQuestions(@AuthenticationPrincipal JwtAuthentication authentication,
                                                         @PageableDefault(size = 1) Pageable pageable) {

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), 1);

        Slice<GreenRoomQuestion> myGreenRoomQuestion =
                questionService.getMyGreenRoomQuestions(authentication.getId(), pageRequest);

        if (myGreenRoomQuestion.getContent().isEmpty()) {
            return GetMyQuestionResponse.notExisted();
        }

        GreenRoomQuestion greenRoomQuestion = myGreenRoomQuestion.getContent().get(0);
        int pictureSize = Math.min(greenRoomQuestion.getQuestionAnswers().size(), 3);

        if (pictureSize == 0) {
            return GetMyQuestionResponse.of(greenRoomQuestion,
                    Collections.emptyList(),
                    myGreenRoomQuestion.hasPrevious(),
                    myGreenRoomQuestion.hasNext());
        }

        List<String> profileImages = answerQueryService.getUserProfileImages(greenRoomQuestion.getId(), pictureSize)
                .stream().map(profileUrl -> isEmpty(profileUrl) ?
                        profileUrl : presignerUtils.getPresignedGetUrl(profileUrl))
                .collect(toList());

        return GetMyQuestionResponse.of(greenRoomQuestion,
                profileImages,
                myGreenRoomQuestion.hasPrevious(),
                myGreenRoomQuestion.hasNext());
    }

    /**
     * 사용자는 자신이 참여한 그린룸질문 목록을 조회할 수 있다 (C3 화면)
     *
     * GET /api/green-questions/involve-questions
     */
    @GetMapping("/involve-questions")
    public List<GetInvolvedQuestionResponse> getInvolvedQuestions(@AuthenticationPrincipal JwtAuthentication authentication,
                                                                  CategorySearchOption categorySearchOption,
                                                                  Pageable pageable) {

        return answerService.getAnswers(categorySearchOption.getCategories(),
                                                  authentication.getId(),
                                                  pageable)
            .stream().map(answer -> {
                String profileUrl = answer.getUser().map(User::getProfileImage).orElse(EMPTY);
                String presignedUrl = isEmpty(profileUrl) ? profileUrl : presignerUtils.getPresignedGetUrl(profileUrl);

                return GetInvolvedQuestionResponse.of(answer.getGreenRoomQuestion(), presignedUrl);
        }).collect(toList());
    }

    /**
     * 사용자는 인기 그린룸질문 목록을 조회할 수 있다 (B1 화면)
     */
    @GetMapping("/popular-questions")
    public List<GetPopularQuestionResponse> getPopularQuestions(Pageable pageable) {
        return questionQueryService.findAllPopularQuestions(pageable)
            .stream().map(dto -> {
                String profileUrl = isEmpty(dto.getProfileUrl()) ? EMPTY : dto.getProfileUrl();
                String presignedUrl = isEmpty(profileUrl) ? profileUrl : presignerUtils.getPresignedGetUrl(profileUrl);
                return GetPopularQuestionResponse.of(dto, presignedUrl);
        }).collect(toList());
    }

    /**
     * 인기 검색어 목록을 조회할 수 있다
     *
     * GET /api/green-questions/popular-search-words
     */
    @GetMapping("/popular-search-words")
    public List<String> getPopularSearchWords() {
        return questionSearchService.getPopularSearchWords();
    }

    @DeleteMapping("/{id}")
    public void deleteGreenRoomQuestion(@PathVariable("id") Long id,
                                        @AuthenticationPrincipal JwtAuthentication authentication) {

        if (!questionService.isOwner(id, authentication.getId())) {
            throw new ApiException(FORBIDDEN, "질문 생성자만 질문을 삭제할 수 있습니다");
        }

        questionService.delete(id);
    }
}
