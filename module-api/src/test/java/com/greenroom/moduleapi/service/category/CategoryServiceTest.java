package com.greenroom.moduleapi.service.category;

import com.greenroom.modulecommon.entity.category.Category;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.repository.category.CategoryRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void init() {
        category = Category.builder()
                .id(1L)
                .name("mockCategory")
                .build();
    }

    @Nested
    @DisplayName("getCategory 테스트")
    class getCategory {

        @Test
        @DisplayName("categoryId로 카테고리를 조회할 수 있다")
        public void success1() {
            //given
            given(categoryRepository.findById(anyLong())).willReturn(Optional.ofNullable(category));

            //when
            Category category = categoryService.getCategory(CategoryServiceTest.this.category.getId());

            //then
            assertThat(category.getId()).isEqualTo(CategoryServiceTest.this.category.getId());
            assertThat(category.getName()).isEqualTo(CategoryServiceTest.this.category.getName());
            verify(categoryRepository).findById(anyLong());
        }

        @Test
        @DisplayName("유효하지 않은 categoryId로 조회하는 경우 예외를 반환한다")
        public void fail1() {
            //given
            given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());
            Long invalidId = category.getId();

            //when
            assertThatThrownBy(() -> categoryService.getCategory(invalidId))
                    .isInstanceOf(ApiException.class);

            //then
            verify(categoryRepository).findById(anyLong());
        }
    }


}