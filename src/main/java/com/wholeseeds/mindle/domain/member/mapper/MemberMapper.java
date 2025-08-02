package com.wholeseeds.mindle.domain.member.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.wholeseeds.mindle.domain.region.dto.SubdistrictDto;
import com.wholeseeds.mindle.domain.region.entity.Subdistrict;
import com.wholeseeds.mindle.domain.member.dto.response.MemberResponseDto;
import com.wholeseeds.mindle.domain.member.entity.Member;

@Mapper(componentModel = "spring")
public interface MemberMapper {

	@Mapping(source = "subdistrict", target = "subdistrict")
	MemberResponseDto toMemberResponseDto(Member member);

	SubdistrictDto toSubdistrictDto(Subdistrict subdistrict);
}
