package com.assessmenttool.controller;

import com.assessmenttool.model.Submission;
import com.assessmenttool.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionRepository submissionRepository;

    @GetMapping
    public List<Submission> getSubmissions(
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) String assessmentId) {
        if (studentName != null && !studentName.isBlank()) {
            return submissionRepository.findByStudentName(studentName);
        }
        if (assessmentId != null && !assessmentId.isBlank()) {
            return submissionRepository.findByAssessmentId(assessmentId);
        }
        return submissionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Submission> getSubmissionById(@PathVariable String id) {
        return submissionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Submission> createSubmission(@RequestBody Map<String, Object> payload) {
        Submission submission = new Submission();
        String id = (String) payload.get("id");
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString().substring(0, 11);
        }
        submission.setId(id);
        submission.setStudentName((String) payload.get("studentName"));
        submission.setAssessmentId((String) payload.get("assessmentId"));
        submission.setAssessmentTitle((String) payload.get("assessmentTitle"));
        
        Object scoreObj = payload.get("score");
        if (scoreObj instanceof Number) {
            submission.setScore(((Number) scoreObj).intValue());
        }
        
        submission.setStatus((String) payload.get("status"));
        submission.setDate((String) payload.get("date"));

        if (payload.containsKey("answers")) {
            Object answers = payload.get("answers");
            if (answers instanceof Map) {
                submission.setAnswers((Map<String, Object>) answers);
            }
        }

        Submission saved = submissionRepository.save(submission);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Submission> patchSubmission(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        return submissionRepository.findById(id).map(submission -> {
            if (updates.containsKey("score")) {
                Object scoreObj = updates.get("score");
                if (scoreObj instanceof Number) {
                    submission.setScore(((Number) scoreObj).intValue());
                }
            }
            if (updates.containsKey("status")) {
                submission.setStatus((String) updates.get("status"));
            }
            if (updates.containsKey("date")) {
                submission.setDate((String) updates.get("date"));
            }
            if (updates.containsKey("answers")) {
                Object answers = updates.get("answers");
                if (answers instanceof Map) {
                    submission.setAnswers((Map<String, Object>) answers);
                }
            }
            return ResponseEntity.ok(submissionRepository.save(submission));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable String id) {
        if (submissionRepository.existsById(id)) {
            submissionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
