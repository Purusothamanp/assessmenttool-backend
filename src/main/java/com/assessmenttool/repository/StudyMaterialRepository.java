package com.assessmenttool.repository;

import com.assessmenttool.model.StudyMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {
    
    @Query("SELECT new com.assessmenttool.model.StudyMaterial(m.id, m.title, m.description, m.fileName, m.fileType, m.uploadedBy, m.uploadDate) FROM StudyMaterial m ORDER BY m.uploadDate DESC")
    List<StudyMaterial> findAllWithoutData();
}
