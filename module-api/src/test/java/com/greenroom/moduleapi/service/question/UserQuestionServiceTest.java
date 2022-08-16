package com.greenroom.moduleapi.service.question;

import com.greenroom.moduleapi.service.category.CategoryService;
import com.greenroom.moduleapi.service.user.UserService;
import com.greenroom.modulecommon.entity.category.Category;
import com.greenroom.modulecommon.entity.question.QuestionType;
import com.greenroom.modulecommon.entity.question.UserQuestion;
import com.greenroom.modulecommon.entity.user.OAuthType;
import com.greenroom.modulecommon.entity.user.User;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.repository.question.UserQuestionRepository;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserQuestionServiceTest {

    @InjectMocks
    private UserQuestionServiceImpl userQuestionService;

    @Mock
    private UserQuestionRepository userQuestionRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserService userService;

    private User user, user2;
    private Category category;
    private UserQuestion publicUserQuestion;
    private UserQuestion privateUserQuestion;

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
    }

    @Nested
    @DisplayName("createPublicQuestion 테스트")
    class createPublicQuestion {

        @Test
        @DisplayName("userId, question, categoryId, expiredAt이 주어지면 PublicQuestion을 생성할 수 있다")
        public void success1() {
            //given
            given(userService.getUser(anyLong())).willReturn(user);
            given(categoryService.getCategory(anyLong())).willReturn(category);
            given(userQuestionRepository.save(any())).willReturn(publicUserQuestion);
            String question = publicUserQuestion.getQuestion();

            //when
            userQuestionService.createPublicQuestion(user.getId(),
                                                    question,
                                                    category.getId(),
                                                    LocalDateTime.now());

            //then
            verify(userService).getUser(anyLong());
            verify(categoryService).getCategory(anyLong());
            verify(userQuestionRepository).save(any());
        }

        @Test
        @DisplayName("질문 길이가 50자가 넘어가면 실패한다")
        public void fail1() {
            //given
            String invalidQuestion = "1234567891012345678910123456789101234567891012345678910TEST";

            //when
            assertThatThrownBy(() -> userQuestionService.createPublicQuestion(user.getId(), invalidQuestion, category.getId(), LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class);

            //then
            verify(userService, never()).getUser(anyLong());
            verify(categoryService, never()).getCategory(anyLong());
            verify(userQuestionRepository, never()).save(any());
        }

    }

    @Nested
    @DisplayName("createPrivateQuestion 테스트")
    class createPrivateQuestion {

        @Test
        @DisplayName("userId, question, categoryId이 주어지면 PrivateQuestion을 생성할 수 있다")
        public void success1() {
            //given
            given(userService.getUser(anyLong())).willReturn(user);
            given(categoryService.getCategory(anyLong())).willReturn(category);
            given(userQuestionRepository.save(any())).willReturn(privateUserQuestion);
            String question = privateUserQuestion.getQuestion();

            //when
            userQuestionService.createPrivateQuestion(user.getId(), question, category.getId());

            //then
            verify(userService).getUser(anyLong());
            verify(categoryService).getCategory(anyLong());
            verify(userQuestionRepository).save(any());
        }

        @Test
        @DisplayName("질문 길이가 50자가 넘어가면 실패한다")
        public void fail1() {
            //given
            String invalidQuestion = "1234567891012345678910123456789101234567891012345678910TEST";

            //when
            assertThatThrownBy(() -> userQuestionService.createPrivateQuestion(user.getId(), invalidQuestion, category.getId()))
                    .isInstanceOf(IllegalArgumentException.class);

            //then
            verify(userService, never()).getUser(anyLong());
            verify(categoryService, never()).getCategory(anyLong());
            verify(userQuestionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getUserQuestions 테스트")
    class getUserQuestions {

        @Test
        @DisplayName("categories과 questionType으로 UserQuestion 목록을 조회할 수 있다")
        public void success1() {
            //given
            Pageable pageable = PageRequest.of(0, 5);
            List<UserQuestion> userQuestions = List.of(publicUserQuestion, publicUserQuestion);
            List<Long> categoryIds = List.of(1L, 2L);

            given(userQuestionRepository.findAll(anyList(), any(), any())).willReturn(userQuestions);

            //when
            List<UserQuestion> results = userQuestionService.getUserQuestions(categoryIds, QuestionType.PUBLIC, pageable);

            //then
            verify(userQuestionRepository).findAll(anyList(), any(), any());
            assertThat(results.size()).isEqualTo(userQuestions.size());
        }
    }

    @Nested
    @DisplayName("getUserQuestion 테스트")
    class getUserQuestion {

        @Test
        @DisplayName("id가 주어지면 UserQuestion을 조회할 수 있다")
        public void success1() {
            //given
            given(userQuestionRepository.find(anyLong())).willReturn(Optional.of(privateUserQuestion));
            Long validId = privateUserQuestion.getId();

            //when
            UserQuestion userQuestion = userQuestionService.getUserQuestion(validId);

            //then
            verify(userQuestionRepository).find(anyLong());
            assertThat(userQuestion).isEqualTo(privateUserQuestion);
        }

        @Test
        @DisplayName("존재하지 않는 id가 주어지면 예외를 반환한다")
        public void fail1() {
            //given
            given(userQuestionRepository.find(anyLong())).willReturn(Optional.empty());
            Long invalidId = 100L;

            //when
            assertThatThrownBy(() -> userQuestionService.getUserQuestion(invalidId))
                .isInstanceOf(ApiException.class);

            //then
            verify(userQuestionRepository).find(anyLong());
        }
    }

    @Nested
    @DisplayName("getPopularQuestions 테스트")
    class getPopularQuestions {

        @Test
        @DisplayName("인기있는 PublicQuestion 목록을 조회할 수 있다")
        public void success1() {
            //given
            Pageable pageable = PageRequest.of(0, 5);
            List<UserQuestion> userQuestions = List.of(publicUserQuestion, publicUserQuestion);

            given(userQuestionRepository.findPopularQuestions(any())).willReturn(userQuestions);

            //when
            List<UserQuestion> results = userQuestionService.getPopularQuestions(pageable);

            //then
            verify(userQuestionRepository).findPopularQuestions(any());
            assertThat(results.size()).isEqualTo(userQuestions.size());
        }
    }

    @Nested
    @DisplayName("update 테스트")
    class isWriter {

        @Test
        @DisplayName("UserQuestion의 작성자면 true를 반환한다")
        public void success1() {
            //given
            given(userQuestionRepository.find(anyLong())).willReturn(Optional.of(privateUserQuestion));
            User writer = privateUserQuestion.getUser();

            //when
            boolean result = userQuestionService.isWriter(privateUserQuestion.getId(), writer.getId());

            //then
            verify(userQuestionRepository).find(anyLong());
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("UserQuestion의 작성자가 아니면 false를 반환한다")
        public void success2() {
            //given
            given(userQuestionRepository.find(anyLong())).willReturn(Optional.of(privateUserQuestion));
            User notWriter = user2;

            //when
            boolean result = userQuestionService.isWriter(privateUserQuestion.getId(), notWriter.getId());

            //then
            verify(userQuestionRepository).find(anyLong());
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("update 테스트")
    class update {

        @Test
        @DisplayName("PrivateQuestion을 수정할 수 있다")
        public void success1() {
            //given
            given(userQuestionRepository.find(anyLong())).willReturn(Optional.of(privateUserQuestion));
            given(categoryService.getCategory(anyLong())).willReturn(category);
            String updatedQuestion = "updated question";
            assertThat(privateUserQuestion.getQuestion()).isNotEqualTo(updatedQuestion);

            //when
            userQuestionService.update(privateUserQuestion.getId(), updatedQuestion, category.getId());

            //then
            verify(userQuestionRepository).find(anyLong());
            verify(categoryService).getCategory(anyLong());
            assertThat(privateUserQuestion.getQuestion()).isEqualTo(updatedQuestion);
        }

        @Test
        @DisplayName("PublicQuestion은 수정할 수 없다")
        public void fail1() {
            //given
            given(userQuestionRepository.find(anyLong())).willReturn(Optional.of(publicUserQuestion));
            String updatedQuestion = "updated question";

            //when
            assertThatThrownBy(() -> userQuestionService.update(publicUserQuestion.getId(), updatedQuestion, category.getId()))
                    .isInstanceOf(IllegalArgumentException.class);

            //then
            verify(userQuestionRepository).find(anyLong());
            verify(categoryService, never()).getCategory(anyLong());
        }

        @Test
        @DisplayName("수정하려는 질문 길이가 50자가 넘어가면 실패한다")
        public void fail2() {
            //given
            String invalidQuestion = "1234567891012345678910123456789101234567891012345678910TEST";

            //when
            assertThatThrownBy(() -> userQuestionService.update(privateUserQuestion.getId(), invalidQuestion, category.getId()))
                    .isInstanceOf(IllegalArgumentException.class);

            //then
            verify(userQuestionRepository, never()).find(anyLong());
            verify(categoryService, never()).getCategory(anyLong());
        }
    }

    @Nested
    @DisplayName("deletePrivateQuestion 테스트")
    class deletePrivateQuestion {

        @Test
        @DisplayName("PrivateQuestion을 삭제할 수 있다")
        public void success1() {
            //given
            given(userQuestionRepository.find(anyLong())).willReturn(Optional.of(privateUserQuestion));
            assertThat(privateUserQuestion.isUsed()).isTrue();

            //when
            userQuestionService.deletePrivateQuestion(privateUserQuestion.getId());

            //then
            verify(userQuestionRepository).find(anyLong());
            assertThat(privateUserQuestion.isUsed()).isFalse();
        }

        @Test
        @DisplayName("PublicQuestion으로 deletePrivateQuestion를 호출하면 실패한다")
        public void fail1() {
            //given
            given(userQuestionRepository.find(anyLong())).willReturn(Optional.of(publicUserQuestion));

            //when
            assertThatThrownBy(() -> userQuestionService.deletePrivateQuestion(publicUserQuestion.getId()))
                .isInstanceOf(IllegalArgumentException.class);

            //then
            verify(userQuestionRepository).find(anyLong());
        }
    }
}