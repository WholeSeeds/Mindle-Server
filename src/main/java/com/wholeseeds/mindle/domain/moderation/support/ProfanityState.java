package com.wholeseeds.mindle.domain.moderation.support;

import java.util.List;
import java.util.Map;

import org.ahocorasick.trie.Trie;

/**
 * Aho–Corasick 트라이와 사전(표준 단어 목록),
 * 정규화 키 → 표준 단어 매핑을 보관하는 불변 State
 */
public record ProfanityState(Trie trie, List<String> list, Map<String, String> normToCanon) {

	public ProfanityState {
		list = (list == null) ? List.of() : List.copyOf(list);
		normToCanon = (normToCanon == null) ? Map.of() : Map.copyOf(normToCanon);
	}

	public static ProfanityState of(Trie trie, List<String> canonicalList, Map<String, String> normToCanon) {
		return new ProfanityState(trie, canonicalList, normToCanon);
	}

	public static ProfanityState disabled() {
		return new ProfanityState(null, List.of(), Map.of());
	}

	public boolean enabled() {
		return trie != null;
	}
}
