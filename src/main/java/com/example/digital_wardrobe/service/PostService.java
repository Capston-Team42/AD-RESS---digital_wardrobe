package com.example.digital_wardrobe.service;

import com.example.digital_wardrobe.model.Post;
import com.example.digital_wardrobe.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final S3Service s3Service;

    /**
     * 글 작성 (사진 포함), 여러장 가능
     */
    public Post createPostWithImages(List<MultipartFile> imageFiles, Post postRequest) {
        List<String> photoUrls = new ArrayList<>();

        if (imageFiles != null && !imageFiles.isEmpty()) {
            System.out.println("✅ 이미지 파일 개수: " + imageFiles.size());

            for (MultipartFile file : imageFiles) {
                try {
                    // 파일 변환
                    File convertedFile = s3Service.convertMultiPartToFile(file);

                    System.out.println("✅ Temp File Created: " + convertedFile.getAbsolutePath());
                    System.out.println("✅ Temp File Exists: " + convertedFile.exists());

                    // S3 업로드
                    String uploadedUrl = s3Service.uploadFile(convertedFile);

                    System.out.println("✅ S3 Upload Success: " + uploadedUrl);

                    photoUrls.add(uploadedUrl);

                    // 임시 파일 삭제
                    boolean deleted = convertedFile.delete();
                    System.out.println("✅ Temp File Deleted: " + deleted);

                } catch (IOException e) {
                    throw new RuntimeException("파일 업로드 중 오류 발생: " + e.getMessage(), e);
                }
            }
        } else {
            System.out.println("⚠️ 이미지 파일이 없습니다.");
        }

        postRequest.setPhotoUrls(photoUrls);
        System.out.println("✅ 최종 등록되는 photoUrls: " + photoUrls);

        return postRepository.save(postRequest);
    }




    /**
     * 글 목록 조회 (정렬 + 페이징)
     */
    public Page<Post> getPosts(String sortType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if ("latest".equals(sortType)) {
            return postRepository.findAllByOrderByCreatedAtDesc(pageable);
        } else if ("popular".equals(sortType)) {
            return postRepository.findAllByOrderByLikeCountDesc(pageable);
        } else {
            return postRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
    }

    /**
     * 글 상세 조회
     */
    public Optional<Post> getPostById(String postId) {
        return postRepository.findById(postId);
    }

    /**
     * 글 수정 (사진 포함, 작성자만 가능)
     */
    public Optional<Post> updatePostWithImages(String postId, String requesterId, List<MultipartFile> newImageFiles, List<String> keepImageUrls, Post updatedPostData) {
        return postRepository.findById(postId).map(post -> {
            try {
                System.out.println("📄 게시글 작성자(post.getUsername()): " + post.getUsername());
                System.out.println("📄 요청자(requesterId): " + requesterId);

                // ✅ 작성자 체크
                if (!post.getUsername().equals(requesterId)) {
                    System.out.println("❌ 작성자 불일치 → 수정 권한 없음");
                    throw new RuntimeException("수정 권한이 없습니다.");
                }
                System.out.println("✅ 작성자 일치 → 수정 가능");

                // ✅ Null-safe keepImageUrls 처리 (null이면 기존 이미지 다 유지)
                List<String> safeKeepImageUrls = keepImageUrls != null ? keepImageUrls : new ArrayList<>(post.getPhotoUrls());
                System.out.println("📂 유지할 기존 이미지 목록: " + safeKeepImageUrls);

                // ✅ 기존 이미지 중 삭제할 이미지들 S3에서 삭제
                List<String> oldImageUrls = post.getPhotoUrls();
                if (oldImageUrls != null) {
                    for (String oldUrl : oldImageUrls) {
                        if (!safeKeepImageUrls.contains(oldUrl)) {
                            System.out.println("🗑️ 삭제할 이미지: " + oldUrl);
                            s3Service.deleteFile(oldUrl);
                        } else {
                            System.out.println("✅ 유지할 이미지: " + oldUrl);
                        }
                    }
                }

                // ✅ 새로 업로드된 이미지들 S3에 올리기
                List<String> newUploadedUrls = new ArrayList<>();
                if (newImageFiles != null) {
                    for (MultipartFile file : newImageFiles) {
                        System.out.println("📤 새 이미지 업로드 시작: " + file.getOriginalFilename());
                        File convertedFile = s3Service.convertMultiPartToFile(file);
                        String uploadedUrl = s3Service.uploadFile(convertedFile);
                        newUploadedUrls.add(uploadedUrl);
                        System.out.println("✅ 업로드 완료 URL: " + uploadedUrl);
                        convertedFile.delete();  // temp 파일 삭제
                    }
                }

                // ✅ 최종적으로 남길 이미지 리스트 구성 (남긴 것 + 새로 올린 것)
                List<String> finalImageUrls = new ArrayList<>(safeKeepImageUrls);
                finalImageUrls.addAll(newUploadedUrls);
                System.out.println("📦 최종 이미지 리스트: " + finalImageUrls);

                // ✅ post 수정
                post.setPhotoUrls(finalImageUrls);
                post.setContent(updatedPostData.getContent());
                post.setHashtags(updatedPostData.getHashtags());

                Post savedPost = postRepository.save(post);
                System.out.println("✅ 게시글 수정 완료: " + savedPost.getPostId());

                return savedPost;

            } catch (IOException e) {
                System.out.println("❗ 파일 업로드 중 예외 발생: " + e.getMessage());
                throw new RuntimeException("Post 수정 중 오류 발생: " + e.getMessage());
            }
        });
    }


    /**
     * 글 삭제 (작성자만 가능, 여러 이미지 삭제)
     */
    public boolean deletePost(String postId, String requesterId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (post.getUsername().equals(requesterId)) {
                // ✅ 글 삭제 전에 S3 이미지들 삭제
                List<String> imageUrls = post.getPhotoUrls();
                if (imageUrls != null && !imageUrls.isEmpty()) {
                    for (String imageUrl : imageUrls) {
                        s3Service.deleteFile(imageUrl);
                    }
                }

                // ✅ MongoDB에서 글 삭제
                postRepository.deleteById(postId);

                return true;
            }
        }
        return false;
    }
}
