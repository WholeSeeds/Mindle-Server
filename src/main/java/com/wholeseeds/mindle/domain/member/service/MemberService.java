package com.wholeseeds.mindle.domain.member.service;

import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.auth.FirebaseToken;
import com.wholeseeds.mindle.domain.location.entity.Subdistrict;
import com.wholeseeds.mindle.domain.location.exception.SubdistrictNotFoundException;
import com.wholeseeds.mindle.domain.location.repository.SubdistrictRepository;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.enums.NotificationType;
import com.wholeseeds.mindle.domain.member.exception.DuplicateNicknameException;
import com.wholeseeds.mindle.domain.member.exception.MemberNotFoundException;
import com.wholeseeds.mindle.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final SubdistrictRepository subdistrictRepository;

	/**
	 * 회원 ID로 탈퇴하지 않은 회원 객체 조회
	 * @param id 회원 ID
	 * @return Member 객체
	 */
	public Member getMember(Long id) {
		return memberRepository.findByIdNotDeleted(id)
			.orElseThrow(MemberNotFoundException::new);
	}

	/**
	 * Firebase UID 기반으로 로그인 (또는 자동 회원가입) 처리
	 * @param firebaseToken Firebase 인증 토큰
	 * @return 로그인 또는 생성된 Member 객체
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
	 * @param memberId 회원 ID
	 * @param newNickname 새 닉네임
	 * @return 업데이트된 Member 객체
	 */
	@Transactional
	public Member updateNickname(Long memberId, String newNickname) {
		if (memberRepository.existsByNicknameAndNotId(newNickname, memberId)) {
			throw new DuplicateNicknameException();
		}
		Member member = getMember(memberId);
		member.updateNickname(newNickname);
		return member;
	}

	/**
	 * 기본 동네 설정
	 * @param memberId 회원 ID
	 * @param subdistrictId 동네 ID
	 * @return 업데이트된 Member 객체
	 */
	@Transactional
	public Member updateSubdistrict(Long memberId, Long subdistrictId) {
		Subdistrict subdistrict = subdistrictRepository.findByIdNotDeleted(subdistrictId)
			.orElseThrow(SubdistrictNotFoundException::new);
		Member member = getMember(memberId);
		member.updateSubdistrict(subdistrict);
		return member;
	}

	/**
	 * 알림 설정 업데이트
	 * @param memberId 회원 ID
	 * @param type 알림 타입 (PUSH, IN_APP 등)
	 * @param enabled 알림 활성화 여부
	 * @return 업데이트된 Member 객체
	 */
	@Transactional
	public Member updateNotificationSetting(Long memberId, NotificationType type, boolean enabled) {
		Member member = getMember(memberId);
		if (Objects.requireNonNull(type) == NotificationType.PUSH) {
			member.setNotificationPush(enabled);
		} else if (type == NotificationType.IN_APP) {
			member.setNotificationInapp(enabled);
		}
		return member;
	}

	/**
	 * 회원 탈퇴 처리 (soft delete)
	 *
	 * @param memberId 회원 ID
	 */
	@Transactional
	public void withdraw(Long memberId) {
		Member member = getMember(memberId);
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
	 * @return 생성된 닉네임
	 */
	private String generateDefaultNickname() {
		int next = memberRepository
			.findMaxUserNicknameSuffix()
			.orElse(0) + 1;
		return "user" + next;
	}
}
