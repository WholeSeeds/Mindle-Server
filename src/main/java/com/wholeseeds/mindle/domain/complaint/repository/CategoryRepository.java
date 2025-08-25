package com.wholeseeds.mindle.domain.complaint.repository;

import com.wholeseeds.mindle.common.repository.JpaBaseRepository;
import com.wholeseeds.mindle.domain.complaint.entity.Category;
import com.wholeseeds.mindle.domain.complaint.repository.custom.CategoryRepositoryCustom;

public interface CategoryRepository extends JpaBaseRepository<Category, Long>, CategoryRepositoryCustom {

}
