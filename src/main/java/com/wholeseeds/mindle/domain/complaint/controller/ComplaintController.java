package com.wholeseeds.mindle.domain.complaint.controller;

import static org.springframework.http.MediaType.*;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wholeseeds.mindle.common.annotation.CurrentMemberId;
import com.wholeseeds.mindle.common.annotation.RequireAuth;
import com.wholeseeds.mindle.common.util.ResponseTemplate;
import com.wholeseeds.mindle.domain.complaint.dto.CommentDto;
import com.wholeseeds.mindle.domain.complaint.dto.request.CommentRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.request.ComplaintListRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.request.SaveComplaintRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.ComplaintDetailResponseDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.ComplaintListResponseDto;
import com.wholeseeds.mindle.domain.complaint.dto.response.SaveComplaintResponseDto;
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
}
