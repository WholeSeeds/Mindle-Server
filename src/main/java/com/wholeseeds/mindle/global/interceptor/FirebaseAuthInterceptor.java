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
