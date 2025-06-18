package com.example.digital_wardrobe.service;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    
    @Value("${cloud.aws.region.static}")
    private String region;

    public S3Service(@Value("${cloud.aws.credentials.accessKey}") String accessKey,
                     @Value("${cloud.aws.credentials.secretKey}") String secretKey,
                     @Value("${cloud.aws.region.static}") String region) {

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }

    public String uploadFile(File file) {
        String fileName = UUID.randomUUID() + "_" + file.getName();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));

        String uploadedUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;

        System.out.println("✅ S3 Upload Complete: " + uploadedUrl);
        return uploadedUrl;
    }
    
    public void deleteFile(String imageUrl) {
        // https://bucket-name.s3.amazonaws.com/파일명 → 파일명만 추출
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .build();

        s3Client.deleteObject(deleteRequest);
    }
    
    public File convertMultiPartToFile(MultipartFile multipartFile) throws IOException {
        String safeFileName = UUID.randomUUID() + "_" +
                (multipartFile.getOriginalFilename() != null ? multipartFile.getOriginalFilename() : "tempfile");

        File convFile = Paths.get(System.getProperty("java.io.tmpdir"), safeFileName).toFile();

        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(multipartFile.getBytes());
        }

        System.out.println("✅ Temp File Path: " + convFile.getAbsolutePath());
        return convFile;
    }


}

