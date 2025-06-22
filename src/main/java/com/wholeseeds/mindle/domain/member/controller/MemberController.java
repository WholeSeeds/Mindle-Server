package com.wholeseeds.mindle.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseToken;
import com.wholeseeds.mindle.common.auth.annotation.RequireAuth;
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

	@RequireAuth
	@GetMapping("/my-info")
	public ResponseEntity<MemberResponseDto> getMyInfo(HttpServletRequest request) {
		FirebaseToken firebaseToken = (FirebaseToken)request.getAttribute("firebaseToken");
		Member member = memberService.login(firebaseToken);
		return ResponseEntity.ok(memberMapper.toMemberResponseDto(member));
	}
}
