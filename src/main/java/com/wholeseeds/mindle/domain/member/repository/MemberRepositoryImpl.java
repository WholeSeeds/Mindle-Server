package com.wholeseeds.mindle.domain.member.repository;

import com.wholeseeds.mindle.common.repository.JpaBaseRepositoryImpl;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.entity.QMember;

import jakarta.persistence.EntityManager;

public class MemberRepositoryImpl extends JpaBaseRepositoryImpl<Member, Long> implements MemberRepositoryCustom {

	private static final QMember member = QMember.member;

	public MemberRepositoryImpl(EntityManager em) {
		super(Member.class, em, member, member.id, member.deletedAt);
	}
}
