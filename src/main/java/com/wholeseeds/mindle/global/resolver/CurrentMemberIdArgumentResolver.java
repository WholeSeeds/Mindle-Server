package com.wholeseeds.mindle.global.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.wholeseeds.mindle.common.annotation.CurrentMemberId;
import com.wholeseeds.mindle.domain.auth.exception.MissingCurrentMemberException;
import com.wholeseeds.mindle.domain.member.entity.Member;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;

/**
 * `@CurrentMemberId`가 붙은 파라미터에 대해 현재 인증된 회원 정보를 주입하는 Argument Resolver
 * - request에 저장된 currentMember를 가져와서 반환
 * - currentMember가 없으면 예외 발생
 */
@Component
public class CurrentMemberIdArgumentResolver implements HandlerMethodArgumentResolver {

	private static final String CURRENT_MEMBER_KEY = "currentMember";

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(CurrentMemberId.class)
			&& parameter.getParameterType().equals(Long.class);
	}

	@Override
	public Object resolveArgument(
		@NonNull MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory
	) {
		HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
		Object member = request.getAttribute(CURRENT_MEMBER_KEY);

		if (member == null) {
			throw new MissingCurrentMemberException();
		}

		return ((Member) member).getId();
	}
}
