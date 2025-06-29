package com.wholeseeds.mindle.domain.member.repository;

import java.util.List;
import java.util.Optional;

import com.wholeseeds.mindle.domain.member.entity.Member;

public interface MemberRepositoryCustom {
	Optional<Member> findByIdNotDeleted(Long id);

	List<Member> findAllNotDeleted();

	Optional<Member> findByFirebaseUidNotDeleted(String firebaseUid);

	Optional<Integer> findMaxUserNicknameSuffix();
}
