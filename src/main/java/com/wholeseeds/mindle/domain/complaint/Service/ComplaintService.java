package com.wholeseeds.mindle.domain.complaint.Service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wholeseeds.mindle.common.code.CommonCode;
import com.wholeseeds.mindle.domain.complaint.dto.CommentDto;
import com.wholeseeds.mindle.domain.complaint.dto.CommentRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.ComplaintDetailWithImagesDto;
import com.wholeseeds.mindle.domain.complaint.dto.ComplaintListRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.ComplaintListResponseDto;
import com.wholeseeds.mindle.domain.complaint.dto.ReactionDto;
import com.wholeseeds.mindle.domain.complaint.dto.SaveComplaintRequestDto;
import com.wholeseeds.mindle.domain.complaint.entity.Category;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.complaint.entity.ComplaintImage;
import com.wholeseeds.mindle.domain.complaint.exception.CategoryNotFoundException;
import com.wholeseeds.mindle.domain.complaint.exception.CityNotFoundException;
import com.wholeseeds.mindle.domain.complaint.exception.ComplaintNotFoundException;
import com.wholeseeds.mindle.domain.complaint.exception.SubdistrictNotFoundException;
import com.wholeseeds.mindle.domain.complaint.repository.CategoryRepository;
import com.wholeseeds.mindle.domain.complaint.repository.ComplaintImageRepository;
import com.wholeseeds.mindle.domain.complaint.repository.ComplaintRepository;
import com.wholeseeds.mindle.domain.location.entity.City;
import com.wholeseeds.mindle.domain.location.entity.Subdistrict;
import com.wholeseeds.mindle.domain.location.repository.CityRepository;
import com.wholeseeds.mindle.domain.location.repository.DistrictRepository;
import com.wholeseeds.mindle.domain.location.repository.SubdistrictRepository;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.exception.MemberNotFoundException;
import com.wholeseeds.mindle.domain.member.repository.MemberRepository;
import com.wholeseeds.mindle.domain.place.entity.Place;
import com.wholeseeds.mindle.domain.place.exception.PlaceNotFoundException;
import com.wholeseeds.mindle.domain.place.repository.PlaceRepository;
import com.wholeseeds.mindle.infra.Service.NcpObjectStorageService;

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
	public Complaint saveComplaint(SaveComplaintRequestDto requestDto, List<MultipartFile> imageList) {
		Category category = categoryRepository.findById(requestDto.getCategoryId())
			.orElseThrow(CategoryNotFoundException::new);
		Member member = memberRepository.findById(requestDto.getMemberId())
			.orElseThrow(MemberNotFoundException::new);
		Subdistrict subdistrict = findSubdistrict(requestDto);
		Place place = null;
		if (requestDto.getPlaceId() != null) {
			place = placeRepository.findByPlaceId(requestDto.getPlaceId()).orElseThrow(PlaceNotFoundException::new);
		}

		Complaint complaint = Complaint.builder()
			.category(category)
			.member(member)
			.subdistrict(subdistrict)
			.place(place)
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.latitude(requestDto.getLatitude())
			.longitude(requestDto.getLongitude())
			.build();
		Complaint saved = complaintRepository.save(complaint);

		/* 이미지 업로드 & image 테이블에 url 저장 */
		if (CommonCode.objectIsNullOrEmpty(imageList)) {
			return saved;
		}
		for (MultipartFile imageFile : imageList) {
			String imageUrl = ncpObjectStorageService.uploadFile("complaint", imageFile);
			ComplaintImage complaintImage = ComplaintImage.builder()
				.complaint(saved)
				.imageUrl(imageUrl)
				.build();
			complaintImageRepository.save(complaintImage);
		}

		return saved;
	}

	public Subdistrict findSubdistrict(SaveComplaintRequestDto dto) {
		if (dto.getCityName() == null || dto.getSubdistrictName() == null) { // 주소값 없는 글
			return null;
		}
		City city = cityRepository.findByName(dto.getCityName()).orElseThrow(CityNotFoundException::new);
		return subdistrictRepository.findByNameAndCity(dto.getSubdistrictName(), city)
			.orElseThrow(SubdistrictNotFoundException::new);
	}

	public ComplaintDetailWithImagesDto getComplaintDetail(Long complaintId) {
		return complaintRepository.getComplaintWithImages(complaintId).orElseThrow(ComplaintNotFoundException::new);
	}

	public ReactionDto getComplaintReaction(Long complaintId, Long memberId) {
		return complaintRepository.getReaction(complaintId, memberId).orElseThrow(ComplaintNotFoundException::new);
	}

	public List<CommentDto> getComplaintComments(CommentRequestDto dto) {
		return complaintRepository.getComment(dto.getComplaintId(),
			CommonCode.stringToLocalDateTime(dto.getCursorCreatedAt()), dto.getPageSize());
	}

	public List<ComplaintListResponseDto> getComplaintList(ComplaintListRequestDto dto) {
		return complaintRepository.findListWithCursor(dto.getCursorComplaintId(), dto.getPageSize(), dto.getCityId(),
			dto.getDistrictId(), dto.getCategoryId());
	}

}
