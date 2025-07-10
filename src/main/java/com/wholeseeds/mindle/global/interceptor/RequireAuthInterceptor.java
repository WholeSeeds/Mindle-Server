package com.wholeseeds.mindle.global.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.google.firebase.auth.FirebaseToken;
import com.wholeseeds.mindle.common.annotation.RequireAuth;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.exception.MemberNotFoundException;
import com.wholeseeds.mindle.domain.member.repository.MemberRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * @RequireAuth가 붙은 요청에 대해 Firebase 인증 토큰을 검사하고, 해당 회원이 존재하는지 확인하는 인터셉터
 * - Firebase 토큰이 없으면 예외 발생
 * - 해당 uid로 가입된 회원이 없으면 예외 발생
 * - 인증된 회원 정보를 request에 저장
 */
@Component
@RequiredArgsConstructor
public class RequireAuthInterceptor implements HandlerInterceptor {

	private final MemberRepository memberRepository;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

		if (!(handler instanceof HandlerMethod method)) {
			return true;
		}

		// @RequireAuth가 붙지 않은 요청이면 무시
		if (!requiresAuth(method)) {
			return true;
		}

		// Firebase 토큰이 없는 경우
		FirebaseToken firebaseToken = (FirebaseToken) request.getAttribute("firebaseToken");
		if (firebaseToken == null) {
			throw new MemberNotFoundException();
		}

		// 해당 uid로 가입된 회원이 존재하는지 확인
		String firebaseUid = firebaseToken.getUid();
		Member member = memberRepository.findByFirebaseUidNotDeleted(firebaseUid)
			.orElseThrow(MemberNotFoundException::new);

		// request에 저장
		request.setAttribute("currentMember", member);

		return true;
	}

	private boolean requiresAuth(HandlerMethod method) {
		return method.getMethod().isAnnotationPresent(RequireAuth.class)
			|| method.getBeanType().isAnnotationPresent(RequireAuth.class);
	}
}
