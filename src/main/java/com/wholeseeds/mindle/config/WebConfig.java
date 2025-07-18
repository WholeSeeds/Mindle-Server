package com.wholeseeds.mindle.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wholeseeds.mindle.global.interceptor.FirebaseAuthInterceptor;
import com.wholeseeds.mindle.global.interceptor.LoggingInterceptor;
import com.wholeseeds.mindle.global.interceptor.RequireAuthInterceptor;
import com.wholeseeds.mindle.global.resolver.CurrentMemberArgumentResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	// Argument Resolver
	private final CurrentMemberArgumentResolver currentMemberArgumentResolver;

	// Interceptor
	private final LoggingInterceptor loggingInterceptor;
	private final FirebaseAuthInterceptor firebaseAuthInterceptor;
	private final RequireAuthInterceptor requireAuthInterceptor;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(currentMemberArgumentResolver);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		String apiPath = "/api/**";

		registry.addInterceptor(loggingInterceptor)
			.addPathPatterns(apiPath);

		registry.addInterceptor(firebaseAuthInterceptor)
			.addPathPatterns(apiPath);

		registry.addInterceptor(requireAuthInterceptor)
			.addPathPatterns(apiPath);
	}
}
