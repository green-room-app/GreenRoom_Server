package com.greenroom.moduleapi.service.category;

import com.greenroom.modulecommon.entity.category.Category;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.repository.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.exception.EnumApiException.NOT_FOUND;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category getCategory(Long id) {
        checkArgument(id != null, "id 값은 필수입니다.");

        return categoryRepository.findById(id)
            .orElseThrow(() -> new ApiException(NOT_FOUND, Category.class, String.format("id = %s", id)));
    }
}
