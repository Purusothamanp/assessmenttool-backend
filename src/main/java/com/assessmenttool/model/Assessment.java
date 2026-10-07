package com.assessmenttool.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "assessments")
public class Assessment {

    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    private String type; // e.g. "Quiz", "Test", "Exam"

    private String category;

    private String topic;

    private String timeDuration;

    @Column(columnDefinition = "LONGTEXT")
    private String questionFormatsJson;

    @Column(columnDefinition = "LONGTEXT")
    private String questionsJson;

    private String date;

    private String creatorId;

    @Transient
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Assessment() {
    }

    public Assessment(String id, String title, String type, String category, String topic, String date, String creatorId) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.category = category;
        this.topic = topic;
        this.date = date;
        this.creatorId = creatorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(String timeDuration) {
        this.timeDuration = timeDuration;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public List<String> getQuestionFormats() {
        if (questionFormatsJson == null || questionFormatsJson.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(questionFormatsJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void setQuestionFormats(List<String> questionFormats) {
        try {
            this.questionFormatsJson = objectMapper.writeValueAsString(questionFormats);
        } catch (Exception e) {
            this.questionFormatsJson = "[]";
        }
    }

    public List<Map<String, Object>> getQuestions() {
        if (questionsJson == null || questionsJson.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(questionsJson, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void setQuestions(List<Map<String, Object>> questions) {
        try {
            this.questionsJson = objectMapper.writeValueAsString(questions);
        } catch (Exception e) {
            this.questionsJson = "[]";
        }
    }

    public String getQuestionFormatsJson() {
        return questionFormatsJson;
    }

    public void setQuestionFormatsJson(String questionFormatsJson) {
        this.questionFormatsJson = questionFormatsJson;
    }

    public String getQuestionsJson() {
        return questionsJson;
    }

    public void setQuestionsJson(String questionsJson) {
        this.questionsJson = questionsJson;
    }
}
