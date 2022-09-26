package com.greenroom.moduleapi.service.interview;

import com.greenroom.modulecommon.entity.interview.InterviewQuestion;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InterviewQuestionService {
    /**
     * 그린룸질문 => 그룹에 담기
     */
    Long createByGreenRoom(Long groupId, Long greenRoomQuestionId, Long userId);
    /**
     * 기본질문 => 그룹에 담기
     */
    Long createByBasicQuestion(Long groupId, Long basicQuestionId, Long userId);
    /**
     * 마이질문 => 그룹에 담기
     */
    Long createByMyQuestionWithGroup(Long groupId, Long myQuestionId, Long userId);
    /**
     * 그룹없는 마이질문 생성
     */
    Long createMyQuestionWithoutGroup(Long categoryId, Long userId, String question);

    //특정 사용자의 my 질문 list 조회
    List<InterviewQuestion> getMyQuestions(Long userId, Pageable pageable);

    //한 그룹 안에 들어있는 면접 질문 list 조회
    List<InterviewQuestion> getInterviewQuestions(Long groupId, Long userId, Pageable pageable);

    InterviewQuestion getInterviewQuestion(Long id);

    Long updateQuestion(Long id, Long categoryId, String question);
    /**
     * 수정 B15, B15-1, B15-2
     */
    Long updateAnswerAndKeywords(Long id, String answer, List<String> keywordList);

    void updateGroups(Long groupId, List<Long> ids, Long userId);
    /**
     * 그룹이동 C5-3
     */
    Long updateGroup(Long id, Long groupId);

    boolean isOwner(Long id, Long userId);

    void delete(Long groupId, List<Long> ids, Long userId);
    /**
     * 질문삭제 C5-3
     */
    void delete(Long id);
}
