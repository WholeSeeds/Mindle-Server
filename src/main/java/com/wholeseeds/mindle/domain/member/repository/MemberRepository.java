package com.wholeseeds.mindle.domain.member.repository;

import org.springframework.stereotype.Repository;

import com.wholeseeds.mindle.common.repository.JpaBaseRepository;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.repository.custom.MemberRepositoryCustom;

@Repository
public interface MemberRepository extends JpaBaseRepository<Member, Long>, MemberRepositoryCustom {
}
