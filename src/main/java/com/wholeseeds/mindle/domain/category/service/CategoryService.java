package com.wholeseeds.mindle.domain.category.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wholeseeds.mindle.domain.category.dto.CategoryRow;
import com.wholeseeds.mindle.domain.category.dto.response.CategoryDto;
import com.wholeseeds.mindle.domain.category.entity.Category;
import com.wholeseeds.mindle.domain.category.repository.CategoryRepository;
import com.wholeseeds.mindle.domain.complaint.exception.CategoryNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
	private final CategoryRepository categoryRepository;

	/**
	 * 카테고리 전체 목록 조회
	 * @param categoryId 카테고리 ID
	 * @return Category 객체
	 */
	@Transactional(readOnly = true)
	public Category findCategory(Long categoryId) {
		return categoryRepository.findById(categoryId)
			.orElseThrow(CategoryNotFoundException::new);
	}

	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "categoryTree", unless = "#result == null || #result.isEmpty()")
	public List<CategoryDto> getCategoryAll() {
		List<CategoryRow> rows = categoryRepository.findAllFlat();

		Map<Long, CategoryDto> map = new HashMap<>(rows.size());
		for (CategoryRow r : rows) {
			map.put(r.getId(), CategoryDto.builder()
				.id(r.getId())
				.name(r.getName())
				.description(r.getDescription())
				.children(new ArrayList<>())
				.build());
		}

		List<CategoryDto> roots = new ArrayList<>();
		for (CategoryRow r : rows) {
			CategoryDto node = map.get(r.getId());
			if (r.getParentId() == null) {
				roots.add(node);
			} else {
				CategoryDto parent = map.get(r.getParentId());
				if (parent != null) {
					parent.getChildren().add(node);
				} else {
					roots.add(node);
				}
			}
		}
		return roots;
	}
}
