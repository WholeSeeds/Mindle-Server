package com.wholeseeds.mindle.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wholeseeds.mindle.global.interceptor.FirebaseAuthInterceptor;
import com.wholeseeds.mindle.global.interceptor.LoggingInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final LoggingInterceptor loggingInterceptor;
	private final FirebaseAuthInterceptor firebaseAuthInterceptor;

	public WebConfig(
		LoggingInterceptor loggingInterceptor,
		FirebaseAuthInterceptor firebaseAuthInterceptor
	) {
		this.loggingInterceptor = loggingInterceptor;
		this.firebaseAuthInterceptor = firebaseAuthInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loggingInterceptor)
			.addPathPatterns("/**");

		registry.addInterceptor(firebaseAuthInterceptor)
			.addPathPatterns("/api/**"); // 또는 /** 로 전체 요청 적용 가능
	}
}
