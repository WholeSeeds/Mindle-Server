package com.wholeseeds.mindle.domain.complaint.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.wholeseeds.mindle.domain.complaint.dto.request.SaveComplaintRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.request.UpdateComplaintRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.SaveComplaintResponseDto;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.place.mapper.PlaceMapper;
import com.wholeseeds.mindle.domain.region.mapper.RegionMapper;

@Mapper(
	componentModel = "spring",
	uses = {RegionMapper.class, PlaceMapper.class, ComplaintRelationMapper.class}
)
public interface ComplaintMapper {

	@Mapping(target = "categoryId", source = "category.id")
	@Mapping(target = "memberId", source = "member.id")
	@Mapping(target = "subdistrictDto", source = "subdistrict")
	@Mapping(target = "placeDto", source = "place")
	@Mapping(target = "resolvedVoteCount", source = "resolvedVoteCount")
	SaveComplaintResponseDto toSaveComplaintResponseDto(Complaint complaint);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void applyScalarPatch(UpdateComplaintRequestDto dto, @MappingTarget Complaint target);

	@Mapping(target = "category", source = "dto.categoryId")
	@Mapping(target = "member", source = "memberId")
	@Mapping(target = "subdistrict", source = "dto.subdistrictCode") // ★ 여기만 변경
	@Mapping(target = "place", source = "dto", qualifiedByName = "toPlaceFromSave")
	@Mapping(target = "title", source = "dto.title")
	@Mapping(target = "content", source = "dto.content")
	@Mapping(target = "latitude", source = "dto.latitude")
	@Mapping(target = "longitude", source = "dto.longitude")
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "isResolved", ignore = true)
	@Mapping(target = "resolvedVoteCount", ignore = true)
	Complaint toNewComplaint(
		Long memberId,
		SaveComplaintRequestDto dto,
		@Context ComplaintRelationMapper resolver
	);

	default void applyRelationsPatch(
		UpdateComplaintRequestDto dto,
		@MappingTarget Complaint target,
		@Context ComplaintRelationMapper resolver
	) {
		if (dto.getCategoryId() != null) {
			target.changeCategory(resolver.toCategory(dto.getCategoryId()));
		}
		if (dto.getSubdistrictCode() != null) {
			target.changeSubdistrict(resolver.toSubdistrict(dto.getSubdistrictCode()));
		}
		if (Boolean.TRUE.equals(dto.getClearPlace())) {
			target.changePlace(null);
		} else if (dto.getPlaceId() != null) {
			target.changePlace(resolver.toPlaceFromUpdate(dto));
		}
	}
}
