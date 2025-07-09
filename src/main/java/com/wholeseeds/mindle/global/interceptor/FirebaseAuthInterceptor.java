package com.wholeseeds.mindle.global.interceptor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.wholeseeds.mindle.common.annotation.RequireAuth;
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
	) throws Exception {

		if (!(handler instanceof HandlerMethod handlerMethod)) {
			return true;
		}

		// Authorization 헤더에서 Firebase ID Token 추출 시도
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String idToken = authHeader.substring(7);
			try {
				FirebaseToken token = firebaseAuthService.verifyIdToken(idToken);
				request.setAttribute("firebaseToken", token);
			} catch (FirebaseAuthException e) {
				// 인증 실패 처리 여부는 아래에서 결정
				if (requiresAuth(handlerMethod)) {
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					response.getWriter().write("Invalid Firebase ID Token");
					return false;
				}
			}
		}

		// @RequireAuth가 붙은 경우인데 firebaseToken이 없는 경우
		if (requiresAuth(handlerMethod) && request.getAttribute("firebaseToken") == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.getWriter().write("Missing or invalid Authorization header");
			return false;
		}

		return true;
	}

	private boolean requiresAuth(HandlerMethod handlerMethod) {
		return handlerMethod.getMethod().isAnnotationPresent(RequireAuth.class)
			|| handlerMethod.getBeanType().isAnnotationPresent(RequireAuth.class);
	}
}
