package com.wholeseeds.mindle.domain.complaint.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.wholeseeds.mindle.domain.complaint.dto.response.SaveComplaintResponseDto;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.region.mapper.RegionMapper;

@Mapper(componentModel = "spring", uses = {RegionMapper.class})
public interface ComplaintMapper {
	@Mapping(target = "categoryId", source = "category.id")
	@Mapping(target = "memberId", source = "member.id")
	@Mapping(target = "subdistrictDto", source = "subdistrict")
	@Mapping(target = "placeId", source = "place.id")
	SaveComplaintResponseDto toSaveComplaintResponseDto(Complaint complaint);
}
