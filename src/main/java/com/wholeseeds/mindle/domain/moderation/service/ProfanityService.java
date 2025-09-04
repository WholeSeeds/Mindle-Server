package com.wholeseeds.mindle.domain.moderation.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wholeseeds.mindle.domain.moderation.dto.request.ProfanityCheckRequestDto;
import com.wholeseeds.mindle.domain.moderation.dto.response.ProfanityCheckResponseDto;
import com.wholeseeds.mindle.domain.moderation.dto.response.ProfanityHitDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProfanityService {

	/**
	 * 패턴과 단어 리스트를 함께 보관하는 불변 상태 홀더.
	 * 한 번에 교체하기 위해 AtomicReference로 감싼다.
	 */
	private static final class ProfanityState {
		final Pattern pattern;          // 불변 & 스레드 세이프
		final List<String> list;        // 불변 리스트 (unmodifiable)
		ProfanityState(Pattern pattern, List<String> list) {
			this.pattern = pattern;
			this.list = list;
		}
	}

	private final AtomicReference<ProfanityState> stateRef =
		new AtomicReference<>(new ProfanityState(null, List.of()));

	/**
	 * 외부(Initializer/관리자 기능 등)에서 단어 목록을 주입해
	 * 정규식 패턴을 리컴파일한다. (원자적 교체)
	 */
	public void refreshWith(List<String> profanities) {
		if (profanities == null) {
			profanities = Collections.emptyList();
		}

		List<String> normalized = profanities.stream()
			.filter(Objects::nonNull)
			.map(String::trim)
			.filter(s -> !s.isEmpty())
			.distinct()
			.sorted(Comparator.comparingInt(String::length).reversed()) // 긴 단어 우선
			.toList();

		if (normalized.isEmpty()) {
			log.warn("Profanity list is empty. Pattern will be disabled.");
			stateRef.set(new ProfanityState(null, List.of()));
			return;
		}

		String alternation = normalized.stream()
			.map(Pattern::quote)
			.collect(Collectors.joining("|"));

		// (?iu) = CASE_INSENSITIVE + UNICODE_CASE
		String regex = "(?iu)(" + alternation + ")";
		Pattern newPattern = Pattern.compile(regex);

		stateRef.set(new ProfanityState(newPattern, normalized));
		log.info("Profanity pattern compiled with {} terms.", normalized.size());
	}

	@Transactional(readOnly = true)
	public ProfanityCheckResponseDto check(ProfanityCheckRequestDto req) {
		String text = req.getText();

		// 스냅샷 획득 (원자적 읽기)
		ProfanityState s = stateRef.get();
		Pattern p = s.pattern;

		if (text == null || text.isBlank() || p == null) {
			return ProfanityCheckResponseDto.builder()
				.passed(true)
				.hits(Collections.emptyList())
				.build();
		}

		List<ProfanityHitDto> hits = new ArrayList<>();
		Matcher matcher = p.matcher(text);
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

	/** 현재 패턴이 활성화되어 있는지 여부 */
	public boolean isEnabled() {
		return stateRef.get().pattern != null;
	}

	/** 현재 사용 중인 비속어 리스트(읽기전용) */
	public List<String> getCurrentList() {
		return stateRef.get().list;
	}
}
