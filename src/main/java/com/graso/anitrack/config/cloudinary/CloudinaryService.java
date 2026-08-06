package com.graso.anitrack.config.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public List<String> upload(List<MultipartFile> files) throws IOException {
        List<String> uploadedUrls = files.stream()
                .map(f -> {
                    try {
                        return cloudinary.uploader().upload(f.getBytes(), ObjectUtils.asMap(
                                "folder", "springboot_uploads",
                                "resource_type", "auto" // Automatically detects images, PDFs, etc.
                        ));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(uploadResult -> (String) uploadResult.get("secure_url"))
                .toList();

        return uploadedUrls;
    }


}
