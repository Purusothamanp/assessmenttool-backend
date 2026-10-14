package com.assessmenttool.controller;

import com.assessmenttool.model.StudyMaterial;
import com.assessmenttool.repository.StudyMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/study-materials")
public class StudyMaterialController {

    @Autowired
    private StudyMaterialRepository repository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadMaterial(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("uploadedBy") String uploadedBy,
            @RequestParam("file") MultipartFile file) {
        
        try {
            if (file == null || file.isEmpty()) {
                Map<String, String> err = new HashMap<>();
                err.put("error", "EMPTY_FILE");
                err.put("message", "Please select a valid file to upload.");
                return ResponseEntity.badRequest().body(err);
            }

            StudyMaterial material = new StudyMaterial();
            material.setTitle(title);
            material.setDescription(description);
            material.setUploadedBy(uploadedBy);
            material.setFileName(file.getOriginalFilename());
            material.setFileType(file.getContentType());
            material.setData(file.getBytes());
            material.setUploadDate(LocalDateTime.now());
            
            repository.save(material);
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Material uploaded successfully");
            resp.put("id", material.getId());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "UPLOAD_FAILED");
            err.put("message", "Could not upload file: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @GetMapping
    public ResponseEntity<List<StudyMaterial>> getAllMaterials() {
        return ResponseEntity.ok(repository.findAllWithoutData());
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<?> sendMaterialToStudents(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        StudyMaterial material = repository.findById(id).orElse(null);
        if (material == null) {
            return ResponseEntity.notFound().build();
        }

        Object studentIdsObj = payload.get("studentIds");
        if (!(studentIdsObj instanceof List)) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "INVALID_REQUEST");
            err.put("message", "studentIds list is required.");
            return ResponseEntity.badRequest().body(err);
        }

        List<?> rawIds = (List<?>) studentIdsObj;
        List<String> targetIds = new ArrayList<>();
        for (Object item : rawIds) {
            if (item != null) targetIds.add(item.toString().trim());
        }

        if (targetIds.isEmpty()) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "EMPTY_STUDENTS");
            err.put("message", "Please select at least one student.");
            return ResponseEntity.badRequest().body(err);
        }

        List<String> currentSent = new ArrayList<>(material.getSentStudentIds());
        List<String> alreadySent = new ArrayList<>();
        List<String> newlyAdded = new ArrayList<>();

        for (String sId : targetIds) {
            if (currentSent.contains(sId)) {
                alreadySent.add(sId);
            } else {
                newlyAdded.add(sId);
            }
        }

        if (!alreadySent.isEmpty() && newlyAdded.isEmpty()) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "ALREADY_SENT");
            err.put("message", "This study material has already been sent to all the selected student(s).");
            err.put("alreadySentStudentIds", alreadySent);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
        }

        currentSent.addAll(newlyAdded);
        material.setSentStudentIds(currentSent);
        repository.save(material);

        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Material sent successfully to " + newlyAdded.size() + " student(s).");
        resp.put("sentCount", newlyAdded.size());
        resp.put("alreadySentStudentIds", alreadySent);
        resp.put("sentStudentIds", currentSent);
        if (!alreadySent.isEmpty()) {
            resp.put("warning", "Some students had already received this material.");
        }

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadMaterial(@PathVariable Long id) {
        StudyMaterial material = repository.findById(id).orElse(null);
        if (material == null || material.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        
        String contentType = material.getFileType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + material.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(material.getData());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMaterial(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.ok().body("{\"message\": \"Deleted successfully\"}");
    }
}
