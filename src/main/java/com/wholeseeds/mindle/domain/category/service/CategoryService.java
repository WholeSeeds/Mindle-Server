// src/main/java/com/wholeseeds/mindle/domain/category/service/CategoryService.java
package com.wholeseeds.mindle.domain.category.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wholeseeds.mindle.domain.category.dto.CategoryRow;
import com.wholeseeds.mindle.domain.category.dto.response.CategoryTreeDto;
import com.wholeseeds.mindle.domain.category.entity.Category;
import com.wholeseeds.mindle.domain.category.exception.CategoryNotFoundException;
import com.wholeseeds.mindle.domain.category.repository.CategoryRepository;
import com.wholeseeds.mindle.domain.category.util.CategoryTreeBuilder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final CategoryTreeBuilder treeBuilder;

	@Value("${category.max-depth}")
	private int maxDepth;

	/**
	 * 단일 카테고리를 조회한다.
	 *
	 * @param categoryId 조회할 카테고리 ID
	 * @return Category 엔티티
	 * @throws CategoryNotFoundException 카테고리를 찾을 수 없는 경우
	 */
	@Transactional(readOnly = true)
	public Category findCategory(Long categoryId) {
		return categoryRepository.findById(categoryId)
			.orElseThrow(CategoryNotFoundException::new);
	}

	/**
	 * 전체 카테고리를 한 번에 로드하여 메모리에서 트리를 구성한 뒤,
	 * 루트 카테고리 목록(각각 자식 포함)을 반환한다.
	 *
	 * @return 루트 카테고리 트리 목록
	 */
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "categoryTree", unless = "#result == null || #result.isEmpty()")
	public List<CategoryTreeDto> getCategoryAll() {
		List<CategoryRow> rows = categoryRepository.findAllFlat();
		return treeBuilder.buildForest(rows, maxDepth);
	}

	/**
	 * 특정 카테고리를 루트로 하는 서브트리를 반환한다.
	 *
	 * @param categoryId 루트로 삼을 카테고리 ID
	 * @return 루트 카테고리 트리
	 * @throws CategoryNotFoundException 카테고리를 찾을 수 없는 경우
	 */
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "categoryTreeById", key = "#categoryId", unless = "#result == null")
	public CategoryTreeDto getCategorySubtree(Long categoryId) {
		List<CategoryRow> rows = categoryRepository.findAllFlat();
		CategoryTreeDto root = treeBuilder.buildSubtree(rows, categoryId, maxDepth);
		if (root == null) {
			throw new CategoryNotFoundException();
		}
		return root;
	}
}
