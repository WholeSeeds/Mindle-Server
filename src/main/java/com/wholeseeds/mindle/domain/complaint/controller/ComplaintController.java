package com.wholeseeds.mindle.domain.complaint.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wholeseeds.mindle.common.CommonCode;
import com.wholeseeds.mindle.common.response.ApiResponse;
import com.wholeseeds.mindle.domain.complaint.Service.ComplaintService;
import com.wholeseeds.mindle.domain.complaint.dto.SaveComplaintRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.SaveComplaintResponseDto;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.complaint.exception.ImageUploadLimitExceeded;
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
		@RequestPart(value = "image", required = false) List<MultipartFile> imageList) throws IOException {

		if (imageList.size() > 3) {
			throw new ImageUploadLimitExceeded();
		}

		log.info("Request : {}", requestDto);
		if (!CommonCode.objectIsNullOrEmpty(imageList)) {
			for (MultipartFile image : imageList) {
				log.info("파일명 : {}", image.getOriginalFilename());
			}
		}

		Complaint saved = complaintService.saveComplaint(requestDto, imageList);

		SaveComplaintResponseDto resDto = complaintMapper.toSaveComplaintResponseDto(saved);
		return ResponseEntity.ok(ApiResponse.ok(resDto));
	}
}
