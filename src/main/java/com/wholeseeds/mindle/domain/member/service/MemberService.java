package com.wholeseeds.mindle.domain.member.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	public Member getMember(Long id) {
		return memberRepository.findByIdNotDeleted(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 삭제된 회원입니다."));
	}

	public List<Member> getAllActiveMembers() {
		return memberRepository.findAllNotDeleted();
	}
}
