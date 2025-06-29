package com.wholeseeds.mindle.domain.complaint.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wholeseeds.mindle.common.response.ApiResponse;
import com.wholeseeds.mindle.domain.complaint.Service.ComplaintService;
import com.wholeseeds.mindle.domain.complaint.dto.SaveComplaintRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.SaveComplaintResponseDto;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.complaint.mapper.ComplaintMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/complaint")
@RequiredArgsConstructor
@Slf4j
public class ComplaintController {
	private final ComplaintService complaintService;
	private final ComplaintMapper complaintMapper;

	@PostMapping(
		value = "/save",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public ResponseEntity<ApiResponse<SaveComplaintResponseDto>> saveComplaint(
		@RequestPart("meta") SaveComplaintRequestDto requestDto,
		@RequestPart("image") MultipartFile image) throws IOException {

		log.info("Request : {}", requestDto);
		log.info("파일명 : {}", image.getOriginalFilename());

		Complaint saved = complaintService.saveComplaint(requestDto, image);

		SaveComplaintResponseDto resDto = complaintMapper.toSaveComplaintResponseDto(saved);
		log.info("Response : {}\n 이미지 : {}", resDto, image);
		return ResponseEntity.ok(ApiResponse.ok(resDto));
	}
}
