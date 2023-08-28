package com.shotFormLetter.sFL.domain.post.s3.service;

import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.post.domain.dto.request.DeletePostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

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

    public String CopyImage(String imageUrl, String key){
        URL originalImageUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(imageUrl).build());
        // 이미지 이동
        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .copySource(bucketName+imageUrl)
                .destinationBucket(bucketName)
                .destinationKey(key)
                .build();
        s3Client.copyObject(copyRequest);

        String getUrl= "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + key;
        return getUrl;
    }
    private String generateImageUrl(String bucketName, String key) {
        // S3에서 업로드된 파일의 주소 생성
        String imageUrl = "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + key;
        return imageUrl;
    }

    public List<String> getUrls(List<MultipartFile> newImageList,  List<String> s3Urls, String postId,String id) {
        for (MultipartFile file : newImageList) {
            String key =id + "/" + postId +"/images/" + file.getOriginalFilename();
            try {
                String imageUrl = uploadImage(file,key);
                s3Urls.add(imageUrl);
            } catch (Exception e) {
                throw new IllegalStateException("이미지 전송 실패");
            }
        }
        return s3Urls;
    }
    public List<String> updategetUrls(List<MultipartFile> newImageList, List<String> s3Urls, String postId,String id) {
        for (MultipartFile file : newImageList) {
            String key =id + "/" + postId +"/images/" + file.getOriginalFilename();
            try {
                String imageUrl = uploadImage(file,key);
                s3Urls.add(imageUrl);
            } catch (Exception e) {
                throw new IllegalStateException("이미지 전송 실패");
            }
        }
        return s3Urls;
    }

    public void uploadThumbnail(List<MultipartFile> newthumbnailList,String postId,String id) {
        for (MultipartFile file : newthumbnailList) {
            String key =id + "/" + postId +"/thumbnail/" +file.getOriginalFilename();
            try {
                String imageUrl = uploadImage(file,key);
            } catch (Exception e) {
                throw new IllegalStateException("이미지 전송 실패");
            }
        }
    }
    public void updateThumbnail(List<MultipartFile> newthumbnailList,String postId,String id) {
        for (MultipartFile file : newthumbnailList) {
            String key =id + "/" + postId +"/thumbnail/" +file.getOriginalFilename();
            try {
                String imageUrl = uploadImage(file,key);
            } catch (Exception e) {
                throw new IllegalStateException("이미지 전송 실패");
            }
        }
    }

    public String uploadMusic(MultipartFile musicFile){
        String musicUrl="";
        String key="music/" + musicFile.getOriginalFilename();
        try {
             musicUrl = uploadImage(musicFile,key);
        } catch (Exception e) {
            throw new IllegalStateException("이미지 전송 실패");
        }
        return musicUrl;
    }

    public String uploadProfile(MultipartFile musicFile, Member member){
        System.out.println(musicFile.getOriginalFilename()+"이름");
        if (musicFile==null){
            return null;
        }
        String key=member.getId().toString()+"/profile/"+ musicFile.getOriginalFilename();
        String userProfile = uploadImage(musicFile,key);
//        try {
//            userProfile = uploadImage(musicFile,key);
//        } catch (Exception e) {
//            throw new IllegalStateException("이미지 전송 실패");
//        }
        return userProfile;
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
            String deletethumbnail = thumbnailUrls.replace(extension, ".JPG");
            deleteImage(imageurls);
            deleteImage(deletethumbnail);
        }
    }

    public void deleteOneImage(String s3urls){
        String imageurls = s3urls.substring(s3baseUrl.length());
        String extension = imageurls.substring(imageurls.lastIndexOf('.'));
        String thumbnailUrls = imageurls.replace("images", "thumbnail");
        String deletethumbnail = thumbnailUrls.replace(extension, ".JPG");
        deleteImage(imageurls);
        deleteImage(deletethumbnail);
    }


    public void deleteImage(String urls){
        String objectKey = urls;
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        s3Client.deleteObject(deleteRequest);
    }

    public void deleteUserImage(String profile){
        String getDeleteKey=profile.substring(s3baseUrl.length());
        deleteImage(getDeleteKey);
    }

    public void test(DeletePostDto deletePostDto){
        String deleteKey=deletePostDto.getUserSeq().toString() + "/" + deletePostDto.getPostId().toString();
        deleteImage(deleteKey);
    }
}


