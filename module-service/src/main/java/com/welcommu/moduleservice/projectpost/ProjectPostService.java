package com.welcommu.moduleservice.projectpost;

import com.welcommu.modulecommon.exception.CustomErrorCode;
import com.welcommu.modulecommon.exception.CustomException;
import com.welcommu.moduledomain.projectpost.ProjectPost;
import com.welcommu.moduledomain.user.User;
import com.welcommu.modulerepository.projectpost.ProjectPostRepository;
import com.welcommu.moduleservice.projectpost.audit.ProjectPostAuditService;
import com.welcommu.moduleservice.projectpost.dto.ProjectPostDetailResponse;
import com.welcommu.moduleservice.projectpost.dto.ProjectPostListResponse;
import com.welcommu.moduleservice.projectpost.dto.ProjectPostRequest;
import com.welcommu.moduleservice.projectpost.dto.ProjectPostSnapshot;
import com.welcommu.moduleservice.user.dto.UserSnapshot;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectPostService {

    private final ProjectPostRepository projectPostRepository;
    private final ProjectPostAuditService projectPostAuditService;

    @Transactional
    public Long createPost(User user, Long projectId, ProjectPostRequest request, String clientIp, Long creatorId) {
        ProjectPost newPost = request.toEntity(user, projectId, request, clientIp);

        ProjectPost savedPost = projectPostRepository.save(newPost);

        projectPostAuditService.createAuditLog(ProjectPostSnapshot.from(savedPost), creatorId);
        return savedPost.getId();
    }

    @Transactional
    public void modifyPost(Long projectId, Long postId, ProjectPostRequest request, Long modifierId) {

        ProjectPost existingPost = projectPostRepository.findById(postId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_POST));
        ProjectPostSnapshot beforeSnapshot = ProjectPostSnapshot.from(existingPost);

        existingPost.setTitle(request.getTitle());
        existingPost.setContent(request.getContent());
        existingPost.setProjectPostStatus(request.getProjectPostStatus());
        existingPost.setModifiedAt();

        ProjectPostSnapshot afterSnapshot = ProjectPostSnapshot.from(existingPost);

        projectPostAuditService.modifyAuditLog(beforeSnapshot, afterSnapshot, modifierId);
    }


    public List<ProjectPostListResponse> getPostList(Long projectId) {
        List<ProjectPost> posts = projectPostRepository.findAllByProjectIdAndDeletedAtIsNull(
            projectId);
        return posts.stream()
            .map(ProjectPostListResponse::from)
            .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public ProjectPostDetailResponse getPostDetail(Long projectId, Long postId) {
        ProjectPost existingPost = projectPostRepository.findById(postId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_POST));

        return ProjectPostDetailResponse.from(existingPost);
    }

    @Transactional
    public void deletePost(Long projectId, Long postId, Long deleterId) {
        ProjectPost existingPost = projectPostRepository.findById(postId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_POST));
        existingPost.setDeletedAt();
        projectPostAuditService.deleteAuditLog(ProjectPostSnapshot.from(existingPost),deleterId);
    }
}
