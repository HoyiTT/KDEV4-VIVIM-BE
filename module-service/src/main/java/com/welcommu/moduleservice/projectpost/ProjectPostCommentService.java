package com.welcommu.moduleservice.projectpost;

import com.welcommu.modulecommon.exception.CustomErrorCode;
import com.welcommu.modulecommon.exception.CustomException;
import com.welcommu.moduledomain.projectpost.ProjectPostComment;
import com.welcommu.moduledomain.user.User;
import com.welcommu.modulerepository.projectpost.ProjectPostCommentRepository;
import com.welcommu.moduleservice.projectpost.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectPostCommentService {
    private final ProjectPostCommentRepository projectPostCommentRepository;

    @Transactional
    public void createComment(User user, Long postId, ProjectPostCommentRequest request, String clientIp) {
        ProjectPostComment newComment= request.toEntity(user, postId, request, clientIp);
        projectPostCommentRepository.save(newComment);
    }

    @Transactional
    public void modifyComment(Long postId, Long commentId, ProjectPostCommentRequest request) {

        ProjectPostComment existingComment = projectPostCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_COMMENT));

        existingComment.setContent(request.getContent());
        existingComment.setModifiedAt();
    }

    @Transactional(readOnly = true)
    public List<ProjectPostCommentListResponse> getCommentList(Long projectPostId) {
        List<ProjectPostComment> comments = projectPostCommentRepository.findAllByProjectPostIdAndDeletedAtIsNull(projectPostId);
        return comments.stream()
                .map(ProjectPostCommentListResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        ProjectPostComment existingComment = projectPostCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_COMMENT));
        existingComment.setDeletedAt();
    }
}
