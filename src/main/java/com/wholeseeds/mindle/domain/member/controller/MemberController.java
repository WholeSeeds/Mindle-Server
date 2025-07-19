package com.wholeseeds.mindle.domain.member.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseToken;
import com.wholeseeds.mindle.common.annotation.CurrentMemberId;
import com.wholeseeds.mindle.common.annotation.RequireAuth;
import com.wholeseeds.mindle.common.util.ResponseTemplate;
import com.wholeseeds.mindle.domain.member.dto.request.UpdateNicknameRequestDto;
import com.wholeseeds.mindle.domain.member.dto.request.UpdateNotificationSettingRequestDto;
import com.wholeseeds.mindle.domain.member.dto.request.UpdateSubdistrictRequestDto;
import com.wholeseeds.mindle.domain.member.dto.response.MemberResponseDto;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.mapper.MemberMapper;
import com.wholeseeds.mindle.domain.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Tag(
	name = "회원",
	description = "회원 API (로그인 또는 회원가입, 내 정보 조회, 닉네임 설정, 회원 동네 설정, 알림 설정 변경, 회원 탈퇴)"
)
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final MemberMapper memberMapper;
	private final ResponseTemplate responseTemplate;

	/**
	 * 로그인 또는 회원가입
	 */
	@Operation(
		summary = "로그인 또는 회원가입",
		description = "Firebase ID Token을 파싱하여, uid로 회원을 로그인하거나 회원가입 처리합니다."
	)
	@ApiResponse(
		responseCode = "200",
		description = "로그인 또는 회원가입 성공",
		content = @Content(schema = @Schema(implementation = MemberResponseDto.class))
	)
	@GetMapping("/login")
	public ResponseEntity<Map<String, Object>> login(
		HttpServletRequest request
	) {
		FirebaseToken firebaseToken = (FirebaseToken)request.getAttribute("firebaseToken");
		Member member = memberService.login(firebaseToken);
		MemberResponseDto dto = memberMapper.toMemberResponseDto(member);
		return responseTemplate.success(dto, HttpStatus.OK);
	}

	/**
	 * 내 정보 조회
	 */
	@Operation(
		summary = "내 정보 조회",
		description = "현재 로그인된 회원의 정보를 조회합니다."
	)
	@ApiResponse(
		responseCode = "200",
		description = "회원 정보 반환",
		content = @Content(schema = @Schema(implementation = MemberResponseDto.class))
	)
	@RequireAuth
	@GetMapping("/my-info")
	public ResponseEntity<Map<String, Object>> getMyInfo(
		@Parameter(hidden = true) @CurrentMemberId Long memberId
	) {
		Member member = memberService.getMember(memberId);
		MemberResponseDto dto = memberMapper.toMemberResponseDto(member);
		return responseTemplate.success(dto, HttpStatus.OK);
	}

	/**
	 * 닉네임 변경
	 */
	@Operation(
		summary = "닉네임 설정",
		description = "회원의 닉네임을 설정하거나 변경합니다."
	)
	@ApiResponse(
		responseCode = "200",
		description = "닉네임 변경 성공",
		content = @Content(schema = @Schema(implementation = MemberResponseDto.class))
	)
	@RequireAuth
	@PatchMapping("/nickname")
	public ResponseEntity<Map<String, Object>> updateNickname(
		@Parameter(hidden = true) @CurrentMemberId Long memberId,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "변경할 닉네임",
			required = true,
			content = @Content(schema = @Schema(implementation = UpdateNicknameRequestDto.class))
		)
		@Valid @RequestBody UpdateNicknameRequestDto dto
	) {
		Member updatedMember = memberService.updateNickname(memberId, dto.getNickname());
		MemberResponseDto responseDto = memberMapper.toMemberResponseDto(updatedMember);
		return responseTemplate.success(responseDto, HttpStatus.OK);
	}

	/**
	 * 회원 동네 설정
	 */
	@Operation(
		summary = "회원 동네 설정",
		description = "회원의 기본 동네(subdistrict)를 설정합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "동네 설정 성공",
		content = @Content(schema = @Schema(implementation = MemberResponseDto.class))
	)
	@RequireAuth
	@PatchMapping("/subdistrict")
	public ResponseEntity<Map<String, Object>> updateSubdistrict(
		@Parameter(hidden = true) @CurrentMemberId Long memberId,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "설정할 동네 ID", required = true,
			content = @Content(schema = @Schema(implementation = UpdateSubdistrictRequestDto.class))
		)
		@Valid @RequestBody UpdateSubdistrictRequestDto dto
	) {
		Member updatedMember = memberService.updateSubdistrict(memberId, dto.getSubdistrictId());
		MemberResponseDto responseDto = memberMapper.toMemberResponseDto(updatedMember);
		return responseTemplate.success(responseDto, HttpStatus.OK);
	}

	/**
	 * 알림 설정 변경
	 * - PUSH 또는 IN_APP 알림 설정을 변경합니다.
	 */
	@Operation(
		summary = "알림 설정 변경",
		description = "PUSH 또는 IN_APP 알림 설정을 변경합니다."
	)
	@ApiResponse(
		responseCode = "200",
		description = "알림 설정 변경 성공",
		content = @Content(schema = @Schema(implementation = MemberResponseDto.class))
	)
	@RequireAuth
	@PatchMapping("/notification")
	public ResponseEntity<Map<String, Object>> updateNotificationSetting(
		@Parameter(hidden = true) @CurrentMemberId Long memberId,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "변경할 알림 설정", required = true,
			content = @Content(schema = @Schema(implementation = UpdateNotificationSettingRequestDto.class))
		)
		@Valid @RequestBody UpdateNotificationSettingRequestDto dto
	) {
		Member updatedMember = memberService.updateNotificationSetting(memberId, dto.getType(), dto.getEnabled());
		MemberResponseDto responseDto = memberMapper.toMemberResponseDto(updatedMember);
		return responseTemplate.success(responseDto, HttpStatus.OK);
	}

	/**
	 * 회원 탈퇴 (Soft Delete)
	 */
	@Operation(
		summary = "회원 탈퇴",
		description = "회원을 탈퇴 처리(Soft Delete) 합니다."
	)
	@ApiResponse(
		responseCode = "200",
		description = "회원 탈퇴 성공"
	)
	@RequireAuth
	@DeleteMapping("/withdraw")
	public ResponseEntity<Map<String, Object>> withdraw(
		@Parameter(hidden = true) @CurrentMemberId Long memberId
	) {
		memberService.withdraw(memberId);
		return responseTemplate.success(null, HttpStatus.OK);
	}
}
