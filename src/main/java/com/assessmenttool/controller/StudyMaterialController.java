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
import java.util.List;

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
            StudyMaterial material = new StudyMaterial();
            material.setTitle(title);
            material.setDescription(description);
            material.setUploadedBy(uploadedBy);
            material.setFileName(file.getOriginalFilename());
            material.setFileType(file.getContentType());
            material.setData(file.getBytes());
            material.setUploadDate(LocalDateTime.now());
            
            repository.save(material);
            return ResponseEntity.ok().body("{\"message\": \"Material uploaded successfully\"}");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"Could not upload file\"}");
        }
    }

    @GetMapping
    public ResponseEntity<List<StudyMaterial>> getAllMaterials() {
        return ResponseEntity.ok(repository.findAllWithoutData());
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
