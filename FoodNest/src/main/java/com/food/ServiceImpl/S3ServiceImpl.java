package com.food.ServiceImpl;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.food.service.S3Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    public S3ServiceImpl(
            S3Client s3Client
    ) {
        this.s3Client = s3Client;
    }

    @Override
    public String uploadFile(
            MultipartFile file
    ) throws IOException {

        String fileName =
                UUID.randomUUID()
                + "-"
                + file.getOriginalFilename();

        PutObjectRequest putObjectRequest =
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .contentType(file.getContentType())
                        .build();

        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromBytes(
                        file.getBytes()
                )
        );

        return "https://"
                + bucketName
                + ".s3."
                + region
                + ".amazonaws.com/"
                + fileName;
    }
}