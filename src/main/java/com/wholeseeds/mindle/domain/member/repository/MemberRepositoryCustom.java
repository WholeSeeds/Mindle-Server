package com.wholeseeds.mindle.domain.member.repository;

import java.util.Optional;

import com.wholeseeds.mindle.domain.member.entity.Member;

public interface MemberRepositoryCustom {

	/**
	 * Firebase UID로 회원을 조회하되, 삭제되지 않은 회원만 반환
	 *
	 * @param firebaseUid Firebase UID
	 * @return 삭제되지 않은 회원이 존재하면 Optional에 포함, 없으면 Optional.empty()
	 */
	Optional<Member> findByFirebaseUidNotDeleted(String firebaseUid);

	/**
	 * "user"로 시작하는 닉네임에서 숫자 부분의 최대값을 찾는 메서드
	 * 예: "user123", "user456" → 456
	 * "user"로 시작하지 않거나 숫자가 없는 경우는 제외
	 *
	 * @return 최대 숫자 부분을 Optional로 감싸서 반환, 없으면 Optional.empty()
	 */
	Optional<Integer> findMaxUserNicknameSuffix();
}
