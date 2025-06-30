package com.wholeseeds.mindle.common.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.wholeseeds.mindle.domain.complaint.exception.NcpFileUploadFailedException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NcpObjectStorageService {
	private final AmazonS3 ncp;

	@Value("${ncp.object.bucket-name}")
	private String bucketName;

	public String uploadFile(String directory, MultipartFile file) {
		String filename = file.getOriginalFilename(); // "example.jpg"
		String extension = "";
		if (filename != null && filename.contains(".")) {
			extension = filename.substring(filename.lastIndexOf('.'));
		}

		// 겹치지 않는 key 생성
		String key;
		do {
			key = String.format("%s/%s%s", directory, UUID.randomUUID(), extension);
		} while (ncp.doesObjectExist(bucketName, key));

		// 메타데이터 세팅
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentType(file.getContentType());
		meta.setContentLength(file.getSize());

		// 업로드
		try (InputStream in = file.getInputStream()) {
			PutObjectRequest req = new PutObjectRequest(bucketName, key, in, meta);
			ncp.putObject(req); // ncp 에 파일 업로드
		} catch (IOException e) {
			throw new NcpFileUploadFailedException();
		}

		// 최종 URL 반환
		return ncp.getUrl(bucketName, key).toString();
	}
}
