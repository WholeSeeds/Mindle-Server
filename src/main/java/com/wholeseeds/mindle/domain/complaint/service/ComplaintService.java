package com.wholeseeds.mindle.domain.complaint.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wholeseeds.mindle.common.util.ObjectUtils;
import com.wholeseeds.mindle.domain.complaint.dto.CommentDto;
import com.wholeseeds.mindle.domain.complaint.dto.ComplaintDetailWithImagesDto;
import com.wholeseeds.mindle.domain.complaint.dto.ReactionDto;
import com.wholeseeds.mindle.domain.complaint.dto.request.CommentRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.request.ComplaintListRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.request.MyComplaintListRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.request.MyReactedComplaintListRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.request.SaveComplaintRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.request.UpdateComplaintRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.ComplaintDetailResponseDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.ComplaintListResponseDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.SaveComplaintResponseDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.VoteResolvedResponseDto;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.complaint.entity.ComplaintReaction;
import com.wholeseeds.mindle.domain.complaint.entity.ComplaintResolvedVote;
import com.wholeseeds.mindle.domain.complaint.exception.ComplaintNotFoundException;
import com.wholeseeds.mindle.domain.complaint.exception.ImageUploadLimitExceeded;
import com.wholeseeds.mindle.domain.complaint.exception.NotComplaintOwnerException;
import com.wholeseeds.mindle.domain.complaint.mapper.ComplaintMapper;
import com.wholeseeds.mindle.domain.complaint.mapper.ComplaintRelationMapper;
import com.wholeseeds.mindle.domain.complaint.repository.ComplaintReactionRepository;
import com.wholeseeds.mindle.domain.complaint.repository.ComplaintRepository;
import com.wholeseeds.mindle.domain.complaint.repository.ComplaintResolvedVoteRepository;
import com.wholeseeds.mindle.domain.member.entity.Member;
import com.wholeseeds.mindle.domain.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplaintService {
	private final ComplaintRepository complaintRepository;
	private final ComplaintMapper complaintMapper;
	private final ComplaintRelationMapper complaintRelationMapper;

	private final MemberService memberService;
	private final ComplaintImageService complaintImageService;
	private final ComplaintResolvedVoteRepository complaintResolvedVoteRepository;
	private final ComplaintReactionRepository complaintReactionRepository;

	private static final int RESOLVE_THRESHOLD = 5;

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

		Complaint complaint =
			complaintMapper.toNewComplaint(memberId, requestDto, complaintRelationMapper);
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
	 * 내가 작성한 민원 목록을 커서 기반으로 조회한다.
	 *
	 * @param memberId 조회 회원 ID
	 * @param dto      목록 조회 요청(커서/사이즈)
	 * @return 민원 목록 DTO
	 */
	@Transactional(readOnly = true)
	public List<ComplaintListResponseDto> getMyComplaintList(Long memberId, MyComplaintListRequestDto dto) {
		return complaintRepository.findMyListWithCursor(
			memberId,
			dto.getCursorComplaintId(),
			dto.getPageSize()
		);
	}

	/**
	 * 내가 공감한 민원 목록을 커서 기반으로 조회한다.
	 *
	 * @param memberId 조회 회원 ID
	 * @param dto      목록 조회 요청(커서/사이즈)
	 * @return 민원 목록 DTO
	 */
	@Transactional(readOnly = true)
	public List<ComplaintListResponseDto> getMyReactedComplaintList(
		Long memberId,
		MyReactedComplaintListRequestDto dto
	) {
		return complaintRepository.findReactedListWithCursor(
			memberId,
			dto.getCursorComplaintId(),
			dto.getPageSize()
		);
	}

	/**
	 * 민원을 부분 수정한다. 스칼라 필드는 MapStruct로 패치하고,
	 * 카테고리/행정동/장소는 매퍼의 연관 패치로 처리한다. 이미지 교체/추가도 지원.
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
		complaintMapper.applyRelationsPatch(dto, complaint, complaintRelationMapper);
		handleImagesOnUpdate(complaint, dto, imageList);

		return complaintMapper.toSaveComplaintResponseDto(complaint);
	}

	/**
	 * 민원을 삭제(soft delete)한다.
	 * - 작성자 본인만 삭제 가능하며, 연결된 이미지도 함께 soft delete 처리한다.
	 *
	 * @param memberId   요청자 회원 ID(소유자 검증)
	 * @param complaintId 민원 ID
	 * @throws ComplaintNotFoundException 대상 민원이 없거나 이미 삭제된 경우
	 * @throws NotComplaintOwnerException 소유자가 아닐 때
	 */
	@Transactional
	public void handleDeleteComplaint(Long memberId, Long complaintId) {
		Complaint complaint = complaintRepository.findByIdNotDeleted(complaintId)
			.orElseThrow(ComplaintNotFoundException::new);

		ensureOwner(complaint, memberId);

		complaintImageService.softDeleteAllForComplaint(complaint);
		complaint.softDelete();
		complaintRepository.save(complaint);
	}

	/**
	 * “해결됨” 투표 처리.
	 * - 동일 회원의 중복 투표 방지
	 * - 첫 투표일 때만 카운트 +1
	 * - 누적 5표 도달 시 자동 RESOLVED 전환
	 *
	 * @param memberId    투표 회원 ID
	 * @param complaintId 대상 민원 ID
	 * @return 변경된 민원 스냅샷 DTO(상태/카운트 확인용)
	 */
	@Transactional
	public VoteResolvedResponseDto handleResolvedVote(Long memberId, Long complaintId) {
		Complaint complaint = complaintRepository.findByIdNotDeleted(complaintId)
			.orElseThrow(ComplaintNotFoundException::new);

		boolean wasResolvedBefore = complaint.getStatus() == Complaint.Status.RESOLVED;

		// 이미 RESOLVED면 멱등 처리: 증가/전환 모두 false
		if (wasResolvedBefore) {
			return VoteResolvedResponseDto.builder()
				.incremented(false)
				.transitionedToResolved(false)
				.complaint(complaintMapper.toSaveComplaintResponseDto(complaint))
				.build();
		}

		boolean alreadyVoted = complaintResolvedVoteRepository
			.existsByComplaintIdAndMemberId(complaintId, memberId);

		boolean incremented = false;
		if (!alreadyVoted) {
			Member voter = memberService.getMember(memberId);
			complaintResolvedVoteRepository.save(ComplaintResolvedVote.of(complaint, voter));

			complaint.incrementResolvedVoteCount();
			complaint.markResolvedIfThresholdReached(RESOLVE_THRESHOLD);
			incremented = true;
		}

		boolean transitionedToResolved = complaint.getStatus() == Complaint.Status.RESOLVED;

		return VoteResolvedResponseDto.builder()
			.incremented(incremented)
			.transitionedToResolved(transitionedToResolved)
			.complaint(complaintMapper.toSaveComplaintResponseDto(complaint))
			.build();
	}

	/**
	 * 민원 공감 추가(멱등).
	 * - 이미 공감 중이면 그대로 성공 처리
	 * - 과거에 공감했다가 취소(soft delete)한 경우 restore
	 *
	 * @param memberId    회원 ID
	 * @param complaintId 민원 ID
	 * @return 공감 수/내 공감여부 DTO
	 */
	@Transactional
	public ReactionDto addReaction(Long memberId, Long complaintId) {
		var complaint = complaintRepository.findByIdNotDeleted(complaintId)
			.orElseThrow(ComplaintNotFoundException::new);

		// 활성 공감이 있으면 멱등 처리
		Optional<ComplaintReaction> active = complaintReactionRepository
			.findByComplaintIdAndMemberIdAndDeletedAtIsNull(complaintId, memberId);
		if (active.isPresent()) {
			return latestReaction(complaintId, memberId);
		}

		// soft-deleted 존재 시 복구, 없으면 신규 생성
		Optional<ComplaintReaction> any = complaintReactionRepository
			.findByComplaintIdAndMemberId(complaintId, memberId);

		if (any.isPresent()) {
			var reaction = any.get();
			if (reaction.isDeleted()) {
				reaction.restore();
				complaintReactionRepository.save(reaction);
			}
		} else {
			var member = memberService.getMember(memberId);
			try {
				complaintReactionRepository.save(
					ComplaintReaction.builder()
						.complaint(complaint)
						.member(member)
						.build()
				);
			} catch (DataIntegrityViolationException e) {
				// 동시성 상황에서 유니크 제약 충돌 → 이미 다른 트랜잭션이 생성함. 멱등 처리.
				log.warn("Concurrent reaction insert detected. complaintId={}, memberId={}", complaintId, memberId);
			}
		}

		return latestReaction(complaintId, memberId);
	}

	/**
	 * 민원 공감 취소(멱등).
	 * - 활성 공감이 있으면 soft delete
	 * - 없으면 그대로 성공 처리
	 *
	 * @param memberId    회원 ID
	 * @param complaintId 민원 ID
	 * @return 공감 수/내 공감여부 DTO
	 */
	@Transactional
	public ReactionDto cancelReaction(Long memberId, Long complaintId) {
		complaintRepository.findByIdNotDeleted(complaintId)
			.orElseThrow(ComplaintNotFoundException::new);

		Optional<ComplaintReaction> active = complaintReactionRepository
			.findByComplaintIdAndMemberIdAndDeletedAtIsNull(complaintId, memberId);

		if (active.isPresent()) {
			var reaction = active.get();
			reaction.softDelete();
			complaintReactionRepository.save(reaction);
		}

		return latestReaction(complaintId, memberId);
	}

	private ReactionDto latestReaction(Long complaintId, Long memberId) {
		return complaintRepository.getReaction(complaintId, memberId)
			.orElseThrow(ComplaintNotFoundException::new);
	}

	private void validateImageCount(List<MultipartFile> imageList) {
		if (!ObjectUtils.objectIsNullOrEmpty(imageList) && imageList.size() > 3) {
			throw new ImageUploadLimitExceeded();
		}
	}

	private void logRequest(SaveComplaintRequestDto requestDto, List<MultipartFile> imageList) {
		log.info("Request : {}", requestDto);
		if (!ObjectUtils.objectIsNullOrEmpty(imageList)) {
			imageList.forEach(image -> log.info("파일명 : {}", image.getOriginalFilename()));
		}
	}

	private void ensureOwner(Complaint complaint, Long memberId) {
		if (!complaint.getMember().getId().equals(memberId)) {
			throw new NotComplaintOwnerException();
		}
	}

	private void handleImagesOnCreate(Complaint saved, List<MultipartFile> imageList) {
		complaintImageService.saveComplaintImages(saved, imageList);
	}

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

	private ComplaintDetailWithImagesDto findComplaintDetail(Long complaintId) {
		return complaintRepository.getComplaintWithImages(complaintId)
			.orElseThrow(ComplaintNotFoundException::new);
	}

	private ReactionDto findComplaintReaction(Long complaintId, Long memberId) {
		return complaintRepository.getReaction(complaintId, memberId)
			.orElseThrow(ComplaintNotFoundException::new);
	}
}
