package org.example.projectjavaservice.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.example.projectjavaservice.entity.Court;
import org.example.projectjavaservice.entity.CourtImage;
import org.example.projectjavaservice.exception.NotFoundException;
import org.example.projectjavaservice.repository.CourtImageRepository;
import org.example.projectjavaservice.repository.CourtRepository;
import org.example.projectjavaservice.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final Cloudinary cloudinary;
    private final CourtRepository courtRepository;
    private final CourtImageRepository courtImageRepository;

    @Override
    public String uploadCourtImage(Long courtId, MultipartFile file) {
        // 1. Kiểm tra xem Sân có tồn tại không
        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new NotFoundException("Court not found"));

        try {
            // 2. GỌI SDK CLOUDINARY ĐỂ UPLOAD LÊN MÂY (Yêu cầu UC-05)
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            // 3. Lấy ra đường link an toàn (Secure URL) từ kết quả Cloud trả về
            String imageUrl = (String) uploadResult.get("secure_url");

            // 4. Lưu URL này vào Database (Bảng CourtImage)
            CourtImage courtImage = CourtImage.builder()
                    .court(court)
                    .imageUrl(imageUrl)
                    .build();
            courtImageRepository.save(courtImage);

            // 5. Trả về URL cho Client
            return imageUrl;

        } catch (IOException e) {
            // Nếu mất mạng, sai API Key -> Ném ra lỗi để GlobalExceptionHandler bắt
            throw new RuntimeException("Cloud storage service is temporarily unavailable. Please try again later.");
        }
    }
}