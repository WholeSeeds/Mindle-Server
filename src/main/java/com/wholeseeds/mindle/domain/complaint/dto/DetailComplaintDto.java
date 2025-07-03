package com.wholeseeds.mindle.domain.complaint.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class DetailComplaintDto {
	private Long id;
	private String title;
	private String content;
	private String categoryName;
	private String memberNickname;
	private String placeName;
	private String cityName;
	private String districtName; // TODO: null 잘 담기는지 테스트
	private String subdistrictName;
	private LocalDateTime createdAt;

	// TODO : complaint_reaction
	// private List<ComplaintReactionDto> reactions; // 추후 구현 예정
}
