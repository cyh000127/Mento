package com.mento.common.file.util;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.mento.common.config.properties.CloudflareProperties;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.FileStorageException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CloudflareStorageUtil {

	private final S3Client s3Client;
	private final CloudflareProperties cloudflareProperties;

	public String uploadFile(final MultipartFile file, final String directory) {
		String key = buildObjectKey(directory, file.getOriginalFilename());

		try {
			uploadToS3(file, key);
			String fileUrl = convertUrl(buildFileUrl(key));
			log.info("[CloudflareStorage] 파일 업로드 완료 {key: {}}", key);
			return fileUrl;

		} catch (S3Exception e) {
			log.error("[CloudflareStorage] S3 업로드 실패 key:{}, statusCode: {}, errorCode: {}, message: {}",
				key, e.statusCode(), e.awsErrorDetails().errorCode(), e.awsErrorDetails().errorMessage(), e);
			throw new FileStorageException(ErrorCode.FILE_UPLOAD_FAILED);
		} catch (IOException _) {
			log.error("[CloudflareStorage] 파일 읽기 실패 {filename: {}}", file.getOriginalFilename());
			throw new FileStorageException(ErrorCode.FILE_UPLOAD_FAILED);
		}
	}

	private void uploadToS3(final MultipartFile file, final String key) throws IOException {
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(cloudflareProperties.bucket())
			.key(key)
			.contentType(file.getContentType())
			.contentLength(file.getSize())
			.build();

		s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
	}

	private String buildObjectKey(final String directory, final String originalFilename) {
		String extension = extractExtension(originalFilename);
		String uniqueFilename = UUID.randomUUID() + extension;
		return directory + "/" + uniqueFilename;
	}

	private String extractExtension(final String filename) {
		if (filename == null || !filename.contains(".")) {
			return "";
		}
		return filename.substring(filename.lastIndexOf("."));
	}

	private String buildFileUrl(final String key) {
		return String.format("%s/%s/%s",
			cloudflareProperties.endpoint(),
			cloudflareProperties.bucket(),
			key
		);
	}

	private String convertUrl(String url) {
		String newPrefix = cloudflareProperties.outerPrefix();
		String keyword = "common";

		int index = url.indexOf(keyword);

		if (index != -1) {
			return newPrefix + url.substring(index);
		} else {
			return url;
		}
	}
}

