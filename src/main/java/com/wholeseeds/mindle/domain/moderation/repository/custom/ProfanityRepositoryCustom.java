package com.wholeseeds.mindle.domain.moderation.repository.custom;

import java.util.List;

public interface ProfanityRepositoryCustom {
	/**
	 * 삭제되지 않은 단어 전체를 길이 내림차순으로 반환
	 */
	List<String> findAllWordsLengthDesc();
}
