package com.wholeseeds.mindle.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wholeseeds.mindle.global.interceptor.FirebaseAuthInterceptor;
import com.wholeseeds.mindle.global.interceptor.LoggingInterceptor;
import com.wholeseeds.mindle.global.interceptor.RequireAuthInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final LoggingInterceptor loggingInterceptor;
	private final FirebaseAuthInterceptor firebaseAuthInterceptor;
	private final RequireAuthInterceptor requireAuthInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loggingInterceptor)
			.addPathPatterns("/**");

		registry.addInterceptor(requireAuthInterceptor)
			.addPathPatterns("/**");

		registry.addInterceptor(firebaseAuthInterceptor)
			.addPathPatterns("/api/**");
	}
}
