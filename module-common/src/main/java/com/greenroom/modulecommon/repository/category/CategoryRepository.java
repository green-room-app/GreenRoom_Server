package com.greenroom.modulecommon.repository.category;

import com.greenroom.modulecommon.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
