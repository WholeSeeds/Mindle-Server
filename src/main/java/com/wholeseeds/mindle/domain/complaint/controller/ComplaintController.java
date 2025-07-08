package com.wholeseeds.mindle.domain.complaint.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wholeseeds.mindle.common.util.ObjectUtils;
import com.wholeseeds.mindle.domain.complaint.service.ComplaintService;
import com.wholeseeds.mindle.domain.complaint.dto.SaveComplaintRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.SaveComplaintResponseDto;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.complaint.exception.ImageUploadLimitExceeded;
import com.wholeseeds.mindle.domain.complaint.mapper.ComplaintMapper;
import com.wholeseeds.mindle.common.util.RequestLogger;
import com.wholeseeds.mindle.common.util.ResponseTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/complaint")
@RequiredArgsConstructor
@Slf4j
public class ComplaintController {
	private final ComplaintService complaintService;
	private final ComplaintMapper complaintMapper;
	private final ResponseTemplate responseTemplate;

	@PostMapping(
		value = "/save",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public ResponseEntity<Map<String, Object>> saveComplaint(
		@RequestPart("meta") SaveComplaintRequestDto requestDto,
		@RequestPart(value = "image", required = false) List<MultipartFile> imageList
	) {
		RequestLogger.body(requestDto);

		if (imageList.size() > 3) {
			throw new ImageUploadLimitExceeded();
		}

		if (!ObjectUtils.objectIsNullOrEmpty(imageList)) {
			for (MultipartFile image : imageList) {
				log.info("파일명 : {}", image.getOriginalFilename());
			}
		}

		Complaint saved = complaintService.saveComplaint(requestDto, imageList);

		SaveComplaintResponseDto resDto = complaintMapper.toSaveComplaintResponseDto(saved);
		return (responseTemplate.success(resDto, HttpStatus.OK));
	}
}
