package com.wholeseeds.mindle.domain.complaint.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wholeseeds.mindle.domain.complaint.Service.ComplaintService;
import com.wholeseeds.mindle.domain.complaint.dto.SaveComplaintRequestDto;
import com.wholeseeds.mindle.domain.complaint.dto.SaveComplaintResponseDto;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/complaint")
@RequiredArgsConstructor
@Slf4j
public class ComplaintController {
	private final ComplaintService complaintService;

	@PostMapping("/save")
	public ResponseEntity<SaveComplaintResponseDto> saveComplaint(@RequestBody SaveComplaintRequestDto requestDto) {
		log.info("Request : {}", requestDto.toString());
		Complaint res = complaintService.saveComplaint(requestDto);
		log.info("저장 완료 결과: {}", res.getTitle());
		SaveComplaintResponseDto resDto = SaveComplaintResponseDto.builder()
			.complaintId(res.getId())
			.title(res.getTitle())
			.build();
		return ResponseEntity.ok(resDto);
	}
}
