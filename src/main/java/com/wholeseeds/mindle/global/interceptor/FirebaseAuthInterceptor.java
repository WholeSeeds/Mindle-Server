package com.wholeseeds.mindle.global.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.wholeseeds.mindle.domain.auth.exception.InvalidTokenException;
import com.wholeseeds.mindle.domain.auth.exception.MissingTokenException;
import com.wholeseeds.mindle.domain.auth.service.FirebaseAuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


/**
 * Firebase 인증 토큰을 검사하는 인터셉터
 * - 요청 헤더에서 Bearer 토큰을 추출하고, Firebase에서 검증
 * - 유효한 토큰이면 요청에 FirebaseToken 객체를 저장
 * - 유효하지 않거나 누락된 경우 예외 발생
 */
@Component
@RequiredArgsConstructor
public class FirebaseAuthInterceptor implements HandlerInterceptor {

	private final FirebaseAuthService firebaseAuthService;

	@Override
	public boolean preHandle(
		@NonNull HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull Object handler
	) {
		// Method 요청만 처리
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}

		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new MissingTokenException();
		}

		String idToken = authHeader.substring(7);

		try {
			FirebaseToken token = firebaseAuthService.verifyIdToken(idToken);
			request.setAttribute("firebaseToken", token);
		} catch (FirebaseAuthException e) {
			throw new InvalidTokenException(); // 만료, 위조 등
		}

		return true;
	}
}
