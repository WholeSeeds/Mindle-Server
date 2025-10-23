package com.wholeseeds.mindle.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wholeseeds.mindle.global.interceptor.FirebaseAuthInterceptor;
import com.wholeseeds.mindle.global.interceptor.LoggingInterceptor;
import com.wholeseeds.mindle.global.interceptor.RequireAuthInterceptor;
import com.wholeseeds.mindle.global.resolver.CurrentMemberIdArgumentResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	// Argument Resolver
	private final CurrentMemberIdArgumentResolver currentMemberIdArgumentResolver;

	// Interceptor
	private final LoggingInterceptor loggingInterceptor;
	private final FirebaseAuthInterceptor firebaseAuthInterceptor;
	private final RequireAuthInterceptor requireAuthInterceptor;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(currentMemberIdArgumentResolver);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		String apiPath = "/api/**";
		String healthPath = "/api/health/**";

		registry.addInterceptor(loggingInterceptor)
			.addPathPatterns(apiPath);

		registry.addInterceptor(firebaseAuthInterceptor)
			.addPathPatterns(apiPath)
			.excludePathPatterns(healthPath);

		registry.addInterceptor(requireAuthInterceptor)
			.addPathPatterns(apiPath)
			.excludePathPatterns(healthPath);
	}

	// swagger 이미지 업로드 때 사용됨
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		for (HttpMessageConverter<?> conv : converters) {
			if (conv instanceof MappingJackson2HttpMessageConverter jackson) {
				// JSON 기본 지원 타입
				List<MediaType> mts = new ArrayList<>(jackson.getSupportedMediaTypes());
				// octet-stream을 JSON 파이프라인에 추가
				mts.add(MediaType.APPLICATION_OCTET_STREAM);
				jackson.setSupportedMediaTypes(mts);
				break;  // 한 번만 적용
			}
		}
	}
}
