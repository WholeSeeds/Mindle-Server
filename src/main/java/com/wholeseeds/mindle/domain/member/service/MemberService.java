package com.wholeseeds.mindle.domain.member.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseToken;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.exception.MemberNotFoundException;
import com.wholeseeds.mindle.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	/**
	 * Firebase UID 기반으로 로그인 (또는 자동 회원가입) 처리
	 */
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
	 * Firebase UID 기반으로 내 정보 조회
	 */
	public Member getMyInfo(String firebaseUid) {
		return memberRepository.findByFirebaseUidNotDeleted(firebaseUid)
			.orElseThrow(MemberNotFoundException::new);
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
