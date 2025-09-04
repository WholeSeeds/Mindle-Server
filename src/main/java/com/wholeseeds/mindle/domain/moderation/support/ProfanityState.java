package com.wholeseeds.mindle.domain.moderation.support;

import java.util.List;

import org.ahocorasick.trie.Trie;

/**
 * Aho–Corasick 자동자와 단어 리스트를 함께 보관하는 불변 상태 홀더.
 * - trie: null이면 비활성(disabled) 상태.
 * - list: 불변(List.copyOf)로 보관.
 */
public record ProfanityState(Trie trie, List<String> list) {

	public ProfanityState {
		list = (list == null) ? List.of() : List.copyOf(list);
	}

	public static ProfanityState of(Trie trie, List<String> list) {
		return new ProfanityState(trie, list);
	}

	public static ProfanityState disabled() {
		return new ProfanityState(null, List.of());
	}

	public boolean enabled() {
		return trie != null;
	}
}
