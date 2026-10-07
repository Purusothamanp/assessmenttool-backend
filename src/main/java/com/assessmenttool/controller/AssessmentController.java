package com.assessmenttool.controller;

import com.assessmenttool.model.Assessment;
import com.assessmenttool.repository.AssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/assessments")
public class AssessmentController {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @GetMapping
    public List<Assessment> getAssessments(@RequestParam(required = false) String creatorId, @RequestParam(required = false) String title) {
        if (creatorId != null && !creatorId.isBlank()) {
            return assessmentRepository.findByCreatorId(creatorId);
        }
        if (title != null && !title.isBlank()) {
            return assessmentRepository.findByTitle(title);
        }
        return assessmentRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assessment> getAssessmentById(@PathVariable String id) {
        return assessmentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Assessment> createAssessment(@RequestBody Map<String, Object> payload) {
        Assessment assessment = new Assessment();
        String id = (String) payload.get("id");
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString().substring(0, 11);
        }
        assessment.setId(id);
        assessment.setTitle((String) payload.get("title"));
        assessment.setType((String) payload.get("type"));
        assessment.setCategory((String) payload.get("category"));
        assessment.setTopic((String) payload.get("topic"));
        assessment.setDate((String) payload.get("date"));
        assessment.setCreatorId((String) payload.get("creatorId"));
        if (payload.containsKey("timeDuration")) {
            assessment.setTimeDuration((String) payload.get("timeDuration"));
        }

        if (payload.containsKey("questionFormats")) {
            Object formats = payload.get("questionFormats");
            if (formats instanceof List) {
                assessment.setQuestionFormats((List<String>) formats);
            }
        }

        if (payload.containsKey("questions")) {
            Object questions = payload.get("questions");
            if (questions instanceof List) {
                assessment.setQuestions((List<Map<String, Object>>) questions);
            }
        }

        Assessment saved = assessmentRepository.save(assessment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Assessment> updateAssessment(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return assessmentRepository.findById(id).map(assessment -> {
            if (payload.containsKey("title")) assessment.setTitle((String) payload.get("title"));
            if (payload.containsKey("type")) assessment.setType((String) payload.get("type"));
            if (payload.containsKey("category")) assessment.setCategory((String) payload.get("category"));
            if (payload.containsKey("topic")) assessment.setTopic((String) payload.get("topic"));
            if (payload.containsKey("date")) assessment.setDate((String) payload.get("date"));
            if (payload.containsKey("creatorId")) assessment.setCreatorId((String) payload.get("creatorId"));
            if (payload.containsKey("timeDuration")) assessment.setTimeDuration((String) payload.get("timeDuration"));

            if (payload.containsKey("questionFormats")) {
                Object formats = payload.get("questionFormats");
                if (formats instanceof List) {
                    assessment.setQuestionFormats((List<String>) formats);
                }
            }

            if (payload.containsKey("questions")) {
                Object questions = payload.get("questions");
                if (questions instanceof List) {
                    assessment.setQuestions((List<Map<String, Object>>) questions);
                }
            }

            return ResponseEntity.ok(assessmentRepository.save(assessment));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssessment(@PathVariable String id) {
        if (assessmentRepository.existsById(id)) {
            assessmentRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
