package com.wholeseeds.mindle.domain.member.repository.impl;

import java.util.Optional;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
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
		// "user123"처럼 user 뒤에 숫자가 붙은 닉네임에서 숫자 부분만 추출하는 SQL 표현식
		// CAST(SUBSTRING(nickname, 5, LENGTH(nickname) - 4) AS UNSIGNED)
		// → 5번째 문자부터 끝까지 추출하여 숫자로 변환
		StringTemplate numericSuffix = Expressions.stringTemplate(
			"CAST(SUBSTRING({0}, {1}, LENGTH({0}) - {1} + 1) AS UNSIGNED)",
			member.nickname,
			Expressions.constant(5)
		);

		// 가장 큰 숫자 부분을 가져오는 쿼리 실행
		// 조건:
		// - deletedAt이 null (삭제되지 않은 회원)
		// - nickname이 "user"로 시작
		// - 앞 4글자가 정확히 "user"인 경우만 (예: "user_abc" 같은 예외 방지)
		// - 숫자 부분이 null이 아닌 경우만 (숫자 추출이 가능한 경우)
		String result = queryFactory
			.select(numericSuffix.max())
			.from(member)
			.where(
				member.deletedAt.isNull(),
				member.nickname.startsWith("user"),
				Expressions.stringTemplate("SUBSTRING({0}, 1, 4)", member.nickname).eq("user"),
				numericSuffix.isNotNull()
			)
			.fetchOne();

		// 결과가 없거나 빈 문자열이면 Optional.empty() 반환
		if (result == null || result.isBlank()) {
			return Optional.empty();
		}
		try {
			return Optional.of(Integer.parseInt(result));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}
}
