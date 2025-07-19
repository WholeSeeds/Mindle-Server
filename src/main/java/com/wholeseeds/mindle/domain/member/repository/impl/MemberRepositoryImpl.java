package com.wholeseeds.mindle.domain.member.repository.impl;

import java.util.Optional;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.entity.QMember;
import com.wholeseeds.mindle.domain.member.repository.custom.MemberRepositoryCustom;

import jakarta.persistence.EntityManager;

public class MemberRepositoryImpl extends JpaBaseRepositoryImpl<Member, Long> implements MemberRepositoryCustom {

	private static final QMember member = QMember.member;

	public MemberRepositoryImpl(EntityManager em) {
		super(Member.class, em, member, member.id, member.deletedAt);
	}

	@Override
	public Optional<Member> findByFirebaseUid(String firebaseUid) {
		return Optional.ofNullable(queryFactory
			.selectFrom(member)
			.where(member.firebaseUid.eq(firebaseUid))
			.fetchOne());
	}

	@Override
	public Optional<Member> findByFirebaseUidNotDeleted(String firebaseUid) {
		return Optional.ofNullable(queryFactory
			.selectFrom(member)
			.where(
				member.firebaseUid.eq(firebaseUid),
				member.deletedAt.isNull()
			)
			.fetchOne());
	}

	@Override
	public Optional<Integer> findMaxUserNicknameSuffix() {
		NumberTemplate<Integer> numericSuffix = Expressions.numberTemplate(
			Integer.class,
			"CAST(SUBSTRING({0}, {1}) AS INTEGER)",
			member.nickname,
			Expressions.constant(5)
		);

		Integer result = queryFactory
			.select(numericSuffix.max())
			.from(member)
			.where(
				member.deletedAt.isNull(),
				member.nickname.startsWith("user"),
				Expressions.stringTemplate("SUBSTRING({0}, 1, 4)", member.nickname).eq("user"),
				numericSuffix.isNotNull()
			)
			.fetchOne();

		return Optional.ofNullable(result);
	}

	@Override
	public boolean existsByNicknameAndNotId(String nickname, Long excludeId) {
		BooleanExpression sameNickname = member.nickname.eq(nickname);
		BooleanExpression notCurrentMember = member.id.ne(excludeId);
		BooleanExpression notDeleted = member.deletedAt.isNull();

		return queryFactory
			.selectOne()
			.from(member)
			.where(sameNickname, notCurrentMember, notDeleted)
			.fetchFirst() != null;
	}
}
