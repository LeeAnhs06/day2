package org.example.projectjavaservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadCourtImage(Long courtId, MultipartFile file);
}