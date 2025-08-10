package com.wholeseeds.mindle.domain.complaint.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ComplaintDetailWithImagesDto {
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
	private List<String> imageUrlList;
}
