package com.wholeseeds.mindle.domain.moderation.repository.impl;

import java.util.List;

import com.querydsl.core.types.dsl.NumberExpression;
import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.moderation.entity.Profanity;
import com.wholeseeds.mindle.domain.moderation.entity.QProfanity;
import com.wholeseeds.mindle.domain.moderation.repository.custom.ProfanityRepositoryCustom;

import jakarta.persistence.EntityManager;

public class ProfanityRepositoryImpl
	extends JpaBaseRepositoryImpl<Profanity, Long>
	implements ProfanityRepositoryCustom {

	private static final QProfanity P = QProfanity.profanity;

	public ProfanityRepositoryImpl(EntityManager em) {
		super(Profanity.class, em, P, P.id, P.deletedAt);
	}

	@Override
	public List<String> findAllWordsLengthDesc() {
		NumberExpression<Integer> len = P.word.length();
		return queryFactory
			.select(P.word)
			.from(P)
			.where(P.deletedAt.isNull())
			.orderBy(len.desc(), P.word.asc())
			.fetch();
	}
}
