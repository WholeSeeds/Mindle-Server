// src/main/java/com/wholeseeds/mindle/domain/moderation/service/ProfanityService.java
package com.wholeseeds.mindle.domain.moderation.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wholeseeds.mindle.domain.moderation.dto.request.ProfanityCheckRequestDto;
import com.wholeseeds.mindle.domain.moderation.dto.response.ProfanityCheckResponseDto;
import com.wholeseeds.mindle.domain.moderation.dto.response.ProfanityHitDto;
import com.wholeseeds.mindle.domain.moderation.support.NormalizedText;
import com.wholeseeds.mindle.domain.moderation.support.ProfanityNormalizer;
import com.wholeseeds.mindle.domain.moderation.support.ProfanityState;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProfanityService {

	private final AtomicReference<ProfanityState> stateRef =
		new AtomicReference<>(ProfanityState.disabled());

	/**
	 * 주어진 비속어 사전을 정규화하여 Aho–Corasick 자동자를 재구성하고,
	 * 상태(트라이, 표준 단어 목록, 정규화 매핑)를 원자적으로 교체한다.
	 *
	 * @param profanities 비속어 단어 목록
	 */
	public void refreshWith(List<String> profanities) {
		if (profanities == null) {
			profanities = Collections.emptyList();
		}

		// 표준 단어 정제
		List<String> canonical = profanities.stream()
			.filter(Objects::nonNull)
			.map(String::trim)
			.filter(s -> !s.isEmpty())
			.distinct()
			.toList();

		if (canonical.isEmpty()) {
			log.warn("Profanity list is empty. Automaton will be disabled.");
			stateRef.set(ProfanityState.disabled());
			return;
		}

		// 정규화 키와 매핑 생성
		List<String> normalizedKeys = new ArrayList<>(canonical.size());
		Map<String, String> normToCanon = new HashMap<>(canonical.size() * 2);

		for (String w : canonical) {
			String key = ProfanityNormalizer.normalizeToken(w);
			if (key.isEmpty()) {
				continue;
			}
			normalizedKeys.add(key);
			// 동일 키에 여러 단어가 매핑될 수 있음 → 첫 항목 유지
			normToCanon.putIfAbsent(key, w);
		}

		if (normalizedKeys.isEmpty()) {
			log.warn("All profanities collapsed to empty after normalization. Disabling automaton.");
			stateRef.set(ProfanityState.disabled());
			return;
		}

		// AC 빌드
		Trie trie = Trie.builder()
			.ignoreOverlaps()
			.addKeywords(normalizedKeys)
			.build();

		stateRef.set(ProfanityState.of(trie, canonical, normToCanon));
		log.info("Profanity automaton built with {} normalized keys ({} canonical).",
			normalizedKeys.size(), canonical.size());
	}

	/**
	 * 본문 텍스트에 대해 비속어를 탐지한다.
	 * - 본문을 정규화(분리문자 제거/자모 분해/소문자화)하고 AC로 매칭
	 * - 매칭 시작 인덱스는 정규화 인덱스를 **원문 인덱스로 역매핑**하여 반환
	 *
	 * @param req 검사 요청 DTO
	 * @return 탐지 결과(passed, hits[])
	 */
	@Transactional(readOnly = true)
	public ProfanityCheckResponseDto check(ProfanityCheckRequestDto req) {
		String text = req.getText();

		ProfanityState state = stateRef.get();
		Trie trie = state.trie();

		if (text == null || text.isBlank() || trie == null) {
			return ProfanityCheckResponseDto.builder()
				.passed(true)
				.hits(Collections.emptyList())
				.build();
		}

		// 본문 정규화 + 역매핑 생성
		NormalizedText nt = ProfanityNormalizer.normalizeForDetection(text);
		String norm = nt.normalized();
		int[] map = nt.indexMap();

		if (norm.isEmpty()) {
			return ProfanityCheckResponseDto.builder()
				.passed(true)
				.hits(Collections.emptyList())
				.build();
		}

		Iterable<Emit> emits = trie.parseText(norm);
		List<ProfanityHitDto> hits = new ArrayList<>();

		for (Emit e : emits) {
			int normStart = e.getStart();
			int origStart = (normStart >= 0 && normStart < map.length) ? map[normStart] : 0;

			// 표준 단어명(사전 단어) 복원
			String canonical = state.normToCanon().getOrDefault(e.getKeyword(), e.getKeyword());

			hits.add(ProfanityHitDto.builder()
				.profanity(canonical)
				.index(origStart) // 원문 0-base 인덱스
				.build());
		}

		return ProfanityCheckResponseDto.builder()
			.passed(hits.isEmpty())
			.hits(hits)
			.build();
	}

	/**
	 * 현재 탐지 자동자가 활성 상태인지 여부를 반환
	 *
	 * @return 활성화되어 있으면 true, 아니면 false
	 */
	public boolean isEnabled() {
		return stateRef.get().enabled();
	}

	/**
	 * 현재 사용 중인 비속어 리스트를 반환
	 *
	 * @return 불변 리스트(List.copyOf 보장)
	 */
	public List<String> getCurrentList() {
		return stateRef.get().list();
	}
}
