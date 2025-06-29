package com.wholeseeds.mindle.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseToken;
import com.wholeseeds.mindle.common.auth.annotation.RequireAuth;
import com.wholeseeds.mindle.common.response.ApiResponse;
import com.wholeseeds.mindle.domain.member.dto.response.MemberResponseDto;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.mapper.MemberMapper;
import com.wholeseeds.mindle.domain.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final MemberMapper memberMapper;

	/**
	 * Firebase UID 기반으로 로그인 (또는 자동 회원가입) 처리
	 */
	@GetMapping("/login")
	public ResponseEntity<ApiResponse<MemberResponseDto>> login(HttpServletRequest request) {
		FirebaseToken firebaseToken = (FirebaseToken)request.getAttribute("firebaseToken");
		Member member = memberService.login(firebaseToken);
		MemberResponseDto dto = memberMapper.toMemberResponseDto(member);

		return ResponseEntity.ok(ApiResponse.ok(dto));
	}

	/**
	 * 내 정보 조회
	 */
	@RequireAuth
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<MemberResponseDto>> getMyInfo(HttpServletRequest request) {
		FirebaseToken firebaseToken = (FirebaseToken)request.getAttribute("firebaseToken");
		String firebaseUid = firebaseToken.getUid();

		Member member = memberService.getMyInfo(firebaseUid);
		MemberResponseDto dto = memberMapper.toMemberResponseDto(member);
		return ResponseEntity.ok(ApiResponse.ok(dto));
	}
}
