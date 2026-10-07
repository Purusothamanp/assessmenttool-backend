package com.assessmenttool.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    private String id;

    private String studentName;

    private String assessmentId;

    private String assessmentTitle;

    private int score;

    private String status; // "Submitted", "Passed", "Failed"

    private String date;

    @Column(columnDefinition = "LONGTEXT")
    private String answersJson;

    @Transient
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Submission() {
    }

    public Submission(String id, String studentName, String assessmentId, String assessmentTitle, int score, String status, String date) {
        this.id = id;
        this.studentName = studentName;
        this.assessmentId = assessmentId;
        this.assessmentTitle = assessmentTitle;
        this.score = score;
        this.status = status;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getAssessmentTitle() {
        return assessmentTitle;
    }

    public void setAssessmentTitle(String assessmentTitle) {
        this.assessmentTitle = assessmentTitle;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, Object> getAnswers() {
        if (answersJson == null || answersJson.isBlank()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(answersJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public void setAnswers(Map<String, Object> answers) {
        try {
            this.answersJson = objectMapper.writeValueAsString(answers);
        } catch (Exception e) {
            this.answersJson = "{}";
        }
    }

    public String getAnswersJson() {
        return answersJson;
    }

    public void setAnswersJson(String answersJson) {
        this.answersJson = answersJson;
    }
}
