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

	/* --- 단건 ID → 엔티티 해석 --- */

	public Category toCategory(Long id) {
		return categoryService.findCategory(id);
	}

	public Member toMember(Long id) {
		return memberService.getMember(id);
	}

	public Subdistrict toSubdistrict(String code) {
		// Save/Update 공통: 유효하지 않으면 서비스에서 예외 발생
		return regionService.findSubdistrict(code);
	}

	/* --- Save DTO → Place 해석 (placeId 없으면 null) --- */
	@Named("toPlaceFromSave")
	public Place toPlaceFromSave(SaveComplaintRequestDto dto) {
		if (dto.getPlaceId() == null || dto.getPlaceId().isBlank()) {
			return null;
		}
		PlaceUpsertCmd cmd = PlaceUpsertCmd.builder()
			.placeId(dto.getPlaceId())
			.placeTypeName(dto.getPlaceType())
			.placeName(dto.getPlaceName())
			.description(dto.getPlaceDescription())
			.latitude(dto.getLatitude())
			.longitude(dto.getLongitude())
			.subdistrictCode(dto.getSubdistrictCode())
			.build();
		return placeService.findOrCreatePlace(cmd);
	}

	/* --- Update DTO → Place 해석 (placeId 없으면 null) --- */

	@Named("toPlaceFromUpdate")
	public Place toPlaceFromUpdate(UpdateComplaintRequestDto dto) {
		if (dto.getPlaceId() == null || dto.getPlaceId().isBlank()) {
			return null;
		}
		PlaceUpsertCmd cmd = PlaceUpsertCmd.builder()
			.placeId(dto.getPlaceId())
			.placeTypeName(dto.getPlaceType())
			.placeName(dto.getPlaceName())
			.description(dto.getPlaceDescription())
			.latitude(dto.getLatitude())
			.longitude(dto.getLongitude())
			.subdistrictCode(dto.getSubdistrictCode())
			.build();
		return placeService.findOrCreatePlace(cmd);
	}

	@Named("toSubdistrictFromSave")
	public Subdistrict toSubdistrictFromSave(SaveComplaintRequestDto dto) {
		return regionService.findSubdistrict(dto.getSubdistrictCode());
	}
}

