package com.wholeseeds.mindle.domain.complaint.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wholeseeds.mindle.common.util.ObjectUtils;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.complaint.entity.ComplaintImage;
import com.wholeseeds.mindle.domain.complaint.repository.ComplaintImageRepository;
import com.wholeseeds.mindle.infra.service.NcpObjectStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplaintImageService {
	private final ComplaintImageRepository complaintImageRepository;
	private final NcpObjectStorageService ncpObjectStorageService;

	private static final String COMPLAINT_IMAGE_FOLDER = "complaint";

	/**
	 * 민원 이미지 저장
	 * @param complaint 민원 객체
	 * @param imageList 이미지 파일 목록
	 */
	@Transactional
	public void saveComplaintImages(Complaint complaint, List<MultipartFile> imageList) {
		if (ObjectUtils.objectIsNullOrEmpty(imageList)) {
			return;
		}

		for (MultipartFile imageFile : imageList) {
			String imageUrl = ncpObjectStorageService.uploadFile(COMPLAINT_IMAGE_FOLDER, imageFile);
			ComplaintImage complaintImage = ComplaintImage.builder()
				.complaint(complaint)
				.imageUrl(imageUrl)
				.build();
			complaintImageRepository.save(complaintImage);
		}
	}
}
