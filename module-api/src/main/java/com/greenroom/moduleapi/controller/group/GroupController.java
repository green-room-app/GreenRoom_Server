package com.greenroom.moduleapi.controller.group;

import com.greenroom.moduleapi.controller.group.GroupDto.CreateResponse;
import com.greenroom.moduleapi.controller.group.GroupDto.GetDetailResponse;
import com.greenroom.moduleapi.controller.group.GroupDto.GetResponse;
import com.greenroom.moduleapi.service.group.QuestionGroupService;
import com.greenroom.moduleapi.service.interview.InterviewQuestionService;
import com.greenroom.modulecommon.entity.group.QuestionGroup;
import com.greenroom.modulecommon.entity.interview.InterviewQuestion;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.jwt.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.greenroom.modulecommon.exception.EnumApiException.FORBIDDEN;
import static java.util.stream.Collectors.toList;

@RequestMapping("/api/groups")
@RequiredArgsConstructor
@RestController
public class GroupController {

    private final QuestionGroupService questionGroupService;
    private final InterviewQuestionService interviewQuestionService;

    /**
     * 사용자는 자신이 생성한 그룹 목록을 조회할 수 있다
     *
     * GET /api/groups
     */
    @GetMapping
    public List<GetResponse> getQuestionGroups(@AuthenticationPrincipal JwtAuthentication authentication,
                                               Pageable pageable) {

        return questionGroupService.getGroups(authentication.getId(), pageable)
                .stream()
                .map(GetResponse::from)
                .collect(toList());
    }

    /**
     * 사용자는 그룹을 생성할 수 있다
     *
     * POST /api/groups
     */
    @PostMapping
    public CreateResponse createQuestionGroup(@AuthenticationPrincipal JwtAuthentication authentication,
                                              @Valid @RequestBody GroupDto.CreateRequest request) {

        Long id = questionGroupService.create(request.getCategoryId(), authentication.getId(), request.getName());
        return CreateResponse.from(id);
    }

    /**
     * 사용자는 그룹을 수정할 수 있다
     *
     * PUT /api/groups/:id
     */
    @PutMapping("/{id}")
    public void update(@PathVariable("id") Long id,
                       @AuthenticationPrincipal JwtAuthentication authentication,
                       @Valid @RequestBody GroupDto.UpdateRequest request) {

        if (!questionGroupService.isOwner(id, authentication.getId())) {
            throw new ApiException(FORBIDDEN, "그룹 생성자만 그룹을 수정할 수 있습니다");
        }

        questionGroupService.update(id, request.getCategoryId(), request.getName());
    }

    /**
     * 사용자는 그룹을 삭제할 수 있다
     *
     * DELETE /api/groups/:id
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id, @AuthenticationPrincipal JwtAuthentication authentication) {

        if (!questionGroupService.isOwner(id, authentication.getId())) {
            throw new ApiException(FORBIDDEN, "그룹 생성자만 그룹을 삭제할 수 있습니다");
        }

        questionGroupService.delete(id);
    }

    /**
     * 사용자는 그룹에 속한 면접 질문을 조회할 수 있다
     *
     * GET /api/groups/:id/questions
     */
    @GetMapping("/{id}/questions")
    public GetDetailResponse getGroupQuestions(@PathVariable("id") Long id,
                                               @AuthenticationPrincipal JwtAuthentication authentication,
                                               Pageable pageable) {

        QuestionGroup group = questionGroupService.getGroup(id);
        Page<InterviewQuestion> questions =
            interviewQuestionService.getInterviewQuestions(id, authentication.getId(), pageable);

        return GetDetailResponse.of(group, questions.getContent(), questions.getTotalElements(), questions.getTotalPages());
    }

    /**
     * 사용자는 그룹에 속한 질문을 다른 그룹으로 변경할 수 있다
     *
     * POST /api/groups/move-questions
     */
    @PostMapping("/move-questions")
    public void moveGroupQuestions(@AuthenticationPrincipal JwtAuthentication authentication,
                                   @Valid @RequestBody GroupDto.MoveRequest request) {

        if (!questionGroupService.isOwner(request.getGroupId(), authentication.getId())) {
            throw new ApiException(FORBIDDEN, "그룹 생성자만 질문을 이동시킬 수 있습니다");
        }

        interviewQuestionService.updateGroups(request.getGroupId(), request.getIds(), authentication.getId());
    }

    /**
     * 사용자는 그룹에 속한 질문을 삭제할 수 있다
     *
     * POST /api/groups/delete-questions
     */
    @PostMapping("/delete-questions")
    public void deleteGroupQuestions(@AuthenticationPrincipal JwtAuthentication authentication,
                                     @Valid @RequestBody GroupDto.DeleteRequest request) {

        if (!questionGroupService.isOwner(request.getGroupId(), authentication.getId())) {
            throw new ApiException(FORBIDDEN, "그룹 생성자만 질문을 삭제할 수 있습니다");
        }

        interviewQuestionService.delete(request.getGroupId(), request.getIds(), authentication.getId());
    }
}
