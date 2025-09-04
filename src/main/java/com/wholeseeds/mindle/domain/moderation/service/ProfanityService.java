package com.wholeseeds.mindle.domain.moderation.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
	 * 단어 목록으로 Aho–Corasick 자동자 재구성 (원자적 교체)
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
			.collect(Collectors.toList());

		if (normalized.isEmpty()) {
			log.warn("Profanity list is empty. Automaton will be disabled.");
			stateRef.set(ProfanityState.disabled());
			return;
		}

		Trie trie = Trie.builder()
			.ignoreCase()
			.removeOverlaps()
			.addKeywords(normalized)
			.build();

		stateRef.set(ProfanityState.of(trie, normalized));
		log.info("Profanity automaton built with {} terms.", normalized.size());
	}

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
				.index(e.getStart()) // 0-base
				.build());
		}

		return ProfanityCheckResponseDto.builder()
			.passed(hits.isEmpty())
			.hits(hits)
			.build();
	}

	public boolean isEnabled() {
		return stateRef.get().enabled();
	}

	public List<String> getCurrentList() {
		return stateRef.get().list();
	}
}
