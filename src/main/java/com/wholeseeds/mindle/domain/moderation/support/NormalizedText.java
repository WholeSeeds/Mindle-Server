package com.wholeseeds.mindle.domain.moderation.support;

import java.util.Arrays;

/**
 * 정규화 결과를 담는 불변 DTO (record).
 * - normalized: 탐지용으로 정규화된 문자열
 * - indexMap: normalized[i]가 원문에서 온 인덱스(0-base)
 */
public record NormalizedText(String normalized, int[] indexMap) {

	/**
	 * canonical constructor — null 방어 + 방어적 복사
	 */
	public NormalizedText {
		normalized = (normalized == null) ? "" : normalized;
		indexMap = (indexMap == null) ? new int[0] : Arrays.copyOf(indexMap, indexMap.length);
	}

	/**
	 * 내부 배열 노출 방지(불변성 보장)를 위해 복사본을 반환
	 */
	@Override
	public int[] indexMap() {
		return Arrays.copyOf(indexMap, indexMap.length);
	}

	/**
	 * 배열 내용 기준 equals
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof NormalizedText other)) {
			return false;
		}
		return normalized.equals(other.normalized) && Arrays.equals(indexMap, other.indexMap);
	}

	/**
	 * 배열 내용 기준 hashCode
	 */
	@Override
	public int hashCode() {
		int result = normalized.hashCode();
		result = 31 * result + Arrays.hashCode(indexMap);
		return result;
	}

	/**
	 * 배열 내용 출력
	 */
	@Override
	public String toString() {
		return "NormalizedText[normalized=%s, indexMap=%s]"
			.formatted(normalized, Arrays.toString(indexMap));
	}
}
