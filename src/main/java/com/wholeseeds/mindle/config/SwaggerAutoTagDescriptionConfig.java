package com.wholeseeds.mindle.config;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.tags.Tag;

/**
 * Swagger Tag description 자동 생성 설정
 * 모든 API의 태그에 대해 자동으로 설명을 생성합니다.
 * 각 태그의 설명은 해당 태그에 속한 API의 summary를 조합하여 생성됩니다.
 */
@Configuration
public class SwaggerAutoTagDescriptionConfig {

	@Bean
	public OpenApiCustomizer autoGenerateTagDescriptions() {
		return openApi -> {
			Map<String, Set<String>> tagSummaryMap = extractTagSummaryMap(openApi);
			List<Tag> generatedTags = generateTagsWithDescription(tagSummaryMap);
			openApi.setTags(generatedTags);
		};
	}

	private Map<String, Set<String>> extractTagSummaryMap(OpenAPI openApi) {
		Map<String, Set<String>> tagSummaryMap = new HashMap<>();

		openApi.getPaths().forEach((path, pathItem) ->
			pathItem.readOperations().forEach(operation -> {
				List<String> tags = operation.getTags();
				String summary = operation.getSummary();

				if (tags != null && summary != null) {
					tags.forEach(tag ->
						tagSummaryMap
							.computeIfAbsent(tag, k -> new LinkedHashSet<>())
							.add(summary)
					);
				}
			})
		);

		return tagSummaryMap;
	}

	private List<Tag> generateTagsWithDescription(Map<String, Set<String>> tagSummaryMap) {
		return tagSummaryMap.entrySet().stream()
			.map(entry -> {
				String tagName = entry.getKey();
				String description = buildDescription(tagName, entry.getValue());
				return new Tag().name(tagName).description(description);
			})
			.toList();
	}

	private String buildDescription(String tagName, Set<String> summaries) {
		String summaryList = String.join(", ", summaries);
		return "%s API (%s)".formatted(tagName, summaryList); // Java 17 형식
	}
}
