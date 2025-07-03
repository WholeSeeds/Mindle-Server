package com.wholeseeds.mindle.domain.complaint.repository;

import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.complaint.entity.Category;
import com.wholeseeds.mindle.domain.complaint.entity.QCategory;

import jakarta.persistence.EntityManager;

public class CategoryRepositoryImpl extends JpaBaseRepositoryImpl<Category, Long> implements CategoryRepositoryCustom {
	private static final QCategory category = QCategory.category;

	public CategoryRepositoryImpl(EntityManager em) {
		super(Category.class, em, category, category.id, category.deletedAt);
	}
}
