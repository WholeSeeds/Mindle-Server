package com.wholeseeds.mindle.domain.complaint.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wholeseeds.mindle.common.service.NcpObjectStorageService;
import com.wholeseeds.mindle.domain.complaint.dto.SaveComplaintRequestDto;
import com.wholeseeds.mindle.domain.complaint.entity.Category;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.complaint.entity.ComplaintImage;
import com.wholeseeds.mindle.domain.complaint.exception.CategoryNotFoundException;
import com.wholeseeds.mindle.domain.complaint.exception.CityNotFoundException;
import com.wholeseeds.mindle.domain.complaint.exception.DistrictNotFoundException;
import com.wholeseeds.mindle.domain.complaint.exception.SubdistrictNotFoundException;
import com.wholeseeds.mindle.domain.complaint.repository.CategoryRepository;
import com.wholeseeds.mindle.domain.complaint.repository.ComplaintImageRepository;
import com.wholeseeds.mindle.domain.complaint.repository.ComplaintRepository;
import com.wholeseeds.mindle.domain.location.entity.City;
import com.wholeseeds.mindle.domain.location.entity.District;
import com.wholeseeds.mindle.domain.location.entity.Subdistrict;
import com.wholeseeds.mindle.domain.location.repository.CityRepository;
import com.wholeseeds.mindle.domain.location.repository.DistrictRepository;
import com.wholeseeds.mindle.domain.location.repository.SubdistrictRepository;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.exception.MemberNotFoundException;
import com.wholeseeds.mindle.domain.member.repository.MemberRepository;
import com.wholeseeds.mindle.domain.place.entity.Place;
import com.wholeseeds.mindle.domain.place.repository.PlaceRepository;

import jakarta.annotation.Nullable;
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
	private final ComplaintImageRepository complaintImageRepository;

	private final NcpObjectStorageService ncpObjectStorageService;

	@Transactional
	public Complaint saveComplaint(SaveComplaintRequestDto requestDto, @Nullable MultipartFile image) {
		Category category = categoryRepository.findById(requestDto.getCategoryId())
			.orElseThrow(CategoryNotFoundException::new);
		Member member = memberRepository.findById(requestDto.getMemberId())
			.orElseThrow(MemberNotFoundException::new);

		// 주소값이 모두 null 이 아닌 경우에만 장소 저장
		Subdistrict subdistrict = null;
		Place place = null;
		if (requestDto.getCityName() != null && requestDto.getDistrictName() != null
			&& requestDto.getSubdistrictName() != null && requestDto.getPlaceName() != null) {
			City city = cityRepository.findByName(requestDto.getCityName())
				.orElseThrow(CityNotFoundException::new);
			District district = districtRepository.findByName(requestDto.getDistrictName())
				.orElseThrow(DistrictNotFoundException::new);
			subdistrict = subdistrictRepository.findByNameAndDistrict(requestDto.getSubdistrictName(), district)
				.orElseThrow(SubdistrictNotFoundException::new);
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

		// 이미지 업로드 & image 테이블에 url 저장
		String imageUrl = null;
		if (image != null && !image.isEmpty()) {
			imageUrl = ncpObjectStorageService.uploadFile("complaint", image);
			ComplaintImage complaintImage = ComplaintImage.builder()
				.complaint(saved)
				.imageUrl(imageUrl)
				.build();
			complaintImageRepository.save(complaintImage);
		}
		return saved;
	}
}
