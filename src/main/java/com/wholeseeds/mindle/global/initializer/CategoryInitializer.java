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
		List<CategoryCsvDto> rows = csvLoader.loadCsv("/data/category.csv", CategoryCsvDto.class);

		// 1) 먼저 모든 카테고리를 생성/저장하고, CSV의 id → 실제 엔티티 매핑을 보관
		java.util.Map<Long, Category> csvIdToEntity = new java.util.HashMap<>();
		for (CategoryCsvDto dto : rows) {
			Category created = Category.builder()
				.name(dto.getName())
				.description(dto.getDescription())
				.build();
			categoryRepository.save(created);
			csvIdToEntity.put(dto.getId(), created);
		}

		// 2) 부모-자식 연결 (CSV의 parent_id를 이용해 메모리 상 매핑으로 연결)
		for (CategoryCsvDto dto : rows) {
			Long parentId = dto.getParentId();
			if (parentId == null || parentId == 0L)
				continue; // 루트는 스킵

			Category child = csvIdToEntity.get(dto.getId());
			Category parent = csvIdToEntity.get(parentId);
			if (child != null && parent != null) {
				parent.addChild(child); // 양방향 동기화
			}
		}
		// 트랜잭션 종료 시 flush
	}
}
