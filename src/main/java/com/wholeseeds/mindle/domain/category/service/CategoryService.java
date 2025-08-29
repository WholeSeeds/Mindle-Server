package com.wholeseeds.mindle.domain.category.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wholeseeds.mindle.domain.category.dto.CategoryRow;
import com.wholeseeds.mindle.domain.category.dto.response.CategoryTreeDto;
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
	 * 단일 카테고리를 조회한다.
	 * 존재하지 않으면 CategoryNotFoundException을 던진다.
	 *
	 * @param categoryId 조회할 카테고리 ID
	 * @return Category 엔티티
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
		Map<Long, CategoryTreeDto> nodeMap = buildNodeMap(rows);

		linkParentChild(nodeMap, rows);
		return collectRootNodes(nodeMap, rows);
	}

	/**
	 * 특정 카테고리를 루트로 하는 서브트리를 반환한다.
	 *
	 * @param categoryId 루트로 삼을 카테고리 ID
	 * @return 루트 카테고리 트리
	 */
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "categoryTreeById", key = "#categoryId", unless = "#result == null")
	public CategoryTreeDto getCategorySubtree(Long categoryId) {
		List<CategoryRow> rows = categoryRepository.findAllFlat();
		Map<Long, CategoryTreeDto> nodeMap = buildNodeMap(rows);

		linkParentChild(nodeMap, rows);

		CategoryTreeDto root = nodeMap.get(categoryId);
		if (root == null) {
			throw new CategoryNotFoundException();
		}
		return root;
	}

	/**
	 * CategoryRow 리스트로부터 id → CategoryTreeDto 노드 맵을 생성한다.
	 * children 리스트는 비어 있는 상태로 초기화한다.
	 */
	private Map<Long, CategoryTreeDto> buildNodeMap(List<CategoryRow> rows) {
		Map<Long, CategoryTreeDto> map = new HashMap<>(rows.size());
		for (CategoryRow r : rows) {
			map.put(r.getId(), CategoryTreeDto.builder()
				.id(r.getId())
				.name(r.getName())
				.description(r.getDescription())
				.children(new ArrayList<>())
				.build());
		}
		return map;
	}

	/**
	 * 주어진 노드 맵과 CategoryRow 리스트를 이용하여
	 * 부모-자식 관계를 메모리 상에서 연결한다.
	 */
	private void linkParentChild(Map<Long, CategoryTreeDto> nodeMap, List<CategoryRow> rows) {
		for (CategoryRow r : rows) {
			Long parentId = r.getParentId();
			if (parentId == null) {
				continue;
			}

			CategoryTreeDto parent = nodeMap.get(parentId);
			CategoryTreeDto child  = nodeMap.get(r.getId());
			if (parent != null && child != null) {
				parent.getChildren().add(child);
			}
		}
	}

	/**
	 * 루트 노드들을 수집하여 반환한다.
	 * - parentId가 null이거나, parentId가 존재하더라도 해당 부모 노드가 맵에 없으면 루트로 간주한다.
	 */
	private List<CategoryTreeDto> collectRootNodes(Map<Long, CategoryTreeDto> nodeMap, List<CategoryRow> rows) {
		List<CategoryTreeDto> roots = new ArrayList<>();
		for (CategoryRow r : rows) {
			Long parentId = r.getParentId();
			CategoryTreeDto node = nodeMap.get(r.getId());
			if (parentId == null || !nodeMap.containsKey(parentId)) {
				roots.add(node);
			}
		}
		return roots;
	}
}
