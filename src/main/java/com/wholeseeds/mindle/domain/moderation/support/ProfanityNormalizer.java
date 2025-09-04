package com.wholeseeds.mindle.domain.moderation.support;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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

	// 한글 분해 테이블 (호환 자모)
	private static final char[] L_TABLE = new char[] {
		'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
	};
	private static final char[] V_TABLE = new char[] {
		'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
	};
	private static final char[] T_TABLE = new char[] {
		'\0', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ',
		'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
	};

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

		// 유니코드 정규화로 결합문자/악센트 분리
		String normalized = Normalizer.normalize(text, Normalizer.Form.NFKD);

		StringBuilder sb = new StringBuilder(normalized.length() * 2);
		List<Integer> map = new ArrayList<>(normalized.length() * 2);

		// 결합 기호/분리 문자 제거, 영문 소문자화, 한글 분해, 그 외는 그대로 유지
		IntStream.range(0, normalized.length()).forEach(i -> {
			char ch = normalized.charAt(i);
			if (Character.getType(ch) == Character.NON_SPACING_MARK) {
				return;
			}
			if (isSkippableSeparator(ch)) {
				return;
			}
			if (ch <= 0x7F) {
				sb.append(Character.toLowerCase(ch));
				map.add(i);
				return;
			}
			if (isHangulSyllable(ch)) {
				decomposeHangulSyllable(ch, sb, map, i);
				return;
			}
			sb.append(ch);
			map.add(i);
		});

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
	 * 주어진 문자가 한글 음절 블록(가~힣)에 속하는지 판단한다.
	 *
	 * @param ch 검사할 문자
	 * @return 한글 음절이면 {@code true}, 아니면 {@code false}
	 */
	private static boolean isHangulSyllable(char ch) {
		return (ch >= 0xAC00 && ch <= 0xD7A3);
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

	/**
	 * 한글 음절(가~힣)을 호환 자모(초성/중성/종성)로 분해하여 출력 버퍼에 추가하고,
	 * 각 추가된 문자에 대해 원문 인덱스를 매핑 테이블에 기록한다.
	 *
	 * @param ch      분해할 한글 음절
	 * @param out     정규화 결과를 누적할 출력 버퍼
	 * @param map     정규화 인덱스 → 원문 인덱스 매핑 테이블
	 * @param origIdx 원문에서의 문자 인덱스
	 */
	private static void decomposeHangulSyllable(
		char ch,
		StringBuilder out,
		List<Integer> map,
		int origIdx
	) {
		int sIndex = ch - 0xAC00;
		int l = sIndex / (21 * 28);
		int v = (sIndex % (21 * 28)) / 28;
		int t = sIndex % 28;

		out.append(L_TABLE[l]);
		map.add(origIdx);

		out.append(V_TABLE[v]);
		map.add(origIdx);

		if (t != 0) {
			out.append(T_TABLE[t]);
			map.add(origIdx);
		}
	}
}
