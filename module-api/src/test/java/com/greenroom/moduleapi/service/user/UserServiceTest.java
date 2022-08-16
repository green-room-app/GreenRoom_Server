package com.greenroom.moduleapi.service.user;

import com.greenroom.moduleapi.service.category.CategoryService;
import com.greenroom.modulecommon.entity.category.Category;
import com.greenroom.modulecommon.entity.user.OAuthType;
import com.greenroom.modulecommon.entity.user.User;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryService categoryService;

    private Category category;
    private User user;

    @BeforeEach
    public void init() {
        category = Category.builder()
                .id(1L)
                .name("mockCategory")
                .build();

        user = User.builder()
                .id(1L)
                .oauthType(OAuthType.KAKAO)
                .oauthId("mockOauthId")
                .category(category)
                .name("mockName")
                .build();
    }

    @Nested
    @DisplayName("create 테스트")
    class create {

        @Test
        @DisplayName("oauthId, oauthType, categoryId, name이 주어지면 User를 생성할 수 있다")
        public void success1() {
            //given
            given(userRepository.save(any())).willReturn(user);
            given(userRepository.exists(anyString())).willReturn(false);
            given(userRepository.existsByOAuthIdAndType(anyString(), any())).willReturn(false);
            given(categoryService.getCategory(anyLong())).willReturn(category);

            String oauthId = user.getOauthId();
            OAuthType oAuthType = user.getOauthType();
            Long categoryId = category.getId();
            String name = "userName";

            //when
            userService.create(oauthId, oAuthType, categoryId, name);

            //then
            verify(userRepository).save(any());
            verify(userRepository).exists(anyString());
            verify(userRepository).existsByOAuthIdAndType(anyString(), any());
            verify(categoryService).getCategory(anyLong());
        }

        @Test
        @DisplayName("이미 존재하는 oauthId와 oauthType인 경우 기존 User를 반환한다")
        public void success2() {
            //given
            given(userRepository.existsByOAuthIdAndType(anyString(), any())).willReturn(true);
            given(userRepository.findByOauthIdAndOauthType(anyString(), any())).willReturn(Optional.ofNullable(user));

            String existedOauthId = user.getOauthId();
            OAuthType existedOauthType = user.getOauthType();
            Long categoryId = category.getId();
            String name = "userName";

            //when
            Long userId = userService.create(existedOauthId, existedOauthType, categoryId, name);

            //then
            assertThat(userId).isEqualTo(user.getId());
            verify(userRepository, never()).save(any());
            verify(userRepository, never()).exists(anyString());
            verify(userRepository).existsByOAuthIdAndType(anyString(), any());
            verify(userRepository).findByOauthIdAndOauthType(anyString(), any());
            verify(categoryService, never()).getCategory(anyLong());
        }

        @Test
        @DisplayName("categoryId가 존재하지 않는 카테고리인 경우 회원 가입에 실패한다")
        public void fail1() {
            //given
            given(userRepository.existsByOAuthIdAndType(anyString(), any())).willReturn(false);
            given(categoryService.getCategory(anyLong())).willThrow(ApiException.class);

            String oauthId = user.getOauthId();
            OAuthType oAuthType = user.getOauthType();
            Long categoryId = category.getId();
            String name = "userName";

            //when
            assertThatThrownBy(() -> userService.create(oauthId, oAuthType, categoryId, name))
                    .isInstanceOf(ApiException.class);

            //then
            verify(userRepository, never()).exists(anyString());
            verify(userRepository).existsByOAuthIdAndType(anyString(), any());
            verify(categoryService).getCategory(anyLong());
            verify(userRepository, never()).findByOauthIdAndOauthType(anyString(), any());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("중복된 닉네임인 경우 회원 가입에 실패한다")
        public void fail2() {
            //given
            given(userRepository.exists(anyString())).willReturn(true);
            given(userRepository.existsByOAuthIdAndType(anyString(), any())).willReturn(false);
            given(categoryService.getCategory(anyLong())).willReturn(category);

            String oauthId = user.getOauthId();
            OAuthType oAuthType = user.getOauthType();
            Long categoryId = category.getId();
            String duplicatedName = "중복닉네임";

            //when
            assertThatThrownBy(() -> userService.create(oauthId, oAuthType, categoryId, duplicatedName))
                    .isInstanceOf(IllegalArgumentException.class);

            //then
            verify(userRepository, never()).save(any());
            verify(userRepository).exists(anyString());
            verify(userRepository).existsByOAuthIdAndType(anyString(), any());
            verify(categoryService).getCategory(anyLong());
        }
    }

    @Nested
    @DisplayName("getUser 테스트")
    class getUser {

        @Test
        @DisplayName("id를 입력하면 User를 반환한다")
        public void success1() {
            //given
            given(userRepository.findById(anyLong())).willReturn(Optional.ofNullable(user));

            //when
            User user = userService.getUser(UserServiceTest.this.user.getId());

            //then
            assertThat(user.getId()).isEqualTo(UserServiceTest.this.user.getId());
            assertThat(user.getOauthId()).isEqualTo(UserServiceTest.this.user.getOauthId());
            assertThat(user.getOauthType()).isEqualTo(UserServiceTest.this.user.getOauthType());
            verify(userRepository).findById(anyLong());
        }

        @Test
        @DisplayName("존재하지 않는 id를 입력하면 예외를 반환한다")
        public void fail1() {
            //given
            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            //when
            assertThatThrownBy(() -> userService.getUser(user.getId())).isInstanceOf(ApiException.class);

            //then
            verify(userRepository).findById(anyLong());
        }
    }

    @Nested
    @DisplayName("getUserByOauthIdAndOauthType 테스트")
    class getUserByOauthIdAndOauthType {

        @Test
        @DisplayName("oauthId와 oauthType을 입력하면 User를 반환한다")
        public void success1() {
            //given
            given(userRepository.findByOauthIdAndOauthType(anyString(), any())).willReturn(Optional.ofNullable(user));

            //when
            User user = userService.getUserByOauthIdAndOauthType(UserServiceTest.this.user.getOauthId(), UserServiceTest.this.user.getOauthType());

            //then
            assertThat(user.getId()).isEqualTo(UserServiceTest.this.user.getId());
            assertThat(user.getOauthId()).isEqualTo(UserServiceTest.this.user.getOauthId());
            assertThat(user.getOauthType()).isEqualTo(UserServiceTest.this.user.getOauthType());
            verify(userRepository).findByOauthIdAndOauthType(anyString(), any());
        }

        @Test
        @DisplayName("올바르지 않은 oauthId나 oauthType을 입력하면 예외를 반환한다")
        public void fail1() {
            //given
            given(userRepository.findByOauthIdAndOauthType(anyString(), any())).willReturn(Optional.empty());

            //when
            assertThatThrownBy(() -> userService.getUserByOauthIdAndOauthType(user.getOauthId(), user.getOauthType()))
                    .isInstanceOf(ApiException.class);

            //then
            verify(userRepository).findByOauthIdAndOauthType(anyString(), any());
        }
    }

    @Nested
    @DisplayName("update 테스트")
    class update {

        @Test
        @DisplayName("중복되지 않은 이름이면 사용자는 이름을 업데이트할 수 있다")
        public void updateName_success1() {
            //given
            given(userRepository.findById(anyLong())).willReturn(Optional.ofNullable(user));
            given(userRepository.exists(anyString())).willReturn(false);

            String newName = "newName";

            //when
            userService.update(user.getId(), newName);

            //then
            assertThat(user.getName()).isEqualTo(newName);
            verify(userRepository).findById(anyLong());
            verify(userRepository).exists(anyString());
        }

        @Test
        @DisplayName("중복된 이름이라도 기존의 본인 닉네임이면 이름을 업데이트할 수 있다")
        public void updateName_success2() {
            //given
            given(userRepository.findById(anyLong())).willReturn(Optional.ofNullable(user));
            given(userRepository.exists(anyString())).willReturn(true);

            String newName = user.getName();

            //when
            userService.update(user.getId(), newName);

            //then
            assertThat(user.getName()).isEqualTo(newName);
            verify(userRepository).findById(anyLong());
            verify(userRepository).exists(anyString());
        }

        @Test
        @DisplayName("중복된 이름이면 예외를 반환한다")
        public void updateName_fail1() {
            //given
            given(userRepository.findById(anyLong())).willReturn(Optional.ofNullable(user));
            given(userRepository.exists(anyString())).willReturn(true);

            String duplicatedName = "중복닉네임";

            //when
            assertThatThrownBy(() -> userService.update(user.getId(), duplicatedName))
                    .isInstanceOf(IllegalArgumentException.class);

            //then
            assertThat(user.getName()).isNotEqualTo(duplicatedName);
            verify(userRepository).findById(anyLong());
            verify(userRepository).exists(anyString());
        }

        @Test
        @DisplayName("카테고리를 업데이트할 수 있다")
        public void updateCategoryId_success1() {
            //given
            given(userRepository.findById(anyLong())).willReturn(Optional.ofNullable(user));
            given(categoryService.getCategory(anyLong())).willReturn(category);

            //when
            userService.update(user.getId(), category.getId());

            //then
            assertThat(user.getCategory()).isEqualTo(category);
            verify(userRepository).findById(anyLong());
            verify(categoryService).getCategory(anyLong());
        }

        @Test
        @DisplayName("존재하지 않는 Category로 업데이트 시 예외를 반환한다")
        public void updateCategoryId_fail1() {
            //given
            given(userRepository.findById(anyLong())).willReturn(Optional.ofNullable(user));
            given(categoryService.getCategory(anyLong())).willThrow(ApiException.class);

            //when
            assertThatThrownBy(() -> userService.update(user.getId(), category.getId()))
                    .isInstanceOf(ApiException.class);

            //then
            verify(userRepository).findById(anyLong());
            verify(categoryService).getCategory(anyLong());
        }

        @Test
        @DisplayName("이름과 카테고리를 업데이트 할 수 있다")
        public void updateNameAndCategoryId_success1() {
            //given
            given(userRepository.findById(anyLong())).willReturn(Optional.ofNullable(user));
            given(userRepository.exists(anyString())).willReturn(false);
            given(categoryService.getCategory(anyLong())).willReturn(category);
            String newName = "newName";

            //when
            userService.update(user.getId(), category.getId(), newName);

            //then
            assertThat(user.getCategory()).isEqualTo(category);
            assertThat(user.getName()).isEqualTo(newName);
            verify(userRepository).findById(anyLong());
            verify(userRepository).exists(anyString());
            verify(categoryService).getCategory(anyLong());
        }

        @Test
        @DisplayName("중복된 이름이라도 기존의 본인 닉네임이면 이름과 카테고리를 업데이트할 수 있다")
        public void updateNameAndCategoryId_success2() {
            //given
            given(userRepository.findById(anyLong())).willReturn(Optional.ofNullable(user));
            given(userRepository.exists(anyString())).willReturn(true);
            given(categoryService.getCategory(anyLong())).willReturn(category);

            String newName = user.getName();

            //when
            userService.update(user.getId(), category.getId(), newName);

            //then
            assertThat(user.getCategory()).isEqualTo(category);
            assertThat(user.getName()).isEqualTo(newName);
            verify(userRepository).findById(anyLong());
            verify(userRepository).exists(anyString());
            verify(categoryService).getCategory(anyLong());
        }

        @Test
        @DisplayName("닉네임이 중복된 경우 예외를 반환한다")
        public void updateNameAndCategoryId_fail1() {
            //given
            given(userRepository.findById(anyLong())).willReturn(Optional.ofNullable(user));
            given(userRepository.exists(anyString())).willReturn(true);
            String duplicatedName = "중복닉네임";

            //when
            assertThatThrownBy(() -> userService.update(user.getId(), category.getId(), duplicatedName))
                    .isInstanceOf(IllegalArgumentException.class);

            //then
            verify(userRepository).findById(anyLong());
            verify(userRepository).exists(anyString());
            verify(categoryService, never()).getCategory(anyLong());
        }

        @Test
        @DisplayName("올바르지 않은 카테고리인 경우 예외를 반환한다")
        public void updateNameAndCategoryId_fail2() {
            //given
            given(userRepository.findById(anyLong())).willReturn(Optional.ofNullable(user));
            given(categoryService.getCategory(anyLong())).willThrow(ApiException.class);
            given(userRepository.exists(anyString())).willReturn(false);

            String newName = "newName";

            //when
            assertThatThrownBy(() -> userService.update(user.getId(), category.getId(), newName))
                    .isInstanceOf(ApiException.class);

            //then
            verify(userRepository).findById(anyLong());
            verify(userRepository).exists(anyString());
            verify(categoryService).getCategory(anyLong());
        }
    }

    @Nested
    @DisplayName("isUniqueName 테스트")
    class isUniqueName {

        @Test
        @DisplayName("중복되지 않은 닉네임이면 true를 반환한다")
        public void success1() {
            //given
            given(userRepository.exists(anyString())).willReturn(false);
            String uniqueName = "유일닉네임";

            //when
            boolean result = userService.isUniqueName(uniqueName);

            //then
            assertThat(result).isTrue();
            verify(userRepository).exists(anyString());
        }

        @Test
        @DisplayName("중복된 닉네임이면 false를 반환한다")
        public void success2() {
            //given
            given(userRepository.exists(anyString())).willReturn(true);
            String duplicatedName = "중복닉네임";

            //when
            boolean result = userService.isUniqueName(duplicatedName);

            //then
            assertThat(result).isFalse();
            verify(userRepository).exists(anyString());
        }
    }

}