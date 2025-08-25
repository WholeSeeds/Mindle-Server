package com.wholeseeds.mindle.domain.complaint.repository.custom;

import java.util.List;

import com.wholeseeds.mindle.domain.complaint.dto.CategoryRow;

public interface CategoryRepositoryCustom {
	List<CategoryRow> findAllFlat();
}
