package com.wholeseeds.mindle.domain.complaint.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.wholeseeds.mindle.domain.place.dto.PlaceDto;
import com.wholeseeds.mindle.domain.region.dto.CityDto;
import com.wholeseeds.mindle.domain.region.dto.DistrictDto;
import com.wholeseeds.mindle.domain.region.dto.SubdistrictDto;

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
	private PlaceDto place;
	private CityDto city;
	private DistrictDto district;
	private SubdistrictDto subdistrict;
	private LocalDateTime createdAt;
	private List<String> imageUrlList;
}
