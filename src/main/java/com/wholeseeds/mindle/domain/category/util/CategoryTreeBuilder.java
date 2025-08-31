package com.wholeseeds.mindle.domain.category.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.wholeseeds.mindle.domain.category.dto.CategoryRow;
import com.wholeseeds.mindle.domain.category.dto.response.CategoryTreeDto;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CategoryTreeBuilder {

	/**
	 * 전체 rows로부터 루트 forest를 빌드합니다.
	 *
	 * @param rows     id, name, description, parentId로 구성된 플랫 데이터
	 * @param maxDepth 루트=0 기준 최대 허용 깊이 (해당 깊이에 도달하면 더 내려가지 않음)
	 * @return 루트 노드 목록(각 노드는 자식 포함)
	 */
	public List<CategoryTreeDto> buildForest(List<CategoryRow> rows, int maxDepth) {
		Map<Long, CategoryRow> rowMap = buildRowMap(rows);
		Map<Long, List<Long>> childIndex = buildChildIndex(rows);

		// 루트: parentId == null 또는 부모가 존재하지 않는 노드
		List<Long> roots = new ArrayList<>();
		for (CategoryRow r : rows) {
			Long p = r.getParentId();
			if (p == null || !rowMap.containsKey(p)) {
				roots.add(r.getId());
			}
		}

		// 각 루트로부터 DFS 빌드
		List<CategoryTreeDto> forest = new ArrayList<>();
		for (Long rootId : roots) {
			CategoryTreeDto node = dfsBuild(
				rootId, 0, maxDepth, rowMap, childIndex, new HashSet<>()
			);
			if (node != null) {
				forest.add(node);
			}
		}
		return forest;
	}

	/**
	 * 특정 rootId를 루트로 하는 서브트리를 빌드합니다.
	 *
	 * @param rows     플랫 데이터
	 * @param rootId   서브트리의 루트 카테고리 ID
	 * @param maxDepth 루트=0 기준 최대 허용 깊이
	 * @return 루트 노드(자식 포함). rootId가 rows에 없으면 null
	 */
	public CategoryTreeDto buildSubtree(List<CategoryRow> rows, Long rootId, int maxDepth) {
		Map<Long, CategoryRow> rowMap = buildRowMap(rows);
		if (!rowMap.containsKey(rootId)) {
			return null;
		}
		Map<Long, List<Long>> childIndex = buildChildIndex(rows);
		return dfsBuild(rootId, 0, maxDepth, rowMap, childIndex, new HashSet<>());
	}

	/**
	 * id → CategoryRow 매핑을 생성합니다.
	 *
	 * @param rows 플랫 데이터
	 * @return id로 빠르게 조회 가능한 Map
	 */
	private Map<Long, CategoryRow> buildRowMap(List<CategoryRow> rows) {
		Map<Long, CategoryRow> rowMap = new HashMap<>(rows.size());
		for (CategoryRow r : rows) {
			rowMap.put(r.getId(), r);
		}
		return rowMap;
	}

	/**
	 * 부모ID → 자식ID 목록 인덱스를 생성합니다.
	 * - self-loop(id == parentId)는 로그 경고 후 스킵합니다.
	 *
	 * @param rows 플랫 데이터
	 * @return 부모별 자식 ID 리스트 인덱스
	 */
	private Map<Long, List<Long>> buildChildIndex(List<CategoryRow> rows) {
		Map<Long, List<Long>> childIdsByParent = new HashMap<>();
		rows.forEach(r -> {
			Long p = r.getParentId();
			if (p == null) {
				return;
			}
			if (p.equals(r.getId())) {
				log.warn("Self-loop detected and skipped: id={} parentId={}", r.getId(), p);
				return;
			}
			childIdsByParent.computeIfAbsent(p, k -> new ArrayList<>()).add(r.getId());
		});
		return childIdsByParent;
	}

	/**
	 * DFS로 안전하게 서브트리를 빌드합니다.
	 * - depth가 maxDepth 이상이면 더 내려가지 않음
	 * - onPath(현재 DFS 경로)에 동일 id가 재등장하면 사이클로 판단하여 가지 확장 중단
	 *
	 * @param id        현재 노드 id
	 * @param depth     현재 깊이(루트=0)
	 * @param maxDepth  최대 허용 깊이
	 * @param rowMap    id → CategoryRow 매핑
	 * @param childIndex 부모ID → 자식ID 목록 인덱스
	 * @param onPath    현재 DFS 경로 상의 방문 집합(사이클 감지용)
	 * @return 빌드된 CategoryTreeDto (없으면 null)
	 */
	private CategoryTreeDto dfsBuild(
		Long id,
		int depth,
		int maxDepth,
		Map<Long, CategoryRow> rowMap,
		Map<Long, List<Long>> childIndex,
		Set<Long> onPath
	) {
		CategoryRow r = rowMap.get(id);
		if (r == null) {
			return null;
		}

		CategoryTreeDto node = CategoryTreeDto.builder()
			.id(r.getId())
			.name(r.getName())
			.description(r.getDescription())
			.children(new ArrayList<>())
			.build();

		// 깊이 제한
		if (depth >= maxDepth) {
			return node;
		}

		// 사이클 감지
		if (!onPath.add(id)) {
			log.warn("Cycle detected. Stop expanding children of id={}", id);
			return node;
		}

		List<Long> children = childIndex.get(id);
		if (children != null) {
			for (Long childId : children) {
				if (onPath.contains(childId)) {
					log.warn("Cycle edge skipped: {} -> {}", id, childId);
					continue;
				}
				CategoryTreeDto child = dfsBuild(childId, depth + 1, maxDepth, rowMap, childIndex, onPath);
				if (child != null) {
					node.getChildren().add(child);
				}
			}
		}

		onPath.remove(id);
		return node;
	}
}
