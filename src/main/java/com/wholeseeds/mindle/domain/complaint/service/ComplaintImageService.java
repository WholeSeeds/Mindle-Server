package com.wholeseeds.mindle.domain.complaint.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wholeseeds.mindle.common.util.ObjectUtils;
import com.wholeseeds.mindle.domain.complaint.entity.Complaint;
import com.wholeseeds.mindle.domain.complaint.entity.ComplaintImage;
import com.wholeseeds.mindle.domain.complaint.exception.ImageUploadLimitExceeded;
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
	private static final int MAX_IMAGES = 3;

	/**
	 * 민원 이미지 저장
	 * @param complaint 민원 객체
	 * @param imageList 이미지 파일 목록
	 */
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


	@Transactional
	public void replaceImages(Complaint complaint, List<MultipartFile> newImages) {
		List<ComplaintImage> active = findActiveImages(complaint.getId());
		for (ComplaintImage img : active) {
			img.softDelete();
			complaintImageRepository.save(img);
			// TODO: 필요 시 NCP 실제 파일 삭제 호출
		}

		if (!ObjectUtils.objectIsNullOrEmpty(newImages)) {
			if (newImages.size() > MAX_IMAGES) {
				throw new ImageUploadLimitExceeded();
			}
			saveComplaintImages(complaint, newImages);
		}
	}

	/** 추가: 기존 활성 + 신규 ≤ MAX 검증 후 저장 */
	@Transactional
	public void appendImages(Complaint complaint, List<MultipartFile> newImages) {
		if (ObjectUtils.objectIsNullOrEmpty(newImages)) {
			return;
		}

		int activeCount = findActiveImages(complaint.getId()).size();
		if (activeCount + newImages.size() > MAX_IMAGES) {
			throw new ImageUploadLimitExceeded();
		}

		saveComplaintImages(complaint, newImages);
	}

	/** 모든 활성 이미지를 soft delete 처리한다. */
	@Transactional
	public void softDeleteAllForComplaint(Complaint complaint) {
		List<ComplaintImage> all = complaintImageRepository.findAllByComplaintId(complaint.getId());
		for (ComplaintImage img : all) {
			if (!img.isDeleted()) {
				img.softDelete();
				complaintImageRepository.save(img);
				// TODO: 필요 시 NCP 실제 파일 삭제 호출
			}
		}
	}

	private List<ComplaintImage> findActiveImages(Long complaintId) {
		return complaintImageRepository.findAllByComplaintId(complaintId).stream()
			.filter(img -> !img.isDeleted())
			.toList();
	}
}
