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
import com.wholeseeds.mindle.domain.complaint.dto.request.UpdateComplaintRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.ComplaintDetailResponseDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.ComplaintListResponseDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.SaveComplaintResponseDto;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.complaint.exception.ComplaintNotFoundException;
import com.wholeseeds.mindle.domain.complaint.exception.ImageUploadLimitExceeded;
import com.wholeseeds.mindle.domain.complaint.exception.NotComplaintOwnerException;
import com.wholeseeds.mindle.domain.complaint.mapper.ComplaintMapper;
import com.wholeseeds.mindle.domain.complaint.repository.ComplaintRepository;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.service.MemberService;
import com.wholeseeds.mindle.domain.place.dto.command.PlaceUpsertCmd;
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
	 * 민원을 생성하고(필요 시 이미지 업로드 포함) 저장한다.
	 *
	 * @param memberId  작성자 회원 ID
	 * @param requestDto 생성 요청 DTO
	 * @param imageList 업로드 이미지 목록(최대 3장)
	 * @return 저장 결과 응답 DTO
	 * @throws ImageUploadLimitExceeded 이미지 개수 제한 초과 시
	 */
	@Transactional
	public SaveComplaintResponseDto handleSaveComplaint(
		Long memberId,
		SaveComplaintRequestDto requestDto,
		List<MultipartFile> imageList
	) {
		validateImageCount(imageList);
		logRequest(requestDto, imageList);

		Complaint complaint = assembleNewComplaint(memberId, requestDto);
		Complaint saved = complaintRepository.save(complaint);

		handleImagesOnCreate(saved, imageList);
		return complaintMapper.toSaveComplaintResponseDto(saved);
	}

	/**
	 * 민원 상세(본문/지역/장소/이미지)와 공감 정보를 함께 조회한다.
	 *
	 * @param complaintId 민원 ID
	 * @param memberId    조회 회원 ID(공감 여부 계산)
	 * @return 상세 + 공감 응답 DTO
	 * @throws ComplaintNotFoundException 민원이 없을 때
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
	 * 민원 댓글을 커서 기반으로 최신순 조회한다.
	 *
	 * @param dto 댓글 조회 요청(커서/사이즈)
	 * @return 댓글 DTO 목록
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
	 * 민원 목록을 커서 기반으로 조회한다(도시/구/카테고리 필터 지원).
	 *
	 * @param dto 목록 조회 요청(커서/사이즈/필터)
	 * @return 민원 목록 DTO
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
	 * 민원을 부분 수정한다. 스칼라 필드는 MapStruct로 패치하고,
	 * 카테고리/행정동/장소는 전용 로직으로 처리한다. 이미지 교체/추가도 지원.
	 *
	 * @param memberId   요청자 회원 ID(소유자 검증)
	 * @param complaintId 민원 ID
	 * @param dto        수정 요청 DTO
	 * @param imageList  수정 이미지(교체/추가)
	 * @return 수정 결과 응답 DTO
	 * @throws ComplaintNotFoundException 대상 민원이 없을 때
	 * @throws NotComplaintOwnerException 소유자가 아닐 때
	 */
	@Transactional
	public SaveComplaintResponseDto handleUpdateComplaint(
		Long memberId,
		Long complaintId,
		UpdateComplaintRequestDto dto,
		List<MultipartFile> imageList
	) {
		Complaint complaint = complaintRepository.findByIdNotDeleted(complaintId)
			.orElseThrow(ComplaintNotFoundException::new);

		ensureOwner(complaint, memberId);

		complaintMapper.applyScalarPatch(dto, complaint);
		applyRelationsPatch(complaint, dto);
		handleImagesOnUpdate(complaint, dto, imageList);

		return complaintMapper.toSaveComplaintResponseDto(complaint);
	}

	/**
	 * 이미지 개수를 검증한다(최대 3장).
	 *
	 * @param imageList 업로드 이미지 목록
	 * @throws ImageUploadLimitExceeded 제한 초과 시
	 */
	private void validateImageCount(List<MultipartFile> imageList) {
		if (!ObjectUtils.objectIsNullOrEmpty(imageList) && imageList.size() > 3) {
			throw new ImageUploadLimitExceeded();
		}
	}

	/**
	 * 요청 DTO와 파일명을 로그로 남긴다.
	 *
	 * @param requestDto 생성 요청 DTO
	 * @param imageList  업로드 이미지 목록
	 */
	private void logRequest(SaveComplaintRequestDto requestDto, List<MultipartFile> imageList) {
		log.info("Request : {}", requestDto);
		if (!ObjectUtils.objectIsNullOrEmpty(imageList)) {
			imageList.forEach(image -> log.info("파일명 : {}", image.getOriginalFilename()));
		}
	}

	/**
	 * 신규 민원을 조립한다(카테고리/회원/행정동/장소 포함).
	 *
	 * @param memberId 작성자 회원 ID
	 * @param dto      생성 요청 DTO
	 * @return 미저장 Complaint 엔티티
	 */
	private Complaint assembleNewComplaint(Long memberId, SaveComplaintRequestDto dto) {
		Category category = categoryService.findCategory(dto.getCategoryId());
		Member member = memberService.getMember(memberId);
		Subdistrict subdistrict = regionService.findSubdistrict(dto.getSubdistrictCode());

		Place place = createOrResolvePlaceIfPresent(
			buildCmdFromSave(dto)
		);

		return Complaint.builder()
			.category(category)
			.member(member)
			.subdistrict(subdistrict)
			.place(place)
			.title(dto.getTitle())
			.content(dto.getContent())
			.latitude(dto.getLatitude())
			.longitude(dto.getLongitude())
			.build();
	}

	/**
	 * 민원 소유자(작성자)인지 검증한다.
	 *
	 * @param complaint 대상 민원
	 * @param memberId  요청자 회원 ID
	 * @throws NotComplaintOwnerException 작성자가 아닌 경우
	 */
	private void ensureOwner(Complaint complaint, Long memberId) {
		if (!complaint.getMember().getId().equals(memberId)) {
			throw new NotComplaintOwnerException();
		}
	}

	/**
	 * 연관관계(카테고리/행정동/장소) 수정 로직을 적용한다.
	 * clearPlace=true면 장소 연관을 제거한다.
	 *
	 * @param complaint 대상 민원
	 * @param dto       수정 요청 DTO
	 */
	private void applyRelationsPatch(Complaint complaint, UpdateComplaintRequestDto dto) {
		if (dto.getCategoryId() != null) {
			complaint.changeCategory(categoryService.findCategory(dto.getCategoryId()));
		}
		if (dto.getSubdistrictCode() != null) {
			complaint.changeSubdistrict(regionService.findSubdistrict(dto.getSubdistrictCode()));
		}

		if (Boolean.TRUE.equals(dto.getClearPlace())) {
			complaint.changePlace(null);
			return;
		}
		if (dto.getPlaceId() != null) {
			Place place = createOrResolvePlaceIfPresent(buildCmdFromUpdate(dto));
			complaint.changePlace(place);
		}
	}

	/**
	 * 생성 시 이미지 저장을 처리한다.
	 *
	 * @param saved     저장된 민원
	 * @param imageList 업로드 이미지 목록
	 */
	private void handleImagesOnCreate(Complaint saved, List<MultipartFile> imageList) {
		complaintImageService.saveComplaintImages(saved, imageList);
	}

	/**
	 * 수정 시 이미지 교체/추가를 처리한다.
	 *
	 * @param complaint 대상 민원
	 * @param dto       수정 요청 DTO(replaceImages 플래그)
	 * @param imageList 이미지 목록
	 */
	private void handleImagesOnUpdate(
		Complaint complaint,
		UpdateComplaintRequestDto dto,
		List<MultipartFile> imageList
	) {
		Boolean replace = dto.getReplaceImages();
		if (Boolean.TRUE.equals(replace)) {
			complaintImageService.replaceImages(complaint, imageList);
		} else {
			complaintImageService.appendImages(complaint, imageList);
		}
	}

	/**
	 * place 관련 필드들로 PlaceUpsertCmd를 생성한다.
	 * placeId가 비어있으면 null을 반환한다.
	 *
	 * @param placeId         외부 place id
	 * @param placeType       place type 명
	 * @param placeName       place 명
	 * @param description     설명
	 * @param latitude        위도
	 * @param longitude       경도
	 * @param subdistrictCode 행정동 코드
	 * @return PlaceUpsertCmd 또는 null
	 */
	private PlaceUpsertCmd buildCmdCore(
		String placeId,
		String placeType,
		String placeName,
		String description,
		Double latitude,
		Double longitude,
		String subdistrictCode
	) {
		if (placeId == null || placeId.isBlank()) {
			return null;
		}
		return PlaceUpsertCmd.builder()
			.placeId(placeId)
			.placeTypeName(placeType)
			.placeName(placeName)
			.description(description)
			.latitude(latitude)
			.longitude(longitude)
			.subdistrictCode(subdistrictCode)
			.build();
	}

	/**
	 * 생성 요청 DTO를 장소 업서트 커맨드로 변환한다.
	 * placeId가 비어있으면 null을 반환한다.
	 *
	 * @param dto 생성 요청 DTO
	 * @return PlaceUpsertCmd
	 */
	private PlaceUpsertCmd buildCmdFromSave(SaveComplaintRequestDto dto) {
		return buildCmdCore(
			dto.getPlaceId(),
			dto.getPlaceType(),
			dto.getPlaceName(),
			dto.getPlaceDescription(),
			dto.getLatitude(),
			dto.getLongitude(),
			dto.getSubdistrictCode()
		);
	}

	/**
	 * 수정 요청 DTO를 장소 업서트 커맨드로 변환한다.
	 * placeId가 비어있으면 null을 반환한다.
	 *
	 * @param dto 수정 요청 DTO
	 */
	private PlaceUpsertCmd buildCmdFromUpdate(UpdateComplaintRequestDto dto) {
		return buildCmdCore(
			dto.getPlaceId(),
			dto.getPlaceType(),
			dto.getPlaceName(),
			dto.getPlaceDescription(),
			dto.getLatitude(),
			dto.getLongitude(),
			dto.getSubdistrictCode()
		);
	}

	/**
	 * placeId가 있는 경우 장소 업서트를 수행하고, 없으면 null을 반환한다.
	 *
	 * @param cmd 업서트 커맨드(또는 null)
	 * @return 조회/복구/신규 Place, 또는 null
	 */
	private Place createOrResolvePlaceIfPresent(PlaceUpsertCmd cmd) {
		if (cmd == null) {
			return null;
		}
		return placeService.findOrCreatePlace(cmd);
	}

	/**
	 * 민원 상세 정보를 조회한다(이미지 목록 포함).
	 *
	 * @param complaintId 민원 ID
	 * @return 상세 DTO
	 * @throws ComplaintNotFoundException 민원이 없을 때
	 */
	private ComplaintDetailWithImagesDto findComplaintDetail(Long complaintId) {
		return complaintRepository.getComplaintWithImages(complaintId)
			.orElseThrow(ComplaintNotFoundException::new);
	}

	/**
	 * 민원의 공감 수와 회원의 공감 여부를 조회한다.
	 *
	 * @param complaintId 민원 ID
	 * @param memberId    회원 ID
	 * @return 공감 DTO
	 * @throws ComplaintNotFoundException 민원이 없을 때
	 */
	private ReactionDto findComplaintReaction(Long complaintId, Long memberId) {
		return complaintRepository.getReaction(complaintId, memberId)
			.orElseThrow(ComplaintNotFoundException::new);
	}
}
