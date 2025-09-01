package com.wholeseeds.mindle.domain.category.repository;

import com.wholeseeds.mindle.common.repository.JpaBaseRepository;
import com.wholeseeds.mindle.domain.category.entity.Category;
import com.wholeseeds.mindle.domain.category.repository.custom.CategoryRepositoryCustom;

public interface CategoryRepository extends JpaBaseRepository<Category, Long>, CategoryRepositoryCustom {

}
