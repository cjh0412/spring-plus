package org.example.expert.domain.health.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.service.S3Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class healthController {

    private final S3Service s3Service;

    @GetMapping
    public ResponseEntity<String> health() {
        return ResponseEntity.ok(LocalDate.now() + "server is running!!");
    }


    @PostMapping("/upload-profile")
    public ResponseEntity<String> uploadProfile(@RequestParam("file") MultipartFile file) {
        try {
            String url = s3Service.fileUpload(file, "profile/image");
            return ResponseEntity.ok(url);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }

}
