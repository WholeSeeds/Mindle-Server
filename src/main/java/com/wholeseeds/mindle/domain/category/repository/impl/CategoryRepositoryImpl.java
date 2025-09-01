package com.wholeseeds.mindle.domain.category.repository.impl;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.category.dto.CategoryRow;
import com.wholeseeds.mindle.domain.category.entity.Category;
import com.wholeseeds.mindle.domain.category.entity.QCategory;
import com.wholeseeds.mindle.domain.category.repository.custom.CategoryRepositoryCustom;

import jakarta.persistence.EntityManager;

public class CategoryRepositoryImpl extends JpaBaseRepositoryImpl<Category, Long> implements CategoryRepositoryCustom {
	private static final QCategory category = QCategory.category;

	public CategoryRepositoryImpl(EntityManager em) {
		super(Category.class, em, category, category.id, category.deletedAt);
	}

	@Override
	public List<CategoryRow> findAllFlat() {
		return queryFactory
			.select(Projections.constructor(
				CategoryRow.class,
				category.id,
				category.name,
				category.description,
				category.parent.id
			))
			.from(category)
			.where(category.deletedAt.isNull())
			.orderBy(category.id.asc())
			.fetch();
	}
}
