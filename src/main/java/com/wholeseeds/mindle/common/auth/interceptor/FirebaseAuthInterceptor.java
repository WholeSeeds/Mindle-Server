package com.wholeseeds.mindle.common.auth.interceptor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.wholeseeds.mindle.common.auth.annotation.RequireAuth;
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
	)
		throws Exception {

		// Handler가 메서드인지 확인
		if (!(handler instanceof HandlerMethod handlerMethod)) {
			return true;
		}

		// 애노테이션이 있는지 확인 (클래스나 메서드)
		boolean hasAnnotation =
			handlerMethod.getMethod().isAnnotationPresent(RequireAuth.class)
				|| handlerMethod.getBeanType().isAnnotationPresent(RequireAuth.class);

		if (!hasAnnotation) {
			return true; // 애노테이션 없으면 통과
		}

		// Authorization 헤더에서 Firebase ID Token 추출
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.getWriter().write("Missing or invalid Authorization header");
			return false;
		}

		String idToken = authHeader.substring(7);
		try {
			FirebaseToken token = firebaseAuthService.verifyIdToken(idToken);
			request.setAttribute("firebaseToken", token);
			return true;
		} catch (FirebaseAuthException e) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.getWriter().write("Invalid Firebase ID Token");
			return false;
		}
	}
}
