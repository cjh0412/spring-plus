package org.example.expert.domain.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.s3.region}")
    private String region;

    public String fileUpload(MultipartFile multipartFile, String dirName) throws IOException {
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .build(),
                RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize())
        );

        return "https://" + bucket + ".s3."+region+".amazonaws.com/" + fileName;
    }
}