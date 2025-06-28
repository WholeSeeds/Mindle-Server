package com.wholeseeds.mindle.domain.member.mapper;

import org.mapstruct.Mapper;

import com.wholeseeds.mindle.domain.member.dto.response.MemberResponseDto;
import com.wholeseeds.mindle.domain.member.entity.Member;

@Mapper(componentModel = "spring")
public interface MemberMapper {

	MemberResponseDto toMemberResponseDto(Member member);
}
