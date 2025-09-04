package com.wholeseeds.mindle.domain.moderation.repository;

import com.wholeseeds.mindle.common.repository.JpaBaseRepository;
import com.wholeseeds.mindle.domain.moderation.entity.Profanity;
import com.wholeseeds.mindle.domain.moderation.repository.custom.ProfanityRepositoryCustom;

public interface ProfanityRepository
	extends JpaBaseRepository<Profanity, Long>, ProfanityRepositoryCustom {

	boolean existsByWord(String word);
}
