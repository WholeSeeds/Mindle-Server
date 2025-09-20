package com.wholeseeds.mindle.domain.complaint.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import com.wholeseeds.mindle.domain.category.entity.Category;
import com.wholeseeds.mindle.domain.category.service.CategoryService;
import com.wholeseeds.mindle.domain.complaint.dto.request.SaveComplaintRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.request.UpdateComplaintRequestDto;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.service.MemberService;
import com.wholeseeds.mindle.domain.place.dto.command.PlaceUpsertCmd;
import com.wholeseeds.mindle.domain.place.entity.Place;
import com.wholeseeds.mindle.domain.place.service.PlaceService;
import com.wholeseeds.mindle.domain.region.entity.Subdistrict;
import com.wholeseeds.mindle.domain.region.service.RegionService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ComplaintRelationMapper {

	private final CategoryService categoryService;
	private final MemberService memberService;
	private final RegionService regionService;
	private final PlaceService placeService;

	public Category toCategory(Long id) {
		return categoryService.findCategory(id);
	}

	public Member toMember(Long id) {
		return memberService.getMember(id);
	}

	public Subdistrict toSubdistrict(String code) {
		return regionService.findSubdistrict(code);
	}

	@Named("toPlaceFromSave")
	public Place toPlaceFromSave(SaveComplaintRequestDto dto) {
		return toPlaceCore(
			dto.getPlaceId(),
			dto.getPlaceType(),
			dto.getPlaceName(),
			dto.getPlaceDescription(),
			dto.getLatitude(),
			dto.getLongitude(),
			dto.getSubdistrictCode()
		);
	}

	@Named("toPlaceFromUpdate")
	public Place toPlaceFromUpdate(UpdateComplaintRequestDto dto) {
		return toPlaceCore(
			dto.getPlaceId(),
			dto.getPlaceType(),
			dto.getPlaceName(),
			dto.getPlaceDescription(),
			dto.getLatitude(),
			dto.getLongitude(),
			dto.getSubdistrictCode()
		);
	}

	/* 공통 Place 생성/업서트 로직 */
	private Place toPlaceCore(
		String placeId,
		String placeType,
		String placeName,
		String description,
		Double latitude,
		Double longitude,
		String subdistrictCode
	) {
		if (placeId == null || placeId.isBlank()) {
			return null;
		}
		PlaceUpsertCmd cmd = PlaceUpsertCmd.builder()
			.placeId(placeId)
			.placeTypeName(placeType)
			.placeName(placeName)
			.description(description)
			.latitude(latitude)
			.longitude(longitude)
			.subdistrictCode(subdistrictCode)
			.build();
		return placeService.findOrCreatePlace(cmd);
	}
}
