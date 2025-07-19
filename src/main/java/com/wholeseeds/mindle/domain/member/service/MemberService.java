package com.wholeseeds.mindle.domain.member.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.auth.FirebaseToken;
import com.wholeseeds.mindle.domain.location.entity.Subdistrict;
import com.wholeseeds.mindle.domain.location.exception.SubdistrictNotFoundException;
import com.wholeseeds.mindle.domain.location.repository.SubdistrictRepository;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.exception.DuplicateNicknameException;
import com.wholeseeds.mindle.domain.member.repository.MemberRepository;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final SubdistrictRepository subdistrictRepository;

	/**
	 * Firebase UID 기반으로 로그인 (또는 자동 회원가입) 처리
	 */
	@Transactional
	public Member login(FirebaseToken firebaseToken) {
		String uid = firebaseToken.getUid();
		String email = firebaseToken.getEmail();
		Map<String, Object> claims = firebaseToken.getClaims();
		String phoneNumber = (String)claims.getOrDefault("phone_number", null);

		return memberRepository.findByFirebaseUidNotDeleted(uid)
			.orElseGet(() -> {
				// 닉네임 없으면 userN 할당
				String nickname = generateDefaultNickname();

				Member newMember = Member.builder()
					.firebaseUid(uid)
					.email(email)
					.phone(phoneNumber)
					.provider(extractProvider(claims))
					.nickname(nickname)
					.build();

				return memberRepository.save(newMember);
			});
	}

	/**
	 * 닉네임 변경
	 * - 중복 체크 후 변경
	 *
	 * @param member      현재 회원 정보
	 * @param newNickname 새 닉네임
	 */
	@Transactional
	public void updateNickname(Member member, String newNickname) {
		if (memberRepository.existsByNicknameAndNotId(newNickname, member.getId())) {
			throw new DuplicateNicknameException();
		}
		member.updateNickname(newNickname);
	}

	/**
	 * 기본 동네 설정
	 * - Firebase UID로 회원 조회 후 동네 설정
	 * @param member 현재 회원 정보
	 * @param subdistrictId 동네 ID
	 */
	@Transactional
	public void updateSubdistrict(Member member, Long subdistrictId) {
		Subdistrict subdistrict = subdistrictRepository.findByIdNotDeleted(subdistrictId)
			.orElseThrow(SubdistrictNotFoundException::new);
		member.updateSubdistrict(subdistrict);
	}

	/**
	 * 회원 탈퇴 처리
	 * - soft delete 방식으로 삭제
	 * - deletedAt 필드에 현재 시간 저장
	 * @param member 현재 회원 정보
	 */
	@Transactional
	public void withdraw(Member member) {
		if (!member.isDeleted()) {
			member.softDelete();
		}
	}

	/**
	 * Firebase 토큰에서 provider 추출
	 * @param claims Firebase 토큰의 클레임
	 * @return provider 이름 (예: "google.com", "facebook.com" 등)
	 */
	private String extractProvider(Map<String, Object> claims) {
		Object firebaseObj = claims.get("firebase");
		if (firebaseObj instanceof Map<?, ?> map) {
			Object rawProvider = map.get("sign_in_provider");
			if (rawProvider instanceof String s) {
				return s;
			}
		}
		return "unknown";
	}

	/**
	 * 기본 닉네임 생성 (user1, user2, ...)
	 * - 현재 최대 숫자 + 1로 생성
	 * - 예: user123 → user124
	 * @return 생성된 닉네임
	 */
	private String generateDefaultNickname() {
		int next = memberRepository
			.findMaxUserNicknameSuffix()
			.orElse(0) + 1;
		return "user" + next;
	}
}
