package com.wholeseeds.mindle.global.initializer;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wholeseeds.mindle.common.util.CsvLoader;
import com.wholeseeds.mindle.domain.moderation.dto.csv.ProfanityCsvDto;
import com.wholeseeds.mindle.domain.moderation.entity.Profanity;
import com.wholeseeds.mindle.domain.moderation.repository.ProfanityRepository;
import com.wholeseeds.mindle.domain.moderation.service.ProfanityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfanityInitializer implements CommandLineRunner {

	private final CsvLoader csvLoader;
	private final ProfanityRepository profanityRepository;
	private final ProfanityService profanityService;

	@Override
	@Transactional
	public void run(String... args) {
		try {
			// CSV 로드
			List<ProfanityCsvDto> rows =
				csvLoader.loadCsv("/data/profanity.csv", ProfanityCsvDto.class);

			// DB에 upsert 방식 시딩
			int inserted = 0;
			for (ProfanityCsvDto dto : rows) {
				String word = dto.getProfanity();
				if (word == null || word.trim().isEmpty()) {
					continue;
				}

				if (!profanityRepository.existsByWord(word)) {
					profanityRepository.save(Profanity.of(word));
					inserted++;
				}
			}
			log.info("ProfanityInitializer: inserted {} new words.", inserted);

			// DB에서 최신 목록을 읽어와 서비스 패턴 리컴파일
			List<String> words = profanityRepository.findAllWordsLengthDesc();
			profanityService.refreshWith(words);

		} catch (Exception e) {
			log.error("Failed to initialize profanities", e);
		}
	}
}
