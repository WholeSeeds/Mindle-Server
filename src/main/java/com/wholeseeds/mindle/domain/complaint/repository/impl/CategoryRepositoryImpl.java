package com.wholeseeds.mindle.domain.complaint.repository.impl;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.complaint.dto.CategoryRow;
import com.wholeseeds.mindle.domain.complaint.entity.Category;
import com.wholeseeds.mindle.domain.complaint.entity.QCategory;
import com.wholeseeds.mindle.domain.complaint.repository.custom.CategoryRepositoryCustom;

import jakarta.persistence.EntityManager;

public class CategoryRepositoryImpl extends JpaBaseRepositoryImpl<Category, Long> implements CategoryRepositoryCustom {
	private static final QCategory category = QCategory.category;

	public CategoryRepositoryImpl(EntityManager em) {
		super(Category.class, em, category, category.id, category.deletedAt);
	}

	@Override
	public List<CategoryRow> findAllFlat() {
		QCategory c = QCategory.category;
		return queryFactory
			.select(Projections.constructor(
				CategoryRow.class,
				c.id,
				c.name,
				c.description,
				c.parent.id
			))
			.from(c)
			.where(c.deletedAt.isNull())
			.orderBy(c.id.asc())
			.fetch();
	}
}
