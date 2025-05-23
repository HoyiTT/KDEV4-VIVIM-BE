package com.welcommu.moduledomain.projectUser;

import com.welcommu.moduledomain.project.Project;
import com.welcommu.moduledomain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "projects_users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "project_id"})
    }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "manage_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectUserManageRole projectUserManageRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private Project project;
}
