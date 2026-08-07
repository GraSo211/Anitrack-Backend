package com.graso.anitrack.config.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.graso.anitrack.administrator.controller.dto.HeroImageResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryService {
    private static final String HERO_FOLDER = "anitrack/hero";

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public List<String> upload(List<MultipartFile> files) throws IOException {
        List<String> uploadedUrls = files.stream()
                .map(f -> {
                    try {
                        return cloudinary.uploader().upload(f.getBytes(), ObjectUtils.asMap(
                                "folder", HERO_FOLDER,
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

    public List<HeroImageResponse> listHeroImages() throws Exception {
        Map<?, ?> response = cloudinary.api().resources(ObjectUtils.asMap(
                "type", "upload",
                "prefix", HERO_FOLDER + "/",
                "max_results", 100
        ));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resources = (List<Map<String, Object>>) response.get("resources");

        return resources.stream()
                .map(resource -> new HeroImageResponse(
                        (String) resource.get("public_id"),
                        (String) resource.get("secure_url")
                ))
                .toList();
    }

    public void deleteHeroImage(String publicId) throws Exception {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

}
