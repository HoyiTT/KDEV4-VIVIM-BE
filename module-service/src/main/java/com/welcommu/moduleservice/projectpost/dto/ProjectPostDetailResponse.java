package com.welcommu.moduleservice.projectpost.dto;

import com.welcommu.moduledomain.projectpost.ProjectPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPostDetailResponse {
    private Long postId;
    private String title;
    private String content;
    private Long parentId;
    private LocalDateTime createdAt;
    private String creatorName;
    private String creatorRole;
    private LocalDateTime modifiedAt;


    public static ProjectPostDetailResponse from(ProjectPost post) {
        return new ProjectPostDetailResponse(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            post.getParentId(),
            post.getCreatedAt(),
            post.getCreatorName(),
            post.getCreatorRole(),
            post.getModifiedAt()
        );
    }
}
