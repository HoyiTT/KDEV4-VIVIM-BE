package com.welcommu.modulerepository.projectprogress;

import com.welcommu.moduledomain.project.Project;
import com.welcommu.moduledomain.projectprogress.ProjectProgress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectProgressRepository extends JpaRepository<ProjectProgress, Long> {

     @Query("select max(p.position) from ProjectProgress p where p.project.id = :projectId")
     Optional<Float> findMaxPositionByProjectId(@Param("projectId") Long projectId);
     List<ProjectProgress> findByProject(Project project);
}
