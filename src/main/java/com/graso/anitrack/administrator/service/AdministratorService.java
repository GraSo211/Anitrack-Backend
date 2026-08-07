package com.graso.anitrack.administrator.service;


import com.graso.anitrack.administrator.controller.dto.HeroImageResponse;
import com.graso.anitrack.config.cloudinary.CloudinaryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AdministratorService {
    CloudinaryService cloudinaryService;

    public AdministratorService(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @CacheEvict(value = "heroImagesCache", allEntries = true)
    public List<String> uploadHeroImages(List<MultipartFile> multipartFileList) {
        try {
            return cloudinaryService.upload(multipartFileList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Cacheable(value = "heroImagesCache", unless = "#result == null")
    public List<HeroImageResponse> getHeroImages() {
        try {
            return cloudinaryService.listHeroImages();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @CacheEvict(value = "heroImagesCache", allEntries = true)
    public void deleteHeroImage(String publicId) {
        try {
            cloudinaryService.deleteHeroImage(publicId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
