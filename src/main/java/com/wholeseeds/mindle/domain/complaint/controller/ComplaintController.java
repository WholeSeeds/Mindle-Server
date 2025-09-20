package com.wholeseeds.mindle.domain.complaint.controller;

import static org.springframework.http.MediaType.*;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wholeseeds.mindle.common.annotation.CurrentMemberId;
import com.wholeseeds.mindle.common.annotation.RequireAuth;
import com.wholeseeds.mindle.common.util.ResponseTemplate;
import com.wholeseeds.mindle.domain.complaint.dto.CommentDto;
import com.wholeseeds.mindle.domain.complaint.dto.ReactionDto;
import com.wholeseeds.mindle.domain.complaint.dto.request.CommentRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.request.ComplaintListRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.request.ReactionUpdateRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.request.SaveComplaintRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.request.UpdateComplaintRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.ComplaintDetailResponseDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.ComplaintListResponseDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.SaveComplaintResponseDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.VoteResolvedResponseDto;
import com.wholeseeds.mindle.domain.complaint.service.ComplaintService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "민원")
@RestController
@RequestMapping("/api/complaint")
@RequiredArgsConstructor
@Slf4j
public class ComplaintController {

	private final ComplaintService complaintService;
	private final ResponseTemplate responseTemplate;

	/**
	 * 민원 등록 API
	 */
	@Operation(
		summary = "민원 등록",
		description = "민원 정보를 등록합니다. 최대 3개의 이미지를 함께 업로드할 수 있습니다."
	)
	@ApiResponse(
		responseCode = "201",
		description = "민원 등록 성공",
		content = @Content(schema = @Schema(implementation = SaveComplaintResponseDto.class))
	)
	@PostMapping(value = "/save", consumes = MULTIPART_FORM_DATA_VALUE)
	@RequireAuth
	public ResponseEntity<Map<String, Object>> saveComplaint(
		@Parameter(hidden = true) @CurrentMemberId Long memberId,
		@RequestPart("meta") SaveComplaintRequestDto requestDto,
		@RequestPart(value = "files", required = false) List<MultipartFile> imageList
	) {
		SaveComplaintResponseDto responseDto = complaintService.handleSaveComplaint(memberId, requestDto, imageList);
		return responseTemplate.success(responseDto, HttpStatus.CREATED);
	}

	/**
	 * 민원 상세 조회 API
	 */
	@Operation(
		summary = "민원 상세 조회",
		description = "민원 ID를 통해 민원 상세 정보 및 이미지, 공감 정보를 조회합니다."
	)
	@ApiResponse(
		responseCode = "200",
		description = "민원 상세 정보 반환",
		content = @Content(schema = @Schema(implementation = ComplaintDetailResponseDto.class))
	)
	@GetMapping("/detail/{complaintId}")
	@RequireAuth
	public ResponseEntity<Map<String, Object>> getComplaintDetail(
		@PathVariable Long complaintId,
		@Parameter(hidden = true) @CurrentMemberId Long memberId
	) {
		ComplaintDetailResponseDto responseDto = complaintService.getComplaintDetailResponse(complaintId, memberId);
		return responseTemplate.success(responseDto, HttpStatus.OK);
	}

	/**
	 * 민원 댓글 조회 API
	 */
	@Operation(
		summary = "민원 댓글 조회",
		description = "커서 기반으로 민원에 작성된 댓글을 최신순으로 조회합니다."
	)
	@ApiResponse(
		responseCode = "200",
		description = "댓글 목록 반환",
		content = @Content(
			mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = CommentDto.class))
		)
	)
	@GetMapping("/detail/comment")
	@RequireAuth
	public ResponseEntity<Map<String, Object>> getComplaintComments(@ModelAttribute CommentRequestDto requestDto) {
		List<CommentDto> responseDtos = complaintService.getComplaintCommentsResponse(requestDto);
		return responseTemplate.success(responseDtos, HttpStatus.OK);
	}

	/**
	 * 민원 목록 조회 API
	 */
	@Operation(
		summary = "민원 목록 조회",
		description = "커서 기반으로 민원 목록을 조회합니다. 시/구/카테고리별 필터링이 가능합니다."
	)
	@ApiResponse(
		responseCode = "200",
		description = "민원 목록 반환",
		content = @Content(
			mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = ComplaintListResponseDto.class))
		)
	)
	@GetMapping("/list")
	@RequireAuth
	public ResponseEntity<Map<String, Object>> getComplaintList(@ModelAttribute ComplaintListRequestDto requestDto) {
		List<ComplaintListResponseDto> responseDtos = complaintService.getComplaintListResponse(requestDto);
		return responseTemplate.success(responseDtos, HttpStatus.OK);
	}

	/**
	 * 민원 수정 API
	 */
	@Operation(
		summary = "민원 수정",
		description = "민원 일부 필드를 수정합니다. replaceImages=true일 경우 기존 이미지를 모두 교체합니다."
	)
	@ApiResponse(
		responseCode = "200",
		description = "민원 수정 성공",
		content = @Content(schema = @Schema(implementation = SaveComplaintResponseDto.class))
	)
	@PatchMapping(value = "/{complaintId}", consumes = MULTIPART_FORM_DATA_VALUE)
	@RequireAuth
	public ResponseEntity<Map<String, Object>> updateComplaint(
		@PathVariable Long complaintId,
		@Parameter(hidden = true) @CurrentMemberId Long memberId,
		@RequestPart("meta") UpdateComplaintRequestDto requestDto,
		@RequestPart(value = "files", required = false) List<MultipartFile> imageList
	) {
		SaveComplaintResponseDto responseDto =
			complaintService.handleUpdateComplaint(memberId, complaintId, requestDto, imageList);
		return responseTemplate.success(responseDto, HttpStatus.OK);
	}

	/**
	 * 민원 삭제 API
	 */
	@Operation(
		summary = "민원 삭제",
		description = "민원을 soft delete 합니다. 작성자 본인만 삭제할 수 있습니다."
	)
	@ApiResponse(
		responseCode = "204",
		description = "민원 삭제 성공"
	)
	@DeleteMapping("/{complaintId}")
	@RequireAuth
	public ResponseEntity<Map<String, Object>> deleteComplaint(
		@PathVariable Long complaintId,
		@Parameter(hidden = true) @CurrentMemberId Long memberId
	) {
		complaintService.handleDeleteComplaint(memberId, complaintId);
		return responseTemplate.success(null, HttpStatus.OK);
	}

	/**
	 * “해결됨” 투표 API
	 */
	@Operation(
		summary = "민원 해결됨 투표",
		description = "특정 민원에 대해 '해결됨' 투표를 1만큼 추가합니다. 누적 5표 시 자동 RESOLVED로 전환합니다."
	)
	@ApiResponse(
		responseCode = "200",
		description = "투표 반영 성공",
		content = @Content(schema = @Schema(implementation = VoteResolvedResponseDto.class))
	)
	@PatchMapping("/{complaintId}/resolved-vote")
	@RequireAuth
	public ResponseEntity<Map<String, Object>> voteResolved(
		@PathVariable Long complaintId,
		@Parameter(hidden = true) @CurrentMemberId Long memberId
	) {
		VoteResolvedResponseDto responseDto = complaintService.handleResolvedVote(memberId, complaintId);
		return responseTemplate.success(responseDto, HttpStatus.OK);
	}

	/**
	 * 민원 공감 상태 업데이트 API
	 */
	@Operation(
		summary = "민원 공감 상태 업데이트",
		description = "특정 민원에 대한 내 공감 상태를 설정합니다. reacted=true면 공감 추가, false면 공감 취소(멱등)."
	)
	@ApiResponse(
		responseCode = "200",
		description = "공감 상태 반영 성공",
		content = @Content(schema = @Schema(implementation = ReactionDto.class))
	)
	@PatchMapping("/{complaintId}/reaction")
	@RequireAuth
	public ResponseEntity<Map<String, Object>> updateReaction(
		@PathVariable Long complaintId,
		@Parameter(hidden = true) @CurrentMemberId Long memberId,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "공감 상태",
			required = true,
			content = @Content(schema = @Schema(implementation = ReactionUpdateRequestDto.class))
		)
		@RequestBody ReactionUpdateRequestDto request
	) {
		ReactionDto responseDto = Boolean.TRUE.equals(request.getReacted())
			? complaintService.addReaction(memberId, complaintId)
			: complaintService.cancelReaction(memberId, complaintId);
		return responseTemplate.success(responseDto, HttpStatus.OK);
	}
}
