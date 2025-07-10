package com.wholeseeds.mindle.domain.member.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseToken;
import com.wholeseeds.mindle.common.annotation.RequireAuth;
import com.wholeseeds.mindle.common.util.ResponseTemplate;
import com.wholeseeds.mindle.domain.member.dto.request.NicknameRequestDto;
import com.wholeseeds.mindle.domain.member.dto.response.MemberResponseDto;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.mapper.MemberMapper;
import com.wholeseeds.mindle.domain.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final MemberMapper memberMapper;
	private final ResponseTemplate responseTemplate;

	/**
	 * Firebase UID 기반으로 로그인 (또는 자동 회원가입) 처리
	 */
	@GetMapping("/login")
	public ResponseEntity<Map<String, Object>> login(HttpServletRequest request) {
		FirebaseToken firebaseToken = (FirebaseToken)request.getAttribute("firebaseToken");
		Member member = memberService.login(firebaseToken);
		MemberResponseDto dto = memberMapper.toMemberResponseDto(member);
		return responseTemplate.success(dto, HttpStatus.OK);
	}

	/**
	 * 내 정보 조회
	 */
	@RequireAuth
	@GetMapping("/me")
	public ResponseEntity<Map<String, Object>> getMyInfo(HttpServletRequest request) {
		Member member = (Member) request.getAttribute("currentMember");
		MemberResponseDto dto = memberMapper.toMemberResponseDto(member);
		return responseTemplate.success(dto, HttpStatus.OK);
	}

	@PatchMapping("/nickname")
	public ResponseEntity<Map<String, Object>> updateNickname(
		HttpServletRequest request,
		@Valid @RequestBody NicknameRequestDto dto
	) {
		FirebaseToken firebaseToken = (FirebaseToken) request.getAttribute("firebaseToken");
		Member member = memberService.updateNickname(firebaseToken.getUid(), dto.getNickname());
		MemberResponseDto responseDto = memberMapper.toMemberResponseDto(member);
		return responseTemplate.success(responseDto, HttpStatus.OK);
	}
}
