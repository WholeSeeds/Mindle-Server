package com.wholeseeds.mindle.domain.moderation.support;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/**
 * 비속어 탐지를 위한 텍스트 정규화 유틸.
 * - 공백/구두점/기호 제거
 * - 영문 소문자화
 * - 한글 음절을 호환 자모(ㄱ~ㅎ, ㅏ~ㅣ)로 분해
 * - 정규화 인덱스 → 원문 인덱스 매핑 제공
 */
public final class ProfanityNormalizer {

	private ProfanityNormalizer() {
	}

	/**
	 * 입력 본문을 탐지용으로 정규화하고 인덱스 역매핑 정보를 생성한다.
	 *
	 * @param text 원본 본문 텍스트(널 가능)
	 * @return 정규화된 문자열과 정규화→원문 인덱스 매핑을 담은 {@link NormalizedText}
	 */
	public static NormalizedText normalizeForDetection(String text) {
		if (text == null || text.isBlank()) {
			return new NormalizedText("", new int[0]);
		}

		StringBuilder sb = new StringBuilder(text.length() * 2);
		List<Integer> map = new ArrayList<>(text.length() * 2);

		// 원문을 글자 단위로 순회하며, 각 글자를 NFKD로 전개하고
		// 전개된 각 문자에 대해 "원문 인덱스 j"를 저장
		for (int j = 0; j < text.length(); j++) {
			char c = text.charAt(j);
			String d = Normalizer.normalize(String.valueOf(c), Normalizer.Form.NFKD);

			for (int k = 0; k < d.length(); k++) {
				char ch = d.charAt(k);

				// 결합 기호(악센트 등) 또는 공백/구두점/기호 등은 스킵
				if (Character.getType(ch) == Character.NON_SPACING_MARK || isSkippableSeparator(ch)) {
					continue;
				}

				// ASCII는 소문자화
				if (ch <= 0x7F) {
					ch = Character.toLowerCase(ch);
				}

				sb.append(ch);
				map.add(j);
			}
		}

		int[] indexMap = map.stream().mapToInt(Integer::intValue).toArray();
		return new NormalizedText(sb.toString(), indexMap);
	}

	/**
	 * 사전 단어를 정규화한다.
	 *
	 * @param token 사전 단어(널 가능)
	 * @return 정규화된 토큰 문자열(널/공백 입력 시 빈 문자열)
	 */
	public static String normalizeToken(String token) {
		return normalizeForDetection(token).normalized();
	}

	/**
	 * 탐지 품질을 위해 제거할 분리 문자인지 여부를 판단한다.
	 * <p>공백/구두점/기호/제어문자/포맷 문자 등을 제거 대상으로 본다.</p>
	 *
	 * @param ch 검사할 문자
	 * @return 제거 대상이면 {@code true}, 유지하면 {@code false}
	 */
	private static boolean isSkippableSeparator(char ch) {
		int type = Character.getType(ch);
		return switch (type) {
			case Character.SPACE_SEPARATOR,
				Character.CONNECTOR_PUNCTUATION,
				Character.DASH_PUNCTUATION,
				Character.START_PUNCTUATION,
				Character.END_PUNCTUATION,
				Character.OTHER_PUNCTUATION,
				Character.MATH_SYMBOL,
				Character.CURRENCY_SYMBOL,
				Character.MODIFIER_SYMBOL,
				Character.OTHER_SYMBOL,
				Character.CONTROL,
				Character.FORMAT -> true;
			default -> false;
		};
	}
}
