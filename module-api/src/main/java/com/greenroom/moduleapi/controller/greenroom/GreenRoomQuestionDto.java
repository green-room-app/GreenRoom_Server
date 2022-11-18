package com.greenroom.moduleapi.controller.greenroom;

import com.greenroom.moduleapi.service.greenroom.question.query.GreenRoomQuestionDetailDto;
import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestion;
import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestionAnswer;
import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestionScrap;
import com.greenroom.modulecommon.entity.user.User;
import com.greenroom.modulecommon.repository.greenroom.question.query.GreenRoomQuestionQueryDto;
import com.greenroom.modulecommon.repository.greenroom.question.query.PopularQuestionQueryDto;
import com.greenroom.modulecommon.repository.greenroom.question.query.RecentQuestionQueryDto;
import com.greenroom.modulecommon.util.KeywordUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class GreenRoomQuestionDto {

    @Getter
    public static class CreateRequest {
        @NotNull(message = "categoryId 값은 필수입니다.")
        private Long categoryId;
        @NotEmpty(message = "question 값은 필수입니다.")
        private String question;
        @NotNull(message = "expiredAt 값은 필수입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime expiredAt;
    }

    @Getter
    public static class CreateAnswerRequest {
        @NotNull(message = "id 값은 필수입니다.")
        private Long id;
        @NotEmpty(message = "answer 값은 필수입니다.")
        private String answer;
        private List<String> keywords;
    }

    @Getter
    public static class CreateScrapRequest {
        @NotNull(message = "id 값은 필수입니다.")
        private Long id;
    }

    @Getter
    public static class CreateInterviewQuestionRequest {
        @NotNull(message = "groupId 값은 필수입니다.")
        private Long groupId;
        @NotNull(message = "questionId 값은 필수입니다.")
        private Long questionId;
    }

    @Getter
    public static class DeleteScrapRequest {
        @Size(max = 10)
        private List<Long> ids = new ArrayList<>();
    }

    /**
     * B2 화면 특정 직무에 해당하는 그린룸질문 조회 결과 DTO
     */
    @Getter
    @Builder
    public static class GetCategoryQuestionResponse {
        private Long id;    //그린룸질문 id
        private String categoryName;
        private String profileImage;
        private String name;    //질문자 닉네임
        private int participants;
        private String question;
        private String answer;
        private LocalDateTime expiredAt;
        private boolean isParticipated;

        public static GetCategoryQuestionResponse of(GreenRoomQuestionQueryDto dto, String profileUrl) {
            return GetCategoryQuestionResponse.builder()
                .id(dto.getId())
                .categoryName(dto.getCategoryName())
                .profileImage(profileUrl)
                .name(isEmpty(dto.getName()) ? EMPTY : dto.getName())
                .participants(dto.getParticipants())
                .question(dto.getQuestion())
                .answer(isEmpty(dto.getAnswer()) ? EMPTY : dto.getAnswer())
                .expiredAt(dto.getExpiredAt())
                .isParticipated(dto.isParticipated())
                .build();
        }
    }

    @Getter
    @Builder
    public static class GetMyQuestionResponse {
        private Long id;    //그린룸질문 id
        private int participants;
        private String question;
        private List<String> profileImages;
        private boolean existed;
        private boolean hasPrev;
        private boolean hasNext;

        public static GetMyQuestionResponse notExisted() {
            return GetMyQuestionResponse.builder()
                    .existed(false)
                    .build();
        }

        public static GetMyQuestionResponse of(GreenRoomQuestion greenRoomQuestion,
                                               List<String> profileImages,
                                               boolean hasPrev,
                                               boolean hasNext) {

            return GetMyQuestionResponse.builder()
                    .id(greenRoomQuestion.getId())
                    .participants(greenRoomQuestion.getQuestionAnswers().size())
                    .question(greenRoomQuestion.getQuestion())
                    .profileImages(profileImages)
                    .existed(true)
                    .hasPrev(hasPrev)
                    .hasNext(hasNext)
                    .build();
        }
    }

    /**
     * B4 화면 최근질문 조회 결과 DTO
     */
    @Getter
    @Builder
    public static class GetRecentQuestionResponse {
        private Long id;
        private String categoryName;
        private String profileImage;
        private String question;
        private boolean isParticipated;
        private LocalDateTime expiredAt;
        private boolean isExpired;

        public static GetRecentQuestionResponse of(RecentQuestionQueryDto dto, String profileUrl) {
            return GetRecentQuestionResponse.builder()
                    .id(dto.getId())
                    .categoryName(dto.getCategoryName())
                    .profileImage(profileUrl)
                    .question(dto.getQuestion())
                    .expiredAt(dto.getExpiredAt())
                    .isExpired(dto.getExpiredAt().isBefore(LocalDateTime.now()))
                    .isParticipated(dto.isParticipated())
                    .build();
        }
    }

    /**
     * B6 화면 스크랩질문 조회 결과 DTO
     */
    @Getter
    @Builder
    public static class GetScrapQuestionResponse {
        private Long id;
        private String categoryName;
        private String profileImage;
        private String question;
        private LocalDateTime expiredAt;
        private boolean isExpired;
        private boolean isParticipated;

        public static GetScrapQuestionResponse of(GreenRoomQuestionScrap scrap, String profileUrl, boolean isParticipated) {
            return GetScrapQuestionResponse.builder()
                    .id(scrap.getId())
                    .categoryName(scrap.getGreenRoomQuestion().getCategory().getName())
                    .profileImage(profileUrl)
                    .question(scrap.getGreenRoomQuestion().getQuestion())
                    .expiredAt(scrap.getGreenRoomQuestion().getExpiredAt())
                    .isExpired(scrap.getGreenRoomQuestion().getExpiredAt().isBefore(LocalDateTime.now()))
                    .isParticipated(isParticipated)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetInvolvedQuestionResponse {
        private Long id;    //그린룸질문 id
        private String categoryName;
        private String profileImage;
        private String question;

        public static GetInvolvedQuestionResponse of(GreenRoomQuestion greenRoomQuestion, String profileUrl) {
            return GetInvolvedQuestionResponse.builder()
                    .id(greenRoomQuestion.getId())
                    .categoryName(greenRoomQuestion.getCategory().getName())
                    .profileImage(profileUrl)
                    .question(greenRoomQuestion.getQuestion())
                    .build();
        }
    }

    /**
     * B1 화면 인기질문에 해당하는 그린룸질문 조회 결과 DTO
     */
    @Getter
    @Builder
    public static class GetPopularQuestionResponse {
        private Long id;    //그린룸질문 id
        private String categoryName;
        private String profileImage;
        private String name;    //질문자 닉네임
        private int participants;
        private String question;
        private LocalDateTime expiredAt;

        public static GetPopularQuestionResponse of(PopularQuestionQueryDto dto, String profileUrl) {
            return GetPopularQuestionResponse.builder()
                    .id(dto.getId())
                    .categoryName(dto.getCategoryName())
                    .profileImage(profileUrl)
                    .name(isEmpty(dto.getName()) ? EMPTY : dto.getName())
                    .participants(dto.getParticipants())
                    .question(dto.getQuestion())
                    .expiredAt(dto.getExpiredAt())
                    .build();
        }
    }

    /**
     * B13, 14 화면 그린룸질문 상세정보에 해당하는 조회 결과 DTO
     */
    @Getter
    @Builder
    public static class GetQuestionDetailResponse {
        private Long id;    //그린룸 질문 id
        private boolean isScrap;
        private Long scrapId;
        private boolean isParticipated;
        private boolean isWriter;
        private boolean isExpired;
        private int participants;
        private LocalDateTime expiredAt;
        private String question;
        private String categoryName;

        public static GetQuestionDetailResponse from(GreenRoomQuestionDetailDto dto) {
            return GetQuestionDetailResponse.builder()
                    .id(dto.getId())
                    .isScrap(dto.isScrap())
                    .scrapId(dto.getScrapId())
                    .isParticipated(dto.isParticipated())
                    .isWriter(dto.isWriter())
                    .isExpired(dto.isExpired())
                    .participants(dto.getParticipants())
                    .expiredAt(dto.getExpiredAt())
                    .question(dto.getQuestion())
                    .categoryName(dto.getCategoryName())
                    .build();
        }
    }

    /**
     * B13-3(1) 화면 그린룸질문에 남긴 답변 상세정보 조회 결과 DTO
     */
    @Getter
    @Builder
    public static class GetQuestionAnswerResponse {
        private Long id;    //답변 id
        private String profileImage;
        private String answer;

        public static GetQuestionAnswerResponse of(GreenRoomQuestionAnswer answer, String profileUrl) {
            return GetQuestionAnswerResponse.builder()
                .id(answer.getId())
                .profileImage(profileUrl)
                .answer(answer.getAnswer())
                .build();
        }

    }

    /**
     * B13-3(1) 화면 그린룸질문에 남긴 답변 상세정보 조회 결과 DTO
     */
    @Getter
    @Builder
    public static class GetQuestionAnswerDetailResponse {
        private Long id;
        private String profileImage;
        private String name;
        private String answer;
        private List<String> keywords;

        public static GetQuestionAnswerDetailResponse of(GreenRoomQuestionAnswer answer, String profileUrl) {
            return GetQuestionAnswerDetailResponse.builder()
                .id(answer.getId())
                .profileImage(profileUrl)
                .name(answer.getUser().map(User::getName).orElse(EMPTY))
                .answer(answer.getAnswer())
                .keywords(KeywordUtils.toKeywordList(answer.getKeywords()))
                .build();
        }
    }


    @Getter
    @AllArgsConstructor
    public static class CreateResponse {
        private Long id;

        public static CreateResponse from(Long id) {
            return new CreateResponse(id);
        }
    }
}
