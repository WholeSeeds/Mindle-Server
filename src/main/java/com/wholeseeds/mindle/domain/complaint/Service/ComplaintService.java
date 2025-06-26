package com.wholeseeds.mindle.domain.complaint.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wholeseeds.mindle.domain.complaint.dto.SaveComplaintRequestDto;
import com.wholeseeds.mindle.domain.complaint.entity.Category;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.complaint.repository.CategoryRepository;
import com.wholeseeds.mindle.domain.complaint.repository.ComplaintRepository;
import com.wholeseeds.mindle.domain.location.entity.City;
import com.wholeseeds.mindle.domain.location.entity.District;
import com.wholeseeds.mindle.domain.location.entity.Subdistrict;
import com.wholeseeds.mindle.domain.location.repository.CityRepository;
import com.wholeseeds.mindle.domain.location.repository.DistrictRepository;
import com.wholeseeds.mindle.domain.location.repository.SubdistrictRepository;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.repository.MemberRepository;
import com.wholeseeds.mindle.domain.place.entity.Place;
import com.wholeseeds.mindle.domain.place.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComplaintService {
	private final ComplaintRepository complaintRepository;
	private final CategoryRepository categoryRepository;
	private final MemberRepository memberRepository;
	private final CityRepository cityRepository;
	private final DistrictRepository districtRepository;
	private final SubdistrictRepository subdistrictRepository;
	private final PlaceRepository placeRepository;

	/**
	 * 테스트
	 * 1. request 에 장소값이 있을때 잘 저장되는지
	 * 2. request 에 장소값이 없을때 잘 저장되는지
	 */
	@Transactional
	public Complaint saveComplaint(SaveComplaintRequestDto requestDto) {
		Category category = categoryRepository.findById(requestDto.getCategoryId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
		Member member = memberRepository.findById(requestDto.getMemberId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

		Subdistrict subdistrict = null;
		Place place = null;
		// 주소값이 모두 null 이 아닌 경우에만 장소 저장
		if (requestDto.getCityName() != null && requestDto.getDistrictName() != null
			&& requestDto.getSubdistrictName() != null && requestDto.getPlaceName() != null) {
			City city = cityRepository.findByName(requestDto.getCityName())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도시입니다."));
			District district = districtRepository.findByName(requestDto.getDistrictName())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 구입니다."));
			subdistrict = subdistrictRepository.findByNameAndDistrict(requestDto.getSubdistrictName(), district)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 동입니다."));
			place = placeRepository.findByName(requestDto.getPlaceName());
		}

		Complaint complaint = Complaint.builder()
			.category(category)
			.member(member)
			.subdistrictId(subdistrict)
			.placeId(place)
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.latitude(requestDto.getLatitude())
			.longitude(requestDto.getLongitude())
			.build();
		Complaint saved = complaintRepository.save(complaint);
		return saved;
	}
}
