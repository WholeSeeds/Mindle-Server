package com.wholeseeds.mindle.domain.category.repository.custom;

import java.util.List;

import com.wholeseeds.mindle.domain.category.dto.CategoryRow;

public interface CategoryRepositoryCustom {
	List<CategoryRow> findAllFlat();
}
