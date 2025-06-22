package com.wholeseeds.mindle.domain.member.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseToken;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	// public Member getMember(Long id) {
	// 	return memberRepository.findByIdNotDeleted(id)
	// 		.orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 삭제된 회원입니다."));
	// }
	//
	// public List<Member> getAllActiveMembers() {
	// 	return memberRepository.findAllNotDeleted();
	// }

	/**
	 * Firebase UID 기반으로 로그인 (또는 자동 회원가입) 처리
	 */
	public Member login(FirebaseToken firebaseToken) {
		String uid = firebaseToken.getUid();
		String email = firebaseToken.getEmail();

		Map<String, Object> claims = firebaseToken.getClaims();

		String phoneNumber = (String)claims.getOrDefault("phone_number", null);

		String provider;
		if (claims.containsKey("firebase")) {
			Map<String, Object> firebaseMap = (Map<String, Object>)claims.get("firebase");
			provider = (String)firebaseMap.getOrDefault("sign_in_provider", "unknown");
		} else {
			provider = "unknown";
		}

		return memberRepository.findByFirebaseUidNotDeleted(uid)
			.orElseGet(() -> {
				Member newMember = Member.builder()
					.firebaseUid(uid)
					.email(email)
					.phone(phoneNumber)
					.provider(provider)
					.build();
				return memberRepository.save(newMember);
			});
	}
}
