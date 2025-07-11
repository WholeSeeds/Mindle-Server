package com.wholeseeds.mindle.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * application/octet-stream 타입을 읽을 수 있도록 설정해줌
 * swagger 가 이미지 업로드시에 meta 파트에 application/octet-stream 헤더를 잘못 붙여 보내는데
 * 서버가 이를 json 으로 인식할 수 있도록 변경
 */
import com.wholeseeds.mindle.global.interceptor.LoggingInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final LoggingInterceptor loggingInterceptor;

	public WebConfig(LoggingInterceptor loggingInterceptor) {
		this.loggingInterceptor = loggingInterceptor;
	}
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loggingInterceptor);
	}
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
