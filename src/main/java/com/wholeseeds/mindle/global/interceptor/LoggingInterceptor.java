package com.wholeseeds.mindle.global.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 모든 요청에 대해 URL을 로깅하는 인터셉터
 * - 요청이 들어올 때마다 URL을 로그로 남김
 */
@Component
public class LoggingInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		logger.trace("---------------------------------------------");
		logger.trace("URL : {}", request.getRequestURI());
		return true;
	}
}
