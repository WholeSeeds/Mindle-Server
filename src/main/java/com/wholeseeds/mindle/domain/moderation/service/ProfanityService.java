package com.wholeseeds.mindle.domain.moderation.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wholeseeds.mindle.domain.moderation.dto.request.ProfanityCheckRequestDto;
import com.wholeseeds.mindle.domain.moderation.dto.response.ProfanityCheckResponseDto;
import com.wholeseeds.mindle.domain.moderation.dto.response.ProfanityHitDto;
import com.wholeseeds.mindle.domain.moderation.support.ProfanityState;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProfanityService {

	private final AtomicReference<ProfanityState> stateRef =
		new AtomicReference<>(ProfanityState.disabled());

	/**
	 * 주어진 비속어 목록으로 Aho–Corasick 자동자를 재구성하고,
	 * 탐지 엔진 상태(자동자 + 사전)를 원자적으로 교체한다.
	 * <p>입력 리스트는 null, 공백을 제거하고 중복을 제거한 뒤 불변 리스트로 유지된다.</p>
	 *
	 * @param profanities 비속어 단어 목록
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
			.toList();

		if (normalized.isEmpty()) {
			log.warn("Profanity list is empty. Automaton will be disabled.");
			stateRef.set(ProfanityState.disabled());
			return;
		}

		Trie trie = Trie.builder()
			.ignoreCase()
			.ignoreOverlaps()
			.addKeywords(normalized)
			.build();

		stateRef.set(ProfanityState.of(trie, normalized));
		log.info("Profanity automaton built with {} terms.", normalized.size());
	}

	/**
	 * 본문 텍스트에 대한 비속어 탐지
	 * <p>자동자가 비활성화되어 있거나 본문이 비어 있으면 통과(passed=true)를 반환한다.</p>
	 *
	 * @param req 검사 요청 DTO (본문 텍스트 포함)
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

		Iterable<Emit> emits = trie.parseText(text);
		List<ProfanityHitDto> hits = new ArrayList<>();
		for (Emit e : emits) {
			hits.add(ProfanityHitDto.builder()
				.profanity(e.getKeyword())
				.index(e.getStart()) // 0-base 시작 인덱스
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
