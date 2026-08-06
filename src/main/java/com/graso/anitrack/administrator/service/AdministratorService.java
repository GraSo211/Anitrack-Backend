package com.graso.anitrack.administrator.service;


import com.graso.anitrack.config.cloudinary.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AdministratorService {
    CloudinaryService cloudinaryService;

    public AdministratorService(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    public List<String> uploadHeroImages(List<MultipartFile> multipartFileList) {
        try {
            return cloudinaryService.upload(multipartFileList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
