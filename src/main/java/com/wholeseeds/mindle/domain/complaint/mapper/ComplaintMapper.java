package com.wholeseeds.mindle.domain.complaint.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.wholeseeds.mindle.domain.complaint.dto.request.UpdateComplaintRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.SaveComplaintResponseDto;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.place.mapper.PlaceMapper;
import com.wholeseeds.mindle.domain.region.mapper.RegionMapper;

@Mapper(componentModel = "spring", uses = {RegionMapper.class, PlaceMapper.class})
public interface ComplaintMapper {
	@Mapping(target = "categoryId", source = "category.id")
	@Mapping(target = "memberId", source = "member.id")
	@Mapping(target = "subdistrictDto", source = "subdistrict")
	@Mapping(target = "placeDto", source = "place")
	@Mapping(target = "resolvedVoteCount", source = "resolvedVoteCount")
	SaveComplaintResponseDto toSaveComplaintResponseDto(Complaint complaint);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void applyScalarPatch(UpdateComplaintRequestDto dto, @MappingTarget Complaint target);
}
