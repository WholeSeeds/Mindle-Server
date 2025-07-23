package com.wholeseeds.mindle.domain.complaint.controller;

import static org.springframework.http.MediaType.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wholeseeds.mindle.common.response.ApiResponse;
import com.wholeseeds.mindle.common.util.ObjectUtils;
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
import com.wholeseeds.mindle.domain.complaint.exception.ImageUploadLimitExceeded;
import com.wholeseeds.mindle.domain.complaint.mapper.ComplaintMapper;
import com.wholeseeds.mindle.domain.complaint.service.ComplaintService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/complaint")
@RequiredArgsConstructor
@Slf4j
public class ComplaintController {
	private final ComplaintService complaintService;
	private final ComplaintMapper complaintMapper;

	// swagger 이미지업로드를 위한 Operation
	@PostMapping(
		value = "/save",
		consumes = MULTIPART_FORM_DATA_VALUE
	)
	public ResponseEntity<ApiResponse<SaveComplaintResponseDto>> saveComplaint(
		@io.swagger.v3.oas.annotations.Parameter(
			name = "meta",
			description = "민원 메타 정보 (JSON)",
			required = true,
			content = @io.swagger.v3.oas.annotations.media.Content(
				mediaType = APPLICATION_JSON_VALUE,
				schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SaveComplaintRequestDto.class)
			)
		)
		@RequestPart("meta")
		SaveComplaintRequestDto requestDto,
		@RequestPart(value = "files", required = false) List<MultipartFile> imageList
	) {

		if (!ObjectUtils.objectIsNullOrEmpty(imageList) && imageList.size() > 3) {
			throw new ImageUploadLimitExceeded();
		}
		log.info("Request : {}", requestDto);
		if (!ObjectUtils.objectIsNullOrEmpty(imageList)) {
			for (MultipartFile image : imageList) {
				log.info("파일명 : {}", image.getOriginalFilename());
			}
		}

		Complaint saved = complaintService.saveComplaint(requestDto, imageList);

		SaveComplaintResponseDto resDto = complaintMapper.toSaveComplaintResponseDto(saved);
		return ResponseEntity.ok(ApiResponse.ok(resDto));
	}

	@GetMapping("/detail/{complaintId}")
	public ResponseEntity<ApiResponse<ComplaintDetailResponseDto>> getComplaintDetail(
		@PathVariable Long complaintId) {
		ComplaintDetailWithImagesDto complaint = complaintService.getComplaintDetail(complaintId);
		ReactionDto reactionDto = complaintService.getComplaintReaction(complaintId, 1L);

		ComplaintDetailResponseDto responseDto = ComplaintDetailResponseDto.builder()
			.complaintDetailWithImagesDto(complaint)
			.reactionDto(reactionDto)
			.build();
		return ResponseEntity.ok(ApiResponse.ok(responseDto));
	}

	@GetMapping("/detail/comment")
	public ResponseEntity<ApiResponse<List<CommentDto>>> getComplaintComments(
		@ModelAttribute CommentRequestDto requestDto) {
		List<CommentDto> comments = complaintService.getComplaintComments(requestDto);
		return ResponseEntity.ok(ApiResponse.ok(comments));
	}

	@GetMapping("/list")
	public ResponseEntity<ApiResponse<List<ComplaintListResponseDto>>> getComplaintList(
		@ModelAttribute ComplaintListRequestDto requestDto) {
		List<ComplaintListResponseDto> complaintList = complaintService.getComplaintList(requestDto);
		return ResponseEntity.ok(ApiResponse.ok(complaintList));
	}

}
