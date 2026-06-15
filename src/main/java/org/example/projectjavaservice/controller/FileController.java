package org.example.projectjavaservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.projectjavaservice.dto.Response.ApiResponse;
import org.example.projectjavaservice.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // POST /api/v1/files/upload
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @RequestParam("courtId") Long courtId,
            @RequestParam("file") MultipartFile file) { // Tên param phải là "file"

        String imageUrl = fileService.uploadCourtImage(courtId, file);
        
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Image uploaded successfully")
                .data(imageUrl) // Trả về cái link mây cho Frontend hiển thị
                .build());
    }
}