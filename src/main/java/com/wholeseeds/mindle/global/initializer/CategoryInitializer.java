package com.wholeseeds.mindle.global.initializer;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wholeseeds.mindle.common.util.CsvLoader;
import com.wholeseeds.mindle.domain.complaint.dto.csv.CategoryCsvDto;
import com.wholeseeds.mindle.domain.complaint.entity.Category;
import com.wholeseeds.mindle.domain.complaint.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryInitializer implements CommandLineRunner {

	private final CsvLoader csvLoader;
	private final CategoryRepository categoryRepository;

	@Override
	@Transactional
	public void run(String... args) {
		List<CategoryCsvDto> categories =
			csvLoader.loadCsv("/data/category.csv", CategoryCsvDto.class);

		for (CategoryCsvDto category : categories) {
			if (!categoryRepository.existsById(category.getId())) {
				categoryRepository.save(Category.builder()
					.name(category.getName())
					.description(category.getDescription())
					.build());
			}
		}
	}
}
