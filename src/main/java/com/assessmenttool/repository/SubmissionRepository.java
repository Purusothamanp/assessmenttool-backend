package com.assessmenttool.repository;

import com.assessmenttool.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, String> {
    List<Submission> findByStudentName(String studentName);
    List<Submission> findByAssessmentId(String assessmentId);
    boolean existsByStudentNameIgnoreCaseAndAssessmentId(String studentName, String assessmentId);
    boolean existsByStudentNameAndAssessmentId(String studentName, String assessmentId);
    List<Submission> findByStudentNameIgnoreCaseAndAssessmentId(String studentName, String assessmentId);
}
