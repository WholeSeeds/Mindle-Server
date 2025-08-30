package com.wholeseeds.mindle.domain.moderation.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wholeseeds.mindle.common.util.CsvLoader;
import com.wholeseeds.mindle.domain.moderation.dto.csv.ProfanityCsvDto;
import com.wholeseeds.mindle.domain.moderation.dto.request.ProfanityCheckRequestDto;
import com.wholeseeds.mindle.domain.moderation.dto.response.ProfanityCheckResponseDto;
import com.wholeseeds.mindle.domain.moderation.dto.response.ProfanityHitDto;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfanityService {

	private final CsvLoader csvLoader;

	private volatile Pattern compiledPattern;
	private List<String> profanityList = Collections.emptyList();

	@PostConstruct
	public void init() {
		try {
			List<ProfanityCsvDto> rows = csvLoader.loadCsv("/data/profanity.csv", ProfanityCsvDto.class);
			this.profanityList = rows.stream()
				.map(ProfanityCsvDto::getProfanity)
				.filter(Objects::nonNull)
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.distinct()
				.sorted(Comparator.comparingInt(String::length).reversed())
				.collect(Collectors.toList());

			if (profanityList.isEmpty()) {
				log.warn("Profanity profanity list is empty.");
				this.compiledPattern = null;
				return;
			}

			// alternation 생성 (모든 항목 literal 매칭)
			String alternation = profanityList.stream()
				.map(Pattern::quote)
				.collect(Collectors.joining("|"));

			// (?iu) = CASE_INSENSITIVE + UNICODE_CASE
			String regex = "(?iu)(" + alternation + ")";
			this.compiledPattern = Pattern.compile(regex);
			log.info("Profanity pattern compiled with {} terms.", profanityList.size());
		} catch (Exception e) {
			log.error("Failed to initialize profanity list from CSV", e);
			this.profanityList = Collections.emptyList();
			this.compiledPattern = null;
		}
	}

	@Transactional(readOnly = true)
	public ProfanityCheckResponseDto check(ProfanityCheckRequestDto req) {
		String text = req.getText();
		if (text == null || text.isBlank() || compiledPattern == null) {
			return ProfanityCheckResponseDto.builder()
				.passed(true)
				.hits(Collections.emptyList())
				.build();
		}

		List<ProfanityHitDto> hits = new ArrayList<>();
		Matcher matcher = compiledPattern.matcher(text);
		while (matcher.find()) {
			hits.add(ProfanityHitDto.builder()
				.profanity(matcher.group())
				.index(matcher.start()) // 0-base
				.build());
		}

		return ProfanityCheckResponseDto.builder()
			.passed(hits.isEmpty())
			.hits(hits)
			.build();
	}

    /* 추후 DB 기반으로 전환할 때:
       - profanityList를 Repository에서 조회
       - 동일한 방식으로 alternation 재컴파일
       - 별도 refresh() 훅을 만들어 핫리로드 가능하게 */
}
