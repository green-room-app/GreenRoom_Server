package com.greenroom.moduleapi.service.answer;

import com.greenroom.moduleapi.service.question.UserQuestionService;
import com.greenroom.moduleapi.service.user.UserService;
import com.greenroom.modulecommon.entity.answer.UserQuestionAnswer;
import com.greenroom.modulecommon.entity.category.Category;
import com.greenroom.modulecommon.entity.question.QuestionType;
import com.greenroom.modulecommon.entity.question.UserQuestion;
import com.greenroom.modulecommon.entity.user.OAuthType;
import com.greenroom.modulecommon.entity.user.User;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.repository.answer.UserQuestionAnswerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserQuestionAnswerServiceTest {

    @InjectMocks
    private UserQuestionAnswerServiceImpl userQuestionAnswerService;

    @Mock
    private UserQuestionAnswerRepository userQuestionAnswerRepository;

    @Mock
    private UserQuestionService userQuestionService;

    @Mock
    private UserService userService;

    private User user, user2;
    private Category category;
    private UserQuestion publicUserQuestion, privateUserQuestion;
    private UserQuestionAnswer publicUserQuestionAnswer, privateUserQuestionAnswer;

    @BeforeEach
    public void init() {
        category = Category.builder().id(1L).name("test").build();

        user = User.builder()
                .id(1L)
                .oauthType(OAuthType.KAKAO)
                .oauthId("12345")
                .category(category)
                .name("test")
                .build();

        user2 = User.builder()
                .id(2L)
                .oauthType(OAuthType.NAVER)
                .oauthId("12345")
                .category(category)
                .name("test")
                .build();

        publicUserQuestion = UserQuestion.builder()
                .id(1L)
                .user(user)
                .category(category)
                .questionType(QuestionType.PUBLIC)
                .question("테스트 질문")
                .expiredAt(LocalDateTime.now())
                .build();

        privateUserQuestion = UserQuestion.builder()
                .id(2L)
                .user(user)
                .category(category)
                .questionType(QuestionType.PRIVATE)
                .question("테스트 질문")
                .expiredAt(LocalDateTime.now())
                .build();

        publicUserQuestionAnswer = UserQuestionAnswer.builder()
                .id(1L)
                .userQuestion(publicUserQuestion)
                .user(user)
                .answer("publicUserQuestionAnswer")
                .build();

        privateUserQuestionAnswer = UserQuestionAnswer.builder()
                .id(2L)
                .userQuestion(privateUserQuestion)
                .user(user)
                .answer("privateUserQuestionAnswer")
                .build();
    }

    @Nested
    @DisplayName("createPrivateQuestion 테스트")
    class createPrivateQuestion {

        @Test
        @DisplayName("PrivateQuestion을 생성할 때 PrivateQuestionAnswer을 함께 생성한다")
        public void success1() {
            //given
            given(userQuestionService.createPrivateQuestion(anyLong(), anyString(), anyLong())).willReturn(privateUserQuestion.getId());
            given(userQuestionService.getUserQuestion(anyLong())).willReturn(privateUserQuestion);
            given(userService.getUser(anyLong())).willReturn(user);
            given(userQuestionAnswerRepository.save(any())).willReturn(privateUserQuestionAnswer);

            Long userId = user.getId();
            Long categoryId = category.getId();
            String question = "question";

            //when
            Long id = userQuestionAnswerService.createPrivateQuestion(userId, question, categoryId);

            //then
            verify(userQuestionService).createPrivateQuestion(anyLong(), anyString(), anyLong());
            verify(userQuestionService).getUserQuestion(anyLong());
            verify(userService).getUser(anyLong());
            verify(userQuestionAnswerRepository).save(any());
            assertThat(id).isEqualTo(privateUserQuestion.getId());
        }
    }

    @Nested
    @DisplayName("createPrivateQuestionAnswer 테스트")
    class createPrivateQuestionAnswer {

        @Test
        @DisplayName("userQuestionId, userId, answer가 주어지면 PrivateQuestionAnswer을 생성할 수 있다")
        public void success1() {
            //given
            given(userQuestionAnswerRepository.find(anyLong(), anyLong())).willReturn(Optional.of(privateUserQuestionAnswer));

            Long userQuestionId = privateUserQuestion.getId();
            Long userId = user.getId();
            String updatedAnswer = "updatedAnswer";

            //when
            Long id = userQuestionAnswerService.createPrivateQuestionAnswer(userQuestionId, userId, updatedAnswer);

            //then
            verify(userQuestionAnswerRepository).find(anyLong(), anyLong());
            assertThat(id).isEqualTo(privateUserQuestionAnswer.getId());
            assertThat(privateUserQuestionAnswer.getAnswer()).isEqualTo(updatedAnswer);
        }

        @Test
        @DisplayName("PublicQuestion에 대한 답변은 수정할 수 없다")
        public void fail1() {
            //given
            given(userQuestionAnswerRepository.find(anyLong(), anyLong())).willReturn(Optional.of(publicUserQuestionAnswer));

            Long userQuestionId = publicUserQuestion.getId();
            Long userId = user.getId();
            String updatedAnswer = "updatedAnswer";

            //when
            assertThatThrownBy(() -> userQuestionAnswerService.createPrivateQuestionAnswer(userQuestionId, userId, updatedAnswer))
                    .isInstanceOf(IllegalArgumentException.class);

            //then
            verify(userQuestionAnswerRepository).find(anyLong(), anyLong());
        }
    }

    @Nested
    @DisplayName("getUserQuestionAnswers 테스트")
    class getUserQuestionAnswers {

        @Test
        @DisplayName("userId와 questionType이 주어지면 UserQuestionAnswer 목록을 조회할 수 있다")
        public void success1() {
            //given
            Pageable pageable = PageRequest.of(0, 5);
            List<UserQuestionAnswer> userQuestions = List.of(publicUserQuestionAnswer, publicUserQuestionAnswer);

            given(userQuestionAnswerRepository.findAllUserQuestionAnswer(anyLong(), any(), any())).willReturn(userQuestions);

            //when
            List<UserQuestionAnswer> results = userQuestionAnswerService.getUserQuestionAnswers(user.getId(), QuestionType.PUBLIC, pageable);

            //then
            verify(userQuestionAnswerRepository).findAllUserQuestionAnswer(anyLong(), any(), any());
            assertThat(results.size()).isEqualTo(userQuestions.size());
        }
    }

    @Nested
    @DisplayName("getUserQuestionAnswer 테스트")
    class getUserQuestionAnswer {

        @Test
        @DisplayName("id가 주어지면 UserQuestionAnswer을 조회할 수 있다")
        public void success1() {
            //given
            given(userQuestionAnswerRepository.find(anyLong())).willReturn(Optional.of(publicUserQuestionAnswer));
            Long validId = publicUserQuestionAnswer.getId();

            //when
            UserQuestionAnswer userQuestionAnswer = userQuestionAnswerService.getUserQuestionAnswer(validId);

            //then
            verify(userQuestionAnswerRepository).find(anyLong());
            assertThat(userQuestionAnswer).isEqualTo(publicUserQuestionAnswer);
        }

        @Test
        @DisplayName("userQuestionId와 userId가 주어지면 UserQuestionAnswer을 조회할 수 있다")
        public void success2() {
            //given
            given(userQuestionAnswerRepository.find(anyLong(), anyLong())).willReturn(Optional.of(privateUserQuestionAnswer));
            Long userQuestionId = privateUserQuestion.getId();
            Long userId = user.getId();

            //when
            UserQuestionAnswer userQuestionAnswer = userQuestionAnswerService.getUserQuestionAnswer(userQuestionId, userId);

            //then
            verify(userQuestionAnswerRepository).find(anyLong(), anyLong());
            assertThat(userQuestionAnswer.getUserQuestion()).isEqualTo(privateUserQuestion);
            assertThat(userQuestionAnswer.getUser().getId()).isEqualTo(userId);
        }

        @Test
        @DisplayName("올바르지 않은 id가 주어지면 예외를 반환한다")
        public void fail1() {
            //given
            given(userQuestionAnswerRepository.find(anyLong())).willReturn(Optional.empty());
            Long invalidId = 100L;

            //when
            assertThatThrownBy(() -> userQuestionAnswerService.getUserQuestionAnswer(invalidId))
                    .isInstanceOf(ApiException.class);

            //then
            verify(userQuestionAnswerRepository).find(anyLong());
        }

        @Test
        @DisplayName("올바르지 않은 userQuestionId와 userId가 예외를 반환한다")
        public void fail2() {
            //given
            given(userQuestionAnswerRepository.find(anyLong(), anyLong())).willReturn(Optional.empty());
            Long invalidUserQuestionId = privateUserQuestion.getId();
            Long invalidUserId = user.getId();

            //when
            assertThatThrownBy(() -> userQuestionAnswerService.getUserQuestionAnswer(invalidUserQuestionId, invalidUserId))
                    .isInstanceOf(ApiException.class);

            //then
            verify(userQuestionAnswerRepository).find(anyLong(), anyLong());
        }
    }


    @Nested
    @DisplayName("update 테스트")
    class isWriter {

        @Test
        @DisplayName("UserQuestionAnswer의 작성자면 true를 반환한다")
        public void success1() {
            //given
            given(userQuestionAnswerRepository.find(anyLong())).willReturn(Optional.of(privateUserQuestionAnswer));
            User writer = privateUserQuestionAnswer.getUser();

            //when
            boolean result = userQuestionAnswerService.isWriter(privateUserQuestionAnswer.getId(), writer.getId());

            //then
            verify(userQuestionAnswerRepository).find(anyLong());
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("UserQuestionAnswer의 작성자가 아니면 false를 반환한다")
        public void success2() {
            //given
            given(userQuestionAnswerRepository.find(anyLong())).willReturn(Optional.of(privateUserQuestionAnswer));
            User notWriter = user2;

            //when
            boolean result = userQuestionAnswerService.isWriter(privateUserQuestionAnswer.getId(), notWriter.getId());

            //then
            verify(userQuestionAnswerRepository).find(anyLong());
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("update 테스트")
    class update {

        @Test
        @DisplayName("PrivateQuestion에 대한 답변을 수정할 수 있다")
        public void success1() {
            //given
            given(userQuestionAnswerRepository.find(anyLong())).willReturn(Optional.of(privateUserQuestionAnswer));
            String updatedAnswer = "updated answer";

            //when
            userQuestionAnswerService.update(privateUserQuestionAnswer.getId(), updatedAnswer);

            //then
            verify(userQuestionAnswerRepository).find(anyLong());
            assertThat(privateUserQuestionAnswer.getAnswer()).isEqualTo(updatedAnswer);
        }

        @Test
        @DisplayName("PublicQuestion에 대한 답변은 수정할 수 없다")
        public void fail1() {
            //given
            given(userQuestionAnswerRepository.find(anyLong())).willReturn(Optional.of(publicUserQuestionAnswer));
            String updatedAnswer = "updated answer";

            //when
            assertThatThrownBy(() -> userQuestionAnswerService.update(publicUserQuestionAnswer.getId(), updatedAnswer))
                    .isInstanceOf(IllegalArgumentException.class);

            //then
            verify(userQuestionAnswerRepository).find(anyLong());
        }
    }

}