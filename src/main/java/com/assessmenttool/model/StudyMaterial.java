package com.assessmenttool.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "study_materials")
public class StudyMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(length = 1000)
    private String description;
    
    private String fileName;
    private String fileType;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] data;

    private String uploadedBy;
    private LocalDateTime uploadDate;

    @Column(columnDefinition = "LONGTEXT")
    private String sentStudentIdsJson = "[]";

    @Transient
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public StudyMaterial() {}

    // Constructor without data (for listing)
    public StudyMaterial(Long id, String title, String description, String fileName, String fileType, String uploadedBy, LocalDateTime uploadDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.fileName = fileName;
        this.fileType = fileType;
        this.uploadedBy = uploadedBy;
        this.uploadDate = uploadDate;
        this.sentStudentIdsJson = "[]";
    }

    // Constructor with sentStudentIdsJson (for listing with recipients)
    public StudyMaterial(Long id, String title, String description, String fileName, String fileType, String uploadedBy, LocalDateTime uploadDate, String sentStudentIdsJson) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.fileName = fileName;
        this.fileType = fileType;
        this.uploadedBy = uploadedBy;
        this.uploadDate = uploadDate;
        this.sentStudentIdsJson = sentStudentIdsJson != null ? sentStudentIdsJson : "[]";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    
    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }
    
    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
    
    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }

    public String getSentStudentIdsJson() { return sentStudentIdsJson; }
    public void setSentStudentIdsJson(String sentStudentIdsJson) { this.sentStudentIdsJson = sentStudentIdsJson; }

    public List<String> getSentStudentIds() {
        if (sentStudentIdsJson == null || sentStudentIdsJson.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(sentStudentIdsJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void setSentStudentIds(List<String> ids) {
        try {
            this.sentStudentIdsJson = objectMapper.writeValueAsString(ids != null ? ids : new ArrayList<>());
        } catch (Exception e) {
            this.sentStudentIdsJson = "[]";
        }
    }
}
