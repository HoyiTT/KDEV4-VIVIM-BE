package com.welcommu.moduledomain.project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "projects")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "current_progress")
    private String currentProgress;

    @Column(name = "project_fee")
    private Integer projectFee;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_status")
    private ProjectStatus projectStatus;

    @Column(name = "project_fee_paid_date")
    private LocalDate projectFeePaidDate;

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public void setProjectFee(Integer projectFee) {
        this.projectFee = projectFee;
    }

    public void setCurrentProgress(String currentProgress) {
        this.currentProgress = currentProgress;
    }

    public void setProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    public void setProjectFeePaidDate() {
        this.projectFeePaidDate = LocalDate.now();
    }


}
