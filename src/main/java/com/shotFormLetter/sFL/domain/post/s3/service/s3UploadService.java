package com.shotFormLetter.sFL.domain.post.s3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class s3UploadService {



    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${s3.base-url}")
    private String s3baseUrl;



    private final S3Client s3Client;

    public String uploadImage(MultipartFile imageFile, String key) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try {
            RequestBody requestBody = RequestBody.fromInputStream(imageFile.getInputStream(), imageFile.getSize());
            PutObjectResponse response = s3Client.putObject(request, requestBody);
            String imageUrl = generateImageUrl(bucketName, key);
            return imageUrl;
        } catch (IOException e) {
            throw new IllegalStateException("이미지 전송 실패", e);
        }
    }

    private String generateImageUrl(String bucketName, String key) {
        // S3에서 업로드된 파일의 주소 생성
        String imageUrl = "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + key;
        return imageUrl;
    }

    public List<String> getUrls(List<MultipartFile> newImageList, String userId, List<String> s3Urls, String postId) {
        for (MultipartFile file : newImageList) {
            String key =userId + "/" + postId +"/images/" + file.getOriginalFilename();
            try {
                String imageUrl = uploadImage(file,key);
                s3Urls.add(imageUrl);
            } catch (Exception e) {
                throw new IllegalStateException("이미지 전송 실패");
            }
        }
        return s3Urls;
    }
    public List<String> updategetUrls(List<MultipartFile> newImageList, String userId, List<String> s3Urls, String postId) {
        for (MultipartFile file : newImageList) {
            String key =userId + "/" + postId +"/images/" + file.getOriginalFilename();
            try {
                String imageUrl = uploadImage(file,key);
                s3Urls.add(imageUrl);
            } catch (Exception e) {
                throw new IllegalStateException("이미지 전송 실패");
            }
        }
        return s3Urls;
    }

    public void uploadThumbnail(List<MultipartFile> newthumbnailList, String userId, List<String> s3Urls,String postId) {
        for (MultipartFile file : newthumbnailList) {
            String key =userId + "/" + postId +"/thumbnail/" +file.getOriginalFilename();
            try {
                String imageUrl = uploadImage(file,key);
            } catch (Exception e) {
                throw new IllegalStateException("이미지 전송 실패");
            }
        }
    }
    public void updateThumbnail(List<MultipartFile> newthumbnailList, String userId, List<String> s3Urls,String postId) {
        for (MultipartFile file : newthumbnailList) {
            String key =userId + "/" + postId +"/thumbnail/" +file.getOriginalFilename();
            try {
                String imageUrl = uploadImage(file,key);
            } catch (Exception e) {
                throw new IllegalStateException("이미지 전송 실패");
            }
        }
    }



    public void deleteaction(String userId,String postId){
        String key= userId+ "/" + postId;
        deleteImage(key);
    }
    public void deleteList(List<String> s3urls) {
        for(String urls : s3urls){
            String imageurls = urls.substring(s3baseUrl.length());
            String extension = imageurls.substring(imageurls.lastIndexOf('.'));
            String thumbnailUrls = imageurls.replace("images", "thumbnail");
            String deletethumbnail = thumbnailUrls.replace(extension, ".jpg");
            deleteImage(imageurls);
            deleteImage(deletethumbnail);
        }
    }

    public void deleteImage(String urls){
        String objectKey = urls;
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        s3Client.deleteObject(deleteRequest);
    }

}


