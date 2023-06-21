package com.shotFormLetter.sFL.domain.post.s3.service;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.PutObjectResponse;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class s3Service {
    private final S3Client s3Client;


    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;



    public String uploadImage(File imageFile, String key) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        PutObjectResponse response = s3Client.putObject(request, imageFile.toPath());

        // 업로드된 파일의 주소 생성
        String imageUrl = generateImageUrl(bucketName, key);
        return imageUrl;
    }


    private String generateImageUrl(String bucketName, String key) {
        // S3에서 업로드된 파일의 주소 생성
        String imageUrl = "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + key;
        return imageUrl;
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(multipartFile.getBytes());
        }
        return file;
    }
    public List<String> getUrls(List<MultipartFile> newImageList, String userId, List<String> s3Urls,String postId) {
        for (MultipartFile file : newImageList) {
            String uuidFile = getFileExtensionAndGenerateUniqueFileName(file.getOriginalFilename());
            String key =userId + "/" + postId +"/images/" + uuidFile;
            try {
                String imageUrl = uploadImage(convertMultipartFileToFile(file),key);
                s3Urls.add(imageUrl);
            } catch (Exception e) {
                throw new IllegalStateException("이미지 전송 실패");
            }
        }
        return s3Urls;
    }
    public void uploadThumbnail(List<MultipartFile> newImageList, String userId, List<String> s3Urls,String postId) {
        for (MultipartFile file : newImageList) {
            String uuidFile = getFileExtensionAndGenerateUniqueFileName(file.getOriginalFilename());
            String key =userId + "/" + postId +"/thumbnail/" + uuidFile;
            try {
                String imageUrl = uploadImage(convertMultipartFileToFile(file),key);
                s3Urls.add(imageUrl);
            } catch (Exception e) {
                throw new IllegalStateException("이미지 전송 실패");
            }
        }
    }

    private String getFileExtensionAndGenerateUniqueFileName(String filename) {
        String fileExtension = filename.substring(filename.lastIndexOf("."));
        UUID uuid = UUID.randomUUID();
        return uuid.toString() + fileExtension;
    }
}
