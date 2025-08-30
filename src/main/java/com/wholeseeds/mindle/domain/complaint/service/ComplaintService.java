package com.wholeseeds.mindle.domain.complaint.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wholeseeds.mindle.common.util.ObjectUtils;
import com.wholeseeds.mindle.domain.category.entity.Category;
import com.wholeseeds.mindle.domain.category.service.CategoryService;
import com.wholeseeds.mindle.domain.complaint.dto.CommentDto;
import com.wholeseeds.mindle.domain.complaint.dto.ComplaintDetailWithImagesDto;
import com.wholeseeds.mindle.domain.complaint.dto.ReactionDto;
import com.wholeseeds.mindle.domain.complaint.dto.request.CommentRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.request.ComplaintListRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.request.SaveComplaintRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.ComplaintDetailResponseDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.ComplaintListResponseDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.SaveComplaintResponseDto;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.complaint.exception.ComplaintNotFoundException;
import com.wholeseeds.mindle.domain.complaint.exception.ImageUploadLimitExceeded;
import com.wholeseeds.mindle.domain.complaint.mapper.ComplaintMapper;
import com.wholeseeds.mindle.domain.complaint.repository.ComplaintRepository;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.service.MemberService;
import com.wholeseeds.mindle.domain.place.entity.Place;
import com.wholeseeds.mindle.domain.place.service.PlaceService;
import com.wholeseeds.mindle.domain.region.entity.Subdistrict;
import com.wholeseeds.mindle.domain.region.service.RegionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplaintService {
	private final ComplaintRepository complaintRepository;
	private final ComplaintMapper complaintMapper;
	private final MemberService memberService;
	private final RegionService regionService;
	private final PlaceService placeService;
	private final ComplaintImageService complaintImageService;
	private final CategoryService categoryService;

	/**
	 * 민원 등록
	 */
	@Transactional
	public SaveComplaintResponseDto handleSaveComplaint(
		Long memberId,
		SaveComplaintRequestDto requestDto,
		List<MultipartFile> imageList
	) {
		validateImageCount(imageList);
		logRequest(requestDto, imageList);

		Complaint saved = createAndSaveComplaint(memberId, requestDto, imageList);
		return complaintMapper.toSaveComplaintResponseDto(saved);
	}

	/**
	 * 민원 상세 정보 및 공감 정보 조회
	 * @param complaintId 민원 ID
	 * @param memberId 회원 ID
	 * @return 민원 상세 정보 DTO
	 */
	@Transactional(readOnly = true)
	public ComplaintDetailResponseDto getComplaintDetailResponse(Long complaintId, Long memberId) {
		ComplaintDetailWithImagesDto complaint = findComplaintDetail(complaintId);
		ReactionDto reactionDto = findComplaintReaction(complaintId, memberId);

		return ComplaintDetailResponseDto.builder()
			.complaintDetailWithImagesDto(complaint)
			.reactionDto(reactionDto)
			.build();
	}

	/**
	 * 민원에 대한 댓글 목록 조회
	 * @param dto 댓글 요청 DTO
	 * @return 댓글 목록
	 */
	@Transactional(readOnly = true)
	public List<CommentDto> getComplaintCommentsResponse(CommentRequestDto dto) {
		return complaintRepository.getComment(
			dto.getComplaintId(),
			ObjectUtils.stringToLocalDateTime(dto.getCursorCreatedAt()),
			dto.getPageSize()
		);
	}

	/**
	 * 민원 목록 조회 (커서 기반)
	 * @param dto 요청 DTO
	 * @return 민원 목록
	 */
	@Transactional(readOnly = true)
	public List<ComplaintListResponseDto> getComplaintListResponse(ComplaintListRequestDto dto) {
		return complaintRepository.findListWithCursor(
			dto.getCursorComplaintId(),
			dto.getPageSize(),
			dto.getCityCode(),
			dto.getDistrictCode(),
			dto.getCategoryId()
		);
	}

	/**
	 * 이미지 파일 개수 검증
	 */
	private void validateImageCount(List<MultipartFile> imageList) {
		if (!ObjectUtils.objectIsNullOrEmpty(imageList) && imageList.size() > 3) {
			throw new ImageUploadLimitExceeded();
		}
	}

	/**
	 * 요청 DTO와 이미지 정보 로그 출력
	 */
	private void logRequest(SaveComplaintRequestDto requestDto, List<MultipartFile> imageList) {
		log.info("Request : {}", requestDto);
		if (!ObjectUtils.objectIsNullOrEmpty(imageList)) {
			imageList.forEach(image -> log.info("파일명 : {}", image.getOriginalFilename()));
		}
	}

	/**
	 * 민원 생성 및 저장
	 * @param requestDto 민원 요청 DTO
	 * @param imageList 이미지 파일 목록
	 * @return 저장된 Complaint 객체
	 */
	private Complaint createAndSaveComplaint(
		Long memberId,
		SaveComplaintRequestDto requestDto,
		List<MultipartFile> imageList
	) {
		Category category = categoryService.findCategory(requestDto.getCategoryId());
		Member member = memberService.getMember(memberId);
		Subdistrict subdistrict = regionService.findSubdistrict(requestDto.getSubdistrictCode());
		Place place = placeService.findPlace(requestDto.getPlaceId());

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
		complaintImageService.saveComplaintImages(saved, imageList);
		return saved;
	}

	/**
	 * 민원 상세 정보 조회
	 * @param complaintId 민원 ID
	 * @return 민원 상세 정보 DTO
	 */
	private ComplaintDetailWithImagesDto findComplaintDetail(Long complaintId) {
		return complaintRepository.getComplaintWithImages(complaintId)
			.orElseThrow(ComplaintNotFoundException::new);
	}

	/**
	 * 민원에 대한 공감 정보 조회
	 * @param complaintId 민원 ID
	 * @param memberId 회원 ID
	 * @return 민원 공감 정보 DTO
	 */
	private ReactionDto findComplaintReaction(Long complaintId, Long memberId) {
		return complaintRepository.getReaction(complaintId, memberId)
			.orElseThrow(ComplaintNotFoundException::new);
	}
}
