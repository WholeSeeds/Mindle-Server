package com.wholeseeds.mindle.domain.member.repository;

import java.util.Optional;

import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.entity.QMember;

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
}
